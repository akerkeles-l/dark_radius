package com.darkradius.managers;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;

public class AssetManager {

    private static AssetManager instance;

    public BitmapFont fontSmall;
    public BitmapFont fontMedium;
    public BitmapFont fontLarge;
    public BitmapFont fontHuge;
    public GlyphLayout layout;

    private AssetManager() {}

    public static AssetManager getInstance() {
        if (instance == null) instance = new AssetManager();
        return instance;
    }

    public void load() {
        fontSmall  = new BitmapFont(); fontSmall.getData().setScale(0.9f);
        fontMedium = new BitmapFont(); fontMedium.getData().setScale(1.5f);
        fontLarge  = new BitmapFont(); fontLarge.getData().setScale(2.5f);
        fontHuge   = new BitmapFont(); fontHuge.getData().setScale(4.5f);
        layout     = new GlyphLayout();
    }

    public void dispose() {
        fontSmall.dispose();
        fontMedium.dispose();
        fontLarge.dispose();
        fontHuge.dispose();
    }
}
