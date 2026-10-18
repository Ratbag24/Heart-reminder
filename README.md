# All-in-One Reminders

A RuneLite plugin for the buffs you have to keep re-applying. When one lapses it
tells you so in words, where you are already looking, with a toggle for every
reminder.

## What it tracks

Twelve buffs, each switchable on its own.

- **Saturated / imbued heart** — also gets a cooldown timer, and lights up in
  your inventory when it is ready
- **Thrall** — lasts a tick per Magic level, doubled at Grandmaster combat
  achievement tier, and its cooldown is watched too
- **Vengeance** — ready once the cooldown ends *and* the held cast has rebounded,
  so it waits for whichever happens second
- **Divine potions** — all eight varbits watched together, so one potion reminds
  once rather than four times
- **Death charge** — spent on kills rather than by running down (one, or two
  with the Book of the Dead), so the reminder waits out the spell's minute of
  cooldown before asking again
- **Shadow veil** — for thieving, so it is not combat-gated by default; its
  cooldown is watched too
- **Antifire** — plain and super watched together, so a super running out under
  an ordinary one says nothing
- **Stamina** — about running rather than fighting, so not combat-gated by
  default
- **Magic imbue** — for the runes it saves on alchemy and superheating, so not
  combat-gated by default
- **Menaphite remedy** — keeps your stats topped up while it lasts
- **Mark of Darkness** — timed from the chat message, see below
- **Ward of Arceuus** — timed from the chat message, see below

**Mark of Darkness and Ward of Arceuus work differently.** The game gives them no
varbit at all — the only sign either was cast is a line of chat. So they are
timed the way RuneLite's own timers do it: hear the cast, run a clock for one
tick per Magic level, remind when it runs out.

That is less certain than a varbit, in two ways worth knowing:

- The duration is a rule about the spell rather than something the game
  confirms, so a change to the spell would silently make it wrong.
- Logging in with one already running is invisible, because the chat line came
  and went before you arrived. Tracking starts at your next cast, and until then
  nothing is claimed either way — it stays quiet rather than announcing an expiry
  it cannot actually know about.

Everything else in the table is read straight from a varbit and has neither
problem.

## The alert banner

When something needs doing, a banner says what, in white on red:

```
+-----------------------------+
|  You need to death charge!  |
+-----------------------------+
```

Phrased as the thing to do rather than the state you are in — mid-fight, "You
need to death charge!" lands faster than "Death charge: down". It stacks if more
than one thing is down.

**Drag it wherever you want it.** Same for the heart's cooldown timer — both are
ordinary RuneLite overlays, so you move them by dragging and the client
remembers where you put them. Its colour and its size are settings too, so
nothing about it has to be taken as given.

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

## Settings

**General** — whether the alert banner shows and how big; whether the screen
edge flashes and in what colour; and whether reminders also print in the chat
box.

**Every reminder** has the same five: *Enable*, *Notify*, *Show in alert banner*,
*Only while fighting* and its own *Banner colour*. So a reminder can be kept but
quietened — notification only, no banner — rather than switched off entirely, and
the ones that matter most to whatever you are doing can be given a colour that
stands out from the rest.

**The heart** adds four of its own: which hearts to track, whether to show the
cooldown timer, whether to light the heart up in your inventory, and whether to
stay quiet unless you are actually carrying one.

The banner and the cooldown timer are ordinary RuneLite overlays, so drag them
wherever you want and the client remembers.

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
once *every* one of its varbits reaches zero. Where a spell has a cooldown, that
varbit is watched alongside the one saying the buff is up, because a buff can be
gone while the spell that applies it is still unavailable — and telling someone
to cast something they cannot cast is worse than saying nothing. That matters in both directions —
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
  chat/
    ChatBuffs.java                Mark of Darkness and Ward of Arceuus
    ChatBuffDefinition.java       one chat-announced buff as data
    ChatBuffReminder.java         hears the cast, times the rest
    TimedBuffState.java           the clock, no RuneLite deps
  buff/
    Buffs.java                    the nine varbit-backed buff definitions
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

`HeartCooldown`, `BuffState`, `TimedBuffState` and `Pulse` deliberately have no
RuneLite imports:
the timing rules are the part most likely to be subtly wrong, so they are kept
testable on their own. Sixty-one tests cover the login sync, the death reset, early
re-use, heart identification, the display countdown, vengeance's two varbits, one
divine potion reminding once rather than four times, and the pulse curve.
