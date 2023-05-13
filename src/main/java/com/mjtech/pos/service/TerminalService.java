package com.mjtech.pos.service;

import com.mjtech.pos.entity.Terminal;
import com.mjtech.pos.repository.TerminalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;

@Service
public class TerminalService {

    @Autowired
    private TerminalRepository terminalRepository;

    public Terminal findCurrentTerminal() {
        try {
            String macAddress = getMacAddress();
            String ipAddress = InetAddress.getLocalHost().getHostAddress();
            return terminalRepository.findByMacAddressAndIpAddress(macAddress, ipAddress).orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("Failed to find Terminal, Might be issue with current macAddress or Ip address of user", e);
        }
    }

    public static String getMacAddress() throws SocketException, UnknownHostException {
        InetAddress ip = InetAddress.getLocalHost();
        NetworkInterface network = NetworkInterface.getByInetAddress(ip);
        byte[] mac = network.getHardwareAddress();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            sb.append(String.format("%02X%s", mac[i], (i < mac.length - 1) ? "-" : ""));
        }
        return sb.toString();
    }
}
