package com.darkradius.patterns.state;

import com.darkradius.entities.Enemy;

// ── Interface ─────────────────────────────────────────────────
public interface IEnemyState {
    void enter(Enemy e);

    void update(Enemy e, float dt);

    void exit(Enemy e);

    String name();
}
