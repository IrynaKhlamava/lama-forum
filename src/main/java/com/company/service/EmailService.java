package com.company.service;

import com.company.model.enumType.RoleName;

public interface EmailService {

    void prepareAndSendEmail(String email, String token, RoleName role);

}
