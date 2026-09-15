# Predator-Prey Simulation

A Java Swing simulation of predators, prey, and grass interacting on a shared canvas. Built for COMP2000 (Object Oriented Programming Practices) using only standard JRE classes; no external libraries.

## Overview

The simulation models a simple ecosystem: predators hunt prey, prey forage for grass and flee danger, and grass regrows over time. All entities share a common `Entity` superclass, with movement, hunger, and reproduction behaviour layered on top through an inheritance hierarchy.

## Project structure

```
predatorPrey/
├── resources/       # sprite assets (tiger.gif, deer.gif, grass.png, background.jpg, OVER.png)
└── src/             # all .java source files
```

Sprite paths in `SimulationPanel` (e.g. `"predatorPrey/resources/tiger.gif"`) are relative to the repository root, not to `src/`. Since `resources/` and `src/` are cloned together as siblings under `predatorPrey/`, running `App.java` from the repo root (the default working directory in most IDEs, including VS Code) resolves these paths correctly with no extra setup.

## Class structure

- **`Entity`** (abstract) — base class for anything drawn on the panel. Holds position (`x`, `y`), original spawn position (`originalX`/`originalY`, both `final`, used for reset), and an `isFood` flag. Declares `update()` as abstract, and provides default no-op hooks for `onDayTick()`, `shouldBeRemoved()`, and `reproduce()` that subclasses override as needed.
- **`Creature`** (abstract, extends `Entity`) — shared base for `Predator` and `Prey`. Adds `speed`, `hunger`, an `energyMeter` for reproduction tracking, a `target` reference, and a facing-direction flag for sprite flipping. Validates `speed` (must be positive) and `hunger` (must be non-negative) on construction, on every setter call, and on `resetState()`, throwing `InvalidCreatureStateException` if either is invalid.
- **`Predator`** (extends `Creature`) — hunts the nearest prey in range, retreats after eating, and reproduces when well-fed.
- **`Prey`** (extends `Creature`) — forages for grass, flees nearby predators, and reproduces when well-fed.
- **`Grass`** (extends `Entity` directly, not `Creature`) — stationary food source with a fixed lifespan; takes only `x` and `y` in its constructor, since it doesn't need hunger, speed, or any other creature-specific state.
- **`InvalidCreatureStateException`** (extends `RuntimeException`) — thrown when a `Creature` is constructed or updated with an invalid `speed` or `hunger` value.
- **`Simulation`** — owns the entity list, runs the per-tick game loop, and advances days.
- **`SimulationPanel`** (extends `JPanel`) — renders all entities, loading sprite images with a graceful fallback to coloured ovals if assets are missing.
- **`App`** — entry point; prompts for predator/prey counts via a dialog, wires up the Start/Pause/Reset controls, and drives the simulation with a 33ms `Timer` (~30 FPS).

## Behaviour per tick

Each call to `Simulation.tick()` runs in this order:

1. **Targeting** — every predator finds the nearest available prey within `PREDATOR_HUNT_RANGE` (200px) and claims it, so two predators don't chase the same prey. Every prey finds the nearest edible grass within `PREY_FORAGE_RANGE` (100px), and separately checks for the nearest predator within `PREY_THREAT_RANGE` (200px) to decide if it should flee.
2. **Movement** — every entity's `update()` runs. Predators either retreat, pursue a fleeing prey using velocity-based prediction, move toward a static target, or wander with bounce. Prey either flee a threat, move toward grass, or wander.
3. **Separation** — an anti-overlap pass nudges creatures apart if they're within `PREY_SEPARATION_DIST` (15px) or `PREDATOR_SEPARATION_DIST` (20px) of another of the same type, so groups don't visually merge into a single overlapping sprite.
4. **Eating** — predators within `EAT_RANGE` (35px) of an unclaimed prey eat it: the predator gains 50 hunger, starts a 75-tick retreat, and the prey's hunger is set to 0. Prey within `EAT_RANGE` of edible grass eat it: the prey gains 30 hunger, and the grass is marked eaten.
5. **Cleanup** — any creature with `hunger <= 0` is removed immediately, and any grass just eaten this tick is removed.
6. **Day counter** — after 152 ticks (`TICKS_PER_DAY`), a full day has passed and `advanceDay()` runs.

## Day cycle (`advanceDay()`)

Once per day:

- Every entity's `onDayTick()` runs. Creatures gain 1 energy if they were fed at least once that day, or lose 1 energy if not (`fedToday` resets afterward). Grass ages by one day and is marked for removal once it reaches `LIFESPAN_DAYS` (2 days), regardless of whether it was ever eaten.
- Each entity attempts to `reproduce()`. Predators reproduce once `energyMeter` reaches 4 (spawning a new `Predator` nearby, full hunger); prey reproduce at a lower threshold of 2, giving prey a faster population growth rate. Reproducing resets the parent's energy to 0.
- Creatures that have starved past their death threshold are removed: predators die at `energyMeter <= -5`, prey die at `energyMeter <= -4`.
- Between 5 and 10 new grass patches spawn at random positions below the ground line.

## Reset behaviour

`Simulation.reset()` restores every entity to its original spawn position (tracked via `Entity.originalX`/`originalY`) and resets creature state — hunger back to 100, energy to 0, target cleared, movement direction reset. Grass is marked uneaten again. This lets the UI's Reset button restart a run without re-creating entities from scratch.

## Error handling

Entity setup in `App.main()` is wrapped in a `try/catch/finally`: if any `Predator` or `Prey` is constructed with an invalid speed or hunger value, `InvalidCreatureStateException` is caught, an error dialog is shown, and the `finally` block logs how many entities were successfully created before the failure — so a partial, invalid setup doesn't silently look like a complete one.

## Running the simulation

Clone the repository (or your fork of it) so that `resources/` and `src/` come down together under `predatorPrey/`. Compile and run `App.java` from the repo root with all other classes on the classpath, using a standard JDK — no external dependencies are required. On launch, a dialog prompts for the number of predators (0–5) and prey (0–20) before the simulation window opens with Start, Pause, and Reset controls.