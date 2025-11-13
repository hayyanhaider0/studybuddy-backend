from fastapi import FastAPI, File, UploadFile
import aiohttp
import asyncio
import uvicorn
from pathlib import Path
from io import BytesIO
from PIL import Image
import numpy as np



OUTPUT_FILE = Path('results.txt')

app = FastAPI()

@app.get('/')
def ping():
    return {'message':'server running'}



def post_process_ocr(result: dict):
    """
    Sort and group text lines based on bounding box positions.
    Helps reconstruct lines that are visually close together but split by OCR.
    """
    lines_with_boxes = []

    read_result = result.get("readResult", {})
    for block in read_result.get("blocks", []):
        for line in block.get("lines", []):
            text = line.get("text", "")
            polygon = line.get("boundingPolygon", [])
            if len(polygon) >= 4:
                avg_y = (polygon[0]["y"] + polygon[1]["y"]) / 2
                avg_x = (polygon[0]["x"] + polygon[1]["x"]) / 2
                lines_with_boxes.append((avg_y, avg_x, text))

    lines_with_boxes.sort(key=lambda x: (x[0], x[1]))

    merged_lines = []
    current_line = ""
    last_y = None
    y_threshold = 10  # tweak this with debug plots

    for y, x, text in lines_with_boxes:
        if last_y is not None and abs(y - last_y) < y_threshold:
            current_line += " " + text
        else:
            if current_line:
                merged_lines.append(current_line.strip())
            current_line = text
        last_y = y

    if current_line:
        merged_lines.append(current_line.strip())

    return merged_lines





# for each image do a separate call to Azure's computer vision api, which returns each token, with its bounding box information
async def analyze_image(session: aiohttp.ClientSession, file: UploadFile):
    try:
        file_bytes = await file.read()
        url = (
            VISION_ENDPOINT +
            "computervision/imageanalysis:analyze" +
            "?api-version=2024-02-01&features=read"
        )
        headers = {
            "Ocp-Apim-Subscription-Key": VISION_KEY,
            "Content-Type": "application/octet-stream",
        }

        async with session.post(url, headers=headers, data=file_bytes) as response:
            response.raise_for_status()
            result = await response.json()

        processed_lines = post_process_ocr(result)

        print(f"OCR complete for {file.filename} ({len(processed_lines)} lines)")
        return file.filename, processed_lines

    except Exception as e:
        print(f"Error processing {file.filename}: {e}")
        return file.filename, []
        
        

@app.post("/upload")
async def upload_images(files: list[UploadFile] = File(...)):
    async with aiohttp.ClientSession() as session:
        tasks = [analyze_image(session, f) for f in files]
        results = await asyncio.gather(*tasks)

    with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
        for filename, lines in results:
            f.write(f"\n--- {filename} ---\n")
            if lines:
                f.write("\n".join(lines))
            else:
                f.write("[No text detected]")
            f.write("\n")

    print(f"\n📁 OCR results saved to: {OUTPUT_FILE.absolute()}")
    return {
        "message": f"OCR complete for {len(files)} images",
        "output_file": str(OUTPUT_FILE.absolute()),
        # "debug_dir": str(DEBUG_DIR.absolute())
    }
    
    




if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0',port=8000)
    
    