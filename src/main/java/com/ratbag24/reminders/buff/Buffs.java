package com.ratbag24.reminders.buff;

import net.runelite.api.gameval.VarbitID;

/**
 * The buffs the plugin reminds about, beyond the heart.
 * <p>
 * Each one is watched through the varbits the game keeps for it, and is treated
 * as gone once every one of them has reached zero.
 */
public final class Buffs
{
	/**
	 * A thrall lasts one tick per Magic level, doubled at Grandmaster combat
	 * achievement tier, and its varbit drops to zero the moment it leaves.
	 */
	public static final BuffDefinition THRALL = new BuffDefinition(
		"Thrall",
		"Your thrall has expired.",
		"No thrall",
		VarbitID.ARCEUUS_RESURRECTION_ACTIVE);

	/**
	 * Vengeance can only be re-cast once its cooldown has run out <em>and</em>
	 * the cast it is holding has rebounded, so both have to be clear before
	 * there is anything worth saying.
	 */
	public static final BuffDefinition VENGEANCE = new BuffDefinition(
		"Vengeance",
		"Vengeance is ready to cast.",
		"Vengeance ready",
		VarbitID.VENGEANCE_TIMELIMIT,
		VarbitID.VENGEANCE_REBOUND);

	/**
	 * A single divine potion sets its own timer and one per stat it boosts, so
	 * all of them are watched together: reminding as each stat timer ran out
	 * would fire several times for one potion.
	 */
	public static final BuffDefinition DIVINE_POTION = new BuffDefinition(
		"Divine potion",
		"Your divine potion has worn off.",
		"No divine boost",
		VarbitID.DIVINECOMBAT_POTION_TIME,
		VarbitID.DIVINEATTACK_POTION_TIME,
		VarbitID.DIVINESTRENGTH_POTION_TIME,
		VarbitID.DIVINEDEFENCE_POTION_TIME,
		VarbitID.DIVINERANGE_POTION_TIME,
		VarbitID.DIVINEMAGIC_POTION_TIME,
		VarbitID.DIVINEBASTION_POTION_TIME,
		VarbitID.DIVINEBATTLEMAGE_POTION_TIME);

	private Buffs()
	{
	}
}
