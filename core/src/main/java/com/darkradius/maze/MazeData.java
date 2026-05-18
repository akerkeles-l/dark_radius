package com.darkradius.maze;

import com.badlogic.gdx.math.Vector2;
import java.util.ArrayList;
import java.util.List;

public class MazeData {

    public static final int WALL  = 1;
    public static final int FLOOR = 0;
    public static final int EXIT  = 2;
    public static final int TS = 40; // tile size px

    public int[][] grid;
    public int width, height, level;
    public Vector2 playerStart;
    public Vector2 exitPos;
    public List<Vector2> enemySpawns = new ArrayList<>();
    public List<Vector2> trapPositions = new ArrayList<>();

    public MazeData(int w, int h, int level) {
        width = w; height = h; this.level = level;
        grid = new int[h][w];
    }

    public boolean isWall(int c, int r) {
        if (c < 0 || c >= width || r < 0 || r >= height) return true;
        return grid[r][c] == WALL;
    }

    public int col(float x) { return (int)(x / TS); }
    public int row(float y) { return (int)(y / TS); }
    public float wx(int c) { return c * TS + TS / 2f; }
    public float wy(int r) { return r * TS + TS / 2f; }
}
