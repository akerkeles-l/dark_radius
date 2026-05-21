package com.darkradius.patterns.factory;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.darkradius.entities.Enemy;
import com.darkradius.entities.TrapType;
import com.darkradius.patterns.strategy.*;

public class EntityFactory {

    public static Enemy enemy(Vector2 pos, int level) {
        float r = MathUtils.random();
        IEnemyBehavior beh;
        float[] color;

        if (level <= 1) {
            beh   = r < 0.6f ? new SlowGuardBehavior() : new WatcherBehavior();
            color = r < 0.6f ? new float[]{0.2f,0.55f,0.95f} : new float[]{1f,0.35f,0.1f};
        } else if (level <= 3) {
            if (r < 0.33f)      { beh = new SlowGuardBehavior(); color = new float[]{0.2f,0.55f,0.95f}; }
            else if (r < 0.66f) { beh = new WatcherBehavior();   color = new float[]{1f,0.35f,0.1f};    }
            else                { beh = new ListenerBehavior();   color = new float[]{0.65f,0.2f,0.95f}; }
        } else {
            if (r < 0.2f)       { beh = new SlowGuardBehavior(); color = new float[]{0.2f,0.55f,0.95f}; }
            else if (r < 0.55f) { beh = new WatcherBehavior();   color = new float[]{1f,0.35f,0.1f};    }
            else                { beh = new ListenerBehavior();   color = new float[]{0.65f,0.2f,0.95f}; }
        }
        return new Enemy(pos, beh, color[0], color[1], color[2]);
    }

}
