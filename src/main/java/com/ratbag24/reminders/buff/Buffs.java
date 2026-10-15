package com.ratbag24.reminders.buff;

import net.runelite.api.gameval.VarbitID;

/**
 * The buffs the plugin reminds about, beyond the heart.
 * <p>
 * Each one is watched through the varbits the game keeps for it, and is treated
 * as gone once every one of them has reached zero. Adding another is a constant
 * here, a config section, and a line in the plugin's registry.
 * <p>
 * Ward of Arceuus and Mark of Darkness are deliberately absent: the game gives
 * them no varbit while they are up, so RuneLite's own timers work them out from
 * chat messages and a duration. That is a different mechanism from everything
 * here, and guessing at it would be worse than leaving them out.
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
		"You need a thrall!",
		VarbitID.ARCEUUS_RESURRECTION_ACTIVE);

	/**
	 * Vengeance can only be re-cast once its cooldown has run out <em>and</em>
	 * the cast it is holding has rebounded, so both have to be clear before
	 * there is anything worth saying.
	 */
	public static final BuffDefinition VENGEANCE = new BuffDefinition(
		"Vengeance",
		"Vengeance is ready to cast.",
		"You need vengeance!",
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
		"You need a divine potion!",
		VarbitID.DIVINECOMBAT_POTION_TIME,
		VarbitID.DIVINEATTACK_POTION_TIME,
		VarbitID.DIVINESTRENGTH_POTION_TIME,
		VarbitID.DIVINEDEFENCE_POTION_TIME,
		VarbitID.DIVINERANGE_POTION_TIME,
		VarbitID.DIVINEMAGIC_POTION_TIME,
		VarbitID.DIVINEBASTION_POTION_TIME,
		VarbitID.DIVINEBATTLEMAGE_POTION_TIME);

	/** Death charge lasts a tick per Magic level and is worth re-casting at once. */
	public static final BuffDefinition DEATH_CHARGE = new BuffDefinition(
		"Death charge",
		"Your death charge has expired.",
		"You need to death charge!",
		VarbitID.ARCEUUS_DEATH_CHARGE_ACTIVE);

	/** Shadow veil, for thieving and for staying unseen. */
	public static final BuffDefinition SHADOW_VEIL = new BuffDefinition(
		"Shadow veil",
		"Your shadow veil has expired.",
		"You need shadow veil!",
		VarbitID.ARCEUUS_SHADOW_VEIL_ACTIVE);

	/**
	 * Both antifire varbits together: a super antifire running out while an
	 * ordinary one is still going is not the moment to warn about dragonfire.
	 */
	public static final BuffDefinition ANTIFIRE = new BuffDefinition(
		"Antifire",
		"Your antifire has run out.",
		"You need antifire!",
		VarbitID.ANTIFIRE_POTION,
		VarbitID.SUPER_ANTIFIRE_POTION);

	/** Stamina, which is about running rather than fighting. */
	public static final BuffDefinition STAMINA = new BuffDefinition(
		"Stamina",
		"Your stamina potion has run out.",
		"You need stamina!",
		VarbitID.STAMINA_DURATION);

	/** Magic imbue, for the runes it saves on alchemy and superheating. */
	public static final BuffDefinition MAGIC_IMBUE = new BuffDefinition(
		"Magic imbue",
		"Your magic imbue has run out.",
		"You need magic imbue!",
		VarbitID.MAGIC_IMBUE_ACTIVE);

	/** Menaphite remedy, which keeps stats topped up while it lasts. */
	public static final BuffDefinition MENAPHITE_REMEDY = new BuffDefinition(
		"Menaphite remedy",
		"Your menaphite remedy has worn off.",
		"You need a menaphite remedy!",
		VarbitID.STATRENEWAL_POTION_TIMER);

	private Buffs()
	{
	}
}
