package com.ratbag24.reminders;

import com.ratbag24.reminders.heart.TrackedHearts;
import java.awt.Color;
import net.runelite.client.config.Alpha;
import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.ConfigSection;
import net.runelite.client.config.Notification;

/**
 * One section per reminder, each with its own Enable, so a reminder can be
 * switched off without touching the others. Section positions go up in tens to
 * leave room for the next one.
 */
@ConfigGroup(RemindersConfig.GROUP)
public interface RemindersConfig extends Config
{
	String GROUP = "aioreminders";

	// ------------------------------------------------------------------
	// General
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "General",
		description = "Settings shared by every reminder.",
		position = 1
	)
	String generalSection = "generalSection";

	@ConfigItem(
		keyName = "chatMessages",
		name = "Send chat messages",
		description = "Also print a message in the game chat whenever a reminder fires.",
		section = generalSection,
		position = 1
	)
	default boolean chatMessages()
	{
		return false;
	}

	@ConfigItem(
		keyName = "screenFlash",
		name = "Flash the screen edge",
		description = "Throb a coloured glow around the edge of the game view while any"
			+ " reminder is asking for attention, and stop as soon as none is.",
		section = generalSection,
		position = 2
	)
	default boolean screenFlash()
	{
		return true;
	}

	@Alpha
	@ConfigItem(
		keyName = "flashColour",
		name = "Flash colour",
		description = "The colour used for the screen edge and for highlighting an item.",
		section = generalSection,
		position = 3
	)
	default Color flashColour()
	{
		return new Color(255, 48, 48, 170);
	}

	// ------------------------------------------------------------------
	// Saturated heart
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
		keyName = "heartItemFlash",
		name = "Flash the heart in your inventory",
		description = "Throb the heart in your inventory while it is ready to invigorate,"
			+ " so the thing to click is the thing that is lit up.",
		section = heartSection,
		position = 6
	)
	default boolean heartItemFlash()
	{
		return true;
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

	// ------------------------------------------------------------------
	// Thrall
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Thrall",
		description = "Reminds you when your resurrected thrall has expired.",
		position = 20
	)
	String thrallSection = "thrallSection";

	@ConfigItem(
		keyName = "thrallEnabled",
		name = "Enable",
		description = "Remind you when a thrall you resurrected runs out.",
		section = thrallSection,
		position = 1
	)
	default boolean thrallEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "thrallNotification",
		name = "Notify when expired",
		description = "Fire a notification as the thrall leaves.",
		section = thrallSection,
		position = 2
	)
	default Notification thrallNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "thrallReminderOverlay",
		name = "Show on-screen reminder",
		description = "Keep a reminder on screen for as long as you are without a thrall.",
		section = thrallSection,
		position = 3
	)
	default boolean thrallReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "thrallOnlyInCombat",
		name = "Only while fighting",
		description = "Stay quiet unless you have dealt or taken damage recently.",
		section = thrallSection,
		position = 4
	)
	default boolean thrallOnlyInCombat()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Vengeance
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Vengeance",
		description = "Reminds you when vengeance can be cast again.",
		position = 30
	)
	String vengeanceSection = "vengeanceSection";

	@ConfigItem(
		keyName = "vengeanceEnabled",
		name = "Enable",
		description = "Remind you once both the cooldown has run out and the cast you were"
			+ " holding has rebounded.",
		section = vengeanceSection,
		position = 1
	)
	default boolean vengeanceEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vengeanceNotification",
		name = "Notify when ready",
		description = "Fire a notification as vengeance becomes castable again.",
		section = vengeanceSection,
		position = 2
	)
	default Notification vengeanceNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "vengeanceReminderOverlay",
		name = "Show on-screen reminder",
		description = "Keep a reminder on screen for as long as vengeance is uncast.",
		section = vengeanceSection,
		position = 3
	)
	default boolean vengeanceReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vengeanceOnlyInCombat",
		name = "Only while fighting",
		description = "Stay quiet unless you have dealt or taken damage recently. Turn this"
			+ " off if you would rather be reminded before a fight starts than during it.",
		section = vengeanceSection,
		position = 4
	)
	default boolean vengeanceOnlyInCombat()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Divine potions
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Divine potions",
		description = "Reminds you when a divine potion's boost has worn off.",
		position = 40
	)
	String divineSection = "divineSection";

	@ConfigItem(
		keyName = "divineEnabled",
		name = "Enable",
		description = "Remind you when the last of a divine potion's boosts runs out.",
		section = divineSection,
		position = 1
	)
	default boolean divineEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "divineNotification",
		name = "Notify when worn off",
		description = "Fire a notification as the boost ends.",
		section = divineSection,
		position = 2
	)
	default Notification divineNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "divineReminderOverlay",
		name = "Show on-screen reminder",
		description = "Keep a reminder on screen for as long as you are without the boost.",
		section = divineSection,
		position = 3
	)
	default boolean divineReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "divineOnlyInCombat",
		name = "Only while fighting",
		description = "Stay quiet unless you have dealt or taken damage recently.",
		section = divineSection,
		position = 4
	)
	default boolean divineOnlyInCombat()
	{
		return true;
	}
}
