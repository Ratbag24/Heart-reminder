package com.ratbag24.reminders.chat;

/**
 * A buff the game only announces in chat, with no varbit to read back.
 *
 * @see TimedBuffState for why these are tracked differently, and less surely,
 * than everything in the buff package
 */
public final class ChatBuffDefinition
{
	/** Stands for "this spell has no cooldown varbit to check". */
	public static final int NO_COOLDOWN_VARBIT = -1;

	private final String castMessage;
	private final String notificationMessage;
	private final String alertText;
	private final int cooldownVarbit;

	/**
	 * @param castMessage          the tail of the chat line the game prints on
	 *                             casting it, colour tag and all
	 * @param notificationMessage  the one-off message sent as it runs out
	 * @param alertText            the banner's wording
	 * @param cooldownVarbit       non-zero while the spell cannot be cast, or
	 *                             {@link #NO_COOLDOWN_VARBIT} when the game
	 *                             gives no such varbit
	 */
	public ChatBuffDefinition(String castMessage, String notificationMessage, String alertText,
		int cooldownVarbit)
	{
		this.castMessage = castMessage;
		this.notificationMessage = notificationMessage;
		this.alertText = alertText;
		this.cooldownVarbit = cooldownVarbit;
	}

	public String getCastMessage()
	{
		return castMessage;
	}

	public String getNotificationMessage()
	{
		return notificationMessage;
	}

	public String getAlertText()
	{
		return alertText;
	}

	public int getCooldownVarbit()
	{
		return cooldownVarbit;
	}

	public boolean hasCooldownVarbit()
	{
		return cooldownVarbit != NO_COOLDOWN_VARBIT;
	}
}
