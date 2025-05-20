# El juego más difícil del mundo
[![Ask DeepWiki](https://deepwiki.com/badge.svg)](https://deepwiki.com/brayan083/El-juego-mas-dificil-del-mundo)

## Introduction
"El juego más difícil del mundo" is a challenging 2D game where the player navigates a series of levels filled with obstacles and coins. The objective is to collect all coins in a level and then reach the goal area to advance to the next. The game lives up to its name with tricky moving obstacles that require precise timing and movement.

## Features
* **Level-Based Progression:** Advance through multiple levels, each with unique layouts and challenges.
* **Player Control:** Navigate a red square character through the levels.
* **Obstacles:** Avoid moving blue circular obstacles. Collision results in death.
* **Collectible Coins:** Collect all gold coins in a level before you can proceed.
* **Goal Area:** Reach the light green goal zone after collecting all coins to complete the level.
* **Wall Collisions:** The player and obstacles interact with walls defined in the level's tile map.
* **Death Counter:** Keep track of your attempts with a death counter displayed in the header.
* **Dynamic Header:** Displays current level, number of coins collected/total in the level, and total deaths.
* **Visuals:**
    * Playable areas feature a chessboard background.
    * Distinct colors for player, obstacles, coins, goal, and walls for clarity.

## Game Elements
* **Player:** A red square controlled by the user.
* **Obstacles:** Blue circles that move horizontally or vertically. Touching them resets the player to the start of the level and increments the death counter.
* **Coins:** Gold circular items that must all be collected to unlock the goal for the current level.
* **Goal:** A light green rectangular area. The player must reach it after collecting all coins to proceed to the next level.
* **Walls:** Lavender-colored tiles that form the boundaries and internal structures of the levels. Neither the player nor obstacles can pass through them.

## Controls
* **Up Arrow:** Move player up.
* **Down Arrow:** Move player down.
* **Left Arrow:** Move player left.
* **Right Arrow:** Move player right.

## Objective
The main objective is to complete all levels. To complete a single level:
1.  Collect all the gold coins present in the level.
2.  After collecting all coins, navigate the player to the green goal area.
3.  Avoid all blue obstacles. Colliding with an obstacle will reset your progress for that level (coins will need to be recollected) and add to your death count.

## Technical Details
* **Language:** Java
* **GUI:** Swing
* **JSON Parsing:** Jackson (for loading level data)
* **Build Tool:** Apache Maven
* **Game Loop:** Runs at approximately 60 FPS using `java.util.Timer`.

## Console Simulation
The `Main.java` file also includes a commented-out method `runConsoleSimulation()`. If uncommented and run, this mode provides a text-based simulation of the player attempting to collect the first coin in the first level and then intentionally colliding with an obstacle. This can be useful for debugging or understanding basic game mechanics without the GUI.

## Project Structure
The project follows a standard Maven layout and is organized into several packages:
* `controller`: Contains the main game logic and state management (`Game.java`).
* `model`: Includes classes for game entities (Player, Obstacle, Coin, Goal), level structure (`Level.java`), configuration (`Config.java`), and level loading (`LevelLoader.java`).
* `view`: Handles the rendering of the game on the screen (`GameView.java`).
* `handler`: Manages user input (`InputHandler.java`).
* The main entry point for the application is `Main.java` in the default package (`src/main/java`).

## Levels Configuration
Level data (player starting position, goal location, obstacle properties, coin positions, and tile maps) is defined in a JSON file located at `src/main/resources/levels.json`. This allows for easy modification and addition of new levels.