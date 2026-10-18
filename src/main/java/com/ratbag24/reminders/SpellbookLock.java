package com.ratbag24.reminders;

/**
 * Ties a spell's reminder to the spellbook it was cast from, so switching books
 * for other content quietens it without anyone touching a setting.
 * <p>
 * The game reports the current spellbook in a varbit, but what its values mean
 * is game data rather than anything fixed: RuneLite reads the varbit as an
 * index into one of the game's own enums rather than hardcoding "three means
 * Arceuus". Hardcoding it here would be a guess, and a wrong guess would
 * silently suppress every Arceuus reminder for good — a failure with no symptom
 * beyond nothing ever happening.
 * <p>
 * So nothing is assumed. A thrall can only be summoned from Arceuus, so the
 * spellbook you are on when one appears <em>is</em> Arceuus, whatever number
 * the game gives it. That value is recorded the first time the buff is applied
 * and compared from then on. It cannot be wrong, because it was observed rather
 * than assumed.
 * <p>
 * This fits what the reminders already do: nothing is said about a buff that
 * has not been applied since logging in, so the one moment the value is needed
 * is always after the moment it is learned.
 */
public final class SpellbookLock
{
	private static final int UNKNOWN = Integer.MIN_VALUE;

	private int learned = UNKNOWN;

	/**
	 * Records the spellbook a buff was just applied from. Later casts overwrite
	 * it, which matters for a spell that moves between books in an update.
	 */
	public void learn(int spellbook)
	{
		learned = spellbook;
	}

	/**
	 * Whether a reminder should be allowed to speak on the current spellbook.
	 * Until a cast has been seen there is nothing to compare against, so the
	 * answer is yes: this gates reminders, it does not invent them.
	 */
	public boolean allows(int currentSpellbook)
	{
		return learned == UNKNOWN || learned == currentSpellbook;
	}

	public boolean isLearned()
	{
		return learned != UNKNOWN;
	}

	/** Forgets it, on login or a world hop, along with everything else. */
	public void reset()
	{
		learned = UNKNOWN;
	}
}
