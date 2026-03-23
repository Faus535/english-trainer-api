-- Seed minimal pairs for Spanish speakers
-- VOWEL_LENGTH (a1): /ɪ/ vs /iː/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'ship', 'sheep', '/ʃɪp/', '/ʃiːp/', 'VOWEL_LENGTH', 'a1'),
    (gen_random_uuid(), 'bit', 'beat', '/bɪt/', '/biːt/', 'VOWEL_LENGTH', 'a1'),
    (gen_random_uuid(), 'sit', 'seat', '/sɪt/', '/siːt/', 'VOWEL_LENGTH', 'a1'),
    (gen_random_uuid(), 'fill', 'feel', '/fɪl/', '/fiːl/', 'VOWEL_LENGTH', 'a1'),
    (gen_random_uuid(), 'hit', 'heat', '/hɪt/', '/hiːt/', 'VOWEL_LENGTH', 'a1');

-- V_B (a1): /v/ vs /b/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'van', 'ban', '/væn/', '/bæn/', 'V_B', 'a1'),
    (gen_random_uuid(), 'vest', 'best', '/vest/', '/best/', 'V_B', 'a1'),
    (gen_random_uuid(), 'vine', 'bine', '/vaɪn/', '/baɪn/', 'V_B', 'a1'),
    (gen_random_uuid(), 'vet', 'bet', '/vet/', '/bet/', 'V_B', 'a1');

-- TH_T (a2): /θ/ vs /t/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'think', 'tink', '/θɪŋk/', '/tɪŋk/', 'TH_T', 'a2'),
    (gen_random_uuid(), 'three', 'tree', '/θriː/', '/triː/', 'TH_T', 'a2'),
    (gen_random_uuid(), 'thin', 'tin', '/θɪn/', '/tɪn/', 'TH_T', 'a2'),
    (gen_random_uuid(), 'bath', 'bat', '/bɑːθ/', '/bæt/', 'TH_T', 'a2');

-- TH_D (a2): /ð/ vs /d/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'then', 'den', '/ðen/', '/den/', 'TH_D', 'a2'),
    (gen_random_uuid(), 'they', 'day', '/ðeɪ/', '/deɪ/', 'TH_D', 'a2'),
    (gen_random_uuid(), 'breathe', 'breed', '/briːð/', '/briːd/', 'TH_D', 'a2');

-- AE_UH (b1): /æ/ vs /ʌ/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'cat', 'cut', '/kæt/', '/kʌt/', 'AE_UH', 'b1'),
    (gen_random_uuid(), 'bat', 'but', '/bæt/', '/bʌt/', 'AE_UH', 'b1'),
    (gen_random_uuid(), 'cap', 'cup', '/kæp/', '/kʌp/', 'AE_UH', 'b1'),
    (gen_random_uuid(), 'ran', 'run', '/ræn/', '/rʌn/', 'AE_UH', 'b1');

-- SH_CH (b1): /ʃ/ vs /tʃ/
INSERT INTO minimal_pairs (id, word1, word2, ipa1, ipa2, sound_category, level) VALUES
    (gen_random_uuid(), 'ship', 'chip', '/ʃɪp/', '/tʃɪp/', 'SH_CH', 'b1'),
    (gen_random_uuid(), 'share', 'chair', '/ʃeər/', '/tʃeər/', 'SH_CH', 'b1'),
    (gen_random_uuid(), 'shoe', 'chew', '/ʃuː/', '/tʃuː/', 'SH_CH', 'b1');
