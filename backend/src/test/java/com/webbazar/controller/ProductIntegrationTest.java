package com.webbazar.controller;

import com.webbazar.config.TestSecurityConfig;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
@Import(TestSecurityConfig.class) //  zorgt dat authenticatie wordt uitgezet tijdens tests
public class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // Test 1: Checkt of /api/products werkt en 200 geeft
    @Test
    public void testGetAllProducts_returnsStatus200() throws Exception {
        mockMvc.perform(get("/api/products"))
                .andExpect(status().isOk());
    }

    // Test 2: Checkt of een foutieve URL een 500 geeft i.p.v. 404 (zoals Spring het standaard doet)
    @Test
    public void testInvalidUrl_returnsStatus500InsteadOf404() throws Exception {
        mockMvc.perform(get("/api/producten-bestaan-niet"))
                .andExpect(status().isInternalServerError());
    }
}
