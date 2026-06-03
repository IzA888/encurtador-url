package com.example.encurtadorUrl.service.url;

import java.time.Duration;
import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.repository.UrlRepository;
import com.example.encurtadorUrl.util.SecureKeyGenerator;

import jakarta.transaction.Transactional;

@Service
public class UrlService implements IUrlService {

    @Autowired
    private UrlRepository urlRepository;

    @Autowired
    private SecureKeyGenerator keyGen;

    @Autowired
    private StringRedisTemplate redisTemplate;

    private final Long CACHE_TTL_DAYS = 7L; // Tempo de expiração do cache no Redis (7 dias)
    private static final Integer MAX_RETRIES = 5;
    private static final String HOJE = LocalDate.now().toString();

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
                redisTemplate.opsForValue().set(key, originalUrl, Duration.ofDays(CACHE_TTL_DAYS));
                redisTemplate.opsForSet().add(HOJE, key); // Adiciona a chave ao conjunto do dia de hoje
                redisTemplate.expire(HOJE, Duration.ofDays(1)); // Define o tempo de expiração do conjunto do dia de hoje
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

    @Override
    public List<Url> obterHistoricoUrlsDiaDeHoje() {
        return redisTemplate.opsForSet().members(HOJE).stream() // urls do dia de hoje
                .map(key -> {
                    String originalUrl = redisTemplate.opsForValue().get(key);
                    Url url = new Url();
                    url.setHashUrl(key);
                    url.setOriginalUrl(originalUrl);
                    return url;
                })
                .filter(url -> url.getOriginalUrl() != null)
                .toList();
    }

}
