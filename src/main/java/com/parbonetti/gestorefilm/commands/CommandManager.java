package com.parbonetti.gestorefilm.commands;

import java.util.Stack;

public class CommandManager {
    private final Stack<Command> commandHistory;
    private final int maxHistorySize;

    public CommandManager(int maxHistorySize) {
        this.commandHistory = new Stack<>();
        this.maxHistorySize = maxHistorySize;
    }

    public void executeCommand(Command command) {
        command.execute();
        commandHistory.push(command);

        if (commandHistory.size() > maxHistorySize) {
            commandHistory.remove(0);
            System.out.println("[CommandManager] History limit reached, removed oldest command");
        }
    }

    public Command undo() {
        if (commandHistory.isEmpty()) {
            return null;
        }

        Command command = commandHistory.pop();
        command.undo();
        return command;
    }

    public boolean canUndo() {
        return !commandHistory.isEmpty();
    }

    public int getHistorySize() {
        return commandHistory.size();
    }

    public void clearHistory() {
        commandHistory.clear();
    }
}
