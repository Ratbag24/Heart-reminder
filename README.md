# All-in-One Reminders

A RuneLite plugin for the "don't forget to re-apply that" reminders, with a
switch for each one so they can live in a single plugin instead of a dozen.

The first reminder is the **saturated heart**. Thralls and the rest come later,
and the plugin is built so that adding one means writing a class, not rewiring
anything.

## The saturated heart reminder

The saturated heart boosts Magic by 4 + 10% of your level for five minutes, and
cannot be used again until those five minutes are up. The imbued heart it is
upgraded from gives 1 + 10% on a seven minute cooldown. Both count down the
*same* game varbit, so one reminder covers either, and the plugin can tell them
apart by how long the cooldown started out.

What it does:

- Counts the cooldown down in an infobox, turning green in the last 30 seconds.
- Fires a notification the moment the heart recharges.
- Keeps a reminder on screen for as long as the heart is sat there unused, so a
  notification you missed is not the only warning you get.

### Settings

| Setting | Default | What it does |
| --- | --- | --- |
| Enable | on | Turns the whole heart reminder on or off. |
| Hearts to track | Imbued and saturated | Restrict it to the saturated heart's shorter cooldown if you have both. |
| Notify when recharged | on | Notification as the cooldown ends. Use RuneLite's own notification settings to choose tray, sound or screen flash. |
| Show on-screen reminder | on | Persistent reminder while the heart is ready. |
| Show cooldown timer | on | Infobox counting the cooldown down. |
| Send a chat message | off | Also print the recharge in the chat box. |
| Only when carrying one | on | Stay quiet when no heart is in your inventory. |
| Only while fighting | on | Stay quiet unless you have dealt or taken damage in the last ten seconds, so it does not follow you round the bank. |

### How the cooldown is read

`Varbits.IMBUED_HEART_COOLDOWN` (varbit 5361) holds the remaining cooldown in
steps of ten game ticks, which is the same varbit RuneLite's own Timers plugin
uses for its "Imbued/Saturated heart" timer. That gives two things:

- **The event.** The step down to zero is the authoritative "it has recharged"
  moment. Nothing is inferred from a clock, so a death reset (which clears the
  cooldown), a world hop or a logout cannot leave the plugin out of step.
- **The countdown.** Ten ticks is six seconds, so the varbit alone would tick
  down in six second jumps. A wall-clock estimate, re-synchronised every time
  the varbit moves, is used for the display instead, and is capped by the varbit
  so it can never claim the heart is ready before the game does.

Which heart is on cooldown is worked out from the length the cooldown started
at: 50 steps is five minutes (saturated), 70 is seven (imbued). When the
cooldown was already running at login, neither can be ruled out, and the heart
stays unidentified rather than guessed at — a reminder still fires, it just says
"Heart" instead of naming it. The game's own `SATURATED_HEART_TIME` varbit is
watched as a second opinion, and will name a heart the cooldown length could
not.

The first varbit value seen after a login is deliberately *adopted* rather than
treated as a change, which is what stops a reminder firing every time you log
in with a ready heart.

## Building and running

Requires JDK 11 or newer.

```sh
./gradlew test     # run the unit tests
./gradlew run      # start RuneLite with this plugin loaded
```

`./gradlew run` launches a developer-mode client, which is how to try the
plugin before it is anywhere near the Plugin Hub.

## Adding the next reminder

Everything below `Reminder` is one reminder's business, and nothing else's:

1. Write a class implementing `Reminder`. Only `getName()`, `isEnabled()` and
   the events you care about need filling in — the rest have defaults.
   `getReminderText()` returns what the overlay should say, or `null` for
   nothing.
2. Add a `@ConfigSection` for it in `RemindersConfig`, with at least an enable
   toggle. Sections are positioned in tens so there is room to slot one in.
3. Register it in `RemindersPlugin.startUp()`.

`RemindersPlugin` subscribes to the client's events once and fans them out, so a
new reminder does not touch the event plumbing. `CombatTracker` is shared, so
"only while fighting" is one method call for any reminder that wants it.

Worth knowing for the thrall reminder specifically: `Varbits.RESURRECT_THRALL`
is 1 while a thrall is up and drops to 0 when it expires, which is the same
shape as the heart's varbit reaching zero. Thrall duration is one tick per Magic
level, +50% at Master and +100% at Grandmaster combat achievement tier.

## Layout

```
src/main/java/com/ratbag24/reminders/
  RemindersPlugin.java        event plumbing and the reminder registry
  RemindersConfig.java        one @ConfigSection per reminder
  RemindersOverlay.java       the shared on-screen panel
  Reminder.java               what a reminder has to implement
  CombatTracker.java          shared "was I fighting recently" state
  heart/
    SaturatedHeartReminder.java   the reminder itself
    HeartCooldown.java            cooldown timing rules, no RuneLite deps
    HeartType.java                imbued, saturated or not yet known
    TrackedHearts.java            which hearts to fire for
    HeartInfoBox.java             the countdown infobox
```

`HeartCooldown` deliberately has no RuneLite imports: the timing rules are the
part most likely to be subtly wrong, so they are kept testable on their own.
`HeartCooldownTest` covers the login sync, the death reset, early re-use,
heart identification and the display countdown.
