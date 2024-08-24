package com.t1project.club_card.members;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table(name = "Roles")
@Data
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class ClubMemberRole {
    @Id
    private Integer id;
    @Column("role")
    private String role;
}
