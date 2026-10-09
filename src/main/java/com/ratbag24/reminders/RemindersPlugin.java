package com.ratbag24.reminders;

import com.google.inject.Provides;
import com.ratbag24.reminders.buff.BuffReminder;
import com.ratbag24.reminders.buff.BuffSettings;
import com.ratbag24.reminders.buff.Buffs;
import com.ratbag24.reminders.heart.SaturatedHeartReminder;
import com.ratbag24.reminders.visual.ItemFlashOverlay;
import com.ratbag24.reminders.visual.ScreenFlashOverlay;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.inject.Inject;
import net.runelite.api.GameState;
import net.runelite.api.events.GameStateChanged;
import net.runelite.api.events.GameTick;
import net.runelite.api.events.HitsplatApplied;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.events.ConfigChanged;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

/**
 * A single plugin for the small "don't forget to re-apply that" reminders, each
 * one switchable on its own.
 * <p>
 * The client's events are subscribed to once here and passed on to every
 * registered {@link Reminder}, so adding the next reminder means defining it
 * and listing it in {@link #startUp()} rather than touching the event plumbing.
 */
@PluginDescriptor(
	name = "All-in-One Reminders",
	description = "Reminds you when your saturated heart and other re-appliable buffs are ready",
	tags = {"heart", "saturated", "imbued", "thrall", "vengeance", "divine", "boost",
		"reminder", "timer", "cooldown", "buff"}
)
public class RemindersPlugin extends Plugin
{
	@Inject
	private OverlayManager overlayManager;

	@Inject
	private RemindersOverlay overlay;

	@Inject
	private ScreenFlashOverlay screenFlashOverlay;

	@Inject
	private ItemFlashOverlay itemFlashOverlay;

	@Inject
	private CombatTracker combatTracker;

	@Inject
	private ReminderContext reminderContext;

	@Inject
	private RemindersConfig config;

	@Inject
	private SaturatedHeartReminder saturatedHeartReminder;

	private final List<Reminder> reminders = new ArrayList<>();

	@Provides
	RemindersConfig provideConfig(ConfigManager configManager)
	{
		return configManager.getConfig(RemindersConfig.class);
	}

	@Override
	protected void startUp()
	{
		// Register each reminder here. The rest of the plugin does not care
		// which ones exist.
		//
		// The heart has a class of its own because it tracks a cooldown with a
		// countdown and two possible hearts behind one varbit. Everything else
		// so far is a buff that is simply there or not, which one class covers:
		// a new one needs a definition in Buffs, a config section, and a line
		// here.
		reminders.add(saturatedHeartReminder);

		reminders.add(new BuffReminder(reminderContext, Buffs.THRALL, new BuffSettings(
			config::thrallEnabled,
			config::thrallNotification,
			config::thrallReminderOverlay,
			config::thrallOnlyInCombat)));

		reminders.add(new BuffReminder(reminderContext, Buffs.VENGEANCE, new BuffSettings(
			config::vengeanceEnabled,
			config::vengeanceNotification,
			config::vengeanceReminderOverlay,
			config::vengeanceOnlyInCombat)));

		reminders.add(new BuffReminder(reminderContext, Buffs.DIVINE_POTION, new BuffSettings(
			config::divineEnabled,
			config::divineNotification,
			config::divineReminderOverlay,
			config::divineOnlyInCombat)));

		for (Reminder reminder : reminders)
		{
			reminder.startUp(this);
		}

		overlayManager.add(overlay);
		overlayManager.add(screenFlashOverlay);
		overlayManager.add(itemFlashOverlay);
	}

	@Override
	protected void shutDown()
	{
		overlayManager.remove(overlay);
		overlayManager.remove(screenFlashOverlay);
		overlayManager.remove(itemFlashOverlay);

		for (Reminder reminder : reminders)
		{
			reminder.shutDown();
		}

		reminders.clear();
		combatTracker.reset();
	}

	@Subscribe
	public void onGameStateChanged(GameStateChanged event)
	{
		final GameState state = event.getGameState();

		if (state == GameState.LOGIN_SCREEN || state == GameState.HOPPING
			|| state == GameState.CONNECTION_LOST)
		{
			// Nothing inferred from the last session can be trusted, and the
			// next value read must not be mistaken for a buff running out.
			combatTracker.reset();

			for (Reminder reminder : reminders)
			{
				reminder.reset();
			}
		}
	}

	@Subscribe
	public void onVarbitChanged(VarbitChanged event)
	{
		for (Reminder reminder : reminders)
		{
			reminder.onVarbitChanged(event);
		}
	}

	@Subscribe
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		for (Reminder reminder : reminders)
		{
			reminder.onItemContainerChanged(event);
		}
	}

	@Subscribe
	public void onHitsplatApplied(HitsplatApplied event)
	{
		combatTracker.onHitsplatApplied(event);
	}

	@Subscribe
	public void onGameTick(GameTick event)
	{
		for (Reminder reminder : reminders)
		{
			reminder.onGameTick();
		}
	}

	@Subscribe
	public void onConfigChanged(ConfigChanged event)
	{
		if (!RemindersConfig.GROUP.equals(event.getGroup()))
		{
			return;
		}

		for (Reminder reminder : reminders)
		{
			reminder.onConfigChanged();
		}
	}

	/**
	 * Whether any enabled reminder is asking for attention, which is what the
	 * screen edge flash follows. Independent of the text panel, so the two can
	 * be switched on and off separately.
	 */
	public boolean isAnyReminderNudging()
	{
		for (Reminder reminder : reminders)
		{
			if (reminder.isNudging())
			{
				return true;
			}
		}

		return false;
	}

	/**
	 * What the enabled reminders want shown on screen, in registration order.
	 * Read by the overlay on the client thread while it renders.
	 */
	List<String> getActiveReminderTexts()
	{
		List<String> texts = null;

		for (Reminder reminder : reminders)
		{
			final String text = reminder.getReminderText();

			if (text != null)
			{
				if (texts == null)
				{
					texts = new ArrayList<>(reminders.size());
				}

				texts.add(text);
			}
		}

		return texts == null ? Collections.emptyList() : texts;
	}
}
