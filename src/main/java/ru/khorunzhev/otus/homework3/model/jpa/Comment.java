package ru.khorunzhev.otus.homework3.model.jpa;


import lombok.*;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@ToString(exclude = "book")
@Entity
@Table(name = "COMMENT")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "TEXT", nullable = false)
    private String text;

    @ManyToOne(targetEntity = Book.class, fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "BOOK_ID")
    Book book;

}
