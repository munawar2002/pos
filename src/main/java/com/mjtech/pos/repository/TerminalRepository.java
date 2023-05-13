package com.mjtech.pos.repository;

import com.mjtech.pos.entity.Terminal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TerminalRepository extends JpaRepository<Terminal, Integer> {

    Optional<Terminal> findByMacAddressAndIpAddress(String macAddress, String ipAddress);
}
