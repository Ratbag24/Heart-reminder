package com.ratbag24.reminders.buff;

import static com.ratbag24.reminders.buff.BuffState.Transition;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CastProofTest
{
	// The real ids, though only their identity matters here.
	private static final int VENG_COOLDOWN = 2451;
	private static final int VENG_REBOUND = 2450;
	private static final int STAMINA = 25;

	@Test
	public void aBuffWithNoCooldownIsTakenAtFaceValue()
	{
		// Potions, and the spells the game keeps no cooldown for. Nobody else
		// can put these on you, so there is nothing to tell apart.
		CastProof stamina = new CastProof(CastProof.NO_COOLDOWN);

		assertFalse(stamina.isProvable());
		assertTrue(stamina.isProven());
	}

	@Test
	public void aCooldownBuffStartsUnproven()
	{
		CastProof veng = new CastProof(VENG_COOLDOWN);

		assertTrue(veng.isProvable());
		assertFalse(veng.isProven());
	}

	@Test
	public void yourOwnCooldownStartingProvesIt()
	{
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_COOLDOWN, 1);

		assertTrue(veng.isProven());
	}

	@Test
	public void theBuffVarbitAloneProvesNothing()
	{
		// This is the whole point: being vengeance-marked is not evidence that
		// you were the one who cast it.
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_REBOUND, 1);

		assertFalse(veng.isProven());
	}

	@Test
	public void aCooldownReadingZeroProvesNothing()
	{
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_COOLDOWN, 0);

		assertFalse(veng.isProven());
	}

	@Test
	public void anUnrelatedVarbitIsIgnored()
	{
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(STAMINA, 1);

		assertFalse(veng.isProven());
	}

	@Test
	public void theProofOutlastsTheCooldownItCameFrom()
	{
		// Having cast the spell once is what makes the reminder wanted; the
		// cooldown running down again does not take that back.
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_COOLDOWN, 1);
		veng.onVarbitValue(VENG_COOLDOWN, 0);

		assertTrue(veng.isProven());
	}

	@Test
	public void loggingOutForgetsIt()
	{
		// The next login may be a different character entirely.
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_COOLDOWN, 1);
		veng.reset();

		assertFalse(veng.isProven());
	}

	@Test
	public void aTeammatesVengeanceOtherIsNeverAskedToBeRecast()
	{
		// Reported from a live game: on Ancients in Theatre of Blood, with a
		// teammate casting Vengeance Other, the banner asked for a vengeance
		// that could not be cast from that spellbook at all.
		//
		// This runs the two pieces together the way BuffReminder does.
		CastProof proof = new CastProof(VENG_COOLDOWN);
		BuffState state = new BuffState(VENG_REBOUND, VENG_COOLDOWN);
		state.sync(new int[]{0, 0});

		// The teammate's cast marks you, and starts no cooldown of yours.
		proof.onVarbitValue(VENG_REBOUND, 1);
		assertEquals(Transition.ACTIVE, state.onVarbitValue(VENG_REBOUND, 1));

		// It rebounds. The buff has genuinely expired...
		proof.onVarbitValue(VENG_REBOUND, 0);
		assertEquals(Transition.EXPIRED, state.onVarbitValue(VENG_REBOUND, 0));
		assertTrue(state.hasBeenActive());

		// ...but it was never yours, so nothing is said.
		assertFalse(proof.isProven());
	}

	@Test
	public void yourOwnVengeanceStillReminds()
	{
		// The same sequence with your own cast, which must still work.
		CastProof proof = new CastProof(VENG_COOLDOWN);
		BuffState state = new BuffState(VENG_REBOUND, VENG_COOLDOWN);
		state.sync(new int[]{0, 0});

		// Casting it marks you and starts your cooldown.
		proof.onVarbitValue(VENG_REBOUND, 1);
		assertEquals(Transition.ACTIVE, state.onVarbitValue(VENG_REBOUND, 1));
		proof.onVarbitValue(VENG_COOLDOWN, 1);
		assertEquals(Transition.NONE, state.onVarbitValue(VENG_COOLDOWN, 1));

		// The cooldown ends first; the vengeance is still waiting to rebound.
		proof.onVarbitValue(VENG_COOLDOWN, 0);
		assertEquals(Transition.NONE, state.onVarbitValue(VENG_COOLDOWN, 0));

		// It rebounds, and this one is worth saying.
		proof.onVarbitValue(VENG_REBOUND, 0);
		assertEquals(Transition.EXPIRED, state.onVarbitValue(VENG_REBOUND, 0));
		assertTrue(proof.isProven());
	}

	@Test
	public void loggingInPartwayThroughYourOwnCooldownCounts()
	{
		// Seeding at login reads the cooldown already running, which is still
		// proof the cast was yours.
		CastProof veng = new CastProof(VENG_COOLDOWN);
		veng.onVarbitValue(VENG_REBOUND, 1);
		veng.onVarbitValue(VENG_COOLDOWN, 17);

		assertTrue(veng.isProven());
	}

	@Test
	public void everyDefinitionWithACooldownWatchesIt()
	{
		// The cooldown is added to the watched set by the definition, so it
		// cannot be listed in one place and forgotten in the other.
		for (BuffDefinition definition : new BuffDefinition[]{
			Buffs.THRALL, Buffs.VENGEANCE, Buffs.DEATH_CHARGE, Buffs.SHADOW_VEIL})
		{
			final int cooldown = definition.getCooldownVarbit();
			assertFalse("should have a cooldown", cooldown == CastProof.NO_COOLDOWN);

			boolean watched = false;
			for (int varbit : definition.getVarbits())
			{
				watched |= varbit == cooldown;
			}

			assertTrue("cooldown should be watched: " + definition.getAlertText(), watched);
		}
	}
}
