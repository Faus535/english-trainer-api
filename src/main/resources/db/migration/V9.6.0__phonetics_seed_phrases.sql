-- Seed 220 practice phrases (5 per phoneme, 44 phonemes)
-- 2 easy, 2 medium, 1 hard per phoneme

-- /ɪ/ near-close near-front unrounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪ/'), 'The fish is big', 'easy', '["fish","is","big"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪ/'), 'Sit still and listen', 'easy', '["sit","still","listen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪ/'), 'His sister lives in the city', 'medium', '["his","sister","lives","in","city"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪ/'), 'The little kitten is sitting in a bin', 'medium', '["little","kitten","is","sitting","in","bin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪ/'), 'Six slim kids slipped quickly into the swimming pool', 'hard', '["six","slim","kids","slipped","quickly","into","swimming"]');

-- /e/ open-mid front unrounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Get ten red pens', 'easy', '["get","ten","red","pens"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'The bed is ready', 'easy', '["bed","ready"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'My best friend lent me his heavy vest', 'medium', '["best","friend","lent","heavy","vest"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Every member of the crew met at seven', 'medium', '["every","member","met","seven"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'The elegant elderly gentleman entered the reception desk area very energetically', 'hard', '["elegant","elderly","gentleman","entered","reception","desk","very","energetically"]');

-- /æ/ near-open front unrounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/æ/'), 'The cat sat down', 'easy', '["cat","sat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/æ/'), 'A black hat and bag', 'easy', '["black","hat","bag"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/æ/'), 'The man grabbed his backpack and ran fast', 'medium', '["man","grabbed","backpack","ran","fast"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/æ/'), 'That happy family has a flat in Manhattan', 'medium', '["that","happy","family","has","flat","Manhattan"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/æ/'), 'The anxious passenger rapidly grabbed the black travel bag and dashed back to the taxi cab', 'hard', '["anxious","passenger","rapidly","grabbed","black","travel","bag","dashed","back","taxi","cab"]');

-- /ɒ/ open back rounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɒ/'), 'The dog got lost', 'easy', '["dog","got","lost"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɒ/'), 'Stop the clock now', 'easy', '["stop","clock"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɒ/'), 'John wants a hot pot of coffee', 'medium', '["John","wants","hot","pot","coffee"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɒ/'), 'Tom dropped the bottle on top of the box', 'medium', '["Tom","dropped","bottle","on","top","box"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɒ/'), 'The honest doctor promptly offered to stop at the hospital lot before the opera concert', 'hard', '["honest","doctor","promptly","offered","stop","hospital","lot","opera","concert"]');

-- /ʌ/ open-mid back unrounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʌ/'), 'The bus is fun', 'easy', '["bus","fun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʌ/'), 'Come run in the sun', 'easy', '["come","run","sun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʌ/'), 'My brother loves his young puppy very much', 'medium', '["brother","loves","young","puppy","much"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʌ/'), 'The drunk judge must cut the budget once', 'medium', '["drunk","judge","must","cut","budget","once"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʌ/'), 'Her husband suddenly discovered a number of wonderful butterflies under the shrubs above the gutter', 'hard', '["husband","suddenly","discovered","number","wonderful","butterflies","under","shrubs","above","gutter"]');

-- /ʊ/ near-close near-back rounded vowel
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊ/'), 'Look at the book', 'easy', '["look","book"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊ/'), 'He took a good look', 'easy', '["took","good","look"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊ/'), 'The woman put sugar in the pudding', 'medium', '["woman","put","sugar","pudding"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊ/'), 'She could push the wooden hook further', 'medium', '["could","push","wooden","hook"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊ/'), 'The good looking cook stood by the bushes and shook the wool cushion carefully', 'hard', '["good","looking","cook","stood","bushes","shook","wool","cushion"]');

-- /ə/ mid central vowel (schwa)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ə/'), 'About a dozen', 'easy', '["about","a","dozen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ə/'), 'The sofa is broken', 'easy', '["the","sofa","broken"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ə/'), 'A banana and a lemon are upon the table', 'medium', '["a","banana","a","lemon","are","upon","the"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ə/'), 'The teacher was afraid to open the problem', 'medium', '["the","teacher","was","afraid","to","open","problem"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ə/'), 'The particular professor was about to deliver a memorable official presentation at the conference', 'hard', '["the","particular","professor","was","about","to","deliver","a","memorable","official","presentation","the","conference"]');

-- /iː/ close front unrounded vowel (long)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/iː/'), 'Please eat the cheese', 'easy', '["please","eat","cheese"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/iː/'), 'We see the tree', 'easy', '["we","see","tree"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/iː/'), 'She needs to read these three sheets', 'medium', '["she","needs","read","these","three","sheets"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/iː/'), 'The team agreed to keep the meeting brief and easy', 'medium', '["team","agreed","keep","meeting","brief","easy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/iː/'), 'The eager teacher repeatedly believed each student could freely achieve increasing reading speed each week', 'hard', '["eager","teacher","repeatedly","believed","each","freely","achieve","increasing","reading","speed","each","week"]');

-- /ɑː/ open back unrounded vowel (long)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɑː/'), 'The car is fast', 'easy', '["car","fast"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɑː/'), 'Start the task now', 'easy', '["start","task"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɑː/'), 'My father parked the car in the large garage', 'medium', '["father","parked","car","large","garage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɑː/'), 'The artist sat calmly in the dark garden bar', 'medium', '["artist","calmly","dark","garden","bar"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɑː/'), 'Charles and his charming aunt danced gracefully past the marble arch in the starlit palace garden', 'hard', '["Charles","charming","aunt","danced","past","marble","arch","starlit","palace","garden"]');

-- /ɔː/ open-mid back rounded vowel (long)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔː/'), 'Walk to the door', 'easy', '["walk","door"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔː/'), 'Draw four small balls', 'easy', '["draw","four","small","balls"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔː/'), 'The tall horse walked across the lawn before dawn', 'medium', '["tall","horse","walked","across","lawn","dawn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔː/'), 'All the authors thought the law was awfully flawed', 'medium', '["all","authors","thought","law","awfully","flawed"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔː/'), 'The cautious lawyer thoughtfully called for more support before installing the audit clause for all the important awards', 'hard', '["cautious","lawyer","thoughtfully","called","for","more","before","audit","clause","for","all","important","awards"]');

-- /uː/ close back rounded vowel (long)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uː/'), 'The blue moon shines', 'easy', '["blue","moon"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uː/'), 'Choose two new shoes', 'easy', '["choose","two","new","shoes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uː/'), 'The group moved smoothly through the room', 'medium', '["group","moved","smoothly","through","room"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uː/'), 'The rude student refused to follow the school rules', 'medium', '["rude","student","refused","school","rules"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uː/'), 'The shrewd youth pursued the truth and produced a beautiful flute tune in the cool moonlit room', 'hard', '["shrewd","youth","pursued","truth","produced","beautiful","flute","tune","cool","moonlit","room"]');

-- /ɜː/ open-mid central unrounded vowel (long)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɜː/'), 'The bird can learn', 'easy', '["bird","learn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɜː/'), 'Her first word hurt', 'easy', '["her","first","word","hurt"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɜː/'), 'The nurse turned early for her work shift', 'medium', '["nurse","turned","early","her","work"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɜː/'), 'The girl searched the earth for perfect purple ferns', 'medium', '["girl","searched","earth","perfect","purple","ferns"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɜː/'), 'The determined German researcher observed the curious bird circling nervously around the burning church on Thursday', 'hard', '["determined","German","researcher","observed","bird","circling","nervously","burning","church","Thursday"]');

-- /eɪ/ diphthong (face)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eɪ/'), 'Wait for the train', 'easy', '["wait","train"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eɪ/'), 'The game came late', 'easy', '["game","came","late"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eɪ/'), 'James made a great cake today', 'medium', '["James","made","great","cake","today"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eɪ/'), 'The player explained the strange delay at the railway station', 'medium', '["player","explained","strange","delay","railway","station"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eɪ/'), 'The neighbouring nation celebrated the amazing display of ancient paintings and paper decorations on the eighth of May', 'hard', '["neighbouring","nation","celebrated","amazing","display","ancient","paintings","paper","decorations","eighth","May"]');

-- /aɪ/ diphthong (price)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aɪ/'), 'I like the night sky', 'easy', '["I","like","night","sky"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aɪ/'), 'My bike is quite nice', 'easy', '["my","bike","quite","nice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aɪ/'), 'The child tried to climb the high white pine', 'medium', '["child","tried","climb","high","white","pine"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aɪ/'), 'I might decide to fly five miles tonight', 'medium', '["I","might","decide","fly","five","miles","tonight"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aɪ/'), 'The wise scientist described a surprising hypothesis while examining the tiny microorganisms under the bright light device', 'hard', '["wise","scientist","described","surprising","hypothesis","while","examining","tiny","bright","light","device"]');

-- /ɔɪ/ diphthong (choice)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔɪ/'), 'The boy has a toy', 'easy', '["boy","toy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔɪ/'), 'Join and enjoy yourself', 'easy', '["join","enjoy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔɪ/'), 'The noisy boys destroyed the toys with joy', 'medium', '["noisy","boys","destroyed","toys","joy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔɪ/'), 'Roy was annoyed by the oily moisture on the coin', 'medium', '["Roy","annoyed","oily","moisture","coin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɔɪ/'), 'The loyal employees voiced their disappointment about the employer''s choice to exploit the oyster joint venture', 'hard', '["loyal","employees","voiced","disappointment","employer''s","choice","exploit","oyster","joint","venture"]');

-- /əʊ/ diphthong (goat)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/əʊ/'), 'Go home alone', 'easy', '["go","home","alone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/əʊ/'), 'The boat floats slowly', 'easy', '["boat","floats","slowly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/əʊ/'), 'Joe knows the road to the old stone boat', 'medium', '["Joe","knows","road","old","stone","boat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/əʊ/'), 'The whole show was devoted to frozen yoghurt and toast', 'medium', '["whole","show","devoted","frozen","toast"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/əʊ/'), 'The lonely poet wrote a hopeful note about the golden glow of the October snow over the meadow below', 'hard', '["lonely","poet","wrote","hopeful","note","golden","glow","October","snow","over","meadow","below"]');

-- /aʊ/ diphthong (mouth)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aʊ/'), 'The cow sat down', 'easy', '["cow","down"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aʊ/'), 'Get out of the house', 'easy', '["out","house"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aʊ/'), 'The loud crowd shouted around the fountain', 'medium', '["loud","crowd","shouted","around","fountain"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aʊ/'), 'The brown owl found a mouse south of town', 'medium', '["brown","owl","found","mouse","south","town"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aʊ/'), 'The outspoken councillor announced a powerful outcome about the boundary dispute surrounding the mountain township grounds', 'hard', '["outspoken","councillor","announced","powerful","outcome","about","boundary","surrounding","mountain","township","grounds"]');

-- /ɪə/ diphthong (near)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪə/'), 'Come here my dear', 'easy', '["here","dear"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪə/'), 'I fear the deer', 'easy', '["fear","deer"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪə/'), 'The weird beard appeared near the pier', 'medium', '["weird","beard","appeared","near","pier"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪə/'), 'The engineer was sincere and clear about the idea', 'medium', '["engineer","sincere","clear","idea"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ɪə/'), 'The fierce volunteer steered the weary cheerful pioneers near the dreary frontier theatre during the severe year', 'hard', '["fierce","volunteer","steered","weary","cheerful","pioneers","near","dreary","frontier","theatre","severe","year"]');

-- /eə/ diphthong (square)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eə/'), 'Take care up there', 'easy', '["care","there"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eə/'), 'The air is fair today', 'easy', '["air","fair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eə/'), 'Mary was aware that the bear was staring at her hair', 'medium', '["aware","bear","staring","hair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eə/'), 'The rare pair of chairs were placed somewhere upstairs', 'medium', '["rare","pair","chairs","somewhere","upstairs"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eə/'), 'The careless billionaire declared that sharing and preparing the healthcare welfare programme was barely fair anywhere', 'hard', '["careless","billionaire","declared","sharing","preparing","healthcare","welfare","barely","fair","anywhere"]');

-- /ʊə/ diphthong (cure)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊə/'), 'Are you sure about that', 'easy', '["sure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊə/'), 'The tour was pure joy', 'easy', '["tour","pure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊə/'), 'The poor tourist was unsure about the detour', 'medium', '["poor","tourist","unsure","detour"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊə/'), 'We need to endure the moor during this rural tour', 'medium', '["endure","moor","rural","tour"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʊə/'), 'The mature insurance entrepreneur secured a cure for the obscure furious illness that plagued the poor rural tourists', 'hard', '["mature","insurance","entrepreneur","secured","cure","obscure","furious","poor","rural","tourists"]');

-- /p/ voiceless bilabial plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Put the pen on paper', 'easy', '["put","pen","paper"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Please pass the plate', 'easy', '["please","pass","plate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Peter picked a proper pepper at the party', 'medium', '["Peter","picked","proper","pepper","party"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'The pilot prepared to park the plane at the airport', 'medium', '["pilot","prepared","park","plane","airport"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'The proud popular professor presented a particularly impressive paper at the open public symposium on political philosophy', 'hard', '["proud","popular","professor","presented","particularly","impressive","paper","open","public","symposium","political","philosophy"]');

-- /b/ voiced bilabial plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'The boy bought bread', 'easy', '["boy","bought","bread"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'Bob broke the box', 'easy', '["Bob","broke","box"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'The baby grabbed the bright blue ball', 'medium', '["baby","grabbed","bright","blue","ball"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'My brother brought a big brown bag of bread', 'medium', '["brother","brought","big","brown","bag","bread"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'The brilliant ambitious librarian rubbed the rubber band before bravely distributing the brand new baseball bats', 'hard', '["brilliant","ambitious","librarian","rubbed","rubber","band","before","bravely","distributing","brand","baseball","bats"]');

-- /t/ voiceless alveolar plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'Tell them the truth', 'easy', '["tell","them","the","truth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'Two cats sat together', 'easy', '["two","cats","sat","together"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'The teacher told Tom to tidy the tent', 'medium', '["teacher","told","Tom","to","tidy","tent"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'It is important to test the water temperature today', 'medium', '["it","important","to","test","water","temperature","today"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'The talented student attempted to translate twenty interesting stories about the industrial textile industry last winter', 'hard', '["talented","student","attempted","to","translate","twenty","interesting","stories","about","industrial","textile","last","winter"]');

-- /d/ voiced alveolar plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The dog dug deep', 'easy', '["dog","dug","deep"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'Dad did a good deed', 'easy', '["dad","did","good","deed"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'David decided to drive down the dark road', 'medium', '["David","decided","drive","down","dark","road"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The dedicated doctor delivered the diagnosis immediately', 'medium', '["dedicated","doctor","delivered","diagnosis","immediately"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The disappointed student demanded a detailed description of the additional deadline and wondered what had caused the sudden delay', 'hard', '["disappointed","student","demanded","detailed","description","additional","deadline","wondered","what","had","caused","sudden","delay"]');

-- /k/ voiceless velar plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The cat can climb', 'easy', '["cat","can","climb"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'Keep the car clean', 'easy', '["keep","car","clean"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The cook quickly cut the cake with a knife', 'medium', '["cook","quickly","cut","cake","knife"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'Kate carefully packed the cotton blankets and thick coats', 'medium', '["Kate","carefully","packed","cotton","blankets","thick","coats"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The calculated risk of the complex mechanical construction required an accurate practical technique according to the clinical doctor', 'hard', '["calculated","risk","complex","mechanical","construction","required","accurate","practical","technique","according","clinical","doctor"]');

-- /g/ voiced velar plosive
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The girl is glad', 'easy', '["girl","glad"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'Get a good grade', 'easy', '["get","good","grade"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The guests began to giggle at the garden gate', 'medium', '["guests","began","giggle","garden","gate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'Greg agreed to give the dog a gorgeous green gift', 'medium', '["Greg","agreed","give","dog","gorgeous","green","gift"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The grumpy geography teacher grudgingly agreed to guide the large group through the magnificent zigzagging gorge again', 'hard', '["grumpy","geography","grudgingly","agreed","guide","large","group","magnificent","zigzagging","gorge","again"]');

-- /f/ voiceless labiodental fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'The fish is fresh', 'easy', '["fish","fresh"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'Five fingers feel fine', 'easy', '["five","fingers","feel","fine"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'The famous firefighter found his friend safe', 'medium', '["famous","firefighter","found","friend","safe"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'The fluffy fox found fifteen fresh figs on the farm', 'medium', '["fluffy","fox","found","fifteen","fresh","figs","farm"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'The fearful official finally confirmed the effective defensive effort before finishing the difficult professional conference', 'hard', '["fearful","official","finally","confirmed","effective","defensive","effort","before","finishing","difficult","professional","conference"]');

-- /v/ voiced labiodental fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'The vase is very nice', 'easy', '["vase","very"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'Drive the van over here', 'easy', '["drive","van","over"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'Victor gave his lovely wife seven violets', 'medium', '["Victor","gave","lovely","seven","violets"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'Every November I visit the vast river valley village', 'medium', '["every","November","visit","vast","river","valley","village"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'The vivacious adventurous traveller arrived at the magnificent vivid lavender covered avenue above the valley in November', 'hard', '["vivacious","adventurous","traveller","arrived","magnificent","vivid","lavender","covered","avenue","above","valley","November"]');

-- /θ/ voiceless dental fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/θ/'), 'I think three thoughts', 'easy', '["think","three","thoughts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/θ/'), 'Both teeth are thin', 'easy', '["both","teeth","thin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/θ/'), 'The youth threw the thick cloth on Thursday', 'medium', '["youth","threw","thick","cloth","Thursday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/θ/'), 'I thought the thirty-third birthday was on Thursday', 'medium', '["thought","thirty-third","birthday","Thursday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/θ/'), 'The enthusiastic mathematician thoroughly thought through the theory beneath the earth''s southern atmosphere with empathy and faith', 'hard', '["enthusiastic","mathematician","thoroughly","thought","through","theory","beneath","earth''s","southern","atmosphere","empathy","faith"]');

-- /ð/ voiced dental fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ð/'), 'This is the weather', 'easy', '["this","the","weather"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ð/'), 'That is my father', 'easy', '["that","my","father"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ð/'), 'These brothers gathered together with their mother', 'medium', '["these","brothers","gathered","together","their","mother"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ð/'), 'The weather was smoother than the other day', 'medium', '["the","weather","smoother","than","the","other"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ð/'), 'The feathered leather clothing was rather breathable and the northern heather withered smoothly together with the other southern bother', 'hard', '["the","feathered","leather","clothing","rather","breathable","the","northern","heather","withered","smoothly","together","the","other","southern","bother"]');

-- /s/ voiceless alveolar fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'The sun is so bright', 'easy', '["sun","is","so"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'Six students sit still', 'easy', '["six","students","sit","still"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'The sister passed the salt across the small space', 'medium', '["sister","passed","salt","across","small","space"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'Samantha''s success was simply sensational this season', 'medium', '["Samantha''s","success","was","simply","sensational","season"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'The suspicious scientist silently observed the ceaseless circulation of essential substances across the surface of the crystal glass', 'hard', '["suspicious","scientist","silently","observed","ceaseless","circulation","essential","substances","across","surface","crystal","glass"]');

-- /z/ voiced alveolar fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'The zoo has zebras', 'easy', '["zoo","has","zebras"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'His eyes were closed', 'easy', '["his","eyes","closed"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'The busy bees buzzed lazily around the roses', 'medium', '["busy","bees","buzzed","lazily","around","roses"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'Elizabeth''s cousins organized a surprise visit at his house', 'medium', '["Elizabeth''s","cousins","organized","surprise","visit","his","house"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'The amazing musicians composed dozens of energizing jazz tunes as the pleased fans recognized their amusing skills and prizes', 'hard', '["amazing","musicians","composed","dozens","energizing","jazz","tunes","as","pleased","fans","recognized","amusing","prizes"]');

-- /ʃ/ voiceless postalveolar fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʃ/'), 'She shut the shop', 'easy', '["she","shut","shop"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʃ/'), 'Wash the shirt and shoes', 'easy', '["wash","shirt","shoes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʃ/'), 'The chef showed us a special mushroom dish', 'medium', '["chef","showed","special","mushroom","dish"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʃ/'), 'Sharon wished she could share the shiny shell collection', 'medium', '["Sharon","wished","she","share","shiny","shell","collection"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʃ/'), 'The fashionable musician passionately showed the international delegation a selection of precious information about the nation''s initial evolution', 'hard', '["fashionable","musician","passionately","showed","international","delegation","selection","precious","information","nation''s","initial","evolution"]');

-- /ʒ/ voiced postalveolar fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʒ/'), 'It was a pleasure', 'easy', '["pleasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʒ/'), 'The usual decision stands', 'easy', '["usual","decision"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʒ/'), 'The television showed a visual illusion at leisure', 'medium', '["television","visual","illusion","leisure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʒ/'), 'His casual vision involved a beige garage treasure', 'medium', '["casual","vision","beige","garage","treasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ʒ/'), 'The unusual collision created massive confusion about the revision of the crucial decision regarding the exclusion of the prestigious television division', 'hard', '["unusual","collision","confusion","revision","crucial","decision","exclusion","prestigious","television","division"]');

-- /h/ voiceless glottal fricative
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'He has a big hat', 'easy', '["he","has","hat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'Her house is huge', 'easy', '["her","house","huge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'Henry hid behind the heavy wooden hedge', 'medium', '["Henry","hid","behind","heavy","hedge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'The happy husband hurried home to help his hungry horse', 'medium', '["happy","husband","hurried","home","help","his","hungry","horse"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'The humble historian heartily hoped that his heroic hypothesis about the ancient human habitat would help hundreds of households', 'hard', '["humble","historian","heartily","hoped","his","heroic","hypothesis","human","habitat","help","hundreds","households"]');

-- /tʃ/ voiceless postalveolar affricate
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/tʃ/'), 'Catch the cheese chunk', 'easy', '["catch","cheese","chunk"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/tʃ/'), 'The child chose chocolate', 'easy', '["child","chose","chocolate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/tʃ/'), 'Charlie watched the children chase each other', 'medium', '["Charlie","watched","children","chase","each"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/tʃ/'), 'The cheerful teacher challenged the church choir champion', 'medium', '["cheerful","teacher","challenged","church","choir","champion"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/tʃ/'), 'The chief researcher achieved a remarkable breakthrough by matching ancient Chinese characters with the enchanting architectural sketches of the future', 'hard', '["chief","researcher","achieved","breakthrough","matching","ancient","Chinese","characters","enchanting","architectural","sketches","future"]');

-- /dʒ/ voiced postalveolar affricate
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/dʒ/'), 'The judge just joked', 'easy', '["judge","just","joked"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/dʒ/'), 'Jane jumped for joy', 'easy', '["Jane","jumped","joy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/dʒ/'), 'George joined the gym in January and June', 'medium', '["George","joined","gym","January","June"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/dʒ/'), 'The manager suggested a major change to the project', 'medium', '["manager","suggested","major","change","project"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/dʒ/'), 'The courageous journalist from Egypt gently encouraged the young engineer to imagine a general strategy for the damaged bridge adjustment', 'hard', '["courageous","journalist","Egypt","gently","encouraged","engineer","imagine","general","strategy","damaged","bridge","adjustment"]');

-- /m/ voiced bilabial nasal
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'My mom makes meals', 'easy', '["my","mom","makes","meals"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'The man met his mate', 'easy', '["man","met","mate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'Mary made some marmalade at home this morning', 'medium', '["Mary","made","some","marmalade","home","morning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'The smart plumber came to mend the swimming pool pump', 'medium', '["smart","plumber","came","mend","swimming","pump"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'The memorable dramatic performance impressed the community members who immediately demanded more information from the famous museum committee', 'hard', '["memorable","dramatic","performance","impressed","community","members","immediately","demanded","more","information","famous","museum","committee"]');

-- /n/ voiced alveolar nasal
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'Nice and neat now', 'easy', '["nice","neat","now"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'Nine nuns in a van', 'easy', '["nine","nuns","in","van"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'Nancy noticed the new notice on the front lawn', 'medium', '["Nancy","noticed","new","notice","on","front","lawn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'The northern engineer ran into an unknown man near noon', 'medium', '["northern","engineer","ran","into","unknown","man","near","noon"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'The Indonesian government announced a new international environmental convention concerning the renovation of the national botanical garden and centre', 'hard', '["Indonesian","government","announced","new","international","environmental","convention","concerning","renovation","national","botanical","garden","centre"]');

-- /ŋ/ voiced velar nasal
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ŋ/'), 'Sing a long song', 'easy', '["sing","long","song"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ŋ/'), 'The king is strong', 'easy', '["king","strong"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ŋ/'), 'The young singer was playing along with the gang', 'medium', '["young","singer","playing","along","gang"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ŋ/'), 'Something interesting was happening during the morning meeting', 'medium', '["something","interesting","happening","during","morning","meeting"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ŋ/'), 'The relaxing evening gathering among the charming neighbouring buildings was amazing and everything including the singing was absolutely stunning', 'hard', '["relaxing","evening","gathering","among","charming","neighbouring","buildings","amazing","everything","including","singing","absolutely","stunning"]');

-- /l/ voiced alveolar lateral approximant
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'Look at the light', 'easy', '["look","light"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'I love the blue lake', 'easy', '["love","blue","lake"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'Lucy laughed loudly in the large living room', 'medium', '["Lucy","laughed","loudly","large","living"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'The little girl carefully collected lovely yellow leaves', 'medium', '["little","girl","carefully","collected","lovely","yellow","leaves"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'The intelligent reliable lawyer eloquently delivered a compelling logical analysis while illustrating the bilateral cultural alliance at the global political level', 'hard', '["intelligent","reliable","lawyer","eloquently","delivered","compelling","logical","analysis","while","illustrating","bilateral","cultural","alliance","global","political","level"]');

-- /r/ voiced alveolar approximant
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'The red rose is pretty', 'easy', '["red","rose","pretty"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'Run really fast right now', 'easy', '["run","really","right"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'Robert arrived at the restaurant around three', 'medium', '["Robert","arrived","restaurant","around","three"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'The rural road ran through the forest around the river', 'medium', '["rural","road","ran","through","forest","around","river"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'The remarkable instructor rigorously prepared the extraordinary curriculum for the spring programme covering irregular grammar structures and prose literature', 'hard', '["remarkable","instructor","rigorously","prepared","extraordinary","curriculum","spring","programme","covering","irregular","grammar","structures","prose","literature"]');

-- /w/ voiced labio-velar approximant
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'We will walk west', 'easy', '["we","will","walk","west"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'The wind was warm', 'easy', '["wind","was","warm"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'William went away one wet winter Wednesday', 'medium', '["William","went","away","one","wet","winter","Wednesday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'The woman wondered what was wrong with the water well', 'medium', '["woman","wondered","what","was","wrong","with","water","well"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'The wise wonderful wizard would always wish the weary wolves well whenever they wandered wildly towards the woodland waterfall somewhere westward', 'hard', '["wise","wonderful","wizard","would","always","wish","weary","wolves","well","whenever","wandered","wildly","towards","woodland","waterfall","somewhere","westward"]');

-- /j/ voiced palatal approximant
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'Yes you are young', 'easy', '["yes","you","young"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'Use the yellow yarn', 'easy', '["use","yellow","yarn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'The university student used a useful yellow uniform', 'medium', '["university","student","used","useful","yellow","uniform"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'Yesterday the young musician played a beautiful tune beyond the yard', 'medium', '["yesterday","young","musician","beautiful","beyond","yard"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'The enthusiastic young European youth used a unique yet familiar formula to calculate the annual yield of the ubiquitous community unit', 'hard', '["enthusiastic","young","European","youth","used","unique","yet","familiar","formula","annual","yield","ubiquitous","community","unit"]');
