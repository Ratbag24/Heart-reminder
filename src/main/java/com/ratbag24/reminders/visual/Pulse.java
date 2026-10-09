package com.ratbag24.reminders.visual;

import java.awt.Color;

/**
 * The slow throb shared by every visual the plugin draws, so the screen edge
 * and a highlighted item rise and fall together rather than beating against
 * each other.
 * <p>
 * It is a smooth sine rather than a blink, and deliberately slow. A hard
 * on/off flash at a few hertz is both harder to ignore for everyone and a
 * genuine problem for photosensitive players; one cycle every 1.4 seconds
 * reads as "look here" without strobing.
 * <p>
 * No RuneLite dependencies, so the curve can be unit tested.
 */
public final class Pulse
{
	/** One full dim-bright-dim cycle, in milliseconds. */
	public static final long PERIOD_MILLIS = 1400L;

	/** How faint the pulse goes at its dimmest, as a fraction of full strength. */
	private static final double TROUGH = 0.35;

	/**
	 * The pulse strength at a moment in time, between {@link #TROUGH} and 1.
	 *
	 * @param nowMillis the current wall clock time
	 */
	public static double strength(long nowMillis)
	{
		// floorMod keeps the phase correct for a clock that reads negative.
		final double phase = Math.floorMod(nowMillis, PERIOD_MILLIS) / (double) PERIOD_MILLIS;

		// A raised cosine: starts at the trough, peaks half way through, and
		// meets itself smoothly at the end of the cycle.
		final double wave = (1.0 - Math.cos(phase * 2.0 * Math.PI)) / 2.0;

		return TROUGH + (1.0 - TROUGH) * wave;
	}

	/**
	 * The given colour with its alpha scaled by the pulse, and optionally by a
	 * further factor for a soft edge.
	 *
	 * @param base      the colour to pulse, whose own alpha sets the maximum
	 * @param nowMillis the current wall clock time
	 * @param falloff   an extra multiplier between 0 and 1
	 */
	public static Color fade(Color base, long nowMillis, double falloff)
	{
		final double scale = strength(nowMillis) * clamp(falloff);
		final int alpha = (int) Math.round(base.getAlpha() * scale);

		return new Color(base.getRed(), base.getGreen(), base.getBlue(), clampAlpha(alpha));
	}

	public static Color fade(Color base, long nowMillis)
	{
		return fade(base, nowMillis, 1.0);
	}

	private static double clamp(double value)
	{
		return Math.max(0.0, Math.min(1.0, value));
	}

	private static int clampAlpha(int alpha)
	{
		return Math.max(0, Math.min(255, alpha));
	}

	private Pulse()
	{
	}
}
