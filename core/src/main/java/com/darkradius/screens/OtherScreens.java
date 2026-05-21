package com.darkradius.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.*;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.utils.ScreenUtils;
import com.darkradius.DarkRadiusGame;
import com.darkradius.managers.GameManager;

// ══════════════════════════════════════════════════════════════
//  PAUSE SCREEN
// ══════════════════════════════════════════════════════════════
class PauseScreen implements Screen {

    private final DarkRadiusGame G;
    private final Screen prev;
    private BitmapFont fBig, fItem, fSub, fTiny;
    private GlyphLayout gl = new GlyphLayout();
    private float t = 0f;

    private static final int W = DarkRadiusGame.W;
    private static final int H = DarkRadiusGame.H;

    PauseScreen(DarkRadiusGame g, Screen prev) {
        G = g; this.prev = prev;
        fBig  = new BitmapFont(); fBig.getData().setScale(7f);
        fItem = new BitmapFont(); fItem.getData().setScale(2.8f);
        fSub  = new BitmapFont(); fSub.getData().setScale(1.3f);
        fTiny = new BitmapFont(); fTiny.getData().setScale(1.1f);
    }

    @Override public void render(float delta) {
        t += delta;
        Matrix4 mat = new Matrix4().setToOrtho2D(0, 0, W, H);
        G.shape.setProjectionMatrix(mat);
        G.batch.setProjectionMatrix(mat);

        ScreenUtils.clear(0.018f, 0.012f, 0.028f, 1f);

        ShapeRenderer sr = G.shape;
        SpriteBatch   bt = G.batch;

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Sol panel
        float LW = W * 0.5f;
        sr.setColor(0.010f, 0.006f, 0.018f, 1f);
        sr.rect(0, 0, LW, H);

        // Glow behind PAUSED
        float gp = (float)(Math.sin(t * 1.8f) * 0.4f + 0.5f);
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.05f * gp));
        sr.circle(LW / 2f, H / 2f + 30f, 180f, 48);

        // Divider
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.85f));
        sr.rect(LW, 0, 3f, H);
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.06f));
        sr.rect(LW - 12f, 0, 12f, H);
        sr.rect(LW + 3f,  0, 12f, H);

        // Oң panel
        sr.setColor(0.012f, 0.008f, 0.020f, 1f);
        sr.rect(LW + 3f, 0, W - LW - 3f, H);

        // Brackets oң
        float bx = LW + 22f;
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.55f));
        sr.rect(W - 55f, H - 5f, 50f, 5f);
        sr.rect(W - 5f, H - 55f, 5f, 55f);
        sr.rect(bx, H - 5f, 50f, 5f);
        sr.rect(bx, H - 55f, 5f, 55f);
        sr.rect(W - 55f, 0f, 50f, 5f);
        sr.rect(W - 5f, 0f, 5f, 55f);
        sr.rect(bx, 0f, 50f, 5f);
        sr.rect(bx, 0f, 5f, 55f);

        // Menu rows
        String[][] rows = {{"RESUME","ENTER / ESC"},{"RESTART","R"},{"MAIN MENU","M"}};
        float rX = LW + 60f, rSY = H / 2f + 80f, rGap = 100f;
        for (int i = 0; i < rows.length; i++) {
            float ry = rSY - i * rGap;
            sr.setColor(new Color(0.05f, 0.85f, 1f, 0.04f));
            sr.rect(rX - 10f, ry - 46f, W - LW - 72f, 60f);
            sr.setColor(new Color(0.05f, 0.85f, 1f, i == 0 ? 0.7f : 0.22f));
            sr.rect(rX - 10f, ry - 46f, 5f, 60f);
        }

        // Bottom bar
        sr.setColor(0f, 0f, 0f, 0.7f);
        sr.rect(LW + 3f, 0, W - LW, 50f);
        sr.setColor(new Color(0.05f, 0.85f, 1f, 0.18f));
        sr.rect(LW + 3f, 50f, W - LW, 1f);

        sr.end();

        bt.begin();

        // Sol: PAUSED
        float pp = (float)(Math.sin(t * 2f) * 0.06f + 0.94f);
        fBig.setColor(new Color(0.05f * pp, 0.88f * pp, 1f * pp, 1f));
        gl.setText(fBig, "PAUSED");
        fBig.draw(bt, "PAUSED", LW / 2f - gl.width / 2f, H / 2f + 100f);

        fSub.setColor(new Color(0.15f, 0.20f, 0.32f, 0.8f));
        gl.setText(fSub, "GAME IS PAUSED");
        fSub.draw(bt, "GAME IS PAUSED", LW / 2f - gl.width / 2f, H / 2f - 10f);

        // Stats
        fSub.setColor(new Color(0.18f, 0.22f, 0.34f, 0.8f));
        fSub.draw(bt, "LEVEL   " + GameManager.getInstance().getLevel(), 50f, H / 2f - 80f);
        fSub.draw(bt, "SCORE   " + GameManager.getInstance().getScore(), 50f, H / 2f - 115f);
        fSub.draw(bt, "LIVES   " + GameManager.getInstance().getLives(), 50f, H / 2f - 150f);

        // Oң: OPTIONS header
        fSub.setColor(new Color(0.05f, 0.85f, 1f, 0.55f));
        fSub.draw(bt, "OPTIONS", rX, H - 50f);

        for (int i = 0; i < rows.length; i++) {
            float ry = rSY - i * rGap;
            fItem.setColor(i == 0
                ? new Color(0.85f, 0.90f, 1f, 0.95f)
                : new Color(0.35f, 0.40f, 0.55f, 0.9f));
            fItem.draw(bt, rows[i][0], rX + 14f, ry);

            fTiny.setColor(new Color(0.05f, 0.85f, 1f, i == 0 ? 0.7f : 0.35f));
            fTiny.draw(bt, "[ " + rows[i][1] + " ]", rX + 14f, ry - 26f);
        }

        // Bottom
        fTiny.setColor(new Color(0.22f, 0.26f, 0.38f, 0.7f));
        fTiny.draw(bt, "Level " + GameManager.getInstance().getLevel()
                + "   Score " + GameManager.getInstance().getScore()
                + "   Lives " + GameManager.getInstance().getLives(),
            rX, 35f);

        bt.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE) || Gdx.input.isKeyJustPressed(Input.Keys.ENTER))
            G.setScreen(prev);
        if (Gdx.input.isKeyJustPressed(Input.Keys.R))
            G.setScreen(new GameScreen(G));
        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            G.setScreen(new MenuScreen(G));
    }

    @Override public void show() {} @Override public void resize(int w, int h) {}
    @Override public void pause() {} @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose(){
        fBig.dispose(); fItem.dispose(); fSub.dispose(); fTiny.dispose();
    }
}

// ══════════════════════════════════════════════════════════════
//  GAME OVER SCREEN
// ══════════════════════════════════════════════════════════════
class GameOverScreen implements Screen {

    private final DarkRadiusGame G;
    private BitmapFont fMassive, fBig, fSub, fTiny;
    private GlyphLayout gl = new GlyphLayout();
    private float t = 0f;
    private float fadeIn = 0f;
    private final float[][] scan = new float[45][4];

    private static final int W = DarkRadiusGame.W;
    private static final int H = DarkRadiusGame.H;

    GameOverScreen(DarkRadiusGame g) {
        G = g;
        fMassive = new BitmapFont(); fMassive.getData().setScale(10f);
        fBig     = new BitmapFont(); fBig.getData().setScale(3.5f);
        fSub     = new BitmapFont(); fSub.getData().setScale(1.3f);
        fTiny    = new BitmapFont(); fTiny.getData().setScale(1.1f);
        for (float[] s : scan) resetScan(s, true);
    }

    private void resetScan(float[] s, boolean rnd) {
        s[0] = MathUtils.random(0f, W);
        s[1] = rnd ? MathUtils.random(0f, H) : H + 4f;
        s[2] = MathUtils.random(35f, 100f);
        s[3] = MathUtils.random(0.05f, 0.28f);
    }

    @Override public void render(float delta) {
        t += delta;
        fadeIn = Math.min(1f, fadeIn + delta * 1.1f);
        for (float[] s : scan) {
            s[1] -= s[2] * delta;
            if (s[1] < -4f) resetScan(s, false);
        }

        Matrix4 mat = new Matrix4().setToOrtho2D(0, 0, W, H);
        G.shape.setProjectionMatrix(mat);
        G.batch.setProjectionMatrix(mat);

        ScreenUtils.clear(0.030f, 0.004f, 0.008f, 1f);

        ShapeRenderer sr = G.shape;
        SpriteBatch   bt = G.batch;

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Qyzyl radial glow
        for (int i = 10; i >= 0; i--) {
            float a = 0.045f * (float)(Math.sin(t * 1.8f + i * 0.3f) * 0.2f + 0.8f);
            sr.setColor(new Color(0.6f, 0f, 0f, a));
            sr.ellipse(W * 0.75f - (80 + i * 65f), H / 2f - (48 + i * 38f),
                160 + i * 130f, 96 + i * 76f);
        }

        // Scan particles
        for (float[] s : scan) {
            sr.setColor(new Color(1f, 0.06f, 0.10f, s[3] * fadeIn));
            sr.circle(s[0], s[1], 2f, 6);
        }

        // Glitch lines
        for (int i = 0; i < 4; i++) {
            float a = (float)(Math.sin(t * 14f + i * 1.8f) * 0.04f + 0.04f);
            sr.setColor(new Color(1f, 0.06f, 0.10f, a));
            sr.rect(0, MathUtils.random(0f, H), W, MathUtils.random(1f, 5f));
        }

        // Sol data panel
        float LW = W * 0.38f;
        sr.setColor(0.018f, 0.002f, 0.005f, 0.92f);
        sr.rect(0, 0, LW, H);
        sr.setColor(new Color(1f, 0.12f, 0.18f, 0.55f));
        sr.rect(LW, 0, 3f, H);
        sr.setColor(new Color(1f, 0.12f, 0.18f, 0.07f));
        sr.rect(LW + 3f, 0, 10f, H);

        // Data rows bg
        sr.setColor(new Color(1f, 0.06f, 0.10f, 0.07f));
        for (int i = 0; i < 3; i++)
            sr.rect(22f, H - 140f - i * 110f, LW - 40f, 80f);

        // Bottom bar
        sr.setColor(0f, 0f, 0f, 0.75f);
        sr.rect(0, 0, W, 52f);
        sr.setColor(new Color(1f, 0.12f, 0.18f, 0.28f));
        sr.rect(0, 52f, W, 1f);

        sr.end();

        bt.begin();

        float fa = fadeIn;
        float pp = (float)(Math.sin(t * 2.2f) * 0.07f + 0.93f);

        // Sol: Mission Report label
        fSub.setColor(new Color(0.6f, 0.12f, 0.18f, 0.7f * fa));
        fSub.draw(bt, "MISSION REPORT", 28f, H - 42f);

        // Data entries
        String[][] data = {
            {"LEVEL REACHED", "" + GameManager.getInstance().getLevel() + " / 5"},
            {"FINAL SCORE",   String.format("%06d", GameManager.getInstance().getScore())},
            {"LIVES LEFT",    "" + GameManager.getInstance().getLives()},
        };
        for (int i = 0; i < data.length; i++) {
            float dy = H - 118f - i * 110f;
            fTiny.setColor(new Color(0.55f, 0.14f, 0.20f, fa));
            fTiny.draw(bt, data[i][0], 28f, dy + 32f);

            if (i == 1) fBig.setColor(new Color(1f, 0.80f, 0.06f, fa));
            else        fBig.setColor(new Color(1f, 0.14f, 0.20f, fa));
            fBig.draw(bt, data[i][1], 28f, dy);
        }

        // Oң: YOU DIED
        fMassive.setColor(new Color(1f * pp, 0.06f, 0.08f, fa));
        gl.setText(fMassive, "YOU");
        fMassive.draw(bt, "YOU", W * 0.72f - gl.width / 2f, H - 25f);

        fMassive.setColor(new Color(0.88f * pp, 0.88f * pp, 0.96f, fa));
        gl.setText(fMassive, "DIED");
        fMassive.draw(bt, "DIED", W * 0.72f - gl.width / 2f, H - 195f);

        // Bottom
        float ba = (float)(Math.sin(t * 3.5f) * 0.3f + 0.7f) * fa;
        fSub.setColor(new Color(0.7f, 0.18f, 0.22f, ba));
        fSub.draw(bt, "ENTER - Try Again", 50f, 36f);
        fSub.setColor(new Color(0.42f, 0.12f, 0.16f, ba));
        fSub.draw(bt, "M - Main Menu", 480f, 36f);

        bt.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GameManager.getInstance().reset();
            G.setScreen(new GameScreen(G));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            G.setScreen(new MenuScreen(G));
    }

    @Override public void show() {} @Override public void resize(int w, int h) {}
    @Override public void pause() {} @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose(){
        fMassive.dispose(); fBig.dispose(); fSub.dispose(); fTiny.dispose();
    }
}

// ══════════════════════════════════════════════════════════════
//  WIN SCREEN
// ══════════════════════════════════════════════════════════════
class WinScreen implements Screen {

    private final DarkRadiusGame G;
    private BitmapFont fMassive, fBig, fSub, fTiny;
    private GlyphLayout gl = new GlyphLayout();
    private float t = 0f;
    private float fadeIn = 0f;
    private final float[][] sparks = new float[70][5];

    private static final int W = DarkRadiusGame.W;
    private static final int H = DarkRadiusGame.H;

    WinScreen(DarkRadiusGame g) {
        G = g;
        fMassive = new BitmapFont(); fMassive.getData().setScale(9f);
        fBig     = new BitmapFont(); fBig.getData().setScale(3.5f);
        fSub     = new BitmapFont(); fSub.getData().setScale(1.3f);
        fTiny    = new BitmapFont(); fTiny.getData().setScale(1.1f);
        for (float[] s : sparks) resetSpark(s, true);
    }

    private void resetSpark(float[] s, boolean rnd) {
        s[0] = MathUtils.random(0f, W);
        s[1] = rnd ? MathUtils.random(0f, H) : -5f;
        s[2] = MathUtils.random(30f, 90f);
        s[3] = MathUtils.random(1.5f, 4f);
        s[4] = MathUtils.random(0f, 1f);
    }

    @Override public void render(float delta) {
        t += delta;
        fadeIn = Math.min(1f, fadeIn + delta);
        for (float[] s : sparks) {
            s[1] += s[2] * delta;
            if (s[1] > H + 5f) resetSpark(s, false);
        }

        Matrix4 mat = new Matrix4().setToOrtho2D(0, 0, W, H);
        G.shape.setProjectionMatrix(mat);
        G.batch.setProjectionMatrix(mat);

        ScreenUtils.clear(0f, 0.022f, 0.014f, 1f);

        ShapeRenderer sr = G.shape;
        SpriteBatch   bt = G.batch;

        sr.begin(ShapeRenderer.ShapeType.Filled);

        // Jasyl radial glow
        for (int i = 10; i >= 0; i--) {
            float a = 0.04f * (float)(Math.sin(t * 1.6f + i * 0.35f) * 0.25f + 0.75f);
            sr.setColor(new Color(0f, 0.65f, 0.38f, a));
            sr.ellipse(W * 0.38f - (75 + i * 58f), H / 2f - (44 + i * 34f),
                150 + i * 116f, 88 + i * 68f);
        }

        // Sparks
        for (float[] s : sparks) {
            float h = s[4];
            sr.setColor(new Color(h * 0.2f, 0.75f + h * 0.25f, (1f - h) * 0.6f, 0.55f * fadeIn));
            sr.circle(s[0], s[1], s[3], 8);
        }

        // Oң panel
        float RX = W * 0.62f;
        sr.setColor(0f, 0.014f, 0.010f, 0.88f);
        sr.rect(RX, 0, W - RX, H);
        sr.setColor(new Color(0f, 0.85f, 0.5f, 0.5f));
        sr.rect(RX, 0, 3f, H);
        sr.setColor(new Color(0f, 0.85f, 0.5f, 0.06f));
        sr.rect(RX - 10f, 0, 10f, H);

        // Brackets
        sr.setColor(new Color(0f, 0.85f, 0.5f, 0.55f));
        sr.rect(W - 55f, H - 5f, 50f, 5f);
        sr.rect(W - 5f, H - 55f, 5f, 55f);
        sr.rect(RX + 22f, H - 5f, 50f, 5f);
        sr.rect(RX + 22f, H - 55f, 5f, 55f);
        sr.rect(W - 55f, 0f, 50f, 5f);
        sr.rect(W - 5f, 0f, 5f, 55f);
        sr.rect(RX + 22f, 0f, 50f, 5f);
        sr.rect(RX + 22f, 0f, 5f, 55f);

        // Data rows bg
        sr.setColor(new Color(0f, 0.85f, 0.5f, 0.06f));
        for (int i = 0; i < 3; i++)
            sr.rect(RX + 26f, H - 140f - i * 110f, W - RX - 40f, 80f);

        // Bottom bar
        sr.setColor(0f, 0f, 0f, 0.7f);
        sr.rect(0, 0, W, 52f);
        sr.setColor(new Color(0f, 0.85f, 0.5f, 0.22f));
        sr.rect(0, 52f, W, 1f);

        sr.end();

        bt.begin();

        float fa = fadeIn;
        float pp = (float)(Math.sin(t * 2.5f) * 0.08f + 0.92f);

        // Sol: YOU ESCAPED
        fMassive.setColor(new Color(0f, pp * 0.88f, pp * 0.52f, fa));
        gl.setText(fMassive, "YOU");
        fMassive.draw(bt, "YOU", W * 0.31f - gl.width / 2f, H - 28f);

        fMassive.setColor(new Color(0.88f * pp, 0.92f * pp, 1f, fa));
        gl.setText(fMassive, "ESCAPED");
        fMassive.draw(bt, "ESCAPED", W * 0.31f - gl.width / 2f, H - 195f);

        fSub.setColor(new Color(0.1f, 0.38f, 0.25f, 0.7f * fa));
        gl.setText(fSub, "YOU SURVIVED THE DARKNESS");
        fSub.draw(bt, "YOU SURVIVED THE DARKNESS", W * 0.31f - gl.width / 2f, H - 235f);

        // Oң: Final Report
        fSub.setColor(new Color(0f, 0.65f, 0.42f, 0.65f * fa));
        fSub.draw(bt, "FINAL REPORT", RX + 34f, H - 42f);

        String[][] stats = {
            {"LEVELS CLEARED", "5 / 5"},
            {"FINAL SCORE",    String.format("%06d", GameManager.getInstance().getScore())},
            {"LIVES REMAINING","" + GameManager.getInstance().getLives()},
        };
        for (int i = 0; i < stats.length; i++) {
            float sy2 = H - 118f - i * 110f;
            fTiny.setColor(new Color(0.12f, 0.42f, 0.28f, fa));
            fTiny.draw(bt, stats[i][0], RX + 34f, sy2 + 32f);

            if (i == 1) fBig.setColor(new Color(1f, 0.80f, 0.06f, fa));
            else        fBig.setColor(new Color(0f, pp * 0.92f, pp * 0.56f, fa));
            fBig.draw(bt, stats[i][1], RX + 34f, sy2);
        }

        // Bottom
        float ba = (float)(Math.sin(t * 3f) * 0.3f + 0.7f) * fa;
        fSub.setColor(new Color(0.15f, 0.55f, 0.35f, ba));
        fSub.draw(bt, "ENTER - Play Again", 50f, 36f);
        fSub.setColor(new Color(0.10f, 0.32f, 0.22f, ba));
        fSub.draw(bt, "M - Main Menu", 480f, 36f);

        bt.end();

        if (Gdx.input.isKeyJustPressed(Input.Keys.ENTER) || Gdx.input.isKeyJustPressed(Input.Keys.SPACE)) {
            GameManager.getInstance().reset();
            G.setScreen(new GameScreen(G));
        }
        if (Gdx.input.isKeyJustPressed(Input.Keys.M))
            G.setScreen(new MenuScreen(G));
    }

    @Override public void show() {} @Override public void resize(int w, int h) {}
    @Override public void pause() {} @Override public void resume() {}
    @Override public void hide() {}
    @Override public void dispose(){
        fMassive.dispose(); fBig.dispose(); fSub.dispose(); fTiny.dispose();
    }
}
