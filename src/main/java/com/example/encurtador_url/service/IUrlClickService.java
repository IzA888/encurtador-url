package com.example.encurtador_url.service;

import com.example.encurtador_url.entity.Url;

import jakarta.servlet.http.HttpServletRequest;

public interface IUrlClickService {

    void registrarClick(Url url, HttpServletRequest request);
}
