package com.darkradius.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.darkradius.DarkRadiusGame;
import com.darkradius.entities.CharacterType;
import com.darkradius.managers.DifficultyManager;
import com.darkradius.managers.DifficultyManager.Difficulty;
import com.darkradius.managers.GameManager;

public class MenuScreen implements Screen {

    private final DarkRadiusGame G;
    private float t = 0f;

    // State: 0=main, 1=difficulty, 2=character
    private int state = 0;
    private int sel   = 0;

    private CharacterType chosenChar = CharacterType.CIRCLE;
    private Difficulty    chosenDiff = Difficulty.MEDIUM;

    private BitmapFont fHuge, fItem, fSub, fTiny;
    private GlyphLayout gl = new GlyphLayout();

    private final float[][] pts = new float[60][6];
    private float scanY = 0f;
    private float glitchT = 99f;

    private static final int W = DarkRadiusGame.W;
    private static final int H = DarkRadiusGame.H;

    public MenuScreen(DarkRadiusGame g) {
        G = g;
        fHuge = new BitmapFont(); fHuge.getData().setScale(11f);
        fItem = new BitmapFont(); fItem.getData().setScale(3.5f);
        fSub  = new BitmapFont(); fSub.getData().setScale(1.4f);
        fTiny = new BitmapFont(); fTiny.getData().setScale(1.1f);
        for (int i = 0; i < pts.length; i++) spawnPt(i, true);
        GameManager.getInstance().reset();
    }

    private void spawnPt(int i, boolean rndY) {
        pts[i][0] = MathUtils.random(0f, W * 0.44f);
        pts[i][1] = rndY ? MathUtils.random(0f, H) : H + 4f;
        pts[i][2] = MathUtils.random(20f, 65f);
        pts[i][3] = MathUtils.random(1.5f, 4f);
        pts[i][4] = MathUtils.random(0.08f, 0.45f);
        pts[i][5] = MathUtils.random(0, 2);
    }

    @Override
    public void render(float delta) {
        t += delta;
        scanY = (scanY + 90f * delta) % H;
        glitchT += delta;
        if (glitchT > MathUtils.random(3f, 7f)) glitchT = 0f;

        for (int i = 0; i < pts.length; i++) {
            pts[i][1] -= pts[i][2] * delta;
            if (pts[i][1] < -5f) spawnPt(i, false);
        }

        ScreenUtils.clear(0.020f, 0.015f, 0.030f, 1f);
        Matrix4 mat = new Matrix4().setToOrtho2D(0, 0, W, H);
        G.shape.setProjectionMatrix(mat);
        G.batch.setProjectionMatrix(mat);

        drawBackground(G.shape);

        if      (state == 0) renderMain(G.shape, G.batch);
        else if (state == 1) renderDifficulty(G.shape, G.batch);
        else                 renderCharacter(G.shape, G.batch);

        handleInput();
    }

    // ── Background (sol panel + divider + particles) ──────────
    private void drawBackground(ShapeRenderer sr) {
        float LW = W * 0.44f;
        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Sol panel
        sr.setColor(0.012f, 0.008f, 0.022f, 1f);
        sr.rect(0, 0, LW, H);

        // Scan line
        sr.setColor(new Color(0.05f, 0.7f, 1f, 0.07f));
        sr.rect(0, scanY, LW, 3f);
        sr.setColor(new Color(0.05f, 0.7f, 1f, 0.03f));
        sr.rect(0, scanY - 8f, LW, 8f);

        // Particles
        for (float[] p : pts) {
            if (p[0] > LW) continue;
            if (p[5] < 1f)      sr.setColor(new Color(0.05f, 0.85f, 1f, p[4]));
            else if (p[5] < 2f) sr.setColor(new Color(1f, 0.15f, 0.25f, p[4]));
            else                sr.setColor(new Color(0.9f, 0.9f, 1f, p[4] * 0.4f));
            sr.circle(p[0], p[1], p[3], 8);
        }

        // Glitch
        if (glitchT < 0.15f) {
            for (int i = 0; i < 3; i++) {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.07f));
                sr.rect(0, MathUtils.random(0f, H), LW, MathUtils.random(2f, 8f));
            }
        }

        // Divider
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.9f));
        sr.rect(LW, 0, 3f, H);
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.07f));
        sr.rect(LW - 12f, 0, 12f, H);
        sr.rect(LW + 3f,  0, 12f, H);

        // Oң panel
        sr.setColor(0.014f, 0.010f, 0.026f, 1f);
        sr.rect(LW + 3f, 0, W - LW - 3f, H);

        // Brackets
        float bx1 = LW + 22f, bx2 = W - 5f;
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.55f));
        sr.rect(bx2 - 55f, H - 5f, 50f, 5f); sr.rect(bx2 - 5f, H - 55f, 5f, 55f);
        sr.rect(bx1, H - 5f, 50f, 5f);        sr.rect(bx1, H - 55f, 5f, 55f);
        sr.rect(bx2 - 55f, 0f, 50f, 5f);      sr.rect(bx2 - 5f, 0f, 5f, 55f);
        sr.rect(bx1, 0f, 50f, 5f);            sr.rect(bx1, 0f, 5f, 55f);

        // Bottom bar
        sr.setColor(0f, 0f, 0f, 0.65f);
        sr.rect(LW + 3f, 0, W - LW, 50f);
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.2f));
        sr.rect(LW + 3f, 50f, W - LW, 1f);

        sr.end();

        // Sol: DARK RADIUS title
        G.batch.begin();
        float tp = (float)(Math.sin(t * 1.6f) * 0.05f + 0.95f);
        fHuge.setColor(new Color(0.93f, 0.95f, 1f, tp));
        gl.setText(fHuge, "DARK");
        fHuge.draw(G.batch, "DARK", LW / 2f - gl.width / 2f, H - 28f);
        fHuge.setColor(new Color(0.05f, 0.88f, 1f, tp));
        gl.setText(fHuge, "RADIUS");
        fHuge.draw(G.batch, "RADIUS", LW / 2f - gl.width / 2f, H - 175f);
        fSub.setColor(new Color(0.2f, 0.24f, 0.36f, 0.85f));
        gl.setText(fSub, "NAVIGATE THE DARKNESS");
        fSub.draw(G.batch, "NAVIGATE THE DARKNESS", LW / 2f - gl.width / 2f, H - 215f);
        G.batch.end();
    }

    // ── STATE 0: Main menu ────────────────────────────────────
    private void renderMain(ShapeRenderer sr, SpriteBatch bt) {
        float LW = W * 0.44f;
        float mX = LW + 65f;
        float mSY = H / 2f + 80f;
        float mGap = 115f;
        String[] items = {"START", "DIFFICULTY", "CHARACTER"};

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < items.length; i++) {
            float iy = mSY - i * mGap;
            if (i == sel) {
                float p = (float)(Math.sin(t * 5) * 0.07f + 0.18f);
                sr.setColor(new Color(0.05f, 0.85f, 1f, p));
                sr.rect(mX - 18f, iy - 50f, W - LW - 85f, 68f);
                sr.setColor(new Color(0.05f, 0.85f, 1f, 1f));
                sr.rect(mX - 18f, iy - 50f, 5f, 68f);
            } else {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.03f));
                sr.rect(mX - 18f, iy - 50f, W - LW - 85f, 68f);
                sr.setColor(new Color(0.3f, 0.35f, 0.45f, 0.35f));
                sr.rect(mX - 18f, iy - 50f, 5f, 68f);
            }
        }
        sr.end();

        bt.begin();
        // Tagline
        fSub.setColor(new Color(0.15f, 0.20f, 0.32f, 0.8f));
        fSub.draw(bt, "TRUST NOTHING.", mX, H - 55f);
        fSub.setColor(new Color(0.1f, 0.14f, 0.22f, 0.55f));
        fSub.draw(bt, "SURVIVE EVERYTHING.", mX, H - 88f);

        for (int i = 0; i < items.length; i++) {
            float iy = mSY - i * mGap;
            fSub.setColor(new Color(0.05f, 0.85f, 1f, i == sel ? 0.55f : 0.18f));
            fSub.draw(bt, "0" + (i + 1), mX - 16f, iy - 10f);

            fItem.setColor(i == sel
                ? new Color(0.05f, 0.92f, 1f, 1f)
                : new Color(0.38f, 0.42f, 0.56f, 0.9f));
            fItem.draw(bt, items[i], mX + 30f, iy);

            // Sub-info
            if (i == 1) {
                fTiny.setColor(new Color(0.05f, 0.85f, 1f, 0.6f));
                fTiny.draw(bt, DifficultyManager.getInstance().label(), mX + 30f, iy - 26f);
            }
            if (i == 2) {
                fTiny.setColor(new Color(0.05f, 0.85f, 1f, 0.6f));
                fTiny.draw(bt, chosenChar.name(), mX + 30f, iy - 26f);
            }

            if (i == sel) {
                float aa = (float)(Math.sin(t * 6) * 0.3f + 0.7f);
                fItem.setColor(new Color(0.05f, 0.92f, 1f, aa));
                fItem.draw(bt, ">>", mX + 30f + 290f, iy);
            }
        }

        fTiny.setColor(new Color(0.28f, 0.34f, 0.48f, 0.85f));
        fTiny.draw(bt, "W/S NAVIGATE      ENTER SELECT", mX, 35f);
        bt.end();
    }

    // ── STATE 1: Difficulty ───────────────────────────────────
    private void renderDifficulty(ShapeRenderer sr, SpriteBatch bt) {
        float LW = W * 0.44f;
        float mX = LW + 65f;
        Difficulty[] diffs = {Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD};
        String[] labels = {"EASY", "MEDIUM", "HARD"};
        String[] descs  = {
            "Bigger vision / Slower enemies",
            "Normal vision / Normal enemies",
            "Smaller vision / Faster enemies"
        };
        Color[] colors = {
            new Color(0.1f, 0.95f, 0.5f, 1f),
            new Color(0.05f, 0.85f, 1f, 1f),
            new Color(1f, 0.25f, 0.25f, 1f)
        };

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int i = 0; i < diffs.length; i++) {
            float iy = H / 2f + 90f - i * 120f;
            boolean isSel = sel == i;
            boolean isChosen = chosenDiff == diffs[i];

            if (isSel) {
                Color c = colors[i];
                sr.setColor(new Color(c.r, c.g, c.b, 0.15f));
                sr.rect(mX - 18f, iy - 55f, W - LW - 85f, 75f);
                sr.setColor(c);
                sr.rect(mX - 18f, iy - 55f, 5f, 75f);
            } else if (isChosen) {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.08f));
                sr.rect(mX - 18f, iy - 55f, W - LW - 85f, 75f);
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.4f));
                sr.rect(mX - 18f, iy - 55f, 5f, 75f);
            } else {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.03f));
                sr.rect(mX - 18f, iy - 55f, W - LW - 85f, 75f);
                sr.setColor(new Color(0.3f, 0.35f, 0.45f, 0.3f));
                sr.rect(mX - 18f, iy - 55f, 5f, 75f);
            }
        }
        sr.end();

        bt.begin();
        fItem.setColor(new Color(0.05f, 0.85f, 1f, 0.7f));
        fItem.draw(bt, "DIFFICULTY", mX, H - 55f);

        for (int i = 0; i < diffs.length; i++) {
            float iy = H / 2f + 90f - i * 120f;
            boolean isSel = (sel == i);
            Color c = isSel ? colors[i] : new Color(0.38f, 0.42f, 0.56f, 0.9f);
            fItem.setColor(c);
            fItem.draw(bt, labels[i], mX + 30f, iy);

            fTiny.setColor(new Color(c.r * 0.7f, c.g * 0.7f, c.b * 0.7f, 0.8f));
            fTiny.draw(bt, descs[i], mX + 30f, iy - 28f);

            if (chosenDiff == diffs[i]) {
                fTiny.setColor(new Color(0.05f, 0.85f, 1f, 0.8f));
                fTiny.draw(bt, "[SELECTED]", mX + 30f + 280f, iy);
            }
        }

        fTiny.setColor(new Color(0.28f, 0.34f, 0.48f, 0.85f));
        fTiny.draw(bt, "W/S NAVIGATE   ENTER SELECT   ESC BACK", mX, 35f);
        bt.end();
    }

    // ── STATE 2: Character ────────────────────────────────────
    private void renderCharacter(ShapeRenderer sr, SpriteBatch bt) {
        float LW = W * 0.44f;
        float mX = LW + 65f;

        CharacterType[] chars = {CharacterType.CIRCLE, CharacterType.TRIANGLE, CharacterType.SQUARE};
        String[] names = {"CIRCLE", "TRIANGLE", "SQUARE"};
        float[] previewX = {mX + 80f, mX + 260f, mX + 440f};
        float previewY = H / 2f + 20f;

        sr.begin(ShapeRenderer.ShapeType.Filled);

        for (int i = 0; i < chars.length; i++) {
            float cx = previewX[i];
            boolean isSel = sel == i;
            boolean isChosen = chosenChar == chars[i];

            // Card background
            if (isSel) {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.15f));
                sr.rect(cx - 65f, previewY - 90f, 130f, 175f);
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.8f));
                sr.rect(cx - 65f, previewY - 90f, 130f, 3f);
                sr.rect(cx - 65f, previewY + 82f, 130f, 3f);
                sr.rect(cx - 65f, previewY - 90f, 3f, 175f);
                sr.rect(cx + 62f, previewY - 90f, 3f, 175f);
            } else if (isChosen) {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.06f));
                sr.rect(cx - 65f, previewY - 90f, 130f, 175f);
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.35f));
                sr.rect(cx - 65f, previewY - 90f, 130f, 2f);
            } else {
                sr.setColor(new Color(0.05f, 0.85f, 1f, 0.03f));
                sr.rect(cx - 65f, previewY - 90f, 130f, 175f);
            }

            // Shape preview
            Color shapeColor = isSel
                ? new Color(0.05f, 0.92f, 1f, 1f)
                : new Color(0.35f, 0.40f, 0.55f, 0.9f);
            sr.setColor(shapeColor);

            if (chars[i] == CharacterType.CIRCLE) {
                sr.circle(cx, previewY + 15f, 30f, 48);
            } else if (chars[i] == CharacterType.TRIANGLE) {
                sr.triangle(
                    cx, previewY + 48f,
                    cx - 30f, previewY - 18f,
                    cx + 30f, previewY - 18f
                );
            } else {
                sr.rect(cx - 26f, previewY - 14f, 52f, 52f);
            }
        }
        sr.end();

        bt.begin();
        fItem.setColor(new Color(0.05f, 0.85f, 1f, 0.7f));
        fItem.draw(bt, "CHOOSE CHARACTER", mX, H - 55f);

        for (int i = 0; i < chars.length; i++) {
            float cx = previewX[i];
            boolean isSel = sel == i;
            fSub.setColor(isSel
                ? new Color(0.05f, 0.92f, 1f, 1f)
                : new Color(0.38f, 0.42f, 0.56f, 0.85f));
            gl.setText(fSub, names[i]);
            fSub.draw(bt, names[i], cx - gl.width / 2f, previewY - 52f);

            if (chosenChar == chars[i]) {
                fTiny.setColor(new Color(0.05f, 0.85f, 1f, 0.8f));
                gl.setText(fTiny, "[CHOSEN]");
                fTiny.draw(bt, "[CHOSEN]", cx - gl.width / 2f, previewY - 78f);
            }
        }

        fTiny.setColor(new Color(0.28f, 0.34f, 0.48f, 0.85f));
        fTiny.draw(bt, "A/D NAVIGATE   ENTER SELECT   ESC BACK", mX, 35f);
        bt.end();
    }

    // ── Input ─────────────────────────────────────────────────
    private void handleInput() {
        if (state == 0) {
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))
                sel = (sel - 1 + 3) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
                sel = (sel + 1) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                if (sel == 0) {
                    DifficultyManager.getInstance().set(chosenDiff);
                    GameManager.getInstance().setChosenChar(chosenChar);
                    G.setScreen(new GameScreen(G));
                } else if (sel == 1) {
                    state = 1; sel = 0;
                } else {
                    state = 2; sel = 0;
                }
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) Gdx.app.exit();

        } else if (state == 1) {
            Difficulty[] diffs = {Difficulty.EASY, Difficulty.MEDIUM, Difficulty.HARD};
            if (Gdx.input.isKeyJustPressed(Input.Keys.W) || Gdx.input.isKeyJustPressed(Input.Keys.UP))
                sel = (sel - 1 + 3) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.S) || Gdx.input.isKeyJustPressed(Input.Keys.DOWN))
                sel = (sel + 1) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                chosenDiff = diffs[sel];
                state = 0; sel = 1;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { state = 0; sel = 1; }

        } else {
            CharacterType[] chars = {CharacterType.CIRCLE, CharacterType.TRIANGLE, CharacterType.SQUARE};
            if (Gdx.input.isKeyJustPressed(Input.Keys.A) || Gdx.input.isKeyJustPressed(Input.Keys.LEFT))
                sel = (sel - 1 + 3) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.D) || Gdx.input.isKeyJustPressed(Input.Keys.RIGHT))
                sel = (sel + 1) % 3;
            if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
                chosenChar = chars[sel];
                state = 0; sel = 2;
            }
            if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE)) { state = 0; sel = 2; }
        }
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) {}
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose(){
        fHuge.dispose(); fItem.dispose(); fSub.dispose(); fTiny.dispose();
    }
}
