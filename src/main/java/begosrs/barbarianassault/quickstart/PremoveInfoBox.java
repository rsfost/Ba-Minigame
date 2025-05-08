package begosrs.barbarianassault.quickstart;

import net.runelite.api.coords.WorldPoint;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class PremoveInfoBox extends InfoBox
{
	private static final BufferedImage premoveImage
			= ImageUtil.loadImageResource(PremoveInfoBox.class, "/premove.png");

	private final WorldPoint wp;

	public PremoveInfoBox(Plugin plugin, WorldPoint wp)
	{
		super(premoveImage, plugin);
		if (wp.getY() < QuickstartAssist.PREMOVE_Y_THRESHOLD)
		{
			setTooltip("Good premove");
		}
		else
		{
			setTooltip("Bad premove");
		}
		this.wp = wp;
	}

	@Override
	public String getText()
	{
		final int y = wp.getY();
		return String.format("%d.%dk", y / 1000, (y % 1000) / 100);
	}

	@Override
	public Color getTextColor()
	{
		if (wp.getY() < QuickstartAssist.PREMOVE_Y_THRESHOLD)
		{
			return Color.GREEN;
		}
		else
		{
			return Color.RED;
		}
	}
}
