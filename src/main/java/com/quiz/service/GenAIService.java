package com.quiz.service;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Properties;
import org.json.JSONArray;
import org.json.JSONObject;
import com.quiz.util.DBConnection;

public class GenAIService {

    private static String API_KEY;
    
    // UPDATED: Switched to 'gemini-2.5-flash' which is the current active model.
    // 'gemini-1.5-flash' is deprecated/retired, causing the 404 error.
    private static final String API_URL = "https://generativelanguage.googleapis.com/v1/models/gemini-2.5-flash:generateContent?key=";

    static {
        Properties props = new Properties();
        try (InputStream input = DBConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (input != null) {
                props.load(input);
                API_KEY = props.getProperty("api.key");
            } else {
                System.err.println("ERROR: db.properties file not found in classpath.");
            }
        } catch (IOException ex) {
        }
    }

    public String generateCourseContent(String topic, String level) {
        if (API_KEY == null || API_KEY.trim().isEmpty()) {
            return createErrorResponse("API Key is missing. Check db.properties.");
        }

        try {
            // Refined prompt to ensure the new model follows strict JSON formatting
            String prompt = String.format(
                "Create a course on '%s' (Level: %s). " +
                "CRITICAL: Output strictly valid JSON only. No Markdown. No code fences. " +
                "Format: {" +
                "\"courseTitle\": \"Title\"," +
                "\"introduction\": \"Intro text\"," +
                "\"lessons\": [{\"lessonTitle\": \"L1\", \"material\": \"Content\"} (x10)]," +
                "\"quiz\": [{\"questionText\": \"Q1\", \"options\": [{\"optionText\": \"Opt1\", \"isCorrect\": boolean} (x4)]} (x10)]" +
                "}", 
                topic, level
            );

            // 1. Construct Request Body
            JSONObject requestBody = new JSONObject()
                .put("contents", new JSONArray()
                    .put(new JSONObject()
                        .put("parts", new JSONArray()
                            .put(new JSONObject().put("text", prompt))
                        )
                    )
                );

            // 2. Build HTTP Request
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(API_URL + API_KEY))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString(), StandardCharsets.UTF_8))
                .build();

            // 3. Send Request
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            // 4. Handle HTTP Errors
            if (response.statusCode() != 200) {
                // Log the full error body to help identify if the new model is also blocked
                System.err.println("API Failure: " + response.statusCode() + " | " + response.body());
                return createErrorResponse("API request failed with status: " + response.statusCode());
            }

            // 5. Parse Response Logic
            JSONObject responseJson = new JSONObject(response.body());

            // Handle API-level errors
            if (responseJson.has("error")) {
                return createErrorResponse("API Error: " + responseJson.getJSONObject("error").optString("message"));
            }

            // Extract content safely
            String generatedText = responseJson.optJSONArray("candidates")
                    .optJSONObject(0)
                    .optJSONObject("content")
                    .optJSONArray("parts")
                    .optJSONObject(0)
                    .optString("text", "");

            if (generatedText.isEmpty()) {
                return createErrorResponse("API returned an empty response.");
            }

            // 6. Clean and Validate
            String validJson = cleanJsonContent(generatedText);
            
            // Fast validation check
            JSONObject validationCheck = new JSONObject(validJson);
            if (!validationCheck.has("courseTitle")) {
                return createErrorResponse("Generated content missing 'courseTitle'.");
            }

            return validJson;

        } catch (Exception e) {
            return createErrorResponse("Internal Server Error: " + e.getMessage());
        }
    }

    private String cleanJsonContent(String content) {
        if (content == null) return "{}";
        // Strip markdown code blocks and whitespace
        content = content.replaceAll("```json", "").replaceAll("```", "").trim();
        
        // Extract strictly from first { to last }
        int first = content.indexOf('{');
        int last = content.lastIndexOf('}');
        if (first != -1 && last != -1 && first < last) {
            return content.substring(first, last + 1);
        }
        return content;
    }

    private String createErrorResponse(String message) {
        return new JSONObject().put("error", message).toString();
    }
}