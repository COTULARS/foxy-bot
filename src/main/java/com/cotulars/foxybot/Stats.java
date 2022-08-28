package com.cotulars.foxybot;

import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;

import static com.cotulars.foxybot.Utils.UTF8;

public class Stats implements Runnable {
    private static Thread thread = new Thread(new Stats());
    private static JDA jda;
    private TextChannel users;
    private TextChannel online;

    @Override
    public void run() {
        try {
            if (!jda.getGuildById("1010954745101435012").getTextChannels().get(0).getName().equals(UTF8("Участников") + "  " + jda.getUsers().size()))
                users = jda.getGuildById("1010954745101435012").getTextChannels().get(0);
            else {
                jda.getGuildById("1010954745101435012").createTextChannel(UTF8("Участников") + "  " + jda.getUsers().size()).queue();
                users = jda.getGuildById("1010954745101435012").getTextChannels().get(0);
            }
        } catch (Exception e){
            jda.getGuildById("1010954745101435012").createTextChannel(UTF8("Участников") + "  " + jda.getUsers().size()).queue();
            users = jda.getGuildById("1010954745101435012").getTextChannels().get(0);
        }

        while (true){
            users.getManager().setName(UTF8("Участников") + "  " + jda.getUsers().size()).queue();

            try {
                Thread.sleep(300000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }


    }

    public static void start(JDA _jda){
        jda = _jda;
        thread.start();
    }
}
