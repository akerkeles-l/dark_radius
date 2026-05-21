package com.darkradius.patterns.state;

import com.badlogic.gdx.math.Vector2;
import com.darkradius.entities.Enemy;
import com.darkradius.managers.DifficultyManager;

// ── PATROL ────────────────────────────────────────────────────
public class PatrolState implements IEnemyState {
    private float timer; private int step;
    private static final float DUR = 2.8f;

    public void enter(Enemy e)  { timer = 0; step = 0; nextTarget(e); }
    public void update(Enemy e, float dt) {
        timer += dt;
        e.moveToward(e.getPatrolTarget(), dt, 28f);
        e.moveToward(e.getPatrolTarget(), dt,
            DifficultyManager.getInstance().enemySpeed() * 0.55f);
        if (timer >= DUR || e.reached(e.getPatrolTarget(), 18f)) {
            timer = 0; step++; nextTarget(e);
        }
    }
    public void exit(Enemy e)   {}
    public String name()        { return "PATROL"; }

    private void nextTarget(Enemy e) {
        Vector2 sp = e.getSpawn();
        float r = 52f;
        switch (step % 4) {
            case 0: e.setPatrolTarget(new Vector2(sp.x + r, sp.y));     break;
            case 1: e.setPatrolTarget(new Vector2(sp.x + r, sp.y + r)); break;
            case 2: e.setPatrolTarget(new Vector2(sp.x, sp.y + r));     break;
            default:e.setPatrolTarget(sp.cpy());                         break;
        }
    }
}

