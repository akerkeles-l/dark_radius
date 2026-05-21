package com.darkradius.patterns.strategy;

import com.darkradius.entities.Enemy;
import com.darkradius.entities.Player;

// ── Interface ─────────────────────────────────────────────────
public interface IEnemyBehavior {
    boolean detect(Enemy e, Player p);

    float range();


    String label();
}
