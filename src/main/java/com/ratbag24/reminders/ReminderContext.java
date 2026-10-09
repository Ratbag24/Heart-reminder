package com.ratbag24.reminders;

import javax.inject.Inject;
import javax.inject.Singleton;
import net.runelite.api.ChatMessageType;
import net.runelite.api.Client;
import net.runelite.client.Notifier;
import net.runelite.client.chat.ChatColorType;
import net.runelite.client.chat.ChatMessageBuilder;
import net.runelite.client.chat.ChatMessageManager;
import net.runelite.client.chat.QueuedMessage;
import net.runelite.client.config.Notification;

/**
 * The services every reminder needs, in one place.
 * <p>
 * Reminders are plain objects rather than injected singletons, because several
 * of them are the same class with different buffs; handing them this instead of
 * five constructor arguments each keeps them to their own business.
 */
@Singleton
public class ReminderContext
{
	@Inject
	private Client client;

	@Inject
	private Notifier notifier;

	@Inject
	private ChatMessageManager chatMessageManager;

	@Inject
	private CombatTracker combatTracker;

	@Inject
	private RemindersConfig config;

	public Client getClient()
	{
		return client;
	}

	public RemindersConfig getConfig()
	{
		return config;
	}

	/** Whether the player has dealt or taken damage recently enough to count. */
	public boolean isInCombat()
	{
		return combatTracker.isInCombat();
	}

	/**
	 * Announces something once: a notification in whatever form the user chose
	 * for it, and a chat message too when they have asked for those.
	 */
	public void announce(Notification notification, String message)
	{
		notifier.notify(notification, message);

		if (config.chatMessages())
		{
			final String formatted = new ChatMessageBuilder()
				.append(ChatColorType.HIGHLIGHT)
				.append(message)
				.build();

			chatMessageManager.queue(QueuedMessage.builder()
				.type(ChatMessageType.CONSOLE)
				.runeLiteFormattedMessage(formatted)
				.build());
		}
	}
}
