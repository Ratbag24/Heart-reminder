package com.ratbag24.reminders.visual;

import com.ratbag24.reminders.RemindersConfig;
import com.ratbag24.reminders.RemindersPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * A banner saying what to do about it, in white on red, for anything currently
 * asking for attention.
 * <p>
 * This is the loud one. The status panel tells you how things stand and the
 * screen edge tells you something is wrong; this says which thing and what to
 * do, in words, big enough to read without looking away from the fight. Drag it
 * wherever suits.
 */
public class AlertBannerOverlay extends Overlay
{
	private static final int PAD_X = 8;
	private static final int PAD_Y = 3;
	/** Gap between stacked banners when more than one thing is down. */
	private static final int GAP = 3;

	private final RemindersPlugin plugin;
	private final RemindersConfig config;

	@Inject
	private AlertBannerOverlay(RemindersPlugin plugin, RemindersConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.ABOVE_CHATBOX_RIGHT);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.alertBanner())
		{
			return null;
		}

		final List<String> alerts = plugin.getAlerts();

		if (alerts.isEmpty())
		{
			return null;
		}

		graphics.setFont(FontManager.getRunescapeBoldFont());

		final FontMetrics metrics = graphics.getFontMetrics();
		final Color background = config.alertColour();
		final Color border = background.brighter();

		int width = 0;

		for (String alert : alerts)
		{
			width = Math.max(width, metrics.stringWidth(alert));
		}

		width += PAD_X * 2;

		final int lineHeight = metrics.getHeight() + PAD_Y * 2;
		int y = 0;

		for (String alert : alerts)
		{
			graphics.setColor(background);
			graphics.fillRect(0, y, width, lineHeight);
			graphics.setColor(border);
			graphics.drawRect(0, y, width - 1, lineHeight - 1);

			final int textX = (width - metrics.stringWidth(alert)) / 2;
			final int textY = y + PAD_Y + metrics.getAscent();

			// Drawn twice: the game's background can be any colour, and a plain
			// white string over a bright scene is hard to read without it.
			graphics.setColor(Color.BLACK);
			graphics.drawString(alert, textX + 1, textY + 1);
			graphics.setColor(Color.WHITE);
			graphics.drawString(alert, textX, textY);

			y += lineHeight + GAP;
		}

		// The trailing gap is not part of the banner.
		return new Dimension(width, y - GAP);
	}
}
