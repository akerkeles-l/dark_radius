package com.darkradius.entities;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Vector2;

public class Key {

    private Vector2 pos;
    private boolean collected = false;
    private float anim = 0f;
    private static final float SIZE = 10f;

    public Key(Vector2 pos) {
        this.pos = pos.cpy();
    }

    public void update(float dt, Player player) {
        anim += dt;
        if (!collected && pos.dst(player.getPos()) < SIZE + player.getSize() + 4f) {
            collected = true;
        }
    }

    public void render(ShapeRenderer sr) {
        if (collected) return;
        float p = (float)(Math.sin(anim * 5) * 0.3 + 0.7);

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Glow
        sr.setColor(new Color(1f, 0.85f, 0.05f, 0.15f * p));
        sr.circle(pos.x, pos.y, SIZE * 2.5f, 24);

        // Key body (gold circle)
        sr.setColor(new Color(1f, 0.82f, 0.05f, 0.95f));
        sr.circle(pos.x, pos.y - 2f, SIZE * 0.65f, 20);

        // Key stem
        sr.rect(pos.x - 2f, pos.y - SIZE, 4f, SIZE);

        // Key teeth
        sr.rect(pos.x, pos.y - SIZE + 2f, SIZE * 0.5f, 3f);
        sr.rect(pos.x, pos.y - SIZE + 7f, SIZE * 0.4f, 3f);

        sr.end();

        // Outline circle
        sr.begin(ShapeRenderer.ShapeType.Line);
        sr.setColor(new Color(1f, 0.82f, 0.05f, 0.6f * p));
        sr.circle(pos.x, pos.y, SIZE * 1.4f, 20);
        sr.end();
    }

    public boolean isCollected() { return collected; }
    public Vector2 getPos()      { return pos; }
}
