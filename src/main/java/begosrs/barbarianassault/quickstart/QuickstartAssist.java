/*
 * Copyright (c) 2025, rsfost <https://github.com/rsfost>
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
package begosrs.barbarianassault.quickstart;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.gameval.ItemID;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.game.ItemManager;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.Color;

public class QuickstartAssist
{
	public static final int PREMOVE_Y_THRESHOLD = 5300;

	private final Client client;
	private final InfoBoxManager infoBoxManager;
	private final ChatMessageManager chatManager;
	private final BaMinigameConfig config;

	private final PremoveInfoBox premoveInfoBox;

	private boolean checkPremove;

	@Inject
	public QuickstartAssist(
			Client client, BaMinigamePlugin plugin, ChatMessageManager chatManager,
			ItemManager itemManager, InfoBoxManager infoBoxManager, BaMinigameConfig config)
	{
		this.client = client;
		this.chatManager = chatManager;
		this.infoBoxManager = infoBoxManager;
		this.config = config;
		this.premoveInfoBox = new PremoveInfoBox(itemManager.getImage(ItemID.TRAIL_WATCH), plugin);
	}

	public void tick()
	{
		if (config.premoveIndicator() == PremoveIndicatorMode.DISABLE)
		{
			checkPremove = false;
			return;
		}

		if (checkPremove)
		{
			updatePremove();
			checkPremove = false;
		}
	}

	public void startWave()
	{
		checkPremove = true;
	}

	public void endWave()
	{
		infoBoxManager.removeInfoBox(premoveInfoBox);
		checkPremove = false;
	}

	private void updatePremove()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}

		final WorldPoint wp = player.getWorldLocation();
		final PremoveIndicatorMode indicatorMode = config.premoveIndicator();
		final String yStr = formatInt(wp.getY());
		final boolean goodPremove = wp.getY() < PREMOVE_Y_THRESHOLD;
		final String premoveStr = goodPremove ? "Good premove" : "Bad premove";

		if (indicatorMode == PremoveIndicatorMode.INFO_BOX || indicatorMode == PremoveIndicatorMode.INFO_BOX_AND_CHAT)
		{
			premoveInfoBox.setText(yStr);
			premoveInfoBox.setGoodPremove(goodPremove);
			premoveInfoBox.setTooltip(premoveStr);
			infoBoxManager.addInfoBox(premoveInfoBox);
		}
		if (indicatorMode == PremoveIndicatorMode.CHAT || indicatorMode == PremoveIndicatorMode.INFO_BOX_AND_CHAT)
		{
			String message = String.format("%s (%s)", premoveStr, yStr);
			if (config.enableGameChatColors())
			{
				message = ColorUtil.wrapWithColorTag(message, goodPremove ? Color.GREEN : Color.RED);
			}
			chatManager.queue(QueuedMessage.builder()
					.type(ChatMessageType.CONSOLE)
					.runeLiteFormattedMessage(message)
					.build());
		}
	}

	private static String formatInt(int a)
	{
		return String.format("%d.%dk", a / 1000, (a % 1000) / 100);
	}
}
