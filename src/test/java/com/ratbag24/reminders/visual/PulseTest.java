package com.ratbag24.reminders.visual;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.awt.Color;
import org.junit.Test;

public class PulseTest
{
	private static final double EPSILON = 0.0001;

	@Test
	public void theCycleStartsAtItsDimmest()
	{
		assertEquals(0.35, Pulse.strength(0L), EPSILON);
	}

	@Test
	public void theCyclePeaksHalfWayThrough()
	{
		assertEquals(1.0, Pulse.strength(Pulse.PERIOD_MILLIS / 2), EPSILON);
	}

	@Test
	public void theCycleRepeats()
	{
		assertEquals(Pulse.strength(0L), Pulse.strength(Pulse.PERIOD_MILLIS), EPSILON);
		assertEquals(Pulse.strength(123L), Pulse.strength(Pulse.PERIOD_MILLIS + 123L), EPSILON);
	}

	@Test
	public void theCycleMeetsItselfSmoothly()
	{
		// A jump at the seam would read as a blink rather than a throb.
		final double justBefore = Pulse.strength(Pulse.PERIOD_MILLIS - 1L);
		final double atTheSeam = Pulse.strength(Pulse.PERIOD_MILLIS);

		assertTrue("seam jumped by " + Math.abs(justBefore - atTheSeam),
			Math.abs(justBefore - atTheSeam) < 0.001);
	}

	@Test
	public void strengthStaysWithinItsBounds()
	{
		for (long t = 0; t < Pulse.PERIOD_MILLIS * 3; t += 7)
		{
			final double strength = Pulse.strength(t);
			assertTrue("under at " + t, strength >= 0.35 - EPSILON);
			assertTrue("over at " + t, strength <= 1.0 + EPSILON);
		}
	}

	@Test
	public void aClockReadingNegativeStillPulses()
	{
		for (long t = -5_000L; t < 0; t += 13)
		{
			final double strength = Pulse.strength(t);
			assertTrue("out of bounds at " + t, strength >= 0.35 - EPSILON && strength <= 1.0 + EPSILON);
		}
	}

	@Test
	public void fadingScalesAlphaAndLeavesTheColourAlone()
	{
		final Color base = new Color(255, 0, 40, 200);
		final Color faded = Pulse.fade(base, Pulse.PERIOD_MILLIS / 2);

		assertEquals(255, faded.getRed());
		assertEquals(0, faded.getGreen());
		assertEquals(40, faded.getBlue());
		assertEquals(200, faded.getAlpha());
	}

	@Test
	public void fadingAtTheTroughIsFainterThanAtThePeak()
	{
		final Color base = new Color(255, 0, 40, 200);

		assertTrue(Pulse.fade(base, 0L).getAlpha() < Pulse.fade(base, Pulse.PERIOD_MILLIS / 2).getAlpha());
	}

	@Test
	public void falloffDimsItFurther()
	{
		final Color base = new Color(255, 0, 40, 200);
		final long peak = Pulse.PERIOD_MILLIS / 2;

		assertEquals(100, Pulse.fade(base, peak, 0.5).getAlpha());
		assertEquals(0, Pulse.fade(base, peak, 0.0).getAlpha());
	}

	@Test
	public void falloffOutsideItsRangeIsClamped()
	{
		final Color base = new Color(255, 0, 40, 200);
		final long peak = Pulse.PERIOD_MILLIS / 2;

		assertEquals(200, Pulse.fade(base, peak, 5.0).getAlpha());
		assertEquals(0, Pulse.fade(base, peak, -1.0).getAlpha());
	}

	@Test
	public void aFullyTransparentColourStaysTransparent()
	{
		final Color invisible = new Color(255, 0, 40, 0);

		assertEquals(0, Pulse.fade(invisible, Pulse.PERIOD_MILLIS / 2).getAlpha());
	}
}
