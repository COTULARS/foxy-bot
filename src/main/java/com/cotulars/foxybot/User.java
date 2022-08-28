package com.cotulars.foxybot;

import net.dv8tion.jda.api.entities.Guild;

import java.util.ArrayList;

public class User {
    public String name, id, prefix;
    public ArrayList<String> roles = new ArrayList<>();
    public int warns = 0, level = 0, cash = 100;

    public User(net.dv8tion.jda.api.entities.User user, Guild guild) {
        name = user.getName();
        id = user.getId();
        guild.getMember(user).getRoles().forEach(role -> {
            roles.add(role.getId());
        });
    }

    public User(){}
}
