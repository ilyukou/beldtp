package org.telegram.bot.beldtp.repository.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.repository.interf.UserRepository;
import org.telegram.bot.beldtp.repository.jpa.UserJpaRepository;

import java.util.List;

@Repository
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

    @Override
    public User get(Long id) {
        return userJpaRepository.findById(id).orElse(null);
    }

    @Override
    public void delete(Long id) {
        userJpaRepository.deleteById(id);
    }

    @Override
    public List<User> getAll() {
        return userJpaRepository.findAll();
    }

    @Override
    public boolean isExist(Long id) {
        return userJpaRepository.existsById(id);
    }

    @Override
    public long size(UserRole userRole) {
        return userJpaRepository.findByRole(userRole).size();
    }

    @Override
    public long size() {
        return userJpaRepository.count();
    }

    @Override
    public long size(Language language) {
        return userJpaRepository.findByLanguage(language).size();
    }

    @Override
    public User get(String username) {
        return userJpaRepository.findByUsername(username);
    }
}
