package com.darkradius.patterns.command;

import com.badlogic.gdx.math.Vector2;
import com.darkradius.entities.Player;

// ── Move commands ─────────────────────────────────────────────
class MoveCommand implements ICommand {
    private final Player p;
    private final float dx, dy;
    private Vector2 prev;

    public MoveCommand(Player p, float dx, float dy) {
        this.p = p;
        this.dx = dx;
        this.dy = dy;
    }

    public void execute() {
        prev = p.getPos().cpy();
        p.applyMove(dx, dy);
    }

    public void undo() {
        if (prev != null) p.setPos(prev);
    }
}
