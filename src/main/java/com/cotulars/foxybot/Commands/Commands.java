package com.cotulars.foxybot.Commands;

import com.cotulars.foxybot.Buttons.BtnType;
import com.cotulars.foxybot.Buttons.Buttons;
import com.cotulars.foxybot.Buttons.OnClickButton;
import com.cotulars.foxybot.Database;
import com.cotulars.foxybot.MessageUtils;
import com.cotulars.foxybot.User;
import com.google.firebase.database.*;
import com.google.gson.Gson;
import com.infinitys.logger.Log;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.*;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.interactions.commands.OptionMapping;
import net.dv8tion.jda.api.interactions.components.ActionRow;
import net.dv8tion.jda.api.interactions.components.buttons.Button;

import java.awt.*;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

import static com.cotulars.foxybot.Utils.UTF8;

public class Commands {

    public static void clear(SlashCommandInteractionEvent event) {
        MessageHistory msgs = new MessageHistory(event.getChannel());
        TextChannel chanel = event.getChannel().asTextChannel();

        int messageCount = Objects.requireNonNull(event.getOption("message_count")).getAsInt();

        if (messageCount < 2 || messageCount > 100){
            event.reply(MessageUtils.generateEmbed(Color.RED, "**Количество сообщений должно быть от 2 до 100**")).queue(message ->{
                MessageUtils.autoDelete(6, TimeUnit.SECONDS, message);
            });
            return;
        }

        try {
            List<Message> msgsList = msgs.retrievePast(messageCount).complete();
            chanel.deleteMessages(msgsList).queue(obj ->{
                event.reply(MessageUtils.generateEmbed(
                        Color.GREEN,
                        "**Удалено " + messageCount + " последних  сообщений (это сообщение исчезнет через 6 секунд)**"
                )).queue(message -> {
                    MessageUtils.autoDelete(6, TimeUnit.SECONDS, message);
                });
            });
        }catch (Exception e){
            if (e.getMessage().equals("Must provide at least 2 or at most 100 messages to be deleted.")){
                event.reply(MessageUtils.generateEmbed(
                        Color.RED,
                        "В канале меньше 2х сообщений, поэтому чисти ручками :)"
                )).queue(message -> {
                    MessageUtils.autoDelete(6, TimeUnit.SECONDS, message);
                });
            } else if (e.getMessage().equals("Messages collection may not be empty")){
                event.reply(MessageUtils.generateEmbed(
                        Color.RED,
                        "В канале нет сообщений :("
                )).queue(message -> {
                    MessageUtils.autoDelete(6, TimeUnit.SECONDS, message);
                });
            } else {
                Log.e("Clear-" + event.getGuild().getName(), "Failed to clear messages in: '" + chanel.getName() + "' Error: '" + e.getMessage() + "'");
                event.reply(MessageUtils.generateEmbed(
                        Color.RED,
                        "Failed to delete messages. Error: '" + e.getMessage() + "'"
                )).queue();
            }
        }
    }
    public static void kick(SlashCommandInteractionEvent event){

        List<Role> userRoles = Objects.requireNonNull(Objects.requireNonNull(event.getGuild()).getMember(event.getUser())).getRoles();

        Log.i("Roles", String.valueOf(event.getGuild().getRoles().indexOf(userRoles.get(0))));



        OptionMapping option = event.getOption("user");
        Guild server = event.getJDA().getGuildById(Objects.requireNonNull(event.getGuild()).getId());

        MessageBuilder mb = new MessageBuilder(MessageUtils.generateEmbed(
                Color.GREEN,
                "исключить пользователя " + UTF8(option.getAsUser().getAsTag()) + " с сервера?"
        ));

        Button ok, done;

        ok = Buttons.generateButton(event.getGuild().getId(), BtnType.GREEN, "исключить", new OnClickButton() {
            @Override
            public void onClick(ButtonInteractionEvent event) {
                try {
                    server.kick(option.getAsUser()).queue(obj ->{
                        event.reply(MessageUtils.generateEmbed(
                                Color.GREEN,
                                "Пользователь " + UTF8(option.getAsUser().getName()) + " был исключён с сервера"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });
                    });
                } catch (Exception e){
                    if (e.getMessage().equals("Can't modify a member with higher or equal highest role than yourself!")){
                        event.reply(MessageUtils.generateEmbed(
                                Color.RED,
                                "Вы не можете исключить этого участника (" + option.getAsUser().getName() + ") Потому что уровень ваших превилегий ниже чем у него, или вы пытаетсь исключить себя"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });

                    } else if (e.getMessage().equals("Cannot kick the owner of a guild!")){
                        event.reply(MessageUtils.generateEmbed(
                                Color.RED,
                                "Сам то понял на кого руку поднял? и вообще как ты себе это представлял?"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });
                    }else {
                        Log.e("Kick-" + event.getGuild().getName(), "Failed to kick user: '" + option.getAsUser().getAsTag() + "' Error: '" + e.getMessage() + "'");
                        event.reply(MessageUtils.generateEmbed(Color.RED, "Ошибка при попытки исключить участника " + option.getAsUser().getName() + ". Ошибка: '" + e.getMessage() + "'")).queue(msg ->{
                            event.getMessage().delete().queue();
                        });
                    }
                }
            }
        });
        done = Buttons.generateButton(event.getGuild().getId(), BtnType.RED, "отмена", new OnClickButton() {
            @Override
            public void onClick(ButtonInteractionEvent event) {
                event.getMessage().delete().queue();
            }
        });

        mb.setActionRows(ActionRow.of(done, ok));

        event.reply(mb.build()).queue();

    }

    public static void ban(SlashCommandInteractionEvent event){
        int days = 2;
        String desc = "По решению администрации";

        try {
            days = Objects.requireNonNull(event.getOption("days")).getAsInt();
        } catch (Exception e){}
        try {
            desc = Objects.requireNonNull(event.getOption("desc")).getAsString();
        }catch (Exception e){}


        OptionMapping option = event.getOption("user");
        Guild server = event.getJDA().getGuildById(Objects.requireNonNull(event.getGuild()).getId());

        MessageBuilder mb = new MessageBuilder(MessageUtils.generateEmbed(
                Color.GREEN,
                "исключить пользователя " + UTF8(option.getAsUser().getAsTag()) + " с сервера?"
        ));

        Button ok, done;

        int finalDays = days;
        String finalDesc = desc;
        ok = Buttons.generateButton(event.getGuild().getId(), BtnType.GREEN, "забанить", new OnClickButton() {
            @Override
            public void onClick(ButtonInteractionEvent event) {
                try {
                    server.ban(option.getAsUser(), finalDays, finalDesc).queue(obj ->{
                        event.reply(MessageUtils.generateEmbed(
                                Color.GREEN,
                                "Пользователь " + UTF8(option.getAsUser().getName()) + " был исключён с сервера"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });
                    });
                } catch (Exception e){
                    if (e.getMessage().equals("Can't modify a member with higher or equal highest role than yourself!")){
                        event.reply(MessageUtils.generateEmbed(
                                Color.RED,
                                "Вы не можете исключить этого участника (" + option.getAsUser().getName() + ") Потому что уровень ваших превилегий ниже чем у него, или вы пытаетсь исключить себя"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });

                    } else if (e.getMessage().equals("Cannot kick the owner of a guild!")){
                        event.reply(MessageUtils.generateEmbed(
                                Color.RED,
                                "Сам то понял на кого руку поднял? и вообще как ты себе это представлял?"
                        )).queue(msg -> {
                            event.getMessage().delete().queue();
                            MessageUtils.autoDelete(6, TimeUnit.SECONDS, msg);
                        });
                    }else {
                        Log.e("Kick-" + event.getGuild().getName(), "Failed to kick user: '" + option.getAsUser().getAsTag() + "' Error: '" + e.getMessage() + "'");
                        event.reply(MessageUtils.generateEmbed(Color.RED, "Ошибка при попытки исключить участника " + option.getAsUser().getName() + ". Ошибка: '" + e.getMessage() + "'")).queue(msg ->{
                            event.getMessage().delete().queue();
                        });
                    }
                }
            }
        });
        done = Buttons.generateButton(event.getGuild().getId(), BtnType.RED, "отмена", new OnClickButton() {
            @Override
            public void onClick(ButtonInteractionEvent event) {
                event.getMessage().delete().queue();
            }
        });

        mb.setActionRows(ActionRow.of(done, ok));

        event.reply(mb.build()).queue();

    }

    public static void warn(SlashCommandInteractionEvent event){
        Guild guild = event.getGuild();
        net.dv8tion.jda.api.entities.User userOp = event.getOption("user").getAsUser();

        event.deferReply().queue();


        Database.getUser(guild.getId(), userOp.getId(), user -> {
            if (user == null){
                User _user = new User(userOp, event.getGuild());
                _user.warns = 1;

                Database.pushUser(guild.getId(), _user, () -> {
                    event.getHook().sendMessage("ok").queue(msg -> {
                        msg.delete().queue();
                    });
                    event.getChannel().sendMessage(MessageUtils.generateEmbed(Color.YELLOW, "Участник " + userOp.getAsTag() + "получает предупреждение!\nТеперь у него " + _user.warns + " предупреждений!")).queue();

                });
            } else {
                user.warns++;

                Database.pushUser(guild.getId(), user, () -> {
                    event.getHook().sendMessage("ok").queue(msg -> {
                        msg.delete().queue();
                    });
                    event.getChannel().sendMessage(MessageUtils.generateEmbed(Color.YELLOW, "Участник " + userOp.getAsTag() + "получает предупреждение!\nТеперь у него " + user.warns + " предупреждений!")).queue();

                });
            }
        });
    }


}
