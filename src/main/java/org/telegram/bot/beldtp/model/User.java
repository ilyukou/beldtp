package org.telegram.bot.beldtp.model;

import org.telegram.telegrambots.meta.api.objects.Update;

import javax.persistence.*;
import java.util.Set;
import java.util.Stack;

@Entity
@Table(name = "user")
public class User {

    @Id
    private Long id;

    private String firstName;

    private String lastName;

    private String username;

    private UserRole role;

    @OneToMany(fetch = FetchType.EAGER, mappedBy = "user")
    private Set<Incident> incident;

    private Language language;

    @Lob
    private Stack<String> status = new Stack<>();


    public User() {

    }

    public User(Update update) {

        org.telegram.telegrambots.meta.api.objects.User user = null;

        if (update.hasMessage()) {
            user = update.getMessage().getFrom();

        } else if (update.hasCallbackQuery()) {

            user = update.getCallbackQuery().getFrom();
        } else if (update.hasEditedMessage()) {

            user = update.getEditedMessage().getFrom();
        }

        if (user != null) {
            this.username = user.getUserName();
            this.firstName = user.getFirstName();
            this.lastName = user.getLastName();
            this.id = user.getId().longValue();
        }
    }

    public void add(Incident incident) {
        this.incident.add(incident);
    }

    public String peekStatus() {
        return status.peek();
    }

    public void popStatus() {
        status.pop();
    }

    public void pushStatus(String string) {
        status.push(string);
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UserRole getRole() {
        return role;
    }

    public void setRole(UserRole role) {
        this.role = role;
    }

    public Set<Incident> getIncident() {
        return incident;
    }

    public void setIncident(Set<Incident> incident) {
        this.incident = incident;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public Stack<String> getStatus() {
        return status;
    }

    public void setStatus(Stack<String> status) {
        this.status = status;
    }
}
