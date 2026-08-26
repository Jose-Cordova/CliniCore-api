package com.clinicore.CliniCore_api.config;

import com.clinicore.CliniCore_api.entities.*;
import com.clinicore.CliniCore_api.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final EspecialidadRepository especialidadRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioRoleRepository usuarioRoleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Roles
        crearRoleSiNoExiste("ADMIN");
        crearRoleSiNoExiste("DOCTOR");
        crearRoleSiNoExiste("PACIENTE");
        crearRoleSiNoExiste("PERSONAL");

        // Especialidades
        crearEspecialidadSiNoExiste("Medicina General");
        crearEspecialidadSiNoExiste("Pediatría");
        crearEspecialidadSiNoExiste("Cardiología");
        crearEspecialidadSiNoExiste("Dermatología");
        crearEspecialidadSiNoExiste("Traumatología");
        crearEspecialidadSiNoExiste("Ginecología");
        crearEspecialidadSiNoExiste("Oftalmología");

        // Admin por defecto
        crearAdminSiNoExiste();
    }

    private void crearRoleSiNoExiste(String nombre) {
        if (roleRepository.findByNombre(nombre).isEmpty()) {
            Role role = new Role();
            role.setNombre(nombre);
            roleRepository.save(role);
        }
    }

    private void crearEspecialidadSiNoExiste(String nombre) {
        if (especialidadRepository.findByNombre(nombre).isEmpty()) {
            Especialidad esp = new Especialidad();
            esp.setNombre(nombre);
            especialidadRepository.save(esp);
        }
    }

    private void crearAdminSiNoExiste() {
        String emailAdmin = "admin@gmail.com";
        String passwordAdmin = "admin123";

        if (usuarioRepository.findByEmail(emailAdmin).isEmpty()) {
            // 1. Crear usuario
            Usuario admin = new Usuario();
            admin.setEmail(emailAdmin);
            admin.setContrasenia(passwordEncoder.encode(passwordAdmin));
            admin.setEstado(true);
            admin = usuarioRepository.save(admin);

            // 2. Obtener rol ADMIN
            Role roleAdmin = roleRepository.findByNombre("ADMIN")
                    .orElseThrow(() -> new RuntimeException("Rol ADMIN no existe"));

            // 3. Asignar rol en usuarios_roles
            UsuarioRole usuarioRole = new UsuarioRole();
            usuarioRole.setUsuario(admin);
            usuarioRole.setRole(roleAdmin);
            usuarioRoleRepository.save(usuarioRole);
        }
    }
}