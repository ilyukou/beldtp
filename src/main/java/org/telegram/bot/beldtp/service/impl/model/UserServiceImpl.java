package org.telegram.bot.beldtp.service.impl.model;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.telegram.bot.beldtp.model.Language;
import org.telegram.bot.beldtp.model.User;
import org.telegram.bot.beldtp.model.UserRole;
import org.telegram.bot.beldtp.repository.interf.UserRepository;
import org.telegram.bot.beldtp.service.interf.model.UserService;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public User save(User user) {
        return userRepository.save(user);
    }

    @Override
    public User update(User user, Update update) {
        int i = 0;
        org.telegram.telegrambots.meta.api.objects.User userFromUpdate = null;

        if (update.hasCallbackQuery()) {
            userFromUpdate = update.getCallbackQuery().getFrom();
        }

        if (update.hasMessage()) {
            userFromUpdate = update.getMessage().getFrom();
        }

        if (userFromUpdate == null) {
            return user;
        }

        user = user.update(userFromUpdate);

        return save(user);
    }

    @Override
    public User get(Long id) {
        return userRepository.get(id);
    }

    @Override
    public void delete(Long id) {
        userRepository.delete(id);
    }

    @Override
    public List<User> getAll() {
        return userRepository.getAll();
    }

    @Override
    public boolean isExist(Long id) {
        return userRepository.isExist(id);
    }

    @Override
    public long size(UserRole userRole) {
        return userRepository.size(userRole);
    }

    @Override
    public long size() {
        return userRepository.size();
    }

    @Override
    public long size(Language language) {
        return userRepository.size(language);
    }

    @Override
    public User get(String username) {
        return userRepository.get(username);
    }
}
