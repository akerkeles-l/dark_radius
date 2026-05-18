package com.darkradius.maze;

import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;

public class MazeRenderer {

    private MazeData maze;
    private float t = 0;

    private static final Color C_WALL       = new Color(0.07f, 0.07f, 0.10f, 1f);
    private static final Color C_WALL_EDGE  = new Color(0.18f, 0.18f, 0.26f, 1f);
    private static final Color C_WALL_INNER = new Color(0.05f, 0.05f, 0.07f, 1f);
    private static final Color C_FLOOR      = new Color(0.03f, 0.03f, 0.045f, 1f);
    private static final Color C_FLOOR_LINE = new Color(0.055f, 0.055f, 0.075f, 1f);

    public MazeRenderer(MazeData maze) { this.maze = maze; }

    public void update(float delta) { t += delta; }

    public void render(ShapeRenderer sr, float camX, float camY, float vw, float vh) {
        int ts = MazeData.TS;
        int c0 = Math.max(0, (int)(camX / ts) - 1);
        int c1 = Math.min(maze.width,  (int)((camX + vw) / ts) + 2);
        int r0 = Math.max(0, (int)(camY / ts) - 1);
        int r1 = Math.min(maze.height, (int)((camY + vh) / ts) + 2);

        sr.begin(ShapeRenderer.ShapeType.Filled);
        for (int r = r0; r < r1; r++) {
            for (int c = c0; c < c1; c++) {
                float wx = c * ts, wy = r * ts;
                int v = maze.grid[r][c];

                if (v == MazeData.WALL) {
                    sr.setColor(C_WALL);
                    sr.rect(wx, wy, ts, ts);
                    // top highlight
                    sr.setColor(C_WALL_EDGE);
                    sr.rect(wx, wy + ts - 2, ts, 2);
                    sr.rect(wx + ts - 2, wy, 2, ts - 2);
                    // inner shadow
                    sr.setColor(C_WALL_INNER);
                    sr.rect(wx + 1, wy, ts - 3, 2);

                } else if (v == MazeData.EXIT) {
                    float p = (float)(Math.sin(t * 3.5) * 0.4 + 0.6);
                    sr.setColor(new Color(0f, 0.75f * p, 0.45f * p, 1f));
                    sr.rect(wx, wy, ts, ts);
                    // pulsing glow border
                    sr.setColor(new Color(0f, p, 0.6f * p, 0.3f));
                    sr.rect(wx - 3, wy - 3, ts + 6, ts + 6);

                } else { // FLOOR
                    sr.setColor(C_FLOOR);
                    sr.rect(wx, wy, ts, ts);
                    // subtle grid
                    sr.setColor(C_FLOOR_LINE);
                    sr.rect(wx, wy + ts - 1, ts, 1);
                    sr.rect(wx + ts - 1, wy, 1, ts);
                }
            }
        }
        sr.end();
    }

    public void renderExit(ShapeRenderer sr) {
        if (maze.exitPos == null) return;
        float p = (float)(Math.sin(t * 4) * 0.5 + 0.5);
        float x = maze.exitPos.x, y = maze.exitPos.y;
        sr.begin(ShapeRenderer.ShapeType.Filled);
        sr.setColor(new Color(0f, 0.9f, 0.55f, 0.12f * p));
        sr.circle(x, y, 40 + p * 12, 48);
        sr.setColor(new Color(0f, 1f, 0.6f, 0.7f + p * 0.3f));
        sr.circle(x, y, 7f, 32);
        sr.setColor(new Color(1f, 1f, 1f, 0.9f));
        sr.circle(x, y, 2.5f, 16);
        sr.end();
    }

    public void setMaze(MazeData m) { maze = m; }
}
