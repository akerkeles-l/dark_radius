package com.darkradius.patterns.strategy;

import com.darkradius.entities.Enemy;
import com.darkradius.entities.Player;

// ── SlowGuard: close proximity ────────────────────────────────
public class SlowGuardBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return e.getPos().dst(p.getPos()) <= range();
    }

    public float range() { return 40f; }

    public String label() {
        return "GUARD";
    }
}
