package com.darkradius.screens;

import com.badlogic.gdx.*;
import com.badlogic.gdx.graphics.*;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Matrix4;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.utils.ScreenUtils;
import com.darkradius.DarkRadiusGame;
import com.darkradius.entities.*;
import com.darkradius.managers.*;
import com.darkradius.maze.*;
import com.darkradius.patterns.factory.EntityFactory;
import com.darkradius.patterns.observer.*;
import com.darkradius.ui.FogOfWar;
import com.darkradius.ui.GameHUD;
import com.darkradius.entities.Key;
import com.darkradius.managers.DifficultyManager;

import java.util.ArrayList;
import java.util.List;

public class GameScreen implements Screen, IGameObserver {

    private final DarkRadiusGame G;
    private final OrthographicCamera cam;

    private MazeData     maze;
    private MazeRenderer mazeRen;
    private Player       player;
    private List<Enemy>  enemies = new ArrayList<>();
    private FogOfWar     fog;
    private GameHUD      hud;

    private List<Key> keys      = new ArrayList<>();
    private int keysCollected   = 0;
    private static final int KEYS_NEEDED = 3;

    private boolean levelDone  = false;
    private boolean playerDead = false;
    private float   flowTimer  = 0f;
    private float   introAlpha = 1f;

    private static final int W = DarkRadiusGame.W;
    private static final int H = DarkRadiusGame.H;

    private List<Vector2> getRandomFloors(MazeData m, int count) {
        List<Vector2> allFloors = new ArrayList<>();

        for (int r = 0; r < m.height; r++) {
            for (int c = 0; c < m.width; c++) {
                if (m.grid[r][c] != MazeData.FLOOR) continue;

                // 4 қасындағы тайл да floor болу керек (ашық кеңістік)
                boolean openUp    = r + 1 < m.height && m.grid[r+1][c] == MazeData.FLOOR;
                boolean openDown  = r - 1 >= 0        && m.grid[r-1][c] == MazeData.FLOOR;
                boolean openRight = c + 1 < m.width   && m.grid[r][c+1] == MazeData.FLOOR;
                boolean openLeft  = c - 1 >= 0        && m.grid[r][c-1] == MazeData.FLOOR;

                // Кемінде 2 жағы ашық болсын
                int openCount = (openUp?1:0)+(openDown?1:0)+(openRight?1:0)+(openLeft?1:0);
                if (openCount < 2) continue;

                float wx = c * MazeData.TS + MazeData.TS / 2f;
                float wy = r * MazeData.TS + MazeData.TS / 2f;
                Vector2 wp = new Vector2(wx, wy);

                if (wp.dst(m.playerStart) > 150f && wp.dst(m.exitPos) > 100f) {
                    allFloors.add(wp);
                }
            }
        }

        java.util.Collections.shuffle(allFloors, new java.util.Random());
        List<Vector2> result = new ArrayList<>();
        for (int i = 0; i < Math.min(count, allFloors.size()); i++) {
            result.add(allFloors.get(i));
        }
        return result;
    }


    public GameScreen(DarkRadiusGame g) {
        G   = g;
        cam = new OrthographicCamera(W, H);
        fog = new FogOfWar(W, H);
        hud = new GameHUD(W, H);

        GameEventBus.get().clear();
        GameEventBus.get().sub(GameEvent.PLAYER_DIED,    this);
        GameEventBus.get().sub(GameEvent.LEVEL_COMPLETE, this);

        loadLevel(GameManager.getInstance().getLevel());
    }

    private void loadLevel(int lvl) {
        enemies.clear();
        keys.clear();
        keysCollected = 0;
        levelDone  = false;
        playerDead = false;
        flowTimer  = 0f;
        introAlpha = 1f;

        GameManager.getInstance().setVisionRadius(
            DifficultyManager.getInstance().visionRadius()
        );

        maze    = LevelManager.getInstance().generate(lvl);
        mazeRen = new MazeRenderer(maze);
        player  = new Player(maze.playerStart, maze);

        // Enemies
        for (Vector2 sp : maze.enemySpawns) {
            Enemy e = EntityFactory.enemy(sp, lvl);
            e.setMaze(maze);
            enemies.add(e);
        }

        // 3 Key — random floor тайлдарға
        List<Vector2> floors = getRandomFloors(maze, 3);
        for (Vector2 kp : floors) keys.add(new Key(kp));

        snapCamera();
    }

    @Override
    public void render(float delta) {
        ScreenUtils.clear(0.015f, 0.010f, 0.020f, 1f);

        introAlpha = Math.max(0f, introAlpha - delta * 1.4f);

        if (!playerDead && !levelDone) update(delta);
        mazeRen.update(delta);

        snapCamera();




        // ── World projection ──────────────────────────────────
        G.shape.setProjectionMatrix(cam.combined);
        G.batch.setProjectionMatrix(cam.combined);

        float camL = cam.position.x - W / 2f;
        float camB = cam.position.y - H / 2f;

        // Render world
        mazeRen.render(G.shape, camL, camB, W, H);
        for (Enemy e : enemies) e.render(G.shape);
        mazeRen.renderExit(G.shape);
        player.render(G.shape);

        // ── Screen projection ─────────────────────────────────
        Matrix4 screenMat = new Matrix4().setToOrtho2D(0, 0, W, H);

        // Fog of war
        float sx = player.getPos().x - camL;
        float sy = player.getPos().y - camB;
        G.batch.setProjectionMatrix(screenMat);
//        fog.render(G.batch, sx, sy, player.getVision());

        // HUD
        G.shape.setProjectionMatrix(screenMat);
        hud.render(G.shape, G.batch,
            player.getHealth(), player.getMaxHp(),
            GameManager.getInstance().getLevel(),
            GameManager.getInstance().getScore(),
            enemies.size(), keysCollected, KEYS_NEEDED, delta);

        // Intro fade
        if (introAlpha > 0f) {
            G.shape.begin(ShapeRenderer.ShapeType.Filled);
            G.shape.setColor(0f, 0f, 0f, introAlpha);
            G.shape.rect(0, 0, W, H);
            G.shape.end();
        }

        // Flow timer
        if (levelDone || playerDead) {
            flowTimer += delta;
            if (levelDone  && flowTimer > 2.8f) nextLevel();
            if (playerDead && flowTimer > 2.8f) G.setScreen(new GameOverScreen(G));
        }

        if (Gdx.input.isKeyJustPressed(Input.Keys.ESCAPE))
            G.setScreen(new PauseScreen(G, this));

        // Maze render-ден кейін:
        for (Key k : keys) k.render(G.shape);
    }

    private void update(float delta) {
        player.update(delta);

        for (Enemy e : enemies) e.update(delta, player);
        enemies.removeIf(e -> !e.isAlive());

        // Keys жинау
        for (Key k : keys) {
            boolean wasColl = k.isCollected();
            k.update(delta, player);
            if (!wasColl && k.isCollected()) {
                keysCollected++;
                GameManager.getInstance().addScore(100);
            }
        }

        // Exit — тек 3 кілт жиналса ғана
        if (!levelDone && keysCollected >= KEYS_NEEDED
            && maze.exitPos != null
            && player.getPos().dst(maze.exitPos) < MazeData.TS * 0.65f) {
            levelDone = true;
            flowTimer = 0f;
            GameManager.getInstance().addScore(200);
            GameEventBus.get().emit(GameEvent.LEVEL_COMPLETE);
        }
    }

    private void nextLevel() {
        GameManager.getInstance().nextLevel();
        int lvl = GameManager.getInstance().getLevel();
        if (lvl > 5) G.setScreen(new WinScreen(G));
        else          loadLevel(lvl);
    }

    private void snapCamera() {
        float mazeW = maze.width  * MazeData.TS;
        float mazeH = maze.height * MazeData.TS;
        cam.position.set(mazeW / 2f, mazeH / 2f, 0);
        cam.update();
    }

    private void smoothCamera(float dt) {
        // Камера қозғалмайды — орталықта тіркелген
    }

    @Override
    public void onEvent(GameEvent event, Object data) {
        if (event == GameEvent.PLAYER_DIED)    { playerDead = true; flowTimer = 0f; GameManager.getInstance().loseLife(); }
        if (event == GameEvent.LEVEL_COMPLETE) { levelDone  = true; flowTimer = 0f; }
    }

    @Override public void show()   {}
    @Override public void resize(int w, int h) { fog.resize(w, h); }
    @Override public void pause()  {}
    @Override public void resume() {}
    @Override public void hide()   {}
    @Override public void dispose(){ fog.dispose(); hud.dispose(); }
}
