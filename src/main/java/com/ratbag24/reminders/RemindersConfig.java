package com.ratbag24.reminders;

import com.ratbag24.reminders.heart.TrackedHearts;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

@ConfigGroup(RemindersConfig.GROUP)
public interface RemindersConfig extends Config
{
	String GROUP = "aioreminders";

	// ------------------------------------------------------------------
	// Saturated heart
	//
	// Each reminder gets a section of its own, so adding the next one means
	// adding a section here rather than reshuffling what is already there.
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Saturated heart",
		description = "Reminds you when your saturated or imbued heart is off cooldown.",
		position = 10
	)
	String heartSection = "heartSection";

	@ConfigItem(
		keyName = "heartEnabled",
		name = "Enable",
		description = "Track the heart cooldown and remind you when it has recharged.",
		section = heartSection,
		position = 1
	)
	default boolean heartEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "heartTrackedHearts",
		name = "Hearts to track",
		description = "The imbued and saturated hearts share one cooldown. Choose whether to"
			+ " be reminded about both, or only the saturated heart's shorter five minutes.",
		section = heartSection,
		position = 2
	)
	default TrackedHearts heartTrackedHearts()
	{
		return TrackedHearts.BOTH;
	}

	@ConfigItem(
		keyName = "heartNotification",
		name = "Notify when recharged",
		description = "Fire a notification the moment the cooldown ends.",
		section = heartSection,
		position = 3
	)
	default Notification heartNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "heartReminderOverlay",
		name = "Show on-screen reminder",
		description = "Keep a reminder on screen for as long as the heart is ready to use,"
			+ " rather than only announcing it once.",
		section = heartSection,
		position = 4
	)
	default boolean heartReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "heartTimer",
		name = "Show cooldown timer",
		description = "Show an infobox counting down the remaining cooldown.",
		section = heartSection,
		position = 5
	)
	default boolean heartTimer()
	{
		return true;
	}

	@ConfigItem(
		keyName = "heartChatMessage",
		name = "Send a chat message",
		description = "Also print a message in the game chat when the heart recharges.",
		section = heartSection,
		position = 6
	)
	default boolean heartChatMessage()
	{
		return false;
	}

	@ConfigItem(
		keyName = "heartRequireInInventory",
		name = "Only when carrying one",
		description = "Stay quiet unless a heart is actually in your inventory.",
		section = heartSection,
		position = 7
	)
	default boolean heartRequireInInventory()
	{
		return true;
	}

	@ConfigItem(
		keyName = "heartOnlyInCombat",
		name = "Only while fighting",
		description = "Stay quiet unless you have dealt or taken damage recently, so the"
			+ " reminder does not follow you around the bank.",
		section = heartSection,
		position = 8
	)
	default boolean heartOnlyInCombat()
	{
		return true;
	}
}
