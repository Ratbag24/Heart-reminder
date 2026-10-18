package com.ratbag24.reminders.buff;

/**
 * Everything that distinguishes one buff reminder from another: what it is
 * called, what to say about it, and which varbits report it.
 * <p>
 * Keeping this as data rather than a class per buff is what makes adding the
 * next reminder a constant in {@link Buffs}, a section in the config and one
 * line of registration.
 */
public final class BuffDefinition
{
	private final String notificationMessage;
	private final String alertText;
	private final int[] varbits;

	/**
	 * @param notificationMessage the one-off message sent as it runs out
	 * @param alertText           the banner's wording, phrased as the thing to
	 *                            do rather than the state you are in
	 * @param varbits             every varbit that is non-zero while any part
	 *                            of the buff is still running
	 */
	public BuffDefinition(String notificationMessage, String alertText, int... varbits)
	{
		this.notificationMessage = notificationMessage;
		this.alertText = alertText;
		this.varbits = varbits.clone();
	}

	public String getNotificationMessage()
	{
		return notificationMessage;
	}

	public String getAlertText()
	{
		return alertText;
	}

	public int[] getVarbits()
	{
		return varbits.clone();
	}
}
