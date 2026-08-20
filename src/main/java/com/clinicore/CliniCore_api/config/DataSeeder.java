package com.clinicore.CliniCore_api.config;

import com.clinicore.CliniCore_api.entities.Especialidad;
import com.clinicore.CliniCore_api.entities.Role;
import com.clinicore.CliniCore_api.repository.EspecialidadRepository;
import com.clinicore.CliniCore_api.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final RoleRepository roleRepository;
    private final EspecialidadRepository especialidadRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // Roles
        crearRoleSiNoExiste("ADMIN");
        crearRoleSiNoExiste("DOCTOR");
        crearRoleSiNoExiste("PACIENTE");
        crearRoleSiNoExiste("PERSONAL");
        /*
        // Especialidades (solo nombre)
        crearEspecialidadSiNoExiste("Medicina General");
        crearEspecialidadSiNoExiste("Pediatría");
        crearEspecialidadSiNoExiste("Cardiología");
        crearEspecialidadSiNoExiste("Dermatología");
        crearEspecialidadSiNoExiste("Traumatología");
        crearEspecialidadSiNoExiste("Ginecología");
        crearEspecialidadSiNoExiste("Oftalmología");
         */
    }

    private void crearRoleSiNoExiste(String nombre) {
        if (roleRepository.findByNombre(nombre).isEmpty()) {
            Role role = new Role();
            role.setNombre(nombre);
            roleRepository.save(role);
        }
    }

    /*
    private void crearEspecialidadSiNoExiste(String nombre) {
        if (especialidadRepository.findByNombre(nombre).isEmpty()) {
            Especialidad esp = new Especialidad();
            esp.setNombre(nombre);
            especialidadRepository.save(esp);
        }
    }
    */
}