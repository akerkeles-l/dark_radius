package com.darkradius.patterns.observer;

import java.util.*;

// ── Event Bus (Singleton) ─────────────────────────────────────
public class GameEventBus {

    private static GameEventBus instance;
    private final Map<GameEvent, List<IGameObserver>> map = new EnumMap<>(GameEvent.class);

    private GameEventBus() {
        for (GameEvent e : GameEvent.values()) map.put(e, new ArrayList<>());
    }

    public static GameEventBus get() {
        if (instance == null) instance = new GameEventBus();
        return instance;
    }

    public void sub(GameEvent e, IGameObserver o)   { map.get(e).add(o); }
    public void unsub(GameEvent e, IGameObserver o) { map.get(e).remove(o); }

    public void emit(GameEvent e, Object data) {
        for (IGameObserver o : new ArrayList<>(map.get(e))) o.onEvent(e, data);
    }
    public void emit(GameEvent e) { emit(e, null); }

    public void clear() { for (GameEvent e : GameEvent.values()) map.get(e).clear(); }
}
