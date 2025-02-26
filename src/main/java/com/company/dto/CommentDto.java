package com.company.dto;

import jakarta.validation.constraints.NotBlank;

public record CommentDto(
        @NotBlank(message = "Comment cannot be empty")
        String content) {

}
