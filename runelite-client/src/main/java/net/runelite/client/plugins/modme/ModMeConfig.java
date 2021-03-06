/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package net.runelite.client.plugins.modme;

import java.awt.event.KeyEvent;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Keybind;

@ConfigGroup("modme")
public interface ModMeConfig extends Config
{
    @ConfigItem(
            position = 0,
            keyName = "gamemode",
            name = "Game mode",
            description = "The game mode to pretend to be."
    )
    default GameMode getGameMode() {
        return GameMode.JAGEX_MODERATOR;
    }

    @ConfigItem(
            position = 1,
            keyName = "fakename",
            name = "Name Change",
            description = "Change your display name."
    )
    default String getFauxName() {
        return "";
    }

    // Customer gear overrides
    @ConfigItem(
            position = 2,
            keyName = "head",
            name = "Head",
            description = "Change your helmet."
    )
    default int getHeadID() {
        return 0;
    }

    @ConfigItem(
            position = 3,
            keyName = "amulet",
            name = "Amulet",
            description = "Change your amulet."
    )
    default int getAmuletID() {
        return 0;
    }

    @ConfigItem(
            position = 4,
            keyName = "torso",
            name = "Torso",
            description = "Change your torso."
    )
    default int getTorsoID() {
        return 0;
    }

    @ConfigItem(
            position = 5,
            keyName = "legs",
            name = "Legs",
            description = "Change your legs."
    )
    default int getLegsID() {
        return 0;
    }

    @ConfigItem(
            position = 7,
            keyName = "gloves",
            name = "Gloves",
            description = "Change your gloves."
    )
    default int getGlovesID() {
        return 0;
    }

    @ConfigItem(
            position = 6,
            keyName = "boots",
            name = "Boots",
            description = "Change your boots."
    )
    default int getBootsID() {
        return 0;
    }

    @ConfigItem(
            position = 8,
            keyName = "shield",
            name = "Shield",
            description = "Change your shield."
    )
    default int getShieldID() {
        return 0;
    }

    @ConfigItem(
            position = 9,
            keyName = "weapon",
            name = "Weapon",
            description = "Change your weapon."
    )
    default int getWeaponID() {
        return 0;
    }

    @ConfigItem(
            position = 10,
            keyName = "cape",
            name = "Cape",
            description = "Change your cape."
    )
    default int getCapeID() {
        return 0;
    }
}
