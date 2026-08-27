package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Integer> {
    Optional<Role> findByNombre(String nombre);
}
