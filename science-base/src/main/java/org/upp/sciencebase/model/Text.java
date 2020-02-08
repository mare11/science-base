package org.upp.sciencebase.model;

import lombok.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Text {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String title;

    @Column(nullable = false)
    private String keyTerms;

    @Column(nullable = false)
    private String apstract;

    @Column(columnDefinition = "blob")
    private byte[] file;

    @ManyToOne
    private ScienceArea scienceArea;

    @ManyToOne
    private User author;

    @EqualsAndHashCode.Exclude
    @ToString.Exclude
    @OneToMany(mappedBy = "text")
    private Set<Author> coauthors;

    @ManyToOne
    private Magazine magazine;
}
