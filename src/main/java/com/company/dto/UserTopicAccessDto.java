package com.company.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class UserTopicAccessDto {

    private String username;

    private boolean canEdit;

    private boolean canComment;

    private boolean isAdmin;

}
