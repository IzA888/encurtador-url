package com.example.encurtadorUrl.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.matches;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.repository.UrlRepository;
import com.example.encurtadorUrl.service.url.UrlService;
import com.example.encurtadorUrl.util.SecureKeyGenerator;

// Testes unitários
@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT) // Permite leniência para evitar erros de stubbing não utilizadosw
@DataJpaTest
class UrlServiceTest {

    @MockitoBean
    private UrlRepository urlRepository;

    @MockitoBean
    private StringRedisTemplate redisTemplate;

    @Mock
    private SetOperations<String, String> setOperations;

    @Mock
    private ValueOperations<String, String> valueOperations;

    @Spy
    private SecureKeyGenerator keyGen;

    @InjectMocks
    private UrlService urlService;

    @BeforeEach
    void setUp() {
        lenient().when(redisTemplate.opsForValue()).thenReturn(valueOperations);
        lenient().when(redisTemplate.opsForSet()).thenReturn(setOperations);

        // 1. Instancia o gerador real
        keyGen = new SecureKeyGenerator();

        // 2. Mocka apenas a parte do Redis se necessário
        redisTemplate = Mockito.mock(StringRedisTemplate.class);
    }

    @Test
    void deveEncurtarUrlComSucesso() throws Exception {
        String urlOriginal = "https://anycript.com/crypto/jasypt";
        Url urlGerada = urlService.encurtarUrl(urlOriginal);
        String regex = "^[a-zA-Z0-9]{6}$";

        lenient().when(urlRepository.save(any(Url.class))).thenReturn(urlGerada);

        assertNotNull(urlGerada);
        verify(urlRepository, times(1)).save(any(Url.class));
        verify(valueOperations, times(1)).set(matches(regex), eq(urlOriginal), any());
    }

    @Test
    void deveBuscarUrlOriginalNoCacheDoRedis() throws Exception {
        String urlOriginalEsperada = "https://anycript.com/crypto/jasypt";
        String hashUrl = "hfsdJ";

        when(redisTemplate.hasKey(hashUrl)).thenReturn(true);
        when(valueOperations.get(eq(hashUrl))).thenReturn(urlOriginalEsperada);
        Url resultado = urlService.obterUrlPorHash(hashUrl);

        assertNotNull(resultado);
        assertEquals(hashUrl, resultado.getHashUrl());
        assertEquals(urlOriginalEsperada, resultado.getOriginalUrl());
        verify(urlRepository, never()).findByHashUrl(anyString()); // Verifica que o repositório não foi chamado, pois a
                                                                   // URL foi encontrada no cache do Redis
    }

    @Test
    void deveLancarExceptionQuandoNaoForEncontradaURl() {
        String hashUrl = "huefkuA";

        when(redisTemplate.hasKey(hashUrl)).thenReturn(false);
        when(urlRepository.findByHashUrl(hashUrl)).thenReturn(Optional.empty());

        Exception exception = assertThrows(Exception.class, () -> {
            urlService.obterUrlPorHash(hashUrl);
        });

        assertEquals("URL não encontrada: " + hashUrl, exception.getMessage());
    }
}