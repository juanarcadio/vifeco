package org.laeq.model;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;

import javax.annotation.Nonnull;
import java.util.Objects;

public class User {
    private SimpleIntegerProperty id;
    private SimpleStringProperty firstName;
    private SimpleStringProperty lastName;
    private SimpleStringProperty email;

    public User() {
        this.id = new SimpleIntegerProperty(0);
        this.firstName = new SimpleStringProperty("");
        this.lastName = new SimpleStringProperty("");
        this.email = new SimpleStringProperty("");
    }

    public User(Integer id, String fName, String lastName, String email) {
        this(fName, lastName, email);
        this.id.set(id);
    }

    public User(String fName, String lName, String email) {
        this.id = new SimpleIntegerProperty(0);
        this.firstName = new SimpleStringProperty(fName);
        this.lastName = new SimpleStringProperty(lName);
        this.email = new SimpleStringProperty(email);
    }

    public String getFirstName() {
        return firstName.get();
    }
    public void setFirstName(String fName) {
        firstName.set(fName);
    }

    public String getLastName() {
        return lastName.get();
    }
    public void setLastName(String fName) {
        lastName.set(fName);
    }

    public String getEmail() {
        return email.get();
    }
    public void setEmail(String fName) {
        email.set(fName);
    }

    public void setId(int id) {
        this.id.set(id);
    }
    public int getId() {
        return this.id.getValue();
    }

    @Override
    public String toString() {
        return "User{" +
                "id=" + id +
                ", firstName=" + firstName +
                ", lastName=" + lastName +
                ", email=" + email +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return id.getValue().equals(user.id.getValue()) &&
                firstName.getValue().equals(user.firstName.getValue()) &&
                lastName.getValue().equals(user.lastName.getValue()) &&
                email.getValue().equals(user.email.getValue());
    }

    @Override
    public int hashCode() {
        return Objects.hash(id.getValue(), firstName.getValue(), lastName.getValue(), email.getValue());
    }
}
