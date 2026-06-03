package com.example.encurtadorUrl.service.urlClick;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.sql.Timestamp;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.encurtadorUrl.entity.Url;
import com.example.encurtadorUrl.entity.UrlClick;
import com.example.encurtadorUrl.repository.UrlClickRepository;

import org.json.JSONObject;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class UrlClickService implements IUrlClickService {

    @Autowired
    private UrlClickRepository urlClickRepository;

    @Override
    public void registrarClick(Url url, HttpServletRequest request) {
        try {
            UrlClick click = new UrlClick();
            click.setUrl(url);
            click.setClickedAt(new Timestamp(System.currentTimeMillis()));
            click.setReferrer(request.getHeader("Referer"));
            click.setCountry(obterPaisPorIP(request.getRemoteAddr()));
            click.setUserAgent(request.getHeader("User-Agent"));
            urlClickRepository.save(click);
        } catch (Exception e) {
            log.error("Erro ao registrar click: " + e.getMessage());
        }
    }

    private String obterPaisPorIP(String remoteAddr) {
        try {
            // Utilizando a API do ip-api para obter informações do IP
            String apiUrl = "http://ip-api.com/json/" + remoteAddr;
            URL url = new URI(apiUrl).toURL();
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();

            if (responseCode == 200) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuffer response = new StringBuffer();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                // Parseando o JSON de resposta
                JSONObject jsonObject = new JSONObject(response.toString());
                return jsonObject.getString("country");
            } else {
                log.error("Erro ao obter país pelo IP: " + responseCode);
                return "Desconhecido";
            }
        } catch (Exception e) {
            log.error("Erro ao obter país pelo IP: " + e.getMessage());
            return "Desconhecido";
        }
    }
}