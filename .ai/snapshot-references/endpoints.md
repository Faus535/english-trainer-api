# Endpoints Snapshot

Total: 9 public + 129 authenticated = 138 endpoints

## Public Endpoints (no auth required)

| Method | Path | Controller | Module |
|--------|------|------------|--------|
| POST | /api/auth/login | LoginController | auth |
| POST | /api/auth/register | RegisterController | auth |
| POST | /api/auth/google | GoogleLoginController | auth |
| POST | /api/auth/refresh | RefreshTokenController | auth |
| POST | /api/auth/forgot-password | ForgotPasswordController | auth |
| POST | /api/auth/reset-password | ResetPasswordController | auth |
| POST | /api/auth/logout | LogoutController | auth |
| GET | /api/assessments/mini-test | GetMiniTestQuestionsController | assessment |
| GET | /api/achievements | GetAllAchievementsController | gamification |

## Authenticated Endpoints by Module

### activity (3)
| GET | /api/profiles/{userId}/activity | GET | /api/profiles/{userId}/streak | POST | /api/profiles/{userId}/activity |

### admin (4)
| GET,POST | /api/admin/vocab | GET | /api/admin/phrases | POST | /api/admin/reading/passages | POST | /api/admin/writing/exercises |

### analytics (3)
| GET | /api/profiles/{userId}/analytics/activity-heatmap | GET | /api/profiles/{userId}/analytics/summary | GET | /api/profiles/{userId}/analytics/progress |

### assessment (4)
| GET | /api/profiles/{userId}/assessments/history | POST | /api/profiles/{userId}/assessments/mini-test | POST | /api/profiles/{userId}/assessments/level-test | GET | /api/profiles/{userId}/assessments/level-test/questions |

### auth (2 authenticated)
| GET | /api/auth/me | PUT | /api/auth/change-password |

### conversation (9)
| GET | /api/conversations/{id} | GET | /api/conversations | POST | /api/conversations/{id}/messages | GET | /api/conversations/suggested-topics | GET | /api/conversations/suggested-goals | GET | /api/conversations/stats | POST | /api/conversations/{id}/messages/stream | POST | /api/conversations | PUT | /api/conversations/{id}/end |

### curriculum (4)
| GET | /api/curriculum/plan | GET | /api/curriculum/modules | GET | /api/curriculum/modules/{name} | GET | /api/curriculum/integrators |

### dailychallenge (3)
| GET | /api/challenges/today | PUT | /api/profiles/{userId}/challenges/today/progress | GET | /api/profiles/{userId}/challenges/today |

### errorpattern (1)
| GET | /api/profiles/{userId}/error-patterns |

### exercise (1)
| GET | /api/conversations/{id}/exercises |

### gamification (4 authenticated)
| POST | /api/profiles/{userId}/xp | POST | /api/profiles/{userId}/achievements/check | GET | /api/profiles/{userId}/achievements | GET | /api/profiles/{userId}/xp-level |

### learningpath (3)
| POST | /api/profiles/{profileId}/learning-path/generate | GET | /api/profiles/{profileId}/learning-path | GET | /api/profiles/{profileId}/learning-status |

### minigame (6)
| GET | /api/minigames/word-match | GET | /api/minigames/fill-gap | GET | /api/minigames/unscramble | POST | /api/profiles/{userId}/minigames/scores | POST | /api/profiles/{userId}/minigames/results | GET | /api/profiles/{userId}/minigames/scores |

### minimalpair (3)
| GET | /api/pronunciation/minimal-pairs | GET | /api/profiles/{userId}/pronunciation/minimal-pairs/stats | POST | /api/profiles/{userId}/pronunciation/minimal-pairs/results |

### moduleprogress (5)
| GET | /api/profiles/{userId}/modules/{module}/levels/{level}/level-up | PUT | /api/profiles/{userId}/modules/{module}/levels/{level}/units/{unit} | GET | /api/profiles/{userId}/modules | GET | /api/profiles/{userId}/modules/{module}/levels/{level} | POST | /api/profiles/{userId}/modules/{module}/levels/{level} |

### notification (3)
| POST | /api/notifications/subscribe | GET | /api/notifications/preferences | PUT | /api/notifications/preferences |

### phrase (2)
| GET | /api/phrases | GET | /api/phrases/random |

### pronunciation (3)
| GET | /api/profiles/{userId}/pronunciation/errors | POST | /api/profiles/{userId}/pronunciation/errors | GET | /api/profiles/{userId}/pronunciation/problematic-sounds |

### reading (5)
| GET | /api/reading/passages | GET | /api/reading/passages/{id} | GET | /api/reading/passages/{id}/questions | POST | /api/reading/passages/{textId}/answers | POST | /api/profiles/{userId}/reading/submit |

### session (7)
| GET | /api/profiles/{userId}/sessions | PUT | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/advance | POST | /api/profiles/{userId}/sessions/generate | GET | /api/profiles/{profileId}/sessions/{sessionId}/blocks/{blockIndex}/exercises | PUT | /api/profiles/{userId}/sessions/{sessionId}/complete | GET | /api/profiles/{userId}/sessions/current | POST | /api/profiles/{profileId}/sessions/{sessionId}/exercises/{exerciseIndex}/result |

### spacedrepetition (4)
| POST | /api/profiles/{userId}/reviews | GET | /api/profiles/{userId}/reviews/stats | PUT | /api/profiles/{userId}/reviews/{itemId}/complete | GET | /api/profiles/{userId}/reviews/due |

### tutorerror (4)
| POST | /api/profiles/{userId}/tutor/errors/{errorId}/exercise | POST | /api/profiles/{userId}/tutor/errors | GET | /api/profiles/{userId}/tutor/errors/trend | GET | /api/profiles/{userId}/tutor/errors |

### user (9)
| POST | /api/profiles | PUT | /api/profiles/{id}/test-completed | DELETE | /api/profiles/{id} | PUT | /api/profiles/{id}/reset-test | PUT | /api/profiles/{id}/levels | PUT | /api/profiles/{id}/modules/{module}/level | GET | /api/profiles/{id} | POST | /api/profiles/{id}/sessions | POST | /api/profiles/{id}/xp |

### vocabulary (7)
| GET | /api/vocab | GET | /api/vocab/search | POST | /api/vocab | GET | /api/vocab/random | GET | /api/vocab/level/{level} | GET | /api/profiles/{userId}/vocabulary/unlearned | GET | /api/profiles/{userId}/vocabulary/progress |

### vocabularycontext (1)
| POST | /api/vocabulary/{wordId}/context |

### writing (4)
| GET | /api/writing/exercises | POST | /api/writing/submissions | GET | /api/profiles/{userId}/writing/history | POST | /api/profiles/{userId}/writing/submit |
