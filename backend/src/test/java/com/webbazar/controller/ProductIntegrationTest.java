package com.webbazar.controller;

import com.webbazar.dto.ProductDTO;
import com.webbazar.security.JwtAuthFilter;
import com.webbazar.service.ProductService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = ProductController.class)
@AutoConfigureMockMvc(addFilters = false) // security filters uit tijdens deze slice-test
class ProductIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductService productService; // service wordt gemockt

    //  JWT-filter mocken voor test
    @MockBean
    private JwtAuthFilter jwtAuthFilter;

    @Test
    void testGetAllProducts_returnsStatus200() throws Exception {
        ProductDTO p1 = new ProductDTO(
                1L,
                "Book A",
                "Author A",
                "Desc A",
                new BigDecimal("9.99"),
                new BigDecimal("1.99"),
                null
        );
        ProductDTO p2 = new ProductDTO(
                2L,
                "Book B",
                "Author B",
                "Desc B",
                new BigDecimal("19.99"),
                new BigDecimal("2.99"),
                null
        );

        when(productService.getAllProducts()).thenReturn(List.of(p1, p2));

        mockMvc.perform(get("/api/products").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].title", is("Book A")))
                .andExpect(jsonPath("$[1].title", is("Book B")));
    }

    @Test
    void testInvalidUrl_returnsStatus404() throws Exception {
        mockMvc.perform(get("/api/producten-bestaan-niet").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
