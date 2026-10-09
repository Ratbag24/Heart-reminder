package com.ratbag24.reminders.buff;

import java.util.function.BooleanSupplier;
import java.util.function.Supplier;
import net.runelite.client.config.Notification;

/**
 * One buff's settings, read live from the config.
 * <p>
 * The settings are passed as suppliers rather than values so that a buff
 * reminder reads whatever the user has set at the moment it matters, without
 * every reminder needing to know about the config interface.
 */
public final class BuffSettings
{
	private final BooleanSupplier enabled;
	private final Supplier<Notification> notification;
	private final BooleanSupplier showReminder;
	private final BooleanSupplier onlyInCombat;

	public BuffSettings(BooleanSupplier enabled, Supplier<Notification> notification,
		BooleanSupplier showReminder, BooleanSupplier onlyInCombat)
	{
		this.enabled = enabled;
		this.notification = notification;
		this.showReminder = showReminder;
		this.onlyInCombat = onlyInCombat;
	}

	public boolean isEnabled()
	{
		return enabled.getAsBoolean();
	}

	public Notification getNotification()
	{
		return notification.get();
	}

	public boolean isShowReminder()
	{
		return showReminder.getAsBoolean();
	}

	public boolean isOnlyInCombat()
	{
		return onlyInCombat.getAsBoolean();
	}
}
