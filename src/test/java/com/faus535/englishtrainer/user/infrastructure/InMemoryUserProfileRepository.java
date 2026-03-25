package com.faus535.englishtrainer.user.infrastructure;

import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class InMemoryUserProfileRepository implements UserProfileRepository {

    private final Map<UserProfileId, UserProfile> store = new HashMap<>();

    @Override
    public Optional<UserProfile> findById(UserProfileId id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public Optional<UserProfile> findByIdForUpdate(UserProfileId id) {
        return findById(id);
    }

    @Override
    public UserProfile save(UserProfile profile) {
        store.put(profile.id(), profile);
        return profile;
    }

    @Override
    public void deleteById(UserProfileId id) {
        store.remove(id);
    }

    public int count() {
        return store.size();
    }

    public void clear() {
        store.clear();
    }

    public boolean contains(UserProfileId id) {
        return store.containsKey(id);
    }
}
