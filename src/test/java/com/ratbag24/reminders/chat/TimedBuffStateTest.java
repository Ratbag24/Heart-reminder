package com.ratbag24.reminders.chat;

import static com.ratbag24.reminders.chat.TimedBuffState.Transition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class TimedBuffStateTest
{
	/** A plausible Magic level, which is what sets the duration in ticks. */
	private static final int DURATION = 85;

	private TimedBuffState state;

	@Before
	public void setUp()
	{
		state = new TimedBuffState();
	}

	@Test
	public void nothingIsTrackedUntilACastIsSeen()
	{
		assertFalse(state.isActive());
		assertFalse(state.hasBeenActive());
		assertEquals(Transition.NONE, state.onTick(1000));
	}

	@Test
	public void castingStartsTheClock()
	{
		state.start(1000, DURATION);

		assertTrue(state.isActive());
		assertTrue(state.hasBeenActive());
	}

	@Test
	public void itRunsForTheWholeDuration()
	{
		state.start(1000, DURATION);

		for (int tick = 1001; tick < 1000 + DURATION; tick++)
		{
			assertEquals("expired early at " + tick, Transition.NONE, state.onTick(tick));
		}

		assertTrue(state.isActive());
	}

	@Test
	public void itExpiresOnTheTickItRunsOut()
	{
		state.start(1000, DURATION);

		assertEquals(Transition.EXPIRED, state.onTick(1000 + DURATION));
		assertFalse(state.isActive());
	}

	@Test
	public void expiryIsReportedOnlyOnce()
	{
		state.start(1000, DURATION);
		assertEquals(Transition.EXPIRED, state.onTick(1000 + DURATION));

		assertEquals(Transition.NONE, state.onTick(1000 + DURATION + 1));
		assertEquals(Transition.NONE, state.onTick(1000 + DURATION + 50));
	}

	@Test
	public void aTickWellPastTheEndStillExpiresRatherThanBeingMissed()
	{
		// Ticks can be missed while the client is busy; the clock must not run
		// on forever just because the exact tick went by unseen.
		state.start(1000, DURATION);

		assertEquals(Transition.EXPIRED, state.onTick(1000 + DURATION + 500));
	}

	@Test
	public void reCastingBeforeItEndsRestartsTheClock()
	{
		state.start(1000, DURATION);
		state.onTick(1050);

		state.start(1060, DURATION);

		assertEquals(Transition.NONE, state.onTick(1060 + DURATION - 1));
		assertEquals(Transition.EXPIRED, state.onTick(1060 + DURATION));
	}

	@Test
	public void itStaysSilentAboutASpellTheCastWasNeverSeenFor()
	{
		// Logging in with one already running: no chat line, so no clock, and
		// nothing is claimed about it either way.
		state.reset();

		assertFalse(state.hasBeenActive());
		assertEquals(Transition.NONE, state.onTick(5000));
	}

	@Test
	public void loggingOutForgetsTheClockRatherThanExpiringIt()
	{
		state.start(1000, DURATION);
		state.reset();

		assertFalse(state.isActive());
		assertFalse(state.hasBeenActive());
		// The old cast must not expire into a reminder after a hop.
		assertEquals(Transition.NONE, state.onTick(1000 + DURATION));
	}

	@Test
	public void aZeroOrNegativeDurationIsNotACast()
	{
		state.start(1000, 0);
		assertFalse(state.isActive());
		assertFalse(state.hasBeenActive());

		state.start(1000, -5);
		assertFalse(state.isActive());
	}

	@Test
	public void theClockSurvivesTheTickCounterWrappingAround()
	{
		// getTickCount() is an int and the comparison has to cope with it
		// going past its maximum rather than reading as "already expired".
		final int nearMax = Integer.MAX_VALUE - 10;
		state.start(nearMax, DURATION);

		assertEquals(Transition.NONE, state.onTick(nearMax + 1));
		assertEquals(Transition.EXPIRED, state.onTick(nearMax + DURATION));
	}
}
