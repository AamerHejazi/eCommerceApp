package com.example.demo.controllers;

import com.example.demo.model.persistence.User;
import com.example.demo.model.persistence.repositories.CartRepository;
import com.example.demo.model.persistence.repositories.UserRepository;
import com.example.demo.model.requests.CreateUserRequest;
import com.example.demo.utils.TestUtils;
import org.junit.Before;
import org.junit.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class UserControllerTest {

    private final UserRepository userRepo = mock(UserRepository.class);
    private final CartRepository cartRepo = mock(CartRepository.class);
    private final BCryptPasswordEncoder encoder = mock(BCryptPasswordEncoder.class);
    private UserController userController;

    @Before
    public void setUp() {
        userController = new UserController();
        TestUtils.injectObject(userController, userRepo, "userRepository");
        TestUtils.injectObject(userController, cartRepo, "cartRepository");
        TestUtils.injectObject(userController, encoder, "bCryptPasswordEncoder");
    }

    @Test
    public void create_user_happy_path() throws Exception {

        // call function to create a new user
        final ResponseEntity<User> response = createNewUser();

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());

        User user = response.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

    }

    @Test
    public void findUserById() throws Exception {

        // call function to create a new user
        final ResponseEntity<User> createUserResponse = createNewUser();
        assertNotNull(createUserResponse);
        assertEquals(200, createUserResponse.getStatusCodeValue());

        User user = createUserResponse.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());

        //find the user by id
        when(userRepo.findById(user.getId())).thenReturn(Optional.of(user));
        ResponseEntity<User> findUserById = userController.findById(user.getId());

        User userById = findUserById.getBody();

        assertNotNull(userById);
        assertEquals(200, findUserById.getStatusCodeValue());
        assertEquals(0, userById.getId());
        assertEquals("test", userById.getUsername());
        assertEquals("thisIsHashed", userById.getPassword());

    }


    @Test
    public void findUserByUserName() {
        // call function to create a new user
        final ResponseEntity<User> createUserResponse = createNewUser();
        assertNotNull(createUserResponse);
        assertEquals(200, createUserResponse.getStatusCodeValue());

        User user = createUserResponse.getBody();
        assertNotNull(user);
        assertEquals(0, user.getId());
        assertEquals("test", user.getUsername());
        assertEquals("thisIsHashed", user.getPassword());


        //find the user by username
        when(userRepo.findByUsername(user.getUsername())).thenReturn(user);
        ResponseEntity<User> findUserByUserName = userController.findByUserName(user.getUsername());

        User userById = findUserByUserName.getBody();

        assertNotNull(userById);
        assertEquals(200, findUserByUserName.getStatusCodeValue());
        assertEquals(0, userById.getId());
        assertEquals("test", userById.getUsername());
        assertEquals("thisIsHashed", userById.getPassword());

    }

    @Test
    public void create_user_failure_path() {
        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest createUserRequest = new CreateUserRequest();
        createUserRequest.setUsername("Amer");
        createUserRequest.setPassword("test");
        createUserRequest.setConfirmPassword("test");

        final ResponseEntity<User> response = userController.createUser(createUserRequest);

        assertNotNull(response);
        assertEquals(400, response.getStatusCodeValue());
        User user = response.getBody();
        assertNull(user);
    }

    @Test
    public void testFindByIdNotFound() {
        final ResponseEntity<User> response = userController.findByUserName("Amer");
        assertEquals(404, response.getStatusCodeValue());
    }

    @Test
    public void testFindByUsernameNotFound() {
        final ResponseEntity<User> response = userController.findById(1L);
        assertEquals(404, response.getStatusCodeValue());
    }

    public ResponseEntity<User> createNewUser() {

        when(encoder.encode("testPassword")).thenReturn("thisIsHashed");
        CreateUserRequest userRequest = new CreateUserRequest();

        userRequest.setUsername("test");
        userRequest.setPassword("testPassword");
        userRequest.setConfirmPassword("testPassword");

        return userController.createUser(userRequest);
    }
}
