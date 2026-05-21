package com.darkradius.patterns.strategy;

import com.darkradius.entities.Enemy;
import com.darkradius.entities.Player;

// ── Watcher: line-of-sight ────────────────────────────────────
public class WatcherBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return e.getPos().dst(p.getPos()) <= range();
    }
    public float range() { return 75f; }
    public String label() { return "WATCHER"; }
}

