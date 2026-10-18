package com.ratbag24.reminders.chat;

import com.ratbag24.reminders.Alert;
import com.ratbag24.reminders.Reminder;
import com.ratbag24.reminders.ReminderContext;
import com.ratbag24.reminders.buff.BuffSettings;
import javax.annotation.Nullable;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Skill;
import net.runelite.api.events.ChatMessage;

/**
 * Reminds the player about a buff the game only announces in chat.
 * <p>
 * The cast is heard, the clock is run for as long as the spell lasts, and the
 * expiry is worked out rather than read. {@link TimedBuffState} sets out what
 * that costs in certainty.
 */
public class ChatBuffReminder implements Reminder
{
	private final ReminderContext ctx;
	private final ChatBuffDefinition definition;
	private final BuffSettings settings;
	private final TimedBuffState state = new TimedBuffState();

	public ChatBuffReminder(ReminderContext ctx, ChatBuffDefinition definition,
		BuffSettings settings)
	{
		this.ctx = ctx;
		this.definition = definition;
		this.settings = settings;
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
	public void onChatMessage(ChatMessage event)
	{
		if (event.getType() != ChatMessageType.SPAM
			&& event.getType() != ChatMessageType.GAMEMESSAGE)
		{
			return;
		}

		if (!event.getMessage().endsWith(definition.getCastMessage()))
		{
			return;
		}

		// Both spells last one tick per Magic level, and it is the real level
		// that counts: a boost does not buy a longer mark.
		state.start(ctx.getClient().getTickCount(),
			ctx.getClient().getRealSkillLevel(Skill.MAGIC));
	}

	@Override
	public void onGameTick()
	{
		final TimedBuffState.Transition transition =
			state.onTick(ctx.getClient().getTickCount());

		if (transition == TimedBuffState.Transition.EXPIRED && isEnabled() && conditionsMet()
			&& !onCooldown())
		{
			ctx.announce(settings.getNotification(), definition.getNotificationMessage());
		}
	}

	@Override
	public boolean isNudging()
	{
		// Nothing is claimed about a spell whose cast was never heard, which is
		// what logging in mid-buff looks like from here.
		if (!isEnabled() || state.isActive() || !state.hasBeenActive() || !conditionsMet())
		{
			return false;
		}

		return !onCooldown();
	}

	/** Whether the spell still cannot be cast, where the game says so. */
	private boolean onCooldown()
	{
		return definition.hasCooldownVarbit()
			&& ctx.getClient().getVarbitValue(definition.getCooldownVarbit()) > 0;
	}

	@Nullable
	@Override
	public Alert getAlert()
	{
		return isNudging() && settings.isShowReminder()
			? new Alert(definition.getAlertText(), settings.getColour())
			: null;
	}

	private boolean conditionsMet()
	{
		return !settings.isOnlyInCombat() || ctx.isInCombat();
	}
}
