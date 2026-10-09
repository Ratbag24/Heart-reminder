package com.ratbag24.reminders.visual;

import com.ratbag24.reminders.RemindersConfig;
import com.ratbag24.reminders.heart.SaturatedHeartReminder;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.inject.Inject;
import net.runelite.api.widgets.WidgetItem;
import net.runelite.client.ui.overlay.WidgetItemOverlay;

/**
 * Throbs the heart in the inventory while it is ready to be invigorated, so the
 * thing to click is the thing that is lit up.
 * <p>
 * Only the heart is highlighted: a thrall and vengeance are spells rather than
 * items, and there is nothing in the inventory to point at.
 */
public class ItemFlashOverlay extends WidgetItemOverlay
{
	/** How strongly the slot itself is washed, against the outline's full strength. */
	private static final double WASH = 0.35;

	private final SaturatedHeartReminder heartReminder;
	private final RemindersConfig config;

	@Inject
	private ItemFlashOverlay(SaturatedHeartReminder heartReminder, RemindersConfig config)
	{
		this.heartReminder = heartReminder;
		this.config = config;
		showOnInventory();
	}

	@Override
	public void renderItemOverlay(Graphics2D graphics, int itemId, WidgetItem widgetItem)
	{
		// Which items count, and whether anything should be lit at all, is the
		// heart reminder's business.
		if (!heartReminder.shouldFlashItem(itemId))
		{
			return;
		}

		final Rectangle bounds = widgetItem.getCanvasBounds();

		if (bounds == null)
		{
			return;
		}

		final long now = System.currentTimeMillis();
		final Color colour = config.flashColour();

		graphics.setColor(Pulse.fade(colour, now, WASH));
		graphics.fillRect(bounds.x, bounds.y, bounds.width, bounds.height);

		graphics.setColor(Pulse.fade(colour, now));
		graphics.drawRect(bounds.x, bounds.y, bounds.width - 1, bounds.height - 1);
	}
}
