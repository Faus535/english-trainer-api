package com.faus535.englishtrainer.learningpath.domain;

import com.faus535.englishtrainer.learningpath.domain.event.UnitMasteredEvent;
import com.faus535.englishtrainer.user.domain.UserProfileId;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

final class LearningUnitTest {

    @Test
    void shouldCreateLearningUnit() {
        LearningPathId pathId = LearningPathId.generate();

        LearningUnit unit = LearningUnitMother.create(pathId);

        assertNotNull(unit.id());
        assertEquals(pathId, unit.learningPathId());
        assertEquals(0, unit.unitIndex());
        assertEquals("A1", unit.targetLevel());
        assertEquals(UnitStatus.NOT_STARTED, unit.status());
        assertEquals(0, unit.masteryScore().value());
        assertEquals(3, unit.contents().size());
        assertNull(unit.completedAt());
    }

    @Test
    void shouldStartUnit() {
        LearningUnit unit = LearningUnitMother.create();

        LearningUnit started = unit.startUnit();

        assertEquals(UnitStatus.IN_PROGRESS, started.status());
        assertEquals(unit.id(), started.id());
    }

    @Test
    void shouldUpdateMasteryToInProgress() {
        UserProfileId userId = UserProfileId.generate();
        LearningUnit unit = LearningUnitMother.create().startUnit();

        LearningUnit updated = unit.updateMastery(new MasteryScore(55), userId);

        assertEquals(UnitStatus.IN_PROGRESS, updated.status());
        assertEquals(55, updated.masteryScore().value());
        assertNull(updated.completedAt());
    }

    @Test
    void shouldUpdateMasteryToNeedsReview() {
        UserProfileId userId = UserProfileId.generate();
        LearningUnit unit = LearningUnitMother.create().startUnit();

        LearningUnit updated = unit.updateMastery(new MasteryScore(30), userId);

        assertEquals(UnitStatus.NEEDS_REVIEW, updated.status());
    }

    @Test
    void shouldUpdateMasteryToMasteredAndRegisterEvent() {
        UserProfileId userId = UserProfileId.generate();
        LearningUnit unit = LearningUnitMother.create().startUnit();

        LearningUnit mastered = unit.updateMastery(new MasteryScore(85), userId);

        assertEquals(UnitStatus.MASTERED, mastered.status());
        assertNotNull(mastered.completedAt());

        var events = mastered.pullDomainEvents();
        assertEquals(1, events.size());
        assertInstanceOf(UnitMasteredEvent.class, events.get(0));

        UnitMasteredEvent event = (UnitMasteredEvent) events.get(0);
        assertEquals(unit.id(), event.unitId());
        assertEquals(unit.learningPathId(), event.pathId());
        assertEquals(userId, event.userId());
    }

    @Test
    void shouldNotRegisterEventWhenAlreadyMastered() {
        UserProfileId userId = UserProfileId.generate();
        LearningUnit unit = LearningUnitMother.create().startUnit();

        LearningUnit mastered = unit.updateMastery(new MasteryScore(85), userId);
        mastered.pullDomainEvents(); // clear events

        LearningUnit stillMastered = mastered.updateMastery(new MasteryScore(90), userId);

        var events = stillMastered.pullDomainEvents();
        assertEquals(0, events.size());
    }

    @Test
    void shouldMarkContentPracticed() {
        UUID contentId = UUID.randomUUID();
        LearningPathId pathId = LearningPathId.generate();
        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, contentId, false, null),
                new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null)
        );
        LearningUnit unit = LearningUnitMother.withContents(pathId, contents);

        LearningUnit updated = unit.markContentPracticed(contentId);

        UnitContent practiced = updated.contents().stream()
                .filter(c -> c.contentId().equals(contentId))
                .findFirst()
                .orElseThrow();
        assertTrue(practiced.practiced());
        assertNotNull(practiced.lastPracticedAt());
    }

    @Test
    void shouldReturnUnpracticedContents() {
        UUID practicedId = UUID.randomUUID();
        UUID unpracticedId = UUID.randomUUID();
        LearningPathId pathId = LearningPathId.generate();
        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, practicedId, true, null),
                new UnitContent(ContentType.PHRASE, unpracticedId, false, null)
        );
        LearningUnit unit = LearningUnitMother.withContents(pathId, contents);

        List<UnitContent> unpracticed = unit.unpracticedContents();

        assertEquals(1, unpracticed.size());
        assertEquals(unpracticedId, unpracticed.get(0).contentId());
    }

    @Test
    void shouldReturnUnpracticedContentsByType() {
        LearningPathId pathId = LearningPathId.generate();
        List<UnitContent> contents = List.of(
                new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null),
                new UnitContent(ContentType.PHRASE, UUID.randomUUID(), false, null),
                new UnitContent(ContentType.VOCAB, UUID.randomUUID(), true, null)
        );
        LearningUnit unit = LearningUnitMother.withContents(pathId, contents);

        List<UnitContent> unpracticedVocab = unit.unpracticedContentsByType(ContentType.VOCAB);

        assertEquals(1, unpracticedVocab.size());
        assertEquals(ContentType.VOCAB, unpracticedVocab.get(0).contentType());
    }

    @Test
    void shouldHaveImmutableContents() {
        LearningUnit unit = LearningUnitMother.create();

        assertThrows(UnsupportedOperationException.class, () ->
                unit.contents().add(new UnitContent(ContentType.VOCAB, UUID.randomUUID(), false, null)));
    }
}
