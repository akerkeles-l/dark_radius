package com.darkradius.ui;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.FrameBuffer;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;

public class FogOfWar {

    private FrameBuffer   fbo;
    private ShapeRenderer fbShape;
    private OrthographicCamera fbCam;
    private int W, H;

    public FogOfWar(int w, int h) {
        W = w; H = h;
        init();
    }

    private void init() {
        fbo     = new FrameBuffer(Pixmap.Format.RGBA8888, W, H, false);
        fbShape = new ShapeRenderer();
        fbCam   = new OrthographicCamera(W, H);
        fbCam.position.set(W / 2f, H / 2f, 0);
        fbCam.update();
    }

    public void render(SpriteBatch batch, float sx, float sy, float radius) {

        fbo.begin();

        Gdx.gl.glClearColor(0, 0, 0, 0);
        Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

        fbShape.setProjectionMatrix(fbCam.combined);

        fbShape.begin(ShapeRenderer.ShapeType.Filled);
        fbShape.setColor(0f, 0f, 0f, 0.97f);
        fbShape.rect(0, 0, W, H);
        fbShape.end();

        Gdx.gl.glEnable(GL20.GL_BLEND);
        Gdx.gl.glBlendFunc(GL20.GL_ZERO, GL20.GL_ONE_MINUS_SRC_ALPHA);

        fbShape.begin(ShapeRenderer.ShapeType.Filled);
        int steps = 16;
        for (int i = steps; i >= 0; i--) {
            float frac  = (float) i / steps;
            float alpha = frac * frac * frac; // cubic falloff
            float ring  = radius * (1f - frac * 0.28f);
            fbShape.setColor(0f, 0f, 0f, alpha);
            fbShape.circle(sx, sy, ring, 80);
        }
        fbShape.end();

        Gdx.gl.glBlendFunc(GL20.GL_SRC_ALPHA, GL20.GL_ONE_MINUS_SRC_ALPHA);

        fbo.end();

        Texture tex = fbo.getColorBufferTexture();
        tex.setFilter(Texture.TextureFilter.Linear, Texture.TextureFilter.Linear);

        batch.begin();
        batch.draw(tex, 0, H, W, -H); // Y flip
        batch.end();
    }

    public void resize(int w, int h) {
        W = w; H = h;
        fbo.dispose();
        fbShape.dispose();
        init();
    }

    public void dispose() {
        fbo.dispose();
        fbShape.dispose();
    }
}
