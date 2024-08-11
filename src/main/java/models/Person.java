package models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("PERSON")
@Data
public class Person extends User {

    @Column(name = "age")
    private int age;

    @Column(name = "email", unique = true)
    private String email;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "book_person",
            joinColumns = @JoinColumn(name = "person_id"),
            inverseJoinColumns = @JoinColumn(name = "book_id")
    )
    private Collection<Book> books = new ArrayList<>();

    @Override
    public String toString() {
        return "Person{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", age=" + age +
                ", email='" + email + '\'' +
                '}';
    }
}
