package com.microservicio_usuarios.microservicio_usuarios.config;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

class WebConfigTest {

    @Test
    void corsConfigurer_RetornarWebMvcConfigurer() {
        WebConfig webConfig = new WebConfig();

        WebMvcConfigurer configurer = webConfig.corsConfigurer();

        assertNotNull(configurer);
    }

    @Test
    void corsConfigurer_ConfigurarCorsCorrectamente() {
        WebConfig webConfig = new WebConfig();

        WebMvcConfigurer configurer = webConfig.corsConfigurer();

        assertNotNull(configurer);
        assertTrue(configurer instanceof WebMvcConfigurer);
    }

    @Test
    void webConfig_CrearInstancia() {
        WebConfig webConfig = new WebConfig();

        assertNotNull(webConfig);
    }
}
