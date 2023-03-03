import jp.mincra.chatgpt.GPT;
import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.GPTResponse;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import jp.mincra.discord.ThreadManager;

import java.io.IOException;
import java.util.Arrays;

public class Test {
    private static GPT gpt;

    public static void main(String[] args) {
        gpt = new GPT();
    }

    public static void testGPT() throws IOException, InterruptedException {
        GPTRequest req = new GPTRequest(Arrays.asList(
                Message.create(Role.SYSTEM, "あなたは役に立つアシスタントです。"),
                Message.create(Role.USER, "2021年の日本シリーズで優勝したのは?"),
                Message.create(Role.ASSISTANT, "2021年の日本シリーズで優勝したのは、東京ヤクルトスワローズです。"),
                Message.create(Role.USER, "その球団の本拠地はどこですか?")
        ));
        System.out.println(req);
        GPTResponse res = gpt.post(req);
        System.out.println(res.toString());
    }
}
