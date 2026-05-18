package com.darkradius.entities;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;
import com.darkradius.maze.MazeData;
import com.darkradius.managers.GameManager;
import com.darkradius.patterns.observer.GameEvent;
import com.darkradius.patterns.observer.GameEventBus;

public class Player {

    private Vector2 pos;
    private float speed = 150f;
    private int   health  = 10;
    private int   maxHp   = 10;
    private boolean moving  = false;
    private boolean dead    = false;
    private float iTimer    = 0f; // invincibility
    private float anim      = 0f;
    private static final float I_DUR = 1.5f;
    private static final float SIZE  = 9f;
    private MazeData maze;

    public Player(Vector2 start, MazeData maze) {
        pos = start.cpy(); this.maze = maze;
    }

    public void update(float dt) {
        anim += dt;
        if (iTimer > 0) iTimer -= dt;

        float dx = 0, dy = 0;
        if (Gdx.input.isKeyPressed(Input.Keys.W) || Gdx.input.isKeyPressed(Input.Keys.UP))    dy =  1;
        if (Gdx.input.isKeyPressed(Input.Keys.S) || Gdx.input.isKeyPressed(Input.Keys.DOWN))   dy = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.A) || Gdx.input.isKeyPressed(Input.Keys.LEFT))   dx = -1;
        if (Gdx.input.isKeyPressed(Input.Keys.D) || Gdx.input.isKeyPressed(Input.Keys.RIGHT))  dx =  1;

        moving = (dx != 0 || dy != 0);
        if (dx != 0 && dy != 0) { dx *= 0.707f; dy *= 0.707f; }

        float nx = pos.x + dx * speed * dt;
        float ny = pos.y + dy * speed * dt;
        if (!wallAt(nx, pos.y)) pos.x = nx;
        if (!wallAt(pos.x, ny)) pos.y = ny;

        if (moving) GameEventBus.get().emit(GameEvent.PLAYER_MOVED, pos);
    }

    // Used by Command pattern
    public void applyMove(float dx, float dy) {
        float nx = pos.x + dx * MazeData.TS;
        float ny = pos.y + dy * MazeData.TS;
        if (!wallAt(nx, pos.y)) pos.x = nx;
        if (!wallAt(pos.x, ny)) pos.y = ny;
    }

    private boolean wallAt(float x, float y) {
        float r = SIZE - 1f;
        return maze.isWall(maze.col(x - r), maze.row(y - r))
            || maze.isWall(maze.col(x + r), maze.row(y - r))
            || maze.isWall(maze.col(x - r), maze.row(y + r))
            || maze.isWall(maze.col(x + r), maze.row(y + r));
    }

    public void render(ShapeRenderer sr) {
        float pulse = (float)(Math.sin(anim * 6) * 0.12 + 0.88);
        boolean inv = iTimer > 0;
        CharacterType shape = GameManager.getInstance().getChosenChar();

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Outer glow
        sr.setColor(new Color(0.15f, 0.7f, 1f, 0.12f));
        sr.circle(pos.x, pos.y, SIZE * 2.2f, 32);

        Color bodyColor = inv && (int)(iTimer * 12) % 2 == 0
            ? new Color(1f, 0.25f, 0.25f, 0.85f)
            : new Color(0.25f, 0.88f, 1f, 0.95f);
        sr.setColor(bodyColor);

        float s = SIZE * pulse;

        if (shape == CharacterType.CIRCLE) {
            sr.circle(pos.x, pos.y, s, 32);

        } else if (shape == CharacterType.TRIANGLE) {
            sr.triangle(
                pos.x, pos.y + s,
                pos.x - s, pos.y - s * 0.6f,
                pos.x + s, pos.y - s * 0.6f
            );

        } else { // SQUARE
            sr.rect(pos.x - s, pos.y - s, s * 2, s * 2);
        }

        // Center white dot
        sr.setColor(Color.WHITE);
        sr.circle(pos.x, pos.y, SIZE * 0.22f, 12);

        sr.end();
    }

    public void takeDamage(int amt) {
        if (iTimer > 0 || dead) return;
        health = Math.max(0, health - amt);
        iTimer = I_DUR;
        GameEventBus.get().emit(GameEvent.PLAYER_DAMAGED, health);
        if (health <= 0) { dead = true; GameEventBus.get().emit(GameEvent.PLAYER_DIED); }
    }

    public Vector2 getPos()     { return pos; }
    public void setPos(Vector2 p){ pos.set(p); }
    public int  getHealth()     { return health; }
    public int  getMaxHp()      { return maxHp; }
    public boolean isMoving()   { return moving; }
    public boolean isDead()     { return dead; }
    public float getSize()      { return SIZE; }
    public float getVision()    { return GameManager.getInstance().getVisionRadius(); }
}
