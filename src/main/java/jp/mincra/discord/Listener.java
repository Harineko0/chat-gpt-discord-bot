package jp.mincra.discord;

import jp.mincra.Constant;
import jp.mincra.GPTBot;
import jp.mincra.chatgpt.dto.GPTResponse;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.concrete.ThreadChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.session.ReadyEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        ForumChannel botForum = GPTBot.getJda().getForumChannelById(Constant.GPT_FORUM_ID);

        switch (event.getName()) {
            case "talk":
                String threadTitle = Objects.requireNonNull(event.getOption("thread-title")).getAsString();

                if (botForum == null) {
                    event.reply("フォーラムが見つかりません！サーバー管理者に連絡してください。").queue();
                    return;
                }

                if (botForum.getThreadChannels().stream().anyMatch(channel -> channel.getId().equals(threadTitle))) {
                    event.reply("スレッド名が重複しています！別の名前を入力してください。").queue();
                    return;
                }

                ForumPost post = botForum.createForumPost(threadTitle, MessageCreateData.fromContent("私は役に立つアシスタントです")).complete();
                Thread thread = new Thread(post.getThreadChannel());
                thread.addMessage(Message.create(Role.SYSTEM, "あなたは役に立つアシスタントです。"));

                GPTBot.getThreadManager().registerThread(thread);

                event.reply("<#" + thread.getChannel().getId() + ">でお話ししましょう").queue();

                break;

        }
    }

    @Override
    public void onMessageReceived(MessageReceivedEvent event)
    {
        if (event.isFromThread() && !event.getAuthor().isBot())
        {
            MessageChannel channel = event.getChannel();
            String id = channel.getId();
            ThreadManager threadManager = GPTBot.getThreadManager();

            if (threadManager.isRegistered(id)) {
                Thread thread = threadManager.getThread(id);
                thread.addMessage(Message.create(Role.USER, event.getMessage().getContentDisplay().replaceAll("\n", "")));
                System.out.println("Thread: " + thread);

                try {
                    GPTResponse res = GPTBot.getGpt().post(thread.generateRequest());
                    System.out.println("Response: " + res);
                    if (res.getChoices() != null) {
                        Message reply = res.getChoices().get(0).getMessage();
                        thread.addMessage(Message.create(reply.getRole(), reply.getContent().replaceAll("\n", "")));
                        channel.sendMessage(reply.getContent()).queue();
                    }


                } catch (IOException | InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        }
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        // 起きているスレッドを再読み込みする
        ForumChannel botForum = GPTBot.getJda().getForumChannelById(Constant.GPT_FORUM_ID);

        if (botForum == null) {
            System.out.print("Forum not found;");
            return;
        }

        var threadManager = GPTBot.getThreadManager();
        var channels = botForum.getThreadChannels().stream()
                .filter(channel -> channel.isJoined() && !channel.isArchived()).toList();
        for (ThreadChannel channel : channels) {
            String latest = channel.getLatestMessageId();
            // 直近10件のみを取得
            channel.getHistoryBefore(latest, 10)
                    .queue(messageHistory -> {
                        var history = messageHistory.getRetrievedHistory().stream()
                                .map(message -> Message.create(
                                        // TODO isBotではなくisChatGPTBotで分岐できるように修正する
                                        message.getAuthor().isBot() ? Role.ASSISTANT : Role.USER,
                                        message.getContentDisplay().replaceAll("\n", "")
                                ))
                                .toList();

                        threadManager.registerThread(new Thread(channel, history));
                    });
        }
    }
}
