package com.ratbag24.reminders.chat;

/**
 * The two Arceuus spells the game gives no varbit for.
 * <p>
 * The message tails are the ones RuneLite's own timers match on, trailing
 * colour tag included, because that is what the game actually prints.
 */
public final class ChatBuffs
{
	public static final ChatBuffDefinition MARK_OF_DARKNESS = new ChatBuffDefinition(
		"You have placed a Mark of Darkness upon yourself.</col>",
		"Your Mark of Darkness has expired.",
		"You need mark of darkness!");

	public static final ChatBuffDefinition WARD_OF_ARCEUUS = new ChatBuffDefinition(
		">Your defence against Arceuus magic has been strengthened.</col>",
		"Your Ward of Arceuus has expired.",
		"You need ward of Arceuus!");

	private ChatBuffs()
	{
	}
}
