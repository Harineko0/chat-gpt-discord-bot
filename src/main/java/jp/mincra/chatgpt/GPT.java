package jp.mincra.chatgpt;

import com.fasterxml.jackson.databind.ObjectMapper;
import jp.mincra.Constant;
import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.GPTResponse;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class GPT {
    private final HttpClient httpClient;

    public GPT() {
        this.httpClient = HttpClient.newHttpClient();
    }

    public GPTResponse post(GPTRequest gReq) throws IOException, InterruptedException {
        HttpRequest req = HttpRequest
                .newBuilder(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Authorization", "Bearer " + Constant.OPEN_API_KEY)
                .header("Content-Type", "application/json; charser=UTF-8")
                .POST(HttpRequest.BodyPublishers.ofString(gReq.toString()))
                .build();

        HttpResponse<String> res = httpClient.send(req, HttpResponse.BodyHandlers.ofString());

        return new ObjectMapper().readValue(res.body(), GPTResponse.class);
    }
}
