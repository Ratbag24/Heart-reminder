package com.ratbag24.reminders.chat;

import net.runelite.api.gameval.VarbitID;

/**
 * The two Arceuus spells the game gives no varbit for.
 * <p>
 * The message tails are the ones RuneLite's own timers match on, trailing
 * colour tag included, because that is what the game actually prints.
 */
public final class ChatBuffs
{
	/**
	 * The game gives this one no cooldown varbit either, so unlike the ward
	 * there is nothing to check before asking for it again.
	 */
	public static final ChatBuffDefinition MARK_OF_DARKNESS = new ChatBuffDefinition(
		"You have placed a Mark of Darkness upon yourself.</col>",
		"Your Mark of Darkness has expired.",
		"You need mark of darkness!",
		ChatBuffDefinition.NO_COOLDOWN_VARBIT);

	/**
	 * Timed from chat like the mark, but it does have a cooldown varbit, so the
	 * reminder can at least avoid asking for a ward that cannot be cast yet.
	 */
	public static final ChatBuffDefinition WARD_OF_ARCEUUS = new ChatBuffDefinition(
		">Your defence against Arceuus magic has been strengthened.</col>",
		"Your Ward of Arceuus has expired.",
		"You need ward of Arceuus!",
		VarbitID.ARCEUUS_WARD_COOLDOWN);

	private ChatBuffs()
	{
	}
}
