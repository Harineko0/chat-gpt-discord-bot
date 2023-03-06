package jp.mincra.discord;

import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import jp.mincra.discord.entity.MessageEntity;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Thread {
    private List<MessageEntity> history;
    private List<String> usersId;
    private final ThreadChannel channel;

    public Thread(ThreadChannel channel) {
        this.history = new ArrayList<>();
        this.usersId = new ArrayList<>();
        this.channel = channel;
    }

    public Thread(ThreadChannel channel, List<MessageEntity> history) {
        this.history = history;
        this.usersId = new ArrayList<>();
        this.channel = channel;
    }

    public void addMessage(MessageEntity message) {
        var user = message.user();
        if (user != null) {
            var userId = user.getId();
            if (!usersId.contains(userId)) {
                history.add(new MessageEntity(Role.SYSTEM, GPTConverter.addUserSyntax(message.user())));
                usersId.add(userId);
            }
        }
        history.add(message);
    }

    public void addTraining(String trainingMessage) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日のHH時mm分");
        Date date = new Date();

        history.add(new MessageEntity(Role.SYSTEM, trainingMessage + "また、現在時刻は" + df.format(date) + "です。"));
    }

    public void removeLatestMessage() {
        if (history.size() > 0) {
            history.remove(history.size() - 1);
        }
    }

    public void removeAllMessages() {
        history.clear();
    }

    public ThreadChannel getChannel() {
        return channel;
    }

    public GPTRequest generateRequest() {
        var messages = history.stream().map(message -> Message.create(message.role(), GPTConverter.encodeMessage(message))).toList();
        return new GPTRequest(messages);
    }

    public void initialize() {
        history = new ArrayList<>();
        usersId = new ArrayList<>();
    }

    @Override
    public String toString() {
        return "{\"Thread\":{"
                + "\"history\":" + history
                + ", \"channel\":" + channel
                + "}}";
    }
}
