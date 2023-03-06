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
import java.util.Collections;
import java.util.Date;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        ForumChannel botForum = GPTBot.getJda().getForumChannelById(Constant.GPT_FORUM_ID);

        switch (event.getName()) {
            case "talk":
                var title = event.getOption("thread-title");
                var trainingOp = event.getOption("type");
                String threadTitle = Objects.requireNonNull(title).getAsString();
                String training = TrainingMessages.assistant;
                if (trainingOp != null) {
                    switch (trainingOp.getAsString()){
                        case "nya":
                            training = TrainingMessages.neko;
                            break;
                        case "assistant":
                            training = TrainingMessages.assistant;
                            break;
                        default:
                            event.reply("その喋り方は登録されてないにゃ").queue();
                            return;
                    }
                }

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

                thread.addTraining(training);

                GPTBot.getThreadManager().registerThread(thread);

                event.reply("<#" + thread.getChannel().getId() + ">でお話しするにゃ").queue();

                break;
            case "forget":
                String threadId = event.getChannel().getId();
                ThreadManager threadManager = GPTBot.getThreadManager();
                threadManager.clearMessagesAll(threadId);
                threadManager.getThread(threadId).addTraining(TrainingMessages.assistant);
                event.reply("あれ？今まで何話してたっけにゃ？").queue();
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
                        String repContent = reply.getContent();

                        // endkeyword が入力されていた場合はキャッシュを削除する
                        boolean isEnd = false; //repContent.endsWith(TrainingMessages.endKeyword);
                        if (isEnd) {
                            repContent = repContent.replaceAll(TrainingMessages.endKeyword, "");
                        }
                        thread.addMessage(new MessageEntity(reply.getRole(), repContent));
                        channel.sendMessage(repContent).queue(success -> future.cancel(true));

                        if (isEnd) {
                            thread.initialize();
                            thread.addMessage(new MessageEntity(Role.SYSTEM, TrainingMessages.neko));
                        }
                    } else {
                        channel.sendMessage("ごめんにゃ、頭がパンクしちゃったにゃ。").queue();
                        thread.removeLatestMessage();
                        future.cancel(true);
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
            channel.getHistoryBefore(latest, 10)
                    .queue(messageHistory -> {
                        var history = new java.util.ArrayList<>(messageHistory.getRetrievedHistory().stream()
                                .map(message -> new MessageEntity(
                                        // TODO isBotではなくisChatGPTBotで分岐できるように修正する
                                        message.getAuthor().isBot() ? Role.ASSISTANT : Role.USER,
                                        message.getContentDisplay(),
                                        message.getAuthor().isBot() ? null : message.getAuthor()
                                ))
                                .toList());
                        // 猫語・顔文字など調教文を入力
                        history.add(new MessageEntity(Role.SYSTEM, TrainingMessages.neko));
                        Collections.reverse(history);

                        threadManager.registerThread(new Thread(channel, history));
                    });
        }
    }
}
