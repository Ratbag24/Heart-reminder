package com.ratbag24.reminders;

import javax.annotation.Nullable;
import net.runelite.api.events.ChatMessage;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.plugins.Plugin;

/**
 * One thing the plugin can remind you about.
 * <p>
 * Everything here is optional bar whether the reminder is switched on, so a new
 * reminder only has to implement the events it actually cares about. {@link RemindersPlugin} subscribes to the
 * client's events once and hands them to every registered reminder.
 */
public interface Reminder
{
	/** Whether the user has this reminder switched on. */
	boolean isEnabled();

	/** Called when the plugin starts, before any events arrive. */
	default void startUp(Plugin plugin)
	{
	}

	/** Called when the plugin stops. Must leave no overlays or infoboxes behind. */
	default void shutDown()
	{
	}

	/**
	 * Called when the player logs in, hops world or logs out: anything the
	 * reminder inferred from the previous session is no longer safe to trust.
	 */
	default void reset()
	{
	}

	default void onVarbitChanged(VarbitChanged event)
	{
	}

	default void onItemContainerChanged(ItemContainerChanged event)
	{
	}

	default void onGameTick()
	{
	}

	default void onChatMessage(ChatMessage event)
	{
	}

	/** Called when any of this plugin's settings changed. */
	default void onConfigChanged()
	{
	}

	/**
	 * Whether this reminder wants the player's attention right now, whatever
	 * form that attention takes.
	 * <p>
	 * Kept separate from {@link #getAlertText()} so that the screen edge and
	 * the banner can be switched on and off independently: someone who wants
	 * only a flashing edge should still get one.
	 */
	default boolean isNudging()
	{
		return false;
	}

	/**
	 * What the alert banner should say while this reminder is nudging, or null
	 * when it has nothing to shout about. Phrased as the thing to do rather
	 * than the state you are in: mid-fight, "You need to death charge!" lands
	 * faster than "Death charge: down".
	 */
	@Nullable
	default String getAlertText()
	{
		return null;
	}
}
