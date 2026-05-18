package com.darkradius.maze;

import com.badlogic.gdx.math.Vector2;
import java.util.*;

public class MazeGenerator {

    private Random rng = new Random();

    public MazeData generate(int w, int h, int enemies, int traps, int level) {
        if (w % 2 == 0) w++; if (h % 2 == 0) h++;
        MazeData m = new MazeData(w, h, level);
        rng = new Random();

        // Fill walls
        for (int r = 0; r < h; r++)
            for (int c = 0; c < w; c++)
                m.grid[r][c] = MazeData.WALL;

        carve(m, 1, 1);

        m.playerStart = new Vector2(m.wx(1), m.wy(1));

        int ec = w - 2, er = h - 2;
        m.grid[er][ec] = MazeData.EXIT;
        m.exitPos = new Vector2(m.wx(ec), m.wy(er));

        placeEntities(m, enemies, traps);
        return m;
    }

    private void carve(MazeData m, int c, int r) {
        m.grid[r][c] = MazeData.FLOOR;
        int[][] dirs = {{2,0},{0,2},{-2,0},{0,-2}};
        shuffle(dirs);
        for (int[] d : dirs) {
            int nc = c + d[0], nr = r + d[1];
            if (nr > 0 && nr < m.height-1 && nc > 0 && nc < m.width-1
                    && m.grid[nr][nc] == MazeData.WALL) {
                m.grid[r + d[1]/2][c + d[0]/2] = MazeData.FLOOR;
                carve(m, nc, nr);
            }
        }
    }

    private void placeEntities(MazeData m, int enemies, int traps) {
        List<int[]> floors = new ArrayList<>();
        for (int r = 0; r < m.height; r++)
            for (int c = 0; c < m.width; c++)
                if (m.grid[r][c] == MazeData.FLOOR) floors.add(new int[]{c, r});
        Collections.shuffle(floors, rng);

        int skip = 6;
        int ep = 0;
        for (int i = skip; i < floors.size() && ep < enemies; i++) {
            int[] t = floors.get(i);
            if (!nearExit(m, t[0], t[1])) {
                m.enemySpawns.add(new Vector2(m.wx(t[0]), m.wy(t[1])));
                ep++; skip = i + 1;
            }
        }
        int tp = 0;
        for (int i = skip; i < floors.size() && tp < traps; i++) {
            int[] t = floors.get(i);
            if (!nearExit(m, t[0], t[1])) {
                m.trapPositions.add(new Vector2(m.wx(t[0]), m.wy(t[1])));
                tp++;
            }
        }
    }

    private boolean nearExit(MazeData m, int c, int r) {
        return Math.abs(c - (m.width-2)) < 3 && Math.abs(r - (m.height-2)) < 3;
    }

    private void shuffle(int[][] a) {
        for (int i = a.length-1; i > 0; i--) {
            int j = rng.nextInt(i+1);
            int[] tmp = a[i]; a[i] = a[j]; a[j] = tmp;
        }
    }
}
