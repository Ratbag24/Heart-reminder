package com.ratbag24.reminders.heart;

/**
 * Which heart the tracked cooldown belongs to.
 * <p>
 * The imbued and saturated hearts share a single cooldown varbit, so the heart
 * in use can only be told apart by how long that cooldown started out: seven
 * minutes for the imbued heart, five for the saturated one. When the cooldown
 * was already running before we started watching it, neither length can be
 * ruled out and the type stays {@link #UNKNOWN}.
 */
public enum HeartType
{
	UNKNOWN("Heart"),
	IMBUED("Imbued heart"),
	SATURATED("Saturated heart");

	private final String displayName;

	HeartType(String displayName)
	{
		this.displayName = displayName;
	}

	public String getDisplayName()
	{
		return displayName;
	}
}
