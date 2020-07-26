/*'
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * Copyright (c) 2018, Abexlry <abexlry@gmail.com>
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

import com.google.inject.Provides;
import java.awt.Color;
import javax.inject.Inject;
import net.runelite.api.*;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.VarClientStrChanged;
import net.runelite.api.kit.KitType;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@PluginDescriptor(
	name = "Faux Account Settings",
	description = "Take pretending to be a player mod to the next level.",
	tags = {"mod", "chat", "replace"},
	enabledByDefault = false
)
public class ModMePlugin extends Plugin
{
	private static final Logger logger = LoggerFactory.getLogger(ModMePlugin.class);
    private static final String CHATBOX_INPUT = "chatboxInput";
	private static final String SCRIPT_EVENT_SET_CHATBOX_INPUT = "setChatboxInput";
    private static final String SCRIPT_EVENT_BLOCK_CHAT_INPUT = "blockChatInput";
    private int i = 0;

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private ModMeConfig config;

	@Provides
	ModMeConfig provideConfig(ConfigManager configManager) {
		return configManager.getConfig(ModMeConfig.class);
	}

	@Override
	protected void startUp() throws Exception {
		clientThread.invoke(() -> {
			if (client.getGameState() == GameState.LOGGED_IN) {
				updateChatboxText();
			}
		});
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
    {
        if (event.getGroup().equals("modme")) {
            updateChatboxText();
            return;
        }
    }

	public void updateChatboxText() {
		Widget chatboxInput = client.getWidget(WidgetInfo.CHATBOX_INPUT);
		if (chatboxInput != null)
		{
			String username = client.getLocalPlayer().getName();

            String strippedChatBoxText = chatboxInput.getText().replaceAll("<img=\\d>", "");

			String updatedChatbox = strippedChatBoxText.replace(username + ": ", getPlayerNameWithIcon() + ": ");

//            logger.info("username: " + username);
//            logger.info("chatbox : " + chatboxInput.getText());
//            logger.info("chatbox*: " + strippedChatBoxText);
//            logger.info("updated : " + updatedChatbox);

//            logger.info("updated with '" + updatedChatbox + "'");
			chatboxInput.setText(updatedChatbox);
		} else {
//            logger.info("chatbox is null");
        }
	}

	private void setKit (KitType type, int item) {
        Player player = client.getLocalPlayer();
        player.getPlayerComposition().getEquipmentIds()[type.getIndex()] = item + 512;
        player.getPlayerComposition().setHash();
    }

	public void updateEquipment () {
		Player player = client.getLocalPlayer();
        int currentHeadID = player.getPlayerComposition().getEquipmentId(KitType.HEAD);
        int currentAmuletID = player.getPlayerComposition().getEquipmentId(KitType.AMULET);
		int currentTorsoID = player.getPlayerComposition().getEquipmentId(KitType.TORSO);
		int currentLegsID = player.getPlayerComposition().getEquipmentId(KitType.LEGS);
		int currentShieldID = player.getPlayerComposition().getEquipmentId(KitType.SHIELD);
		int currentCapeID = player.getPlayerComposition().getEquipmentId(KitType.CAPE);

        int overrideHeadID = config.getHeadID();
        int overrideAmuletID = config.getAmuletID();
        int overrideTorsoID = config.getTorsoID();
        int overrideLegsID = config.getLegsID();
        int overrideShieldID = config.getShieldID();
        int overrideWeaponID = config.getWeaponID();
        int overrideCapeID = config.getCapeID();
        int overrideGlovesID = config.getGlovesID();
        int overrideBootsID = config.getBootsID();

        // Head
        if (overrideHeadID > 0) {
            setKit(KitType.HEAD, overrideHeadID);
        } else {
            // no-op
        }

        // Head
        if (overrideAmuletID > 0) {
            setKit(KitType.AMULET, overrideAmuletID);
        } else {
            // no-op
        }

        // Torso
        if (overrideTorsoID > 0) {
            setKit(KitType.TORSO, overrideTorsoID);
        } else {
            if (currentTorsoID == ItemID.AHRIMS_ROBETOP_100 ||
                    currentTorsoID == ItemID.AHRIMS_ROBETOP_75 ||
                    currentTorsoID == ItemID.AHRIMS_ROBETOP_50 ||
                    currentTorsoID == ItemID.AHRIMS_ROBETOP_25) {
                setKit(KitType.TORSO, ItemID._3RD_AGE_ROBE_TOP);
            }
        }

        // Legs
        if (overrideLegsID > 0) {
            setKit(KitType.LEGS, overrideLegsID);
        } else {
            if (currentLegsID == ItemID.AHRIMS_ROBESKIRT_100 ||
                    currentLegsID == ItemID.AHRIMS_ROBESKIRT_75 ||
                    currentLegsID == ItemID.AHRIMS_ROBESKIRT_50 ||
                    currentLegsID == ItemID.AHRIMS_ROBESKIRT_25) {
                setKit(KitType.LEGS, ItemID._3RD_AGE_ROBE);
            }
        }

        // Shield
        if (overrideShieldID > 0) {
            setKit(KitType.SHIELD, overrideShieldID);
        } else {
            if (currentShieldID == ItemID.BOOK_OF_DARKNESS) {
                setKit(KitType.SHIELD, ItemID.ARCANE_SPIRIT_SHIELD);
            }
        }

        // Weapon
        if (overrideWeaponID > 0) {
            setKit(KitType.WEAPON, overrideWeaponID);
        } else {
            // no-op
        }

        // Gloves
        if (overrideGlovesID > 0) {
            setKit(KitType.HANDS, overrideGlovesID);
        } else {
            // no-op
        }

        // Boots
        if (overrideBootsID > 0) {
            setKit(KitType.BOOTS, overrideBootsID);
        } else {
            // no-op
        }

        // Cape
        if (overrideCapeID > 0) {
            setKit(KitType.CAPE, overrideCapeID);
        } else {
            if (currentCapeID == ItemID.FIRE_CAPE) {
                setKit(KitType.CAPE, ItemID.INFERNAL_CAPE);
            }
            if (currentCapeID == ItemID.FIRE_MAX_CAPE) {
                setKit(KitType.CAPE, ItemID.INFERNAL_MAX_CAPE_L);
            }
        }

	}

	@Subscribe
	public void onGameTick(GameTick gameTick)
	{
		updateEquipment();
	}

	/**
	 * Replaces chatbox text with the modded username.
	 *
	 * @param setMessage The chat message.
	 */
	@Subscribe
	public void onChatMessage(ChatMessage chatMessage) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		switch (chatMessage.getType()) {
			case PUBLICCHAT:
			case MODCHAT:
			case PRIVATECHATOUT:
			case PRIVATECHAT:
			case MODPRIVATECHAT:
			case FRIENDSCHAT:
				break;
			default:
				return;
		}

		MessageNode messageNode = chatMessage.getMessageNode();

		// Only replace name for your own player.
        if (messageNode.getName().replaceAll("<.*>", "").equals(client.getLocalPlayer().getName())) {
            // clear RuneLite formatted message as the message node is
            // being reused.
            messageNode.setRuneLiteFormatMessage(null);
            if (config.getGameMode() == GameMode.PLAYER_MODERATOR) {
                messageNode.setName(IconID.PLAYER_MODERATOR + getPlayerNameWithIcon());
            } else {
                messageNode.setName(getPlayerNameWithIcon());
            }
            client.refreshChat();
        }

        // For Zubes
        if (messageNode.getName().equals("Zubes")) {
            // clear RuneLite formatted message as the message node is
            // being reused.
            messageNode.setRuneLiteFormatMessage(null);
            messageNode.setName(IconID.PLAYER_MODERATOR + "Zubes");
            client.refreshChat();
        }
	}

//    @Subscribe
//    public void onVarClientStrChanged(VarClientStrChanged strChanged)
//    {
//        logger.info("THING CHANGED " + i++);
//        updateChatboxText();
//    }

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
//	    logger.info("onScriptCallbackEvent: " + scriptCallbackEvent.getEventName());
		switch (scriptCallbackEvent.getEventName()) {
            case SCRIPT_EVENT_BLOCK_CHAT_INPUT:
		    case SCRIPT_EVENT_SET_CHATBOX_INPUT:
            case CHATBOX_INPUT:
			    updateChatboxText();
				break;
		}
	}

	private String getPlayerNameWithIcon() {
		IconID icon;
		String displayName = client.getLocalPlayer().getName();
		GameMode mode = config.getGameMode();

        if (config.getFauxName().length() > 0) {
            displayName = config.getFauxName().trim();
        }

		if (mode == GameMode.HARDCORE_IRONMAN_MODE) {
			icon = IconID.HARDCORE_IRONMAN;
		} else if (mode == GameMode.IRONMAN_MODE) {
			icon = IconID.IRONMAN;
		} else if (mode == GameMode.ULTIMATE_IRONMAN_MODE) {
			icon = IconID.ULTIMATE_IRONMAN;
		} else if (mode == GameMode.JAGEX_MODERATOR) {
			icon = IconID.JAGEX_MODERATOR;
		} else {
			return displayName;
		}

		return icon + displayName;
	}
}
