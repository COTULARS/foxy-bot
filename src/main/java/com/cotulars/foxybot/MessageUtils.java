package com.cotulars.foxybot;

import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.MessageBuilder;
import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.interactions.InteractionHook;

import java.awt.*;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

public class MessageUtils {

    public static Message generateEmbed(Color color, String text){
        MessageBuilder b = new MessageBuilder();
        b.setEmbeds(new EmbedBuilder()
                .setColor(color)
                .setDescription(new String(text.getBytes(), StandardCharsets.UTF_8))
                .build());
        return b.build();
    }

    public static void autoDelete(long time, TimeUnit timeUnit, Message message){
        message.delete().queueAfter(time, timeUnit);
    }
    public static void autoDelete(long time, TimeUnit timeUnit, InteractionHook message){
        message.deleteOriginal().queueAfter(time, timeUnit);
    }
}
