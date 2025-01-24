package service;

import dto.AdminUserDto;
import model.User;

import java.util.List;

public interface UserService {
    AdminUserDto createUser(User user);

    void deleteUser(Long userId);

    List<User> readUsers(List<Long> idList, int from, int size);

    AdminUserDto findUserById(Long id);
}