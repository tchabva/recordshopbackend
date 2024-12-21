package com.northcoders.recordshopbackend.repository;

import com.northcoders.recordshopbackend.model.Genre;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GenreRepository extends JpaRepository<Genre, Long> {
    Genre findByGenre(String genre);
}
