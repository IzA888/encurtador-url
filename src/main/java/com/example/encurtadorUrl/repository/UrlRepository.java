package com.example.encurtadorUrl.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.encurtadorUrl.entity.Url;

@Repository
public interface UrlRepository extends JpaRepository<Url, Long> {

    Boolean existsByHashUrl(String key);

    Optional<Url> findByHashUrl(String hashUrl);

    void deleteByHashUrl(String hashUrl);

}
