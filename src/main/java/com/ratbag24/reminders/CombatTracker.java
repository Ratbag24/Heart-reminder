package com.ratbag24.reminders;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.Client;
import net.runelite.api.events.HitsplatApplied;

/**
 * Keeps track of how recently the player was fighting, so that reminders can
 * stay quiet while you are banking or running between tasks.
 * <p>
 * Only hitsplats count. Who the player is interacting with would also catch
 * talking to an NPC or using a bank, whereas damage dealt or taken is combat
 * and nothing else.
 */
@Singleton
public class CombatTracker
{
	/**
	 * How long after the last hit the player still counts as fighting. Long
	 * enough to cover the gap between kills and a slow weapon's attack speed.
	 */
	private static final int COMBAT_TIMEOUT_TICKS = 17;

	@Inject
	private Client client;

	private int lastCombatTick = Integer.MIN_VALUE;

	void onHitsplatApplied(HitsplatApplied event)
	{
		final boolean dealtByUs = event.getHitsplat().isMine();
		final boolean takenByUs = event.getActor() == client.getLocalPlayer();

		if (dealtByUs || takenByUs)
		{
			lastCombatTick = client.getTickCount();
		}
	}

	void reset()
	{
		lastCombatTick = Integer.MIN_VALUE;
	}

	/** Whether the player has been in combat recently enough to count. */
	public boolean isInCombat()
	{
		if (lastCombatTick == Integer.MIN_VALUE)
		{
			return false;
		}

		return client.getTickCount() - lastCombatTick <= COMBAT_TIMEOUT_TICKS;
	}
}
