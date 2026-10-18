package com.ratbag24.reminders.buff;

import net.runelite.api.gameval.VarbitID;

/**
 * The buffs the plugin reminds about, beyond the heart.
 * <p>
 * Each one is watched through the varbits the game keeps for it, and is treated
 * as gone once every one of them has reached zero. Adding another is a constant
 * here, a config section, and a line in the plugin's registry.
 * <p>
 * Ward of Arceuus and Mark of Darkness live in the chat package instead: the
 * game gives them no varbit while they are up, so they have to be timed from
 * the chat line that announces them.
 * <p>
 * Where a spell has a cooldown of its own, that varbit is watched alongside the
 * one saying the buff is up. A buff can be gone while the spell that applies it
 * is still unavailable, and telling someone to cast something they cannot cast
 * is worse than saying nothing.
 */
public final class Buffs
{
	/**
	 * A thrall lasts one tick per Magic level, doubled at Grandmaster combat
	 * achievement tier, and its varbit drops to zero the moment it leaves.
	 * <p>
	 * The cooldown is watched alongside it so the reminder cannot ask for a
	 * thrall that cannot yet be summoned.
	 */
	public static final BuffDefinition THRALL = new BuffDefinition(
		"Your thrall has expired.",
		"You need a thrall!",
		VarbitID.ARCEUUS_RESURRECTION_ACTIVE,
		VarbitID.ARCEUUS_RESURRECTION_COOLDOWN);

	/**
	 * Vengeance can only be re-cast once its cooldown has run out <em>and</em>
	 * the cast it is holding has rebounded, so both have to be clear before
	 * there is anything worth saying.
	 */
	public static final BuffDefinition VENGEANCE = new BuffDefinition(
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

	/**
	 * Death charge is spent on a kill rather than running down a clock, and the
	 * spell then sits on a minute's cooldown regardless. Watching the charge
	 * alone announced "you need to death charge" the moment a kill used it up,
	 * a minute before it could actually be cast; the cooldown is watched with
	 * it so the reminder waits until the spell is available.
	 */
	public static final BuffDefinition DEATH_CHARGE = new BuffDefinition(
		"Your death charge is ready to cast again.",
		"You need to death charge!",
		VarbitID.ARCEUUS_DEATH_CHARGE_ACTIVE,
		VarbitID.ARCEUUS_DEATH_CHARGE_COOLDOWN);

	/** Shadow veil, for thieving, with its cooldown watched the same way. */
	public static final BuffDefinition SHADOW_VEIL = new BuffDefinition(
		"Your shadow veil has expired.",
		"You need shadow veil!",
		VarbitID.ARCEUUS_SHADOW_VEIL_ACTIVE,
		VarbitID.ARCEUUS_SHADOW_VEIL_COOLDOWN);

	/**
	 * Both antifire varbits together: a super antifire running out while an
	 * ordinary one is still going is not the moment to warn about dragonfire.
	 */
	public static final BuffDefinition ANTIFIRE = new BuffDefinition(
		"Your antifire has run out.",
		"You need antifire!",
		VarbitID.ANTIFIRE_POTION,
		VarbitID.SUPER_ANTIFIRE_POTION);

	/** Stamina, which is about running rather than fighting. */
	public static final BuffDefinition STAMINA = new BuffDefinition(
		"Your stamina potion has run out.",
		"You need stamina!",
		VarbitID.STAMINA_DURATION);

	/** Magic imbue, for the runes it saves on alchemy and superheating. */
	public static final BuffDefinition MAGIC_IMBUE = new BuffDefinition(
		"Your magic imbue has run out.",
		"You need magic imbue!",
		VarbitID.MAGIC_IMBUE_ACTIVE);

	/** Menaphite remedy, which keeps stats topped up while it lasts. */
	public static final BuffDefinition MENAPHITE_REMEDY = new BuffDefinition(
		"Your menaphite remedy has worn off.",
		"You need a menaphite remedy!",
		VarbitID.STATRENEWAL_POTION_TIMER);

	private Buffs()
	{
	}
}
