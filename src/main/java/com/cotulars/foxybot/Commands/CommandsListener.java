package com.cotulars.foxybot.Commands;

import net.dv8tion.jda.api.events.guild.GuildReadyEvent;
import net.dv8tion.jda.api.events.interaction.command.SlashCommandInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.commands.OptionType;
import net.dv8tion.jda.api.interactions.commands.build.CommandData;
import net.dv8tion.jda.api.interactions.commands.build.Commands;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;

import static com.cotulars.foxybot.Utils.UTF8;

public class CommandsListener extends ListenerAdapter {



    @Override
    public void onSlashCommandInteraction(@NotNull SlashCommandInteractionEvent event) {
        String command = event.getName();
        switch (command){
            case "test":
                event.reply("Hello " + event.getUser().getName()).queue();
                break;
            case "clear":
                com.cotulars.foxybot.Commands.Commands.clear(event);
                break;
            case "kick":
                com.cotulars.foxybot.Commands.Commands.kick(event);
                break;
            case "ban":
                com.cotulars.foxybot.Commands.Commands.ban(event);
                break;
            case "warn":
                com.cotulars.foxybot.Commands.Commands.warn(event);
                break;
        }
    }

    @Override
    public void onGuildReady(@NotNull GuildReadyEvent event) {
        ArrayList<CommandData> commands = new ArrayList<>();
        commands.add(Commands.slash("test", "test command " + event.getGuild().getName()));
        commands.add(Commands.slash("clear", UTF8("удаляет пследние сообщения"))
                .addOption(OptionType.INTEGER, "message_count", UTF8("количество последних сообщений для удаления"), true)
        );
        commands.add(Commands.slash("kick", UTF8("исключает участника с сервера"))
                .addOption(OptionType.USER, "user", UTF8("участник которого нужно исключить"), true));
        commands.add(Commands.slash("ban", UTF8("исключает пользователя с срвера без возможности возвращения"))
                .addOption(OptionType.USER, "user", UTF8("Пользователь который больше не вернётся на сервер"), true)
                .addOption(OptionType.INTEGER, "days", UTF8("Количество дней в течении которых он не сможет вернуться (1 по умолчанию)"), false)
                .addOption(OptionType.STRING, "desc", UTF8("Причина бана"), false));
        commands.add(Commands.slash("warn", UTF8("выдаёт предупреждеие пользователю"))
                .addOption(OptionType.USER, "user", UTF8("участник которому нужно выдать предупрждение"), true));

        event.getGuild().updateCommands().addCommands(commands).queue();
    }
}
