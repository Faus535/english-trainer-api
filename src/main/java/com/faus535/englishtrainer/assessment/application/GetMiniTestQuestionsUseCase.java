package com.faus535.englishtrainer.assessment.application;

import com.faus535.englishtrainer.assessment.domain.TestQuestion;
import com.faus535.englishtrainer.shared.application.annotation.UseCase;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@UseCase
public class GetMiniTestQuestionsUseCase {

    private static final Map<String, Map<String, List<TestQuestion>>> QUESTION_BANK = new HashMap<>();

    static {
        // =============================================
        // VOCABULARY questions (20 per level)
        // =============================================
        Map<String, List<TestQuestion>> vocabByLevel = new HashMap<>();
        vocabByLevel.put("a1", List.of(
                new TestQuestion("mv1", "vocabulary", "What does 'dog' mean?", List.of("gato", "perro", "pájaro", "pez"), "perro", "a1"),
                new TestQuestion("mv2", "vocabulary", "What does 'table' mean?", List.of("silla", "mesa", "cama", "puerta"), "mesa", "a1"),
                new TestQuestion("mv3", "vocabulary", "What does 'red' mean?", List.of("azul", "verde", "rojo", "amarillo"), "rojo", "a1"),
                new TestQuestion("mv4", "vocabulary", "What does 'mother' mean?", List.of("padre", "madre", "hermano", "hijo"), "madre", "a1"),
                new TestQuestion("mv5", "vocabulary", "What does 'bread' mean?", List.of("arroz", "pan", "leche", "carne"), "pan", "a1"),
                new TestQuestion("mv6", "vocabulary", "What does 'big' mean?", List.of("pequeño", "grande", "alto", "bajo"), "grande", "a1"),
                new TestQuestion("mv7", "vocabulary", "What does 'school' mean?", List.of("hospital", "escuela", "tienda", "iglesia"), "escuela", "a1"),
                new TestQuestion("mv8", "vocabulary", "What does 'happy' mean?", List.of("triste", "feliz", "enojado", "cansado"), "feliz", "a1"),
                new TestQuestion("mv9", "vocabulary", "What does 'friend' mean?", List.of("enemigo", "amigo", "vecino", "primo"), "amigo", "a1"),
                new TestQuestion("mv10", "vocabulary", "What does 'morning' mean?", List.of("tarde", "noche", "mañana", "mediodía"), "mañana", "a1"),
                new TestQuestion("mv101", "vocabulary", "What does 'milk' mean?", List.of("agua", "leche", "jugo", "café"), "leche", "a1"),
                new TestQuestion("mv102", "vocabulary", "What does 'door' mean?", List.of("ventana", "puerta", "pared", "techo"), "puerta", "a1"),
                new TestQuestion("mv103", "vocabulary", "What does 'cold' mean?", List.of("caliente", "frío", "tibio", "helado"), "frío", "a1"),
                new TestQuestion("mv104", "vocabulary", "What does 'sun' mean?", List.of("luna", "sol", "estrella", "nube"), "sol", "a1"),
                new TestQuestion("mv105", "vocabulary", "What does 'car' mean?", List.of("bicicleta", "coche", "autobús", "tren"), "coche", "a1"),
                new TestQuestion("mv106", "vocabulary", "What does 'work' mean?", List.of("jugar", "trabajar", "dormir", "comer"), "trabajar", "a1"),
                new TestQuestion("mv107", "vocabulary", "What does 'girl' mean?", List.of("niño", "niña", "mujer", "hombre"), "niña", "a1"),
                new TestQuestion("mv108", "vocabulary", "What does 'eat' mean?", List.of("beber", "comer", "correr", "leer"), "comer", "a1"),
                new TestQuestion("mv109", "vocabulary", "What does 'night' mean?", List.of("día", "noche", "tarde", "mañana"), "noche", "a1"),
                new TestQuestion("mv110", "vocabulary", "What does 'good' mean?", List.of("malo", "bueno", "regular", "horrible"), "bueno", "a1")
        ));
        vocabByLevel.put("a2", List.of(
                new TestQuestion("mv11", "vocabulary", "What does 'journey' mean?", List.of("viaje", "trabajo", "juego", "fiesta"), "viaje", "a2"),
                new TestQuestion("mv12", "vocabulary", "What does 'weather' mean?", List.of("clima", "noticias", "deporte", "comida"), "clima", "a2"),
                new TestQuestion("mv13", "vocabulary", "What does 'angry' mean?", List.of("feliz", "triste", "enojado", "nervioso"), "enojado", "a2"),
                new TestQuestion("mv14", "vocabulary", "What does 'cheap' mean?", List.of("caro", "barato", "gratis", "valioso"), "barato", "a2"),
                new TestQuestion("mv15", "vocabulary", "What does 'busy' mean?", List.of("libre", "ocupado", "cansado", "aburrido"), "ocupado", "a2"),
                new TestQuestion("mv16", "vocabulary", "What does 'advice' mean?", List.of("consejo", "aviso", "noticia", "orden"), "consejo", "a2"),
                new TestQuestion("mv17", "vocabulary", "What does 'match' mean?", List.of("partido", "mesa", "silla", "puerta"), "partido", "a2"),
                new TestQuestion("mv18", "vocabulary", "What does 'share' mean?", List.of("comprar", "vender", "compartir", "guardar"), "compartir", "a2"),
                new TestQuestion("mv19", "vocabulary", "What does 'neighbour' mean?", List.of("amigo", "vecino", "primo", "hermano"), "vecino", "a2"),
                new TestQuestion("mv20", "vocabulary", "What does 'perhaps' mean?", List.of("siempre", "nunca", "quizás", "ahora"), "quizás", "a2"),
                new TestQuestion("mv201", "vocabulary", "What does 'luggage' mean?", List.of("equipaje", "billete", "pasaporte", "reserva"), "equipaje", "a2"),
                new TestQuestion("mv202", "vocabulary", "What does 'receipt' mean?", List.of("receta", "recibo", "regalo", "reseña"), "recibo", "a2"),
                new TestQuestion("mv203", "vocabulary", "What does 'polite' mean?", List.of("grosero", "educado", "tímido", "gracioso"), "educado", "a2"),
                new TestQuestion("mv204", "vocabulary", "What does 'empty' mean?", List.of("lleno", "vacío", "pesado", "ligero"), "vacío", "a2"),
                new TestQuestion("mv205", "vocabulary", "What does 'lend' mean?", List.of("pedir prestado", "prestar", "devolver", "guardar"), "prestar", "a2"),
                new TestQuestion("mv206", "vocabulary", "What does 'noise' mean?", List.of("silencio", "ruido", "música", "voz"), "ruido", "a2"),
                new TestQuestion("mv207", "vocabulary", "What does 'crowded' mean?", List.of("vacío", "abarrotado", "tranquilo", "limpio"), "abarrotado", "a2"),
                new TestQuestion("mv208", "vocabulary", "What does 'delayed' mean?", List.of("temprano", "retrasado", "cancelado", "rápido"), "retrasado", "a2"),
                new TestQuestion("mv209", "vocabulary", "What does 'skill' mean?", List.of("problema", "habilidad", "dificultad", "error"), "habilidad", "a2"),
                new TestQuestion("mv210", "vocabulary", "What does 'comfortable' mean?", List.of("incómodo", "cómodo", "difícil", "estrecho"), "cómodo", "a2")
        ));
        vocabByLevel.put("b1", List.of(
                new TestQuestion("mv21", "vocabulary", "What does 'opportunity' mean?", List.of("problema", "oportunidad", "dificultad", "obligación"), "oportunidad", "b1"),
                new TestQuestion("mv22", "vocabulary", "What does 'research' mean?", List.of("investigación", "enseñanza", "lectura", "escritura"), "investigación", "b1"),
                new TestQuestion("mv23", "vocabulary", "What does 'environment' mean?", List.of("gobierno", "medio ambiente", "edificio", "hospital"), "medio ambiente", "b1"),
                new TestQuestion("mv24", "vocabulary", "What does 'behaviour' mean?", List.of("comportamiento", "conocimiento", "sentimiento", "pensamiento"), "comportamiento", "b1"),
                new TestQuestion("mv25", "vocabulary", "What does 'eventually' mean?", List.of("nunca", "eventualmente", "siempre", "finalmente"), "finalmente", "b1"),
                new TestQuestion("mv26", "vocabulary", "What does 'benefit' mean?", List.of("perjuicio", "beneficio", "castigo", "error"), "beneficio", "b1"),
                new TestQuestion("mv27", "vocabulary", "What does 'significant' mean?", List.of("insignificante", "significativo", "simple", "común"), "significativo", "b1"),
                new TestQuestion("mv28", "vocabulary", "What does 'demand' mean?", List.of("oferta", "demanda", "regalo", "descuento"), "demanda", "b1"),
                new TestQuestion("mv29", "vocabulary", "What does 'struggle' mean?", List.of("descansar", "luchar", "rendirse", "dormir"), "luchar", "b1"),
                new TestQuestion("mv30", "vocabulary", "What does 'hire' mean?", List.of("despedir", "contratar", "renunciar", "jubilar"), "contratar", "b1"),
                new TestQuestion("mv301", "vocabulary", "What does 'aware' mean?", List.of("ignorante", "consciente", "dormido", "ausente"), "consciente", "b1"),
                new TestQuestion("mv302", "vocabulary", "What does 'complain' mean?", List.of("felicitar", "quejarse", "agradecer", "celebrar"), "quejarse", "b1"),
                new TestQuestion("mv303", "vocabulary", "What does 'purpose' mean?", List.of("accidente", "propósito", "resultado", "problema"), "propósito", "b1"),
                new TestQuestion("mv304", "vocabulary", "What does 'consider' mean?", List.of("ignorar", "considerar", "rechazar", "olvidar"), "considerar", "b1"),
                new TestQuestion("mv305", "vocabulary", "What does 'lack' mean?", List.of("abundancia", "carencia", "exceso", "suficiencia"), "carencia", "b1"),
                new TestQuestion("mv306", "vocabulary", "What does 'willing' mean?", List.of("obligado", "dispuesto", "forzado", "reluctante"), "dispuesto", "b1"),
                new TestQuestion("mv307", "vocabulary", "What does 'approach' mean?", List.of("alejarse", "acercarse", "evitar", "huir"), "acercarse", "b1"),
                new TestQuestion("mv308", "vocabulary", "What does 'outcome' mean?", List.of("inicio", "resultado", "proceso", "entrada"), "resultado", "b1"),
                new TestQuestion("mv309", "vocabulary", "What does 'pretend' mean?", List.of("admitir", "fingir", "confesar", "revelar"), "fingir", "b1"),
                new TestQuestion("mv310", "vocabulary", "What does 'attempt' mean?", List.of("rendirse", "intentar", "rechazar", "abandonar"), "intentar", "b1")
        ));
        QUESTION_BANK.put("vocabulary", vocabByLevel);

        // =============================================
        // GRAMMAR questions (20 per level)
        // =============================================
        Map<String, List<TestQuestion>> grammarByLevel = new HashMap<>();
        grammarByLevel.put("a1", List.of(
                new TestQuestion("mg1", "grammar", "He ___ a teacher.", List.of("is", "am", "are", "be"), "is", "a1"),
                new TestQuestion("mg2", "grammar", "We ___ lunch at 12.", List.of("has", "have", "having", "had"), "have", "a1"),
                new TestQuestion("mg3", "grammar", "The children ___ playing.", List.of("is", "am", "are", "be"), "are", "a1"),
                new TestQuestion("mg4", "grammar", "My sister ___ in London.", List.of("live", "lives", "living", "lived"), "lives", "a1"),
                new TestQuestion("mg5", "grammar", "There ___ three books on the table.", List.of("is", "am", "are", "be"), "are", "a1"),
                new TestQuestion("mg6", "grammar", "I ___ like spinach.", List.of("don't", "doesn't", "isn't", "aren't"), "don't", "a1"),
                new TestQuestion("mg7", "grammar", "___ she a doctor?", List.of("Does", "Do", "Is", "Are"), "Is", "a1"),
                new TestQuestion("mg8", "grammar", "We ___ TV every evening.", List.of("watch", "watches", "watching", "watched"), "watch", "a1"),
                new TestQuestion("mg9", "grammar", "This is ___ book.", List.of("a", "an", "the", "no article"), "a", "a1"),
                new TestQuestion("mg10", "grammar", "She can ___ English.", List.of("speaks", "spoke", "speak", "speaking"), "speak", "a1"),
                new TestQuestion("mg101", "grammar", "She ___ milk every day.", List.of("drink", "drinks", "drinking", "drank"), "drinks", "a1"),
                new TestQuestion("mg102", "grammar", "I ___ from Spain.", List.of("am", "is", "are", "be"), "am", "a1"),
                new TestQuestion("mg103", "grammar", "They ___ in a big house.", List.of("live", "lives", "living", "lived"), "live", "a1"),
                new TestQuestion("mg104", "grammar", "___ you like pizza?", List.of("Do", "Does", "Is", "Are"), "Do", "a1"),
                new TestQuestion("mg105", "grammar", "He ___ to school by bus.", List.of("go", "goes", "going", "gone"), "goes", "a1"),
                new TestQuestion("mg106", "grammar", "We ___ have a car.", List.of("doesn't", "don't", "isn't", "aren't"), "don't", "a1"),
                new TestQuestion("mg107", "grammar", "This is ___ apple.", List.of("a", "an", "the", "no article"), "an", "a1"),
                new TestQuestion("mg108", "grammar", "My mother ___ a nurse.", List.of("is", "am", "are", "be"), "is", "a1"),
                new TestQuestion("mg109", "grammar", "The ___ are in the garden.", List.of("child", "childs", "children", "childrens"), "children", "a1"),
                new TestQuestion("mg110", "grammar", "I ___ two brothers.", List.of("has", "have", "having", "had"), "have", "a1")
        ));
        grammarByLevel.put("a2", List.of(
                new TestQuestion("mg11", "grammar", "I ___ to the cinema yesterday.", List.of("go", "goes", "went", "going"), "went", "a2"),
                new TestQuestion("mg12", "grammar", "She ___ her homework yet.", List.of("didn't finish", "hasn't finished", "doesn't finish", "won't finish"), "hasn't finished", "a2"),
                new TestQuestion("mg13", "grammar", "We are ___ than them.", List.of("tall", "taller", "tallest", "more tall"), "taller", "a2"),
                new TestQuestion("mg14", "grammar", "You ___ study harder.", List.of("should", "can", "may", "would"), "should", "a2"),
                new TestQuestion("mg15", "grammar", "I was ___ when the phone rang.", List.of("sleep", "slept", "sleeping", "sleeps"), "sleeping", "a2"),
                new TestQuestion("mg16", "grammar", "This is the ___ movie I have ever seen.", List.of("good", "better", "best", "most good"), "best", "a2"),
                new TestQuestion("mg17", "grammar", "He ___ be at home. His car is outside.", List.of("must", "can't", "shouldn't", "needn't"), "must", "a2"),
                new TestQuestion("mg18", "grammar", "I'll call you when I ___ home.", List.of("get", "will get", "got", "getting"), "get", "a2"),
                new TestQuestion("mg19", "grammar", "She asked me where I ___.", List.of("live", "lived", "living", "lives"), "lived", "a2"),
                new TestQuestion("mg20", "grammar", "The letter ___ yesterday.", List.of("sent", "was sent", "sends", "is sending"), "was sent", "a2"),
                new TestQuestion("mg201", "grammar", "We ___ to the beach last summer.", List.of("go", "goes", "went", "going"), "went", "a2"),
                new TestQuestion("mg202", "grammar", "She is ___ to visit her grandmother.", List.of("go", "goes", "going", "went"), "going", "a2"),
                new TestQuestion("mg203", "grammar", "My house is ___ than yours.", List.of("big", "bigger", "biggest", "more big"), "bigger", "a2"),
                new TestQuestion("mg204", "grammar", "I ___ born in 1995.", List.of("am", "was", "were", "is"), "was", "a2"),
                new TestQuestion("mg205", "grammar", "They ___ watch TV yesterday.", List.of("don't", "doesn't", "didn't", "aren't"), "didn't", "a2"),
                new TestQuestion("mg206", "grammar", "He is the ___ student in the class.", List.of("smart", "smarter", "smartest", "most smart"), "smartest", "a2"),
                new TestQuestion("mg207", "grammar", "She arrived ___ the morning.", List.of("in", "on", "at", "by"), "in", "a2"),
                new TestQuestion("mg208", "grammar", "___ you play football when you were young?", List.of("Do", "Did", "Are", "Were"), "Did", "a2"),
                new TestQuestion("mg209", "grammar", "The film was ___ than the book.", List.of("good", "better", "best", "more good"), "better", "a2"),
                new TestQuestion("mg210", "grammar", "I ___ my keys yesterday.", List.of("lose", "lost", "losing", "loses"), "lost", "a2")
        ));
        grammarByLevel.put("b1", List.of(
                new TestQuestion("mg21", "grammar", "If it rains, we ___ inside.", List.of("stay", "will stay", "stayed", "would stay"), "will stay", "b1"),
                new TestQuestion("mg22", "grammar", "I wish I ___ more time.", List.of("have", "had", "has", "having"), "had", "b1"),
                new TestQuestion("mg23", "grammar", "By the time he arrived, we ___.", List.of("left", "have left", "had left", "leave"), "had left", "b1"),
                new TestQuestion("mg24", "grammar", "She suggested ___ a break.", List.of("take", "to take", "taking", "took"), "taking", "b1"),
                new TestQuestion("mg25", "grammar", "The report ___ by tomorrow.", List.of("will finish", "will be finished", "finishes", "has finished"), "will be finished", "b1"),
                new TestQuestion("mg26", "grammar", "He denied ___ the window.", List.of("break", "to break", "breaking", "broke"), "breaking", "b1"),
                new TestQuestion("mg27", "grammar", "I'd rather you ___ later.", List.of("come", "came", "coming", "will come"), "came", "b1"),
                new TestQuestion("mg28", "grammar", "Not until I got home ___ I realise my mistake.", List.of("do", "did", "have", "was"), "did", "b1"),
                new TestQuestion("mg29", "grammar", "She is used to ___ early.", List.of("wake", "waking", "woke", "waken"), "waking", "b1"),
                new TestQuestion("mg30", "grammar", "He must ___ the train. He is never late.", List.of("miss", "have missed", "missing", "be missing"), "have missed", "b1"),
                new TestQuestion("mg301", "grammar", "I ___ to London three times.", List.of("went", "have been", "go", "am going"), "have been", "b1"),
                new TestQuestion("mg302", "grammar", "If I ___ rich, I would travel the world.", List.of("am", "was", "were", "will be"), "were", "b1"),
                new TestQuestion("mg303", "grammar", "The woman ___ lives next door is a doctor.", List.of("which", "who", "whom", "whose"), "who", "b1"),
                new TestQuestion("mg304", "grammar", "She ___ working here since 2020.", List.of("is", "was", "has been", "had been"), "has been", "b1"),
                new TestQuestion("mg305", "grammar", "I ___ play the piano when I was a child.", List.of("use to", "used to", "was used to", "am used to"), "used to", "b1"),
                new TestQuestion("mg306", "grammar", "The book ___ you lent me was excellent.", List.of("who", "which", "whom", "whose"), "which", "b1"),
                new TestQuestion("mg307", "grammar", "If it ___ tomorrow, we'll cancel the picnic.", List.of("rain", "rains", "rained", "will rain"), "rains", "b1"),
                new TestQuestion("mg308", "grammar", "I've ___ finished my homework.", List.of("yet", "already", "still", "since"), "already", "b1"),
                new TestQuestion("mg309", "grammar", "He might ___ late for the meeting.", List.of("is", "be", "being", "been"), "be", "b1"),
                new TestQuestion("mg310", "grammar", "I wish I ___ speak French fluently.", List.of("can", "could", "would", "should"), "could", "b1")
        ));
        QUESTION_BANK.put("grammar", grammarByLevel);

        // =============================================
        // LISTENING questions (20 per level)
        // =============================================
        Map<String, List<TestQuestion>> listeningByLevel = new HashMap<>();
        listeningByLevel.put("a1", List.of(
                new TestQuestion("ml1", "listening", "Which word sounds like 'too'?", List.of("two", "to", "both A and B", "neither"), "both A and B", "a1"),
                new TestQuestion("ml2", "listening", "Which word rhymes with 'ball'?", List.of("bell", "bill", "tall", "bull"), "tall", "a1"),
                new TestQuestion("ml3", "listening", "Which word has two syllables?", List.of("dog", "happy", "cat", "fish"), "happy", "a1"),
                new TestQuestion("ml4", "listening", "Which pair sounds the same?", List.of("write/right", "read/red", "where/were", "all of them"), "write/right", "a1"),
                new TestQuestion("ml5", "listening", "Which word ends with a 'k' sound?", List.of("back", "bag", "bad", "ban"), "back", "a1"),
                new TestQuestion("ml6", "listening", "How many syllables does 'elephant' have?", List.of("2", "3", "4", "1"), "3", "a1"),
                new TestQuestion("ml7", "listening", "Which word starts with a 'sh' sound?", List.of("chair", "shop", "check", "cheap"), "shop", "a1"),
                new TestQuestion("ml8", "listening", "Which word rhymes with 'day'?", List.of("die", "dew", "say", "do"), "say", "a1"),
                new TestQuestion("ml9", "listening", "Which word has a long 'e' sound?", List.of("bed", "bead", "bid", "bad"), "bead", "a1"),
                new TestQuestion("ml10", "listening", "Which word rhymes with 'sun'?", List.of("son", "sin", "san", "soon"), "son", "a1"),
                new TestQuestion("ml101", "listening", "Which word rhymes with 'cat'?", List.of("cut", "hat", "hot", "hit"), "hat", "a1"),
                new TestQuestion("ml102", "listening", "Which word has one syllable?", List.of("apple", "house", "banana", "table"), "house", "a1"),
                new TestQuestion("ml103", "listening", "Which word starts with a 'b' sound?", List.of("phone", "vine", "bus", "fun"), "bus", "a1"),
                new TestQuestion("ml104", "listening", "Which word rhymes with 'look'?", List.of("luck", "book", "lake", "lock"), "book", "a1"),
                new TestQuestion("ml105", "listening", "Which word has a 'k' sound at the start?", List.of("city", "cat", "cell", "cent"), "cat", "a1"),
                new TestQuestion("ml106", "listening", "How many syllables in 'water'?", List.of("1", "2", "3", "4"), "2", "a1"),
                new TestQuestion("ml107", "listening", "Which word rhymes with 'go'?", List.of("do", "no", "to", "so"), "no", "a1"),
                new TestQuestion("ml108", "listening", "Which word has a short 'a' sound?", List.of("cake", "car", "cat", "came"), "cat", "a1"),
                new TestQuestion("ml109", "listening", "Which word ends with an 'n' sound?", List.of("map", "man", "mat", "mad"), "man", "a1"),
                new TestQuestion("ml110", "listening", "Which two words sound the same?", List.of("see/sea", "sit/set", "sell/sail", "say/sigh"), "see/sea", "a1")
        ));
        QUESTION_BANK.put("listening", listeningByLevel);

        // =============================================
        // PRONUNCIATION questions (20 per level)
        // =============================================
        Map<String, List<TestQuestion>> pronByLevel = new HashMap<>();
        pronByLevel.put("a1", List.of(
                new TestQuestion("mp1", "pronunciation", "How many syllables in 'banana'?", List.of("2", "3", "4", "1"), "3", "a1"),
                new TestQuestion("mp2", "pronunciation", "Where is the stress in 'information'?", List.of("IN-for-ma-tion", "in-FOR-ma-tion", "in-for-MA-tion", "in-for-ma-TION"), "in-for-MA-tion", "a1"),
                new TestQuestion("mp3", "pronunciation", "Which letter is silent in 'knife'?", List.of("n", "k", "i", "e"), "k", "a1"),
                new TestQuestion("mp4", "pronunciation", "Which word has a silent 'b'?", List.of("club", "climb", "cab", "crab"), "climb", "a1"),
                new TestQuestion("mp5", "pronunciation", "How is 'th' pronounced in 'the'?", List.of("like 't'", "like 'd'", "voiced th", "voiceless th"), "voiced th", "a1"),
                new TestQuestion("mp6", "pronunciation", "How many syllables in 'chocolate'?", List.of("2", "3", "4", "1"), "3", "a1"),
                new TestQuestion("mp7", "pronunciation", "Where is the stress in 'hotel'?", List.of("HO-tel", "ho-TEL", "equal stress", "no stress"), "ho-TEL", "a1"),
                new TestQuestion("mp8", "pronunciation", "Which word has a different 'a' sound: 'cat', 'car', 'can', 'cap'?", List.of("cat", "car", "can", "cap"), "car", "a1"),
                new TestQuestion("mp9", "pronunciation", "How is the 's' in 'dogs' pronounced?", List.of("like 's'", "like 'z'", "like 'iz'", "silent"), "like 'z'", "a1"),
                new TestQuestion("mp10", "pronunciation", "Which word starts with a different sound?", List.of("city", "circle", "cat", "cent"), "cat", "a1"),
                new TestQuestion("mp101", "pronunciation", "How many syllables in 'telephone'?", List.of("2", "3", "4", "1"), "3", "a1"),
                new TestQuestion("mp102", "pronunciation", "Which letter is silent in 'write'?", List.of("w", "r", "i", "e"), "w", "a1"),
                new TestQuestion("mp103", "pronunciation", "Where is the stress in 'teacher'?", List.of("TEA-cher", "tea-CHER", "equal stress", "no stress"), "TEA-cher", "a1"),
                new TestQuestion("mp104", "pronunciation", "How many syllables in 'computer'?", List.of("2", "3", "4", "1"), "3", "a1"),
                new TestQuestion("mp105", "pronunciation", "Which word rhymes with 'blue'?", List.of("blow", "true", "blood", "build"), "true", "a1"),
                new TestQuestion("mp106", "pronunciation", "Which word has the 'ee' sound?", List.of("bread", "break", "tree", "bear"), "tree", "a1"),
                new TestQuestion("mp107", "pronunciation", "How is 'ph' in 'phone' pronounced?", List.of("like 'p'", "like 'f'", "like 'v'", "silent"), "like 'f'", "a1"),
                new TestQuestion("mp108", "pronunciation", "Where is the stress in 'doctor'?", List.of("DOC-tor", "doc-TOR", "equal stress", "no stress"), "DOC-tor", "a1"),
                new TestQuestion("mp109", "pronunciation", "Which word has a silent 'e'?", List.of("me", "cake", "the", "be"), "cake", "a1"),
                new TestQuestion("mp110", "pronunciation", "How many syllables in 'family'?", List.of("2", "3", "4", "1"), "3", "a1")
        ));
        QUESTION_BANK.put("pronunciation", pronByLevel);
    }

    @Transactional(readOnly = true)
    public List<TestQuestion> execute(String moduleName, String level) {
        Map<String, List<TestQuestion>> moduleQuestions = QUESTION_BANK.get(moduleName.toLowerCase());
        if (moduleQuestions == null) {
            return List.of();
        }
        List<TestQuestion> levelQuestions = moduleQuestions.get(level.toLowerCase());
        if (levelQuestions == null) {
            return List.of();
        }
        return new ArrayList<>(levelQuestions);
    }
}
