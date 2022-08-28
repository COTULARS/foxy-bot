package com.cotulars.foxybot.Buttons;

import com.cotulars.foxybot.Utils;
import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import net.dv8tion.jda.api.interactions.components.buttons.Button;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import static com.cotulars.foxybot.Utils.UTF8;

public class Buttons extends ListenerAdapter {
    private static HashMap<String, HashMap<String, OnClickButton>> clicks = new HashMap<>();

    public static Button generateButton(String serverId, BtnType btnType, String text, OnClickButton onClickButton){
        Button btn;
        String utfText = UTF8(text);
        String id = Utils.getRandomId(15);


        switch (btnType){

            case RED:
                btn = Button.danger(id, utfText);
                break;
            case GREEN:
                btn = Button.success(id, utfText);
                break;
            case GRAY:
                btn = Button.secondary(id, utfText);
                break;
            case BLUE:
                btn = Button.primary(id, utfText);
                break;
            default:
                btn = Button.secondary(id, utfText);
                break;
        }
        if (!clicks.containsKey(serverId))
            clicks.put(serverId, new HashMap<>());

        clicks.get(serverId).put(id, onClickButton);

        return btn;
    }

    @Override
    public void onButtonInteraction(@NotNull ButtonInteractionEvent event) {
        clicks.get(event.getGuild().getId()).get(event.getButton().getId()).onClick(event);
        clicks.get(event.getGuild().getId()).remove(event.getButton().getId());
    }
}
