package com.housetreasure.controller;

import java.util.List;

import org.springframework.web.bind.annotation.*;

import com.housetreasure.model.Item;
import com.housetreasure.service.ItemService;

@RestController
@RequestMapping("/api/items")
public class ItemController {
    private final ItemService itemService;

    public ItemController(ItemService itemService){
        this.itemService = itemService;
    }

    @GetMapping
    public List<Item> getAllItems(){
        return itemService.getAllItems();
    }

    @PostMapping
    public Item saveItem(Item item){
        return itemService.saveItem(item);
    }
}
