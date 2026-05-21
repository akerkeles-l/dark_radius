package com.darkradius.patterns.observer;

// ── Observer interface ────────────────────────────────────────
public interface IGameObserver {
    void onEvent(GameEvent event, Object data);
}
