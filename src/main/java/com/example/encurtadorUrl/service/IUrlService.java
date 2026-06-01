package com.example.encurtadorUrl.service;

import com.example.encurtadorUrl.entity.Url;

public interface IUrlService {
    // Metodo para encurtar uma URL
    Url encurtarUrl(String urlOriginal);
    // Metodo para obter uma URL pelo seu hash
    Url obterUrlPorHash(String hashUrl);
    // Metodo para excluir uma URL pelo seu hash
    void excluirUrl(String hashUrl);
}