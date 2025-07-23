package com.samyukgu.what2wear.ai.service;

// Jackson 관련
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

// OkHttp 관련
import okhttp3.*;
import java.io.IOException;

public class OpenAiChatService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper(); // Jackson 사용
    private final String apiKey;

    public OpenAiChatService(String apiKey) {
        this.apiKey = apiKey;
    }

    public String getOutfitRecommendation(String location, String purpose) throws IOException {
        String prompt = String.format(
                "내일 %s에서 %s을(를) 위해 외출할 예정입니다. 날씨와 활동에 어울리는 옷을 다음 형식으로 추천해줘:\n상의:\n하의:\n신발:\n악세사리:",
                location, purpose
        );

        String json = """
        {
          "model": "gpt-4o",
          "messages": [
            {"role": "system",  "content": "너는 패션 코디 전문가야. 유저가 가지고 있는 옷을 기반으로 코디를 제안해줘. 다음 형식으로만 대답해: '상의: OOO\\n하의: OOO\\n신발: OOO\\n악세사리: OOO'. 만약 해당 항목의 추천이 어렵거나 없는 경우 '없음'이라고 명확하게 써줘. 다른 말은 하지 마."},
            {"role": "user", "content": "%s"}
          ],
          "temperature": 0.7,
          "max_tokens": 100
        }
        """.formatted(prompt);

        RequestBody body = RequestBody.create(json, MediaType.get("application/json"));
        Request request = new Request.Builder()
                .url(API_URL)
                .post(body)
                .addHeader("Authorization", "Bearer " + apiKey)
                .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                throw new IOException("API 호출 실패: " + response);
            }
            JsonNode jsonNode = objectMapper.readTree(response.body().string());
            return jsonNode.get("choices").get(0).get("message").get("content").asText().trim();
        }
    }
}
