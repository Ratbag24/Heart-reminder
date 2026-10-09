package com.ratbag24.reminders.heart;

/**
 * Tracks the shared imbued/saturated heart cooldown from its varbit.
 * <p>
 * The varbit counts down in steps of ten game ticks, so it only gives us the
 * remaining time to the nearest six seconds. The value is therefore used for
 * two different jobs: the step down to zero is the authoritative "the heart has
 * recharged" event, while a wall clock estimate, re-synchronised on every step,
 * provides a display countdown that moves once a second instead of in six
 * second jumps.
 * <p>
 * This class deliberately has no RuneLite dependencies so that the timing rules
 * can be unit tested on their own.
 */
public final class HeartCooldown
{
	/** The cooldown varbit decrements once every ten game ticks. */
	public static final int TICKS_PER_UNIT = 10;
	public static final int MILLIS_PER_TICK = 600;
	public static final int MILLIS_PER_UNIT = TICKS_PER_UNIT * MILLIS_PER_TICK;

	/** The saturated heart's five minute cooldown, in varbit units. */
	public static final int SATURATED_UNITS = 50;
	/** The imbued heart's seven minute cooldown, in varbit units. */
	public static final int IMBUED_UNITS = 70;

	/**
	 * A fresh cooldown can be read a step or two after the game set it, so the
	 * starting value is matched to the nearest of the two known lengths rather
	 * than exactly. Anything this far below the shorter of the two is too short
	 * to have come from either heart.
	 */
	private static final int CLASSIFY_SLACK = 5;

	/** What a varbit value meant relative to the one before it. */
	public enum Transition
	{
		/** The value did not say anything worth acting on. */
		NONE,
		/** First value seen since logging in; the state was adopted, not entered. */
		SYNCED,
		/** A heart was just invigorated, or re-invigorated early. */
		STARTED,
		/** The cooldown ran out: the heart can be used again. */
		READY
	}

	private boolean synced;
	private int units;
	private HeartType type = HeartType.UNKNOWN;
	private long readyAtMillis;

	/**
	 * Feeds a new value of the cooldown varbit in.
	 *
	 * @param value      the raw varbit value, in units of ten game ticks
	 * @param nowMillis  the current wall clock time
	 * @return what the change meant
	 */
	public Transition onVarbitValue(int value, long nowMillis)
	{
		final int previous = units;
		units = Math.max(0, value);

		if (!synced)
		{
			// The first value after a login tells us the state the account was
			// already in. Announcing a heart as ready here would fire on every
			// login, so the value is only adopted.
			synced = true;
			type = units > 0 ? classifyPartial(units) : HeartType.UNKNOWN;
			readyAtMillis = units > 0 ? nowMillis + (long) units * MILLIS_PER_UNIT : 0L;
			return Transition.SYNCED;
		}

		if (units > 0)
		{
			readyAtMillis = nowMillis + (long) units * MILLIS_PER_UNIT;

			// Going up can only mean a heart was just used: either the cooldown
			// was at zero, or it was refreshed before it ran out.
			if (units > previous)
			{
				type = classifyFresh(units);
				return Transition.STARTED;
			}

			return Transition.NONE;
		}

		readyAtMillis = 0L;
		return previous > 0 ? Transition.READY : Transition.NONE;
	}

	/**
	 * Forgets everything known about the cooldown. Called when the player logs
	 * out or hops, so that the next value read is treated as a fresh sync
	 * rather than as a change.
	 */
	public void reset()
	{
		synced = false;
		units = 0;
		type = HeartType.UNKNOWN;
		readyAtMillis = 0L;
	}

	/**
	 * Narrows an unknown heart down to the saturated one. The game keeps its own
	 * varbit for the saturated heart's boost, and that only ever moves for a
	 * saturated heart, so it can identify a cooldown we joined partway through.
	 */
	public void markSaturated()
	{
		if (type == HeartType.UNKNOWN)
		{
			type = HeartType.SATURATED;
		}
	}

	public boolean isOnCooldown()
	{
		return units > 0;
	}

	public boolean isSynced()
	{
		return synced;
	}

	public int getUnits()
	{
		return units;
	}

	public HeartType getType()
	{
		return type;
	}

	/**
	 * How long until the heart recharges, for display. Held at one second while
	 * the cooldown is still running so that a countdown never reads as finished
	 * before the varbit says it is.
	 */
	public long getRemainingMillis(long nowMillis)
	{
		if (units <= 0)
		{
			return 0L;
		}

		final long remaining = readyAtMillis - nowMillis;
		final long cap = (long) units * MILLIS_PER_UNIT;
		return Math.min(cap, Math.max(1000L, remaining));
	}

	/** Matches a just-started cooldown to whichever heart's length is closer. */
	private static HeartType classifyFresh(int value)
	{
		if (value < SATURATED_UNITS - CLASSIFY_SLACK)
		{
			// Too short to be either heart's full cooldown.
			return HeartType.UNKNOWN;
		}

		final int toSaturated = Math.abs(value - SATURATED_UNITS);
		final int toImbued = Math.abs(value - IMBUED_UNITS);
		return toImbued < toSaturated ? HeartType.IMBUED : HeartType.SATURATED;
	}

	/**
	 * Identifies a cooldown that was already running. Only a value longer than
	 * the saturated heart's entire cooldown rules that heart out.
	 */
	private static HeartType classifyPartial(int value)
	{
		return value > SATURATED_UNITS ? HeartType.IMBUED : HeartType.UNKNOWN;
	}
}
