package com.mjtech.pos.util;

import com.mjtech.pos.entity.Terminal;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ActiveTerminal {
    private static Terminal terminal;

    private ActiveTerminal() {

    }

    public static void setTerminal(Terminal value) {
        terminal = value;
        log.info("Current terminal is "+ terminal.getComputerName());
    }

    public static Terminal getTerminal() {
        return terminal;
    }

    public static Integer getTerminalId() {
        return terminal == null ? null : terminal.getId();
    }
}
