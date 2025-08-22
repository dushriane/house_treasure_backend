package com.housetreasure.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.housetreasure.model.Item;
import com.housetreasure.repository.ItemRepository;

@Service
public class ItemService {
    private final ItemRepository itemRepository;

    public ItemService(ItemRepository itemRepository){
        this.itemRepository = itemRepository;
    }

    public List<Item> getAllItems(){
        return itemRepository.findAll();
    }

    public Item saveItem(Item item){
        item.setUpdatedAt(LocalDateTime.now());
        return itemRepository.save(item);
    }

    
}
