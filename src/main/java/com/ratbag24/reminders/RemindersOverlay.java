package com.ratbag24.reminders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/**
 * A movable board of the buffs you are using: green while each is up, red once
 * it needs re-applying.
 * <p>
 * It lists only what the player actually uses — a buff that has not been
 * applied since logging in gets no line — so the panel fills itself in rather
 * than showing every buff the plugin knows about.
 * <p>
 * Each line carries the state as a word as well as a colour. Red and green are
 * the worst possible pair to tell apart for the commonest kind of colour
 * blindness, so the colour is never the only thing saying what is going on.
 */
class RemindersOverlay extends OverlayPanel
{
	private static final Color TITLE_COLOR = new Color(255, 199, 0);
	private static final String UP = "UP";
	private static final String DOWN = "DOWN";

	private final RemindersPlugin plugin;
	private final RemindersConfig config;

	@Inject
	private RemindersOverlay(RemindersPlugin plugin, RemindersConfig config)
	{
		super(plugin);
		this.plugin = plugin;
		this.config = config;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final PanelMode mode = config.statusPanel();

		if (mode == PanelMode.OFF)
		{
			return null;
		}

		final Color upColour = config.upColour();
		final Color downColour = config.downColour();
		boolean any = false;

		for (Reminder reminder : plugin.getReminders())
		{
			if (!reminder.isRelevant())
			{
				continue;
			}

			final boolean up = reminder.isUp();

			if (up && mode == PanelMode.ONLY_WHEN_DOWN)
			{
				continue;
			}

			if (!any)
			{
				// Only worth a heading once there is something under it.
				panelComponent.getChildren().add(TitleComponent.builder()
					.text("Reminders")
					.color(TITLE_COLOR)
					.build());
				any = true;
			}

			panelComponent.getChildren().add(LineComponent.builder()
				.left(reminder.getName())
				.right(up ? UP : DOWN)
				.leftColor(up ? upColour : downColour)
				.rightColor(up ? upColour : downColour)
				.build());
		}

		return any ? super.render(graphics) : null;
	}
}
