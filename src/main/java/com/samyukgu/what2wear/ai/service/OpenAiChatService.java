package com.samyukgu.what2wear.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import okhttp3.*;

import java.io.IOException;

public class OpenAiChatService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;

    public OpenAiChatService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getOutfitRecommendation(String location, String purpose) throws IOException {
        String userPrompt = String.format(
                "내일 %s에서 %s을(를) 위해 외출할 예정입니다. 날씨와 활동에 어울리는 옷을 다음 형식으로 추천해줘:\n상의:\n하의:\n신발:\n악세사리:",
                location, purpose
        );

        // JSON 생성
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put("model", "gpt-4o");
        requestJson.put("temperature", 0.7);
        requestJson.put("max_tokens", 100);

        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(objectMapper.createObjectNode()
                .put("role", "system")
                .put("content", "너는 패션 코디 전문가야. 유저가 가지고 있는 옷을 기반으로 코디를 제안해줘. 다음 형식으로만 대답해: '상의: OOO\\n하의: OOO\\n신발: OOO\\n악세사리: OOO'. 만약 해당 항목의 추천이 어렵거나 없는 경우 '없음'이라고 명확하게 써줘. 다른 말은 하지 마. 응답도 좀 제대로 해줘")
        );
        messages.add(objectMapper.createObjectNode()
                .put("role", "user")
                .put("content", userPrompt)
        );
        requestJson.set("messages", messages);

        // HTTP 요청 생성
        RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(requestJson),
                MediaType.get("application/json")
        );

        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            String responseBody = response.body().string();

            if (!response.isSuccessful()) {
                System.err.println("❌ OpenAI API 호출 실패");
                System.err.println("응답 코드: " + response.code());
                System.err.println("응답 메시지: " + response.message());
                System.err.println("응답 바디: " + responseBody);
                throw new IOException("OpenAI API 호출 실패: " + response.code());
            }
            System.out.println(response.message());
            System.out.println(response.message());

            JsonNode jsonNode = objectMapper.readTree(responseBody);
            return jsonNode.get("choices").get(0).get("message").get("content").asText().trim();
        }
    }
}
