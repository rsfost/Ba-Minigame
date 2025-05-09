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
