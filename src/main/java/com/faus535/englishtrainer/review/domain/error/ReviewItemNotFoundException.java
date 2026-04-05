package com.faus535.englishtrainer.review.domain.error;

import com.faus535.englishtrainer.review.domain.ReviewItemId;

public final class ReviewItemNotFoundException extends ReviewException {

    public ReviewItemNotFoundException(ReviewItemId id) {
        super("Review item not found: " + id.value());
    }
}
