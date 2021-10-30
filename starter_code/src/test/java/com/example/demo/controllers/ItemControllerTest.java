package com.example.demo.controllers;

import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ItemControllerTest {

    private ItemController itemController;
    private final ItemRepository itemRepo = mock(ItemRepository.class);

    @Before
    public void setUp() {
        itemController = new ItemController();
        TestUtils.injectObject(itemController, itemRepo, "itemRepository");
    }

    @Test
    public void getItems() {
        final ResponseEntity<List<Item>> response = itemController.getItems();
        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertNotNull(response.getBody());
        assertEquals(0, response.getBody().size());
    }

    @Test
    public void getItemById() {
        Item newItem = createNewItem(1L, "Fidget Spinner", "Toy", new BigDecimal("5"));
        when(itemRepo.findById(newItem.getId())).thenReturn(Optional.of(newItem));

        ResponseEntity<Item> response = itemController.getItemById(1L);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Item itemFound = response.getBody();
        assertNotNull(itemFound);
        assertEquals(Long.valueOf(1L), itemFound.getId());
        assertEquals("Fidget Spinner", itemFound.getName());
        assertEquals("Toy", itemFound.getDescription());
        assertEquals(BigDecimal.valueOf(5), itemFound.getPrice());
    }

    @Test
    public void getItemsByName() {
        Item newItem1 = createNewItem(1L, "Fidget Spinner", "Metal Toy", new BigDecimal("5"));
        Item newItem2 = createNewItem(1L, "Fidget Spinner", "Plastic Toy", new BigDecimal("5"));
        List<Item> items = new ArrayList<>();
        items.add(newItem1);
        items.add(newItem2);
        when(itemRepo.findByName("Fidget Spinner")).thenReturn(items);

        ResponseEntity<List<Item>> response = itemController.getItemsByName("Fidget Spinner");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<Item> itemsFound = response.getBody();
        assertNotNull(itemsFound);
        assertEquals(Long.valueOf(1L), itemsFound.get(0).getId());
        assertEquals("Fidget Spinner", itemsFound.get(0).getName());
        assertEquals("Metal Toy", itemsFound.get(0).getDescription());
        assertEquals(BigDecimal.valueOf(5), itemsFound.get(0).getPrice());
    }

    public Item createNewItem(Long id, String name, String description, BigDecimal price) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setDescription(description);
        newItem.setPrice(price);
        return newItem;
    }
}
