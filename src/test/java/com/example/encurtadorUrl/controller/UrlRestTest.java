package com.example.encurtadorUrl.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.service.url.UrlService;



// Teste de Integração
@SpringBootTest
@Testcontainers
@AutoConfigureMockMvc
class UrlRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private  UrlService urlService; //foco em comportamento HTTP

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        // Injeta dinamicamente as credenciais do contêiner temporário no Spring
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Test
    void deveRetornarStatus201AoCriarUrlEncurtada() throws Exception {
        String urlOriginal = "https://anycript.com/crypto/jasypt";
        Url urlSalva = urlService.encurtarUrl(urlOriginal);

        Mockito.when(urlService.encurtarUrl(urlSalva.getOriginalUrl())).thenReturn(urlSalva);        
        
        
        
        mockMvc.perform(post("/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"originalUrl\": \"" + urlOriginal + "\"}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.hashUrl").value(urlSalva.getHashUrl())); 
    }

    @Test
    void deveRedirectionarAoBuscarChaveExistente() throws Exception {
        String urlOriginal = "https://anycript.com/crypto/jasypt";
        Url urlSalva = urlService.encurtarUrl(urlOriginal);
        Mockito.when(urlService.obterUrlPorHash(urlSalva.getHashUrl())).thenReturn(urlSalva);

        mockMvc.perform(get("/urls/" + urlSalva.getHashUrl()))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlOriginal));
    }
    

}