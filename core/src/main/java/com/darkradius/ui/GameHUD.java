package com.darkradius.ui;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class GameHUD {

    private final int W, H;
    private BitmapFont fLabel, fValue, fTiny;
    private GlyphLayout gl = new GlyphLayout();
    private float t = 0f;
    private int   lastScore = 0;
    private float scoreAnim = 0f;

    private static final Color C_BG    = new Color(0.010f, 0.008f, 0.015f, 0.95f);
    private static final Color C_CYAN  = new Color(0.12f, 0.88f, 1f, 1f);
    private static final Color C_GREEN = new Color(0.1f, 0.95f, 0.5f, 1f);
    private static final Color C_RED   = new Color(1f, 0.15f, 0.15f, 1f);
    private static final Color C_GOLD  = new Color(1f, 0.82f, 0.08f, 1f);
    private static final Color C_DIM   = new Color(0.28f, 0.30f, 0.40f, 1f);

    public GameHUD(int w, int h) {
        W = w; H = h;
        fLabel = new BitmapFont(); fLabel.getData().setScale(1.2f);
        fValue = new BitmapFont(); fValue.getData().setScale(2.0f);
        fTiny  = new BitmapFont(); fTiny.getData().setScale(1.1f);
    }

    public void render(ShapeRenderer sr, SpriteBatch bt,
                       int hp, int maxHp, int level, int score,
                       int enemyCount, int keysCollected, int keysNeeded,
                       float delta) {
        t += delta;
        if (score != lastScore) { scoreAnim = 1f; lastScore = score; }
        if (scoreAnim > 0f) scoreAnim = Math.max(0f, scoreAnim - delta * 2.5f);

        float BAR = 48f;

        sr.begin(ShapeRenderer.ShapeType.Filled);

        Matrix4 screenMat = new Matrix4().setToOrtho2D(0, 0, W, H);
        sr.setProjectionMatrix(screenMat);
        bt.setProjectionMatrix(screenMat);



        sr.setColor(C_BG);
        sr.rect(0, H - BAR, W, BAR);

        sr.setColor(new Color(0.12f, 0.88f, 1f, 0.55f));
        sr.rect(0, H - BAR - 2f, W, 2f);
        sr.setColor(new Color(0.12f, 0.88f, 1f, 0.08f));
        sr.rect(0, H - BAR - 8f, W, 6f);

        sr.setColor(new Color(0.12f, 0.88f, 1f, 0.18f));
        sr.rect(280f, H - BAR + 6f, 1f, BAR - 12f);

        sr.rect(560f, H - BAR + 6f, 1f, BAR - 12f);

        sr.rect(840f, H - BAR + 6f, 1f, BAR - 12f);

        float hbX = 90f, hbY = H - BAR + 10f;
        float hbW = 180f, hbH = 16f;

        sr.setColor(new Color(0.08f, 0.08f, 0.12f, 1f));
        sr.rect(hbX, hbY, hbW, hbH);

        float frac = (float) hp / maxHp;
        Color hc = hp <= 1 ? C_RED : hp < maxHp ? new Color(1f, 0.6f, 0.1f, 1f) : C_GREEN;
        float pulse = hp <= 1 ? (float)(Math.sin(t * 8) * 0.25 + 0.75) : 1f;
        sr.setColor(new Color(hc.r * pulse, hc.g * pulse, hc.b * pulse, 1f));
        sr.rect(hbX, hbY, hbW * frac, hbH);

        for (int i = 1; i < maxHp; i++) {
            sr.setColor(C_BG);
            sr.rect(hbX + hbW * i / maxHp - 1f, hbY, 2f, hbH);
        }

        sr.setColor(new Color(0.12f, 0.88f, 1f, 0.25f));
        sr.rect(hbX - 1f, hbY - 1f, hbW + 2f, 1f);
        sr.rect(hbX - 1f, hbY + hbH, hbW + 2f, 1f);
        sr.rect(hbX - 1f, hbY - 1f, 1f, hbH + 2f);
        sr.rect(hbX + hbW, hbY - 1f, 1f, hbH + 2f);

        float orbX = 420f, orbY = H - BAR / 2f;
        float op = (float)(Math.sin(t * 2.2) * 0.1 + 0.9);
        sr.setColor(new Color(0.12f * op, 0.88f * op, 1f * op, 0.18f));
        sr.circle(orbX, orbY, 20f, 32);
        sr.setColor(new Color(0.12f, 0.88f, 1f, 0.32f));
        sr.circle(orbX, orbY, 14f, 32);

        float eX = 600f;
        for (int i = 0; i < Math.min(enemyCount, 10); i++) {
            float dotX = eX + i * 22f;
            float dotY = H - BAR / 2f;
            sr.setColor(new Color(1f, 0.15f, 0.15f, 0.85f));
            sr.circle(dotX, dotY, 6f, 12);
        }
        if (enemyCount == 0) {
            sr.setColor(new Color(0.1f, 0.95f, 0.5f, 0.7f));
            sr.circle(eX, H - BAR / 2f, 6f, 12);
        }
        if (scoreAnim > 0f) {
            sr.setColor(new Color(1f, 0.82f, 0.08f, 0.08f * scoreAnim));
            sr.rect(860f, H - BAR, W - 860f, BAR);
        }


        for (int i = 0; i < 5; i++) {
            sr.setColor(i < level
                ? new Color(0.12f, 0.88f, 1f, 0.8f)
                : new Color(0.12f, 0.14f, 0.20f, 1f));
            sr.circle(16f + i * 16f, H - BAR - 14f, i < level ? 4f : 3f, 12);
        }

        sr.end();

        bt.begin();



        fTiny.setColor(C_DIM);
        fTiny.draw(bt, "HP", 10f, H - BAR + 30f);

        fTiny.setColor(hc);
        fTiny.draw(bt, hp + "/" + maxHp, hbX + hbW + 8f, H - BAR + 24f);

        fTiny.setColor(C_DIM);
        gl.setText(fTiny, "LVL");
        fTiny.draw(bt, "LVL", orbX - gl.width / 2f, H - BAR + 44f);

        fValue.setColor(new Color(0.12f * op, 0.9f * op, 1f * op, 1f));
        gl.setText(fValue, "" + level);
        fValue.draw(bt, "" + level, orbX - gl.width / 2f, H - BAR + 30f);

        fTiny.setColor(C_DIM);
        fTiny.draw(bt, "ENEMIES", 580f, H - BAR + 44f);

        if (enemyCount == 0) {
            fTiny.setColor(C_GREEN);
            fTiny.draw(bt, "CLEAR", 600f, H - BAR + 20f);
        }

        fTiny.setColor(C_DIM);
        fTiny.draw(bt, "SCORE", 870f, H - BAR + 44f);

        float ss = 1.45f + scoreAnim * 0.25f;
        fValue.getData().setScale(ss);
        fValue.setColor(C_GOLD);
        fValue.draw(bt, String.format("%06d", score), 870f, H - BAR + 30f);
        fValue.getData().setScale(1.45f);

        fTiny.setColor(new Color(0.18f, 0.20f, 0.28f, 0.6f));
        fTiny.draw(bt, "ESC pause", W - 95f, H - BAR + 20f);

        fTiny.setColor(new Color(1f, 0.82f, 0.05f, 0.6f));
        fTiny.draw(bt, "KEYS", 820f, H - BAR + 44f);

        for (int i = 0; i < keysNeeded; i++) {
            if (i < keysCollected) {
                fValue.setColor(new Color(1f, 0.82f, 0.05f, 1f));
            } else {
                fValue.setColor(new Color(0.25f, 0.25f, 0.32f, 0.6f));
            }
            fValue.draw(bt, "♦", 820f + i * 32f, H - BAR + 28f);
        }
        fTiny.setColor(new Color(1f, 0.82f, 0.05f, 0.7f));
        fTiny.draw(bt, "KEYS", 500f, H - BAR + 44f);

        for (int i = 0; i < keysNeeded; i++) {
            if (i < keysCollected) {
                sr.begin(ShapeRenderer.ShapeType.Filled);
                sr.setColor(new Color(1f, 0.82f, 0.05f, 1f));
                sr.circle(530f + i * 38f, H - BAR + 18f, 10f, 20);
                sr.setColor(new Color(1f, 0.95f, 0.4f, 0.3f));
                sr.circle(530f + i * 38f, H - BAR + 18f, 15f, 20);
                sr.end();
            } else {
                sr.begin(ShapeRenderer.ShapeType.Line);
                sr.setColor(new Color(0.3f, 0.32f, 0.42f, 0.7f));
                sr.circle(530f + i * 38f, H - BAR + 18f, 10f, 20);
                sr.end();
            }
        }

        if (keysCollected < keysNeeded) {
            fTiny.setColor(new Color(0.6f, 0.3f, 0.1f, 0.7f));
            fTiny.draw(bt, "Find all keys to open exit!", 460f, H - BAR - 18f);
        } else {
            fTiny.setColor(new Color(0.1f, 0.95f, 0.5f, 0.9f));
            fTiny.draw(bt, "EXIT IS OPEN! Go to green!", 460f, H - BAR - 18f);
        }

        bt.end();
    }

    public void dispose() {
        fLabel.dispose(); fValue.dispose(); fTiny.dispose();
    }
}
