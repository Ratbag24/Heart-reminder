package com.ratbag24.reminders.buff;

import static com.ratbag24.reminders.buff.BuffState.Transition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class BuffStateTest
{
	// Stand-ins for the real varbit ids; only the identity matters here.
	private static final int THRALL = 12413;
	private static final int VENG_COOLDOWN = 2451;
	private static final int VENG_REBOUND = 2450;
	private static final int DIVINE_COMBAT = 13663;
	private static final int DIVINE_ATTACK = 8429;
	private static final int DIVINE_STRENGTH = 8430;

	@Test
	public void aThrallExpiringIsReported()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});

		assertEquals(Transition.ACTIVE, thrall.onVarbitValue(THRALL, 1));
		assertTrue(thrall.isActive());

		assertEquals(Transition.EXPIRED, thrall.onVarbitValue(THRALL, 0));
		assertFalse(thrall.isActive());
	}

	@Test
	public void expiringIsReportedOnlyOnce()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});
		thrall.onVarbitValue(THRALL, 1);

		assertEquals(Transition.EXPIRED, thrall.onVarbitValue(THRALL, 0));
		assertEquals(Transition.NONE, thrall.onVarbitValue(THRALL, 0));
	}

	@Test
	public void reApplyingWhileStillRunningIsNotAnExpiry()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});
		thrall.onVarbitValue(THRALL, 1);

		// A re-cast while the old one is still up must not look like anything
		// ending.
		assertEquals(Transition.NONE, thrall.onVarbitValue(THRALL, 1));
		assertTrue(thrall.isActive());
	}

	@Test
	public void loggingInWithTheBuffAlreadyUpAnnouncesNothing()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{1});

		assertTrue(thrall.isActive());
		assertEquals(Transition.EXPIRED, thrall.onVarbitValue(THRALL, 0));
	}

	@Test
	public void changesBeforeTheFirstSyncAreNotAnnounced()
	{
		BuffState thrall = new BuffState(THRALL);

		// Values arriving before login has settled are recorded silently.
		assertEquals(Transition.NONE, thrall.onVarbitValue(THRALL, 1));
		assertEquals(Transition.NONE, thrall.onVarbitValue(THRALL, 0));
		assertFalse(thrall.isSynced());
	}

	@Test
	public void resettingMakesTheNextSyncAFreshJoin()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});
		thrall.onVarbitValue(THRALL, 1);

		thrall.reset();
		assertFalse(thrall.isSynced());
		assertFalse(thrall.isActive());

		// Re-joining with it still up, then watching it go, is one expiry.
		thrall.sync(new int[]{1});
		assertEquals(Transition.EXPIRED, thrall.onVarbitValue(THRALL, 0));
	}

	@Test
	public void anUnwatchedVarbitIsIgnored()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});

		assertEquals(Transition.NONE, thrall.onVarbitValue(DIVINE_COMBAT, 500));
		assertFalse(thrall.isActive());
	}

	@Test
	public void vengeanceIsReadyOnlyWhenCooldownAndReboundAreBothClear()
	{
		BuffState veng = new BuffState(VENG_COOLDOWN, VENG_REBOUND);
		veng.sync(new int[]{0, 0});

		// Cast it: on cooldown and waiting to rebound.
		assertEquals(Transition.ACTIVE, veng.onVarbitValue(VENG_COOLDOWN, 1));
		assertEquals(Transition.NONE, veng.onVarbitValue(VENG_REBOUND, 1));

		// Cooldown ends but the cast is still sat there waiting: not ready.
		assertEquals(Transition.NONE, veng.onVarbitValue(VENG_COOLDOWN, 0));
		assertTrue(veng.isActive());

		// It rebounds, and now it can be cast again.
		assertEquals(Transition.EXPIRED, veng.onVarbitValue(VENG_REBOUND, 0));
		assertFalse(veng.isActive());
	}

	@Test
	public void vengeanceReboundingBeforeTheCooldownEndsIsNotReady()
	{
		BuffState veng = new BuffState(VENG_COOLDOWN, VENG_REBOUND);
		veng.sync(new int[]{0, 0});
		veng.onVarbitValue(VENG_COOLDOWN, 1);
		veng.onVarbitValue(VENG_REBOUND, 1);

		// Took a hit, so it rebounded early; the cooldown still has time on it.
		assertEquals(Transition.NONE, veng.onVarbitValue(VENG_REBOUND, 0));
		assertTrue(veng.isActive());

		assertEquals(Transition.EXPIRED, veng.onVarbitValue(VENG_COOLDOWN, 0));
	}

	@Test
	public void oneDivinePotionRemindsOnceNotPerStat()
	{
		// A divine super combat potion sets its own timer and the three stat
		// timers. Reminding as each stat timer runs out would fire four times.
		BuffState divine = new BuffState(DIVINE_COMBAT, DIVINE_ATTACK, DIVINE_STRENGTH);
		divine.sync(new int[]{0, 0, 0});

		assertEquals(Transition.ACTIVE, divine.onVarbitValue(DIVINE_COMBAT, 500));
		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_ATTACK, 500));
		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_STRENGTH, 500));

		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_COMBAT, 0));
		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_ATTACK, 0));
		assertEquals(Transition.EXPIRED, divine.onVarbitValue(DIVINE_STRENGTH, 0));
	}

	@Test
	public void toppingUpOneStatKeepsTheBuffAlive()
	{
		BuffState divine = new BuffState(DIVINE_COMBAT, DIVINE_ATTACK);
		divine.sync(new int[]{0, 0});
		divine.onVarbitValue(DIVINE_COMBAT, 100);
		divine.onVarbitValue(DIVINE_ATTACK, 100);

		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_COMBAT, 0));
		// Drinking another before the last timer ran out.
		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_COMBAT, 500));
		assertEquals(Transition.NONE, divine.onVarbitValue(DIVINE_ATTACK, 0));
		assertTrue(divine.isActive());

		assertEquals(Transition.EXPIRED, divine.onVarbitValue(DIVINE_COMBAT, 0));
	}

	@Test
	public void negativeValuesCountAsZero()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});
		thrall.onVarbitValue(THRALL, 1);

		assertEquals(Transition.EXPIRED, thrall.onVarbitValue(THRALL, -1));
		assertFalse(thrall.isActive());
	}

	@Test
	public void aBuffNeverAppliedIsNotWorthAStandingReminder()
	{
		// Not having cast vengeance is the normal state for most players, so
		// there is nothing to nag about until they have cast it once.
		BuffState veng = new BuffState(VENG_COOLDOWN, VENG_REBOUND);
		veng.sync(new int[]{0, 0});

		assertFalse(veng.hasBeenActive());
	}

	@Test
	public void applyingABuffMakesItWorthRemindingAbout()
	{
		BuffState veng = new BuffState(VENG_COOLDOWN, VENG_REBOUND);
		veng.sync(new int[]{0, 0});
		veng.onVarbitValue(VENG_COOLDOWN, 1);

		assertTrue(veng.hasBeenActive());

		veng.onVarbitValue(VENG_COOLDOWN, 0);
		assertFalse(veng.isActive());
		assertTrue(veng.hasBeenActive());
	}

	@Test
	public void aBuffAlreadyUpAtLoginCountsAsApplied()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{1});

		assertTrue(thrall.hasBeenActive());
	}

	@Test
	public void loggingOutForgetsThatTheBuffWasEverUp()
	{
		BuffState thrall = new BuffState(THRALL);
		thrall.sync(new int[]{0});
		thrall.onVarbitValue(THRALL, 1);
		thrall.onVarbitValue(THRALL, 0);
		assertTrue(thrall.hasBeenActive());

		thrall.reset();
		assertFalse(thrall.hasBeenActive());
	}

	@Test(expected = IllegalArgumentException.class)
	public void aBuffMustWatchAtLeastOneVarbit()
	{
		new BuffState();
	}

	@Test(expected = IllegalArgumentException.class)
	public void syncingTheWrongNumberOfValuesIsRejected()
	{
		new BuffState(THRALL).sync(new int[]{0, 0});
	}
}
