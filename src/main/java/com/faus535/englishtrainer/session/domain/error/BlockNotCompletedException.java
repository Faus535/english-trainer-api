package com.faus535.englishtrainer.session.domain.error;

public class BlockNotCompletedException extends SessionException {

    private final int blockIndex;

    public BlockNotCompletedException(int blockIndex) {
        super("Complete all exercises in block " + blockIndex + " before advancing");
        this.blockIndex = blockIndex;
    }

    public int blockIndex() {
        return blockIndex;
    }
}
