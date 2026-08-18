package com.clinicore.CliniCore_api.repository;

import com.clinicore.CliniCore_api.entities.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UsuarioRepositiry extends JpaRepository<Usuario, Integer> {
}
