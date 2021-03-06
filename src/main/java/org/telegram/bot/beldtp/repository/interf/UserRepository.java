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

    User get(Long id);

    void delete(Long id);

    List<User> getAll();

    boolean isExist(Long id);

    long size(UserRole userRole);

    long size();

    long size(Language language);

    User get(String username);
}
