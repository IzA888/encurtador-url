package com.example.encurtadorUrl.service;

import com.example.encurtadorUrl.entity.Url;

import jakarta.servlet.http.HttpServletRequest;

public interface IUrlClickService {

    void registrarClick(Url url, HttpServletRequest request);
}
