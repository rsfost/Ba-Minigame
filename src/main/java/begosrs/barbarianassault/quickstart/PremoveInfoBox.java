package begosrs.barbarianassault.quickstart;

import lombok.Getter;
import lombok.Setter;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;
import net.runelite.client.util.ImageUtil;

import java.awt.Color;
import java.awt.image.BufferedImage;

public class PremoveInfoBox extends InfoBox
{
	private static final BufferedImage premoveImage
			= ImageUtil.loadImageResource(PremoveInfoBox.class, "/premove.png");

	@Getter @Setter
	private String text;
	@Getter @Setter
	private boolean goodPremove;

	public PremoveInfoBox(Plugin plugin)
	{
		super(premoveImage, plugin);
	}

	@Override
	public Color getTextColor()
	{
		return goodPremove ? Color.GREEN : Color.RED;
	}
}
