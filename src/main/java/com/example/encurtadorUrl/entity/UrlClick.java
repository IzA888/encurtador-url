package com.example.encurtadorUrl.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;

@Entity
@Table(name = "url_clicks")
public class UrlClick {
    // id: Chave primária
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id")
    private Long id;

    // url_id: Chave estrangeira apontando para a tabela urls
    @ManyToOne
    @JoinColumn(name = "url_id", referencedColumnName = "id")
    private Url url;

    // clicked_at: Data e hora exata do clique
    @Column(name = "clicked_at")
    private Timestamp clickedAt;

    // referrer: De onde o usuário veio (ex: Twitter, LinkedIn)
    @Column(name = "referrer")
    private String referrer;

    // country: País de origem (obtido via IP no backend)
    @Column(name = "country")
    private String country;

    // user_agent: String do navegador/dispositivo para extrair estatísticas
    @Column(name = "user_agent")
    private String userAgent;

    // Getters e Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Url getUrl() {
        return url;
    }

    public void setUrl(Url url) {
        this.url = url;
    }

    public Timestamp getClickedAt() {
        return clickedAt;
    }

    public void setClickedAt(Timestamp clickedAt) {
        this.clickedAt = clickedAt;
    }

    public String getReferrer() {
        return referrer;
    }

    public void setReferrer(String referrer) {
        this.referrer = referrer;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getUserAgent() {
        return userAgent;
    }

    public void setUserAgent(String userAgent) {
        this.userAgent = userAgent;
    }

    public UrlClick() {
    }

}
