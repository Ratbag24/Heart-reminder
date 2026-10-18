package com.ratbag24.reminders;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SpellbookLockTest
{
	// Deliberately not 0-3: the point of learning the value is that the plugin
	// never has to know what the game's numbering happens to be.
	private static final int ARCEUUS = 7;
	private static final int STANDARD = 4;

	private SpellbookLock lock;

	@Before
	public void setUp()
	{
		lock = new SpellbookLock();
	}

	@Test
	public void beforeAnyCastItGetsInTheWayOfNothing()
	{
		assertFalse(lock.isLearned());
		assertTrue(lock.allows(STANDARD));
		assertTrue(lock.allows(ARCEUUS));
	}

	@Test
	public void castingTeachesItWhichSpellbookTheSpellLivesOn()
	{
		lock.learn(ARCEUUS);

		assertTrue(lock.isLearned());
		assertTrue(lock.allows(ARCEUUS));
	}

	@Test
	public void switchingAwayQuietensIt()
	{
		lock.learn(ARCEUUS);

		assertFalse(lock.allows(STANDARD));
	}

	@Test
	public void switchingBackLetsItSpeakAgain()
	{
		lock.learn(ARCEUUS);
		assertFalse(lock.allows(STANDARD));

		assertTrue(lock.allows(ARCEUUS));
	}

	@Test
	public void aLaterCastFromElsewhereMovesIt()
	{
		// If a spell ever moved spellbook, the next cast re-teaches it rather
		// than leaving the reminder stuck on a book it is no longer cast from.
		lock.learn(ARCEUUS);
		lock.learn(STANDARD);

		assertTrue(lock.allows(STANDARD));
		assertFalse(lock.allows(ARCEUUS));
	}

	@Test
	public void loggingOutForgetsIt()
	{
		lock.learn(ARCEUUS);
		lock.reset();

		assertFalse(lock.isLearned());
		assertTrue(lock.allows(STANDARD));
	}

	@Test
	public void anyNumberingTheGameUsesWorks()
	{
		// Whatever the game's values are, only equality is ever relied on.
		for (int book : new int[]{0, 1, 2, 3, 17, -5, Integer.MAX_VALUE})
		{
			SpellbookLock l = new SpellbookLock();
			l.learn(book);
			assertTrue("same book should pass: " + book, l.allows(book));
			assertFalse("other book should not: " + book, l.allows(book - 1));
		}
	}
}
