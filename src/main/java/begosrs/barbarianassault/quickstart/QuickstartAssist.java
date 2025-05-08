package begosrs.barbarianassault.quickstart;

import begosrs.barbarianassault.BaMinigameConfig;
import begosrs.barbarianassault.BaMinigamePlugin;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.coords.WorldPoint;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.ColorUtil;

import javax.inject.Inject;
import java.awt.Color;

public class QuickstartAssist
{
	public static final int PREMOVE_Y_THRESHOLD = 5300;

	private final Client client;
	private final BaMinigamePlugin plugin;
	private final InfoBoxManager infoBoxManager;
	private final ChatMessageManager chatManager;
	private final BaMinigameConfig config;

	private PremoveInfoBox premoveInfoBox;

	@Inject
	public QuickstartAssist(
			Client client, BaMinigamePlugin plugin, ChatMessageManager chatManager,
			InfoBoxManager infoBoxManager, BaMinigameConfig config)
	{
		this.client = client;
		this.plugin = plugin;
		this.chatManager = chatManager;
		this.infoBoxManager = infoBoxManager;
		this.config = config;
	}

	public void startWave()
	{
		Player player = client.getLocalPlayer();
		if (player == null)
		{
			return;
		}
		WorldPoint wp = player.getWorldLocation();

		final PremoveIndicatorMode indicatorMode = config.premoveIndicator();
		if (indicatorMode == PremoveIndicatorMode.INFO_BOX || indicatorMode == PremoveIndicatorMode.INFO_BOX_AND_CHAT)
		{
			if (premoveInfoBox != null)
			{
				infoBoxManager.removeInfoBox(premoveInfoBox);
			}
			premoveInfoBox = new PremoveInfoBox(plugin, wp);
			infoBoxManager.addInfoBox(premoveInfoBox);
		}
		if (indicatorMode == PremoveIndicatorMode.CHAT || indicatorMode == PremoveIndicatorMode.INFO_BOX_AND_CHAT)
		{
			boolean goodPremove = wp.getY() < PREMOVE_Y_THRESHOLD;
			String message = String.format("Premove condition is %s (%d).",
					goodPremove ? "good" : "bad", wp.getY());
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

	public void endWave()
	{
		if (premoveInfoBox != null)
		{
			infoBoxManager.removeInfoBox(premoveInfoBox);
			premoveInfoBox = null;
		}
	}
}
