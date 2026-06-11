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
     * @return hash aleatório correspondente à URL
     */
    Url encurtarUrl(String urlOriginal);

    /**
     * Método para obter uma URL pelo seu hash.
     * 
     * @param hashUrl o hash da URL a ser obtida
     * @return a URL original correspondente ao hash fornecido
      * @throws Exception se a URL não for encontrada ou ocorrer um erro durante a obtenção
     */
    Url obterUrlPorHash(String hashUrl) throws Exception;

    /**
     * Método para excluir uma URL pelo seu hash.
     * 
     * @param hashUrl o hash da URL a ser excluída
     */
    void excluirUrl(String hashUrl);

    /**
     * Método para obter histórico de URLs encurtadas do dia de hoje.
     * 
     * @return a lista de todas as URLs
     */
    List<Url> obterHistoricoUrlsDiaDeHoje();
}