package com.val.junit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.assertj.core.api.MapAssert.assertThatMap;

import com.val.junit.resolver.UserServiceParameterResolver;
import java.time.Duration;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;
import javax.swing.text.html.Option;
import org.hamcrest.MatcherAssert;
import org.hamcrest.collection.IsEmptyCollection;
import org.hamcrest.collection.IsMapContaining;

import static org.assertj.core.api.Assertions.assertThat;

import com.val.junit.dto.User;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.RepetitionInfo;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInfo;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.api.Timeout;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.CsvFileSource;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.EmptySource;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;

@Tag("fast")
@TestInstance(Lifecycle.PER_METHOD)
@ExtendWith({
    UserServiceParameterResolver.class
})
@Timeout(value = 250, unit = TimeUnit.MILLISECONDS)
//@TestMethodOrder(MethodOrderer.DisplayName.class)
public class UserServiceTest {

  private static final User IVAN = User.of(1, "Ivan", "123");
  private static final User ADMIN = User.of(2, "admin", "pass");
  UserService userService;

  UserServiceTest(TestInfo testInfo) {
    System.out.println("Constructor" + testInfo.getDisplayName());
  }

  @BeforeAll
  static void setUpAll() {
    System.out.println("Before all tests" + UserServiceTest.class);
  }

  @BeforeEach
  void setUp(UserService userService) {
    System.out.println("Before each test" + this);
    this.userService = userService;
  }

  @Test
  void testUsersList_IfNoUsersAdded_ShouldReturnEmpty(UserService userService) {
    List<User> users = userService.getAll();
//    fail("Not yet implemented");
    MatcherAssert.assertThat(users, IsEmptyCollection.empty());
    assertThat(users).hasSize(0);
    //assertTrue(users.isEmpty(), () -> "List is not empty");
  }

  @Test
  void testUsersList_IfUsersAdded_ShouldReturnNonEmpty() {
    UserService userService = new UserService();
//    List<User> all = userService.getAll();
    userService.add(IVAN);
    var users = userService.getAll();

    Assertions.assertEquals(1, users.size());
  }

  @Test
  @Tag("login")
  void testLoginSuccess_IfUserExists_ShouldReturnTrue() {
    userService.add(IVAN);
    Optional<User> potentialUser = userService.login(IVAN.getUsername(), IVAN.getPassword());
    assertThat(potentialUser).isPresent();
    potentialUser.ifPresent(user -> assertThat(user).isEqualTo(IVAN));
  }

  @Test
  void testLoginFail_IfPasswordNotCorrect_ShouldReturnTrue() {
    userService.add(IVAN);
    Optional<User> potentialUser = userService.login(IVAN.getUsername(), "1234");
    assertThat(potentialUser.isEmpty()).isTrue();
  }

//  @Test
  @RepeatedTest(value = 3, name = RepeatedTest.DISPLAY_NAME_PLACEHOLDER + ": {currentRepetition}/{totalRepetitions}")
  void testLoginFail_IfUserDoesNotExist_ShouldReturnTrue(RepetitionInfo repetitionInfo) {
    userService.add(IVAN);
    Optional<User> potentialUser = userService.login("dummy", IVAN.getPassword());
    Assertions.assertTrue(potentialUser.isEmpty());
  }

  @Nested
  @DisplayName("test user login functionality")
  @Tag("login")
  @Timeout(value = 150, unit = TimeUnit.MILLISECONDS)
  @TestInstance(Lifecycle.PER_CLASS)
  class LoginTest {

    @BeforeAll
    static void setUpAll() {
      System.out.println("Hello: Before all tests" + UserServiceTest.class);
    }

    @BeforeEach
    void setUp() {
      userService.add(IVAN);
      userService.add(ADMIN);
    }

    @BeforeEach
    void setUp(TestInfo testInfo) {
      System.out.println("Before each test" + testInfo.getDisplayName());
    }

    @Test
    void testLogin_WhenUserLogin_TimeShouldBeLessThan30ms() {
      Optional<User> optionalUser = Assertions.assertTimeout(Duration.ofMillis(30L), () -> {
        Thread.sleep(10L);
        return userService.login(IVAN.getUsername(), IVAN.getPassword());
      });
      assertThat(optionalUser).isEqualTo(Optional.of(IVAN));
    }

    @Test
    void testLogin_WhenUserLoginAsAdmin_TimeShouldBeLessThan15ms() {
       Optional<User> user = Assertions.assertTimeoutPreemptively(Duration.ofMillis(15L), () -> {
        Thread.sleep(5L);
        return userService.login(ADMIN.getUsername(), ADMIN.getPassword());
      });
    }

//    @SuppressWarnings("OptionalUsedAsFieldOrParameterType")
    @ParameterizedTest(name = "{arguments} + test")
//  @ArgumentsSource()
//  @NullSource
//  @EmptySource
//  @ValueSource(strings = {"admin", "ivan"})
    @MethodSource("com.val.junit.service.UserServiceTest#getArgumentsForLoginTest")
//  @CsvFileSource(resources = "/login-test-data.csv", delimiter = ',', numLinesToSkip = 1)
//  @CsvSource({"Ivan,123", "admin,pass"})
    void testLogin_WhenUserExists_ShouldReturnTrue(String username, String password, Optional<User> expectedUser) {
      var optionalUser = userService.login(username, password);
      assertThat(optionalUser).isEqualTo(expectedUser);
    }
  }

  static Stream<Arguments> getArgumentsForLoginTest() {
    return Stream.of(Arguments.of("Ivan", "123", Optional.of(IVAN)),
        Arguments.of("admin", "pass", Optional.of(ADMIN)),
        Arguments.of("dummy", "123", Optional.empty()));
  }

  @AfterEach
  void deleteDataFromDatabase() {
    System.out.println("After each test" + this + "\n");
  }

  @AfterAll
  static void closeConnectionPool() {
    System.out.println("After all tests" + UserServiceTest.class);
  }

}
