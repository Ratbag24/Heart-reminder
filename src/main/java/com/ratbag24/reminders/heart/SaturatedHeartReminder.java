package com.ratbag24.reminders.heart;

import com.ratbag24.reminders.Reminder;
import com.ratbag24.reminders.ReminderContext;
import com.ratbag24.reminders.RemindersConfig;
import java.util.Locale;
import javax.annotation.Nullable;
import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ItemContainer;
import net.runelite.api.events.ItemContainerChanged;
import net.runelite.api.events.VarbitChanged;
import net.runelite.api.gameval.InventoryID;
import net.runelite.api.gameval.ItemID;
import net.runelite.api.gameval.VarbitID;
import net.runelite.client.game.ItemManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.ui.overlay.infobox.InfoBoxManager;
import net.runelite.client.util.AsyncBufferedImage;

/**
 * Reminds the player that their saturated (or imbued) heart is ready again.
 * <p>
 * Both hearts count down the same varbit, so this one reminder covers either.
 * The varbit stepping to zero is treated as the authoritative "recharged"
 * event; the on-screen reminder, in contrast, reflects the ongoing state of
 * having a usable heart, which also covers a heart you have not got round to
 * using yet.
 */
@Singleton
public class SaturatedHeartReminder implements Reminder
{
	private final HeartCooldown cooldown = new HeartCooldown();

	@Inject
	private ReminderContext ctx;

	@Inject
	private RemindersConfig config;

	@Inject
	private InfoBoxManager infoBoxManager;

	@Inject
	private ItemManager itemManager;

	private Plugin plugin;
	private HeartInfoBox infoBox;
	private int infoBoxItemId;

	@Override
	public String getName()
	{
		return "Saturated heart";
	}

	@Override
	public boolean isEnabled()
	{
		return config.heartEnabled();
	}

	@Override
	public void startUp(Plugin plugin)
	{
		this.plugin = plugin;
	}

	@Override
	public void shutDown()
	{
		removeInfoBox();
		cooldown.reset();
		plugin = null;
	}

	@Override
	public void reset()
	{
		cooldown.reset();
		removeInfoBox();
	}

	@Override
	public void onVarbitChanged(VarbitChanged event)
	{
		if (event.getVarbitId() == VarbitID.SATURATED_HEART_TIME && event.getValue() > 0)
		{
			// Only a saturated heart moves this one, which is enough to put a
			// name to a cooldown that was already running when we logged in.
			cooldown.markSaturated();
			syncInfoBox();
			return;
		}

		if (event.getVarbitId() != VarbitID.IMBUED_HEART_TIMER)
		{
			return;
		}

		// The cooldown is tracked even while the reminder is switched off, so
		// that switching it on mid-cooldown shows the right time straight away.
		final HeartCooldown.Transition transition =
			cooldown.onVarbitValue(event.getValue(), System.currentTimeMillis());

		if (transition == HeartCooldown.Transition.READY)
		{
			announceReady();
		}

		syncInfoBox();
	}

	@Override
	public void onItemContainerChanged(ItemContainerChanged event)
	{
		if (event.getContainerId() == InventoryID.INV)
		{
			// Banking a heart, or taking one out, changes whether the timer
			// should be on screen.
			syncInfoBox();
		}
	}

	@Override
	public void onGameTick()
	{
		if (!cooldown.isSynced())
		{
			// Seed from the current value on the first tick after logging in.
			// Adopting the value this way never counts as the heart recharging.
			cooldown.onVarbitValue(ctx.getClient().getVarbitValue(VarbitID.IMBUED_HEART_TIMER),
				System.currentTimeMillis());
			syncInfoBox();
		}
	}

	@Override
	public void onConfigChanged()
	{
		syncInfoBox();
	}

	@Nullable
	@Override
	public String getReminderText()
	{
		if (!isEnabled() || !config.heartReminderOverlay())
		{
			return null;
		}

		if (!cooldown.isSynced() || cooldown.isOnCooldown() || !isTracked())
		{
			return null;
		}

		if (!conditionsMet())
		{
			return null;
		}

		return cooldown.getType().getDisplayName() + " ready";
	}

	private void announceReady()
	{
		if (!isEnabled() || !isTracked() || !conditionsMet())
		{
			return;
		}

		final String heartName = cooldown.getType().getDisplayName().toLowerCase(Locale.ENGLISH);
		ctx.announce(config.heartNotification(), "Your " + heartName + " has recharged.");
	}

	/** Whether the heart on cooldown is one the user asked to be reminded about. */
	private boolean isTracked()
	{
		return config.heartTrackedHearts().includes(cooldown.getType());
	}

	private boolean conditionsMet()
	{
		if (config.heartRequireInInventory() && !carryingHeart())
		{
			return false;
		}

		return !config.heartOnlyInCombat() || ctx.isInCombat();
	}

	private boolean carryingHeart()
	{
		final ItemContainer inventory = ctx.getClient().getItemContainer(InventoryID.INV);
		if (inventory == null)
		{
			return false;
		}

		if (inventory.contains(ItemID.SATURATED_HEART))
		{
			return true;
		}

		// An imbued heart only counts when imbued hearts are being tracked.
		return config.heartTrackedHearts() == TrackedHearts.BOTH
			&& inventory.contains(ItemID.IMBUED_HEART);
	}

	/**
	 * Brings the infobox in line with the settings and the cooldown. Called
	 * after anything that could change either, so there is one place that
	 * decides whether the timer should be on screen.
	 */
	private void syncInfoBox()
	{
		// The timer follows "only when carrying one" but deliberately not "only
		// while fighting": a countdown that vanished between kills would be
		// worse than no countdown at all.
		final boolean wanted = isEnabled()
			&& config.heartTimer()
			&& cooldown.isOnCooldown()
			&& isTracked()
			&& (!config.heartRequireInInventory() || carryingHeart());

		if (!wanted)
		{
			removeInfoBox();
			return;
		}

		final int itemId = heartItemId();

		if (infoBox != null && infoBoxItemId == itemId)
		{
			// The heart may have been identified since the box went up, in
			// which case only the wording needs catching up.
			infoBox.setTooltip(tooltip());
			return;
		}

		// A heart identified part way through a cooldown leaves the infobox
		// showing the other heart's icon, so it is rebuilt rather than reused.
		removeInfoBox();
		addInfoBox(itemId);
	}

	private void addInfoBox(int itemId)
	{
		if (plugin == null)
		{
			return;
		}

		final AsyncBufferedImage image = itemManager.getImage(itemId);
		final HeartInfoBox box = new HeartInfoBox(image, plugin, cooldown);
		box.setTooltip(tooltip());

		infoBox = box;
		infoBoxItemId = itemId;
		infoBoxManager.addInfoBox(box);

		// Item images load off the client thread; refresh once it arrives.
		image.onLoaded(() ->
		{
			if (infoBox == box)
			{
				box.setImage(image);
				infoBoxManager.updateInfoBoxImage(box);
			}
		});
	}

	private void removeInfoBox()
	{
		if (infoBox != null)
		{
			infoBoxManager.removeInfoBox(infoBox);
			infoBox = null;
			infoBoxItemId = 0;
		}
	}

	private String tooltip()
	{
		return cooldown.getType().getDisplayName() + " cooldown";
	}

	private int heartItemId()
	{
		return cooldown.getType() == HeartType.IMBUED
			? ItemID.IMBUED_HEART
			: ItemID.SATURATED_HEART;
	}
}
