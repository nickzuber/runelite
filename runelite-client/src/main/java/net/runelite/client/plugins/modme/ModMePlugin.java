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
import net.runelite.api.events.ScriptCallbackEvent;
import net.runelite.api.events.SetMessage;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.api.widgets.Widget;
import net.runelite.api.widgets.WidgetInfo;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.input.KeyManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

@PluginDescriptor(
	name = "Mod Me",
	description = "Take pretending to be a player mod to the next level.",
	tags = {"mod", "chat", "replace"},
	enabledByDefault = false
)
public class ModMePlugin extends Plugin
{
	private static final String SCRIPT_EVENT_SET_CHATBOX_INPUT = "setChatboxInput";

	@Inject
	private Client client;

	@Inject
	private ClientThread clientThread;

	@Inject
	private KeyManager keyManager;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Override
	protected void startUp() throws Exception {
		clientThread.invoke(() -> {
			if (client.getGameState() == GameState.LOGGED_IN) {
				updateChatboxText();
			}
		});
	}

	@Provides
    ModMeConfig getConfig(ConfigManager configManager) {
		return configManager.getConfig(ModMeConfig.class);
	}

	public void updateChatboxText() {
		Widget chatboxInput = client.getWidget(WidgetInfo.CHATBOX_INPUT);
		if (chatboxInput != null)
		{
			String username = client.getLocalPlayer().getName();
			String updatedChatbox = chatboxInput.getText().replace(username + ": ", getPlayerNameWithIcon() + ": ");
			chatboxInput.setText(updatedChatbox);
		}
	}

	/**
	 * Replaces chatbox text with the modded username.
	 *
	 * @param setMessage The chat message.
	 */
	@Subscribe
	public void onSetMessage(SetMessage setMessage) {
		if (client.getGameState() != GameState.LOGGED_IN) {
			return;
		}

		switch (setMessage.getType()) {
			case PUBLIC:
			case PUBLIC_MOD:
			case CLANCHAT:
			case PRIVATE_MESSAGE_RECEIVED:
			case PRIVATE_MESSAGE_SENT:
				break;
			default:
				return;
		}

		MessageNode messageNode = setMessage.getMessageNode();
		// Only replace name for your own player.
		if (messageNode.getName().equals(client.getLocalPlayer().getName())) {
			// clear RuneLite formatted message as the message node is
			// being reused.
			messageNode.setRuneLiteFormatMessage(null);
			messageNode.setName(getPlayerNameWithIcon());
			client.refreshChat();
		}
	}

	@Subscribe
	public void onScriptCallbackEvent(ScriptCallbackEvent scriptCallbackEvent) {
		switch (scriptCallbackEvent.getEventName()) {
			case SCRIPT_EVENT_SET_CHATBOX_INPUT:
				updateChatboxText();
				break;
		}
	}

	private String getPlayerNameWithIcon() {
		return IconID.PLAYER_MODERATOR + client.getLocalPlayer().getName();
	}
}
