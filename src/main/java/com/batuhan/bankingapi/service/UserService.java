package com.batuhan.bankingapi.service;

import java.util.List;
import com.batuhan.bankingapi.exception.EmailAlreadyExistsException;
import com.batuhan.bankingapi.repository.UserRepository;
import com.batuhan.bankingapi.entity.User;
import org.springframework.stereotype.Service;
import com.batuhan.bankingapi.exception.UserNotFoundException;
@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository){
        this.userRepository = userRepository;
    }
    public User saveUser(User user) {
        if (userRepository.existsByEmail(user.getEmail())) {
            throw new EmailAlreadyExistsException("Bu email zaten kullanılıyor");
        }
        return userRepository.save(user);
    }
    public List<User>getAllUsers(){
        return userRepository.findAll();
    }
    public User getUserById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new UserNotFoundException("Kullanıcı bulunamadı"));
    }
    public void deleteUser(Long id){
        User user = getUserById(id);
        userRepository.deleteById(id);
    }
    public User updateUser(Long id, User newUser) {

        User user = getUserById(id);

        if (userRepository.existsByEmailAndIdNot(newUser.getEmail(), id)) {
            throw new EmailAlreadyExistsException("Bu email zaten kullanılıyor");
        }

        user.setFullName(newUser.getFullName());
        user.setEmail(newUser.getEmail());

        return userRepository.save(user);
    }

}
