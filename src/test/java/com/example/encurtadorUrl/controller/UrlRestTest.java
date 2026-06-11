package com.example.encurtadorUrl.controller;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.service.url.UrlService;



// Teste de Integração
@SpringBootTest
@AutoConfigureMockMvc
class UrlRestTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private  UrlService urlService; //foco em comportamento HTTP

    @Test
    void deveRetornarStatus201AoCriarUrlEncurtada() throws Exception {
        String urlOriginal = "https://anycript.com/crypto/jasypt";
        String hashUrl = "hfsdJ";
        Url urlSalva = new Url();
        urlSalva.setOriginalUrl(urlOriginal);
        urlSalva.setHashUrl(hashUrl);
        
        Mockito.when(urlService.encurtarUrl(anyString())).thenReturn(urlSalva);       
        
        mockMvc.perform(post("/urls")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content("{\"originalUrl\": \"" + urlOriginal + "\"}"))
                .andExpect(status().isOk())            
                .andExpect(content().string("URL encurtada: " + hashUrl));
    }

    @Test
    void deveRedirectionarAoBuscarChaveExistente() throws Exception {
        String urlOriginal = "https://anycript.com/crypto/jasypt";
        String hashUrl = "hfsdJ";
        Url urlSalva = new Url();
        urlSalva.setOriginalUrl(urlOriginal);
        urlSalva.setHashUrl(hashUrl);

        Mockito.when(urlService.obterUrlPorHash(hashUrl)).thenReturn(urlSalva);

        mockMvc.perform(get("/urls/" + hashUrl))
                .andExpect(status().is3xxRedirection())
                .andExpect(redirectedUrl(urlSalva.getOriginalUrl()));
    }
    

}