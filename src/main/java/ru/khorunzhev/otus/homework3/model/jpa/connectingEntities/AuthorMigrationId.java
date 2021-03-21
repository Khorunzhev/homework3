package ru.khorunzhev.otus.homework3.model.jpa.connectingEntities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
@Entity
@Table(name = "AUTHOR_MIGRATION_ID")
public class AuthorMigrationId {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long ID;

    @Column(name = "ID_NOSQL", nullable = false, unique = true)
    private String noSqlId;
}
