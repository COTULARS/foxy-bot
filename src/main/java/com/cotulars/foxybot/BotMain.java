package com.cotulars.foxybot;

import com.cotulars.foxybot.Buttons.Buttons;
import com.cotulars.foxybot.Commands.Commands;
import com.cotulars.foxybot.Commands.CommandsListener;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.infinitys.logger.Log;
import io.github.cdimascio.dotenv.Dotenv;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.Activity;

import java.io.FileInputStream;

public class BotMain {

    public static void main(String[] args) throws Exception {
        Dotenv dotenv = Dotenv.load();

        FileInputStream serviceAccount =
                new FileInputStream("foxybot-cotulars-firebase-adminsdk-1skkx-70bda25168.json");

        FirebaseOptions options = new FirebaseOptions.Builder()
                .setDatabaseUrl("https://foxybot-cotulars-default-rtdb.europe-west1.firebasedatabase.app")
                .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                .build();

        FirebaseApp.initializeApp(options);

        JDA jda = JDABuilder.createDefault(dotenv.get("TOKEN"))
                .setActivity(Activity.playing("Kus"))
                .addEventListeners(new CommandsListener(), new Buttons())
                .build();

        jda.awaitReady();
        jda.getCategories().get(1).getTextChannels().get(0).sendMessage("test").queue();

        Log.init("logs");
        Log.enableMemReporter();
        Log.d("Main", "BotStarted");

        Database.init();

        Stats.start(jda);
    }
}
