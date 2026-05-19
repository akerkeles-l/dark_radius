package com.darkradius.managers;

import com.darkradius.maze.MazeData;
import com.darkradius.maze.MazeGenerator;

public class LevelManager {

    private static LevelManager instance;
    private MazeGenerator generator = new MazeGenerator();
    private MazeData current;

    // [width, height, enemies, traps]
    private static final int[][] CFG = {
        {29, 13, 2, 0},
        {29, 13, 4, 0},
        {29, 13, 6, 0},
        {29, 13, 8, 0},
        {29, 13, 10, 0},
    };

    private LevelManager() {}

    public static LevelManager getInstance() {
        if (instance == null) instance = new LevelManager();
        return instance;
    }

    public MazeData generate(int level) {
        int i = Math.min(level - 1, CFG.length - 1);
        current = generator.generate(CFG[i][0], CFG[i][1], CFG[i][2], CFG[i][3], level);
        return current;
    }

    public MazeData getCurrent() { return current; }

    public float speedMult(int level)  { return 1f + (level - 1) * 0.18f; }
    public float visionMult(int level) { return Math.max(0.65f, 1f - (level - 1) * 0.08f); }
}
