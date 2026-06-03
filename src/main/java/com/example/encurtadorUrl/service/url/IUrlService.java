package com.example.encurtadorUrl.service.url;

import java.util.List;

import com.example.encurtadorUrl.entity.Url;

/**
 * Interface que define os métodos para o serviço de encurtamento de URLs.
 */
public interface IUrlService {

    /**
     * Método para encurtar uma URL.
     * 
     * @param urlOriginal a URL original a ser encurtada
     * @return a URL encurtada
     */
    Url encurtarUrl(String urlOriginal);

    /**
     * Método para obter uma URL pelo seu hash.
     * 
     * @param hashUrl o hash da URL a ser obtida
     * @return a URL obtida pelo hash
     */
    Url obterUrlPorHash(String hashUrl);

    /**
     * Método para excluir uma URL pelo seu hash.
     * 
     * @param hashUrl o hash da URL a ser excluída
     */
    void excluirUrl(String hashUrl);

    /**
     * Método para obter histórico de URLs encurtadas do dia de hoje.
     * 
     * @return a lista de URLs encurtadas
     */
    List<Url> obterHistoricoUrlsDiaDeHoje();
}