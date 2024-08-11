package models;

import lombok.*;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@EqualsAndHashCode(callSuper = true)
@Entity
@DiscriminatorValue("EDITOR")
@Data
public class Editor extends User {

    @Column(name = "access_level")
    private String accessLevel;

    @OneToMany(mappedBy = "editor", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<Publication> publications = new HashSet<>();

    public void addPublication(Publication publication) {
        publications.add(publication);
        publication.setEditor(this);
    }

    @Override
    public String toString() {
        return "Editor{" +
                "id=" + getId() +
                ", name='" + getName() + '\'' +
                ", accessLevel='" + accessLevel + '\'' +
                '}';
    }
}