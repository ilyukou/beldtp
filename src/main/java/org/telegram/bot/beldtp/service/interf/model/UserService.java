package org.telegram.bot.beldtp.service.interf.model;

import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;

import java.util.List;

public interface UserService {

    User save(User user);

    User get(Long id);

    void delete(Long id);

    List<User> getAll();

    boolean isExist(Long id);

    long size(UserRole userRole);

    long size();

    long size(Language language);

    User get(String username);
}
