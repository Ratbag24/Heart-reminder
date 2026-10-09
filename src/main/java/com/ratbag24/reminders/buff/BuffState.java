package com.ratbag24.reminders.buff;

import java.util.Arrays;

/**
 * Tracks a buff that the game reports through one or more varbits, each
 * non-zero for as long as some part of the buff is still running.
 * <p>
 * One shape covers every buff the plugin reminds about besides the heart:
 * <ul>
 *     <li>a thrall, whose single varbit drops to zero when it expires;</li>
 *     <li>vengeance, which is ready to cast once both its cooldown and its
 *     cast-and-waiting varbit are zero;</li>
 *     <li>divine potions, where one potion sets several varbits at once, so the
 *     boost is only really gone when the last of them reaches zero.</li>
 * </ul>
 * Waiting for <em>all</em> of them is what keeps a divine super combat potion
 * from reminding four times as its separate stat timers run out.
 * <p>
 * Like {@link com.ratbag24.reminders.heart.HeartCooldown} this has no RuneLite
 * dependencies, so the rules can be unit tested on their own.
 */
public final class BuffState
{
	/** What a varbit change meant relative to the state before it. */
	public enum Transition
	{
		/** Nothing worth acting on, or a varbit this buff does not watch. */
		NONE,
		/** The buff started, or was re-applied while still running. */
		ACTIVE,
		/** The last of the watched varbits reached zero: the buff is gone. */
		EXPIRED
	}

	private final int[] varbits;
	private final int[] values;
	private boolean synced;
	private boolean seenActive;

	public BuffState(int... varbits)
	{
		if (varbits.length == 0)
		{
			throw new IllegalArgumentException("a buff must watch at least one varbit");
		}

		this.varbits = varbits.clone();
		this.values = new int[varbits.length];
	}

	public int[] getVarbits()
	{
		return varbits.clone();
	}

	/**
	 * Adopts the current value of every watched varbit without reporting a
	 * transition. Called on the first tick after logging in: a buff that was
	 * already running, or already gone, is the state we join, not a change we
	 * should announce.
	 *
	 * @param currentValues one value per varbit, in the order given to the
	 *                      constructor
	 */
	public void sync(int[] currentValues)
	{
		if (currentValues.length != values.length)
		{
			throw new IllegalArgumentException(
				"expected " + values.length + " values, got " + currentValues.length);
		}

		for (int i = 0; i < values.length; i++)
		{
			values[i] = Math.max(0, currentValues[i]);
		}

		synced = true;
		seenActive |= isActive();
	}

	/**
	 * Feeds in a varbit change.
	 *
	 * @param varbitId the varbit that changed, watched or not
	 * @param value    its new value
	 * @return what the change meant
	 */
	public Transition onVarbitValue(int varbitId, int value)
	{
		final int index = indexOf(varbitId);

		if (index < 0)
		{
			return Transition.NONE;
		}

		if (!synced)
		{
			// Values are seeded together on the first tick after login, so a
			// change arriving before that would be read against zeroes we have
			// not confirmed. Record it and say nothing.
			values[index] = Math.max(0, value);
			return Transition.NONE;
		}

		final boolean wasActive = isActive();
		values[index] = Math.max(0, value);
		final boolean nowActive = isActive();

		if (nowActive)
		{
			seenActive = true;
			return wasActive ? Transition.NONE : Transition.ACTIVE;
		}

		return wasActive ? Transition.EXPIRED : Transition.NONE;
	}

	/** Forgets everything, so the next sync is treated as a fresh join. */
	public void reset()
	{
		synced = false;
		seenActive = false;
		Arrays.fill(values, 0);
	}

	public boolean isSynced()
	{
		return synced;
	}

	/**
	 * Whether the buff has been up at all since logging in.
	 * <p>
	 * For most buffs, not having one is the normal state of a player who never
	 * uses it: vengeance is uncast for everyone who does not cast vengeance. A
	 * standing reminder is only worth showing to someone who has shown they
	 * want the buff by applying it at least once.
	 */
	public boolean hasBeenActive()
	{
		return seenActive;
	}

	/** Whether any part of the buff is still running. */
	public boolean isActive()
	{
		for (int value : values)
		{
			if (value > 0)
			{
				return true;
			}
		}

		return false;
	}

	private int indexOf(int varbitId)
	{
		for (int i = 0; i < varbits.length; i++)
		{
			if (varbits[i] == varbitId)
			{
				return i;
			}
		}

		return -1;
	}
}
