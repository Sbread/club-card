package com.t1project.club_card.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.Set;

@Table("RoleCardTemplates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoleCardTemplate {
    @Id
    private Integer id;
    @Column("role")
    private String role;
    @Column("templates")
    private Set<String> templates;
}
