package com.housetreasure.repository;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.housetreasure.model.Item;

public interface ItemRepository extends MongoRepository<Item, String> {

}
