package com.val.junit.service;

import com.val.junit.dto.User;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class UserService {

  private final List<User> users = new ArrayList<>();

  public List<User> getAll() {
    return users;
  }

  public void add(User... users) {
    this.users.addAll(List.of(users));
  }

  public Optional<User> login(String username, String password) {
    return users.stream()
        .filter(user -> user.getUsername().equals(username) && user.getPassword().equals(password))
        .findFirst();
  }
}
