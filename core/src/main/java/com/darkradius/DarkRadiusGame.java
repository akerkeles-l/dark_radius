package com.darkradius;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.darkradius.managers.GameManager;
import com.darkradius.screens.MenuScreen;

public class DarkRadiusGame extends Game {

    public SpriteBatch   batch;
    public ShapeRenderer shape;

    public static final int W = 1200;
    public static final int H = 600;

    @Override
    public void create() {
        batch = new SpriteBatch();
        shape = new ShapeRenderer();
        GameManager.getInstance().init(this);
        setScreen(new MenuScreen(this));
    }

    @Override
    public void render() { super.render(); }

    @Override
    public void dispose() {
        batch.dispose();
        shape.dispose();
    }
}
