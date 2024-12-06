package com.northcoders.recordshopbackend.service;

import com.northcoders.recordshopbackend.model.Artist;

import java.util.List;

public interface ArtistService {
    List<Artist> getAllArtist();
    Artist addNewArtist(String artistName);
}