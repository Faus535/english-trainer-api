package com.faus535.englishtrainer.session.infrastructure;

import com.faus535.englishtrainer.IntegrationTestBase;
import com.faus535.englishtrainer.session.domain.Session;
import com.faus535.englishtrainer.session.domain.SessionMother;
import com.faus535.englishtrainer.session.domain.SessionRepository;
import com.faus535.englishtrainer.user.domain.UserProfile;
import com.faus535.englishtrainer.user.domain.UserProfileMother;
import com.faus535.englishtrainer.user.domain.UserProfileRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

class SessionRepositoryIT extends IntegrationTestBase {

    @Autowired
    private SessionRepository sessionRepository;

    @Autowired
    private UserProfileRepository userProfileRepository;

    @Test
    void save_and_find_by_id_round_trip() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Session session = SessionMother.create(userProfile.id());

        sessionRepository.save(session);

        Optional<Session> found = sessionRepository.findById(session.id());

        assertThat(found).isPresent();
        assertThat(found.get().id()).isEqualTo(session.id());
        assertThat(found.get().userId()).isEqualTo(session.userId());
        assertThat(found.get().mode()).isEqualTo(session.mode());
        assertThat(found.get().sessionType()).isEqualTo(session.sessionType());
        assertThat(found.get().listeningModule()).isEqualTo(session.listeningModule());
        assertThat(found.get().secondaryModule()).isEqualTo(session.secondaryModule());
        assertThat(found.get().integratorTheme()).isEqualTo(session.integratorTheme());
        assertThat(found.get().completed()).isEqualTo(session.completed());
        assertThat(found.get().blocks()).hasSize(session.blocks().size());
    }

    @Test
    void find_active_by_user_returns_uncompleted_session() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Session session = SessionMother.create(userProfile.id());
        sessionRepository.save(session);

        Optional<Session> found = sessionRepository.findActiveByUser(userProfile.id());

        assertThat(found).isPresent();
        assertThat(found.get().completed()).isFalse();
    }

    @Test
    void find_by_user_returns_all_user_sessions() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Session session1 = SessionMother.create(userProfile.id());
        Session session2 = SessionMother.create(userProfile.id());
        sessionRepository.save(session1);
        sessionRepository.save(session2);

        List<Session> found = sessionRepository.findByUser(userProfile.id());

        assertThat(found).hasSize(2);
    }

    @Test
    void save_completed_session_maps_correctly() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Session session = SessionMother.create(userProfile.id());
        sessionRepository.save(session);

        Session completed = session.complete(25);
        sessionRepository.save(completed);

        Optional<Session> found = sessionRepository.findById(session.id());

        assertThat(found).isPresent();
        assertThat(found.get().completed()).isTrue();
        assertThat(found.get().durationMinutes()).isEqualTo(25);
        assertThat(found.get().completedAt()).isNotNull();
    }

    @Test
    void find_active_by_user_returns_empty_when_all_completed() {
        UserProfile userProfile = UserProfileMother.create();
        userProfileRepository.save(userProfile);

        Session session = SessionMother.create(userProfile.id());
        sessionRepository.save(session);

        Session completed = session.complete(15);
        sessionRepository.save(completed);

        Optional<Session> found = sessionRepository.findActiveByUser(userProfile.id());

        assertThat(found).isEmpty();
    }
}
