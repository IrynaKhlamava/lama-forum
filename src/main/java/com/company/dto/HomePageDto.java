package com.company.dto;

import com.company.model.Topic;
import com.company.model.User;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class HomePageDto {

    private List<Topic> topics;
    private int currentPage;
    private int size;
    private boolean hasNext;
    private boolean hasPrev;
    private String message;
    private String username;
    private User currentUser;
    private boolean isAdmin;
}
