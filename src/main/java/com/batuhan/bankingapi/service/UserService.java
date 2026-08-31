package com.batuhan.bankingapi.service;

import com.batuhan.bankingapi.entity.User;
import com.batuhan.bankingapi.exception.EmailAlreadyExistsException;
import com.batuhan.bankingapi.exception.UserNotFoundException;
import com.batuhan.bankingapi.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.List;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    private final SecureRandom secureRandom =
            new SecureRandom();

    public UserService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder
    ) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    public User saveUser(User user) {

        if (userRepository.existsByEmail(
                user.getEmail()
        )) {
            throw new EmailAlreadyExistsException(
                    "Bu email zaten kullanılıyor"
            );
        }

        if (user.getNationalId() != null &&
                userRepository.existsByNationalId(
                        user.getNationalId()
                )) {
            throw new EmailAlreadyExistsException(
                    "Bu T.C. kimlik numarası zaten kayıtlı"
            );
        }

        if (user.getPhone() != null &&
                userRepository.existsByPhone(
                        user.getPhone()
                )) {
            throw new EmailAlreadyExistsException(
                    "Bu telefon numarası zaten kayıtlı"
            );
        }

        if (user.getCustomerNumber() == null) {
            user.setCustomerNumber(
                    generateCustomerNumber()
            );
        }

        String encodedPassword =
                passwordEncoder.encode(
                        user.getPassword()
                );

        user.setPassword(encodedPassword);

        return userRepository.save(user);
    }

    private String generateCustomerNumber() {

        String customerNumber;

        do {
            int number =
                    10_000_000 +
                            secureRandom.nextInt(
                                    90_000_000
                            );

            customerNumber =
                    String.valueOf(number);

        } while (
                userRepository.existsByCustomerNumber(
                        customerNumber
                )
        );

        return customerNumber;
    }

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserById(Long id) {
        return userRepository
                .findById(id)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "Kullanıcı bulunamadı"
                        )
                );
    }

    public void deleteUser(Long id) {

        getUserById(id);

        userRepository.deleteById(id);
    }

    public User updateUser(
            Long id,
            User newUser
    ) {

        User user = getUserById(id);

        if (userRepository.existsByEmailAndIdNot(
                newUser.getEmail(),
                id
        )) {
            throw new EmailAlreadyExistsException(
                    "Bu email zaten kullanılıyor"
            );
        }

        user.setFullName(
                newUser.getFullName()
        );

        user.setEmail(
                newUser.getEmail()
                        .trim()
                        .toLowerCase()
        );

        return userRepository.save(user);
    }

    public User getUserByEmail(
            String email
    ) {

        return userRepository
                .findByEmail(email)
                .orElseThrow(
                        () -> new UserNotFoundException(
                                "Kullanıcı bulunamadı"
                        )
                );
    }
}