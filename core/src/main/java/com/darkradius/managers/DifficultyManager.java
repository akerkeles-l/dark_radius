package com.darkradius.managers;

import com.darkradius.entities.Enemy;
import com.darkradius.entities.Player;
import com.darkradius.patterns.strategy.IEnemyBehavior;

public class DifficultyManager {

    public enum Difficulty { EASY, MEDIUM, HARD }

    private static DifficultyManager instance;
    private Difficulty current = Difficulty.MEDIUM;

    private DifficultyManager() {}

    public static DifficultyManager getInstance() {
        if (instance == null) instance = new DifficultyManager();
        return instance;
    }

    public void set(Difficulty d) { current = d; }
    public Difficulty get()       { return current; }

    public float visionRadius() {
        switch (current) {
            case EASY:   return 160f;
            case HARD:   return 80f;
            default:     return 115f;
        }
    }

    public float enemySpeed() {
        switch (current) {
            case EASY:   return 30f;
            case HARD:   return 75f;
            default:     return 45f;
        }
    }

    public float enemyRange() {
        switch (current) {
            case EASY:   return 55f;
            case HARD:   return 100f;
            default:     return 75f;
        }
    }

    public String label() {
        switch (current) {
            case EASY:   return "EASY";
            case HARD:   return "HARD";
            default:     return "MEDIUM";
        }
    }
}
class WatcherBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return e.getPos().dst(p.getPos()) <= range();
    }
    public float range()  { return DifficultyManager.getInstance().enemyRange(); }
    public String label() { return "WATCHER"; }
}

class ListenerBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return p.isMoving() && e.getPos().dst(p.getPos()) <= range();
    }
    public float range()  { return DifficultyManager.getInstance().enemyRange() * 0.7f; }
    public String label() { return "LISTENER"; }
}

class SlowGuardBehavior implements IEnemyBehavior {
    public boolean detect(Enemy e, Player p) {
        return e.getPos().dst(p.getPos()) <= range();
    }
    public float range()  { return DifficultyManager.getInstance().enemyRange() * 0.55f; }
    public String label() { return "GUARD"; }
}
