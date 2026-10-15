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
		keyName = "statusPanel",
		name = "Status panel",
		description = "The movable panel listing the buffs you are using, green while they"
			+ " are up and red once they need re-applying. Drag it anywhere on screen.",
		section = generalSection,
		position = 1
	)
	default PanelMode statusPanel()
	{
		return PanelMode.ALL;
	}

	@ConfigItem(
		keyName = "upColour",
		name = "Colour when up",
		description = "The colour for a buff that is still running.",
		section = generalSection,
		position = 2
	)
	default Color upColour()
	{
		return new Color(86, 201, 109);
	}

	@ConfigItem(
		keyName = "downColour",
		name = "Colour when down",
		description = "The colour for a buff that needs re-applying.",
		section = generalSection,
		position = 3
	)
	default Color downColour()
	{
		return new Color(255, 73, 73);
	}

	@ConfigItem(
		keyName = "alertBanner",
		name = "Alert banner",
		description = "A banner saying what to do about whatever is down, in white on red."
			+ " Drag it wherever suits.",
		section = generalSection,
		position = 4
	)
	default boolean alertBanner()
	{
		return true;
	}

	@ConfigItem(
		keyName = "alertColour",
		name = "Alert banner colour",
		description = "The background colour of the alert banner.",
		section = generalSection,
		position = 5
	)
	default Color alertColour()
	{
		return new Color(176, 26, 36);
	}

	@ConfigItem(
		keyName = "screenFlash",
		name = "Flash the screen edge",
		description = "Throb a coloured glow around the edge of the game view while any"
			+ " reminder is asking for attention, and stop as soon as none is.",
		section = generalSection,
		position = 6
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
		position = 7
	)
	default Color flashColour()
	{
		return new Color(255, 48, 48, 170);
	}

	@ConfigItem(
		keyName = "chatMessages",
		name = "Send chat messages",
		description = "Also print a message in the game chat whenever a reminder fires.",
		section = generalSection,
		position = 8
	)
	default boolean chatMessages()
	{
		return false;
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
		name = "Show in status panel",
		description = "List the heart in the status panel.",
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
		description = "Only notify and flash while you have dealt or taken damage recently,"
			+ " so nothing follows you around the bank.",
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
		description = "Remind you when your resurrected thrall has expired.",
		section = thrallSection,
		position = 1
	)
	default boolean thrallEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "thrallNotification",
		name = "Notify",
		description = "Fire a notification when your resurrected thrall has expired.",
		section = thrallSection,
		position = 2
	)
	default Notification thrallNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "thrallReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
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
		description = "Only notify and flash while you have dealt or taken damage recently.",
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
		description = "Remind you when vengeance can be cast again.",
		section = vengeanceSection,
		position = 1
	)
	default boolean vengeanceEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "vengeanceNotification",
		name = "Notify",
		description = "Fire a notification when vengeance can be cast again.",
		section = vengeanceSection,
		position = 2
	)
	default Notification vengeanceNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "vengeanceReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
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
		description = "Only notify and flash while you have dealt or taken damage recently.",
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
		description = "Remind you when a divine potion's boost has worn off.",
		section = divineSection,
		position = 1
	)
	default boolean divineEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "divineNotification",
		name = "Notify",
		description = "Fire a notification when a divine potion's boost has worn off.",
		section = divineSection,
		position = 2
	)
	default Notification divineNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "divineReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
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
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = divineSection,
		position = 4
	)
	default boolean divineOnlyInCombat()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Death charge
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Death charge",
		description = "Reminds you when your death charge has expired.",
		position = 50
	)
	String deathChargeSection = "deathChargeSection";

	@ConfigItem(
		keyName = "deathChargeEnabled",
		name = "Enable",
		description = "Remind you when your death charge has expired.",
		section = deathChargeSection,
		position = 1
	)
	default boolean deathChargeEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "deathChargeNotification",
		name = "Notify",
		description = "Fire a notification when your death charge has expired.",
		section = deathChargeSection,
		position = 2
	)
	default Notification deathChargeNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "deathChargeReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = deathChargeSection,
		position = 3
	)
	default boolean deathChargeReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "deathChargeOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = deathChargeSection,
		position = 4
	)
	default boolean deathChargeOnlyInCombat()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Shadow veil
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Shadow veil",
		description = "Reminds you when your shadow veil has expired.",
		position = 60
	)
	String shadowVeilSection = "shadowVeilSection";

	@ConfigItem(
		keyName = "shadowVeilEnabled",
		name = "Enable",
		description = "Remind you when your shadow veil has expired.",
		section = shadowVeilSection,
		position = 1
	)
	default boolean shadowVeilEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "shadowVeilNotification",
		name = "Notify",
		description = "Fire a notification when your shadow veil has expired.",
		section = shadowVeilSection,
		position = 2
	)
	default Notification shadowVeilNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "shadowVeilReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = shadowVeilSection,
		position = 3
	)
	default boolean shadowVeilReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "shadowVeilOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = shadowVeilSection,
		position = 4
	)
	default boolean shadowVeilOnlyInCombat()
	{
		return false;
	}

	// ------------------------------------------------------------------
	// Antifire
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Antifire",
		description = "Reminds you when your antifire has run out.",
		position = 70
	)
	String antifireSection = "antifireSection";

	@ConfigItem(
		keyName = "antifireEnabled",
		name = "Enable",
		description = "Remind you when your antifire has run out.",
		section = antifireSection,
		position = 1
	)
	default boolean antifireEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "antifireNotification",
		name = "Notify",
		description = "Fire a notification when your antifire has run out.",
		section = antifireSection,
		position = 2
	)
	default Notification antifireNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "antifireReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = antifireSection,
		position = 3
	)
	default boolean antifireReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "antifireOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = antifireSection,
		position = 4
	)
	default boolean antifireOnlyInCombat()
	{
		return true;
	}

	// ------------------------------------------------------------------
	// Stamina
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Stamina",
		description = "Reminds you when your stamina potion has run out.",
		position = 80
	)
	String staminaSection = "staminaSection";

	@ConfigItem(
		keyName = "staminaEnabled",
		name = "Enable",
		description = "Remind you when your stamina potion has run out.",
		section = staminaSection,
		position = 1
	)
	default boolean staminaEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "staminaNotification",
		name = "Notify",
		description = "Fire a notification when your stamina potion has run out.",
		section = staminaSection,
		position = 2
	)
	default Notification staminaNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "staminaReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = staminaSection,
		position = 3
	)
	default boolean staminaReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "staminaOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = staminaSection,
		position = 4
	)
	default boolean staminaOnlyInCombat()
	{
		return false;
	}

	// ------------------------------------------------------------------
	// Magic imbue
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Magic imbue",
		description = "Reminds you when your magic imbue has run out.",
		position = 90
	)
	String magicImbueSection = "magicImbueSection";

	@ConfigItem(
		keyName = "magicImbueEnabled",
		name = "Enable",
		description = "Remind you when your magic imbue has run out.",
		section = magicImbueSection,
		position = 1
	)
	default boolean magicImbueEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "magicImbueNotification",
		name = "Notify",
		description = "Fire a notification when your magic imbue has run out.",
		section = magicImbueSection,
		position = 2
	)
	default Notification magicImbueNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "magicImbueReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = magicImbueSection,
		position = 3
	)
	default boolean magicImbueReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "magicImbueOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = magicImbueSection,
		position = 4
	)
	default boolean magicImbueOnlyInCombat()
	{
		return false;
	}

	// ------------------------------------------------------------------
	// Menaphite remedy
	// ------------------------------------------------------------------

	@ConfigSection(
		name = "Menaphite remedy",
		description = "Reminds you when your menaphite remedy has worn off.",
		position = 100
	)
	String menaphiteRemedySection = "menaphiteRemedySection";

	@ConfigItem(
		keyName = "menaphiteRemedyEnabled",
		name = "Enable",
		description = "Remind you when your menaphite remedy has worn off.",
		section = menaphiteRemedySection,
		position = 1
	)
	default boolean menaphiteRemedyEnabled()
	{
		return true;
	}

	@ConfigItem(
		keyName = "menaphiteRemedyNotification",
		name = "Notify",
		description = "Fire a notification when your menaphite remedy has worn off.",
		section = menaphiteRemedySection,
		position = 2
	)
	default Notification menaphiteRemedyNotification()
	{
		return Notification.ON;
	}

	@ConfigItem(
		keyName = "menaphiteRemedyReminderOverlay",
		name = "Show in status panel",
		description = "List this in the status panel once you have used it.",
		section = menaphiteRemedySection,
		position = 3
	)
	default boolean menaphiteRemedyReminderOverlay()
	{
		return true;
	}

	@ConfigItem(
		keyName = "menaphiteRemedyOnlyInCombat",
		name = "Only while fighting",
		description = "Only notify and flash while you have dealt or taken damage recently.",
		section = menaphiteRemedySection,
		position = 4
	)
	default boolean menaphiteRemedyOnlyInCombat()
	{
		return true;
	}
}
