package com.ratbag24.reminders.visual;

import java.awt.Font;
import net.runelite.client.ui.FontManager;

/**
 * Which of the game's own typefaces the alert banner is drawn in, and so how
 * big the banner ends up.
 * <p>
 * These are the three faces RuneLite ships rather than a size in points. They
 * are bitmap faces drawn at a fixed size, so scaling one would make it fuzzy;
 * picking between them keeps the banner looking like it belongs to the game.
 */
public enum BannerFont
{
	SMALL("Small"),
	REGULAR("Regular"),
	LARGE("Large");

	private final String displayName;

	BannerFont(String displayName)
	{
		this.displayName = displayName;
	}

	/** The font itself. Resolved at render time, never cached. */
	public Font getFont()
	{
		switch (this)
		{
			case SMALL:
				return FontManager.getRunescapeSmallFont();
			case LARGE:
				return FontManager.getRunescapeBoldFont();
			default:
				return FontManager.getRunescapeFont();
		}
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
