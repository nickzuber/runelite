package net.runelite.client.plugins.modme;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum GameMode {
    JAGEX_MODERATOR("Jagex Moderator"),
    PLAYER_MODERATOR("Player Moderator"),
    IRONMAN_MODE("Ironman"),
    HARDCORE_IRONMAN_MODE("Harcode Ironman"),
    ULTIMATE_IRONMAN_MODE("Ultimate Ironman");

    private String name;

    @Override
    public String toString() {
        return getName();
    }
}