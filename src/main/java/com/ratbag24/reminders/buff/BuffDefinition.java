package com.ratbag24.reminders.buff;

import java.util.Arrays;

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
	private final int cooldownVarbit;
	private final int[] varbits;

	/**
	 * @param notificationMessage the one-off message sent as it runs out
	 * @param alertText           the banner's wording, phrased as the thing to
	 *                            do rather than the state you are in
	 * @param cooldownVarbit      the varbit the game runs down after a cast,
	 *                            which is what proves the cast was the player's
	 *                            own; {@link CastProof#NO_COOLDOWN} where the
	 *                            game keeps no such varbit. It is watched along
	 *                            with the rest, since a buff is not finished
	 *                            with while its spell is still unavailable.
	 * @param varbits             every other varbit that is non-zero while any
	 *                            part of the buff is still running
	 */
	public BuffDefinition(String notificationMessage, String alertText, int cooldownVarbit,
		int... varbits)
	{
		this.notificationMessage = notificationMessage;
		this.alertText = alertText;
		this.cooldownVarbit = cooldownVarbit;

		if (cooldownVarbit == CastProof.NO_COOLDOWN)
		{
			this.varbits = varbits.clone();
		}
		else
		{
			this.varbits = Arrays.copyOf(varbits, varbits.length + 1);
			this.varbits[varbits.length] = cooldownVarbit;
		}
	}

	public String getNotificationMessage()
	{
		return notificationMessage;
	}

	public String getAlertText()
	{
		return alertText;
	}

	/** The cooldown varbit, or {@link CastProof#NO_COOLDOWN} if there is none. */
	public int getCooldownVarbit()
	{
		return cooldownVarbit;
	}

	public int[] getVarbits()
	{
		return varbits.clone();
	}
}
