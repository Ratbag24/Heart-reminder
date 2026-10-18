package com.ratbag24.reminders;

import java.awt.Color;

/**
 * One line the alert banner should be showing, and the colour it should be in.
 * <p>
 * The two travel together so a reminder's wording and its colour cannot drift
 * apart: the banner is handed finished lines rather than being left to look up
 * who each one belongs to.
 */
public final class Alert
{
	private final String text;
	private final Color colour;

	public Alert(String text, Color colour)
	{
		this.text = text;
		this.colour = colour;
	}

	public String getText()
	{
		return text;
	}

	public Color getColour()
	{
		return colour;
	}
}
