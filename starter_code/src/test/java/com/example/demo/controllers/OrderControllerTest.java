package com.example.demo.controllers;

import com.example.demo.model.persistence.Cart;
import com.example.demo.model.persistence.Item;
import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.UserOrder;
import com.example.demo.model.persistence.repositories.OrderRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class OrderControllerTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final OrderRepository orderRepo = mock(OrderRepository.class);
    private OrderController orderController;

    @Before
    public void setUp() {
        orderController = new OrderController();
        TestUtils.injectObject(orderController, userRepo, "userRepository");
        TestUtils.injectObject(orderController, orderRepo, "orderRepository");
    }

    @Test
    public void submitNewOrder() {
        
        //create item and add it to the list
        Item item = createNewItem(1L, "Key", BigDecimal.valueOf(5), "Car Key");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        
        // create new cart and add user to it
        Cart cart = createNewCart(1L, items, null);
        User user = createNewUser(1L, "Amer", "Amer_12345", cart);
        cart.setUser(user);

        when(userRepo.findByUsername("Amer")).thenReturn(user);
        final ResponseEntity<UserOrder> response = orderController.submit("Amer");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        UserOrder order = response.getBody();
        assertEquals(user, order.getUser());
        assertEquals(items, order.getItems());
        assertEquals(BigDecimal.valueOf(5), order.getTotal());
    }

    @Test
    public void getUserOrders() {

        Item item = createNewItem(1L, "Watch", BigDecimal.valueOf(15), "new item");
        ArrayList<Item> items = new ArrayList<>();
        items.add(item);
        Cart cart = createNewCart(1L, new ArrayList<>(), null);
        User user = createNewUser(1L, "Amer", "Amer_12345", null);
        cart.setUser(user);
        cart.setItems(items);
        user.setCart(cart);

        orderController.submit("Amer");
        when(userRepo.findByUsername("Amer")).thenReturn(user);

        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Amer");

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        List<UserOrder> orders = response.getBody();
        assertNotNull(orders);
    }

    @Test
    public void submitOrderFailuerCase() {
        when(userRepo.findByUsername("Amer")).thenReturn(null);
        final ResponseEntity<UserOrder> response = orderController.submit("Amer");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void getOrderFailuerCase() {
        when(userRepo.findByUsername("Amer")).thenReturn(null);
        final ResponseEntity<List<UserOrder>> response = orderController.getOrdersForUser("Amer");
        assertEquals(404, response.getStatusCodeValue());
    }

    // to create a nre user
    public User createNewUser(long userId, String username, String password, Cart cart) {
        User newUser = new User();
        newUser.setId(userId);
        newUser.setUsername(username);
        newUser.setPassword(password);
        newUser.setCart(cart);
        return newUser;
    }
    
    // to create a new item
    public Item createNewItem(Long id, String name, BigDecimal price, String description) {
        Item newItem = new Item();
        newItem.setId(id);
        newItem.setName(name);
        newItem.setPrice(price);
        newItem.setDescription(description);
        return newItem;
    }

    // to create a new cart
    public Cart createNewCart(long cartId, ArrayList<Item> items, User user) {
        Cart newCart = new Cart();
        newCart.setId(cartId);
        newCart.setItems(items);
        newCart.setUser(user);
        newCart.setTotal(BigDecimal.valueOf(5));
        return newCart;
    }
}
