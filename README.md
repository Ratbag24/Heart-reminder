# All-in-One Reminders

A RuneLite plugin for the buffs you have to keep re-applying. When one lapses it
tells you so in words, where you are already looking, with a toggle for every
reminder.

## What it tracks

| Reminder | Notes |
| --- | --- |
| Saturated / imbued heart | Plus a cooldown timer, and the heart lights up in your inventory |
| Thrall | |
| Vengeance | Ready once the cooldown ends *and* the held cast has rebounded |
| Divine potions | All eight varbits together, so one potion reminds once |
| Death charge | |
| Shadow veil | |
| Antifire | Plain and super together |
| Stamina | |
| Magic imbue | |
| Menaphite remedy | |

**Ward of Arceuus and Mark of Darkness are not here.** The game gives them no
varbit while they are up, so RuneLite's own timers work them out from a chat
message and a duration. That is a different mechanism from everything above, and
guessing at it would be worse than leaving them out.

## The alert banner

When something needs doing, a banner says what, in white on red:

```
+-----------------------------+
|  You need to death charge!  |
+-----------------------------+
```

Phrased as the thing to do rather than the state you are in — mid-fight, "You
need to death charge!" lands faster than "Death charge: down". It stacks if more
than one thing is down, and drags wherever suits.

It only speaks up about buffs you actually use. Not having cast vengeance is the
normal state for most of the game, so a reminder stays quiet until you have shown
you want it by casting it once.

## The rest of it

- A **notification** when something runs out, in whatever form you have set up in
  RuneLite's own notification settings.
- A **screen edge glow** that throbs while anything needs attention. It sits at
  the edge rather than washing the view — the middle of the screen is where the
  fight is.
- The **heart lights up in your inventory** until you use it.
- **Only while fighting**, on by default for the combat buffs, so nothing
  follows you round the bank.

The pulse is a slow sine, one cycle every 1.4 seconds, rather than a blink. A
hard flash at a few hertz is harder to ignore for everyone and a real problem for
photosensitive players.

## How the game state is read

Everything is driven by varbits rather than a stopwatch, so dying, hopping or
logging out cannot knock a reminder out of step. The first values read after a
login are *adopted* rather than treated as a change, which is what stops a
reminder firing every time you log in.

**The heart** uses `VarbitID.IMBUED_HEART_TIMER` (5361), which both hearts count
down in steps of ten game ticks. The step to zero is the "recharged" event; a
wall-clock estimate re-synced on every step drives the display countdown so it
moves once a second rather than in six second jumps, capped by the varbit so it
never reads ready early. Which heart it is comes from the length the cooldown
started at — 50 steps for the saturated heart's five minutes, 70 for the imbued
heart's seven. A cooldown already running at login stays unidentified rather than
guessed, and the panel just says "Heart".

Its boost and its cooldown run out together, so a heart still on cooldown counts
as **up**: the thing you would re-apply is still working.

**Everything else** is reported through varbits that are non-zero while any part
of the buff is running, so one implementation covers them all: the buff is gone
once *every* one of its varbits reaches zero. That matters in both directions —
vengeance needs both its cooldown and its rebound varbit clear, and a divine
potion sets its own timer plus one per stat, so watching all eight is what stops
one potion reminding four times.

## Building and running

Requires JDK 11 or newer.

```sh
./gradlew test     # run the unit tests
./gradlew run      # start RuneLite with this plugin loaded
```

Every push also builds and tests against `net.runelite:client` on GitHub Actions.

## Adding another reminder

For anything reported by varbits there is no new class to write:

1. Add a `BuffDefinition` to `Buffs`: its name, the notification message, and the
   varbits that are non-zero while it lasts.
2. Add a `@ConfigSection` for it in `RemindersConfig` with the usual four
   settings. Sections go up in tens so there is room to slot one in.
3. Register it in `RemindersPlugin.startUp()`.

Anything needing more than "is it there or not" — a countdown, a cooldown, an
item to check for — implements `Reminder` directly, as the heart does.

## Layout

```
src/main/java/com/ratbag24/reminders/
  RemindersPlugin.java        event plumbing and the reminder registry
  RemindersConfig.java        one @ConfigSection per reminder
  Reminder.java               what a reminder has to implement
  ReminderContext.java        services every reminder needs
  CombatTracker.java          shared "was I fighting recently" state
  buff/
    Buffs.java                    the nine buff definitions
    BuffDefinition.java           one buff as data
    BuffSettings.java             one buff's settings, read live
    BuffReminder.java             the reminder shared by every buff
    BuffState.java                buff tracking rules, no RuneLite deps
  visual/
    Pulse.java                    the shared throb curve, no RuneLite deps
    AlertBannerOverlay.java       the white-on-red "you need to X" banner
    ScreenFlashOverlay.java       the glow around the game view
    ItemFlashOverlay.java         the highlight on the heart in your inventory
  heart/
    SaturatedHeartReminder.java   the heart, which needs more than a buff does
    HeartCooldown.java            cooldown timing rules, no RuneLite deps
    HeartType.java                imbued, saturated or not yet known
    TrackedHearts.java            which hearts to fire for
    HeartInfoBox.java             the countdown infobox
```

`HeartCooldown`, `BuffState` and `Pulse` deliberately have no RuneLite imports:
the timing rules are the part most likely to be subtly wrong, so they are kept
testable on their own. Fifty tests cover the login sync, the death reset, early
re-use, heart identification, the display countdown, vengeance's two varbits, one
divine potion reminding once rather than four times, and the pulse curve.
