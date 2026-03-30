-- Seed 44 English phonemes
-- Short vowels
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɪ', 'near-close near-front unrounded', 'vowel', 'short_vowel', 1, '["sit","fish","bit"]', 'A short relaxed vowel similar to the "i" in "sit".', 'Lips slightly spread, tongue high and forward but relaxed.', 'Keep it short and relaxed, not like the long "ee" sound.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'e', 'open-mid front unrounded', 'vowel', 'short_vowel', 2, '["bed","red","ten"]', 'A short mid-front vowel as in "bed".', 'Lips slightly spread, mouth half open, tongue mid-height.', 'Keep the mouth more open than for /ɪ/ but less than for /æ/.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'æ', 'near-open front unrounded', 'vowel', 'short_vowel', 3, '["cat","hat","man"]', 'A short open front vowel as in "cat".', 'Mouth wide open, lips spread, tongue low and forward.', 'Open your mouth wider than for /e/ and push the tongue forward.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɒ', 'open back rounded', 'vowel', 'short_vowel', 4, '["hot","dog","lot"]', 'A short open back rounded vowel as in "hot".', 'Lips slightly rounded, mouth open, tongue low and back.', 'Round your lips slightly and keep the sound short.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ʌ', 'open-mid back unrounded', 'vowel', 'short_vowel', 5, '["cup","bus","run"]', 'A short central vowel as in "cup".', 'Lips relaxed and neutral, mouth slightly open, tongue in center.', 'Relax your mouth completely and make a short neutral sound.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ʊ', 'near-close near-back rounded', 'vowel', 'short_vowel', 6, '["put","book","look"]', 'A short rounded vowel as in "put".', 'Lips loosely rounded, tongue high and pulled back.', 'Keep the lips gently rounded and the sound short, not like "oo".');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ə', 'mid central schwa', 'vowel', 'short_vowel', 7, '["about","banana","sofa"]', 'The most common English vowel, a weak neutral sound.', 'Mouth barely open, lips and tongue completely relaxed in center.', 'This is the laziest vowel; just relax everything and make a quick sound.');

-- Long vowels
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'iː', 'close front unrounded long', 'vowel', 'long_vowel', 8, '["see","tree","free"]', 'A long high front vowel as in "see".', 'Lips spread wide, tongue high and forward, jaw nearly closed.', 'Smile while making this sound and hold it longer than /ɪ/.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɑː', 'open back unrounded long', 'vowel', 'long_vowel', 9, '["car","far","star"]', 'A long open back vowel as in "car".', 'Mouth wide open, tongue low and back, lips unrounded.', 'Open your mouth wide like at the doctor and hold the sound.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɔː', 'open-mid back rounded long', 'vowel', 'long_vowel', 10, '["saw","door","more"]', 'A long rounded back vowel as in "saw".', 'Lips rounded into an oval, tongue mid-low and back.', 'Round your lips firmly and keep the sound long and steady.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'uː', 'close back rounded long', 'vowel', 'long_vowel', 11, '["too","moon","food"]', 'A long high back rounded vowel as in "moon".', 'Lips tightly rounded, tongue high and pulled back.', 'Push your lips forward as if blowing a candle and hold the sound.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɜː', 'open-mid central unrounded long', 'vowel', 'long_vowel', 12, '["bird","word","nurse"]', 'A long central vowel as in "bird".', 'Lips neutral and slightly spread, tongue mid-height in center.', 'Hold a relaxed neutral position and sustain the sound longer.');

-- Diphthongs
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'eɪ', 'closing diphthong', 'vowel', 'diphthong', 13, '["say","day","make"]', 'A gliding vowel that moves from /e/ toward /ɪ/.', 'Mouth starts half open then closes as tongue rises.', 'Start with an open mouth and glide smoothly upward.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'aɪ', 'closing diphthong', 'vowel', 'diphthong', 14, '["my","time","fly"]', 'A gliding vowel that moves from open /a/ toward /ɪ/.', 'Mouth starts wide open then closes as tongue rises forward.', 'Open wide and glide to a smile position.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɔɪ', 'closing diphthong', 'vowel', 'diphthong', 15, '["boy","toy","join"]', 'A gliding vowel that moves from /ɔ/ toward /ɪ/.', 'Lips start rounded then spread as tongue moves forward.', 'Start with rounded lips and end with a smile.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'əʊ', 'closing diphthong', 'vowel', 'diphthong', 16, '["go","home","know"]', 'A gliding vowel that moves from /ə/ toward /ʊ/.', 'Lips start neutral then round as tongue moves back and up.', 'Start relaxed and glide into a rounded lip position.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'aʊ', 'closing diphthong', 'vowel', 'diphthong', 17, '["now","house","out"]', 'A gliding vowel that moves from open /a/ toward /ʊ/.', 'Mouth starts wide open then closes with lips rounding.', 'Open wide and glide into a small rounded mouth shape.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ɪə', 'centering diphthong', 'vowel', 'diphthong', 18, '["near","here","beer"]', 'A gliding vowel that moves from /ɪ/ toward /ə/.', 'Tongue starts high and forward then relaxes to center.', 'Start with a short /ɪ/ and relax into a schwa.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'eə', 'centering diphthong', 'vowel', 'diphthong', 19, '["hair","care","where"]', 'A gliding vowel that moves from /e/ toward /ə/.', 'Mouth starts half open then relaxes to neutral.', 'Start with /e/ and let your mouth relax naturally.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ʊə', 'centering diphthong', 'vowel', 'diphthong', 20, '["pure","tour","sure"]', 'A gliding vowel that moves from /ʊ/ toward /ə/.', 'Lips start rounded then relax to neutral position.', 'Start with rounded lips and relax them as the sound fades.');

-- Plosives
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'p', 'voiceless bilabial plosive', 'consonant', 'plosive', 21, '["pen","top","stop"]', 'A voiceless sound made by releasing air from closed lips.', 'Both lips pressed together then released with a burst of air.', 'Press your lips tightly and release with a puff of air.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'b', 'voiced bilabial plosive', 'consonant', 'plosive', 22, '["big","baby","job"]', 'A voiced sound made by releasing air from closed lips.', 'Both lips pressed together then released while voicing.', 'Like /p/ but vibrate your vocal cords at the same time.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 't', 'voiceless alveolar plosive', 'consonant', 'plosive', 23, '["ten","top","cat"]', 'A voiceless sound made by releasing the tongue from the alveolar ridge.', 'Tongue tip touches the ridge behind upper teeth then releases.', 'Tap your tongue firmly behind your upper teeth and release.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'd', 'voiced alveolar plosive', 'consonant', 'plosive', 24, '["dog","did","bad"]', 'A voiced sound made by releasing the tongue from the alveolar ridge.', 'Tongue tip touches the ridge behind upper teeth then releases with voice.', 'Like /t/ but add voice; feel the vibration in your throat.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'k', 'voiceless velar plosive', 'consonant', 'plosive', 25, '["cat","key","back"]', 'A voiceless sound made by releasing the tongue from the soft palate.', 'Back of tongue presses against soft palate then releases.', 'Press the back of your tongue up and release with a burst.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'g', 'voiced velar plosive', 'consonant', 'plosive', 26, '["get","big","dog"]', 'A voiced sound made by releasing the tongue from the soft palate.', 'Back of tongue presses against soft palate then releases with voice.', 'Like /k/ but vibrate your vocal cords as you release.');

-- Fricatives
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'f', 'voiceless labiodental fricative', 'consonant', 'fricative', 27, '["fish","off","life"]', 'A voiceless hissing sound made with teeth on lip.', 'Upper teeth rest on lower lip while air flows through.', 'Gently bite your lower lip and blow air through.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'v', 'voiced labiodental fricative', 'consonant', 'fricative', 28, '["van","five","give"]', 'A voiced buzzing sound made with teeth on lip.', 'Upper teeth rest on lower lip while voicing and air flow.', 'Like /f/ but add voice; you should feel buzzing on your lip.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'θ', 'voiceless dental fricative', 'consonant', 'fricative', 29, '["think","three","bath"]', 'A voiceless sound made by blowing air between tongue and teeth.', 'Tongue tip between or just behind upper teeth, air flows over it.', 'Place your tongue lightly between your teeth and blow.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ð', 'voiced dental fricative', 'consonant', 'fricative', 30, '["this","that","mother"]', 'A voiced sound made by vibrating air between tongue and teeth.', 'Tongue tip between or just behind upper teeth with voicing.', 'Like /θ/ but add voice; feel the vibration on your tongue.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 's', 'voiceless alveolar fricative', 'consonant', 'fricative', 31, '["see","sit","miss"]', 'A voiceless hissing sound as in "see".', 'Tongue close to alveolar ridge, air hisses through narrow gap.', 'Keep your tongue near the roof and hiss like a snake.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'z', 'voiced alveolar fricative', 'consonant', 'fricative', 32, '["zoo","buzz","his"]', 'A voiced buzzing sound as in "zoo".', 'Tongue close to alveolar ridge with voicing, air buzzes through.', 'Like /s/ but add voice; it should buzz like a bee.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ʃ', 'voiceless postalveolar fricative', 'consonant', 'fricative', 33, '["she","ship","fish"]', 'A voiceless hushing sound as in "she".', 'Tongue broad and raised behind alveolar ridge, lips slightly rounded.', 'Round your lips slightly and make a "shh" sound.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ʒ', 'voiced postalveolar fricative', 'consonant', 'fricative', 34, '["vision","measure","pleasure"]', 'A voiced buzzing version of the "sh" sound.', 'Tongue broad and raised behind alveolar ridge with voicing.', 'Like /ʃ/ but add voice; think of the "s" in "measure".');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'h', 'voiceless glottal fricative', 'consonant', 'fricative', 35, '["hat","hot","who"]', 'A breathy sound made by pushing air from the throat.', 'Mouth open in position of the following vowel, air from throat.', 'Just breathe out with your mouth open, like fogging a mirror.');

-- Affricates
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'tʃ', 'voiceless postalveolar affricate', 'consonant', 'affricate', 36, '["church","watch","match"]', 'A voiceless sound combining /t/ and /ʃ/ as in "church".', 'Tongue starts at alveolar ridge then releases into a "sh" position.', 'Start with a /t/ and immediately slide into /ʃ/.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'dʒ', 'voiced postalveolar affricate', 'consonant', 'affricate', 37, '["judge","age","bridge"]', 'A voiced sound combining /d/ and /ʒ/ as in "judge".', 'Tongue starts at alveolar ridge then releases with voicing.', 'Like /tʃ/ but add voice; think of the "j" in "jump".');

-- Nasals
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'm', 'bilabial nasal', 'consonant', 'nasal', 38, '["man","some","swim"]', 'A voiced nasal sound made with closed lips.', 'Lips closed, air flows through the nose.', 'Close your lips and hum; the sound comes through your nose.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'n', 'alveolar nasal', 'consonant', 'nasal', 39, '["no","ten","sun"]', 'A voiced nasal sound made with tongue on the alveolar ridge.', 'Tongue tip on alveolar ridge, air flows through the nose.', 'Press your tongue behind your upper teeth and hum through your nose.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'ŋ', 'velar nasal', 'consonant', 'nasal', 40, '["sing","ring","thing"]', 'A voiced nasal sound made at the back of the mouth.', 'Back of tongue against soft palate, air flows through nose.', 'Press the back of your tongue up and hum through your nose.');

-- Approximants
INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'l', 'alveolar lateral approximant', 'consonant', 'approximant', 41, '["let","ball","all"]', 'A voiced sound where air flows around the sides of the tongue.', 'Tongue tip on alveolar ridge, air flows around the sides.', 'Touch your tongue tip to the ridge and let air pass around it.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'r', 'alveolar approximant', 'consonant', 'approximant', 42, '["red","run","write"]', 'A voiced sound with the tongue curled back slightly.', 'Tongue tip curled back slightly, not touching the roof.', 'Curl your tongue back without touching anything and voice it.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'w', 'labial-velar approximant', 'consonant', 'approximant', 43, '["we","wet","win"]', 'A voiced sound made with rounded lips and raised back tongue.', 'Lips tightly rounded, back of tongue raised toward soft palate.', 'Start with tightly rounded lips and quickly open into the next vowel.');

INSERT INTO phonemes (id, symbol, name, category, subcategory, difficulty_order, example_words, description, mouth_position, tips)
VALUES (gen_random_uuid(), 'j', 'palatal approximant', 'consonant', 'approximant', 44, '["yes","you","yet"]', 'A voiced sound with the tongue raised toward the hard palate.', 'Tongue raised high and forward toward hard palate, lips spread.', 'Raise your tongue close to the roof of your mouth and voice it.');
