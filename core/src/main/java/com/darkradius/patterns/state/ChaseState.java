package com.darkradius.patterns.state;

import com.badlogic.gdx.math.Vector2;
import com.darkradius.entities.Enemy;
import com.darkradius.managers.DifficultyManager;

// ── CHASE ─────────────────────────────────────────────────────
public class ChaseState implements IEnemyState {
    private float lostTimer;

    public void enter(Enemy e) {
        lostTimer = 0;
    }

    public void update(Enemy e, float dt) {
        Vector2 last = e.getLastKnown();
        e.moveToward(last, dt, DifficultyManager.getInstance().enemySpeed());
        if (last != null) e.moveToward(last, dt, 45f);
        if (!e.isChasing()) {
            lostTimer += dt;
            if (lostTimer > 3.5f) e.changeState(new PatrolState());
        } else {
            lostTimer = 0;
        }
    }

    public void exit(Enemy e) {
    }

    public String name() {
        return "CHASE";
    }
}
