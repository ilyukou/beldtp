package org.telegram.bot.beldtp.repository.jpa;


import org.springframework.data.jpa.repository.JpaRepository;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;

import java.util.List;

public interface UserJpaRepository extends JpaRepository<User, Long> {
    List<User> findByRole(UserRole userRole);

    List<User> findByLanguage(Language language);

    User findByUsername(String username);
}
