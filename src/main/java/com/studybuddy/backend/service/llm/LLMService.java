package com.studybuddy.backend.service.llm;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.studybuddy.backend.dto.llm.GenerateRequest;
import com.studybuddy.backend.dto.llm.GenerateResponse;

@Service
public class LLMService {
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${generate.server.url}")
    private String generateServerUrl;

    public GenerateResponse generate(GenerateRequest req) {
        ResponseEntity<GenerateResponse> res = restTemplate.postForEntity(generateServerUrl + "ai/generate", req,
                GenerateResponse.class);

        return res.getBody();
    }
}
