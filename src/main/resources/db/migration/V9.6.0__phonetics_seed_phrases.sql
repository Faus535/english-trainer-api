-- ============================================================
-- Practice phrases for all 44 phonemes (5 phrases each = 220 total)
-- Phrases ordered by difficulty: easy, easy, medium, medium, hard
-- ============================================================

-- /i/ (short i)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/i/'), 'Sit in this ship', 'easy', '["sit","in","this","ship"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/i/'), 'Big fish swim', 'easy', '["big","fish","swim"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/i/'), 'His sister is still sitting', 'medium', '["his","sister","is","still","sitting"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/i/'), 'Six thick bricks sit in a pit', 'medium', '["six","thick","bricks","sit","pit"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/i/'), 'The children are busy knitting with thin sticks', 'hard', '["children","busy","knitting","with","thin","sticks"]');

-- /e/ (short e)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Red bed spread', 'easy', '["red","bed","spread"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Ten men left', 'easy', '["ten","men","left"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Fred said he went west', 'medium', '["Fred","said","went","west"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'Tell them seven eggs fell from the shelf', 'medium', '["tell","them","seven","eggs","fell","shelf"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/e/'), 'The weatherman predicted that twenty men get wet', 'hard', '["weatherman","predicted","twenty","men","get","wet"]');

-- /ae/ (cat vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ae/'), 'Bad cat sat', 'easy', '["bad","cat","sat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ae/'), 'That man ran fast', 'easy', '["that","man","ran","fast"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ae/'), 'The black cat sat on a flat mat', 'medium', '["black","cat","sat","flat","mat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ae/'), 'Sam had a ham sandwich and a glass of apple jam', 'medium', '["Sam","had","ham","sandwich","glass","apple","jam"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ae/'), 'The happy family planned a fantastic camping adventure', 'hard', '["happy","family","planned","fantastic","camping","adventure"]');

-- /ah/ (hot vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ah/'), 'Hot pot stop', 'easy', '["hot","pot","stop"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ah/'), 'Bob got a job', 'easy', '["Bob","got","job"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ah/'), 'The doctor stopped at the shop to drop off a box', 'medium', '["doctor","stopped","shop","drop","box"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ah/'), 'John wants his mom to come along on the long walk', 'medium', '["John","wants","mom","come","along","long"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ah/'), 'The common problem involves adopting proper stock options', 'hard', '["common","problem","involves","adopting","proper","stock","options"]');

-- /uh/ (cup vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uh/'), 'Cut the nut', 'easy', '["cut","nut"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uh/'), 'Come run in the sun', 'easy', '["come","run","sun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uh/'), 'My brother loves his mother and uncle', 'medium', '["brother","loves","mother","uncle"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uh/'), 'The young couple struggled to fund their summer fun', 'medium', '["young","couple","struggled","fund","summer","fun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/uh/'), 'Something wonderful suddenly comes from nothing but luck and love', 'hard', '["something","wonderful","suddenly","comes","nothing","luck","love"]');

-- /oo/ (book vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oo/'), 'Good book look', 'easy', '["good","book","look"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oo/'), 'Put your foot down', 'easy', '["put","foot","down"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oo/'), 'The cook took a good look at the pudding', 'medium', '["cook","took","good","look","pudding"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oo/'), 'He could not should would push the wool further', 'medium', '["could","should","would","push","wool"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oo/'), 'The woman understood the crooked brook near the bushes', 'hard', '["woman","understood","crooked","brook","bushes"]');

-- /schwa/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/schwa/'), 'About a banana', 'easy', '["about","banana"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/schwa/'), 'The sofa is comfortable', 'easy', '["sofa","comfortable"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/schwa/'), 'A woman alone in America is about to arrive', 'medium', '["woman","alone","America","about","arrive"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/schwa/'), 'The official photograph of the president appears original', 'medium', '["official","photograph","president","appears","original"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/schwa/'), 'Several familiar animals appeared along the mountain above the horizon', 'hard', '["several","familiar","animals","appeared","along","mountain","above","horizon"]');

-- /ee/ (long ee)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ee/'), 'See the tree', 'easy', '["see","tree"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ee/'), 'She sees green leaves', 'easy', '["she","sees","green","leaves"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ee/'), 'Please keep these keys near the screen', 'medium', '["please","keep","these","keys","screen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ee/'), 'Three people agree to eat cheese and cream each evening', 'medium', '["three","people","agree","eat","cheese","cream","evening"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ee/'), 'The eager teacher believed the team would easily achieve peace', 'hard', '["eager","teacher","believed","team","easily","achieve","peace"]');

-- /aa/ (father vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aa/'), 'Far dark car', 'easy', '["far","dark","car"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aa/'), 'Father starts the car', 'easy', '["father","starts","car"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aa/'), 'The farmer parked his car near the barn yard', 'medium', '["farmer","parked","car","barn","yard"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aa/'), 'Arthur started gardening and planted palm trees in March', 'medium', '["Arthur","started","gardening","planted","palm","March"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aa/'), 'The sergeant guarded the large marketplace against harm after dark', 'hard', '["sergeant","guarded","large","marketplace","against","harm","dark"]');

-- /aw/ (law vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw/'), 'All fall dawn', 'easy', '["all","fall","dawn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw/'), 'Paul saw the ball', 'easy', '["Paul","saw","ball"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw/'), 'The author called and talked about the law', 'medium', '["author","called","talked","about","law"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw/'), 'The daughter caught the small dog walking across the hall', 'medium', '["daughter","caught","small","dog","walking","across","hall"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw/'), 'His exhausted daughter thoughtfully bought all the autumn decorations', 'hard', '["exhausted","daughter","thoughtfully","bought","autumn","decorations"]');

-- /ooh/ (food vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ooh/'), 'Blue moon soon', 'easy', '["blue","moon","soon"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ooh/'), 'The crew flew to the zoo', 'easy', '["crew","flew","zoo"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ooh/'), 'The group moved through the room and used the new tools', 'medium', '["group","moved","through","room","used","new","tools"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ooh/'), 'The cool school produces super juicy fruit smoothies', 'medium', '["cool","school","produces","super","juicy","fruit","smoothies"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ooh/'), 'Two foolish students ruined the beautiful pool with superglue', 'hard', '["two","foolish","students","ruined","beautiful","pool","superglue"]');

-- /ur/ (bird vowel)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ur/'), 'Bird turns first', 'easy', '["bird","turns","first"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ur/'), 'Her nurse works early', 'easy', '["her","nurse","works","early"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ur/'), 'The girl heard the word and turned nervously', 'medium', '["girl","heard","word","turned","nervously"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ur/'), 'The world is certainly worth learning about further', 'medium', '["world","certainly","worth","learning","further"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ur/'), 'The determined researcher thoroughly searched the earth for perfect pearls', 'hard', '["determined","researcher","thoroughly","searched","earth","perfect","pearls"]');

-- /ay/ (say diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ay/'), 'Wait and play', 'easy', '["wait","play"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ay/'), 'Say his name today', 'easy', '["say","name","today"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ay/'), 'The baby played games and made paper airplanes', 'medium', '["baby","played","games","made","paper","airplanes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ay/'), 'They say the train came late again and we may wait all day', 'medium', '["say","train","came","late","again","may","wait","day"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ay/'), 'Jake claimed the famous painting displayed great imagination', 'hard', '["Jake","claimed","famous","painting","displayed","great","imagination"]');

-- /eye/ (my diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eye/'), 'My life time', 'easy', '["my","life","time"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eye/'), 'I like to fly high', 'easy', '["I","like","fly","high"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eye/'), 'The child decided to ride his bike five miles', 'medium', '["child","decided","ride","bike","five","miles"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eye/'), 'Nine tiny spiders climbed inside the tiny white pipe', 'medium', '["nine","tiny","spiders","climbed","inside","white","pipe"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/eye/'), 'The scientist described his exciting findings about wildlife migration', 'hard', '["scientist","described","exciting","findings","wildlife","migration"]');

-- /oy/ (boy diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oy/'), 'Boy enjoys toys', 'easy', '["boy","enjoys","toys"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oy/'), 'The boy pointed at the coin', 'easy', '["boy","pointed","coin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oy/'), 'Roy enjoyed the noisy crowd at the royal appointment', 'medium', '["Roy","enjoyed","noisy","crowd","royal","appointment"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oy/'), 'The oyster was boiling in oil and destroyed the foil wrapper', 'medium', '["oyster","boiling","oil","destroyed","foil"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/oy/'), 'The loyal employee avoided the annoying exploitation of employees', 'hard', '["loyal","employee","avoided","annoying","exploitation","employees"]');

-- /ow/ (go diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ow/'), 'Go home slow', 'easy', '["go","home","slow"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ow/'), 'Show me the road', 'easy', '["show","road"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ow/'), 'The old woman drove slowly over the stone bridge', 'medium', '["old","woman","drove","slowly","over","stone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ow/'), 'Joe knows the whole story about the golden bowl', 'medium', '["Joe","knows","whole","story","golden","bowl"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ow/'), 'The lonely soldier rode his pony below the frozen snow-covered plateau', 'hard', '["lonely","soldier","rode","pony","below","frozen","snow-covered","plateau"]');

-- /aw_d/ (out diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw_d/'), 'Out and about', 'easy', '["out","about"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw_d/'), 'How now brown cow', 'easy', '["how","now","brown","cow"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw_d/'), 'The loud crowd shouted around the town fountain', 'medium', '["loud","crowd","shouted","around","town","fountain"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw_d/'), 'The mouse found a thousand flowers growing outside the house', 'medium', '["mouse","found","thousand","flowers","outside","house"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/aw_d/'), 'The outrageous accountant proudly announced the astounding amount without doubt', 'hard', '["outrageous","accountant","proudly","announced","astounding","amount","without","doubt"]');

-- /ear/ (ear diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ear/'), 'Hear me clear', 'easy', '["hear","clear"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ear/'), 'Near here we cheer', 'easy', '["near","here","cheer"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ear/'), 'The deer appeared near the clear stream last year', 'medium', '["deer","appeared","near","clear","year"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ear/'), 'We feared the weird idea would interfere with the premiere', 'medium', '["feared","weird","idea","interfere","premiere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ear/'), 'The sincere engineer volunteered to steer the career of the cheerful cashier', 'hard', '["sincere","engineer","volunteered","steer","career","cheerful","cashier"]');

-- /air/ (air diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/air/'), 'Fair share there', 'easy', '["fair","share","there"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/air/'), 'Where is the chair', 'easy', '["where","chair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/air/'), 'Mary carefully prepared to share the pears', 'medium', '["Mary","carefully","prepared","share","pears"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/air/'), 'The parents stared at the rare bear near the stairs', 'medium', '["parents","stared","rare","bear","stairs"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/air/'), 'Claire declared the extraordinary millionaire barely cared about welfare', 'hard', '["Claire","declared","extraordinary","millionaire","barely","cared","welfare"]');

-- /ure/ (tour diphthong)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ure/'), 'Pure cure sure', 'easy', '["pure","cure","sure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ure/'), 'The tour was mature', 'easy', '["tour","mature"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ure/'), 'The poor tourist was unsure about the rural moor', 'medium', '["poor","tourist","unsure","rural","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ure/'), 'The curious endurance athlete secured a detour around the moor', 'medium', '["curious","endurance","secured","detour","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ure/'), 'The furious curator ensured the obscure insurance policy covered the premature failure', 'hard', '["furious","curator","ensured","obscure","insurance","premature","failure"]');

-- /p/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Pop a pie', 'easy', '["pop","pie"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Put the pen on paper', 'easy', '["put","pen","paper"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'Peter picked a pretty purple plum', 'medium', '["Peter","picked","pretty","purple","plum"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'The happy puppy played on the slippery path', 'medium', '["happy","puppy","played","slippery","path"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/p/'), 'The political opponent proposed a completely impractical policy', 'hard', '["political","opponent","proposed","completely","impractical","policy"]');

-- /b/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'Big boy bus', 'easy', '["big","boy","bus"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'Bob bought a blue bag', 'easy', '["Bob","bought","blue","bag"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'The baby grabbed a brown rubber ball', 'medium', '["baby","grabbed","brown","rubber","ball"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'Nobody remembered to bring the basketball to the barbecue', 'medium', '["nobody","remembered","bring","basketball","barbecue"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/b/'), 'The ambitious blackberry cobbler was probably the best contribution', 'hard', '["ambitious","blackberry","cobbler","probably","best","contribution"]');

-- /t/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'Top ten times', 'easy', '["top","ten","times"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'Take the train to town', 'easy', '["take","train","town"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'Tom told twelve tourists about the tall tower', 'medium', '["Tom","told","twelve","tourists","tall","tower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'The little kitten sat on top of the table and ate a tomato', 'medium', '["little","kitten","sat","top","table","ate","tomato"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/t/'), 'The talented attorney contacted the committee about the important matter', 'hard', '["talented","attorney","contacted","committee","important","matter"]');

-- /d/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'Did dad drive', 'easy', '["did","dad","drive"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The dog dug a deep hole', 'easy', '["dog","dug","deep"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'David decided to dance during dinner', 'medium', '["David","decided","dance","during","dinner"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The dedicated doctor discovered a deadly disease downtown', 'medium', '["dedicated","doctor","discovered","deadly","disease","downtown"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/d/'), 'The distinguished diplomat demanded additional documents regarding the deadline', 'hard', '["distinguished","diplomat","demanded","additional","documents","regarding","deadline"]');

-- /k/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'Cat can cook', 'easy', '["cat","can","cook"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'Keep the cake cold', 'easy', '["keep","cake","cold"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The quick captain carried the black sack across the creek', 'medium', '["quick","captain","carried","black","sack","across","creek"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The complicated chemical reaction quickly became a catastrophe', 'medium', '["complicated","chemical","reaction","quickly","became","catastrophe"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/k/'), 'The accomplished architect accurately calculated the acoustic characteristics', 'hard', '["accomplished","architect","accurately","calculated","acoustic","characteristics"]');

-- /g/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'Go get good', 'easy', '["go","get","good"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The girl gave a gift', 'easy', '["girl","gave","gift"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The big dog guards the garden gate', 'medium', '["big","dog","guards","garden","gate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'A group of giggling girls gathered grapes in the gorgeous garden', 'medium', '["group","giggling","girls","gathered","grapes","gorgeous","garden"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/g/'), 'The government guaranteed an aggressive investigation regarding illegal gambling', 'hard', '["government","guaranteed","aggressive","investigation","regarding","illegal","gambling"]');

-- /f/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'Five fine fish', 'easy', '["five","fine","fish"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'Find a fresh flower', 'easy', '["find","fresh","flower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'My friend fixed the flat shelf', 'medium', '["friend","fixed","flat","shelf"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'The famous professor offered free coffee after the conference', 'medium', '["famous","professor","offered","free","coffee","after","conference"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/f/'), 'Fifty different officials officially confirmed the effects of deforestation', 'hard', '["fifty","different","officials","officially","confirmed","effects","deforestation"]');

-- /v/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'Very vast view', 'easy', '["very","vast","view"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'The van drove over a valley', 'easy', '["van","drove","over","valley"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'Vivian gave a valuable violin to the village', 'medium', '["Vivian","gave","valuable","violin","village"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'The devoted investor viewed seven olive groves in November', 'medium', '["devoted","investor","viewed","seven","olive","groves","November"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/v/'), 'The various innovative devices obviously have improved vocabulary development', 'hard', '["various","innovative","devices","obviously","improved","vocabulary","development"]');

-- /th_v/ (voiceless th)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_v/'), 'Think and thank', 'easy', '["think","thank"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_v/'), 'Three thick things', 'easy', '["three","thick","things"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_v/'), 'On Thursday he thought about three thousand themes', 'medium', '["Thursday","thought","three","thousand","themes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_v/'), 'Both authors thoroughly thought through the math theory', 'medium', '["both","authors","thoroughly","thought","through","math","theory"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_v/'), 'The enthusiastic mathematician methodically theorized about thermodynamics', 'hard', '["enthusiastic","mathematician","methodically","theorized","thermodynamics"]');

-- /th_d/ (voiced th)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_d/'), 'This and that', 'easy', '["this","that"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_d/'), 'The mother and father', 'easy', '["the","mother","father"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_d/'), 'The other brother gathered together with them', 'medium', '["other","brother","gathered","together","them"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_d/'), 'Whether the weather is smooth or rather bothered', 'medium', '["whether","weather","smooth","rather","bothered"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/th_d/'), 'They breathed heavily although the leather was smoother than they remembered', 'hard', '["they","breathed","although","leather","smoother","they","remembered"]');

-- /s/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'See six socks', 'easy', '["see","six","socks"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'Sam sits in the sun', 'easy', '["Sam","sits","sun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'Some students seem especially interested in science', 'medium', '["some","students","seem","especially","interested","science"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'The serious cyclist crossed the icy surface successfully', 'medium', '["serious","cyclist","crossed","icy","surface","successfully"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/s/'), 'The suspicious circumstances surrounding the missing princess seemed senseless', 'hard', '["suspicious","circumstances","surrounding","missing","princess","seemed","senseless"]');

-- /z/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'Zoo zebra zone', 'easy', '["zoo","zebra","zone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'The buzzing bees in the breeze', 'easy', '["buzzing","bees","breeze"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'His cousin was amazed by the noisy jazz music', 'medium', '["his","cousin","amazed","noisy","jazz"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'These roses and daisies deserve prizes for their amazing sizes', 'medium', '["these","roses","daisies","deserve","prizes","amazing","sizes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/z/'), 'The organization realized its business proposals pleased dozens of advisors', 'hard', '["organization","realized","business","proposals","pleased","dozens","advisors"]');

-- /sh/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/sh/'), 'She shops shoes', 'easy', '["she","shops","shoes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/sh/'), 'Show me the ship', 'easy', '["show","ship"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/sh/'), 'She should wash the shirts before the fashion show', 'medium', '["she","should","wash","shirts","fashion","show"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/sh/'), 'The official chef specializes in delicious shellfish dishes', 'medium', '["official","chef","specializes","delicious","shellfish","dishes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/sh/'), 'The passionate musician showcased an exceptional international exhibition', 'hard', '["passionate","musician","showcased","exceptional","international","exhibition"]');

-- /zh/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/zh/'), 'Measure pleasure', 'easy', '["measure","pleasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/zh/'), 'The vision of leisure', 'easy', '["vision","leisure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/zh/'), 'It was a pleasure to measure the beige treasure', 'medium', '["pleasure","measure","beige","treasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/zh/'), 'The unusual decision caused a revision of the television schedule', 'medium', '["unusual","decision","revision","television"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/zh/'), 'The explosion of visual illusions on television made the intrusion particularly unusual', 'hard', '["explosion","visual","illusions","television","intrusion","particularly","unusual"]');

-- /h/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'His hat here', 'easy', '["his","hat","here"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'He has a huge house', 'easy', '["he","has","huge","house"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'Harry hopefully hurried home to have his hot hamburger', 'medium', '["Harry","hopefully","hurried","home","have","hot","hamburger"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'The heavy horse happily hopped behind the high hedge', 'medium', '["heavy","horse","happily","hopped","behind","high","hedge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/h/'), 'The humble historian hypothesized how the ancient inhabitants inhabited the harsh highlands', 'hard', '["humble","historian","hypothesized","ancient","inhabitants","inhabited","harsh","highlands"]');

-- /ch/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ch/'), 'Cheese and chips', 'easy', '["cheese","chips"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ch/'), 'Each child chose chocolate', 'easy', '["each","child","chose","chocolate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ch/'), 'The teacher watched the cheerful children play catch', 'medium', '["teacher","watched","cheerful","children","catch"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ch/'), 'Much research has been achieved in the natural and cultural chapters', 'medium', '["much","research","achieved","natural","cultural","chapters"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ch/'), 'The chief architect sketched a charming kitchen with a characteristic arched chamber', 'hard', '["chief","architect","sketched","charming","kitchen","characteristic","arched","chamber"]');

-- /j/ (judge)
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'Just join Jane', 'easy', '["just","join","Jane"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'The judge enjoys jogging', 'easy', '["judge","enjoys","jogging"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'George joined the gym in January to jog and jump', 'medium', '["George","joined","gym","January","jog","jump"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'The large agency gently managed the major energy project', 'medium', '["large","agency","gently","managed","major","energy","project"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/j/'), 'The courageous sergeant generally engaged in dangerous rescue operations', 'hard', '["courageous","sergeant","generally","engaged","dangerous","rescue","operations"]');

-- /m/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'Make more music', 'easy', '["make","more","music"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'Mom makes muffins every morning', 'easy', '["mom","makes","muffins","morning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'The mammoth museum has many memorable moments', 'medium', '["mammoth","museum","many","memorable","moments"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'Maximum commitment may improve most management problems', 'medium', '["maximum","commitment","improve","management","problems"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/m/'), 'The immense community unanimously recommended the magnificent memorial monument', 'hard', '["immense","community","unanimously","recommended","magnificent","memorial","monument"]');

-- /n/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'Nine new nuns', 'easy', '["nine","new","nuns"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'No one knows nothing', 'easy', '["no","one","knows","nothing"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'Nancy noticed an innocent animal running nearby', 'medium', '["Nancy","noticed","innocent","animal","running","nearby"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'The international tennis tournament announced a new champion in June', 'medium', '["international","tennis","tournament","announced","new","champion","June"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/n/'), 'The environmental engineer eventually announced an innovative intervention plan', 'hard', '["environmental","engineer","eventually","announced","innovative","intervention","plan"]');

-- /ng/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ng/'), 'Sing a song', 'easy', '["sing","song"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ng/'), 'Bring the long string', 'easy', '["bring","long","string"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ng/'), 'The young king was wrong about the strong thing', 'medium', '["young","king","wrong","strong","thing"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ng/'), 'Nothing is more amazing than belonging to a loving, caring gang', 'medium', '["nothing","amazing","belonging","loving","caring"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/ng/'), 'The lingering feeling of longing among the gathering was overwhelming and intriguing', 'hard', '["lingering","feeling","longing","among","gathering","overwhelming","intriguing"]');

-- /r/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'Red rose ring', 'easy', '["red","rose","ring"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'Run around the room', 'easy', '["run","around","room"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'Roger really regretted reading the wrong recipe', 'medium', '["Roger","really","regretted","reading","wrong","recipe"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'The red rooster ran across the railroad bridge every morning', 'medium', '["red","rooster","ran","across","railroad","bridge","morning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/r/'), 'The remarkable rural restaurant regularly receives rave reviews regarding their roast recipes', 'hard', '["remarkable","rural","restaurant","regularly","receives","rave","reviews","regarding","roast","recipes"]');

-- /w/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'We want water', 'easy', '["we","want","water"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'Why would we wait', 'easy', '["why","would","we","wait"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'The woman wore a wonderful white wool sweater', 'medium', '["woman","wore","wonderful","white","wool","sweater"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'William wondered whether the weather would worsen by Wednesday', 'medium', '["William","wondered","whether","weather","would","worsen","Wednesday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/w/'), 'The woodworker wisely withdrew from the worldwide convention somewhere in Washington', 'hard', '["woodworker","wisely","withdrew","worldwide","convention","somewhere","Washington"]');

-- /y/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/y/'), 'Yes you young', 'easy', '["yes","you","young"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/y/'), 'Yesterday was yellow', 'easy', '["yesterday","yellow"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/y/'), 'The young student yearned for a year in New York', 'medium', '["young","student","yearned","year","York"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/y/'), 'Your unique opinion about the university yielded useful results', 'medium', '["your","unique","opinion","university","yielded","useful"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/y/'), 'The youthful musician enthusiastically communicated his peculiar yet brilliant ideas', 'hard', '["youthful","musician","enthusiastically","communicated","peculiar","brilliant","ideas"]');

-- /l/
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'Look left long', 'easy', '["look","left","long"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'Little bells fall well', 'easy', '["little","bells","fall","well"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'Lily collected beautiful blue glass bottles', 'medium', '["Lily","collected","beautiful","blue","glass","bottles"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'The reluctant diplomat carefully controlled the political dialogue', 'medium', '["reluctant","diplomat","carefully","controlled","political","dialogue"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = '/l/'), 'The brilliant electrical installation literally illuminated the cultural festival hall', 'hard', '["brilliant","electrical","installation","literally","illuminated","cultural","festival","hall"]');
