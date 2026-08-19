package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.EmailAlreadyExistsException;
import com.batuhan.bankingapi.exception.UserNotFoundException;
import com.batuhan.bankingapi.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    private UserService userService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        userService = new UserService(
                userRepository,
                passwordEncoder
        );
    }

    @Test
    void shouldEncodePasswordWhenSavingUser() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("12345678");

        when(userRepository.existsByEmail("test@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("12345678"))
                .thenReturn("$2a$10$encodedPassword");

        when(userRepository.save(user))
                .thenReturn(user);

        userService.saveUser(user);

        verify(passwordEncoder)
                .encode("12345678");

        verify(userRepository)
                .save(user);
    }
    @Test
    void shouldSaveEncodedPasswordInsteadOfRawPassword() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("12345678");

        when(userRepository.existsByEmail("test@test.com"))
                .thenReturn(false);

        when(passwordEncoder.encode("12345678"))
                .thenReturn("$2a$10$encodedPassword");

        userService.saveUser(user);

        ArgumentCaptor<User> userCaptor =
                ArgumentCaptor.forClass(User.class);

        verify(userRepository)
                .save(userCaptor.capture());

        User savedUser = userCaptor.getValue();

        assertEquals(
                "$2a$10$encodedPassword",
                savedUser.getPassword()
        );

        assertNotEquals(
                "12345678",
                savedUser.getPassword()
        );
    }
    @Test
    void shouldNotSaveUserWhenEmailAlreadyExists() {

        User user = new User();
        user.setEmail("test@test.com");
        user.setPassword("12345678");

        when(userRepository.existsByEmail("test@test.com"))
                .thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.saveUser(user)
        );

        verify(passwordEncoder, never())
                .encode(anyString());

        verify(userRepository, never())
                .save(any(User.class));
    }
    @Test
    void shouldThrowExceptionWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserById(999L)
        );
    }
    @Test
    void shouldReturnUserWhenUserExists() {

        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        User result = userService.getUserById(1L);

        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@test.com", result.getEmail());
    }
    @Test
    void shouldThrowExceptionWhenUpdatingWithExistingEmail() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Test User");
        existingUser.setEmail("old@test.com");

        User updatedUser = new User();
        updatedUser.setFullName("Updated User");
        updatedUser.setEmail("used@test.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmailAndIdNot(
                "used@test.com",
                1L
        )).thenReturn(true);

        assertThrows(
                EmailAlreadyExistsException.class,
                () -> userService.updateUser(
                        1L,
                        updatedUser
                )
        );

        verify(userRepository, never())
                .save(any(User.class));
    }
    @Test
    void shouldUpdateUserSuccessfully() {

        User existingUser = new User();
        existingUser.setId(1L);
        existingUser.setFullName("Old Name");
        existingUser.setEmail("old@test.com");

        User updatedUser = new User();
        updatedUser.setFullName("New Name");
        updatedUser.setEmail("new@test.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(existingUser));

        when(userRepository.existsByEmailAndIdNot(
                "new@test.com",
                1L
        )).thenReturn(false);

        when(userRepository.save(existingUser))
                .thenReturn(existingUser);

        User result = userService.updateUser(
                1L,
                updatedUser
        );

        assertEquals("New Name", result.getFullName());
        assertEquals("new@test.com", result.getEmail());

        verify(userRepository).save(existingUser);
    }
    @Test
    void shouldDeleteUserSuccessfully() {

        User user = new User();
        user.setId(1L);
        user.setEmail("test@test.com");

        when(userRepository.findById(1L))
                .thenReturn(Optional.of(user));

        userService.deleteUser(1L);

        verify(userRepository)
                .deleteById(1L);
    }
    @Test
    void shouldNotDeleteUserWhenUserDoesNotExist() {

        when(userRepository.findById(999L))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.deleteUser(999L)
        );

        verify(userRepository, never())
                .deleteById(999L);
    }
    @Test
    void shouldReturnUserWhenEmailExists() {

        User user = new User();
        user.setId(1L);
        user.setFullName("Test User");
        user.setEmail("test@test.com");

        when(userRepository.findByEmail("test@test.com"))
                .thenReturn(Optional.of(user));

        User result = userService.getUserByEmail("test@test.com");

        assertEquals(1L, result.getId());
        assertEquals("Test User", result.getFullName());
        assertEquals("test@test.com", result.getEmail());
    }
    @Test
    void shouldThrowExceptionWhenEmailDoesNotExist() {

        when(userRepository.findByEmail("missing@test.com"))
                .thenReturn(Optional.empty());

        assertThrows(
                UserNotFoundException.class,
                () -> userService.getUserByEmail("missing@test.com")
        );
    }
}