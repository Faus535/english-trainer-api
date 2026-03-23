-- Phase 1: Dynamic question bank for level test
-- Phase 2: Listening complexity metadata

CREATE TABLE IF NOT EXISTS test_questions (
    id          UUID PRIMARY KEY DEFAULT gen_random_uuid(),
    type        VARCHAR(20) NOT NULL,
    level       VARCHAR(5)  NOT NULL,
    question    TEXT        NOT NULL,
    options     JSONB       NOT NULL,
    correct_answer TEXT     NOT NULL,
    audio_speed NUMERIC(3,2),
    active      BOOLEAN     NOT NULL DEFAULT TRUE,
    created_at  TIMESTAMP   NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_test_questions_type_level ON test_questions(type, level, active);

-- =============================================
-- VOCABULARY questions (45 total: 9 per level)
-- =============================================

-- Vocabulary A1 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'a1', 'What does ''hello'' mean?', '["hola","adiós","gracias","por favor"]', 'hola'),
('vocabulary', 'a1', 'What does ''water'' mean?', '["fuego","agua","tierra","aire"]', 'agua'),
('vocabulary', 'a1', 'What does ''book'' mean?', '["mesa","silla","libro","puerta"]', 'libro'),
('vocabulary', 'a1', 'What does ''cat'' mean?', '["perro","gato","pájaro","pez"]', 'gato'),
('vocabulary', 'a1', 'What does ''house'' mean?', '["coche","casa","calle","ciudad"]', 'casa'),
('vocabulary', 'a1', 'What does ''food'' mean?', '["ropa","comida","dinero","trabajo"]', 'comida'),
('vocabulary', 'a1', 'What does ''happy'' mean?', '["triste","feliz","enojado","cansado"]', 'feliz'),
('vocabulary', 'a1', 'What does ''big'' mean?', '["pequeño","grande","largo","corto"]', 'grande'),
('vocabulary', 'a1', 'What does ''friend'' mean?', '["enemigo","amigo","hermano","vecino"]', 'amigo');

-- Vocabulary A2 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'a2', 'What does ''schedule'' mean?', '["horario","escuela","clase","tarea"]', 'horario'),
('vocabulary', 'a2', 'What does ''improve'' mean?', '["empeorar","mejorar","mantener","cambiar"]', 'mejorar'),
('vocabulary', 'a2', 'What does ''appointment'' mean?', '["apartamento","cita","departamento","documento"]', 'cita'),
('vocabulary', 'a2', 'What does ''advice'' mean?', '["consejo","aviso","noticia","pregunta"]', 'consejo'),
('vocabulary', 'a2', 'What does ''weather'' mean?', '["clima","agua","viento","nube"]', 'clima'),
('vocabulary', 'a2', 'What does ''journey'' mean?', '["destino","viaje","camino","mapa"]', 'viaje'),
('vocabulary', 'a2', 'What does ''explain'' mean?', '["preguntar","explicar","responder","repetir"]', 'explicar'),
('vocabulary', 'a2', 'What does ''afford'' mean?', '["permitirse","ofrecer","comprar","vender"]', 'permitirse'),
('vocabulary', 'a2', 'What does ''borrow'' mean?', '["prestar","pedir prestado","devolver","robar"]', 'pedir prestado');

-- Vocabulary B1 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'b1', 'What does ''reliable'' mean?', '["confiable","rápido","lento","caro"]', 'confiable'),
('vocabulary', 'b1', 'What does ''achieve'' mean?', '["perder","lograr","intentar","fallar"]', 'lograr'),
('vocabulary', 'b1', 'What does ''genuine'' mean?', '["falso","auténtico","barato","caro"]', 'auténtico'),
('vocabulary', 'b1', 'What does ''shortage'' mean?', '["exceso","escasez","equilibrio","abundancia"]', 'escasez'),
('vocabulary', 'b1', 'What does ''complaint'' mean?', '["cumplido","queja","sugerencia","pregunta"]', 'queja'),
('vocabulary', 'b1', 'What does ''struggle'' mean?', '["rendirse","luchar","descansar","ganar"]', 'luchar'),
('vocabulary', 'b1', 'What does ''consequence'' mean?', '["causa","consecuencia","razón","motivo"]', 'consecuencia'),
('vocabulary', 'b1', 'What does ''meanwhile'' mean?', '["después","mientras tanto","antes","finalmente"]', 'mientras tanto'),
('vocabulary', 'b1', 'What does ''opportunity'' mean?', '["problema","oportunidad","obligación","dificultad"]', 'oportunidad');

-- Vocabulary B2 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'b2', 'What does ''thoroughly'' mean?', '["rápidamente","minuciosamente","fácilmente","brevemente"]', 'minuciosamente'),
('vocabulary', 'b2', 'What does ''acknowledge'' mean?', '["ignorar","reconocer","negar","olvidar"]', 'reconocer'),
('vocabulary', 'b2', 'What does ''reluctant'' mean?', '["entusiasta","reacio","decidido","impaciente"]', 'reacio'),
('vocabulary', 'b2', 'What does ''undermine'' mean?', '["apoyar","socavar","construir","reforzar"]', 'socavar'),
('vocabulary', 'b2', 'What does ''profound'' mean?', '["superficial","profundo","simple","breve"]', 'profundo'),
('vocabulary', 'b2', 'What does ''ambiguous'' mean?', '["claro","ambiguo","directo","preciso"]', 'ambiguo'),
('vocabulary', 'b2', 'What does ''overwhelm'' mean?', '["calmar","abrumar","ignorar","simplificar"]', 'abrumar'),
('vocabulary', 'b2', 'What does ''subtle'' mean?', '["obvio","sutil","fuerte","llamativo"]', 'sutil'),
('vocabulary', 'b2', 'What does ''deteriorate'' mean?', '["mejorar","deteriorarse","estabilizarse","crecer"]', 'deteriorarse');

-- Vocabulary C1 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('vocabulary', 'c1', 'What does ''ubiquitous'' mean?', '["raro","omnipresente","invisible","antiguo"]', 'omnipresente'),
('vocabulary', 'c1', 'What does ''exacerbate'' mean?', '["mejorar","agravar","resolver","simplificar"]', 'agravar'),
('vocabulary', 'c1', 'What does ''pragmatic'' mean?', '["idealista","teórico","práctico","abstracto"]', 'práctico'),
('vocabulary', 'c1', 'What does ''juxtapose'' mean?', '["separar","yuxtaponer","mezclar","ocultar"]', 'yuxtaponer'),
('vocabulary', 'c1', 'What does ''tenacious'' mean?', '["débil","tenaz","perezoso","tímido"]', 'tenaz'),
('vocabulary', 'c1', 'What does ''ephemeral'' mean?', '["permanente","efímero","importante","sólido"]', 'efímero'),
('vocabulary', 'c1', 'What does ''convoluted'' mean?', '["simple","enrevesado","directo","breve"]', 'enrevesado'),
('vocabulary', 'c1', 'What does ''acquiesce'' mean?', '["resistir","consentir","rechazar","protestar"]', 'consentir'),
('vocabulary', 'c1', 'What does ''unprecedented'' mean?', '["común","sin precedentes","esperado","repetido"]', 'sin precedentes');

-- =============================================
-- GRAMMAR questions (36 total: ~7 per level)
-- =============================================

-- Grammar A1 (9)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'a1', 'She ___ to school every day.', '["go","goes","going","gone"]', 'goes'),
('grammar', 'a1', 'They ___ students.', '["is","am","are","be"]', 'are'),
('grammar', 'a1', 'I ___ a car.', '["has","have","having","had"]', 'have'),
('grammar', 'a1', '___ you like coffee?', '["Does","Do","Is","Are"]', 'Do'),
('grammar', 'a1', 'He ___ not speak French.', '["do","does","is","are"]', 'does'),
('grammar', 'a1', 'There ___ many people here.', '["is","am","are","be"]', 'are'),
('grammar', 'a1', 'She ___ a teacher.', '["is","am","are","be"]', 'is'),
('grammar', 'a1', 'We ___ to the park yesterday.', '["go","goes","went","going"]', 'went'),
('grammar', 'a1', 'I ___ English every day.', '["study","studies","studying","studied"]', 'study');

-- Grammar A2 (7)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'a2', 'I ___ dinner when you called.', '["cook","cooked","was cooking","am cooking"]', 'was cooking'),
('grammar', 'a2', 'She has ___ to Paris twice.', '["be","been","being","was"]', 'been'),
('grammar', 'a2', 'You should ___ more water.', '["drink","drinking","to drink","drank"]', 'drink'),
('grammar', 'a2', 'He ___ here since 2020.', '["lives","lived","has lived","is living"]', 'has lived'),
('grammar', 'a2', 'If it rains, I ___ stay home.', '["will","would","am","was"]', 'will'),
('grammar', 'a2', 'She is ___ than her sister.', '["tall","taller","tallest","more tall"]', 'taller'),
('grammar', 'a2', 'They ___ football when it started raining.', '["play","played","were playing","have played"]', 'were playing');

-- Grammar B1 (7)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'b1', 'If I ___ rich, I would travel the world.', '["am","was","were","be"]', 'were'),
('grammar', 'b1', 'The book ___ by the author last year.', '["wrote","was written","has written","writes"]', 'was written'),
('grammar', 'b1', 'I wish I ___ more time.', '["have","had","has","having"]', 'had'),
('grammar', 'b1', 'She suggested that he ___ earlier.', '["arrives","arrived","arrive","arriving"]', 'arrive'),
('grammar', 'b1', 'By the time we arrived, they ___.', '["left","have left","had left","leave"]', 'had left'),
('grammar', 'b1', 'He asked me where I ___.', '["live","lived","living","lives"]', 'lived'),
('grammar', 'b1', 'The report ___ by tomorrow.', '["will finish","will be finished","finishes","is finishing"]', 'will be finished');

-- Grammar B2 (7)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'b2', 'Had I known, I ___ differently.', '["would act","would have acted","acted","will act"]', 'would have acted'),
('grammar', 'b2', 'Not only ___ he pass the exam, but he got the highest score.', '["does","did","has","was"]', 'did'),
('grammar', 'b2', 'She ___ have left already; her coat is gone.', '["must","can","should","would"]', 'must'),
('grammar', 'b2', 'The project, ___ was delayed twice, is finally complete.', '["that","which","what","who"]', 'which'),
('grammar', 'b2', 'I would rather you ___ me the truth.', '["tell","told","telling","tells"]', 'told'),
('grammar', 'b2', 'No sooner ___ he arrived than it started raining.', '["has","had","did","was"]', 'had'),
('grammar', 'b2', 'It is essential that she ___ on time.', '["is","be","was","being"]', 'be');

-- Grammar C1 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('grammar', 'c1', 'Scarcely ___ the train left when it started raining.', '["has","had","have","did"]', 'had'),
('grammar', 'c1', 'Were it not for his help, we ___ succeeded.', '["wouldn''t have","won''t have","didn''t","couldn''t"]', 'wouldn''t have'),
('grammar', 'c1', 'Little ___ she know what was about to happen.', '["does","did","has","was"]', 'did'),
('grammar', 'c1', 'So intense ___ the heat that the road began to melt.', '["is","was","has been","were"]', 'was'),
('grammar', 'c1', 'He speaks as though he ___ an expert.', '["is","were","was","be"]', 'were'),
('grammar', 'c1', 'Under no circumstances ___ this information be shared.', '["should","would","could","might"]', 'should');

-- =============================================
-- LISTENING questions (45 total: 9 per level)
-- With progressive complexity and audio_speed
-- =============================================

-- Listening A1 (9) - Simple sentences, slow speed
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'a1', 'Which word sounds like ''sea''?', '["see","say","so","sue"]', 'see', 0.80),
('listening', 'a1', 'Which word rhymes with ''cat''?', '["cut","bat","cot","bit"]', 'bat', 0.80),
('listening', 'a1', 'Which word rhymes with ''light''?', '["lit","let","night","lot"]', 'night', 0.80),
('listening', 'a1', 'Which word rhymes with ''make''?', '["milk","cake","mock","muck"]', 'cake', 0.80),
('listening', 'a1', 'Which word sounds like ''two''?', '["to","tie","tea","toy"]', 'to', 0.80),
('listening', 'a1', 'Which word rhymes with ''bed''?', '["bad","bid","red","rod"]', 'red', 0.80),
('listening', 'a1', 'Which word sounds like ''right''?', '["write","rate","root","rest"]', 'write', 0.80),
('listening', 'a1', 'Which word rhymes with ''day''?', '["die","do","play","due"]', 'play', 0.80),
('listening', 'a1', 'Which word sounds like ''know''?', '["now","no","new","not"]', 'no', 0.80);

-- Listening A2 (9) - Coordinated sentences, stress patterns
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'a2', '''I would have gone'' - which word is stressed?', '["I","would","have","gone"]', 'gone', 0.85),
('listening', 'a2', 'Which pair are homophones?', '["their/there","this/that","here/hear","both A and C"]', 'both A and C', 0.85),
('listening', 'a2', 'In ''She CAN''T swim'', which word is emphasized?', '["She","CAN''T","swim","none"]', 'CAN''T', 0.85),
('listening', 'a2', 'Which word has a silent ''b''?', '["climb","cabin","ribbon","maybe"]', 'climb', 0.85),
('listening', 'a2', '''I like coffee, but she prefers tea'' - how many clauses?', '["1","2","3","4"]', '2', 0.85),
('listening', 'a2', 'Which pair sounds the same?', '["flower/flour","house/horse","bear/beer","car/care"]', 'flower/flour', 0.85),
('listening', 'a2', 'In ''He DIDN''T go'', the stress is on:', '["He","DIDN''T","go","none"]', 'DIDN''T', 0.85),
('listening', 'a2', 'Which word has a silent ''k''?', '["knock","kite","keep","key"]', 'knock', 0.85),
('listening', 'a2', '''They went to the store and bought milk'' - which word links the clauses?', '["to","the","and","bought"]', 'and', 0.85);

-- Listening B1 (9) - Subordinate clauses, phrasal verbs
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'b1', 'In ''She didn''t go'', what is the contraction of?', '["did not","does not","do not","is not"]', 'did not', 0.90),
('listening', 'b1', 'Which word has a silent letter: knight, jump, plan, desk?', '["knight","jump","plan","desk"]', 'knight', 0.90),
('listening', 'b1', '''If it rains tomorrow, we''ll stay home'' - what type of sentence is this?', '["simple","compound","conditional","question"]', 'conditional', 0.90),
('listening', 'b1', 'What does ''pick up'' mean in ''I''ll pick you up at 8''?', '["recoger","elegir","levantar","llamar"]', 'recoger', 0.90),
('listening', 'b1', '''Although she was tired, she kept working'' - which word starts the subordinate clause?', '["she","was","Although","kept"]', 'Although', 0.90),
('listening', 'b1', '''I''ve been waiting for an hour'' - what tense is this?', '["present simple","past simple","present perfect continuous","future"]', 'present perfect continuous', 0.90),
('listening', 'b1', 'Which sentence uses the passive voice?', '["She wrote the letter","The letter was written","He is writing now","They write daily"]', 'The letter was written', 0.90),
('listening', 'b1', '''He turned down the offer'' - what does ''turned down'' mean?', '["aceptó","rechazó","consideró","recibió"]', 'rechazó', 0.90),
('listening', 'b1', '''The man who lives next door is a doctor'' - ''who lives next door'' is a:', '["main clause","relative clause","conditional","question"]', 'relative clause', 0.90);

-- Listening B2 (9) - Passive, conditionals, idioms
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'b2', '''Had I known earlier, I would have told you'' - what type of conditional is this?', '["zero","first","second","third"]', 'third', 1.00),
('listening', 'b2', 'What does ''break the ice'' mean?', '["romper algo","iniciar una conversación","causar problemas","hacer frío"]', 'iniciar una conversación', 1.00),
('listening', 'b2', '''The report, which was submitted late, needs revision'' - identify the non-restrictive clause.', '["The report","which was submitted late","needs revision","The report needs"]', 'which was submitted late', 1.00),
('listening', 'b2', '''She must have left already'' - what does ''must have'' express?', '["obligation","certainty about past","possibility","permission"]', 'certainty about past', 1.00),
('listening', 'b2', 'What does ''get cold feet'' mean?', '["tener frío","acobardarse","enfermarse","correr rápido"]', 'acobardarse', 1.00),
('listening', 'b2', '''Not only did he win, but he also broke the record'' - this uses:', '["passive voice","inversion","reported speech","gerund"]', 'inversion', 1.00),
('listening', 'b2', '''The project was supposed to have been completed by Friday'' - this expresses:', '["a future plan","an unfulfilled expectation","a suggestion","a preference"]', 'an unfulfilled expectation', 1.00),
('listening', 'b2', 'What does ''let the cat out of the bag'' mean?', '["liberar un animal","revelar un secreto","causar caos","escaparse"]', 'revelar un secreto', 1.00),
('listening', 'b2', '''Were I in your position, I would accept'' - this is:', '["first conditional","second conditional with inversion","third conditional","zero conditional"]', 'second conditional with inversion', 1.00);

-- Listening C1 (9) - Complex sentences, advanced vocabulary, contractions
INSERT INTO test_questions (type, level, question, options, correct_answer, audio_speed) VALUES
('listening', 'c1', '''She''s been meaning to bring it up, but it never quite comes up'' - ''bring it up'' means:', '["levantarlo","mencionarlo","subirlo","recordarlo"]', 'mencionarlo', 1.10),
('listening', 'c1', '''Notwithstanding the evidence, the jury acquitted him'' - ''notwithstanding'' means:', '["debido a","a pesar de","además de","sin considerar"]', 'a pesar de', 1.10),
('listening', 'c1', '''The implications of this policy are far-reaching and multifaceted'' - ''multifaceted'' means:', '["simple","complicado","multifacético","unilateral"]', 'multifacético', 1.10),
('listening', 'c1', '''He couldn''t have been more wrong'' - this expresses:', '["slight error","maximum possible error","uncertainty","regret"]', 'maximum possible error', 1.10),
('listening', 'c1', '''The CEO''s remarks were tantamount to an admission of guilt'' - ''tantamount to'' means:', '["contrario a","equivalente a","diferente de","superior a"]', 'equivalente a', 1.10),
('listening', 'c1', '''Seldom does one encounter such a compelling argument'' - this sentence uses:', '["passive voice","subjunctive","fronted adverbial with inversion","cleft sentence"]', 'fronted adverbial with inversion', 1.10),
('listening', 'c1', '''The findings corroborate previous research'' - ''corroborate'' means:', '["contradecir","corroborar","ignorar","cuestionar"]', 'corroborar', 1.10),
('listening', 'c1', '''It''s high time we addressed this issue'' - ''it''s high time'' expresses:', '["past habit","future plan","urgency/overdue action","possibility"]', 'urgency/overdue action', 1.10),
('listening', 'c1', '''The nuances of the debate were lost on the audience'' - ''lost on'' means:', '["interesantes para","incomprendidas por","importantes para","aburridas para"]', 'incomprendidas por', 1.10);

-- =============================================
-- PRONUNCIATION questions (30 total: 6 per level)
-- =============================================

-- Pronunciation A1 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'a1', 'How many syllables does ''beautiful'' have?', '["2","3","4","5"]', '3'),
('pronunciation', 'a1', 'Which word starts with a vowel sound?', '["house","hour","horse","hat"]', 'hour'),
('pronunciation', 'a1', 'How many syllables does ''interesting'' have?', '["2","3","4","5"]', '4'),
('pronunciation', 'a1', 'Which word starts with a consonant sound?', '["apple","uncle","uniform","egg"]', 'uniform'),
('pronunciation', 'a1', 'How many syllables does ''chocolate'' have?', '["2","3","4","1"]', '3'),
('pronunciation', 'a1', 'Which word has 1 syllable?', '["open","about","cat","water"]', 'cat');

-- Pronunciation A2 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'a2', 'Where is the stress in ''computer''?', '["COM-pu-ter","com-PU-ter","com-pu-TER","all equal"]', 'com-PU-ter'),
('pronunciation', 'a2', 'Which word has a different vowel sound: ''boot'', ''food'', ''good'', ''moon''?', '["boot","food","good","moon"]', 'good'),
('pronunciation', 'a2', 'Where is the stress in ''important''?', '["IM-por-tant","im-POR-tant","im-por-TANT","all equal"]', 'im-POR-tant'),
('pronunciation', 'a2', 'Which word has a silent ''e''?', '["cake","bed","pen","pet"]', 'cake'),
('pronunciation', 'a2', 'Where is the stress in ''banana''?', '["BA-na-na","ba-NA-na","ba-na-NA","all equal"]', 'ba-NA-na'),
('pronunciation', 'a2', 'Which ''th'' sound is different: ''the'', ''this'', ''think'', ''that''?', '["the","this","think","that"]', 'think');

-- Pronunciation B1 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'b1', 'Where is the stress in ''photograph'' vs ''photography''?', '["Same place","PHO-to-graph vs pho-TO-gra-phy","pho-TO-graph vs PHO-to-gra-phy","No stress"]', 'PHO-to-graph vs pho-TO-gra-phy'),
('pronunciation', 'b1', 'How many syllables does ''comfortable'' have in natural speech?', '["2","3","4","5"]', '3'),
('pronunciation', 'b1', 'Which word pair has different stress patterns?', '["import (noun) / import (verb)","table / chair","happy / funny","walking / running"]', 'import (noun) / import (verb)'),
('pronunciation', 'b1', 'In connected speech, ''want to'' often sounds like:', '["wanna","wonto","want-to","wanta"]', 'wanna'),
('pronunciation', 'b1', 'Which word has the ''schwa'' sound (/ə/)?', '["cat","about","see","too"]', 'about'),
('pronunciation', 'b1', 'Where is the stress in ''advertisement''?', '["AD-ver-tise-ment","ad-VER-tise-ment","ad-ver-TISE-ment","ad-ver-tise-MENT"]', 'ad-VER-tise-ment');

-- Pronunciation B2 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'b2', 'In ''I SAID Tuesday, not Thursday'' - what type of stress is on ''SAID''?', '["word stress","sentence stress","contrastive stress","no stress"]', 'contrastive stress'),
('pronunciation', 'b2', 'Which minimal pair tests the /ɪ/ vs /iː/ distinction?', '["ship/sheep","cat/cut","bed/bad","hot/hat"]', 'ship/sheep'),
('pronunciation', 'b2', 'In natural speech, ''going to'' becomes:', '["gonna","gointo","going-to","gonto"]', 'gonna'),
('pronunciation', 'b2', 'The word ''record'' changes stress based on:', '["tense","whether it''s a noun or verb","formality","speaker''s accent"]', 'whether it''s a noun or verb'),
('pronunciation', 'b2', 'Which word has a different number of syllables than the others?', '["business","family","different","interesting"]', 'interesting'),
('pronunciation', 'b2', 'The ''ed'' in ''wanted'' is pronounced as:', '["/t/","/d/","/ɪd/","silent"]', '/ɪd/');

-- Pronunciation C1 (6)
INSERT INTO test_questions (type, level, question, options, correct_answer) VALUES
('pronunciation', 'c1', 'In ''She didn''t STEAL it, she BORROWED it'', the stressed words indicate:', '["emphasis","contrastive focus","new information","correction"]', 'correction'),
('pronunciation', 'c1', 'Which sentence has a falling intonation pattern?', '["Are you coming?","She went home.","Really?","Is that yours?"]', 'She went home.'),
('pronunciation', 'c1', 'The intrusive /r/ occurs in:', '["law and order","the car is red","far away","more or less"]', 'law and order'),
('pronunciation', 'c1', 'In rapid speech, ''I have got to go'' typically reduces to:', '["I gotta go","I have go","I got going","I have got go"]', 'I gotta go'),
('pronunciation', 'c1', '''Assimilation'' in ''ten bikes'' means the ''n'' sounds more like:', '["/m/","/ŋ/","/n/","/d/"]', '/m/'),
('pronunciation', 'c1', 'Which phenomenon causes ''did you'' to sound like ''didju''?', '["elision","assimilation","palatalization","liaison"]', 'palatalization');
