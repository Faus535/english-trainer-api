package com.faus535.englishtrainer.auth.domain;

public interface EmailPort {

    void sendPasswordResetEmail(String toEmail, String resetToken);
}
