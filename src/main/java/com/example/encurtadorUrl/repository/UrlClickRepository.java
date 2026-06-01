package com.example.encurtadorUrl.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.encurtadorUrl.entity.UrlClick;

@Repository
public interface UrlClickRepository extends JpaRepository<UrlClick, Long> {

}
