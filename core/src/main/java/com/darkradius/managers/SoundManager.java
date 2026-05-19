package com.darkradius.managers;

public class SoundManager {

    private static SoundManager instance;
    private boolean soundOn = true;
    private boolean musicOn = true;

    private SoundManager() {}

    public static SoundManager getInstance() {
        if (instance == null) instance = new SoundManager();
        return instance;
    }

    public void init() {
        // Sounds loaded from assets/sounds/ when files exist
    }

    public void toggleSound() { soundOn = !soundOn; }
    public void toggleMusic() { musicOn = !musicOn; }
    public boolean isSoundOn() { return soundOn; }
    public boolean isMusicOn() { return musicOn; }

    public void dispose() {}
}
