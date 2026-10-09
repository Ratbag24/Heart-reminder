package com.ratbag24.reminders;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.util.List;
import javax.inject.Inject;
import net.runelite.client.ui.overlay.OverlayPanel;
import net.runelite.client.ui.overlay.OverlayPosition;
import net.runelite.client.ui.overlay.components.LineComponent;
import net.runelite.client.ui.overlay.components.TitleComponent;

/**
 * Lists whatever the enabled reminders currently want to say. One panel is
 * shared by every reminder so that a handful of them cannot bury the screen in
 * separate boxes.
 */
class RemindersOverlay extends OverlayPanel
{
	private static final Color TITLE_COLOR = new Color(255, 199, 0);

	private final RemindersPlugin plugin;

	@Inject
	private RemindersOverlay(RemindersPlugin plugin)
	{
		super(plugin);
		this.plugin = plugin;
		setPosition(OverlayPosition.TOP_LEFT);
		setPriority(PRIORITY_HIGH);
	}

	@Override
	public Dimension render(Graphics2D graphics)
	{
		final List<String> reminders = plugin.getActiveReminderTexts();

		if (reminders.isEmpty())
		{
			return null;
		}

		panelComponent.getChildren().add(TitleComponent.builder()
			.text("Reminders")
			.color(TITLE_COLOR)
			.build());

		for (String reminder : reminders)
		{
			panelComponent.getChildren().add(LineComponent.builder()
				.left(reminder)
				.build());
		}

		return super.render(graphics);
	}
}
