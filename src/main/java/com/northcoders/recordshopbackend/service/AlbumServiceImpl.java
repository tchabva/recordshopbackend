package com.northcoders.recordshopbackend.service;

import com.northcoders.recordshopbackend.dto.AlbumDTO;
import com.northcoders.recordshopbackend.dto.StockDTO;
import com.northcoders.recordshopbackend.model.Album;
import com.northcoders.recordshopbackend.model.Stock;
import com.northcoders.recordshopbackend.repository.AlbumRepository;
import com.northcoders.recordshopbackend.exception.ItemNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Service
public class AlbumServiceImpl implements AlbumService{

    @Autowired
    private AlbumRepository albumRepository;

    @Autowired
    private ArtistService artistService;

    @Autowired
    private StockService stockService;

    @Autowired
    private AlbumCache albumCache;

    @Override
    public List<Album> getAllAlbums() {
        return new ArrayList<>(albumRepository.findAll());
    }

    @Override
    public List<AlbumDTO> createListOfAlbumDTOs(List<Album> albums) {
        return albums
                .stream()
                .map(this::createAlbumDTO)
                .toList();
    }

    @Override
    public List<AlbumDTO> getAllInStockAlbumDTOs() {
        return createListOfAlbumDTOs(getAllAlbums())
                .stream()
                .filter(albumDTO -> albumDTO.getStock() > 0)
                .toList();
    }

    @Override
    public Album getAlbumById(Long albumId) {

        if (albumCache.containsKey(albumId) && albumCache.isValid()){
            return albumCache.get(albumId);
        }
        if (albumRepository.findById(albumId).isPresent()){
            Album album = albumRepository.findById(albumId).get();
            albumCache.put(albumId, album);
            albumCache.setValid(true);
            return album;
        } else{
            throw new ItemNotFoundException(String.format("Album with the id '%s' cannot be found", albumId)
            );
        }
    }

    @Override
    public AlbumDTO updateAlbumById(Long albumId, AlbumDTO updatedAlbumDTO) {
        if (albumRepository.findById(albumId).isPresent()){
            albumCache.setValid(false);
            Album selectedAlbum = albumRepository.findById(albumId).get();

            if (updatedAlbumDTO.getTitle() != null){
                selectedAlbum.setTitle(updatedAlbumDTO.getTitle());
            }

            if (updatedAlbumDTO.getArtist() != null){
                selectedAlbum.setArtist(artistService.getOrCreateAlbumArtist(updatedAlbumDTO.getArtist()));
            }

            if (updatedAlbumDTO.getGenre() != null){
                selectedAlbum.setGenre(updatedAlbumDTO.getGenre());
            }

            if (updatedAlbumDTO.getReleaseDate() != null){
                selectedAlbum.setReleaseDate(updatedAlbumDTO.getReleaseDate());
            }

            if (updatedAlbumDTO.getStock() != null){
                selectedAlbum.getStock().setQuantityInStock(updatedAlbumDTO.getStock());
            }

            if(updatedAlbumDTO.getPrice() != null){
                selectedAlbum.setPrice(updatedAlbumDTO.getPrice());
            }

            selectedAlbum.setDateModified(Instant.now());
            return createAlbumDTO(albumRepository.save(selectedAlbum));
        }else{
            throw new ItemNotFoundException(String.format("Album with the id '%s' cannot be found", albumId)
            );
        }
    }

    @Override
    public AlbumDTO returnAlbumDTOById(Long albumId) {
        return createAlbumDTO(getAlbumById(albumId));
    }

    @Override
    public AlbumDTO postNewAlbum(AlbumDTO albumDTO) {
        return createAlbumDTO(addNewAlbum(albumDTO));
    }

    // Album to DTO mapper
    @Override
    public AlbumDTO createAlbumDTO(Album album) {

        return AlbumDTO.builder()
                .id(album.getId())
                .title(album.getTitle())
                .artist(album.getArtist().getArtistName())
                .genre(album.getGenre())
                .releaseDate(album.getReleaseDate())
                .stock(album.getStock().getQuantityInStock())
                .price(album.getPrice())
                .dateCreated(album.getDateCreated().toString())
                .dateModified(album.getDateModified().toString())
                .build();
    }

    @Override
    public Album addNewAlbum(AlbumDTO albumDTO) {
        return albumRepository.save(Album.builder()
                .title(albumDTO.getTitle())
                .artist(artistService.getOrCreateAlbumArtist(albumDTO.getArtist()))
                .genre(albumDTO.getGenre())
                .releaseDate(albumDTO.getReleaseDate())
                .stock(stockService.addNewStock(albumDTO.getStock()))
                .price(albumDTO.getPrice())
                .dateCreated(Instant.now())
                .dateModified((Instant.now()))
                .build());
    }

    @Override
    public Album updateAlbumStockById(Long albumId, StockDTO stockDTO) {

        Album album = getAlbumById(albumId);// If ID is not present this method should throw an error
        Stock stock = album.getStock();
        stock.setQuantityInStock(stock.getQuantityInStock() + stockDTO.getQuantityToAdd());
        album.setDateModified(Instant.now());

        album.setStock(stockService.savedUpdatedStock(stock));

        return albumRepository.save(album);
    }

    @Override
    public String deleteAlbumById(Long albumId) {
        if (albumRepository.existsById(albumId)){
            albumCache.setValid(false);
            albumRepository.deleteById(albumId);
            return String.format(
                    "Album of ID '%d' has been deleted",
                    albumId
            );
        } else {
            throw new ItemNotFoundException(String.format("Album with the ID '%s' cannot be found", albumId));
        }
    }

    @Override
    public String decreaseStockByAlbumId(Long albumId) {
        if(albumRepository.findById(albumId).isPresent()){
            Album album = albumRepository.findById(albumId).get();// If ID is not present this method should throw an error
            Stock stock = album.getStock();

            if (stock.getQuantityInStock() > 0){
                stock.setQuantityInStock(stock.getQuantityInStock() - 1);
                album.setStock(stockService.savedUpdatedStock(stock));
                album.setDateModified(Instant.now());
                albumRepository.save(album);
                return String.format(
                        "Album Title: %s\nArist: %s\nQuantity in stock: %d",
                        album.getTitle(),
                        album.getArtist().getArtistName(),
                        album.getStock().getQuantityInStock()
                );
            }else {
                return String.format(
                        "Sorry, '%s' by '%s' is out of stock!",
                        album.getTitle(),
                        album.getArtist()
                );
            }

        }else {
            throw new ItemNotFoundException(String.format("Album with the ID '%s' cannot be found", albumId));
        }
    }
}
