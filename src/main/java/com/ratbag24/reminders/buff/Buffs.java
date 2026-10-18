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
 * The first varbit on each one is its cooldown, where the game keeps one. It
 * does double duty. A buff can be gone while the spell that applies it is still
 * unavailable, and telling someone to cast something they cannot cast is worse
 * than saying nothing; and because a cooldown only ever starts on the caster,
 * seeing it start is what proves the buff was the player's own doing rather
 * than a teammate's. See {@link CastProof}.
 */
public final class Buffs
{
	/**
	 * A thrall lasts one tick per Magic level, doubled at Grandmaster combat
	 * achievement tier, and its varbit drops to zero the moment it leaves.
	 */
	public static final BuffDefinition THRALL = new BuffDefinition(
		"Your thrall has expired.",
		"You need a thrall!",
		VarbitID.ARCEUUS_RESURRECTION_COOLDOWN,
		VarbitID.ARCEUUS_RESURRECTION_ACTIVE);

	/**
	 * Vengeance can only be re-cast once its cooldown has run out <em>and</em>
	 * the cast it is holding has rebounded, so both have to be clear before
	 * there is anything worth saying.
	 * <p>
	 * The rebound varbit on its own is not enough to go on: Vengeance Other
	 * sets it on a player who did not cast anything, and may be on a spellbook
	 * with no vengeance to cast. Only the cooldown says the cast was theirs.
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
		CastProof.NO_COOLDOWN,
		VarbitID.DIVINECOMBAT_POTION_TIME,
		VarbitID.DIVINEATTACK_POTION_TIME,
		VarbitID.DIVINESTRENGTH_POTION_TIME,
		VarbitID.DIVINEDEFENCE_POTION_TIME,
		VarbitID.DIVINERANGE_POTION_TIME,
		VarbitID.DIVINEMAGIC_POTION_TIME,
		VarbitID.DIVINEBASTION_POTION_TIME,
		VarbitID.DIVINEBATTLEMAGE_POTION_TIME);

	/**
	 * Death charge is spent on kills rather than by running down a clock — one
	 * kill, or two with the Book of the Dead — and the spell then sits on a
	 * minute's cooldown regardless.
	 * <p>
	 * Watching the charge alone announced "you need to death charge" the moment
	 * the last charge was used, a minute before it could actually be cast. How
	 * many charges a cast carries makes no difference here: the varbit is
	 * non-zero while any remain, so one charge or two behave the same.
	 */
	public static final BuffDefinition DEATH_CHARGE = new BuffDefinition(
		"Your death charge is ready to cast again.",
		"You need to death charge!",
		VarbitID.ARCEUUS_DEATH_CHARGE_COOLDOWN,
		VarbitID.ARCEUUS_DEATH_CHARGE_ACTIVE);

	/** Shadow veil, for thieving, with its cooldown watched the same way. */
	public static final BuffDefinition SHADOW_VEIL = new BuffDefinition(
		"Your shadow veil has expired.",
		"You need shadow veil!",
		VarbitID.ARCEUUS_SHADOW_VEIL_COOLDOWN,
		VarbitID.ARCEUUS_SHADOW_VEIL_ACTIVE);

	/**
	 * Both antifire varbits together: a super antifire running out while an
	 * ordinary one is still going is not the moment to warn about dragonfire.
	 */
	public static final BuffDefinition ANTIFIRE = new BuffDefinition(
		"Your antifire has run out.",
		"You need antifire!",
		CastProof.NO_COOLDOWN,
		VarbitID.ANTIFIRE_POTION,
		VarbitID.SUPER_ANTIFIRE_POTION);

	/** Stamina, which is about running rather than fighting. */
	public static final BuffDefinition STAMINA = new BuffDefinition(
		"Your stamina potion has run out.",
		"You need stamina!",
		CastProof.NO_COOLDOWN,
		VarbitID.STAMINA_DURATION);

	/**
	 * Magic imbue, a Lunar spell, for the runes it saves on superheating. The
	 * game keeps no cooldown varbit for it, so there is nothing to prove the
	 * cast with — but nobody else can cast it on you either.
	 */
	public static final BuffDefinition MAGIC_IMBUE = new BuffDefinition(
		"Your magic imbue has run out.",
		"You need magic imbue!",
		CastProof.NO_COOLDOWN,
		VarbitID.MAGIC_IMBUE_ACTIVE);

	/** Menaphite remedy, which keeps stats topped up while it lasts. */
	public static final BuffDefinition MENAPHITE_REMEDY = new BuffDefinition(
		"Your menaphite remedy has worn off.",
		"You need a menaphite remedy!",
		CastProof.NO_COOLDOWN,
		VarbitID.STATRENEWAL_POTION_TIMER);

	private Buffs()
	{
	}
}
