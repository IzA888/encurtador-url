package com.example.encurtador_url.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.encurtador_url.entity.UrlClick;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

}
