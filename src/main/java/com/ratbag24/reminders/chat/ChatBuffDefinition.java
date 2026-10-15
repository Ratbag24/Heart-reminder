package com.ratbag24.reminders.chat;

/**
 * A buff the game only announces in chat, with no varbit to read back.
 *
 * @see TimedBuffState for why these are tracked differently, and less surely,
 * than everything in the buff package
 */
public final class ChatBuffDefinition
{
	private final String castMessage;
	private final String notificationMessage;
	private final String alertText;

	/**
	 * @param castMessage          the tail of the chat line the game prints on
	 *                             casting it, colour tag and all
	 * @param notificationMessage  the one-off message sent as it runs out
	 * @param alertText            the banner's wording
	 */
	public ChatBuffDefinition(String castMessage, String notificationMessage, String alertText)
	{
		this.castMessage = castMessage;
		this.notificationMessage = notificationMessage;
		this.alertText = alertText;
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
}
