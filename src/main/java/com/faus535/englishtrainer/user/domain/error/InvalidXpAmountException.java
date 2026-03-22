package com.faus535.englishtrainer.user.domain.error;

public final class InvalidXpAmountException extends UserProfileException {

    public InvalidXpAmountException(int amount) {
        super("XP amount cannot be negative: " + amount);
    }
}
