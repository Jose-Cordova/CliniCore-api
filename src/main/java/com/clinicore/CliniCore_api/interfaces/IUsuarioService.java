package com.clinicore.CliniCore_api.interfaces;

import com.clinicore.CliniCore_api.dto.PerfilDTO;

public interface IUsuarioService {
    PerfilDTO obtenerPerfil();
    PerfilDTO actualizarPerfil(PerfilDTO dto);
}