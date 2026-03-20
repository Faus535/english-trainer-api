package com.faus535.englishtrainer.user.domain.error;

public final class InvalidXpAmountException extends Exception {

    public InvalidXpAmountException(int amount) {
        super("XP amount cannot be negative: " + amount);
    }
}
