package com.ratbag24.reminders.visual;

import com.ratbag24.reminders.RemindersConfig;
import com.ratbag24.reminders.RemindersPlugin;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.api.Client;
import net.runelite.api.GameState;
import net.runelite.client.ui.overlay.Overlay;
import net.runelite.client.ui.overlay.OverlayLayer;
import net.runelite.client.ui.overlay.OverlayPosition;

/**
 * Throbs a coloured edge around the game view while any reminder is asking for
 * attention, and stops the moment nothing is.
 * <p>
 * Drawn as a glow around the edge rather than a wash over the whole screen:
 * what is in the middle of the view is usually the thing being fought, and
 * tinting it is the fastest way to get a plugin switched off.
 */
public class ScreenFlashOverlay extends Overlay
{
	/** How many one-pixel rings make up the glow. */
	private static final int BANDS = 7;

	private final Client client;
	private final RemindersPlugin plugin;
	private final RemindersConfig config;

	@Inject
	private ScreenFlashOverlay(Client client, RemindersPlugin plugin, RemindersConfig config)
	{
		super(plugin);
		this.client = client;
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.DYNAMIC);
		setLayer(OverlayLayer.ABOVE_WIDGETS);
		setPriority(PRIORITY_HIGHEST);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		if (!config.screenFlash()
			|| client.getGameState() != GameState.LOGGED_IN
			|| !plugin.isAnyReminderNudging())
		{
			return null;
		}

		final int x = client.getViewportXOffset();
		final int y = client.getViewportYOffset();
		final int width = client.getViewportWidth();
		final int height = client.getViewportHeight();

		// A viewport this small is the client mid-resize, not something to draw on.
		if (width < BANDS * 2 || height < BANDS * 2)
		{
			return null;
		}

		final long now = System.currentTimeMillis();
		final Color colour = config.flashColour();

		// Nested rings fading inwards, so the edge reads as a glow rather than
		// a drawn-on border.
		for (int i = 0; i < BANDS; i++)
		{
			final double falloff = 1.0 - (i / (double) BANDS);
			graphics.setColor(Pulse.fade(colour, now, falloff));
			graphics.drawRect(x + i, y + i, width - 1 - (i * 2), height - 1 - (i * 2));
		}

		return null;
	}
}
