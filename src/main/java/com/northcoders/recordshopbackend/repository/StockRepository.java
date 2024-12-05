package com.northcoders.recordshopbackend.repository;

import com.northcoders.recordshopbackend.model.Stock;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface StockRepository extends CrudRepository<Stock, Long> {
}