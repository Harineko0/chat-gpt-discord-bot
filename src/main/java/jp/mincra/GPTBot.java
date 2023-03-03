package jp.mincra;

import jp.mincra.chatgpt.GPT;
import jp.mincra.discord.Listener;
import jp.mincra.discord.ThreadManager;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import net.dv8tion.jda.api.requests.GatewayIntent;
import net.dv8tion.jda.api.utils.cache.CacheFlag;

public class GPTBot {
    private static JDA jda;
    private static GPT gpt;
    private static ThreadManager threadManager;

    public static void main(String[] args) {
        gpt = new GPT();
        threadManager = new ThreadManager();
        jda = JDABuilder.createDefault(Constant.TOKEN, GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MEMBERS, GatewayIntent.GUILD_MESSAGES)
                .disableCache(CacheFlag.MEMBER_OVERRIDES, CacheFlag.VOICE_STATE)
                .setBulkDeleteSplittingEnabled(false)
                .setActivity(Activity.watching("Type /ask"))
                .addEventListeners(new Listener())
                .build();

        jda.updateCommands().addCommands(
                Commands.slash("ask", "Start conversation with me").addOption(OptionType.STRING, "thread-title", "This will be a name of thread.", true)
        ).queue();
    }

    public static JDA getJda() {
        return jda;
    }

    public static GPT getGpt() {
        return gpt;
    }

    public static ThreadManager getThreadManager() {
        return threadManager;
    }
}