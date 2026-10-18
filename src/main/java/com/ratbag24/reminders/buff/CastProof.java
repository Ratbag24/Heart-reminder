package com.ratbag24.reminders.buff;

/**
 * Whether the player was the one who put a buff up, rather than someone else.
 * <p>
 * A buff varbit only says the buff is on you; it does not say whose doing it
 * was. Vengeance Other is the clearest case: a teammate casting it sets your
 * vengeance varbit while you are on a spellbook you could never cast it from,
 * and the reminder that follows asks for something you cannot do.
 * <p>
 * What distinguishes the two is the cooldown. Casting a spell starts its
 * cooldown on <em>the caster</em>, so seeing your own cooldown begin is proof
 * the cast was yours. Receiving the buff from someone else never starts it.
 * <p>
 * The proof latches for the session: having cast a spell once is enough to want
 * reminding about it, and a cooldown that has since run down does not undo
 * that. Logging out forgets it, because the next login may be a different
 * character.
 */
public final class CastProof
{
	/** For a buff the game gives no cooldown varbit, so nothing can be proven. */
	public static final int NO_COOLDOWN = -1;

	private final int cooldownVarbit;
	private boolean proven;

	public CastProof(int cooldownVarbit)
	{
		this.cooldownVarbit = cooldownVarbit;
	}

	/**
	 * Feeds in a varbit value, from a change or from the seeding done at login.
	 * Anything other than this buff's cooldown is ignored.
	 */
	public void onVarbitValue(int varbitId, int value)
	{
		if (cooldownVarbit != NO_COOLDOWN && varbitId == cooldownVarbit && value > 0)
		{
			proven = true;
		}
	}

	/**
	 * Whether the buff can be treated as the player's own.
	 * <p>
	 * A buff with no cooldown to go on is taken at face value: these are
	 * potions and items, and self-cast spells the game tracks no cooldown for,
	 * none of which anyone else can apply to you.
	 */
	public boolean isProven()
	{
		return cooldownVarbit == NO_COOLDOWN || proven;
	}

	/** Whether this buff has a cooldown that could prove anything at all. */
	public boolean isProvable()
	{
		return cooldownVarbit != NO_COOLDOWN;
	}

	public void reset()
	{
		proven = false;
	}
}
