-- Add C2 level test questions for all 4 types

-- =============================================
-- VOCABULARY C2 (12 questions)
-- =============================================
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'c2', 'What does ''obfuscate'' mean?', '["clarificar","ofuscar","facilitar","simplificar"]', 'ofuscar'),
('vocabulary', 'c2', 'What does ''perfunctory'' mean?', '["perfecto","superficial","profundo","permanente"]', 'superficial'),
('vocabulary', 'c2', 'What does ''sycophant'' mean?', '["adulador","filántropo","pesimista","líder"]', 'adulador'),
('vocabulary', 'c2', 'What does ''ubiquitous'' mean?', '["único","omnipresente","raro","invisible"]', 'omnipresente'),
('vocabulary', 'c2', 'What does ''ephemeral'' mean?', '["eterno","efímero","esencial","enorme"]', 'efímero'),
('vocabulary', 'c2', 'What does ''pernicious'' mean?', '["beneficioso","pernicioso","persistente","permisivo"]', 'pernicioso'),
('vocabulary', 'c2', 'What does ''equivocate'' mean?', '["igualar","ser ambiguo","equivocar","equilibrar"]', 'ser ambiguo'),
('vocabulary', 'c2', 'What does ''recalcitrant'' mean?', '["obediente","recalcitrante","calculador","tranquilo"]', 'recalcitrante'),
('vocabulary', 'c2', 'What does ''vicissitude'' mean?', '["virtud","vicisitud","victoria","violencia"]', 'vicisitud'),
('vocabulary', 'c2', 'What does ''laconic'' mean?', '["verboso","lacónico","lento","largo"]', 'lacónico'),
('vocabulary', 'c2', 'What does ''ineluctable'' mean?', '["evitable","ineludible","ineficaz","inelegante"]', 'ineludible'),
('vocabulary', 'c2', 'What does ''perspicacious'' mean?', '["confuso","perspicaz","persistente","persuasivo"]', 'perspicaz');

-- =============================================
-- GRAMMAR C2 (10 questions)
-- =============================================
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'c2', 'Which sentence is more formal? A) "We need to look into this." B) "This matter warrants investigation."', '["A","B","Both equal","Neither"]', 'B'),
('grammar', 'c2', 'Had it not been for her intervention, the project ___ failed.', '["would have","will have","has","had"]', 'would have'),
('grammar', 'c2', 'It is imperative that he ___ present at the hearing.', '["is","be","will be","was"]', 'be'),
('grammar', 'c2', 'Not only ___ the report late, but it also contained errors.', '["was","is","were","had been"]', 'was'),
('grammar', 'c2', 'The CEO, along with the board members, ___ the decision.', '["support","supports","are supporting","have supported"]', 'supports'),
('grammar', 'c2', 'She speaks as though she ___ the entire situation firsthand.', '["witnesses","witnessed","had witnessed","has witnessed"]', 'had witnessed'),
('grammar', 'c2', 'Seldom ___ such a compelling argument been presented.', '["has","have","had","is"]', 'has'),
('grammar', 'c2', 'The research, ___ implications are far-reaching, was published last month.', '["which","whose","that","whom"]', 'whose'),
('grammar', 'c2', 'Were she ___ the truth, things would be different now.', '["to know","knowing","known","know"]', 'to know'),
('grammar', 'c2', 'Little ___ they realise what consequences their actions would have.', '["do","did","have","are"]', 'did');

-- =============================================
-- LISTENING C2 (5 questions)
-- =============================================
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'c2', 'In fast speech, "I should have gone" often sounds like...', '["I shoulda gone","I should gone","I shove gone","I showed gone"]', 'I shoulda gone', 1.20),
('listening', 'c2', 'What does "He''s not the sharpest tool in the shed" imply?', '["He is dangerous","He is not very intelligent","He needs new tools","He works in a shed"]', 'He is not very intelligent', 1.00),
('listening', 'c2', 'In connected speech, "want to" is often reduced to...', '["wanna","wanto","want-a","won''t"]', 'wanna', 1.20),
('listening', 'c2', 'The phrase "a damp squib" means...', '["something wet","a failed event","a small animal","a loud noise"]', 'a failed event', 1.00),
('listening', 'c2', 'When someone says "I couldn''t care less" vs "I could care less", which is logically correct?', '["I couldn''t care less","I could care less","Both are correct","Neither"]', 'I couldn''t care less', 1.00);

-- =============================================
-- PRONUNCIATION C2 (5 questions)
-- =============================================
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'c2', 'Where is the primary stress in ''characteristically''?', '["CHA-rac-ter-is-ti-cal-ly","cha-RAC-ter-is-ti-cal-ly","cha-rac-ter-IS-ti-cal-ly","cha-rac-ter-is-ti-CAL-ly"]', 'cha-rac-ter-IS-ti-cal-ly'),
('pronunciation', 'c2', 'In the compound noun ''greenhouse'' vs the phrase ''green house'', stress falls on...', '["GREEN-house vs green HOUSE","green-HOUSE vs GREEN house","Same stress pattern","No difference"]', 'GREEN-house vs green HOUSE'),
('pronunciation', 'c2', 'The word ''record'' changes stress depending on...', '["whether it''s a noun or verb","the speaker''s accent","the sentence length","the following word"]', 'whether it''s a noun or verb'),
('pronunciation', 'c2', 'Rising intonation at the end of a statement usually indicates...', '["certainty","disbelief or seeking confirmation","anger","sadness"]', 'disbelief or seeking confirmation'),
('pronunciation', 'c2', 'Which word has the primary stress on the third syllable?', '["development","understanding","photography","determination"]', 'determination');
