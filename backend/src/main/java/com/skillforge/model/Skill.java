package com.skillforge.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "id")
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 80)
    private String name;

    /** Nullable so pre-Module-2 rows (created before this column existed) don't break;
     *  DataSeeder backfills it for the whole baseline catalog on next startup. */
    @Enumerated(EnumType.STRING)
    @Column(length = 30)
    private SkillCategory category;

    public Skill(String name) {
        this.name = name;
    }

    public Skill(String name, SkillCategory category) {
        this.name = name;
        this.category = category;
    }
}
