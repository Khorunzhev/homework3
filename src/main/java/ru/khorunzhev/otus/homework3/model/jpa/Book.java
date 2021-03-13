package ru.khorunzhev.otus.homework3.model.jpa;

import lombok.*;
import org.hibernate.annotations.BatchSize;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.*;
import java.util.Set;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
@ToString(exclude = "comment")
@EqualsAndHashCode(exclude = "comment")
@Builder
@Table(name = "BOOK")
@NamedEntityGraph(name = "BOOK.authorAndGenre",
        attributeNodes = { @NamedAttributeNode("author"),  @NamedAttributeNode("genre")}
)
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "TITLE", nullable = false, unique = true)
    private String title;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(targetEntity = ru.khorunzhev.otus.homework2.model.Author.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "AUTHOR_ID")
    private ru.khorunzhev.otus.homework2.model.Author author;

    @Fetch(FetchMode.JOIN)
    @ManyToOne(targetEntity = Genre.class, cascade = {CascadeType.MERGE, CascadeType.PERSIST}, fetch = FetchType.EAGER)
    @JoinColumn(name = "GENRE_ID")
    private Genre genre;

    @Fetch(FetchMode.SELECT)
    @BatchSize(size = 3)
    @OneToMany(targetEntity = ru.khorunzhev.otus.homework2.model.Comment.class, mappedBy = "book", fetch = FetchType.LAZY)
    private Set<ru.khorunzhev.otus.homework2.model.Comment> comment;

}
