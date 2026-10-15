package com.ratbag24.reminders.chat;

/**
 * Tracks a buff the game never reports a state for, only announces in chat.
 * <p>
 * Mark of Darkness and Ward of Arceuus have no varbit saying whether they are
 * up. All the game gives is a line of chat when you cast one, so the only way
 * to know is to start a clock on that line and run it for as long as the spell
 * is known to last. RuneLite's own timers for these two work exactly this way.
 * <p>
 * That makes this less certain than a varbit, in two ways worth being honest
 * about:
 * <ul>
 *     <li>the duration is a rule about the spell rather than something the game
 *     confirms, so a change to the spell would silently make it wrong;</li>
 *     <li>logging in with one already running is invisible — there was no chat
 *     line to see — so tracking only begins at the next cast.</li>
 * </ul>
 * Neither can be fixed from outside the game. What can be avoided is claiming
 * to know more than we do, so an untracked buff stays silent rather than
 * guessing that it has expired.
 * <p>
 * Counted in game ticks rather than wall clock: the duration is given in ticks,
 * and ticks do not run while the client is logged out.
 */
public final class TimedBuffState
{
	/** No cast has been seen, so there is no clock running. */
	private static final int NOT_RUNNING = Integer.MIN_VALUE;

	public enum Transition
	{
		/** Nothing worth acting on. */
		NONE,
		/** The clock just ran out: the buff is gone. */
		EXPIRED
	}

	private int expiryTick = NOT_RUNNING;
	private boolean seenActive;

	/**
	 * Starts the clock, as of a cast seen in chat.
	 *
	 * @param nowTick        the client's current tick count
	 * @param durationTicks  how long the spell lasts
	 */
	public void start(int nowTick, int durationTicks)
	{
		if (durationTicks <= 0)
		{
			// A duration of nothing is not a cast worth tracking.
			return;
		}

		expiryTick = nowTick + durationTicks;
		seenActive = true;
	}

	/**
	 * Advances the clock.
	 *
	 * @return {@link Transition#EXPIRED} on the one tick the buff runs out
	 */
	public Transition onTick(int nowTick)
	{
		if (expiryTick == NOT_RUNNING || nowTick - expiryTick < 0)
		{
			return Transition.NONE;
		}

		// Cleared so the expiry is reported once, not on every tick after.
		expiryTick = NOT_RUNNING;
		return Transition.EXPIRED;
	}

	/** Whether the buff is believed to still be running. */
	public boolean isActive()
	{
		return expiryTick != NOT_RUNNING;
	}

	/**
	 * Whether a cast has been seen at all since logging in. Nothing is said
	 * about a spell the player has not shown any interest in.
	 */
	public boolean hasBeenActive()
	{
		return seenActive;
	}

	/**
	 * Forgets the clock. Called on login and on hopping: a cast from before is
	 * not something this can still time, and pretending otherwise would expire
	 * a buff that may well still be up.
	 */
	public void reset()
	{
		expiryTick = NOT_RUNNING;
		seenActive = false;
	}
}
