package com.cotulars.foxybot.Buttons;

import net.dv8tion.jda.api.events.interaction.component.ButtonInteractionEvent;

public interface OnClickButton {
    void onClick(ButtonInteractionEvent event);
}
