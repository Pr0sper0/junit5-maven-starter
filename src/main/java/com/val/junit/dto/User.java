package com.val.junit.dto;

import lombok.Value;
import lombok.experimental.FieldDefaults;

@Value(staticConstructor = "of")
@FieldDefaults(makeFinal = true, level = lombok.AccessLevel.PRIVATE)
public class User {

  Integer id;
  String username;
  String password;

}
