package com.clinicore.CliniCore_api.controllers;

import com.clinicore.CliniCore_api.dto.PerfilDTO;
import com.clinicore.CliniCore_api.interfaces.IUsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final IUsuarioService usuarioService;

    @GetMapping("/perfil")
    public ResponseEntity<PerfilDTO> obtenerPerfil() {
        return ResponseEntity.ok(usuarioService.obtenerPerfil());
    }

    @PutMapping("/perfil")
    public ResponseEntity<PerfilDTO> actualizarPerfil(@RequestBody PerfilDTO dto) {
        return ResponseEntity.ok(usuarioService.actualizarPerfil(dto));
    }
}