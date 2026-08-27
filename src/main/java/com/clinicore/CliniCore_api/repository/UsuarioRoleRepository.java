package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.UsuarioRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UsuarioRoleRepository extends JpaRepository<UsuarioRole, Integer> {
    //Un usuario -> un role
    Optional<UsuarioRole> findByUsuario_Id(Integer usuarioId);
    boolean existsByUsuario_Id(Integer usuarioId);
}