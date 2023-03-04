package jp.mincra.discord;

import jp.mincra.Constant;
import jp.mincra.GPTBot;
import jp.mincra.chatgpt.dto.GPTRequest;
import jp.mincra.chatgpt.dto.GPTResponse;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import jp.mincra.discord.entity.MessageEntity;
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
import java.text.SimpleDateFormat;
import java.util.Date;
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

                ForumPost post = botForum.createForumPost(threadTitle, MessageCreateData.fromContent("じぷにゃんにゃ。何を話すにゃ？")).complete();
                Thread thread = new Thread(post.getThreadChannel());
                SimpleDateFormat df = new SimpleDateFormat("yyyy年MM月dd日のHH時mm分ss秒");
                Date date = new Date();

                thread.addMessage(new MessageEntity(Role.SYSTEM,
                        "あなたはまるで人間であるかのように振る舞います！あなたの名前は「じぷにゃん」です。" +
                                "ですますなどの敬語は使わないでください。語尾に「にゃ」をつけてください。語尾の「にゃ」の前にはですますはつきません。疑問文の最後は「か」の代わりに「にゃ」を付けてください。" +
                                "句読点(。)の代わりに！や、♬、♪を使用してください。また、記号(！や♬、♪)の後にあなたの感情に合ったtwimojiを複数個書き加えてください。" +
                                "以下の構文のようにユーザーの情報を渡します。あなたはそれに従って返答してください。\nid: 000000000000000000\n  name: 名前" +
                                "また、現在時刻は" + df.format(date) + "です。"));

                GPTBot.getThreadManager().registerThread(thread);

                event.reply("<#" + thread.getChannel().getId() + ">でお話しするにゃ").queue();

                break;
            case "resume":

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
                thread.addMessage(new MessageEntity(Role.USER, event.getMessage().getContentDisplay(), event.getAuthor()));

                try {
                    GPTRequest req = thread.generateRequest();
                    System.out.println("Request: " + req);
                    var future = channel.sendTyping().submit();

                    GPTResponse res = GPTBot.getGpt().post(req);
                    System.out.println("Response: " + res);
                    if (res.getChoices() != null) {
                        Message reply = res.getChoices().get(0).getMessage();
                        thread.addMessage(new MessageEntity(reply.getRole(), reply.getContent()));
                        channel.sendMessage(reply.getContent()).queue();
                    }

                    future.cancel(true);

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
                                .map(message -> new MessageEntity(
                                        // TODO isBotではなくisChatGPTBotで分岐できるように修正する
                                        message.getAuthor().isBot() ? Role.ASSISTANT : Role.USER,
                                        message.getContentDisplay(),
                                        message.getAuthor()
                                ))
                                .toList();

                        threadManager.registerThread(new Thread(channel, history));
                    });
        }
    }
}
