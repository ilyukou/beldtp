package org.telegram.bot.beldtp.repository.impl;

import org.jvnet.hk2.annotations.Service;
import org.springframework.beans.factory.annotation.Autowired;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.repository.interf.UserRepository;
import org.telegram.bot.beldtp.repository.jpa.UserJpaRepository;

import java.util.List;

@Service
public class UserRepositoryImpl implements UserRepository {

    @Autowired
    private UserJpaRepository userJpaRepository;

    @Override
    public List<User> get(UserRole userRole) {
        return userJpaRepository.findByRole(userRole);
    }

    @Override
    public List<User> get(Language language) {
        return userJpaRepository.findByLanguage(language);
    }

    @Override
    public User save(User user) {
        return userJpaRepository.save(user);
    }

    @Override
    public void delete(User user) {
        userJpaRepository.delete(user);
    }
}
