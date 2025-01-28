package ru.practicum.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import exception.NotFoundException;
import exception.NotUniqueException;
import ru.practicum.dto.AdminUserDto;
import ru.practicum.mapper.UserMapper;
import ru.practicum.model.User;
import ru.practicum.repository.UserMainServiceRepository;
import ru.practicum.service.UserService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserMainServiceRepository userMainServiceRepository;
    private final UserMapper userMapper;

    @Transactional
    @Override
    public AdminUserDto createUser(User user) {
        log.info("createUser - invoked");
        if (userMainServiceRepository.existsByEmail(user.getEmail())) {
            throw new NotUniqueException("User with this email already exists");
        }
        AdminUserDto adminDto = userMapper.toAdminUserDto(userMainServiceRepository.save(user));
        log.info("Result: user {} - created", adminDto);
        return adminDto;
    }

    @Transactional
    @Override
    public void deleteUser(Long userId) {
        log.info("deleteUser - invoked");
        if (!userMainServiceRepository.existsById(userId)) {
            log.error("User with id = {} not exist", userId);
            throw new NotFoundException("User not found");
        }
        log.info("Result: user with id = {} - removed", userId);
        userMainServiceRepository.deleteById(userId);
    }

    @Override
    public List<User> readUsers(List<Long> idList, int from, int size) {
        log.info("readUsers - invoked");
        Pageable pageable = createPageRequestAsc(from, size);

        if (idList.isEmpty()) {
            List<User> allUsers = userMainServiceRepository.findAllUser(pageable);
            log.info("Result: idList is empty, returning all users size = {}", allUsers.size());
            return allUsers;
        }

        List<User> users = userMainServiceRepository.findAllByIdIn(idList, pageable);
        if (users.isEmpty()) {
            return List.of();
        }
        log.info("Result: list of Users size = {}", users.size());
        return users;
    }

    private static PageRequest createPageRequestAsc(int from, int size) {
        return PageRequest.of(from, size, Sort.Direction.ASC, "id");
    }

    @Override
    public AdminUserDto findUserById(Long id) {
        Optional<User> user = userMainServiceRepository.findById(id);

        if (user.isEmpty()) {
            log.error("User with id {} not found", id);
            throw new NotFoundException(String.format("User with id=%s was not found", id));
        }

        log.info("User with id {} found", id);
        return userMapper.toAdminUserDto(user.get());
    }
}