package org.com.dianping.repository;

import org.com.dianping.entity.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
class UserRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private UserRepository userRepository;

    @Test
    void existsByUsername_ShouldReturnTrue_WhenUsernameExists() {
        // given
        User user = new User("testuser", "encryptedPassword");
        entityManager.persist(user);
        entityManager.flush();

        // when
        boolean exists = userRepository.existsByUsername("testuser");

        // then
        assertTrue(exists);
    }

    @Test
    void existsByUsername_ShouldReturnFalse_WhenUsernameDoesNotExist() {
        // when
        boolean exists = userRepository.existsByUsername("nonexistentuser");

        // then
        assertFalse(exists);
    }

    @Test
    void findByUsername_ShouldReturnUser_WhenUsernameExists() {
        // given
        User user = new User("testuser", "encryptedPassword");
        entityManager.persist(user);
        entityManager.flush();

        // when
        Optional<User> foundUser = userRepository.findByUsername("testuser");

        // then
        assertTrue(foundUser.isPresent());
        assertEquals("testuser", foundUser.get().getUsername());
        assertEquals("encryptedPassword", foundUser.get().getEncryptedPassword());
    }

    @Test
    void findByUsername_ShouldReturnEmptyOptional_WhenUsernameDoesNotExist() {
        // when
        Optional<User> foundUser = userRepository.findByUsername("nonexistentuser");

        // then
        assertFalse(foundUser.isPresent());
    }

    @Test
    void save_ShouldPersistUser_AndGenerateId() {
        // given
        User user = new User("newuser", "encryptedPassword");

        // when
        User savedUser = userRepository.save(user);

        // then
        assertNotNull(savedUser.getId());
        assertEquals("newuser", savedUser.getUsername());
        assertEquals("encryptedPassword", savedUser.getEncryptedPassword());

        // verify it's in the database
        User foundUser = entityManager.find(User.class, savedUser.getId());
        assertNotNull(foundUser);
        assertEquals("newuser", foundUser.getUsername());
    }
}
