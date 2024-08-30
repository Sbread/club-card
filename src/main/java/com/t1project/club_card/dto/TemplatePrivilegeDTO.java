package com.t1project.club_card.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class TemplatePrivilegeDTO {
    private Map<String, Set<String>> templatePrivilegesMap;
}
