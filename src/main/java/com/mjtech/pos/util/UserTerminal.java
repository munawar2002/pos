package com.mjtech.pos.util;

import lombok.extern.slf4j.Slf4j;

import java.net.InetAddress;
import java.net.UnknownHostException;

@Slf4j
public class UserTerminal {

    private static String terminal;

    private UserTerminal() throws UnknownHostException {
        InetAddress localHost = InetAddress.getLocalHost();
        String hostName = localHost.getHostName();
        String ip = localHost.getHostAddress();
        terminal = hostName + " - " + ip;
    }

    public static String getTerminal() {
        return terminal;
    }
}
