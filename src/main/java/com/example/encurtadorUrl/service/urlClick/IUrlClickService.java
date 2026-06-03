package com.example.encurtadorUrl.service.urlClick;

import com.example.encurtadorUrl.entity.Url;

import jakarta.servlet.http.HttpServletRequest;

public interface IUrlClickService {

    /**
     * Registers a click on the given URL.
     * 
     * @param url    the URL that was clicked
     * @param request the HTTP request that triggered the click
     */
    void registrarClick(Url url, HttpServletRequest request);
}
