package com.darkradius.patterns.strategy;

import com.darkradius.entities.Enemy;
import com.darkradius.entities.Player;

// ── Listener: sound-based ────────────────────────────────────
public class ListenerBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return p.isMoving() && e.getPos().dst(p.getPos()) <= range();
    }

    public float range() { return 55f; }

    public String label() {
        return "LISTENER";
    }
}
