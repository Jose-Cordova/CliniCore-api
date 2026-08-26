package com.clinicore.CliniCore_api;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

public class GenerarHashTest {

    @Test
    void generarHash(){
        String passwordPlano = "admin123";
        String hash = new BCryptPasswordEncoder().encode(passwordPlano);
        System.out.println("Hash generadi: " + hash);

        boolean coincide = new BCryptPasswordEncoder().matches(passwordPlano, hash);
        System.out.println("Coinide?" + coincide);
    }

}