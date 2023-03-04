package jp.mincra.discord.entity;

import jp.mincra.chatgpt.dto.Role;
import net.dv8tion.jda.api.entities.User;
import org.jetbrains.annotations.Nullable;

public record MessageEntity(Role role, String content, @Nullable User user) {
    public MessageEntity(Role role, String content) {
        this(role, content.replaceAll("\n", ""), null);
    }

    @Override
    public String toString() {
        return "{\"MessageEntity\":{"
                + "\"role\":\"" + role + "\""
                + ", \"content\":\"" + content + "\""
                + ", \"user\":" + user
                + "}}";
    }
}
