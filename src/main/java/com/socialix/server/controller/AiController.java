package com.socialix.server.controller;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    @Value("${gemini.api.key}")
    private String apiKey;

    @PostMapping("/generate-caption")
    public ResponseEntity<Map<String, String>> generateCaption(@RequestBody Map<String, String> request) {
        String topic = request.getOrDefault("topic", "");
        String platform = request.getOrDefault("platform", "General");
        String tone = request.getOrDefault("tone", "Professional");

        String prompt = String.format(
                "You are an expert social media manager. Write a caption tailored for %s with a %s tone based on this request: '%s'. " +
                        "Include relevant emojis and 3 to 5 trending hashtags. Keep it concise.",
                platform, tone, topic
        );

        // Fixed: Use valid Gemini model identifier
        String url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-1.5-flash:generateContent?key=" + apiKey;

        Map<String, String> responseMap = new HashMap<>();

        try {
            RestTemplate restTemplate = new RestTemplate();

            // Construct Gemini REST Payload
            JSONObject textPart = new JSONObject().put("text", prompt);
            JSONArray partsArray = new JSONArray().put(textPart);
            JSONObject contentObject = new JSONObject().put("parts", partsArray);
            JSONArray contentsArray = new JSONArray().put(contentObject);
            JSONObject body = new JSONObject().put("contents", contentsArray);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);

            HttpEntity<String> entity = new HttpEntity<>(body.toString(), headers);
            ResponseEntity<String> response = restTemplate.postForEntity(url, entity, String.class);

            // Parse Gemini Response JSON
            JSONObject responseJson = new JSONObject(response.getBody());
            String generatedCaption = responseJson
                    .getJSONArray("candidates")
                    .getJSONObject(0)
                    .getJSONObject("content")
                    .getJSONArray("parts")
                    .getJSONObject(0)
                    .getString("text");

            responseMap.put("caption", generatedCaption.trim());
            return ResponseEntity.ok(responseMap);

        } catch (Exception e) {
            e.printStackTrace();
            String errorDetails = (e.getMessage() != null) ? e.getMessage() : "Internal server error occurred";
            responseMap.put("caption", "Error generating caption: " + errorDetails);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseMap);
        }
    }
}