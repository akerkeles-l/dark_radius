package com.darkradius.managers;

import com.darkradius.DarkRadiusGame;
import com.darkradius.entities.CharacterType;

public class GameManager {

    private static GameManager instance;
    private DarkRadiusGame game;

    private int level  = 1;
    private int score  = 0;
    private int lives  = 3;
    private float visionRadius = 110f;
    private CharacterType chosenChar = CharacterType.CIRCLE;

    private GameManager() {}

    public static GameManager getInstance() {
        if (instance == null) instance = new GameManager();
        return instance;
    }

    public CharacterType getChosenChar()       { return chosenChar; }
    public void setChosenChar(CharacterType c) { chosenChar = c; }

    public void init(DarkRadiusGame g) { this.game = g; }
    public DarkRadiusGame getGame()    { return game; }

    public int   getLevel()           { return level; }
    public void  setLevel(int l)      { level = l; }
    public void  nextLevel()          { if (level < 5) level++; }

    public int   getScore()           { return score; }
    public void  addScore(int pts)    { score += pts; }

    public int   getLives()           { return lives; }
    public void  loseLife()           { if (lives > 0) lives--; }
    public boolean isGameOver()       { return lives <= 0; }

    public float getVisionRadius()    { return visionRadius; }
    public void  setVisionRadius(float r) { visionRadius = r; }

    public void reset() {
        level = 1; score = 0; lives = 3; visionRadius = 110f;
        chosenChar = CharacterType.CIRCLE;
    }
}
