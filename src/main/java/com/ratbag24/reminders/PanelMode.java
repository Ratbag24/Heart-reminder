package com.ratbag24.reminders;

/** How much the status panel shows. */
public enum PanelMode
{
	/** Every buff you are using, up or down. */
	ALL("Everything you're using"),
	/** Only the ones that need re-applying. */
	ONLY_WHEN_DOWN("Only what's down"),
	/** Nothing; notifications and the screen flash still work. */
	OFF("Off");

	private final String displayName;

	PanelMode(String displayName)
	{
		this.displayName = displayName;
	}

	@Override
	public String toString()
	{
		return displayName;
	}
}
