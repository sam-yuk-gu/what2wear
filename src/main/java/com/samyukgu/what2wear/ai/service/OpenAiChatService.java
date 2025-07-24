package com.samyukgu.what2wear.ai.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.samyukgu.what2wear.wardrobe.model.Wardrobe;
import okhttp3.*;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class OpenAiChatService {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private final OkHttpClient client = new OkHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String apiKey;

    public OpenAiChatService(String apiKey) {
        this.apiKey = apiKey;
    }

    // system 역할로 GPT의 스타일과 포맷을 강제하는 프롬프트
    public String getOutfitRecommendation(String location, String purpose, Map<String, List<String>> closet) throws IOException {
        String closetDescription = buildClosetText(closet);
        String userPrompt = String.format(
                "내일 %s에서 %s을(를) 위해 외출할 예정입니다. 아래 옷들 중에서만 코디를 추천해줘.\n\n%s\n\n" +
                        "다음 형식으로만 대답해:\n상의: OOO\n바지: OOO\n신발: OOO\n악세사리: OOO",
                location, purpose, closetDescription
        );

        // JSON 생성
        ObjectNode requestJson = objectMapper.createObjectNode();
        requestJson.put("model", "gpt-4o");
        requestJson.put("temperature", 0.7);
        requestJson.put("max_tokens", 100);


        // OpenAI API에 전송되는 핵심 프롬프트
        ArrayNode messages = objectMapper.createArrayNode();
        messages.add(objectMapper.createObjectNode()
                .put("role", "system")
                .put("content", "너는 패션 코디 전문가야. 유저가 가지고 있는 옷을 기반으로 코디를 제안해줘. 다음 형식으로만 대답해: '상의: OOO\\n바지: OOO\\n신발: OOO\\n악세사리: OOO'. 만약 해당 항목의 추천이 어렵거나 없는 경우 '없음'이라고 명확하게 써줘. 다른 말은 하지 마.")
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

    // AI에 옷 리스트 전달
    private String buildClosetText(Map<String, List<String>> closet) {
        StringBuilder sb = new StringBuilder();
        sb.append("내 옷 목록은 다음과 같아.\n");

        for (String category : List.of("상의", "바지", "신발", "악세사리")) {
            List<String> items = closet.getOrDefault(category, List.of());
            String joined = items.isEmpty() ? "없음" : String.join(", ", items);
            sb.append(category).append(": ").append(joined).append("\n");
        }

        return sb.toString();
    }

//    private String buildClosetText(Map<String, List<Wardrobe>> closet) {
//        StringBuilder sb = new StringBuilder();
//        sb.append("내 옷 목록은 다음과 같아.\n");
//
//        for (String category : List.of("상의", "바지", "신발", "악세사리")) {
//            List<Wardrobe> items = closet.getOrDefault(category, List.of());
//
//            for(Wardrobe clothes : closet.get(category)){
//                sb.append(clothes.getName()).append(" pk = ").append(clothes.getId());
//            }
//            sb.append(category).append(": ").append(joined).append("\n");
//        }
//
//        return sb.toString();
//    }
}
