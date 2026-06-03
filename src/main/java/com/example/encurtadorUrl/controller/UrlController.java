package com.example.encurtadorUrl.controller;

import java.net.URI;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.service.IUrlClickService;
import com.example.encurtadorUrl.service.IUrlService;

import jakarta.servlet.http.HttpServletRequest;

@Controller
@RequestMapping("/urls")
public class UrlController {

    @Autowired
    private IUrlService urlService;

    @Autowired
    private IUrlClickService urlClickService;

    @GetMapping
    public String telaInicial(Model model) {
        return "index";
    }

    // Endpoint para encurtar URL
    @PostMapping
    @ResponseBody // <--- ISSO diz ao Spring para retornar texto puro e não uma tela HTML
    public ResponseEntity<String> encurtarUrl(@RequestBody String url) {
        // Encurta a URL e pega o hash (ex: "xyz12")
        String hashUrl = urlService.encurtarUrl(url).getHashUrl();
        
        // 4. Retorna o texto esperado pela Regex
        return ResponseEntity.ok("URL encurtada: " + hashUrl);  
    }

    // Endpoint para acessar URL encurtada
    @GetMapping("/{codigo}")
    public ResponseEntity<Void> acessarUrl(@PathVariable String codigo, HttpServletRequest request) {
        // Redireciona para a URL original
        Url url = urlService.obterUrlPorHash(codigo);
        // 2. Remove qualquer caractere invisível de controle (\p{Cntrl}) e espaços (\s)
        String urlLimpa = url.getOriginalUrl().replaceAll("\\p{Cntrl}", "").replaceAll("\\s", "");
        urlClickService.registrarClick(url, request);
        return ResponseEntity.status(HttpStatus.FOUND)
                .location(URI.create(urlLimpa))
                .build();
    }

    // Endpoint para deletar URL encurtada
    @PostMapping("/{codigo}")
    public String deletarUrl(@PathVariable String codigo, Model model) {
        // Deleta a URL encurtada do banco de dados
        urlService.excluirUrl(codigo);
        model.addAttribute("mensagem", "URL excluída com sucesso");
        return "index";
    }
}