package org.telegram.bot.beldtp.repository.interf;


import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;

import java.util.List;

public interface UserRepository {
    List<User> get(UserRole userRole);

    List<User> get(Language language);

    User save(User user);

    void delete(User user);
}
