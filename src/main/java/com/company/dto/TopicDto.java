package com.company.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class TopicDto {

    @NotBlank(message = "Title cannot be blank")
    String title;
    @NotBlank(message = "Content cannot be blank")
    String content;

}
