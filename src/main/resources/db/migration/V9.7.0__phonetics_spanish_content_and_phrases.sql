-- V9.7.0: Update phonemes with Spanish-oriented content (10 example words, Spanish descriptions/tips/mouthPosition)
-- and add 15 new phrases per phoneme (660 INSERTs: 5 easy + 5 medium + 5 hard each)

-- ============================================
-- SECTION 1: UPDATE phonemes with Spanish content
-- ============================================

-- /ɪ/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta como en "sit". Mucho mas relajada que la /i/ espanola. No tenses los labios ni la lengua; es un sonido breve y suave.',
  mouth_position = 'Labios ligeramente separados, lengua alta y adelantada pero relajada. La mandibula baja un poco mas que para la /i/ espanola.',
  tips = 'Piensa en una "i" espanola rapida y relajada. No sonrias tanto como para /iː/. Practica con "sit" vs "seat" para notar la diferencia.',
  example_words = '["sit","fish","bit","kit","lip","big","pin","tip","win","slim"]'
WHERE symbol = 'ɪ';

-- /e/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta media-frontal como en "bed". Similar a la "e" espanola pero un poco mas abierta.',
  mouth_position = 'Labios ligeramente separados, boca medio abierta, lengua a media altura. Mas abierta que para /ɪ/ pero menos que para /æ/.',
  tips = 'Es parecida a la "e" espanola de "mesa", pero abre un poco mas la boca. Practica con "bed" y "bad" para distinguir /e/ de /æ/.',
  example_words = '["bed","red","ten","pen","leg","hen","set","wet","yes","best"]'
WHERE symbol = 'e';

-- /æ/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta abierta-frontal como en "cat". No existe en espanol. Esta entre la "a" y la "e" espanolas.',
  mouth_position = 'Boca bien abierta, labios separados y estirados, lengua baja y adelantada. Mas abierta que /e/.',
  tips = 'Abre la boca como para decir "a" pero empuja la lengua hacia delante como para "e". El resultado es un sonido entre ambas. Practica con "cat" y "cut" para distinguir /æ/ de /ʌ/.',
  example_words = '["cat","hat","man","bad","sad","bag","map","fan","ran","back"]'
WHERE symbol = 'æ';

-- /ɒ/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta abierta-posterior redondeada como en "hot" (ingles britanico). Similar a una "o" espanola corta con la boca mas abierta.',
  mouth_position = 'Labios ligeramente redondeados, boca abierta, lengua baja y hacia atras.',
  tips = 'Piensa en la "o" espanola pero abre mas la boca y haz el sonido muy corto. En ingles americano se parece mas a /ɑː/.',
  example_words = '["hot","dog","lot","box","top","got","not","job","stop","rock"]'
WHERE symbol = 'ɒ';

-- /ʌ/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta central como en "cup". No existe en espanol. Es un sonido neutro, relajado y breve, parecido a una "a" muy corta.',
  mouth_position = 'Labios relajados y neutros, boca ligeramente abierta, lengua en posicion central.',
  tips = 'Relaja completamente la boca y haz un sonido corto neutro. No redondees los labios. Practica "cup" vs "cop" y "cut" vs "cat".',
  example_words = '["cup","bus","run","sun","fun","cut","luck","duck","mud","shut"]'
WHERE symbol = 'ʌ';

-- /ʊ/ short vowel
UPDATE phonemes SET
  description = 'Vocal corta redondeada como en "put". Mas corta y relajada que /uː/. No existe exactamente en espanol.',
  mouth_position = 'Labios ligeramente redondeados, lengua alta y hacia atras. Menos tension que para /uː/.',
  tips = 'Piensa en una "u" espanola rapida y relajada. No empujes los labios hacia delante como para /uː/. Practica "full" vs "fool".',
  example_words = '["put","book","look","good","foot","cook","push","pull","wood","hook"]'
WHERE symbol = 'ʊ';

-- /ə/ schwa
UPDATE phonemes SET
  description = 'La vocal mas comun del ingles: el "schwa". Es un sonido debil, neutro y muy breve que aparece en silabas atonas. No existe como fonema en espanol.',
  mouth_position = 'Boca apenas abierta, labios y lengua completamente relajados en posicion central. Es la vocal mas perezosa.',
  tips = 'Es el sonido mas relajado posible. Piensa en el sonido que haces cuando dudas: "uh". Aparece en silabas sin acento: "about" (əbaʊt), "banana" (bənænə).',
  example_words = '["about","banana","sofa","ago","taken","open","again","woman","police","problem"]'
WHERE symbol = 'ə';

-- /iː/ long vowel
UPDATE phonemes SET
  description = 'Vocal larga alta-frontal como en "see". Parecida a la "i" espanola pero mas larga y tensa. Sonrie al producirla.',
  mouth_position = 'Labios bien estirados como en una sonrisa, lengua alta y adelantada, mandibula casi cerrada.',
  tips = 'Sonrie ampliamente y mantiene el sonido mas tiempo que /ɪ/. Practica "sheep" vs "ship" para distinguir /iː/ de /ɪ/.',
  example_words = '["see","tree","free","beach","eat","team","read","green","sleep","week"]'
WHERE symbol = 'iː';

-- /ɑː/ long vowel
UPDATE phonemes SET
  description = 'Vocal larga abierta-posterior como en "car". Similar a la "a" espanola pero mas larga, mas abierta y con la lengua mas atras.',
  mouth_position = 'Boca bien abierta, lengua baja y hacia atras, labios no redondeados.',
  tips = 'Abre la boca como cuando el medico te dice "di aaa" y mantiene el sonido. Practica con "car", "far", "star".',
  example_words = '["car","far","star","heart","park","dark","arm","art","class","bath"]'
WHERE symbol = 'ɑː';

-- /ɔː/ long vowel
UPDATE phonemes SET
  description = 'Vocal larga media-posterior redondeada como en "saw". Parecida a una "o" espanola larga y redondeada.',
  mouth_position = 'Labios redondeados en forma de ovalo, lengua media-baja y hacia atras.',
  tips = 'Redondea firmemente los labios y mantiene el sonido largo y estable. Practica con "door", "more", "saw".',
  example_words = '["saw","door","more","four","law","call","walk","talk","ball","fall"]'
WHERE symbol = 'ɔː';

-- /uː/ long vowel
UPDATE phonemes SET
  description = 'Vocal larga alta-posterior redondeada como en "moon". Parecida a la "u" espanola pero mas larga y con los labios mas apretados.',
  mouth_position = 'Labios fuertemente redondeados y empujados hacia delante, lengua alta y hacia atras.',
  tips = 'Empuja los labios hacia delante como si soplaras una vela y mantiene el sonido largo. Practica "pool" vs "pull" para distinguir /uː/ de /ʊ/.',
  example_words = '["too","moon","food","blue","shoe","cool","room","soon","fruit","group"]'
WHERE symbol = 'uː';

-- /ɜː/ long vowel
UPDATE phonemes SET
  description = 'Vocal larga central como en "bird". No existe en espanol. Es un sonido neutro largo, como un schwa prolongado.',
  mouth_position = 'Labios neutros y ligeramente estirados, lengua a media altura en el centro de la boca.',
  tips = 'Mantiene una posicion relajada neutra y prolonga el sonido. Piensa en un "eee" pero sin sonreir y con la lengua en el centro. Muy comun en "girl", "word", "nurse".',
  example_words = '["bird","word","nurse","first","turn","learn","earth","work","shirt","early"]'
WHERE symbol = 'ɜː';

-- /eɪ/ diphthong
UPDATE phonemes SET
  description = 'Diptongo que se desliza de /e/ hacia /ɪ/ como en "say". Similar al diptongo "ei" espanol pero mas suave.',
  mouth_position = 'La boca empieza medio abierta y se va cerrando mientras la lengua sube.',
  tips = 'Empieza con la boca abierta en posicion de /e/ y desliza suavemente hacia /ɪ/. Practica con "say", "day", "make".',
  example_words = '["say","day","make","take","name","late","rain","play","wait","great"]'
WHERE symbol = 'eɪ';

-- /aɪ/ diphthong
UPDATE phonemes SET
  description = 'Diptongo que se desliza de /a/ abierta hacia /ɪ/ como en "my". Similar al diptongo "ai" espanol.',
  mouth_position = 'La boca empieza bien abierta y se va cerrando mientras la lengua sube hacia delante.',
  tips = 'Abre bien la boca y desliza hacia una posicion de sonrisa. Muy parecido al "ai" de "aire" en espanol. Practica "my", "time", "fly".',
  example_words = '["my","time","fly","nice","life","write","night","high","like","side"]'
WHERE symbol = 'aɪ';

-- /ɔɪ/ diphthong
UPDATE phonemes SET
  description = 'Diptongo que se desliza de /ɔ/ hacia /ɪ/ como en "boy". Similar al diptongo "oi" espanol de "oiga".',
  mouth_position = 'Los labios empiezan redondeados y se estiran mientras la lengua se mueve hacia delante.',
  tips = 'Empieza con labios redondeados y termina sonriendo. Muy parecido al "oi" de "oigo" en espanol. Practica con "boy", "toy", "join".',
  example_words = '["boy","toy","join","oil","coin","voice","point","choice","noise","enjoy"]'
WHERE symbol = 'ɔɪ';

-- /əʊ/ diphthong
UPDATE phonemes SET
  description = 'Diptongo que se desliza de /ə/ hacia /ʊ/ como en "go" (ingles britanico). No tiene equivalente directo en espanol.',
  mouth_position = 'Los labios empiezan neutros y se van redondeando mientras la lengua se mueve hacia atras y arriba.',
  tips = 'Empieza relajado con un schwa y desliza hacia una "u" corta redondeada. En ingles americano suena mas como /oʊ/. Practica con "go", "home", "know".',
  example_words = '["go","home","know","show","road","boat","phone","close","snow","old"]'
WHERE symbol = 'əʊ';

-- /aʊ/ diphthong
UPDATE phonemes SET
  description = 'Diptongo que se desliza de /a/ abierta hacia /ʊ/ como en "now". Similar al diptongo "au" espanol de "pausa".',
  mouth_position = 'La boca empieza bien abierta y se cierra con los labios redondeandose.',
  tips = 'Abre la boca y desliza hacia una "u" redondeada. Parecido al "au" de "causa" en espanol. Practica con "now", "house", "out".',
  example_words = '["now","house","out","town","down","found","round","about","cloud","mouth"]'
WHERE symbol = 'aʊ';

-- /ɪə/ diphthong
UPDATE phonemes SET
  description = 'Diptongo centralizante que se desliza de /ɪ/ hacia /ə/ como en "near". No existe en espanol.',
  mouth_position = 'La lengua empieza alta y adelantada y se relaja hacia el centro.',
  tips = 'Empieza con un /ɪ/ corto y relajate hacia un schwa. Es como decir "ia" pero muy relajado. Practica con "near", "here", "beer".',
  example_words = '["near","here","beer","clear","fear","year","idea","ear","appear","deer"]'
WHERE symbol = 'ɪə';

-- /eə/ diphthong
UPDATE phonemes SET
  description = 'Diptongo centralizante que se desliza de /e/ hacia /ə/ como en "hair". No existe en espanol.',
  mouth_position = 'La boca empieza medio abierta y se relaja a posicion neutra.',
  tips = 'Empieza con /e/ y deja que la boca se relaje naturalmente. Practica con "hair", "care", "where". En muchos acentos modernos se simplifica a una vocal larga.',
  example_words = '["hair","care","where","fair","pair","bear","share","wear","air","there"]'
WHERE symbol = 'eə';

-- /ʊə/ diphthong
UPDATE phonemes SET
  description = 'Diptongo centralizante que se desliza de /ʊ/ hacia /ə/ como en "pure". Es el diptongo menos comun del ingles.',
  mouth_position = 'Los labios empiezan redondeados y se relajan a posicion neutra.',
  tips = 'Empieza con labios redondeados como /ʊ/ y relajalos. En muchos acentos se reemplaza por /ɔː/. Practica con "pure", "tour", "sure".',
  example_words = '["pure","tour","sure","cure","poor","moor","lure","endure","mature","plural"]'
WHERE symbol = 'ʊə';

-- /p/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva bilabial sorda como en "pen". Igual que la "p" espanola pero con mas aspiracion en posicion inicial.',
  mouth_position = 'Ambos labios presionados juntos y luego liberados con una explosion de aire.',
  tips = 'Pon un papel delante de la boca: al decir "pen" el papel debe moverse por la aspiracion. La "p" espanola no tiene esta aspiracion. Practica "pin", "pen", "park".',
  example_words = '["pen","top","stop","park","put","pay","help","keep","open","speak"]'
WHERE symbol = 'p';

-- /b/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva bilabial sonora como en "big". Muy similar a la "b" espanola en posicion inicial (despues de pausa o "m").',
  mouth_position = 'Ambos labios presionados juntos y liberados mientras vibran las cuerdas vocales.',
  tips = 'Como la /p/ pero con vibracion de las cuerdas vocales. La "b" espanola entre vocales se suaviza, pero en ingles siempre es una oclusiva completa.',
  example_words = '["big","baby","job","back","ball","best","black","break","table","begin"]'
WHERE symbol = 'b';

-- /t/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva alveolar sorda como en "ten". Se articula con la lengua en los alveolos, no en los dientes como la "t" espanola.',
  mouth_position = 'La punta de la lengua toca la cresta alveolar (detras de los dientes superiores) y se libera con una explosion de aire.',
  tips = 'La diferencia clave con la "t" espanola: toca los alveolos, no los dientes. Ademas, en posicion inicial tiene aspiracion. Practica "ten", "top", "time".',
  example_words = '["ten","top","cat","time","tell","take","eat","sit","meet","start"]'
WHERE symbol = 't';

-- /d/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva alveolar sonora como en "dog". Se articula con la lengua en los alveolos, como /t/ pero con voz.',
  mouth_position = 'La punta de la lengua toca la cresta alveolar y se libera con vibracion de las cuerdas vocales.',
  tips = 'Como la /t/ pero con vibracion. La "d" espanola entre vocales se suaviza, pero en ingles siempre es oclusiva completa. Practica "dog", "did", "day".',
  example_words = '["dog","did","bad","day","door","down","hard","old","food","hand"]'
WHERE symbol = 'd';

-- /k/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva velar sorda como en "cat". Similar a la "c" espanola de "casa" pero con aspiracion inicial.',
  mouth_position = 'La parte posterior de la lengua presiona contra el paladar blando y se libera con una explosion de aire.',
  tips = 'Como la "c" de "casa" pero con aspiracion al inicio de palabra. Pon la mano delante de la boca para sentir el aire. Practica "cat", "key", "come".',
  example_words = '["cat","key","back","come","keep","cold","kick","quick","dark","ask"]'
WHERE symbol = 'k';

-- /g/ plosive
UPDATE phonemes SET
  description = 'Consonante oclusiva velar sonora como en "get". Similar a la "g" espanola de "gato" en posicion inicial.',
  mouth_position = 'La parte posterior de la lengua presiona contra el paladar blando y se libera con vibracion de las cuerdas vocales.',
  tips = 'Como la /k/ pero con vibracion de las cuerdas vocales. La "g" espanola entre vocales se suaviza, pero en ingles siempre es oclusiva completa.',
  example_words = '["get","big","dog","go","give","good","game","girl","green","bag"]'
WHERE symbol = 'g';

-- /f/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa labiodental sorda como en "fish". Igual que la "f" espanola.',
  mouth_position = 'Los dientes superiores descansan sobre el labio inferior mientras el aire fluye entre ellos.',
  tips = 'Es identica a la "f" espanola. Muerde suavemente el labio inferior y sopla. Practica con "fish", "off", "life".',
  example_words = '["fish","off","life","food","five","fast","feel","left","safe","first"]'
WHERE symbol = 'f';

-- /v/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa labiodental sonora como en "van". No existe en espanol. Es como la /f/ pero con vibracion.',
  mouth_position = 'Los dientes superiores descansan sobre el labio inferior con vibracion de cuerdas vocales y flujo de aire.',
  tips = 'Coloca los dientes sobre el labio inferior como para /f/ y anade vibracion. Deberias sentir un zumbido en el labio. No la confundas con la "b" espanola.',
  example_words = '["van","five","give","very","love","have","voice","leave","over","seven"]'
WHERE symbol = 'v';

-- /θ/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa dental sorda como en "think". Similar a la "z" castellana de "zapato".',
  mouth_position = 'La punta de la lengua entre los dientes o justo detras de los dientes superiores, el aire fluye sobre ella.',
  tips = 'Pon la lengua entre los dientes y sopla sin voz. Si hablas castellano, es similar a la "z" de "caza". Practica "think", "three", "bath".',
  example_words = '["think","three","bath","thin","thick","thank","teeth","north","both","month"]'
WHERE symbol = 'θ';

-- /ð/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa dental sonora como en "this". Similar a la "d" suave espanola entre vocales en "cada".',
  mouth_position = 'La punta de la lengua entre los dientes o justo detras de los dientes superiores, con vibracion de cuerdas vocales.',
  tips = 'Como /θ/ pero con vibracion. Parecida a la "d" entre vocales en espanol (como en "nada"). Practica "this", "that", "mother".',
  example_words = '["this","that","mother","the","then","there","other","with","father","together"]'
WHERE symbol = 'ð';

-- /s/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa alveolar sorda como en "see". Similar a la "s" espanola pero articulada en los alveolos.',
  mouth_position = 'Lengua cerca de la cresta alveolar, el aire silba por un hueco estrecho.',
  tips = 'Es muy parecida a la "s" espanola. Mantiene la lengua cerca del techo de la boca y silba como una serpiente. Practica "see", "sit", "miss".',
  example_words = '["see","sit","miss","sun","six","same","face","place","class","ask"]'
WHERE symbol = 's';

-- /z/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa alveolar sonora como en "zoo". No existe como fonema independiente en espanol.',
  mouth_position = 'Lengua cerca de la cresta alveolar con vibracion de cuerdas vocales, el aire zumba por un hueco estrecho.',
  tips = 'Como la /s/ pero con vibracion. Deberia zumbar como una abeja. En espanol la "s" entre vocales se sonoriza a veces, pero no es un fonema separado. Practica "zoo", "buzz", "his".',
  example_words = '["zoo","buzz","his","zero","nose","music","easy","close","please","reason"]'
WHERE symbol = 'z';

-- /ʃ/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa postalveolar sorda como en "she". Similar al sonido "sh" que usamos para pedir silencio.',
  mouth_position = 'Lengua ancha y elevada detras de la cresta alveolar, labios ligeramente redondeados.',
  tips = 'Redondea un poco los labios y haz un sonido "shh" como cuando pides silencio. Practica "she", "ship", "fish".',
  example_words = '["she","ship","fish","shop","show","shake","push","wash","cash","short"]'
WHERE symbol = 'ʃ';

-- /ʒ/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa postalveolar sonora como en "vision". No existe en espanol. Es como /ʃ/ pero con vibracion.',
  mouth_position = 'Lengua ancha y elevada detras de la cresta alveolar con vibracion de cuerdas vocales.',
  tips = 'Como el sonido "sh" pero con vibracion. Piensa en la "j" francesa de "je". Aparece en palabras como "measure", "pleasure", "vision".',
  example_words = '["vision","measure","pleasure","usual","treasure","decision","television","garage","massage","beige"]'
WHERE symbol = 'ʒ';

-- /h/ fricative
UPDATE phonemes SET
  description = 'Consonante fricativa glotal sorda como en "hat". Similar a la "j" suave espanola pero mucho mas suave, casi un soplo.',
  mouth_position = 'Boca abierta en la posicion de la vocal siguiente, el aire sale de la garganta.',
  tips = 'Simplemente exhala con la boca abierta, como empanando un espejo. Es mucho mas suave que la "j" espanola. Nunca es muda en ingles cuando se escribe. Practica "hat", "hot", "who".',
  example_words = '["hat","hot","who","he","her","have","home","help","hope","here"]'
WHERE symbol = 'h';

-- /tʃ/ affricate
UPDATE phonemes SET
  description = 'Consonante africada postalveolar sorda como en "church". Similar a la "ch" espanola de "chico".',
  mouth_position = 'La lengua empieza en la cresta alveolar como /t/ y se libera hacia la posicion de /ʃ/.',
  tips = 'Es muy similar a la "ch" espanola. Empieza con /t/ e inmediatamente desliza a /ʃ/. Practica "church", "watch", "match".',
  example_words = '["church","watch","match","chair","child","change","each","catch","much","teach"]'
WHERE symbol = 'tʃ';

-- /dʒ/ affricate
UPDATE phonemes SET
  description = 'Consonante africada postalveolar sonora como en "judge". No existe en espanol. Es como /tʃ/ pero con vibracion.',
  mouth_position = 'La lengua empieza en la cresta alveolar y se libera con vibracion.',
  tips = 'Como la "ch" espanola pero con vibracion. Piensa en la "j" de "jump" o la "g" de "George". Practica "judge", "age", "bridge".',
  example_words = '["judge","age","bridge","job","just","join","page","large","change","orange"]'
WHERE symbol = 'dʒ';

-- /m/ nasal
UPDATE phonemes SET
  description = 'Consonante nasal bilabial sonora como en "man". Identica a la "m" espanola.',
  mouth_position = 'Labios cerrados, el aire fluye por la nariz.',
  tips = 'Es identica a la "m" espanola. Cierra los labios y tararea; el sonido sale por la nariz. Practica "man", "some", "swim".',
  example_words = '["man","some","swim","make","more","name","come","time","home","room"]'
WHERE symbol = 'm';

-- /n/ nasal
UPDATE phonemes SET
  description = 'Consonante nasal alveolar sonora como en "no". Similar a la "n" espanola pero articulada en los alveolos.',
  mouth_position = 'La punta de la lengua en la cresta alveolar, el aire fluye por la nariz.',
  tips = 'Muy similar a la "n" espanola. Presiona la lengua detras de los dientes superiores y tararea por la nariz. Practica "no", "ten", "sun".',
  example_words = '["no","ten","sun","new","name","nice","one","any","been","done"]'
WHERE symbol = 'n';

-- /ŋ/ nasal
UPDATE phonemes SET
  description = 'Consonante nasal velar sonora como en "sing". Existe en espanol antes de "g/k" (como en "tengo") pero en ingles puede ir al final de palabra.',
  mouth_position = 'La parte posterior de la lengua contra el paladar blando, el aire fluye por la nariz.',
  tips = 'Es el sonido de la "n" en "tengo" o "banco" en espanol. La diferencia es que en ingles puede terminar una palabra: "sing", "ring". Nunca anadas una "g" despues: no es "sing-g".',
  example_words = '["sing","ring","thing","long","song","king","bring","young","wrong","spring"]'
WHERE symbol = 'ŋ';

-- /l/ approximant
UPDATE phonemes SET
  description = 'Consonante lateral alveolar sonora como en "let". Similar a la "l" espanola pero con dos variantes: clara (inicio) y oscura (final).',
  mouth_position = 'La punta de la lengua toca la cresta alveolar, el aire fluye por los lados de la lengua.',
  tips = 'La "l" inicial es como la espanola. Pero la "l" final ("ball", "milk") es "oscura": la parte posterior de la lengua sube. Practica "let" (clara) vs "ball" (oscura).',
  example_words = '["let","ball","all","look","tell","feel","well","will","call","still"]'
WHERE symbol = 'l';

-- /r/ approximant
UPDATE phonemes SET
  description = 'Consonante aproximante alveolar sonora como en "red". Muy diferente de la "r" espanola. La lengua NO toca nada.',
  mouth_position = 'La punta de la lengua curvada ligeramente hacia atras, sin tocar el techo de la boca.',
  tips = 'La clave: la lengua NO vibra ni toca nada. Curva la punta hacia atras sin tocar el paladar. Redondea ligeramente los labios. Practica "red", "run", "right". Es el sonido mas dificil para hispanohablantes.',
  example_words = '["red","run","write","right","room","read","rest","great","three","very"]'
WHERE symbol = 'r';

-- /w/ approximant
UPDATE phonemes SET
  description = 'Consonante aproximante labio-velar sonora como en "we". Similar al sonido de la "u" espanola cuando va antes de vocal (como "huevo").',
  mouth_position = 'Labios fuertemente redondeados, parte posterior de la lengua elevada hacia el paladar blando.',
  tips = 'Redondea mucho los labios como para /uː/ y abrelos rapidamente hacia la vocal siguiente. Piensa en "hu" de "huevo". Practica "we", "wet", "win".',
  example_words = '["we","wet","win","way","want","will","well","with","water","world"]'
WHERE symbol = 'w';

-- /j/ approximant
UPDATE phonemes SET
  description = 'Consonante aproximante palatal sonora como en "yes". Identica a la "y" espanola en "yo" (en dialectos sin yeismo fuerte).',
  mouth_position = 'Lengua elevada alta y adelantada hacia el paladar duro, labios estirados.',
  tips = 'Es como la "y" espanola de "yo" pero mas suave, sin friccion. Eleva la lengua cerca del paladar y desliza a la vocal siguiente. Practica "yes", "you", "yet".',
  example_words = '["yes","you","yet","year","young","your","use","yellow","yard","yesterday"]'
WHERE symbol = 'j';

-- ============================================
-- SECTION 2: INSERT 660 new phrases (15 per phoneme)
-- 5 easy + 5 medium + 5 hard per phoneme
-- ============================================

-- /ɪ/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'The kid is sick', 'easy', '["kid","is","sick"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Pick a big gift', 'easy', '["pick","big","gift"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'His ship is thin', 'easy', '["his","ship","is","thin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Fill it with milk', 'easy', '["fill","it","with","milk"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Swim in the river', 'easy', '["swim","in","river"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'The twin sisters visited the city district this winter', 'medium', '["twin","sisters","visited","city","district","this","winter"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Billy quickly finished his dinner with a big grin', 'medium', '["Billy","quickly","finished","his","dinner","with","big","grin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Kristin lives in a little village with fifty families', 'medium', '["Kristin","lives","in","little","village","with","fifty","families"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Jim is beginning to think this business trip is risky', 'medium', '["Jim","is","beginning","think","this","business","trip","is","risky"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'The children insisted on visiting the swimming facility', 'medium', '["children","insisted","visiting","swimming","facility"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'In the distance the six thin chimneys were visible through the thick winter mist', 'hard', '["in","distance","six","thin","chimneys","visible","thick","winter","mist"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'The vividly written script depicted a thrilling incident in a fictitious district', 'hard', '["vividly","written","script","depicted","thrilling","incident","in","fictitious","district"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Fifty British citizens living in this vicinity witnessed the incident within minutes', 'hard', '["fifty","British","citizens","living","in","this","vicinity","witnessed","incident","within","minutes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'Tim''s sister quickly knitted six mittens with intricate stitching', 'hard', '["Tim''s","sister","quickly","knitted","six","mittens","with","intricate","stitching"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪ'), 'The linguistic institute insisted on switching to a different grading system this spring', 'hard', '["linguistic","institute","insisted","switching","different","grading","system","this","spring"]');

-- /e/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Get the red pen', 'easy', '["get","red","pen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Ten men left', 'easy', '["ten","men","left"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'The bed is wet', 'easy', '["bed","wet"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Lend me the rest', 'easy', '["lend","rest"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Let them help next', 'easy', '["let","them","help","next"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Every member of the team met at the entrance at seven', 'medium', '["every","member","met","entrance","seven"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Ben spent the rest of the weekend getting better at chess', 'medium', '["Ben","spent","rest","weekend","getting","better","chess"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'The pet vet helped the hen get well again', 'medium', '["pet","vet","helped","hen","get","well"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Ted sent several letters to his best friend Ken', 'medium', '["Ted","sent","several","letters","best","friend","Ken"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Jennifer mentioned that the next test deadline is Wednesday', 'medium', '["Jennifer","mentioned","next","test","deadline","Wednesday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'The elegant elderly gentleman entered the reception desk area very energetically and left eleven messages', 'hard', '["elegant","elderly","gentleman","entered","reception","desk","very","energetically","left","eleven","messages"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Ben expressed tremendous respect for the legendary detective and said every effort helped', 'hard', '["Ben","expressed","tremendous","respect","legendary","detective","said","every","effort","helped"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Several experts recommended better methods to prevent excessive spending on essential medical expenses', 'hard', '["several","experts","recommended","better","methods","prevent","excessive","spending","essential","medical","expenses"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'The self-educated engineer designed a better system to generate electricity from renewable elements', 'hard', '["self-educated","engineer","designed","better","system","generate","electricity","renewable","elements"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'e'), 'Frederick set the best record at the seventh annual chess federation event held in west Tennessee', 'hard', '["Frederick","set","best","record","seventh","annual","chess","federation","event","held","west","Tennessee"]');

-- /æ/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The fat cat sat', 'easy', '["fat","cat","sat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Grab that black bag', 'easy', '["grab","that","black","bag"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Dad had a nap', 'easy', '["dad","had","nap"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The map is flat', 'easy', '["map","flat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Clap and stand back', 'easy', '["clap","and","stand","back"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The angry man grabbed his backpack and ran fast across the track', 'medium', '["angry","man","grabbed","backpack","ran","fast","across","track"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'That happy family has a flat in a fancy part of Manhattan', 'medium', '["that","happy","family","has","flat","fancy","Manhattan"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Sam had a bad habit of snacking on candy and crackers', 'medium', '["Sam","had","bad","habit","snacking","candy","and","crackers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The black cat sat on a mat and scratched at the lamp', 'medium', '["black","cat","sat","mat","and","scratched","at","lamp"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Janet planned to travel back to her cabin at the ranch', 'medium', '["Janet","planned","travel","back","cabin","at","ranch"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The anxious passenger rapidly grabbed the black travel bag and dashed back through the packed taxi stand', 'hard', '["anxious","passenger","rapidly","grabbed","black","travel","bag","dashed","back","packed","taxi","stand"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Captain Jackson commanded the cavalry battalion to advance past the canyon at maximum capacity', 'hard', '["Captain","Jackson","commanded","cavalry","battalion","advance","past","canyon","at","maximum","capacity"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Jasmine''s fantastic plan to expand the campus attracted massive attention at the annual management banquet', 'hard', '["Jasmine''s","fantastic","plan","expand","campus","attracted","massive","attention","at","annual","management","banquet"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'The passionate activist launched a campaign to ban the manufacturing of hazardous plastic packaging', 'hard', '["passionate","activist","launched","campaign","ban","manufacturing","hazardous","plastic","packaging"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'æ'), 'Patrick and Sandra clapped happily as the band played jazz and classical music at the Saturday gathering', 'hard', '["Patrick","and","Sandra","clapped","happily","as","band","played","jazz","and","classical","at","Saturday","gathering"]');

-- /ɒ/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Stop the clock', 'easy', '["stop","clock"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The box is hot', 'easy', '["box","hot"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Got a lot of pots', 'easy', '["got","lot","pots"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Bob lost his job', 'easy', '["Bob","lost","job"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Drop it on top', 'easy', '["drop","on","top"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Tom got a hot chocolate and trotted along the rocky dock', 'medium', '["Tom","got","hot","chocolate","trotted","along","rocky","dock"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The doctor stopped to watch the fox crossing the pond', 'medium', '["doctor","stopped","watch","fox","crossing","pond"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'John dropped the bottle of olive stock on the spot', 'medium', '["John","dropped","bottle","olive","stock","on","spot"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The dog got locked in the shop behind the clock tower', 'medium', '["dog","got","locked","shop","clock","tower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Robin forgot to stop at the crossing and caused a problem', 'medium', '["Robin","forgot","stop","at","crossing","caused","problem"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The prominent scholar acknowledged that the common topics in modern philosophy are not often properly documented', 'hard', '["prominent","scholar","acknowledged","common","topics","modern","philosophy","not","often","properly","documented"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Bob from Scotland spotted a fox trotting on top of the rock formation opposite the pond', 'hard', '["Bob","Scotland","spotted","fox","trotting","on","top","rock","formation","opposite","pond"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The popular soccer competition involved lots of strong contestants from across the continent', 'hard', '["popular","soccer","competition","involved","lots","strong","contestants","from","across","continent"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'The watchman logged the odd observations along the dockyards following the October robbery', 'hard', '["watchman","logged","odd","observations","along","dockyards","following","October","robbery"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɒ'), 'Oliver''s novel concept of modern politics shocked and rocked the conference audience on Monday', 'hard', '["Oliver''s","novel","concept","modern","politics","shocked","rocked","conference","audience","on","Monday"]');

-- /ʌ/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'Run in the sun', 'easy', '["run","sun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'A cup of nuts', 'easy', '["cup","nuts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'Cut the bun up', 'easy', '["cut","bun","up"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'Shut the front door', 'easy', '["shut","front"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The bus was stuck', 'easy', '["bus","stuck"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'My brother rushed to the bus but just missed the one he must catch', 'medium', '["brother","rushed","bus","but","just","must"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The young couple struggled to budget enough money for summer', 'medium', '["young","couple","struggled","budget","enough","money","summer"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'Duncan loves nothing but running under the sun every Sunday', 'medium', '["Duncan","loves","nothing","but","running","under","sun","Sunday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'My uncle just discovered a wonderful hunting club nearby', 'medium', '["uncle","just","discovered","wonderful","hunting","club"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The plumber reluctantly agreed to come on Monday to unblock the pump', 'medium', '["plumber","reluctantly","come","Monday","unblock","pump"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The utter frustration of the husband suddenly erupted into a troublesome discussion about the budget cuts', 'hard', '["utter","frustration","husband","suddenly","erupted","troublesome","discussion","about","budget","cuts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'Justin''s young cousin reluctantly confessed that her judgment about the company structure was fundamentally wrong', 'hard', '["Justin''s","young","cousin","reluctantly","confessed","judgment","about","company","structure","fundamentally"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'One hundred customers rushed to the public function when the annual summer fundraiser was announced', 'hard', '["one","hundred","customers","rushed","public","function","annual","summer","fundraiser"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The government published updated instructions but none of the customs officials understood much of the document', 'hard', '["government","published","updated","instructions","but","none","customs","understood","much","document"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʌ'), 'The judge bluntly discussed the injustice underlying the corrupt construction company''s conduct', 'hard', '["judge","bluntly","discussed","injustice","underlying","corrupt","construction","company''s","conduct"]');

-- /ʊ/ phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Look at the book', 'easy', '["look","book"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'A good cook stood', 'easy', '["good","cook","stood"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Push the wood in', 'easy', '["push","wood"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Put the hood on', 'easy', '["put","hood"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'She took a good look', 'easy', '["took","good","look"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The woman stood by the bushes and looked at the wooden footbridge', 'medium', '["woman","stood","bushes","looked","wooden","footbridge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Could you put the sugar and the cookbook on the shelf', 'medium', '["could","put","sugar","cookbook"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The butcher took a good look at the pudding and shook his head', 'medium', '["butcher","took","good","look","pudding","shook"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The good-looking cook pushed the wooden stool into the full kitchen', 'medium', '["good-looking","cook","pushed","wooden","stool","full","kitchen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The footballer looked at his foot and pulled the wool scarf over his hood', 'medium', '["footballer","looked","foot","pulled","wool","hood"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The bookworm who stood in the woollen pullover could barely fit through the full classroom doorway', 'hard', '["bookworm","stood","woollen","pullover","could","full","classroom","doorway"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Would the woman from the neighbourhood be good enough to take a look at the overlooked woodland footpath', 'hard', '["would","woman","neighbourhood","good","enough","look","overlooked","woodland","footpath"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The childhood cook mistook the sugar for the pudding and the good dessert was ruined by the blunder', 'hard', '["childhood","cook","mistook","sugar","pudding","good","dessert"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'The touring bushwalker took a good look through the logbook and understood that the full route was unsuitable', 'hard', '["touring","bushwalker","took","good","look","logbook","understood","full"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊ'), 'Whoever should have booked the woodland lodge overlooked the bulletin and it was fully occupied by the weekend', 'hard', '["whoever","should","booked","woodland","overlooked","bulletin","fully"]');

-- /ə/ schwa phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'About a problem', 'easy', '["about","problem"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'Open the other door', 'easy', '["open","other"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'A banana again', 'easy', '["banana","again"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'Today was pleasant', 'easy', '["today","pleasant"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The sofa is comfortable', 'easy', '["sofa","comfortable"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The woman opposite was amazed at the panorama from the balcony', 'medium', '["woman","opposite","amazed","at","panorama","from","balcony"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'A special occasion presented an opportunity for a celebration', 'medium', '["special","occasion","presented","opportunity","celebration"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'Amanda forgot about the appointment and arrived late again', 'medium', '["Amanda","forgot","about","appointment","arrived","again"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The professor''s proposal generated a considerable amount of discussion', 'medium', '["professor''s","proposal","generated","considerable","amount","discussion"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The official announced a memorable event at the local community centre', 'medium', '["official","announced","memorable","event","at","local","community","centre"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The particular combination of a reasonable proposal and a sustainable financial arrangement appealed to everyone at the conference', 'hard', '["particular","combination","reasonable","proposal","sustainable","financial","arrangement","appealed","everyone","at","conference"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'An anonymous gentleman presented a comparable analysis of the original economic situation at the annual forum', 'hard', '["anonymous","gentleman","presented","comparable","analysis","original","economic","situation","at","annual","forum"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'The professional photographer captured a phenomenal panorama of the botanical garden from an unusual position', 'hard', '["professional","photographer","captured","phenomenal","panorama","botanical","garden","from","unusual","position"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'Victoria was confident that the controversial proposal about the traditional curriculum would generate enormous opposition', 'hard', '["Victoria","confident","controversial","proposal","about","traditional","curriculum","generate","enormous","opposition"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ə'), 'A considerable number of international students participated in the academic competition organised by the educational association', 'hard', '["considerable","number","international","students","participated","academic","competition","organised","educational","association"]');

-- /iː/ long vowel phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'See the green tree', 'easy', '["see","green","tree"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'She needs to eat', 'easy', '["she","needs","eat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Keep the tea clean', 'easy', '["keep","tea","clean"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'He reads each week', 'easy', '["he","reads","each","week"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Feel free to leave', 'easy', '["feel","free","leave"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Steve believes the team needs to meet at least three times each season', 'medium', '["Steve","believes","team","needs","meet","least","three","each","season"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Please teach me to read these easy Greek pieces', 'medium', '["please","teach","me","read","these","easy","Greek","pieces"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'We agreed to keep the beach clean and free from debris', 'medium', '["we","agreed","keep","beach","clean","free","debris"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Peter seems eager to succeed and achieve his dream of competing overseas', 'medium', '["Peter","seems","eager","succeed","achieve","dream","competing","overseas"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Lee''s speech deeply pleased the eager teachers at the evening meeting', 'medium', '["Lee''s","speech","deeply","pleased","eager","teachers","evening","meeting"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'The extremely eager team achieved an unbelievable feat of engineering by completing the breathtaking steel cathedral', 'hard', '["extremely","eager","team","achieved","unbelievable","feat","engineering","completing","breathtaking","steel","cathedral"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Three Vietnamese researchers recently received prestigious academic achievements in the field of marine ecology', 'hard', '["three","Vietnamese","researchers","recently","received","prestigious","academic","achievements","field","marine","ecology"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'The teachers agreed to restructure the evening reading programme and speed up the teaching of key themes', 'hard', '["teachers","agreed","restructure","evening","reading","programme","speed","teaching","key","themes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Neither team succeeded in defeating the unbeaten league leaders even after three consecutive seasons', 'hard', '["neither","team","succeeded","defeating","unbeaten","league","leaders","even","three","consecutive","seasons"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'iː'), 'Chiefly the reason she agreed to leave was the extreme need to be nearer the seaside retreat', 'hard', '["chiefly","reason","she","agreed","leave","extreme","need","be","nearer","seaside","retreat"]');

-- /ɑː/ long vowel phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The car is far', 'easy', '["car","far"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'A dark star shines', 'easy', '["dark","star"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Park the cart here', 'easy', '["park","cart"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Art class starts now', 'easy', '["art","class","starts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Her heart is large', 'easy', '["heart","large"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Father parked the large car far from the market in the dark', 'medium', '["father","parked","large","car","far","market","dark"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Charles started his afternoon art class after a bath in the garden', 'medium', '["Charles","started","afternoon","art","class","after","bath","garden"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The farmer''s barn is past the arch and partly hidden by tall grass', 'medium', '["farmer''s","barn","past","arch","partly","grass"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Martha asked the bartender for a large glass of sparkling water', 'medium', '["Martha","asked","bartender","large","glass","sparkling"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The guards marched smartly past the castle after dark', 'medium', '["guards","marched","smartly","past","castle","after","dark"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The charming artist from Argentina started a remarkable partnership with a large pharmaceutical company', 'hard', '["charming","artist","Argentina","started","remarkable","partnership","large","pharmaceutical"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Father''s avant-garde approach to architecture alarmed the conservative partners at the start of the last quarter', 'hard', '["father''s","avant-garde","approach","architecture","alarmed","conservative","partners","start","last","quarter"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The harvest dance at the farm lasted past midnight and the participants departed in their large fast cars', 'hard', '["harvest","dance","farm","lasted","past","midnight","participants","departed","large","fast","cars"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'Margaret asked Martin to park the dark car far past the garden arch near the drama department', 'hard', '["Margaret","asked","Martin","park","dark","car","far","past","garden","arch","drama","department"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɑː'), 'The disastrous aftermath of the fire alarmed the staff and sparked a sharp debate about disaster preparedness at the branch', 'hard', '["disastrous","aftermath","alarmed","staff","sparked","sharp","debate","disaster","branch"]');

-- /ɔː/ long vowel phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'Call before four', 'easy', '["call","before","four"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'Walk to the door', 'easy', '["walk","door"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'All the tall walls', 'easy', '["all","tall","walls"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'More water for Paul', 'easy', '["more","water","for","Paul"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'Draw a small horse', 'easy', '["draw","small","horse"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'Paul saw all the tall horses walking towards the shore before dawn', 'medium', '["Paul","saw","all","tall","horses","walking","towards","shore","before","dawn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'The tall daughter called for more warm water from the fourth floor', 'medium', '["tall","daughter","called","more","warm","water","fourth","floor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'Laura taught the small children to draw on the board all morning', 'medium', '["Laura","taught","small","children","draw","board","all","morning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'George walked across the lawn towards the store to order more chalk', 'medium', '["George","walked","across","lawn","towards","store","order","more","chalk"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'The auditorium floor was adorned with imported ornamental straw', 'medium', '["auditorium","floor","adorned","imported","ornamental","straw"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'According to the court order all employees performing poorly ought to report to the fourth floor hall before autumn', 'hard', '["according","court","order","all","performing","poorly","ought","report","fourth","floor","hall","before","autumn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'The appalling law reform caused enormous outrage and forced all supporters to withdraw their endorsement', 'hard', '["appalling","law","reform","caused","enormous","outrage","forced","all","supporters","withdraw","endorsement"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'George bought forty ornate doors and installed them across all four corridors of the restored historical hall', 'hard', '["George","bought","forty","ornate","doors","installed","across","all","four","corridors","restored","historical","hall"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'All morning the exhausted explorers walked along the shore calling for the lost horse before the storm caught them', 'hard', '["all","morning","exhausted","explorers","walked","along","shore","calling","lost","horse","before","storm","caught"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔː'), 'The thoughtful author recorded forty extraordinary stories from small war-torn southern European port towns', 'hard', '["thoughtful","author","recorded","forty","extraordinary","stories","from","small","war-torn","southern","European","port","towns"]');

-- /uː/ long vowel phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'A cool blue moon', 'easy', '["cool","blue","moon"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Choose a new shoe', 'easy', '["choose","new","shoe"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Food in the room', 'easy', '["food","room"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Move to the pool', 'easy', '["move","pool"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The fruit juice is smooth', 'easy', '["fruit","juice","smooth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The group moved through the huge room to view the new blue pool', 'medium', '["group","moved","through","huge","room","view","new","blue","pool"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Susan used two spoons to scoop the cool fruit soup', 'medium', '["Susan","used","two","spoons","scoop","cool","fruit","soup"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Luke proved that choosing smooth music truly improves the mood of the studio crew', 'medium', '["Luke","proved","choosing","smooth","music","truly","improves","mood","studio","crew"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The school reviewed the new rules and introduced a cool uniform for the students', 'medium', '["school","reviewed","new","rules","introduced","cool","uniform","students"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The youth group flew to Peru in June to volunteer at a rural school', 'medium', '["youth","group","flew","Peru","June","volunteer","rural","school"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The shrewd producer recruited a group of enthusiastic youths to film a documentary at a secluded lagoon in June', 'hard', '["shrewd","producer","recruited","group","enthusiastic","youths","film","documentary","secluded","lagoon","June"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The crew of two hundred and two sailed through a huge monsoon pursuing a new route to the lagoon in Cancun', 'hard', '["crew","two","hundred","two","sailed","through","huge","monsoon","pursuing","new","route","lagoon","Cancun"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The group of students eagerly consumed the new smooth fruit juice that Ruth brewed in the cool blue kitchen', 'hard', '["group","students","eagerly","consumed","new","smooth","fruit","juice","Ruth","brewed","cool","blue","kitchen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'The rude reviewer refused to approve the new musical production even though the troupe performed beautifully at the debut', 'hard', '["rude","reviewer","refused","approve","new","musical","production","troupe","performed","beautifully","debut"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'uː'), 'Two new gourmet food institutions opened in the huge commune providing exquisite fruit smoothies and fusion soups to enthused customers', 'hard', '["two","new","gourmet","food","institutions","huge","commune","exquisite","fruit","smoothies","fusion","soups","enthused"]');

-- /ɜː/ long vowel phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Her first word', 'easy', '["her","first","word"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The bird turns left', 'easy', '["bird","turns"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Learn the new verb', 'easy', '["learn","verb"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The nurse works early', 'easy', '["nurse","works","early"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The girl earned a shirt', 'easy', '["girl","earned","shirt"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The German nurse learned thirty new words on her first birthday journey', 'medium', '["German","nurse","learned","thirty","words","her","first","birthday","journey"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Shirley returned to work early after the church service on Thursday', 'medium', '["Shirley","returned","work","early","church","service","Thursday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The workers certainly earned their first award this term for their research', 'medium', '["workers","certainly","earned","first","award","term","research"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'A girl with curly hair stirred the herbs in the earthen bowl', 'medium', '["girl","curly","hair","stirred","herbs","earthen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The world-turning research confirmed that early birds perform worse in certain nocturnal searches', 'medium', '["world-turning","research","confirmed","early","birds","perform","worse","certain","nocturnal","searches"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The determined journalist thoroughly searched for the earliest version of the controversial thirty-word journal excerpt', 'hard', '["determined","journalist","thoroughly","searched","earliest","version","controversial","thirty-word","journal","excerpt"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Fern''s attorney personally confirmed that the concerning circumstances surrounding the firm''s merger were worse than first observed', 'hard', '["Fern''s","attorney","personally","confirmed","concerning","circumstances","surrounding","firm''s","merger","worse","first","observed"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Nurse Turner urgently returned to serve the thirteen thirsty German bird-watchers who were searching the preserve', 'hard', '["nurse","Turner","urgently","returned","serve","thirteen","thirsty","German","bird-watchers","searching","preserve"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'The early bird certainly earned its worth by learning to turn and curve before the entire flock stirred', 'hard', '["early","bird","certainly","earned","worth","learning","turn","curve","before","entire","flock","stirred"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɜː'), 'Her research concerning the earth''s surface further confirmed a disturbing pattern in terms of thermal energy dispersal', 'hard', '["her","research","concerning","earth''s","surface","further","confirmed","disturbing","pattern","terms","thermal","energy","dispersal"]');

-- /eɪ/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'A great day to play', 'easy', '["great","day","play"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Take the late train', 'easy', '["take","late","train"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Stay and wait here', 'easy', '["stay","wait"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Say your name today', 'easy', '["say","name","today"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Make a paper snake', 'easy', '["make","paper","snake"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Nathan waited at the railway station for a late train to take him away for the day', 'medium', '["Nathan","waited","railway","station","late","train","take","away","day"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The neighbours always complain when they raise the volume late at night on weekdays', 'medium', '["neighbours","always","complain","raise","late","at","weekdays"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Kate explained the basics of painting grey landscapes with acrylic paints', 'medium', '["Kate","explained","basics","painting","grey","landscapes","paints"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The brave teenager saved his classmate from the dangerous waves at the lake', 'medium', '["brave","teenager","saved","classmate","dangerous","waves","lake"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Spain remained Jane''s favourite holiday destination for a birthday getaway', 'medium', '["Spain","remained","Jane''s","favourite","holiday","destination","birthday","getaway"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The trade delegation arranged to display their latest innovation in sustainable paper-based packaging at the annual trade fair in May', 'hard', '["trade","delegation","arranged","display","latest","innovation","sustainable","paper-based","packaging","annual","trade","fair","May"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The acclaimed painter David Wayne displayed eighty amazing paintings portraying landscapes of ancient Mediterranean bays and gateways', 'hard', '["acclaimed","painter","David","Wayne","displayed","eighty","amazing","paintings","portraying","landscapes","ancient","Mediterranean","bays","gateways"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The campaign to save the endangered marine mammals gained amazing support when famous players raised awareness', 'hard', '["campaign","save","endangered","marine","mammals","gained","amazing","famous","players","raised","awareness"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'The database contained detailed data relating to the patient''s daily intake of medication throughout the eight-day hospital stay', 'hard', '["database","contained","detailed","data","relating","patient''s","daily","intake","medication","eight-day","hospital","stay"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eɪ'), 'Grace made arrangements to delay the Wednesday celebration and rearranged the table placement for eighty-eight neighbouring estates', 'hard', '["Grace","made","arrangements","delay","Wednesday","celebration","rearranged","table","placement","eighty-eight","neighbouring","estates"]');

-- /aɪ/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'My time to fly high', 'easy', '["my","time","fly","high"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Try the nice pie', 'easy', '["try","nice","pie"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Five white mice hide', 'easy', '["five","white","mice","hide"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'I like to ride bikes', 'easy', '["I","like","ride","bikes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Write a line tonight', 'easy', '["write","line","tonight"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Simon decided to climb the high mountain despite the icy wind', 'medium', '["Simon","decided","climb","high","mountain","despite","icy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'I quite like arriving by bicycle on a fine Friday night', 'medium', '["I","quite","like","arriving","bicycle","fine","Friday","night"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'The shy child tried to hide behind the pile of tyres by the side', 'medium', '["shy","child","tried","hide","behind","pile","tyres","side"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'My wife designed a stylish dining room by combining five bright lights', 'medium', '["my","wife","designed","stylish","dining","combining","five","bright","lights"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'The drivers finally arrived at the island site quite tired and slightly behind time', 'medium', '["drivers","finally","arrived","island","site","quite","tired","slightly","behind","time"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'The enterprising scientist decided to analyse the surprising findings outlined in five highly classified guidelines', 'hard', '["enterprising","scientist","decided","analyse","surprising","findings","outlined","five","highly","classified","guidelines"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Despite the frightening lightning strike on Friday night the firefighters strived valiantly to revive the vital supply lines', 'hard', '["despite","frightening","lightning","strike","Friday","night","firefighters","strived","valiantly","revive","vital","supply","lines"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'The highly motivated climbers survived a harrowing nine-mile hike along the icy divide while fighting the biting wind', 'hard', '["highly","motivated","climbers","survived","harrowing","nine-mile","hike","along","icy","divide","while","fighting","biting","wind"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'I admire the fine white ivory designs that the Thai artist outlined in his prize-winning five-part exhibit', 'hard', '["I","admire","fine","white","ivory","designs","Thai","artist","outlined","prize-winning","five-part","exhibit"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aɪ'), 'Tyler described a frightening encounter with a tiger while driving by the uninhabited wild side of the island at twilight', 'hard', '["Tyler","described","frightening","encounter","tiger","while","driving","uninhabited","wild","side","island","twilight"]');

-- /ɔɪ/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The boy enjoys toys', 'easy', '["boy","enjoys","toys"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Oil the noisy joint', 'easy', '["oil","noisy","joint"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'A coin for joy', 'easy', '["coin","joy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Join the royal point', 'easy', '["join","royal","point"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Avoid the moist soil', 'easy', '["avoid","moist","soil"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The annoyed boy employed a ploy to avoid doing his chores and enjoyed the toy instead', 'medium', '["annoyed","boy","employed","ploy","avoid","doing","enjoyed","toy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Roy pointed to the coin and said the joyful voyage was his choice', 'medium', '["Roy","pointed","coin","said","joyful","voyage","choice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Joyce was disappointed by the noisy boiler that destroyed her enjoyment of the evening', 'medium', '["Joyce","disappointed","noisy","boiler","destroyed","enjoyment","evening"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The loyal employee voiced his annoyance at the noisy construction adjoining his office', 'medium', '["loyal","employee","voiced","annoyance","noisy","construction","adjoining"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The oyster farmer in Illinois exploited the moist soil and employed boys from the township', 'medium', '["oyster","farmer","Illinois","exploited","moist","soil","employed","boys","township"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The overjoyed royal family enjoyed the noisy celebration with the loyal employees who had toiled to prepare the voyage', 'hard', '["overjoyed","royal","family","enjoyed","noisy","celebration","loyal","employees","toiled","prepare","voyage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The disappointed boy exploited every loophole in the employment contract to avoid the pointless royal appointment', 'hard', '["disappointed","boy","exploited","employment","contract","avoid","pointless","royal","appointment"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'Joyce rejoiced at the appointment and pointed out that the choice of venue showcased the finest examples of Detroit''s architecture', 'hard', '["Joyce","rejoiced","appointment","pointed","choice","venue","showcased","finest","Detroit''s","architecture"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The moisture in the soil destroyed the poison ivy and the oyster beds adjoining the point were subsequently enjoyed by tourists', 'hard', '["moisture","soil","destroyed","poison","ivy","oyster","beds","adjoining","point","subsequently","enjoyed","tourists"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɔɪ'), 'The annoying noise from the boiling oil caused such a disturbance that loyal employees voiced their joint boycott of the employer', 'hard', '["annoying","noise","boiling","oil","caused","disturbance","loyal","employees","voiced","joint","boycott","employer"]');

-- /əʊ/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Go home alone', 'easy', '["go","home","alone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Show me the road', 'easy', '["show","road"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'The old boat floats', 'easy', '["old","boat","floats"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Cold snow and stone', 'easy', '["cold","snow","stone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Hold the phone close', 'easy', '["hold","phone","close"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Joe drove slowly along the coastal road but almost lost control in the snow', 'medium', '["Joe","drove","slowly","along","coastal","road","almost","lost","control","snow"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'The whole programme showed how roses grow in zones with low rainfall', 'medium', '["whole","programme","showed","roses","grow","zones","low"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Nobody knows who stole the golden trophy from the owner''s home', 'medium', '["nobody","knows","stole","golden","trophy","owner''s","home"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'She was told to postpone the opening of the showroom on Monday', 'medium', '["told","postpone","opening","showroom","Monday"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Owen spoke openly about his hope to compose a solo for the local folk show', 'medium', '["Owen","spoke","openly","hope","compose","solo","local","folk","show"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'The promotional brochure boasted that the coastal hotel''s remote location offered the most wholesome and noble experience known to globetrotters', 'hard', '["promotional","brochure","boasted","coastal","hotel''s","remote","location","offered","most","wholesome","noble","known","globetrotters"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Notably the solo vocalist chose an emotional tone to invoke the nostalgia of broken hopes and stolen moments of old', 'hard', '["notably","solo","vocalist","chose","emotional","tone","invoke","nostalgia","broken","hopes","stolen","moments","old"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'The whole approach to postponing the global conference provoked outspoken opposition from those closely devoted to the old protocol', 'hard', '["whole","approach","postponing","global","conference","provoked","outspoken","opposition","those","closely","devoted","old","protocol"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'Joseph composed a slow folk song devoted to the lonely souls who roam the open roads of remote coastal zones', 'hard', '["Joseph","composed","slow","folk","song","devoted","lonely","souls","roam","open","roads","remote","coastal","zones"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'əʊ'), 'The closely controlled explosive demolition programme disclosed enormous overspending and exposed a grossly overlooked structural flaw below the dome', 'hard', '["closely","controlled","explosive","demolition","programme","disclosed","enormous","overspending","exposed","grossly","overlooked","structural","flaw","below","dome"]');

-- /aʊ/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Out of the house now', 'easy', '["out","house","now"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Count down from ten', 'easy', '["count","down"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'A loud sound outside', 'easy', '["loud","sound","outside"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The brown cow sat down', 'easy', '["brown","cow","down"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Found around the town', 'easy', '["found","around","town"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The crowd shouted loudly outside the council house in the south of town', 'medium', '["crowd","shouted","loudly","outside","council","house","south","town"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Howard found a thousand round brown flowers growing around the fountain', 'medium', '["Howard","found","thousand","round","brown","flowers","around","fountain"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The coward cowered at the howling sound of the hound outside the tower', 'medium', '["coward","cowered","howling","sound","hound","outside","tower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Our accountant frowned and announced that the outstanding amounts were doubtful', 'medium', '["our","accountant","frowned","announced","outstanding","amounts","doubtful"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The powerful drought throughout the county caused thousands of flowers to shrivel', 'medium', '["powerful","drought","throughout","county","caused","thousands","flowers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The outspoken council member loudly denounced the outrageous amounts of taxpayer pounds being spent on the controversial roundabout downtown', 'hard', '["outspoken","council","loudly","denounced","outrageous","amounts","taxpayer","pounds","controversial","roundabout","downtown"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Howard found himself surrounded by howling crowds outside the courthouse in the south of the overcrowded county town', 'hard', '["Howard","found","surrounded","howling","crowds","outside","courthouse","south","overcrowded","county","town"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'Without doubt the outstanding amount of groundwater found around the mountain boundaries astounded the renowned scouts', 'hard', '["without","doubt","outstanding","amount","groundwater","found","around","mountain","boundaries","astounded","renowned","scouts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The accountant proudly announced that our foundation had raised a staggering amount for the community outreach programme downtown', 'hard', '["accountant","proudly","announced","our","foundation","amount","community","outreach","programme","downtown"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'aʊ'), 'The mouse in our house somehow found its way around the couch and out through the brown wooden flowerbox by the tower', 'hard', '["mouse","our","house","somehow","found","around","couch","out","through","brown","wooden","flowerbox","tower"]');

-- /ɪə/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Come near and hear', 'easy', '["near","hear"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Clear beer is here', 'easy', '["clear","beer","here"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Fear the fierce deer', 'easy', '["fear","fierce","deer"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'A weird idea appeared', 'easy', '["weird","idea","appeared"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'The ear is near', 'easy', '["ear","near"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'It appears that the engineers cleared the area near the pier without interference', 'medium', '["appears","engineers","cleared","area","near","pier","interference"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'The cheerful volunteer feared that the weird atmosphere here would interfere with the premiere', 'medium', '["cheerful","volunteer","feared","weird","atmosphere","here","interfere","premiere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'We merely appeared to have been steering near the rear of the clearing', 'medium', '["merely","appeared","steering","near","rear","clearing"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'The deer disappeared into the sheer forest near the weir last year', 'medium', '["deer","disappeared","sheer","forest","near","weir","year"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Ian''s career experience here spans nearly thirty years of pioneering research', 'medium', '["Ian''s","career","experience","here","nearly","years","pioneering","research"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'The atmospheric interference mysteriously appeared near the engineering sphere and severely altered the pioneer''s experiments here', 'hard', '["atmospheric","interference","mysteriously","appeared","near","engineering","sphere","severely","altered","pioneer''s","experiments","here"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'The fierce volunteer steered clear of the eerie pier where a weird experience had interfered with her career', 'hard', '["fierce","volunteer","steered","clear","eerie","pier","weird","experience","interfered","career"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'It became increasingly clear that a severe theatre premiere near the frontier created tremendous fear among the sincere engineers', 'hard', '["increasingly","clear","severe","theatre","premiere","near","frontier","created","tremendous","fear","among","sincere","engineers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Year after year the pioneering researchers persevered and appeared to have nearly solved the endearing mystery of the biosphere', 'hard', '["year","after","year","pioneering","researchers","persevered","appeared","nearly","solved","endearing","mystery","biosphere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ɪə'), 'Merely hearing the eerie cheering near the clearing triggered a fierce reaction and severe interference with the theatrical experience here', 'hard', '["merely","hearing","eerie","cheering","near","clearing","triggered","fierce","reaction","severe","interference","theatrical","experience","here"]');

-- /eə/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Where is the fair', 'easy', '["where","fair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Take care up there', 'easy', '["care","there"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'The chair is bare', 'easy', '["chair","bare"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Share the pear fairly', 'easy', '["share","pear","fairly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Wear your hair down', 'easy', '["wear","hair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Mary prepared to share her spare chair with the fair-haired pair', 'medium', '["Mary","prepared","share","spare","chair","fair-haired","pair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'There was a glare from the bare staircase where the rare painting was displayed', 'medium', '["there","glare","bare","staircase","where","rare","painting"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Claire declared that the air fare was unfair compared to elsewhere', 'medium', '["Claire","declared","air","fare","unfair","compared","elsewhere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'The hare stared and dared to go where the bear''s lair was barely visible', 'medium', '["hare","stared","dared","where","bear''s","lair","barely"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Their nightmare involved a dare to repair the stairs without wearing any gear', 'medium', '["their","nightmare","dare","repair","stairs","wearing","gear"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'The millionaire declared that comparing air fares for various carriers was necessary before their heir''s departure anywhere', 'hard', '["millionaire","declared","comparing","air","fares","various","carriers","necessary","heir''s","departure","anywhere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Claire prepared to share the remarkable welfare initiative she had carefully designed to ensure fairer healthcare everywhere', 'hard', '["Claire","prepared","share","remarkable","welfare","initiative","carefully","designed","ensure","fairer","healthcare","everywhere"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'The mayor''s extraordinary affair barely survived the glaring stares of the taxpayers who dared to compare his unfair decisions', 'hard', '["mayor''s","extraordinary","affair","barely","survived","glaring","stares","taxpayers","dared","compare","unfair","decisions"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Where there is fresh air and sparse bare landscape the rare square-shaped prairie hares are comparatively aware of their surroundings', 'hard', '["where","there","fresh","air","sparse","bare","landscape","rare","square-shaped","prairie","hares","comparatively","aware","surroundings"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'eə'), 'Their parents carefully prepared a farewell prayer before declaring that the fair at the square was beyond compare anywhere', 'hard', '["their","parents","carefully","prepared","farewell","prayer","declaring","fair","square","beyond","compare","anywhere"]');

-- /ʊə/ diphthong phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'A pure cure for sure', 'easy', '["pure","cure","sure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The poor moor is dull', 'easy', '["poor","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'Tour the rural farm', 'easy', '["tour","rural"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'A lure for the tourist', 'easy', '["lure","tourist"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'Endure the cold moor', 'easy', '["endure","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The tourists were lured to the rural moor by the allure of a pure natural cure', 'medium', '["tourists","lured","rural","moor","allure","pure","natural","cure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'We toured the secure premises to ensure the poor ventilation was cured', 'medium', '["toured","secure","premises","ensure","poor","ventilation","cured"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The mature student endured the poor conditions on the rural tour', 'medium', '["mature","student","endured","poor","conditions","rural","tour"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'She ensured a pure supply of mineral water during the detour through the moor', 'medium', '["ensured","pure","supply","during","detour","through","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The insurance company was unsure about securing the tour against poor weather', 'medium', '["insurance","unsure","securing","tour","poor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The obscure rural touring company endured considerable pressure to procure a pure source of sustainable fuel from the impoverished moor', 'hard', '["obscure","rural","touring","company","endured","considerable","pressure","procure","pure","source","sustainable","fuel","impoverished","moor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The mature entrepreneur ensured a secure and enduring cure for the poor infrastructure plaguing the rural tourism industry', 'hard', '["mature","entrepreneur","ensured","secure","enduring","cure","poor","infrastructure","plaguing","rural","tourism","industry"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The insurance procurement jury was unsure whether the poor conditions on the moor could endure another plural series of storms', 'hard', '["insurance","procurement","jury","unsure","poor","conditions","moor","endure","plural","series","storms"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'During the grand tour the curious tourists marvelled at the allure of the pure blue waters surrounding the secure moorland reserve', 'hard', '["during","grand","tour","curious","tourists","marvelled","allure","pure","blue","surrounding","secure","moorland","reserve"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʊə'), 'The furious detour through the moor ensured a poor experience but the enduring beauty of the purely natural landscape cured their discontent', 'hard', '["furious","detour","through","moor","ensured","poor","experience","enduring","beauty","purely","natural","landscape","cured","discontent"]');

-- /p/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Put the pen up top', 'easy', '["put","pen","up","top"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Pass the pepper please', 'easy', '["pass","pepper","please"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'A pretty purple cap', 'easy', '["pretty","purple","cap"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Snap the paper apart', 'easy', '["snap","paper","apart"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Stop and pick it up', 'easy', '["stop","pick","up"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Peter prepared a special platter of pasta with peppers and parsley', 'medium', '["Peter","prepared","special","platter","pasta","peppers","parsley"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'The pilot promptly put the plane on the proper path to the airport', 'medium', '["pilot","promptly","put","plane","proper","path","airport"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Perhaps the popular programme will inspire more people to participate', 'medium', '["perhaps","popular","programme","inspire","people","participate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Priscilla politely proposed to postpone the important presentation', 'medium', '["Priscilla","politely","proposed","postpone","important","presentation"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'The plumber promptly repaired the pipe and replaced the pump', 'medium', '["plumber","promptly","repaired","pipe","replaced","pump"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'The presidential spokesperson publicly promoted the proposed policy platform as the most popular option for national prosperity', 'hard', '["presidential","spokesperson","publicly","promoted","proposed","policy","platform","popular","option","national","prosperity"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Peter''s impressive presentation persuasively depicted the positive impact of proper preparation and persistent participation in the professional programme', 'hard', '["Peter''s","impressive","presentation","persuasively","depicted","positive","impact","proper","preparation","persistent","participation","professional","programme"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'The experimental pilot project incorporated improvements to the processing platform that permanently prevented repetitive problems', 'hard', '["experimental","pilot","project","incorporated","improvements","processing","platform","permanently","prevented","repetitive","problems"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'The public repeatedly expressed appreciation for the pamphlet explaining the practical approach to personal and professional development', 'hard', '["public","repeatedly","expressed","appreciation","pamphlet","explaining","practical","approach","personal","professional","development"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'p'), 'Philippe rapidly prepared multiple separate portions of the spectacular pumpkin soup as part of the special prize-winning recipe competition', 'hard', '["Philippe","rapidly","prepared","multiple","separate","portions","spectacular","pumpkin","soup","part","special","prize-winning","recipe","competition"]');

-- /b/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'A big blue ball', 'easy', '["big","blue","ball"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Bob brought bread', 'easy', '["Bob","brought","bread"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'The baby bit the bib', 'easy', '["baby","bit","bib"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Break the brown box', 'easy', '["break","brown","box"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Buy black boots below', 'easy', '["buy","black","boots","below"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Barbara grabbed a bunch of blueberries from the bush behind the barn', 'medium', '["Barbara","grabbed","bunch","blueberries","bush","behind","barn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'The neighbours began building a beautiful basketball court in the back', 'medium', '["neighbours","began","building","beautiful","basketball","back"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Nobody doubted that the brilliant business was about to become a global brand', 'medium', '["nobody","doubted","brilliant","business","about","become","global","brand"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'The ambitious club members contributed to rebuilding the broken bridge', 'medium', '["ambitious","club","contributed","rebuilding","broken","bridge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Ben''s hobby was buying and browsing old books about submarines and battleships', 'medium', '["Ben''s","hobby","buying","browsing","books","about","submarines","battleships"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'The remarkable establishment subsequently absorbed a considerable number of ambitious job applicants from both urban and suburban backgrounds', 'hard', '["remarkable","establishment","subsequently","absorbed","considerable","number","ambitious","applicants","both","urban","suburban","backgrounds"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Barbara''s breathtaking debut album about heartbreak and betrayal became the best-selling publication in the global business of independent labels', 'hard', '["Barbara''s","breathtaking","debut","album","about","heartbreak","betrayal","became","best-selling","publication","global","business","labels"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Nobody at the debate objected when the cabinet member contributed a remarkable observation about abolishing the bureaucratic rubber-stamping', 'hard', '["nobody","debate","objected","cabinet","contributed","remarkable","observation","about","abolishing","bureaucratic","rubber-stamping"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'The abandoned laboratory building was observable from the nearby bluff where numberless blackbirds bobbed above the barbed-wire boundary', 'hard', '["abandoned","laboratory","building","observable","nearby","bluff","numberless","blackbirds","bobbed","above","barbed-wire","boundary"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'b'), 'Bob''s burdensome debts combined with unbearable problems eventually subsided once the suburb bank contributed substantial bailout benefits', 'hard', '["Bob''s","burdensome","debts","combined","unbearable","problems","subsided","suburb","bank","contributed","substantial","bailout","benefits"]');

-- /t/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Take it to the top', 'easy', '["take","it","to","top"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Tell the truth today', 'easy', '["tell","truth","today"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'A tall tree stands', 'easy', '["tall","tree","stands"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Cut the toast in two', 'easy', '["cut","toast","two"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Put that plate right there', 'easy', '["put","that","plate","right","there"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The student wanted to get better at sitting the written test twice', 'medium', '["student","wanted","get","better","at","sitting","written","test","twice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Timothy sat quietly at his private table and started to translate the text', 'medium', '["Timothy","sat","quietly","at","private","table","started","translate","text"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The talented artist created twenty attractive portraits during the art competition', 'medium', '["talented","artist","created","twenty","attractive","portraits","art","competition"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'That restaurant situated at the crossroads attracted tourists with its authentic Italian pasta', 'medium', '["that","restaurant","situated","at","crossroads","attracted","tourists","authentic","Italian","pasta"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'Too much attention was put into the tentative attempt to meet the strict deadline', 'medium', '["too","much","attention","put","tentative","attempt","meet","strict","deadline"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The constitutionally elected government started to institute the latest international treaty after extensive tactical consultation with the cabinet', 'hard', '["constitutionally","elected","government","started","institute","latest","international","treaty","extensive","tactical","consultation","cabinet"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The architectural institute ultimately attracted tremendous interest with its outstanding twenty-chapter textbook on contemporary tropical structures', 'hard', '["architectural","institute","ultimately","attracted","tremendous","interest","outstanding","twenty-chapter","textbook","contemporary","tropical","structures"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'After the dramatic entertainment the meticulous committee treated the outstanding participants to a fantastic selection of traditional tarts', 'hard', '["after","dramatic","entertainment","meticulous","committee","treated","outstanding","participants","fantastic","selection","traditional","tarts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The strict controller hesitantly permitted the talented team to test their prototype at the institute without interruption', 'hard', '["strict","controller","hesitantly","permitted","talented","team","test","prototype","at","institute","without","interruption"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 't'), 'The district court instructed the detective to investigate the petty theft and systematically interrogate the suspected perpetrators at the station', 'hard', '["district","court","instructed","detective","investigate","petty","theft","systematically","interrogate","suspected","perpetrators","station"]');

-- /d/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Did dad drive today', 'easy', '["did","dad","drive"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The old red door', 'easy', '["old","red","door"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Dance and drink deeply', 'easy', '["dance","drink","deeply"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'A good deed is done', 'easy', '["good","deed","done"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The kids played outside', 'easy', '["kids","played","outside"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'David decided to address the delayed delivery and demanded a detailed explanation', 'medium', '["David","decided","address","delayed","delivery","demanded","detailed","explanation"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The dedicated doctor studied advanced medicine and demonstrated outstanding understanding', 'medium', '["dedicated","doctor","studied","advanced","medicine","demonstrated","outstanding","understanding"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Dozens of kids headed downtown to dance and celebrate the holidays', 'medium', '["dozens","kids","headed","downtown","dance","celebrate","holidays"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The damaged building stood abandoned beside the divided road', 'medium', '["damaged","building","stood","abandoned","beside","divided","road"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Diane discussed the disadvantages of the proposed deal and advised the board', 'medium', '["Diane","discussed","disadvantages","proposed","deal","advised","board"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The underdeveloped district demanded dedicated leadership and standardised education throughout the divided communities', 'hard', '["underdeveloped","district","demanded","dedicated","leadership","standardised","education","divided","communities"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Donald demonstrated extraordinary dedication by designing an advanced method to decode the encrypted data discovered underground', 'hard', '["Donald","demonstrated","extraordinary","dedication","designing","advanced","method","decode","encrypted","data","discovered","underground"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The distinguished director addressed the widespread disappointment and decided to redirect the budget towards underfunded departments', 'hard', '["distinguished","director","addressed","widespread","disappointment","decided","redirect","budget","towards","underfunded","departments"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'The standard deadline demanded by the administration displaced hundreds of dedicated individuals and disrupted established procedures', 'hard', '["standard","deadline","demanded","administration","displaced","hundreds","dedicated","individuals","disrupted","established","procedures"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'd'), 'Despite determined efforts the underfunded development fund struggled and the divided board demanded immediate additional worldwide assistance', 'hard', '["despite","determined","efforts","underfunded","development","fund","struggled","divided","board","demanded","immediate","additional","worldwide","assistance"]');

-- /k/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Keep the car clean', 'easy', '["keep","car","clean"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The black cat climbs', 'easy', '["black","cat","climbs"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Cook the cake quickly', 'easy', '["cook","cake","quickly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Come back to class', 'easy', '["come","back","class"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Pick the cold crackers', 'easy', '["pick","cold","crackers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The accidental discovery caused chaos in the chemical company''s core division', 'medium', '["accidental","discovery","caused","chaos","chemical","company''s","core"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Kathleen asked the mechanic to check the cracked components quickly', 'medium', '["Kathleen","asked","mechanic","check","cracked","components","quickly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The country club cooked a fantastic chicken casserole and crisp corn cakes', 'medium', '["country","club","cooked","fantastic","chicken","casserole","crisp","corn","cakes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Kate called the accountant to confirm the contract and the correct costs', 'medium', '["Kate","called","accountant","confirm","contract","correct","costs"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The construction company knocked down the crumbling brick kiosk across the market', 'medium', '["construction","company","knocked","crumbling","brick","kiosk","across","market"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The economic committee concluded that the critical lack of skilled technicians could significantly complicate the upcoming construction projects', 'hard', '["economic","committee","concluded","critical","lack","skilled","technicians","could","significantly","complicate","upcoming","construction","projects"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The accomplished chemist characteristically conducted the complex experiment with remarkable accuracy and uncompromising attention to chemical composition', 'hard', '["accomplished","chemist","characteristically","conducted","complex","experiment","remarkable","accuracy","uncompromising","attention","chemical","composition"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'Kristin''s incredible accomplishment in kickboxing attracted the attention of the country''s most decorated athletic commission', 'hard', '["Kristin''s","incredible","accomplishment","kickboxing","attracted","attention","country''s","decorated","athletic","commission"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The construction contractor quickly recalculated the cost of the critical structural components and cancelled the incorrect invoice', 'hard', '["construction","contractor","quickly","recalculated","cost","critical","structural","components","cancelled","incorrect","invoice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'k'), 'The accomplished classical conductor took the complex acoustic composition and conducted it with a charismatic and captivating technique', 'hard', '["accomplished","classical","conductor","took","complex","acoustic","composition","conducted","charismatic","captivating","technique"]');

-- /g/ plosive phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'A great green garden', 'easy', '["great","green","garden"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Go get the bag', 'easy', '["go","get","bag"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The big dog guards', 'easy', '["big","dog","guards"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Grab your glass and go', 'easy', '["grab","glass","go"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Good games for girls', 'easy', '["good","games","girls"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Greg''s biggest goal is gaining recognition as an organic vegetable grower', 'medium', '["Greg''s","biggest","goal","gaining","recognition","organic","vegetable","grower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The government agreed to guarantee the global agriculture programme', 'medium', '["government","agreed","guarantee","global","agriculture","programme"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Maggie eagerly grabbed the gorgeous golden ring from the gift bag', 'medium', '["Maggie","eagerly","grabbed","gorgeous","golden","ring","gift","bag"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The guide suggested going to the gorge through the grape gardens', 'medium', '["guide","suggested","going","gorge","through","grape","gardens"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Getting a good grade in geography requires great dialogue and group discussion', 'medium', '["getting","good","grade","geography","requires","great","dialogue","group","discussion"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The government gathering regarding the grim global energy gap generated vigorous disagreement among the geological engineers', 'hard', '["government","gathering","regarding","grim","global","energy","gap","generated","vigorous","disagreement","among","geological","engineers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The biggest agricultural group gladly agreed to guarantee a regular supply of organic ingredients to the growing chain of gourmet restaurants', 'hard', '["biggest","agricultural","group","gladly","agreed","guarantee","regular","supply","organic","ingredients","growing","gourmet","restaurants"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Greg''s greatest legacy in government was gaining global recognition for a ground-breaking programme targeting neglected geographic regions', 'hard', '["Greg''s","greatest","legacy","government","gaining","global","recognition","ground-breaking","programme","targeting","neglected","geographic","regions"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'The gorgeous gallery of golden figurines gathered together from across the globe was regarded as the most magnificent aggregation ever exhibited', 'hard', '["gorgeous","gallery","golden","figurines","gathered","together","from","across","globe","regarded","magnificent","aggregation","exhibited"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'g'), 'Margaret begged the government agency to regulate the grossly negligent garment manufacturers and guarantee dignified working conditions globally', 'hard', '["Margaret","begged","government","agency","regulate","grossly","negligent","garment","manufacturers","guarantee","dignified","globally"]');

-- /f/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'A fine fish for four', 'easy', '["fine","fish","for","four"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Feel free to find it', 'easy', '["feel","free","find"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The first leaf fell off', 'easy', '["first","leaf","fell","off"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Fill the flask full', 'easy', '["fill","flask","full"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Finish the food fast', 'easy', '["finish","food","fast"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The firefighter found fifteen frightened families fleeing from the fifth floor', 'medium', '["firefighter","found","fifteen","frightened","families","fleeing","from","fifth","floor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Frederick offered useful feedback after focusing on the professor''s new draft', 'medium', '["Frederick","offered","useful","feedback","focusing","professor''s","draft"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The fresh fruit from the farm satisfied the staff perfectly', 'medium', '["fresh","fruit","from","farm","satisfied","staff","perfectly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Fifty different official files were found in the office on the first floor', 'medium', '["fifty","different","official","files","found","office","first","floor"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Frances felt comfortable enough to confide in her faithful friend after the difficult affair', 'medium', '["Frances","felt","comfortable","confide","faithful","friend","difficult","affair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The influential professor confirmed her firm belief that effective conflict resolution offered a profoundly different future for the affected families', 'hard', '["influential","professor","confirmed","firm","belief","effective","conflict","offered","profoundly","different","future","affected","families"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The fearful officer offered insufficient justification for the offensive and fundamentally flawed enforcement effort', 'hard', '["fearful","officer","offered","insufficient","justification","offensive","fundamentally","flawed","enforcement","effort"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'Francis and Frederick profoundly influenced the final draft of the definitive manifesto for a fair and reformed welfare programme', 'hard', '["Francis","Frederick","profoundly","influenced","final","draft","definitive","manifesto","fair","reformed","welfare","programme"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'The certification office officially confirmed the successful fulfilment of fifteen fundamental safety factors affecting the refurbished facility', 'hard', '["certification","office","officially","confirmed","successful","fulfilment","fifteen","fundamental","safety","factors","affecting","refurbished","facility"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'f'), 'After significant efforts from the financial staff the difficult forthcoming reform finally offered sufficient fuel for further fruitful transformation', 'hard', '["after","significant","efforts","financial","staff","difficult","forthcoming","reform","finally","offered","sufficient","fuel","further","fruitful","transformation"]');

-- /v/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'A very vast valley', 'easy', '["very","vast","valley"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Five leaves arrived', 'easy', '["five","leaves","arrived"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Love every view you see', 'easy', '["love","every","view"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Drive over the bridge', 'easy', '["drive","over"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Give Steve eleven grapes', 'easy', '["give","Steve","eleven"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Vivienne moved to a village overlooking the vast river valley to revive her creative drive', 'medium', '["Vivienne","moved","village","overlooking","vast","river","valley","revive","creative","drive"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'The divers discovered valuable silver coins preserved in the riverbed for over five centuries', 'medium', '["divers","discovered","valuable","silver","preserved","riverbed","over","five","centuries"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Every November the village festival involves the vivid arrival of over seven travelling vendors', 'medium', '["every","November","village","festival","involves","vivid","arrival","over","seven","travelling","vendors"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'The innovative device improved the lives of vulnerable individuals living in severe poverty', 'medium', '["innovative","device","improved","lives","vulnerable","individuals","living","severe","poverty"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Steven gave a moving overview of the environmental advantages of vegetarian living', 'medium', '["Steven","gave","moving","overview","environmental","advantages","vegetarian","living"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'The innovative involvement of volunteer drivers proved valuable in delivering provisions to seven vulnerable villages devastated by severe river floods', 'hard', '["innovative","involvement","volunteer","drivers","proved","valuable","delivering","provisions","seven","vulnerable","villages","devastated","severe","river","floods"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Victor reviewed the inventive governmental initiative to revive diverse investment in advanced environmental conservation and prevention of invasive development', 'hard', '["Victor","reviewed","inventive","governmental","initiative","revive","diverse","investment","advanced","environmental","conservation","prevention","invasive","development"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Every available vehicle traversed the elevated avenue delivering invaluable provisions to the survivors who had been deprived during the event', 'hard', '["every","available","vehicle","traversed","elevated","avenue","delivering","invaluable","provisions","survivors","deprived","during","event"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'The progressive university revised its vast vocabulary-development programme and eventually delivered improved evaluations of verbal competency', 'hard', '["progressive","university","revised","vast","vocabulary-development","programme","eventually","delivered","improved","evaluations","verbal","competency"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'v'), 'Vivienne received overwhelming evidence of the evolving environmental advantages of converting conventional vehicles to electric alternatives', 'hard', '["Vivienne","received","overwhelming","evidence","evolving","environmental","advantages","converting","conventional","vehicles","electric","alternatives"]');

-- /θ/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Think three thick thoughts', 'easy', '["think","three","thick","thoughts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Thank the thin man', 'easy', '["thank","thin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Both teeth are gone', 'easy', '["both","teeth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The path goes north', 'easy', '["path","north"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'A thousand things to do', 'easy', '["thousand","things"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Thousands of athletes gathered at the north end for the marathon through the thickest section', 'medium', '["thousands","athletes","gathered","north","marathon","through","thickest"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The mathematician thoroughly thought through the method for the thirty-third theorem', 'medium', '["mathematician","thoroughly","thought","through","method","thirty-third","theorem"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Both authors thought that the theme of the thesis was thought-provoking', 'medium', '["both","authors","thought","theme","thesis","thought-provoking"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The growth of the youth movement threatened the authority of the wealthy elite', 'medium', '["growth","youth","movement","threatened","authority","wealthy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'On the seventh Thursday the health authority withdrew from the northern therapy centre', 'medium', '["seventh","Thursday","health","authority","withdrew","northern","therapy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The theoretical breakthrough in thermal thermodynamics thought to be worthy of the three-thousand-dollar mathematics prize caused enthusiasm throughout the earth', 'hard', '["theoretical","breakthrough","thermal","thermodynamics","thought","worthy","three-thousand-dollar","mathematics","enthusiasm","throughout","earth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Thirty-three anthropologists thoroughly thought about the health and death rituals of the ethnic group from the southern earth mounds', 'hard', '["thirty-three","anthropologists","thoroughly","thought","about","health","death","rituals","ethnic","southern","earth","mounds"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The youthful marathon enthusiast thanked both therapists for their thorough methods and empathetic approach throughout the three-month rehabilitation', 'hard', '["youthful","marathon","enthusiast","thanked","both","therapists","thorough","methods","empathetic","approach","throughout","three-month","rehabilitation"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'The theological thesis authored by the mathematician was thought to represent the thickest theoretical synthesis on the theme of truth', 'hard', '["theological","thesis","authored","mathematician","thought","represent","thickest","theoretical","synthesis","theme","truth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'θ'), 'Beneath the earth both the northern and southern paths led through three thick thorn-covered areas that threatened the health of thousands', 'hard', '["beneath","earth","both","northern","southern","paths","through","three","thick","thorn-covered","threatened","health","thousands"]');

-- /ð/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'This is the other one', 'easy', '["this","the","other"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'They went with them', 'easy', '["they","with","them"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'My father and mother', 'easy', '["father","mother"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'The weather is smooth', 'easy', '["the","weather","smooth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Breathe and bathe there', 'easy', '["breathe","bathe","there"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'The brothers gathered together with their father and mother for the celebration', 'medium', '["the","brothers","gathered","together","with","their","father","and","mother"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Whether the weather is good or bad they always bathe in the river', 'medium', '["whether","the","weather","they","bathe","the"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'There is another reason for the clothing to be smoother than the others', 'medium', '["there","another","the","clothing","smoother","the","others"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Their brother and father breathed with ease as the southern weather improved', 'medium', '["their","brother","father","breathed","with","the","southern","weather"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'The feather that blew this way and that bothered the mother gathering clothes', 'medium', '["the","feather","that","this","that","bothered","the","mother","gathering"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Although the weather this Thursday was rather disagreeable the brothers nevertheless gathered with their father and bathed in the soothing southern river together', 'hard', '["although","the","weather","this","Thursday","rather","the","brothers","nevertheless","gathered","with","their","father","bathed","the","soothing","southern","together"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'The feathered creatures weathered the southern storms with ease and then smoothly glided over the heather as though nothing had bothered them', 'hard', '["the","feathered","weathered","the","southern","with","then","smoothly","the","heather","though","bothered","them"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Their father furthermore insisted that the clothing should be smoother and that the leather boots were worthier than the others', 'hard', '["their","father","furthermore","that","the","clothing","smoother","that","the","leather","worthier","the","others"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Whether the mother and father together with the brothers themselves would rather gather at this venue than the other was the question', 'hard', '["whether","the","mother","father","together","with","the","brothers","themselves","rather","gather","this","the","other","the"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ð'), 'Nonetheless the weather and the soothing rhythm of the southern breeze bothered neither the mother nor the father who breathed freely throughout the evening', 'hard', '["nonetheless","the","weather","the","soothing","the","southern","bothered","neither","the","mother","the","father","breathed","throughout","the"]');

-- /s/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Six socks in a sack', 'easy', '["six","socks","sack"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'See the sun set slowly', 'easy', '["see","sun","set","slowly"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Sit still and listen', 'easy', '["sit","still"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'This place is nice', 'easy', '["this","place","nice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Sam sings a sweet song', 'easy', '["Sam","sings","sweet","song"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Susan sat silently beside the peaceful seaside absorbing the special sunset scene', 'medium', '["Susan","sat","silently","beside","peaceful","seaside","absorbing","special","sunset","scene"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The scientist discovered several suspicious substances inside the sealed glass cylinder', 'medium', '["scientist","discovered","several","suspicious","substances","inside","sealed","glass","cylinder"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The students successfully passed the stressful assessment and celebrated with a special supper', 'medium', '["students","successfully","passed","stressful","assessment","celebrated","special","supper"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Sebastian expressed his sincere satisfaction at the sensational success of the festival', 'medium', '["Sebastian","expressed","sincere","satisfaction","sensational","success","festival"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The spacious house beside the cypress trees was suitable for someone seeking silence', 'medium', '["spacious","house","beside","cypress","suitable","someone","seeking","silence"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The sophisticated scientist''s astonishing statistical analysis surprised the specialists by suggesting a sensational solution to the persistent social crisis', 'hard', '["sophisticated","scientist''s","astonishing","statistical","analysis","surprised","specialists","suggesting","sensational","solution","persistent","social","crisis"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'Sebastian''s sensational success in the gymnastics discipline was a source of satisfaction across the sports science establishment', 'hard', '["Sebastian''s","sensational","success","gymnastics","discipline","source","satisfaction","across","sports","science","establishment"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The suspicious substance discovered inside the secure facility raised serious safety concerns and necessitated a swift response from the specialist rescue services', 'hard', '["suspicious","substance","discovered","inside","secure","facility","serious","safety","concerns","necessitated","swift","response","specialist","rescue","services"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'This season the students in the science class simultaneously submitted essays assessing the disastrous consequences of excessive plastic consumption on species', 'hard', '["this","season","students","science","class","simultaneously","submitted","essays","assessing","disastrous","consequences","excessive","plastic","consumption","species"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 's'), 'The system administrator insisted that essential security assessments must supersede all secondary tasks across the susceptible software service centres', 'hard', '["system","administrator","insisted","essential","security","assessments","must","supersede","secondary","tasks","across","susceptible","software","service","centres"]');

-- /z/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Roses and zoos are fun', 'easy', '["roses","zoos"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'His nose is frozen', 'easy', '["his","nose","frozen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Bees buzz all day', 'easy', '["bees","buzz"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Choose the easy prize', 'easy', '["choose","easy","prize"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Use your eyes and ears', 'easy', '["use","eyes","ears"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Elizabeth''s cousin was amazed by the buzzing noise from dozens of bees near the roses', 'medium', '["Elizabeth''s","cousin","amazed","buzzing","noise","dozens","bees","roses"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'The organization caused a buzz when it proposed a zone with zero emissions', 'medium', '["organization","caused","buzz","proposed","zone","zero","emissions"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'These musicians easily mesmerised the crowds with jazzy compositions', 'medium', '["these","musicians","easily","mesmerised","jazzy","compositions"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'His frozen pizzas and fizzy drinks were prizes in the surprise raffle', 'medium', '["his","frozen","pizzas","fizzy","prizes","surprise"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'The design magazine praised his amazing laser visualisation and cozy interiors', 'medium', '["design","magazine","praised","amazing","laser","visualisation","cozy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'The amazing realisation that dozens of hazardous industrial zones jeopardised thousands of citizens caused Elizabeth to organise a massive fundraising enterprise', 'hard', '["amazing","realisation","dozens","hazardous","industrial","zones","jeopardised","thousands","citizens","caused","Elizabeth","organise","massive","fundraising","enterprise"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'His cousin easily recognised the buzzing noise as hundreds of bees whose busy habits energised the surrounding ecosystem', 'hard', '["his","cousin","easily","recognised","buzzing","noise","hundreds","bees","busy","energised","surrounding","ecosystem"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'The amazing musicians who mesmerised the audiences with improvised jazz compositions utilised unusual synthesiser and laser configurations', 'hard', '["amazing","musicians","mesmerised","audiences","improvised","jazz","compositions","utilised","unusual","synthesiser","laser","configurations"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'The prize-winning enterprise was recognised for its phenomenal visualisation designs and surprising utilisation of renewable energy sources', 'hard', '["prize-winning","enterprise","recognised","phenomenal","visualisation","designs","surprising","utilisation","renewable","energy","sources"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'z'), 'Louise''s zealous organisation raised awareness about the hazards of urbanisation and proposed revised zoning policies as the basis for a sustainable future', 'hard', '["Louise''s","zealous","organisation","raised","awareness","hazards","urbanisation","proposed","revised","zoning","policies","basis","sustainable"]');

-- /ʃ/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'She sells short shirts', 'easy', '["she","sells","short","shirts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The ship is in the shop', 'easy', '["ship","shop"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Wash the shoes now', 'easy', '["wash","shoes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Shout and shake the sheet', 'easy', '["shout","shake","sheet"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Push the shelf to the shed', 'easy', '["push","shelf","shed"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Shannon showed the shoppers how to find the freshest fish at the seashore', 'medium', '["Shannon","showed","shoppers","freshest","fish","seashore"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The shy chef wished to share a special mushroom dish with the guests', 'medium', '["shy","chef","wished","share","special","mushroom","dish"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The fashion show showcased the most astonishing collection of cashew-coloured shirts', 'medium', '["fashion","show","showcased","astonishing","collection","cashew-coloured","shirts"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The publisher should have finished the national edition before the shareholders met', 'medium', '["publisher","should","finished","national","edition","shareholders"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Shelly shivered as she rushed to shut the shutters during the shower', 'medium', '["Shelly","shivered","she","rushed","shut","shutters","shower"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The prestigious international fashion show showcased an astonishing selection of luxurious short shirts and shimmering shoes designed by Shannon', 'hard', '["prestigious","international","fashion","show","showcased","astonishing","selection","luxurious","short","shirts","shimmering","shoes","designed","Shannon"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The publisher ensured the finished edition featured sensational information about the international championship and the shocking financial shenanigans', 'hard', '["publisher","ensured","finished","edition","featured","sensational","information","international","championship","shocking","financial","shenanigans"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'Shannon''s special mushroom dish astonished the shareholders at the prestigious national gala and was shared across several fashion publications', 'hard', '["Shannon''s","special","mushroom","dish","astonished","shareholders","prestigious","national","shared","several","fashion","publications"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'She rushed to share the shocking information with the officials but the sheer volume of paperwork frustrated her ambitious publishing schedule', 'hard', '["she","rushed","share","shocking","information","officials","sheer","frustrated","ambitious","publishing","schedule"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʃ'), 'The shimmering shore stretched for miles and the lush shallow marshes sheltered various shy fish and shellfish that flourished in the shade', 'hard', '["shimmering","shore","stretched","lush","shallow","marshes","sheltered","shy","fish","shellfish","flourished","shade"]');

-- /ʒ/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'A vision of pleasure', 'easy', '["vision","pleasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'Measure the treasure', 'easy', '["measure","treasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The usual decision', 'easy', '["usual","decision"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'An unusual garage sale', 'easy', '["unusual","garage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'A beige corsage for her', 'easy', '["beige","corsage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The television programme provided a visual illusion that caused confusion', 'medium', '["television","programme","visual","illusion","caused","confusion"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The decision to measure the garage was unusual but provided great pleasure', 'medium', '["decision","measure","garage","unusual","pleasure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The collision caused a massive explosion of beige-coloured material in the garage', 'medium', '["collision","caused","massive","explosion","beige-coloured","garage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'It is her usual pleasure to watch a television version of the classic espionage thriller', 'medium', '["usual","pleasure","television","version","espionage"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The revision of the legislation created an unusual division within the commission', 'medium', '["revision","legislation","unusual","division","commission"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The television presenter''s unusual decision to measure the precise visual dimensions of the explosion provided a sensational conclusion to the espionage programme', 'hard', '["television","presenter''s","unusual","decision","measure","precise","visual","dimensions","explosion","sensational","conclusion","espionage","programme"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The casual exclusion of the crucial revision in the legislation caused an unusual erosion of pleasure and cohesion within the commission', 'hard', '["casual","exclusion","crucial","revision","legislation","caused","unusual","erosion","pleasure","cohesion","commission"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'It was with immeasurable pleasure that the visionary made the decision to televise the prestigious treasure exhibition at the beige-toned mirage-themed pavilion', 'hard', '["immeasurable","pleasure","visionary","decision","televise","prestigious","treasure","exhibition","beige-toned","mirage-themed","pavilion"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The intrusion on their leisure caused visible confusion but the unusual measures taken provided an illusion of composure among the delegates', 'hard', '["intrusion","leisure","caused","visible","confusion","unusual","measures","provided","illusion","composure","delegates"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ʒ'), 'The revision and division of the usual television schedule created considerable tension and an unusual collision between the visual arts commission and the leisure department', 'hard', '["revision","division","usual","television","schedule","considerable","tension","unusual","collision","visual","arts","commission","leisure","department"]');

-- /h/ fricative phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'He has a heavy hat', 'easy', '["he","has","heavy","hat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Help her hold hands', 'easy', '["help","her","hold","hands"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Hurry home for lunch', 'easy', '["hurry","home"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'The horse hides here', 'easy', '["horse","hides","here"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'A huge hot hamburger', 'easy', '["huge","hot","hamburger"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Henry hopefully headed home after the horrible hike through the humid hills', 'medium', '["Henry","hopefully","headed","home","horrible","hike","humid","hills"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'The hotel housed hundreds of happy holidaymakers who had gathered here', 'medium', '["hotel","housed","hundreds","happy","holidaymakers","had","here"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Hannah heard the heavy helicopter hovering high above the hillside', 'medium', '["Hannah","heard","heavy","helicopter","hovering","high","hillside"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'He hurried behind the hedgehog habitat hoping to hide his homework', 'medium', '["he","hurried","behind","hedgehog","habitat","hoping","hide","his","homework"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'The house at the hilltop had a huge herb garden and a henhouse', 'medium', '["house","hilltop","had","huge","herb","henhouse"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'The humanitarian organisation hurriedly dispatched hundreds of helpers to the historic highland hospital that had been hit by the horrific heatwave', 'hard', '["humanitarian","organisation","hurriedly","dispatched","hundreds","helpers","historic","highland","hospital","had","hit","horrific","heatwave"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Henry hypothesised that the heavy humidity hovering above the hillside habitat had a harmful effect on the herds inhabiting the highlands', 'hard', '["Henry","hypothesised","heavy","humidity","hovering","hillside","habitat","had","harmful","herds","inhabiting","highlands"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'Hannah''s heartfelt homage to her hometown heritage highlighted the heroic history of the humble households behind the harbour', 'hard', '["Hannah''s","heartfelt","homage","her","hometown","heritage","highlighted","heroic","history","humble","households","behind","harbour"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'He unhesitatingly helped the helpless hostages who had been hidden in the humid hallway behind the hotel until the hazard had been handled', 'hard', '["he","unhesitatingly","helped","helpless","hostages","had","hidden","humid","hallway","behind","hotel","hazard","had","handled"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'h'), 'The hilarious host of the household gathering happily recounted how his hopeless hunting hobby had humorously humiliated him in front of the whole household', 'hard', '["hilarious","host","household","gathering","happily","how","his","hopeless","hunting","hobby","had","humorously","humiliated","him","whole","household"]');

-- /tʃ/ affricate phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Check the cheap cheese', 'easy', '["check","cheap","cheese"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Choose a chocolate chip', 'easy', '["choose","chocolate","chip"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Watch the teacher chat', 'easy', '["watch","teacher","chat"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The child ate lunch', 'easy', '["child","lunch"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Catch the beach chair', 'easy', '["catch","beach","chair"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The cheerful children watched each other chase the chicken through the churchyard', 'medium', '["cheerful","children","watched","each","other","chase","chicken","churchyard"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Charlotte challenged the champion to a chess match in the enchanting chapel', 'medium', '["Charlotte","challenged","champion","chess","match","enchanting","chapel"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The teacher''s choice to schedule a march through the charming Chinese district was much appreciated', 'medium', '["teacher''s","choice","schedule","march","charming","Chinese","district","much"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Charles purchased a cheap watch and an enchanting charm from the charity shop', 'medium', '["Charles","purchased","cheap","watch","enchanting","charm","charity","shop"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Each chapter of the textbook featured research about natural and virtual challenges', 'medium', '["each","chapter","textbook","featured","research","natural","virtual","challenges"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The chancellor''s achievement in restructuring the charitable institution was much cheered by the teachers at the enchanting church gathering', 'hard', '["chancellor''s","achievement","restructuring","charitable","institution","much","cheered","teachers","enchanting","church","gathering"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The cheerful children from the Chinese exchange programme chose to watch a championship chess match at the ancient enchanted chapel', 'hard', '["cheerful","children","Chinese","exchange","programme","chose","watch","championship","chess","match","ancient","enchanted","chapel"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'Charlotte''s chief challenge was teaching the mischievous children to catch and pitch without cheating at each and every match', 'hard', '["Charlotte''s","chief","challenge","teaching","mischievous","children","catch","pitch","cheating","each","every","match"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The botched approach to purchasing cheap furniture for the charitable church attracted much criticism and eventually changed the entire procurement procedure', 'hard', '["botched","approach","purchasing","cheap","furniture","charitable","church","attracted","much","criticism","changed","entire","procurement","procedure"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'tʃ'), 'The researchers launched an ambitious search to uncover how future technological changes will enrich and challenge the charitable sector and virtual education', 'hard', '["researchers","launched","ambitious","search","uncover","future","technological","changes","enrich","challenge","charitable","sector","virtual","education"]');

-- /dʒ/ affricate phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'Just jump and jog', 'easy', '["just","jump","jog"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The judge joined us', 'easy', '["judge","joined"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'A jar of orange jam', 'easy', '["jar","orange","jam"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'Large pages of jokes', 'easy', '["large","pages","jokes"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'George enjoys juice', 'easy', '["George","enjoys","juice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The generous stranger encouraged the injured teenager to imagine a joyful future', 'medium', '["generous","stranger","encouraged","injured","teenager","imagine","joyful"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The management suggested adjusting the budget to fund a major bridge project', 'medium', '["management","suggested","adjusting","budget","major","bridge","project"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'George and Jane joined the gym to engage in regular vigorous exercise', 'medium', '["George","Jane","joined","gym","engage","regular","vigorous","exercise"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'A large majority of the college graduates pledged to volunteer in the region', 'medium', '["large","majority","college","graduates","pledged","volunteer","region"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The general manager judged the energetic gymnasts at the regional competition', 'medium', '["general","manager","judged","energetic","gymnasts","regional","competition"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The agile judge''s judicious arrangement of the agenda encouraged a gradual emergence of genuine dialogue in the damaged region', 'hard', '["agile","judge''s","judicious","arrangement","agenda","encouraged","gradual","emergence","genuine","dialogue","damaged","region"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'George imagined a huge project that would rejuvenate the dilapidated villages on the edge of the jungle and generate jobs', 'hard', '["George","imagined","huge","project","rejuvenate","dilapidated","villages","edge","jungle","generate","jobs"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The management of the prestigious college arranged an engaging programme of lectures on digital technology and the dangers of disinformation', 'hard', '["management","prestigious","college","arranged","engaging","programme","lectures","digital","technology","dangers","disinformation"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The generous stranger from Japan changed the trajectory of the teenager''s damaged life by gently encouraging him to imagine joy', 'hard', '["generous","stranger","Japan","changed","trajectory","teenager''s","damaged","gently","encouraging","imagine","joy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'dʒ'), 'The energetic gymnasts who competed in the regional championship emerged as the strongest contingent and were acknowledged for their extraordinary courage and agility', 'hard', '["energetic","gymnasts","competed","regional","championship","emerged","strongest","contingent","acknowledged","extraordinary","courage","agility"]');

-- /m/ nasal phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Make more time for me', 'easy', '["make","more","time","me"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Mom made some milk', 'easy', '["mom","made","some","milk"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'A small mouse moved', 'easy', '["small","mouse","moved"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Come home from market', 'easy', '["come","home","from","market"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The man swims at dawn', 'easy', '["man","swims"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Malcolm immediately reminded his team members to submit the remaining homework by tomorrow morning', 'medium', '["Malcolm","immediately","reminded","team","members","submit","remaining","homework","tomorrow","morning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The management committee recommended implementing the ambitious marketing campaign in summer', 'medium', '["management","committee","recommended","implementing","ambitious","marketing","campaign","summer"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Samantha memorised the complicated formula and claimed it made chemistry seem simpler', 'medium', '["Samantha","memorised","complicated","formula","claimed","made","chemistry","seem","simpler"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The museum welcomed many families and made a memorable impression on them', 'medium', '["museum","welcomed","many","families","made","memorable","impression","them"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Max commented that the commercial film had misrepresented the maritime community', 'medium', '["Max","commented","commercial","film","misrepresented","maritime","community"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The monumental misunderstanding between management committee members demanded immediate diplomatic mediation from the company chairman', 'hard', '["monumental","misunderstanding","management","committee","members","demanded","immediate","diplomatic","mediation","company","chairman"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Samantha remarkably memorised the mammoth amount of mathematical material and demonstrated a magnificent mastery in the examination', 'hard', '["Samantha","remarkably","memorised","mammoth","amount","mathematical","material","demonstrated","magnificent","mastery","examination"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The commercial documentary commemorated the remarkable maritime achievements of the common merchant seamen from the small community', 'hard', '["commercial","documentary","commemorated","remarkable","maritime","achievements","common","merchant","seamen","small","community"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'Mohammed''s mesmerising performance at the summer music festival moved the multitude and became an important milestone in modern entertainment', 'hard', '["Mohammed''s","mesmerising","performance","summer","music","festival","moved","multitude","became","important","milestone","modern","entertainment"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'm'), 'The commission promised to commit maximum investment to the embattled community''s monumental programme to improve mammography and maternal medicine', 'hard', '["commission","promised","commit","maximum","investment","embattled","community''s","monumental","programme","improve","mammography","maternal","medicine"]');

-- /n/ nasal phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'No one knew the name', 'easy', '["no","one","knew","name"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Nine new nice things', 'easy', '["nine","new","nice"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Run in the rain again', 'easy', '["run","rain","again"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'A thin and clean pen', 'easy', '["thin","clean","pen"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Turn on the main fan', 'easy', '["turn","on","main","fan"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The innovation plan encouraged nine new tenants to renovate the downtown neighbourhood', 'medium', '["innovation","plan","encouraged","nine","new","tenants","renovate","downtown","neighbourhood"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Nathan innocently mentioned that the northern train line needed maintenance again', 'medium', '["Nathan","innocently","mentioned","northern","train","line","needed","maintenance","again"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The experienced accountant noticed an inconsistency in the financial documentation', 'medium', '["experienced","accountant","noticed","inconsistency","financial","documentation"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The international convention began on Monday afternoon in a nice open arena', 'medium', '["international","convention","began","Monday","afternoon","nice","open","arena"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Jennifer planned an enjoyable afternoon in the downtown entertainment zone', 'medium', '["Jennifer","planned","enjoyable","afternoon","downtown","entertainment","zone"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The announcement concerning the international environmental convention generated intense interest in the nomination of an unknown independent candidate', 'hard', '["announcement","concerning","international","environmental","convention","generated","intense","interest","nomination","unknown","independent","candidate"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'Nathan''s unintentional mention of the confidential information turned into an uncontained situation that necessitated intervention from the management', 'hard', '["Nathan''s","unintentional","mention","confidential","information","turned","uncontained","situation","necessitated","intervention","management"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The renowned Indonesian engineer planned an innovative nine-month renovation of the abandoned underground tunnel connecting the northern and southern stations', 'hard', '["renowned","Indonesian","engineer","planned","innovative","nine-month","renovation","abandoned","underground","tunnel","connecting","northern","southern","stations"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'In an outstanding demonstration of inventiveness the international foundation financed an unconventional entertainment programme for underprivileged children in nine nations', 'hard', '["outstanding","demonstration","inventiveness","international","foundation","financed","unconventional","entertainment","programme","underprivileged","children","nine","nations"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'n'), 'The nomination announcement genuinely astonished the audience at the convention and none of the journalists anticipated the unexpected turn of events', 'hard', '["nomination","announcement","genuinely","astonished","audience","convention","none","journalists","anticipated","unexpected","turn","events"]');

-- /ŋ/ nasal phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Sing a long song', 'easy', '["sing","long","song"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'A strong young king', 'easy', '["strong","young","king"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Ring the evening bell', 'easy', '["ring","evening"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Bring something along', 'easy', '["bring","something","along"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Nothing is wrong here', 'easy', '["nothing","wrong"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The young singer was longing to perform the enchanting evening song for the king', 'medium', '["young","singer","longing","perform","enchanting","evening","song","king"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Something is wrong with the plumbing and the banging is getting annoying', 'medium', '["something","wrong","plumbing","banging","getting","annoying"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The striking painting depicting a running spring stream was absolutely breathtaking', 'medium', '["striking","painting","depicting","running","spring","stream","breathtaking"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Among the thrilling things to do is hang gliding along the stunning coastline', 'medium', '["among","thrilling","things","hang","along","stunning"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The lingering feeling of belonging strengthened her longing to do something meaningful', 'medium', '["lingering","feeling","belonging","strengthened","longing","something","meaningful"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The breathtaking evening gathering of the singing congregation along the winding spring river was an increasingly moving and long-lasting experience', 'hard', '["breathtaking","evening","gathering","singing","congregation","along","winding","spring","river","increasingly","moving","long-lasting"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The strong young king was longing to bring something meaningful to his struggling kingdom by engaging in a long-running unifying campaign', 'hard', '["strong","young","king","longing","bring","something","meaningful","struggling","kingdom","engaging","long-running","unifying","campaign"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'Nothing wrong with hanging around and enjoying the stunning evening as the ringing bells were echoing along the rolling hillsides', 'hard', '["nothing","wrong","hanging","around","enjoying","stunning","evening","ringing","echoing","along","rolling"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The ongoing sparring among the competing factions was undermining everything and putting a lingering strain on the long-standing working relationship', 'hard', '["ongoing","sparring","among","competing","undermining","everything","putting","lingering","strain","long-standing","working"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'ŋ'), 'The young engineering team belonging to the leading manufacturing conglomerate was longing to bring a stunning technological breakthrough to the challenging sporting industry', 'hard', '["young","engineering","belonging","leading","manufacturing","conglomerate","longing","bring","stunning","technological","breakthrough","challenging","sporting","industry"]');

-- /l/ approximant phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'Let me look at the light', 'easy', '["let","look","light"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'A long lovely letter', 'easy', '["long","lovely","letter"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'Call the little girl', 'easy', '["call","little","girl"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'All the tall buildings', 'easy', '["all","tall","buildings"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'Feel well and relax', 'easy', '["feel","well","relax"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'Lily literally called the local florist to deliver twelve yellow lilies', 'medium', '["Lily","literally","called","local","florist","deliver","twelve","yellow","lilies"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'The tall classical building overlooked the beautiful lake and rolling hills', 'medium', '["tall","classical","building","overlooked","beautiful","lake","rolling","hills"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'All the children played nicely on the level field until the bell called them', 'medium', '["all","children","played","nicely","level","field","bell","called"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'William reluctantly followed the trail along the hillside with skillful balance', 'medium', '["William","reluctantly","followed","trail","along","hillside","skillful","balance"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'The electrical installation involved several parallel cable lines along the hall', 'medium', '["electrical","installation","involved","several","parallel","cable","lines","along","hall"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'The brilliantly skilled electrical specialist carefully installed the parallel cable lines that illuminated the colossal cultural hall', 'hard', '["brilliantly","skilled","electrical","specialist","carefully","installed","parallel","cable","lines","illuminated","colossal","cultural","hall"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'Lily reluctantly concluded that the relatively traditional philosophical analysis lacked the essential logical clarity to be labelled a valuable contribution', 'hard', '["Lily","reluctantly","concluded","relatively","traditional","philosophical","analysis","lacked","essential","logical","clarity","labelled","valuable","contribution"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'All the skillful classical violinists called for a monumental collaboration that would leave an indelible cultural legacy for following millennia', 'hard', '["all","skillful","classical","violinists","called","monumental","collaboration","leave","indelible","cultural","legacy","following","millennia"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'William brilliantly illustrated the delicate structural balance of the colonial building by revealing the parallel lines and elaborate lateral beams', 'hard', '["William","brilliantly","illustrated","delicate","structural","balance","colonial","building","revealing","parallel","lines","elaborate","lateral","beams"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'l'), 'The legal challenge ultimately failed to deliver a reliable solution and the multilateral alliance reluctantly allowed the flawed political deal to materialise', 'hard', '["legal","challenge","ultimately","failed","deliver","reliable","solution","multilateral","alliance","reluctantly","allowed","flawed","political","deal","materialise"]');

-- /r/ approximant phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Run really fast right now', 'easy', '["run","really","fast","right"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'A red rose for her', 'easy', '["red","rose","her"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Read the written rule', 'easy', '["read","written","rule"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The rain arrived early', 'easy', '["rain","arrived","early"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'A great grey bridge', 'easy', '["great","grey","bridge"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Robert''s parents arranged for three remarkable artists to restore the rural property', 'medium', '["Robert''s","parents","arranged","three","remarkable","artists","restore","rural","property"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The rare tropical parrot preferred the warmer region near the northern river', 'medium', '["rare","tropical","parrot","preferred","warmer","region","near","northern","river"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Rachel rapidly retrieved her rucksack from the train and raced through the crowd', 'medium', '["Rachel","rapidly","retrieved","rucksack","train","raced","through","crowd"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The primary criteria for the research programme was to recruit truly creative writers', 'medium', '["primary","criteria","research","programme","recruit","truly","creative","writers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Three strong children ran around the rural park carrying bright red streamers', 'medium', '["three","strong","children","ran","around","rural","park","carrying","bright","red","streamers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The extraordinary architectural restoration of the remarkable rural retreat received tremendous praise from the critical reviewers and prominent researchers', 'hard', '["extraordinary","architectural","restoration","remarkable","rural","retreat","received","tremendous","praise","critical","reviewers","prominent","researchers"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Robert''s remarkable portrayal of the revolutionary hero reverberated through the auditorium creating rapturous admiration from the appreciative audience', 'hard', '["Robert''s","remarkable","portrayal","revolutionary","hero","reverberated","through","auditorium","creating","rapturous","admiration","appreciative","audience"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The primary research laboratory reported a rare and extraordinary discovery regarding the irregular characteristics of the previously unrecorded Arctic organism', 'hard', '["primary","research","laboratory","reported","rare","extraordinary","discovery","regarding","irregular","characteristics","previously","unrecorded","Arctic","organism"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'Three resourceful researchers from the rural programme arrived earlier to prepare comprehensive presentations on tropical river conservation strategies', 'hard', '["three","resourceful","researchers","rural","programme","arrived","earlier","prepare","comprehensive","presentations","tropical","river","conservation","strategies"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'r'), 'The prodigiously creative orchestral arrangement of the dramatic overture represented the greatest triumph of the extraordinary Russian-born professor''s career', 'hard', '["prodigiously","creative","orchestral","arrangement","dramatic","overture","represented","greatest","triumph","extraordinary","Russian-born","professor''s","career"]');

-- /w/ approximant phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'We want warm water', 'easy', '["we","want","warm","water"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'Walk with the wind', 'easy', '["walk","with","wind"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'A wet wild winter', 'easy', '["wet","wild","winter"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The woman waited well', 'easy', '["woman","waited","well"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'Would you like some wine', 'easy', '["would","wine"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'William wanted to win the award but he was worried about the wild weather conditions', 'medium', '["William","wanted","win","award","was","worried","wild","weather"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The wise woman was willing to wait while the wind went wild outside the window', 'medium', '["wise","woman","was","willing","wait","while","wind","went","wild","window"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'Walter went west to work at the wonderful wildlife reserve in Wisconsin', 'medium', '["Walter","went","west","work","wonderful","wildlife","reserve","Wisconsin"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The award-winning writer was once a warehouse worker in a wealthy town', 'medium', '["award-winning","writer","was","once","warehouse","worker","wealthy"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'Wednesday''s weather was worse and the waves were wild with a powerful westward wind', 'medium', '["Wednesday''s","weather","was","worse","waves","were","wild","powerful","westward","wind"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The world-renowned wildlife documentary wonderfully showcased the wisdom of wolves wandering westwards through the wild winter wilderness', 'hard', '["world-renowned","wildlife","documentary","wonderfully","showcased","wisdom","wolves","wandering","westwards","wild","winter","wilderness"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'William''s award-winning watercolour was widely celebrated as a wonderful illustration of the Welsh wetlands where wild swans winter', 'hard', '["William''s","award-winning","watercolour","was","widely","celebrated","wonderful","illustration","Welsh","wetlands","where","wild","swans","winter"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The weather worsened as we were walking westward and the wind was whipping wildly around the woodland while the waves were crashing', 'hard', '["weather","worsened","we","were","walking","westward","wind","was","whipping","wildly","woodland","while","waves","were"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'We wondered whether the wealthy businessman would withdraw the controversial wildlife warehouse proposal that bewildered the whole community', 'hard', '["we","wondered","whether","wealthy","would","withdraw","controversial","wildlife","warehouse","bewildered","whole","community"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'w'), 'The weary woman was unwilling to wait any longer for the wayward workmen who were woefully behind schedule with the waterway widening works', 'hard', '["weary","woman","was","unwilling","wait","wayward","workmen","who","were","woefully","behind","waterway","widening","works"]');

-- /j/ approximant phrases
INSERT INTO phoneme_practice_phrases (id, phoneme_id, text, difficulty, target_words) VALUES
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yes you are young', 'easy', '["yes","you","young"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'A yellow yard of yarn', 'easy', '["yellow","yard","yarn"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Your year starts here', 'easy', '["your","year"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Use your unique view', 'easy', '["use","your","unique","view"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yet the youth ran away', 'easy', '["yet","youth"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yesterday your younger sister used the yellow yacht to sail beyond the yachting yard', 'medium', '["yesterday","your","younger","used","yellow","yacht","beyond","yachting","yard"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'The university students enthusiastically used the new computerised yearbook system', 'medium', '["university","students","enthusiastically","used","new","computerised","yearbook"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yolanda yielded to the unanimous opinion and accepted the year-long European union exchange', 'medium', '["Yolanda","yielded","unanimous","year-long","European","union","exchange"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'The useful YouTube tutorial featured a unique approach to growing yams in your yard', 'medium', '["useful","YouTube","tutorial","featured","unique","approach","yams","your","yard"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'You are welcome to use the youthful volunteers for the annual community yogurt festival', 'medium', '["you","use","youthful","volunteers","annual","community","yogurt"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yesterday the youthful European graduates enthusiastically united to formulate a unique year-long programme utilising your university''s resources', 'hard', '["yesterday","youthful","European","graduates","enthusiastically","united","formulate","unique","year-long","programme","utilising","your","university''s"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'The annual review yielded useful insights into how the uniform communication policy uniquely contributed to the unity of the Young Entrepreneurs Union', 'hard', '["annual","review","yielded","useful","insights","uniform","communication","uniquely","contributed","unity","Young","Entrepreneurs","Union"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Your contribution as a volunteer at the universal youth festival was unanimously valued by the committee for its unique humanitarian yield', 'hard', '["your","contribution","volunteer","universal","youth","festival","unanimously","valued","committee","unique","humanitarian","yield"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'The youthful musician used a beautiful ukulele to compose a unique yet universally appealing melody that could be enjoyed throughout the year', 'hard', '["youthful","musician","used","beautiful","ukulele","compose","unique","yet","universally","appealing","melody","enjoyed","year"]'),
(gen_random_uuid(), (SELECT id FROM phonemes WHERE symbol = 'j'), 'Yasmin''s yearning for a united European community yielded a useful document that was universally admired for its humorous yet courageous humanitarian vision', 'hard', '["Yasmin''s","yearning","united","European","community","yielded","useful","document","universally","admired","humorous","yet","courageous","humanitarian","vision"]');
