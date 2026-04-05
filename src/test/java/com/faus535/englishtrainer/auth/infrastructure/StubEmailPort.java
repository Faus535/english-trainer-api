package com.faus535.englishtrainer.auth.infrastructure;

import com.faus535.englishtrainer.auth.domain.EmailPort;

import java.util.ArrayList;
import java.util.List;

public final class StubEmailPort implements EmailPort {

    private final List<EmailCall> calls = new ArrayList<>();

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        calls.add(new EmailCall(toEmail, resetToken));
    }

    public List<EmailCall> getCalls() {
        return List.copyOf(calls);
    }

    public record EmailCall(String toEmail, String resetToken) {}
}
