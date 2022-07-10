package de.cofinpro.splitter.controller.command;

public enum CommandType {
    HELP("help"),
    EXIT("exit"),
    BORROW("borrow"),
    REPAY("repay"),
    BALANCE("balance"),
    GROUP("group"),
    PURCHASE("purchase"),
    CASH_BACK("cashBack"),
    SECRET_SANTA("secretSanta"),
    WRITE_OFF("writeOff");

    private final String commandName;

    CommandType(String commandName) {
        this.commandName = commandName;
    }

    public String getCommandName() {
        return commandName;
    }
}
