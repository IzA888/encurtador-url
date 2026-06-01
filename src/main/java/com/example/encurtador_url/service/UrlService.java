package com.example.encurtador_url.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.encurtador_url.entity.Url;
import com.example.encurtador_url.repository.UrlRepository;
import com.example.encurtador_url.util.SecureKeyGenerator;

import jakarta.transaction.Transactional;

@Service
public class UrlService implements IUrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private SecureKeyGenerator keyGen;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Long CACHE_TTL_DAYS = 7L;
    private static final Integer MAX_RETRIES = 5;

    @Override
    public Url encurtarUrl(String originalUrl) {
        Integer tentativa = 0;
        while (tentativa < MAX_RETRIES) {
            try {
                return gerarUrlEncurtada(originalUrl);
            } catch (RuntimeException e) {
                tentativa++;
                if (tentativa >= MAX_RETRIES) {
                    throw new RuntimeException("Não foi possível gerar uma URL encurtada após " + MAX_RETRIES + " tentativas", e);
                }
            }
        }
        throw new RuntimeException("Erro inesperado ao encurtar a URL");
    }

    private Url gerarUrlEncurtada(String originalUrl) {
        String key = keyGen.genRandomKey();
        if(redisTemplate.hasKey(key) == Boolean.FALSE){ // Verifica se a chave já existe no Redis
            if(urlRepository.existsByHashUrl(key) == Boolean.FALSE){ // Verifica se a chave já existe no banco de dados
                redisTemplate.opsForValue().set(key, originalUrl, CACHE_TTL_DAYS);
                Url url = new Url();
                url.setHashUrl(key);
                url.setOriginalUrl(originalUrl);
                urlRepository.save(url);
                return url;
            } else {
                throw new RuntimeException("Hash já existe no banco de dados" + key);
            }
        } else {
            throw new RuntimeException("Hash já existe no Redis" + key);
        }
        
    }

    @Override
    public Url obterUrlPorHash(String hashUrl) {
        if (redisTemplate.hasKey(hashUrl)) {
            String originalUrl = redisTemplate.opsForValue().get(hashUrl);
            Url url = new Url();
            url.setHashUrl(hashUrl);
            url.setOriginalUrl(originalUrl);
            return url;
        } else {
            return urlRepository.findByHashUrl(hashUrl).orElseThrow(() -> new RuntimeException("URL não encontrada: " + hashUrl));
        }
    }

    @Transactional
    @Override
    public void excluirUrl(String hashUrl) {
        redisTemplate.delete(hashUrl);
        urlRepository.deleteByHashUrl(hashUrl);
    }

}
