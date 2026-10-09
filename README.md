# All-in-One Reminders

A RuneLite plugin for the "don't forget to re-apply that" reminders, with a
switch for each one so they live in a single plugin instead of a dozen.

Four reminders so far: the **saturated heart**, **thralls**, **vengeance** and
**divine potions**.

## How a reminder behaves

Every reminder works the same way, because missing a notification should not be
the end of it:

- A **notification** the moment the thing runs out, in whatever form you have
  set up in RuneLite's own notification settings.
- An **on-screen reminder** that stays up for as long as the buff is missing,
  listed in one shared panel rather than a box each.
- A **visual**: a coloured glow throbs around the edge of the game view while
  anything is asking for attention, and the heart itself throbs in your
  inventory while it is ready to invigorate.
- **Only while fighting**, on by default, so nothing follows you round the bank.
  Combat means having dealt or taken damage in the last ten seconds.

Nothing is said about a buff you have not used since logging in. Not having
cast vengeance is the normal state for most of the game, so the reminder only
starts caring once you have shown you want it by casting it once.

## The reminders

### Saturated heart

The saturated heart boosts Magic by 4 + 10% of your level for five minutes and
cannot be used again until those five minutes are up. The imbued heart it is
upgraded from gives 1 + 10% on a seven minute cooldown. Both count down the
*same* varbit, so one reminder covers either, and the plugin tells them apart by
how long the cooldown started out.

On top of the shared behaviour it adds a countdown infobox that turns green for
the last 30 seconds, an option to only track the saturated heart, and an option
to stay quiet unless a heart is actually in your inventory.

### Thrall

A thrall lasts one tick per Magic level, doubled at Grandmaster combat
achievement tier. Its varbit drops to zero the moment it leaves, which is when
the reminder fires.

### Vengeance

Vengeance can only be re-cast once two things are true: the cooldown has run
out, *and* the cast you were holding has rebounded. Both are watched, so the
reminder waits for whichever happens second rather than firing early.

Its "only while fighting" default means you hear about it mid-fight. Turn that
off if you would rather be reminded before a fight starts.

### Divine potions

One divine potion sets its own timer and one per stat it boosts, so all eight
divine varbits are watched together and the reminder fires when the last of them
reaches zero. Reminding as each stat timer ran out would fire four times for one
divine super combat potion.

## Settings

A **General** section holds the settings shared by everything: *Send chat
messages* (off by default), *Flash the screen edge* (on), and the *Flash colour*
used by both the screen edge and the item highlight.

The pulse is a slow sine rather than a blink — one cycle every 1.4 seconds. A
hard on/off flash at a few hertz is harder to ignore for everyone and a real
problem for photosensitive players; this reads as "look here" without strobing.
Turning the colour's alpha down makes it subtler, and the toggle turns it off.

Every reminder then gets a section with *Enable*, *Notify*, *Show on-screen
reminder* and *Only while fighting*. The saturated heart adds three of its own:

| Setting | Default | What it does |
| --- | --- | --- |
| Hearts to track | Imbued and saturated | Restrict it to the saturated heart's shorter cooldown if you have both. |
| Show cooldown timer | on | Infobox counting the cooldown down. |
| Flash the heart in your inventory | on | Throb the heart itself while it is ready, so the thing to click is lit up. |
| Only when carrying one | on | Stay quiet when no heart is in your inventory. |

## How the game state is read

Everything is driven by varbits rather than by a stopwatch, so a death, a world
hop or a logout cannot leave a reminder out of step with the game. For every
reminder, the first values read after a login are *adopted* rather than treated
as a change — that is what stops a reminder firing every time you log in.

**The heart** uses `VarbitID.IMBUED_HEART_TIMER` (varbit 5361), the same varbit
RuneLite's own Timers plugin uses for its "Imbued/Saturated heart" timer. It
holds the remaining cooldown in steps of ten game ticks, which gives two things:

- **The event.** The step down to zero is the authoritative "it has recharged"
  moment.
- **The countdown.** Ten ticks is six seconds, so the varbit alone would tick
  down in six second jumps. A wall-clock estimate, re-synchronised every time
  the varbit moves, drives the display instead, and is capped by the varbit so
  it can never claim the heart is ready before the game does.

Which heart is on cooldown comes from the length it started at: 50 steps is five
minutes (saturated), 70 is seven (imbued). When the cooldown was already running
at login, neither can be ruled out, and the heart stays unidentified rather than
guessed at — the reminder still fires, it just says "Heart" instead of naming it.
The game's own `SATURATED_HEART_TIME` varbit is watched as a second opinion, and
will name a heart the cooldown length could not.

**Everything else** is a buff that is either there or not, reported through one
or more varbits that are non-zero while any part of it is running. That one
shape covers thralls, vengeance and divine potions alike, so they share an
implementation: the buff counts as gone once *every* one of its varbits has
reached zero.

## Building and running

Requires JDK 11 or newer.

```sh
./gradlew test     # run the unit tests
./gradlew run      # start RuneLite with this plugin loaded
```

`./gradlew run` launches a developer-mode client, which is how to try the plugin
before it is anywhere near the Plugin Hub. Every push also builds and tests
against `net.runelite:client` on GitHub Actions.

## Adding another reminder

For anything that is a buff reported by varbits — most of them — there is no new
class to write:

1. Add a `BuffDefinition` to `Buffs`: its name, the message for the
   notification, the line for the on-screen panel, and the varbits that are
   non-zero while it lasts.
2. Add a `@ConfigSection` for it in `RemindersConfig` with the usual four
   settings. Sections are positioned in tens so there is room to slot one in.
3. Register it in `RemindersPlugin.startUp()`.

Anything that needs more than "is it there or not" — a countdown, a cooldown, an
item to check for — implements `Reminder` directly, as the heart does. Only
`getName()`, `isEnabled()` and the events it cares about need filling in;
`getReminderText()` returns what the overlay should say, or `null` for nothing.

`RemindersPlugin` subscribes to the client's events once and fans them out, so a
new reminder never touches the event plumbing. `ReminderContext` hands every
reminder the client, notifications and the shared combat tracker.

## Layout

```
src/main/java/com/ratbag24/reminders/
  RemindersPlugin.java        event plumbing and the reminder registry
  RemindersConfig.java        one @ConfigSection per reminder
  RemindersOverlay.java       the shared on-screen panel
  Reminder.java               what a reminder has to implement
  ReminderContext.java        services every reminder needs
  CombatTracker.java          shared "was I fighting recently" state
  buff/
    Buffs.java                    the buff definitions: thrall, vengeance, divine
    BuffDefinition.java           one buff as data
    BuffSettings.java             one buff's settings, read live
    BuffReminder.java             the reminder shared by every buff
    BuffState.java                buff tracking rules, no RuneLite deps
  visual/
    Pulse.java                    the shared throb curve, no RuneLite deps
    ScreenFlashOverlay.java       the glow around the game view
    ItemFlashOverlay.java         the highlight on the heart in your inventory
  heart/
    SaturatedHeartReminder.java   the heart, which needs more than a buff does
    HeartCooldown.java            cooldown timing rules, no RuneLite deps
    HeartType.java                imbued, saturated or not yet known
    TrackedHearts.java            which hearts to fire for
    HeartInfoBox.java             the countdown infobox
```

`HeartCooldown`, `BuffState` and `Pulse` deliberately have no RuneLite imports: the
timing rules are the part most likely to be subtly wrong, so they are kept
testable on their own. Between them `HeartCooldownTest`, `BuffStateTest` and
`PulseTest` cover the login sync, the death reset, early re-use, heart
identification, the display countdown, vengeance's two varbits, one divine
potion reminding once rather than four times, and the pulse curve staying in
bounds and meeting itself smoothly at the seam.
