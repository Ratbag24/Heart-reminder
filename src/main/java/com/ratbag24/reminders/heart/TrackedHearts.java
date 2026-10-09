package com.ratbag24.reminders.heart;

/** Which of the two hearts the reminder should fire for. */
public enum TrackedHearts
{
	BOTH("Imbued and saturated"),
	SATURATED_ONLY("Saturated only");

	private final String displayName;

	TrackedHearts(String displayName)
	{
		this.displayName = displayName;
	}

	/** Whether a cooldown belonging to this heart should be reminded about. */
	public boolean includes(HeartType type)
	{
		// An unidentified heart gets the benefit of the doubt: a reminder that
		// occasionally fires for the wrong heart beats one that silently never
		// fires because the cooldown was already running at login.
		return this == BOTH || type != HeartType.IMBUED;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
