package models;

import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

@Entity
@Table(name = "books")
@Data
public class Book {

    private static final Logger logger = Logger.getLogger(Book.class.getName());

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    private String title;

    @ManyToOne(cascade = CascadeType.ALL) // Связь с автором
    @JoinColumn(name = "author_id", nullable = false) // Имя колонки для внешнего ключа
    private Author author;

    private int year;

    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(name = "Book_Person",
            joinColumns = @JoinColumn(name = "book_id"),
            inverseJoinColumns = @JoinColumn(name = "person_id"))
    private Collection<Person> people = new ArrayList<>();

    @Override
    public String toString() {
        return "Book{" +
                "author=" + author +
                ", id=" + id +
                ", title='" + title + '\'' +
                ", year=" + year +
                '}';
    }
}