package com.darkradius.patterns.command;

// ── Interface ─────────────────────────────────────────────────
public interface ICommand {
    void execute();

    void undo();
}
