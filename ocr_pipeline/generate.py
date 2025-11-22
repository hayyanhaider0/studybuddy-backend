from fastapi import FastAPI, File, UploadFile, Form
from typing import Optional, List
import json
import tempfile
import aiohttp
import os
import asyncio
import uvicorn
from pathlib import Path
from io import BytesIO
from PIL import Image
import numpy as np
import base64
from google import genai
from fastapi.middleware.cors import CORSMiddleware
from pydantic import BaseModel

class ChapterCanvas(BaseModel):
    chapterName: str
    canvases: List[str] #base64 strings.
    
class GenerateRequest(BaseModel):
    taskType: str
    occupation: Optional[str]
    educationLevel: Optional[str]
    notebookName: str
    chaptersWithCanvases: List[ChapterCanvas]
    options: Optional[dict]



OUTPUT_FILE = Path('results.txt')

app = FastAPI()

app.add_middleware(
    CORSMiddleware,
    allow_origins=["*"],  # <-- allow your frontend here
    allow_credentials=True,
    allow_methods=["*"],
    allow_headers=["*"],
)


client = genai.Client(api_key=GEMINI_API_KEY)

@app.get('/')
def ping():
    return {'message':'server running'}

async def upload_images(files: list[UploadFile] = File(...)):
    
    async with aiohttp.ClientSession() as session:  # session uses non-blocking IO to concurrently handle multiple tasks
        # tasks = [analyze_image(session, f) for f in files]
        tasks = [analyze_image_layout_mode(session, f) for f in files]
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
    return results

async def upload_images2(image_strings: list[str]):
    
    async with aiohttp.ClientSession() as session:  # session uses non-blocking IO to concurrently handle multiple tasks
        # tasks = [analyze_image(session, f) for f in files]
        tasks = [analyze_image_layout_mode2(session, f) for f in image_strings]
        results = await asyncio.gather(*tasks)

    # with open(OUTPUT_FILE, "w", encoding="utf-8") as f:
    #     for filename, lines in results:
    #         f.write(f"\n--- {filename} ---\n")
    #         if lines:
    #             f.write("\n".join(lines))
    #         else:
    #             f.write("[No text detected]")
    #         f.write("\n")

    # print(f"\n📁 OCR results saved to: {OUTPUT_FILE.absolute()}")
    return results


# option 2: Use Azure's documentintelligence service, specifically the prebuilt-layout model 
async def analyze_image_layout_mode2(session: aiohttp.ClientSession, image_str: str):
    
    modelId = 'prebuilt-layout'
    try:
        # file_bytes = await file.read()
        
        base64_encoded_file_string = image_str
        
        url = (
            DOCUMENT_ENDPOINT +
            f'documentintelligence/documentModels/{modelId}:analyze' +
            f'?api-version=2024-11-30'
        )
        
        headers = {
            # 'Content-Type': 'application/json',
            "Ocp-Apim-Subscription-Key": DOCUMENT_KEY,   
        }
        
        body = {
            'base64Source': base64_encoded_file_string
        }
        
        
        async with session.post(url,headers=headers, json=body) as response:
            response.raise_for_status()
            
            operation_location = response.headers.get('Operation-Location')
            if not operation_location:
                raise RuntimeError('Missing header in response from Azure')
        
        print('job submitted')
        
        analysis = None
        while True:
            async with session.get(operation_location, headers=headers) as analysis_response:
                analysis_response.raise_for_status()
                analysis = await analysis_response.json()
                
                status = analysis.get('status')
                if status == 'succeeded':
                    break
                elif status in ('running', 'notStarted'):
                    await asyncio.sleep(3)
                else:
                    raise RuntimeError(f'Analysis failed with status: {status}')
        
        paragraphs = analysis.get('analyzeResult').get('paragraphs')
        # print(paragraphs)
        text = [p.get('content') for p in paragraphs]
        
        print(f"OCR complete for image ({len(text)} lines)")
        return text
    
    except Exception as e:
        print(f'Error processing image: {e}')
        return []








@app.post('/ai/generate')
async def generate_notes(req: GenerateRequest):
    print('Received request')
    # print(req.model_dump())
    
    
    image_strings_base64 = []
    for chapter in req.chaptersWithCanvases:
        for canvas in chapter.canvases:
            image_strings_base64.append(canvas)

    image_text = await upload_images2(image_strings_base64)
    
    print(image_text)
            
        
    
    
    return {"message": "OK", "taskType": req.taskType}


    
    


# option 2: Use Azure's documentintelligence service, specifically the prebuilt-layout model 
async def analyze_image_layout_mode(session: aiohttp.ClientSession, file:UploadFile):
    
    modelId = 'prebuilt-layout'
    try:
        file_bytes = await file.read()
        
        base64_encoded_file_string = base64.b64encode(file_bytes).decode('utf-8')
        
        url = (
            DOCUMENT_ENDPOINT +
            f'documentintelligence/documentModels/{modelId}:analyze' +
            f'?api-version=2024-11-30'
        )
        
        headers = {
            # 'Content-Type': 'application/json',
            "Ocp-Apim-Subscription-Key": DOCUMENT_KEY,   
        }
        
        body = {
            'base64Source': base64_encoded_file_string
        }
        
        
        async with session.post(url,headers=headers, json=body) as response:
            response.raise_for_status()
            
            operation_location = response.headers.get('Operation-Location')
            if not operation_location:
                raise RuntimeError('Missing header in response from Azure')
        
        print('job submitted')
        
        analysis = None
        while True:
            async with session.get(operation_location, headers=headers) as analysis_response:
                analysis_response.raise_for_status()
                analysis = await analysis_response.json()
                
                status = analysis.get('status')
                if status == 'succeeded':
                    break
                elif status in ('running', 'notStarted'):
                    await asyncio.sleep(3)
                else:
                    raise RuntimeError(f'Analysis failed with status: {status}')
        
        paragraphs = analysis.get('analyzeResult').get('paragraphs')
        # print(paragraphs)
        text = [p.get('content') for p in paragraphs]
        
        print(f"OCR complete for {file.filename} ({len(text)} lines)")
        return file.filename, text
    
    except Exception as e:
        print(f'Error processing {file.filename}: {e}')
        return file.filename, []
        
    

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
   
        
        



def generate_notes(chapter_name: str, options: Optional[str], pages: List[dict]):
    
    SYS_PROMPT = """
    You are an AI assistant that generates clean, accurate study notes from noisy OCR-extracted text.

    Your responsibilities:
    1. Understand the user’s original intent (the “task” field, e.g., summarize, generate study notes, etc.).
    2. Interpret OCR text that may be incomplete, noisy, out of order, or misformatted.
    3. Produce concise, correct, well-structured notes strictly based on the provided OCR content.
    4. Never invent facts that are not inferable from the text.
    5. Remove timestamps, page numbers, and irrelevant metadata.
    6. Preserve technical correctness (e.g., in CS, math, physics topics).
    7. Improve clarity, grammar, and structure while keeping meaning faithful to the notes.

    Formatting rules:
    - Always output in strict JSON.
    - JSON shape MUST be:

    {
    "chapter": "<chapter name>",
    "summary": "<clean summarized notes>",
    "key_points": ["...", "..."],
    "improvements": ["things the OCR missed or unclear lines"],
    "raw_used_text": ["line1", "line2", "..."]
    }

    STRICT requirements:
    - Do NOT output anything outside the JSON object.
    - Do NOT include markdown.
    - Do NOT add commentary.
    - Do NOT add quotes around the entire JSON output.

    You must ALWAYS follow these rules.
    """
    
    all_text_lines = []
    for p in pages:
        all_text_lines.extend(p["text"])

    USER_PROMPT = f"""
    User request:
    Task: Summarize / generate study notes.
    Chapter: {chapter_name}
    Options: {options or ''} (optional field)

    OCR extracted lines:
    {all_text_lines}

    Generate clean structured notes following the JSON schema.
    """
    
    prompt = SYS_PROMPT + USER_PROMPT
    

    gemini_response = call_gemini_api(prompt)
    
    response = gemini_response if gemini_response else ''
    
    return response
    
    
    
# @app.get('/test-gemini')
def call_gemini_api(prompt:str):
    
    try:
        response = client.models.generate_content(
            model='gemini-2.5-flash', contents=prompt
        )
        
        print(response.text)
        
        return response.text
    except Exception as e:
        raise RuntimeError(f'Error calling gemini api: {e}')
    return None


# entry point for the server for now, takes a form, which includes, username, chapter_name, task, options(optional), images
@app.post('/process-task')
async def process_task(
    username: str = Form(...),
    chapter_name: str = Form(...),
    task: str = Form(...),
    options: Optional[str] = Form(None),
    images: List[UploadFile] = File(...)
):
    parsed_options = None
    if options:
        try:
            parsed_options = json.loads(options)
        except json.JSONDecodeError:
            parsed_options = {'raw': options}
    
    structure_output = []
    
    try:
    
        image_results = await upload_images(images)
        for result in image_results:
            structure_output.append(
                {
                    "filename": result[0],
                    "text": result[1]
                }
            )
    except Exception as e:
        print(e)
    finally:
        # chapter_result = {
        #     'username': username,
        #     'chapter':chapter_name,
        #     'task':task,
        #     'options': parsed_options,
        #     'pages': structure_output
            
        # }
        
        llm_result = generate_notes(chapter_name, options, structure_output)
        
        
    return llm_result
    
    

if __name__ == '__main__':
    uvicorn.run(app, host='0.0.0.0',port=8000)
    
    