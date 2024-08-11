package models;

import lombok.*;

import javax.persistence.*;

@Entity
@Data
@DiscriminatorValue("PUBLICATION")
public class Publication {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(length = 2000)
    private String content;

    @ManyToOne
    @JoinColumn(name = "author_id", nullable = false) // Убедитесь, что имя колонки верное
    private Author author;

    @ManyToOne // Добавляем связь с редактором
    @JoinColumn(name = "editor_id") // Предполагая, что такая колонка существует
    private Editor editor;

    @Override
    public String toString() {
        return "Publication{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", content='" + content + '\'' +
                ", author=" + (author != null ? author.getName() : "none") +
                ", editor=" + (editor != null ? editor.getName() : "none") + // Добавляем редактора
                '}';
    }
}