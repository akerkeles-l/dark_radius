package com.darkradius.patterns.observer;

// ── Event types ───────────────────────────────────────────────
public enum GameEvent {
    PLAYER_MOVED, PLAYER_DETECTED, PLAYER_LOST,
    PLAYER_DAMAGED, PLAYER_DIED,
    TRAP_TRIGGERED, ENEMY_KILLED, LEVEL_COMPLETE
}
