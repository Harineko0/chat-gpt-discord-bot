package jp.mincra.discord;

import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import jp.mincra.discord.entity.MessageEntity;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;

import java.util.ArrayList;
import java.util.List;

public class Thread {
    private final List<MessageEntity> history;
    private final List<String> usersId;
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
        if (message.user() != null && !usersId.contains(message.user().getId())) {
            history.add(new MessageEntity(Role.SYSTEM, GPTConverter.addUserSyntax(message.user())));
        }
        history.add(message);
    }

    public ThreadChannel getChannel() {
        return channel;
    }

    public GPTRequest generateRequest() {
        var messages = history.stream().map(message -> Message.create(message.role(), GPTConverter.messageSyntax(message))).toList();
        return new GPTRequest(messages);
    }

    @Override
    public String toString() {
        return "{\"Thread\":{"
                + "\"history\":" + history
                + ", \"channel\":" + channel
                + "}}";
    }
}
