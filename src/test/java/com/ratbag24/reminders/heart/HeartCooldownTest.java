package com.ratbag24.reminders.heart;

import static com.ratbag24.reminders.heart.HeartCooldown.IMBUED_UNITS;
import static com.ratbag24.reminders.heart.HeartCooldown.MILLIS_PER_UNIT;
import static com.ratbag24.reminders.heart.HeartCooldown.SATURATED_UNITS;
import static com.ratbag24.reminders.heart.HeartCooldown.Transition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class HeartCooldownTest
{
	private HeartCooldown cooldown;

	@Before
	public void setUp()
	{
		cooldown = new HeartCooldown();
	}

	@Test
	public void saturatedHeartCooldownIsFiveMinutes()
	{
		assertEquals(300_000L, (long) SATURATED_UNITS * MILLIS_PER_UNIT);
	}

	@Test
	public void imbuedHeartCooldownIsSevenMinutes()
	{
		assertEquals(420_000L, (long) IMBUED_UNITS * MILLIS_PER_UNIT);
	}

	@Test
	public void firstValueAfterLoginIsOnlyAdopted()
	{
		// Logging in mid-cooldown must not look like a heart being used.
		assertEquals(Transition.SYNCED, cooldown.onVarbitValue(23, 0L));
		assertTrue(cooldown.isOnCooldown());
	}

	@Test
	public void readyAtLoginDoesNotAnnounceAnything()
	{
		assertEquals(Transition.SYNCED, cooldown.onVarbitValue(0, 0L));
		assertEquals(Transition.NONE, cooldown.onVarbitValue(0, 6_000L));
		assertFalse(cooldown.isOnCooldown());
	}

	@Test
	public void usingASaturatedHeartStartsAFiveMinuteCooldown()
	{
		cooldown.onVarbitValue(0, 0L);
		assertEquals(Transition.STARTED, cooldown.onVarbitValue(SATURATED_UNITS, 1_000L));
		assertEquals(HeartType.SATURATED, cooldown.getType());
		assertEquals(300_000L, cooldown.getRemainingMillis(1_000L));
	}

	@Test
	public void usingAnImbuedHeartStartsASevenMinuteCooldown()
	{
		cooldown.onVarbitValue(0, 0L);
		assertEquals(Transition.STARTED, cooldown.onVarbitValue(IMBUED_UNITS, 1_000L));
		assertEquals(HeartType.IMBUED, cooldown.getType());
		assertEquals(420_000L, cooldown.getRemainingMillis(1_000L));
	}

	@Test
	public void aStartingValueReadLateStillIdentifiesTheHeart()
	{
		// The varbit can be read a step or two after the game set it.
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS - 2, 1_000L);
		assertEquals(HeartType.SATURATED, cooldown.getType());

		HeartCooldown other = new HeartCooldown();
		other.onVarbitValue(0, 0L);
		other.onVarbitValue(IMBUED_UNITS - 2, 1_000L);
		assertEquals(HeartType.IMBUED, other.getType());
	}

	@Test
	public void countingDownIsNotAnEvent()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);

		long now = 0L;
		for (int units = SATURATED_UNITS - 1; units > 0; units--)
		{
			now += MILLIS_PER_UNIT;
			assertEquals("step to " + units, Transition.NONE, cooldown.onVarbitValue(units, now));
		}

		assertTrue(cooldown.isOnCooldown());
	}

	@Test
	public void reachingZeroMeansTheHeartIsReady()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		cooldown.onVarbitValue(1, 294_000L);

		assertEquals(Transition.READY, cooldown.onVarbitValue(0, 300_000L));
		assertFalse(cooldown.isOnCooldown());
		assertEquals(0L, cooldown.getRemainingMillis(300_000L));
	}

	@Test
	public void readyIsAnnouncedOnlyOnce()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		assertEquals(Transition.READY, cooldown.onVarbitValue(0, 300_000L));
		assertEquals(Transition.NONE, cooldown.onVarbitValue(0, 306_000L));
	}

	@Test
	public void dyingResetsTheCooldownAndCountsAsReady()
	{
		// The cooldown is cleared on death, which genuinely does make the heart
		// usable again, so it should be announced like any other recharge.
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		assertEquals(Transition.READY, cooldown.onVarbitValue(0, 120_000L));
	}

	@Test
	public void usingAHeartEarlyRestartsTheCooldown()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		cooldown.onVarbitValue(10, 240_000L);

		assertEquals(Transition.STARTED, cooldown.onVarbitValue(SATURATED_UNITS, 300_000L));
		assertEquals(300_000L, cooldown.getRemainingMillis(300_000L));
	}

	@Test
	public void loggingBackInDoesNotReAnnounceAReadyHeart()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);

		cooldown.reset();

		assertFalse(cooldown.isSynced());
		assertEquals(Transition.SYNCED, cooldown.onVarbitValue(0, 500_000L));
		assertEquals(Transition.NONE, cooldown.onVarbitValue(0, 506_000L));
	}

	@Test
	public void displayCountdownRunsSmoothlyBetweenVarbitSteps()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);

		// No varbit change for three seconds: the estimate should have moved.
		assertEquals(297_000L, cooldown.getRemainingMillis(3_000L));
		assertEquals(294_000L, cooldown.getRemainingMillis(6_000L));
	}

	@Test
	public void displayCountdownNeverOutlivesTheVarbit()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);

		// Far past the estimate, but the varbit still says one step remains.
		cooldown.onVarbitValue(1, 294_000L);
		assertEquals(1_000L, cooldown.getRemainingMillis(400_000L));
		assertTrue(cooldown.isOnCooldown());
	}

	@Test
	public void displayCountdownIsCappedByTheVarbit()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 10_000L);

		// A clock that jumped backwards must not inflate the countdown.
		assertEquals(300_000L, cooldown.getRemainingMillis(0L));
	}

	@Test
	public void aPartialCooldownLongerThanFiveMinutesMustBeAnImbuedHeart()
	{
		cooldown.onVarbitValue(SATURATED_UNITS + 1, 0L);
		assertEquals(HeartType.IMBUED, cooldown.getType());
	}

	@Test
	public void aShortPartialCooldownCouldBeEitherHeart()
	{
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		assertEquals(HeartType.UNKNOWN, cooldown.getType());
	}

	@Test
	public void theSaturatedVarbitNarrowsDownAnUnknownHeart()
	{
		cooldown.onVarbitValue(30, 0L);
		assertEquals(HeartType.UNKNOWN, cooldown.getType());

		cooldown.markSaturated();
		assertEquals(HeartType.SATURATED, cooldown.getType());
	}

	@Test
	public void theSaturatedVarbitDoesNotOverrideAKnownImbuedHeart()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(IMBUED_UNITS, 0L);

		cooldown.markSaturated();
		assertEquals(HeartType.IMBUED, cooldown.getType());
	}

	@Test
	public void negativeValuesAreTreatedAsReady()
	{
		cooldown.onVarbitValue(0, 0L);
		cooldown.onVarbitValue(SATURATED_UNITS, 0L);
		assertEquals(Transition.READY, cooldown.onVarbitValue(-1, 300_000L));
		assertEquals(0, cooldown.getUnits());
	}
}
