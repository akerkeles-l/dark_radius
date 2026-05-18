package com.darkradius.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.darkradius.maze.MazeData;
import com.darkradius.patterns.observer.GameEvent;
import com.darkradius.patterns.observer.GameEventBus;
import com.darkradius.patterns.state.*;
import com.darkradius.patterns.strategy.IEnemyBehavior;

public class Enemy {

    private Vector2 pos, spawn;
    private Vector2 lastKnown;
    private Vector2 patrolTarget;
    private IEnemyState state;
    private IEnemyBehavior behavior;

    private float r, g, b;
    private float anim = 0f;
    private boolean chasing = false;
    private boolean alive   = true;
    private float stunTimer = 0f;

    private MazeData maze;
    private static final float SIZE = 5f;

    public Enemy(Vector2 pos, IEnemyBehavior beh, float r, float g, float b) {
        this.pos = pos.cpy(); this.spawn = pos.cpy();
        this.patrolTarget = pos.cpy();
        this.behavior = beh;
        this.r = r; this.g = g; this.b = b;
        this.state = new PatrolState();
        this.state.enter(this);
    }

    public void setMaze(MazeData m) { maze = m; }

    public void update(float dt, Player player) {
        if (!alive) return;
        anim += dt;

        boolean detected = behavior.detect(this, player);

        if (detected && !chasing) {
            chasing = true;
            lastKnown = player.getPos().cpy();
            changeState(new ChaseState());
            GameEventBus.get().emit(GameEvent.PLAYER_DETECTED, this);
        } else if (!detected && chasing) {
            chasing = false;
            GameEventBus.get().emit(GameEvent.PLAYER_LOST, this);
        }
        if (detected) lastKnown = player.getPos().cpy();

        state.update(this, dt);

        if (pos.dst(player.getPos()) < SIZE + player.getSize() + 2f)
            player.takeDamage(1);

        if (stunTimer > 0f) {
            stunTimer -= dt;
        }
    }

    public void stun(float duration) {
        stunTimer = duration;
        chasing = false;
    }

    public boolean isStunned() { return stunTimer > 0f; }

    public void render(ShapeRenderer sr) {
        if (!alive) return;
        float p = (float)(Math.sin(anim * (chasing ? 9 : 3.5)) * 0.18 + 0.82);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        sr.setColor(new Color(r, g, b, chasing ? 0.18f : 0.07f));
        sr.circle(pos.x, pos.y, behavior.range(), 48);

        if (chasing) {
            sr.setColor(new Color(r, g, b, 0.22f * p));
            sr.circle(pos.x, pos.y, SIZE * 2.8f, 32);
        }

        if (stunTimer > 0f) {
            float sp = (float)(Math.sin(anim * 12) * 0.3 + 0.7);
            sr.setColor(new Color(1f, 0.9f, 0.1f, 0.6f * sp));
            sr.circle(pos.x, pos.y, SIZE * 2.2f, 32);
            sr.setColor(new Color(1f, 0.9f, 0.1f, 0.9f));
            sr.circle(pos.x, pos.y + SIZE + 8f, 4f, 12);
        }

        sr.setColor(new Color(r * p, g * p, b * p, 0.92f));
        sr.circle(pos.x, pos.y, SIZE, 32);

        float ex = pos.x + 3.5f, ey = pos.y + 3.5f;
        sr.setColor(Color.WHITE);
        sr.circle(ex, ey, SIZE * 0.28f, 12);
        sr.setColor(new Color(0.05f, 0.05f, 0.08f, 1f));
        sr.circle(ex + 0.8f, ey + 0.8f, SIZE * 0.14f, 8);

        Color sc = chasing ? new Color(1f, 0.2f, 0.2f, 1f) : new Color(0.3f, 1f, 0.5f, 0.7f);
        sr.setColor(sc);
        sr.circle(pos.x, pos.y + SIZE + 4f, 2.5f, 8);

        sr.end();
    }

    public void moveToward(Vector2 target, float dt, float spd) {
        if (target == null || maze == null) return;
        Vector2 dir = new Vector2(target).sub(pos);
        if (dir.len() < 2f) return;
        dir.nor();
        float nx = pos.x + dir.x * spd * dt;
        float ny = pos.y + dir.y * spd * dt;
        if (!wallAt(nx, pos.y)) pos.x = nx;
        if (!wallAt(pos.x, ny)) pos.y = ny;
    }

    public boolean reached(Vector2 t, float thr) {
        return t != null && pos.dst(t) < thr;
    }

    private boolean wallAt(float x, float y) {
        if (maze == null) return false;
        float r2 = SIZE * 0.75f;
        return maze.isWall(maze.col(x - r2), maze.row(y - r2))
            || maze.isWall(maze.col(x + r2), maze.row(y - r2))
            || maze.isWall(maze.col(x - r2), maze.row(y + r2))
            || maze.isWall(maze.col(x + r2), maze.row(y + r2));
    }

    public void changeState(IEnemyState next) {
        if (state != null) state.exit(this);
        state = next;
        state.enter(this);
    }

    public String getStateName()           { return state != null ? state.name() : "?"; }
    public String getBehaviorLabel()       { return behavior.label(); }

    public Vector2 getPos()                { return pos; }
    public Vector2 getSpawn()              { return spawn; }
    public Vector2 getLastKnown()          { return lastKnown; }
    public Vector2 getPatrolTarget()       { return patrolTarget; }
    public void setPatrolTarget(Vector2 t) { patrolTarget = t; }
    public boolean isChasing()             { return chasing; }
    public boolean isAlive()               { return alive; }
    public void kill()                     { alive = false; }
}
