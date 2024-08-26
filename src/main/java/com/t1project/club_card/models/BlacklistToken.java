package com.t1project.club_card.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Table("BlacklistTokens")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BlacklistToken {
    @Id
    private Integer id;
    private String token;
}
