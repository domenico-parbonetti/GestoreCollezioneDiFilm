package com.parbonetti.gestorefilm.commands;

public interface Command {
    void execute();
    void undo();
    String getDescription();
}
