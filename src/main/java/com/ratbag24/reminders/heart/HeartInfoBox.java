package com.ratbag24.reminders.heart;

import java.awt.Color;
import java.awt.image.BufferedImage;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBox;

/**
 * Counts the heart's remaining cooldown down in the infobox row.
 * <p>
 * The text is driven straight off {@link HeartCooldown} rather than off a
 * wall clock of its own, so it stays in step with the game even after a
 * logout, a world hop or a death reset.
 */
class HeartInfoBox extends InfoBox
{
	/** Below this the countdown turns green: time to get the next one ready. */
	private static final long NEARLY_READY_MILLIS = 30_000L;

	private final HeartCooldown cooldown;

	HeartInfoBox(BufferedImage image, Plugin plugin, HeartCooldown cooldown)
	{
		super(image, plugin);
		this.cooldown = cooldown;
	}

	@Override
	public String getText()
	{
		final long remaining = cooldown.getRemainingMillis(System.currentTimeMillis());
		final long totalSeconds = (remaining + 999L) / 1000L;
		return String.format("%d:%02d", totalSeconds / 60L, totalSeconds % 60L);
	}

	@Override
	public Color getTextColor()
	{
		final long remaining = cooldown.getRemainingMillis(System.currentTimeMillis());
		return remaining <= NEARLY_READY_MILLIS ? Color.GREEN.brighter() : Color.WHITE;
	}

	@Override
	public boolean render()
	{
		return cooldown.isOnCooldown();
	}
}
