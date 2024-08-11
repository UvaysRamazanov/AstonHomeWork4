package models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@DiscriminatorValue("AUTHOR")
@Data
@EqualsAndHashCode(callSuper = true)
public class Author extends User {

    @Column(nullable = false)
    private String biography;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Publication> publications = new HashSet<>();

    public void addPublication(Publication publication) {
        if (publication != null) {
            publications.add(publication);
            publication.setAuthor(this);
        }
    }
}