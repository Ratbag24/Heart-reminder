package com.ratbag24.reminders;

import net.runelite.client.RuneLite;
import net.runelite.client.externalplugins.ExternalPluginManager;

/**
 * Starts RuneLite with this plugin loaded, for testing it in a live client:
 * {@code ./gradlew run}
 */
public class RemindersPluginTest
{
	public static void main(String[] args) throws Exception
	{
		ExternalPluginManager.loadBuiltin(RemindersPlugin.class);
		RuneLite.main(args);
	}
}
