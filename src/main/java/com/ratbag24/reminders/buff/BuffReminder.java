package com.ratbag24.reminders.buff;

import com.ratbag24.reminders.Reminder;
import com.ratbag24.reminders.ReminderContext;
import javax.annotation.Nullable;
import net.runelite.api.events.VarbitChanged;

/**
 * Reminds the player that a buff has run out, for any buff the game reports
 * through varbits that are non-zero while it lasts.
 * <p>
 * One instance per buff, built from a {@link BuffDefinition}. The notification
 * fires on the buff running out; the on-screen line, in contrast, reflects the
 * ongoing state of not having it, which also covers a buff never applied in the
 * first place.
 */
public class BuffReminder implements Reminder
{
	private final ReminderContext ctx;
	private final BuffDefinition definition;
	private final BuffSettings settings;
	private final BuffState state;

	public BuffReminder(ReminderContext ctx, BuffDefinition definition, BuffSettings settings)
	{
		this.ctx = ctx;
		this.definition = definition;
		this.settings = settings;
		this.state = new BuffState(definition.getVarbits());
	}

	@Override
	public String getName()
	{
		return definition.getName();
	}

	@Override
	public boolean isEnabled()
	{
		return settings.isEnabled();
	}

	@Override
	public void reset()
	{
		state.reset();
	}

	@Override
	public void onVarbitChanged(VarbitChanged event)
	{
		// The buff is tracked even while its reminder is switched off, so that
		// switching it on does not need a re-login to read right.
		final BuffState.Transition transition =
			state.onVarbitValue(event.getVarbitId(), event.getValue());

		if (transition == BuffState.Transition.EXPIRED && isEnabled() && conditionsMet())
		{
			ctx.announce(settings.getNotification(), definition.getNotificationMessage());
		}
	}

	@Override
	public void onGameTick()
	{
		if (!state.isSynced())
		{
			// Seed every watched varbit together on the first tick after
			// logging in. Adopting them this way never counts as an expiry.
			final int[] varbits = state.getVarbits();
			final int[] values = new int[varbits.length];

			for (int i = 0; i < varbits.length; i++)
			{
				values[i] = ctx.getClient().getVarbitValue(varbits[i]);
			}

			state.sync(values);
		}
	}

	@Override
	public boolean isNudging()
	{
		if (!isEnabled() || !state.isSynced() || state.isActive())
		{
			return false;
		}

		// Nothing is said to a player who has not had this buff up at all: not
		// having cast vengeance is the normal state for most of the game.
		return state.hasBeenActive() && conditionsMet();
	}

	@Nullable
	@Override
	public String getReminderText()
	{
		return isNudging() && settings.isShowReminder() ? definition.getReminderText() : null;
	}

	private boolean conditionsMet()
	{
		return !settings.isOnlyInCombat() || ctx.isInCombat();
	}
}
