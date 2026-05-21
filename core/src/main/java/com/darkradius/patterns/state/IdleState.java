package com.darkradius.patterns.state;

import com.darkradius.entities.Enemy;

// ── IDLE ─────────────────────────────────────────────────────
class IdleState implements IEnemyState {
    private float timer;

    public void enter(Enemy e) {
        timer = 0;
    }

    public void update(Enemy e, float dt) {
        timer += dt;
        if (timer > 1.8f) e.changeState(new PatrolState());
    }

    public void exit(Enemy e) {
    }

    public String name() {
        return "IDLE";
    }
}
