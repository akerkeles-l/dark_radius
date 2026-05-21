package com.darkradius.patterns.command;

import com.darkradius.entities.Player;
import java.util.ArrayDeque;
import java.util.Deque;

// ── Invoker ───────────────────────────────────────────────────
public class CommandInvoker {
    private final Deque<ICommand> history = new ArrayDeque<>();

    public void run(ICommand cmd) {
        cmd.execute();
        history.push(cmd);
        if (history.size() > 30) history.pollLast();
    }

    public void undo() {
        if (!history.isEmpty()) history.pop().undo();
    }

    public void clear() { history.clear(); }

    // Convenience factory methods
    public static ICommand up(Player p)    { return new MoveCommand(p,  0,  1); }
    public static ICommand down(Player p)  { return new MoveCommand(p,  0, -1); }
    public static ICommand left(Player p)  { return new MoveCommand(p, -1,  0); }
    public static ICommand right(Player p) { return new MoveCommand(p,  1,  0); }
}
