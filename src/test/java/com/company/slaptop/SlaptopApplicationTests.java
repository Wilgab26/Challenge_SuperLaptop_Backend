package com.company.slaptop;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SlaptopApplicationTests extends BaseIntegrationTest {

    @Test
    void laptopIncidentAndFailureReportFlow() throws Exception {
        long siteId = createCatalog("/api/sedes", """
                {"nombre": "Sede de prueba"}
                """);
        long userId = createCatalog("/api/usuarios", """
                {"nombre": "Usuario de prueba", "correo": "test@example.com"}
                """);
        long componentId = createCatalog("/api/componentes", """
                {"nombre": "Disco SSD", "tipo": "Almacenamiento"}
                """);
        long applicationId = createCatalog("/api/aplicaciones", """
                {"nombre": "Office", "version": "2024"}
                """);

        String laptopResponse = mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ip": "192.168.10.22",
                                  "nombre": "LAP-TEST-01",
                                  "marca": "Lenovo",
                                  "modelo": "T14",
                                  "estado": "ACTIVA",
                                  "sedeId": %d,
                                  "usuarioAsignadoId": %d,
                                  "componenteIds": [%d],
                                  "aplicacionIds": [%d]
                                }
                                """.formatted(siteId, userId, componentId, applicationId)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.sede.nombre").value("Sede de prueba"))
                .andExpect(jsonPath("$.componentes[0].nombre").value("Disco SSD"))
                .andReturn().getResponse().getContentAsString();
        long laptopId = objectMapper.readTree(laptopResponse).get("id").asLong();

        mockMvc.perform(post("/api/incidents")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "laptopId": %d,
                                  "descripcion": "Falla detectada",
                                  "componenteDanadoIds": [%d]
                                }
                                """.formatted(laptopId, componentId)))
                .andExpect(status().isCreated())
                                .andExpect(jsonPath("$.componentesDanados[0].id").value(componentId));

        mockMvc.perform(get("/api/reports/component-failures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Disco SSD"))
                .andExpect(jsonPath("$[0].cantidadFallas").value(1));

        mockMvc.perform(get("/api/laptops/{id}", laptopId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.usuarioAsignado.correo").value("test@example.com"));

        mockMvc.perform(get("/api/usuarios/{id}", userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nombre").value("Usuario de prueba"))
                .andExpect(jsonPath("$.correo").value("test@example.com"));
    }

    @Test
    void catalogCanBeCreatedAndListed() throws Exception {
        createCatalog("/api/sedes", """
                {"nombre": "Sede de prueba"}
                """);

        mockMvc.perform(get("/api/sedes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nombre").value("Sede de prueba"));
    }

    @Test
    void duplicateUserEmailReturnsConflict() throws Exception {
        String user = """
                {"nombre": "Ana", "correo": "ana@example.com"}
                """;
        createCatalog("/api/usuarios", user);

        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(user))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.status").value(409));
    }

    @Test
    void invalidCatalogRequestReturnsBadRequest() throws Exception {
        mockMvc.perform(post("/api/usuarios")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {"nombre": "", "correo": "not-an-email"}
                                """))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validationErrors.nombre").exists())
                .andExpect(jsonPath("$.validationErrors.correo").exists());
    }

    @Test
    void laptopWithUnknownSiteReturnsNotFound() throws Exception {
        mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                                {
                                  "ip": "192.168.10.40",
                                  "nombre": "LAP-TEST-02",
                                  "marca": "Lenovo",
                                  "modelo": "T14",
                                  "estado": "ACTIVA",
                                  "sedeId": 9999
                                }
                                """))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Sede no encontrado: 9999"));
    }

    @Test
    void duplicateLaptopIpReturnsConflict() throws Exception {
        long siteId = createCatalog("/api/sedes", """
                {"nombre": "Sede duplicado"}
                """);
        String laptop = """
                {
                  "ip": "192.168.10.41",
                  "nombre": "LAP-TEST-03",
                  "marca": "Lenovo",
                  "modelo": "T14",
                  "estado": "ACTIVA",
                  "sedeId": %d
                }
                """.formatted(siteId);

        mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(laptop))
                .andExpect(status().isCreated());

        mockMvc.perform(post("/api/laptops")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(laptop))
                .andExpect(status().isConflict())
                .andExpect(jsonPath("$.message").value(
                        "Ya existe una laptop con la IP 192.168.10.41"));
    }
}
