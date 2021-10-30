package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.ItemRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.ModifyCartRequest;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Optional;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CartControllerTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final ItemRepository itemRepo = mock(ItemRepository.class);
    private CartController cartController;

    @Before
    public void setUp() {
        cartController = new CartController();
        TestUtils.injectObject(cartController, userRepo, "userRepository");
        TestUtils.injectObject(cartController, cartRepo, "cartRepository");
        TestUtils.injectObject(cartController, itemRepo, "itemRepository");
    }

    @Test
    public void addItemToCart() {
        ModifyCartRequest createNewCart = createNewCart();
        final ResponseEntity<Cart> response = cartController.addTocart(createNewCart);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();

        assertNotNull(cart);
        assertEquals(3, cart.getItems().size());
    }

    @Test
    public void removeItemFromCart() {
        ModifyCartRequest newCartRequest = createNewCart();
        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        Cart cart = response.getBody();

        assertNotNull(cart);
        assertEquals(0, cart.getItems().size());
    }

    @Test
    public void addItemToCartWithoutUser() {
        ModifyCartRequest newCartRequest = createCartRequest(1L, 5, "Amer");
        ArrayList<Item> listOfItems = new ArrayList<Item>();

        when(userRepo.findByUsername("Amer")).thenReturn(null);
        when(itemRepo.findById(anyLong())).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testAddToCartNoItem() {
        Cart newCart = new Cart();
        User newUser = createUser(1l, "Amer", "12345678", newCart);
        Item newItem = createnewItem(1L, "Round Spinner", new BigDecimal("2"), "Spinner Desc");
        ModifyCartRequest newCartRequest = createCartRequest(1L, 5, "Amer");
        newCart = createCart(1l, null, newUser);

        when(userRepo.findByUsername("Amer")).thenReturn(newUser);
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(null));

        final ResponseEntity<Cart> response = cartController.addTocart(newCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartNoUser() {
        ModifyCartRequest newCartRequest = createCartRequest(1L, 5, "Amer");

        when(userRepo.findByUsername("Amer")).thenReturn(null);

        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testRemoveFromCartNoItem() {

        // create new cart
        Cart newCart = new Cart();
        User newUser = createUser(1l, "Amer", "password", newCart);

        Item newItem = createnewItem(1L, "Desc", new BigDecimal("2"), "Desc");
        ModifyCartRequest newCartRequest = createCartRequest(1L, 5, "Amer");

        when(userRepo.findByUsername("Amer")).thenReturn(newUser);
        when(itemRepo.findById(1L)).thenReturn(Optional.ofNullable(null));

        final ResponseEntity<Cart> response = cartController.removeFromcart(newCartRequest);

        assertNotNull(response);
        assertEquals(404, response.getStatusCodeValue());
    }
    
    
    // Create cart, user and item
    public ModifyCartRequest createNewCart() {
        Cart newCart = new Cart();
        User newUser = createUser(1l, "Amer", "12345678", newCart);
        Item newItem = createnewItem(1L, "Round Spinner", new BigDecimal("2"), "Spinner Desc");
        ModifyCartRequest newCartRequest = createCartRequest(1L, 3, "Amer");
        ArrayList<Item> listOfItems = new ArrayList<Item>();
        listOfItems.add(newItem);
        createCart(1l, listOfItems, newUser);

        when(userRepo.findByUsername("Amer")).thenReturn(newUser);
        when(itemRepo.findById(1L)).thenReturn(Optional.of(newItem));
        return newCartRequest;
    }

   
    // create a cart and set user to it
    public ModifyCartRequest createCartRequest(long itemId, int quantity, String username) {
        ModifyCartRequest cartRequest = new ModifyCartRequest();
        cartRequest.setItemId(itemId);
        cartRequest.setQuantity(quantity);
        cartRequest.setUsername(username);
        return cartRequest;
    }
    
    // create a user and create cart for user
    public User createUser(long userId, String username, String password, Cart cart) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setCart(cart);
        return newUser;
    }

    
    // create item
    public Item createnewItem(Long id, String name, BigDecimal price, String description) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setDescription(description);
        return newItem;
    }

    
    // create Cart and set user for it
    public Cart createCart(long cartId, ArrayList<Item> items, User user) {
        Cart newCart = new Cart();
        newCart.setId(cartId);
        newCart.setItems(items);
        newCart.setUser(user);
        return newCart;
    }

}
