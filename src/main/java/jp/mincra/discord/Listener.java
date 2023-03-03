package jp.mincra.discord;

import jp.mincra.Constant;
import jp.mincra.GPTBot;
import jp.mincra.chatgpt.dto.GPTResponse;
import jp.mincra.chatgpt.dto.Message;
import jp.mincra.chatgpt.dto.Role;
import net.dv8tion.jda.api.entities.channel.concrete.ForumChannel;
import net.dv8tion.jda.api.entities.channel.forums.ForumPost;
import net.dv8tion.jda.api.entities.channel.middleman.MessageChannel;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.utils.messages.MessageCreateData;

import java.io.IOException;
import java.util.Objects;

public class Listener extends ListenerAdapter {

    @Override
    public void onSlashCommandInteraction(SlashCommandInteractionEvent event)
    {
        switch (event.getName()) {
            case "ask":
                String threadTitle = Objects.requireNonNull(event.getOption("thread-title")).getAsString();
                ForumChannel forum = GPTBot.getJda().getForumChannelById(Constant.GPT_FORUM_ID);

                if (forum != null) {
                    ForumPost post = forum.createForumPost(threadTitle, MessageCreateData.fromContent("私は役に立つアシスタントです")).complete();
                    Thread thread = new Thread(post.getThreadChannel());
                    thread.addMessage(Message.create(Role.SYSTEM, "あなたは役に立つアシスタントです。"));

                    GPTBot.getThreadManager().addThread(thread);

                    event.reply("<#" + thread.getChannel().getId() + ">でお話ししましょう").queue();
                } else {
                    System.out.println("Forum not found.");
                }

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
}
