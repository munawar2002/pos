package com.mjtech.pos.repository;

import com.mjtech.pos.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {

    List<Role> findByIdIn(List<Integer> ids);
}
