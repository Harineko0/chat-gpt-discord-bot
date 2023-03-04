package jp.mincra.discord;

import jp.mincra.discord.entity.MessageEntity;
import net.dv8tion.jda.api.entities.User;

public class GPTConverter {
    public static String messageSyntax(MessageEntity message) {
        if (message.user() != null) {
            return "id: " + message.user().getId() + "\n" +
                    "  message: " + message.content();
        } else {
            return message.content();
        }
    }

    public static String addUserSyntax(User user) {
        return "id: " + user.getId() + "\n" +
                "  name: " + user.getName() + "\n";
    }
}
