package com.example.encurtadorUrl.rest;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.service.IUrlClickService;
import com.example.encurtadorUrl.service.IUrlService;

import jakarta.servlet.http.HttpServletRequest;

@RestController
@RequestMapping("/urls")
public class UrlRest {

    @Autowired
    private IUrlService urlService;

    @Autowired
    private IUrlClickService urlClickService;

    // Endpoint para encurtar URL
    @PostMapping
    public ResponseEntity<String> encurtarUrl(@RequestBody String url) {
        // Encurta a URL e salva no banco de dados
        String urlEncurtada = urlService.encurtarUrl(url).getHashUrl();
        return ResponseEntity.status(HttpStatus.CREATED).body(urlEncurtada);
    }

    // Endpoint para acessar URL encurtada
    @GetMapping("/{codigo}")
    public ResponseEntity<String> acessarUrl(@PathVariable String codigo, HttpServletRequest request) {
        // Redireciona para a URL original
        Url url = urlService.obterUrlPorHash(codigo);
        urlClickService.registrarClick(url, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                                .location(URI.create(url.getOriginalUrl()))
                                .build();

    }

    // Endpoint para deletar URL encurtada
    @DeleteMapping("/{codigo}")
    public ResponseEntity<HttpStatus> deletarUrl(@PathVariable String codigo) {
        // Deleta a URL encurtada do banco de dados
        urlService.excluirUrl(codigo);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}