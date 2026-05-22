package com.webbazar.integration;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

// Integratietest voor login en rollen: echte JWT login + rol-scheiding voor Product-CRUD.
@SpringBootTest
@AutoConfigureMockMvc
class AuthAndRolesIT {

    @Autowired MockMvc mockMvc;
    @Autowired ObjectMapper mapper;

    @Test
    void userVsAdmin_accessControl_products() throws Exception {
        // login als USER
        var loginUser = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"user@webbazar.test","password":"Password123!"}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        String userToken = mapper.readTree(loginUser.getResponse().getContentAsString()).get("accessToken").asText();
        assertThat(userToken).isNotBlank();

        // public GET mag
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());

        // USER mag niet POSTen -> 403
        mockMvc.perform(multipart("/api/products")
                        .file("file", new byte[0])
                        .param("product", """
                            {"title":"X","price":10.00,"rentalPrice":1.00}
                        """)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization","Bearer " + userToken))
                .andExpect(status().isForbidden());

        // login als ADMIN
        var loginAdmin = mockMvc.perform(post("/api/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("""
                            {"email":"admin@webbazar.test","password":"Password123!"}
                        """))
                .andExpect(status().isOk())
                .andReturn();
        String adminToken = mapper.readTree(loginAdmin.getResponse().getContentAsString()).get("accessToken").asText();

        // ADMIN mag product aanmaken
        var createRes = mockMvc.perform(multipart("/api/products")
                        .param("product", """
                            {"title":"Nieuw Boek","author":"Auteur","description":"Desc","price":12.50,"rentalPrice":1.25}
                        """)
                        .contentType(MediaType.MULTIPART_FORM_DATA)
                        .header("Authorization","Bearer " + adminToken))
                .andExpect(status().isCreated())
                .andReturn();

        JsonNode created = mapper.readTree(createRes.getResponse().getContentAsString());
        assertThat(created.get("id").asLong()).isPositive();
    }
}
