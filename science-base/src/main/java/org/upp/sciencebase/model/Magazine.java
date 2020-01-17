package org.upp.sciencebase.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
public class Magazine {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String issn;

    @Column(nullable = false)
    private boolean enabled;

    @ManyToMany
    private Set<ScienceArea> scienceAreas;

    @ManyToOne
    private PaymentMethod paymentMethod;

    @ManyToOne
    private User mainEditor;

    @ManyToMany
    private Set<User> areaEditors;

    @ManyToMany
    private Set<User> areaReviewers;

}
