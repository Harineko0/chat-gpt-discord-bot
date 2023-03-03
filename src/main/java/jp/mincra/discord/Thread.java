package jp.mincra.discord;

import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.Message;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.ArrayList;
import java.util.List;

public class Thread {
    private final List<Message> history;
    private final ThreadChannel channel;

    public Thread(ThreadChannel channel) {
        this.history = new ArrayList<>();
        this.channel = channel;
    }

    public void addMessage(Message message) {
        history.add(message);
        if (history.size() > 4) {
            history.remove(0);
        }
    }

    public ThreadChannel getChannel() {
        return channel;
    }

    public GPTRequest generateRequest() {
        return new GPTRequest(history);
    }

    @Override
    public String toString() {
        return "{\"Thread\":{"
                + "\"history\":" + history
                + ", \"channel\":" + channel
                + "}}";
    }
}
