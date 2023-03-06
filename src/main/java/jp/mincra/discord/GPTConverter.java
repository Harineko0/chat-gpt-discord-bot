package jp.mincra.discord;

import jp.mincra.discord.entity.MessageEntity;
import jp.mincra.util.StringUtil;
import net.dv8tion.jda.api.entities.User;

public class GPTConverter {
    public static String encodeMessage(MessageEntity message) {
        if (message.user() != null) {
            return "id: " + message.user().getId() +
                    "  message: " + StringUtil.encodeEscape(message.content());
        } else {
            return StringUtil.encodeEscape(message.content());
        }
    }

    public static String addUserSyntax(User user) {
        return "id: " + user.getId() +
                "  name: " + user.getName();
    }
}
