-- Seed data: Reading passages and questions for all CEFR levels

-- ===== A1 - Beginner =====

INSERT INTO reading_passages (id, title, content, level, topic, word_count, created_at) VALUES
('a1000001-0000-0000-0000-000000000001', 'My Daily Routine',
'I wake up at seven o''clock every morning. I brush my teeth and take a shower. Then I eat breakfast. I usually have toast and orange juice. I go to work by bus. I start work at nine o''clock. I have lunch at one o''clock. I eat a sandwich and drink water. I finish work at five o''clock. I go home and cook dinner. I watch TV in the evening. I go to bed at eleven o''clock.',
'a1', 'daily life', 72, NOW()),

('a1000001-0000-0000-0000-000000000002', 'At the Supermarket',
'I go to the supermarket every Saturday. I buy fruit and vegetables. I like apples, bananas and oranges. I also buy milk, bread and cheese. The supermarket is near my house. I walk there. It takes ten minutes. The people who work there are very nice. I pay with my card. I carry my bags home. I put the food in the fridge.',
'a1', 'shopping', 62, NOW());

INSERT INTO reading_questions (id, passage_id, question, options, correct_answer, explanation) VALUES
('b1000001-0000-0000-0000-000000000001', 'a1000001-0000-0000-0000-000000000001',
'What time does the person wake up?', 'Six o''clock|||Seven o''clock|||Eight o''clock|||Nine o''clock', 1,
'The text says "I wake up at seven o''clock every morning."'),
('b1000001-0000-0000-0000-000000000002', 'a1000001-0000-0000-0000-000000000001',
'How does the person go to work?', 'By car|||By train|||By bus|||On foot', 2,
'The text says "I go to work by bus."'),
('b1000001-0000-0000-0000-000000000003', 'a1000001-0000-0000-0000-000000000001',
'What does the person have for breakfast?', 'Cereal and milk|||Toast and orange juice|||Eggs and coffee|||Fruit and yogurt', 1,
'The text says "I usually have toast and orange juice."'),

('b1000001-0000-0000-0000-000000000004', 'a1000001-0000-0000-0000-000000000002',
'When does the person go to the supermarket?', 'Every Monday|||Every Friday|||Every Saturday|||Every Sunday', 2,
'The text says "I go to the supermarket every Saturday."'),
('b1000001-0000-0000-0000-000000000005', 'a1000001-0000-0000-0000-000000000002',
'How does the person get to the supermarket?', 'By bus|||By car|||By bicycle|||On foot', 3,
'The text says "I walk there."'),
('b1000001-0000-0000-0000-000000000006', 'a1000001-0000-0000-0000-000000000002',
'How does the person pay?', 'With cash|||With a card|||With a check|||With a phone', 1,
'The text says "I pay with my card."');

-- ===== A2 - Elementary =====

INSERT INTO reading_passages (id, title, content, level, topic, word_count, created_at) VALUES
('a2000001-0000-0000-0000-000000000001', 'A Weekend Trip',
'Last weekend, my friend Sara and I went to the beach. We left early in the morning because it was a two-hour drive. The weather was sunny and warm. We swam in the sea and played volleyball on the sand. For lunch, we ate fish and chips at a small restaurant near the beach. In the afternoon, we walked along the coast and took many photos. We saw some beautiful birds and a few crabs on the rocks. We were tired but happy when we got home. It was a wonderful day and we want to go again next month.',
'a2', 'travel', 101, NOW()),

('a2000001-0000-0000-0000-000000000002', 'My New Neighbour',
'A new family moved into the house next to mine last month. Their names are Tom and Maria, and they have two children, a boy called Jake and a girl called Lily. Tom works as a mechanic and Maria is a nurse at the local hospital. Jake is eight years old and Lily is five. They are very friendly people. Last Sunday, they invited us for a barbecue in their garden. Maria made a delicious salad and Tom cooked burgers on the grill. The children played together in the garden. I think we are going to be good friends.',
'a2', 'people', 107, NOW());

INSERT INTO reading_questions (id, passage_id, question, options, correct_answer, explanation) VALUES
('b2000001-0000-0000-0000-000000000001', 'a2000001-0000-0000-0000-000000000001',
'How long was the drive to the beach?', 'One hour|||Two hours|||Three hours|||Thirty minutes', 1,
'The text says "it was a two-hour drive."'),
('b2000001-0000-0000-0000-000000000002', 'a2000001-0000-0000-0000-000000000001',
'What did they eat for lunch?', 'Pizza|||Sandwiches|||Fish and chips|||Burgers', 2,
'The text says "we ate fish and chips at a small restaurant."'),
('b2000001-0000-0000-0000-000000000003', 'a2000001-0000-0000-0000-000000000001',
'What did they see on the rocks?', 'Fish|||Shells|||Crabs|||Starfish', 2,
'The text says "We saw some beautiful birds and a few crabs on the rocks."'),

('b2000001-0000-0000-0000-000000000004', 'a2000001-0000-0000-0000-000000000002',
'What does Tom do for work?', 'He is a doctor|||He is a mechanic|||He is a teacher|||He is a cook', 1,
'The text says "Tom works as a mechanic."'),
('b2000001-0000-0000-0000-000000000005', 'a2000001-0000-0000-0000-000000000002',
'How old is Lily?', 'Three|||Four|||Five|||Eight', 2,
'The text says "Lily is five."'),
('b2000001-0000-0000-0000-000000000006', 'a2000001-0000-0000-0000-000000000002',
'What happened last Sunday?', 'They went to a restaurant|||They had a barbecue|||They went shopping|||They watched a film', 1,
'The text says "they invited us for a barbecue in their garden."');

-- ===== B1 - Intermediate =====

INSERT INTO reading_passages (id, title, content, level, topic, word_count, created_at) VALUES
('b1100001-0000-0000-0000-000000000001', 'The Benefits of Learning a Second Language',
'Learning a second language has many advantages that go beyond simple communication. Research shows that bilingual people often have better memory and problem-solving skills. When you learn a new language, your brain creates new neural pathways, which helps keep your mind sharp as you age. Additionally, speaking another language can improve your career opportunities. Many companies prefer employees who can communicate with international clients. Studies have also shown that learning a language increases empathy because you begin to understand different cultures and ways of thinking. Of course, it requires dedication and practice, but the rewards are well worth the effort. Whether you choose to learn through classes, apps, or conversation with native speakers, the key is consistency. Even fifteen minutes of daily practice can lead to significant progress over time.',
'b1', 'education', 135, NOW()),

('b1100001-0000-0000-0000-000000000002', 'Remote Work: The New Normal',
'The way we work has changed dramatically in recent years. Before the pandemic, only a small percentage of workers did their jobs from home. Now, remote work has become common in many industries. There are clear benefits to working from home. Employees save time and money on commuting, and they often report better work-life balance. Companies can also save on office space and attract talent from anywhere in the world. However, remote work is not without its challenges. Some people find it difficult to separate their personal and professional lives when both happen in the same space. Others miss the social interaction that comes with an office environment. Communication can also be harder when teams are spread across different locations and time zones. Many organisations are now adopting a hybrid model, where employees work from home some days and go to the office on others.',
'b1', 'work', 158, NOW());

INSERT INTO reading_questions (id, passage_id, question, options, correct_answer, explanation) VALUES
('c1000001-0000-0000-0000-000000000001', 'b1100001-0000-0000-0000-000000000001',
'What happens to the brain when you learn a new language?', 'It becomes smaller|||It creates new neural pathways|||It stops developing|||It loses old memories', 1,
'The text says "your brain creates new neural pathways."'),
('c1000001-0000-0000-0000-000000000002', 'b1100001-0000-0000-0000-000000000001',
'Why does learning a language increase empathy?', 'Because you travel more|||Because you read more books|||Because you understand different cultures|||Because you make more friends', 2,
'The text says "learning a language increases empathy because you begin to understand different cultures."'),
('c1000001-0000-0000-0000-000000000003', 'b1100001-0000-0000-0000-000000000001',
'How much daily practice does the text recommend?', 'Five minutes|||Fifteen minutes|||Thirty minutes|||One hour', 1,
'The text says "Even fifteen minutes of daily practice can lead to significant progress."'),

('c1000001-0000-0000-0000-000000000004', 'b1100001-0000-0000-0000-000000000002',
'What is one benefit of remote work for companies?', 'Higher salaries|||More meetings|||Saving on office space|||More employees', 2,
'The text says "Companies can also save on office space."'),
('c1000001-0000-0000-0000-000000000005', 'b1100001-0000-0000-0000-000000000002',
'What challenge do some remote workers face?', 'They earn less money|||They cannot use technology|||They find it hard to separate personal and work life|||They have to travel more', 2,
'The text says "Some people find it difficult to separate their personal and professional lives."'),
('c1000001-0000-0000-0000-000000000006', 'b1100001-0000-0000-0000-000000000002',
'What is a hybrid work model?', 'Working only from home|||Working only from the office|||A mix of home and office work|||Working in different countries', 2,
'The text explains a hybrid model as "employees work from home some days and go to the office on others."');

-- ===== B2 - Upper Intermediate =====

INSERT INTO reading_passages (id, title, content, level, topic, word_count, created_at) VALUES
('b2100001-0000-0000-0000-000000000001', 'The Psychology of Decision Making',
'Every day, the average person makes approximately 35,000 decisions, ranging from trivial choices like what to eat for breakfast to significant ones that can alter the course of their life. Psychologists have identified several cognitive biases that influence our decision-making process, often without our awareness. Confirmation bias, for instance, leads us to seek information that supports our existing beliefs while ignoring contradictory evidence. The anchoring effect causes us to rely too heavily on the first piece of information we receive when making judgements. Perhaps most concerning is the sunk cost fallacy, where we continue investing in something simply because we have already invested time or resources, even when it would be more rational to cut our losses. Understanding these biases does not make us immune to them, but it can help us make more deliberate and thoughtful choices. Experts recommend several strategies: taking time before making important decisions, seeking diverse perspectives, and actively looking for reasons why your initial instinct might be wrong.',
'b2', 'psychology', 167, NOW()),

('b2100001-0000-0000-0000-000000000002', 'Sustainable Cities of the Future',
'As urban populations continue to grow, city planners and architects are reimagining what sustainable cities could look like. Copenhagen has set an ambitious goal of becoming carbon neutral, investing heavily in cycling infrastructure and wind energy. Singapore, despite its limited land area, has pioneered the concept of vertical gardens, integrating greenery into skyscrapers and public buildings to combat the urban heat island effect. Barcelona has implemented "superblocks," redesigning city grids to prioritise pedestrians and cyclists over cars, resulting in reduced air pollution and increased community interaction. These initiatives share a common philosophy: that sustainability and quality of life are not mutually exclusive but rather deeply interconnected. Critics argue that such transformations are expensive and may increase housing costs, potentially displacing lower-income residents. However, proponents counter that the long-term savings in healthcare, energy, and environmental remediation far outweigh the initial investment. The challenge lies not in the technology, which already exists, but in the political will to implement these changes at scale.',
'b2', 'environment', 172, NOW());

INSERT INTO reading_questions (id, passage_id, question, options, correct_answer, explanation) VALUES
('d1000001-0000-0000-0000-000000000001', 'b2100001-0000-0000-0000-000000000001',
'What is confirmation bias?', 'Making decisions too quickly|||Seeking information that supports existing beliefs|||Relying on the first piece of information|||Continuing to invest despite losses', 1,
'The text defines confirmation bias as seeking "information that supports our existing beliefs while ignoring contradictory evidence."'),
('d1000001-0000-0000-0000-000000000002', 'b2100001-0000-0000-0000-000000000001',
'What is the sunk cost fallacy?', 'Ignoring all previous investments|||Making decisions based on emotions only|||Continuing to invest because of past investment, even when irrational|||Always choosing the cheapest option', 2,
'The text describes it as continuing "investing in something simply because we have already invested time or resources."'),
('d1000001-0000-0000-0000-000000000003', 'b2100001-0000-0000-0000-000000000001',
'What do experts recommend to counteract biases?', 'Trust your first instinct|||Avoid making decisions altogether|||Seek diverse perspectives and challenge initial instincts|||Only rely on data and statistics', 2,
'The text recommends "seeking diverse perspectives, and actively looking for reasons why your initial instinct might be wrong."'),

('d1000001-0000-0000-0000-000000000004', 'b2100001-0000-0000-0000-000000000002',
'What has Singapore pioneered?', 'Cycling infrastructure|||Carbon neutral goals|||Vertical gardens|||Superblocks', 2,
'The text says "Singapore has pioneered the concept of vertical gardens."'),
('d1000001-0000-0000-0000-000000000005', 'b2100001-0000-0000-0000-000000000002',
'What are Barcelona''s "superblocks"?', 'Large shopping centres|||Redesigned city grids prioritising pedestrians|||Tall residential buildings|||Public transport hubs', 1,
'The text describes them as "redesigning city grids to prioritise pedestrians and cyclists over cars."'),
('d1000001-0000-0000-0000-000000000006', 'b2100001-0000-0000-0000-000000000002',
'According to the text, what is the main challenge for sustainable cities?', 'Lack of technology|||Insufficient funding|||Lack of political will|||Resistance from citizens', 2,
'The text says "The challenge lies not in the technology... but in the political will to implement these changes."');

-- ===== C1 - Advanced =====

INSERT INTO reading_passages (id, title, content, level, topic, word_count, created_at) VALUES
('c1100001-0000-0000-0000-000000000001', 'The Paradox of Choice in Modern Society',
'In his seminal work, psychologist Barry Schwartz argues that the abundance of choice in contemporary Western societies, far from liberating us, has become a source of anxiety and dissatisfaction. Schwartz distinguishes between "maximisers," who exhaustively search for the best possible option, and "satisficers," who settle for an option that meets their criteria of acceptability. Research consistently demonstrates that maximisers, despite often making objectively better choices, report lower levels of satisfaction and higher rates of regret. This counterintuitive finding challenges the fundamental assumption of consumer capitalism: that more options invariably lead to greater well-being. The implications extend beyond consumer behaviour into domains such as career planning, relationships, and healthcare. When patients are presented with numerous treatment options, for instance, the burden of choice can lead to decision paralysis and worse health outcomes. Some researchers have proposed that institutions should adopt "libertarian paternalism," designing choice architectures that gently nudge individuals toward beneficial decisions while preserving their freedom to choose otherwise. This approach, however, raises profound ethical questions about who decides what constitutes a beneficial outcome and whether such subtle manipulation undermines genuine autonomy.',
'c1', 'psychology', 184, NOW()),

('c1100001-0000-0000-0000-000000000002', 'Artificial Intelligence and the Future of Creativity',
'The emergence of sophisticated AI systems capable of generating text, images, and music has ignited a fierce debate about the nature of creativity itself. Proponents argue that AI democratises creative expression, enabling individuals without traditional artistic training to realise their visions. Detractors contend that AI-generated content is merely sophisticated pattern matching, devoid of the intentionality and lived experience that underpin genuine creative work. This debate, however, may rest on a false dichotomy. Throughout history, creative tools have evolved and been met with resistance; the camera was initially dismissed as a threat to painting, yet photography eventually emerged as an art form in its own right. What distinguishes human creativity is not the medium or tool employed but the capacity for meaning-making, the ability to imbue a work with significance that resonates with shared human experience. Rather than viewing AI as a replacement for human creativity, it may be more productive to conceive of it as a collaborator that expands the boundaries of what is artistically possible. The most compelling creative works of the future may well emerge from the symbiosis between human imagination and machine capability, challenging our conventional understanding of authorship and originality in ways we have only begun to contemplate.',
'c1', 'technology', 199, NOW());

INSERT INTO reading_questions (id, passage_id, question, options, correct_answer, explanation) VALUES
('e1000001-0000-0000-0000-000000000001', 'c1100001-0000-0000-0000-000000000001',
'According to Schwartz, what is the difference between maximisers and satisficers?', 'Maximisers are happier with their choices|||Satisficers search for the best option|||Maximisers exhaustively search for the best; satisficers accept what meets their criteria|||There is no meaningful difference', 2,
'The text says maximisers "exhaustively search for the best possible option" while satisficers "settle for an option that meets their criteria."'),
('e1000001-0000-0000-0000-000000000002', 'c1100001-0000-0000-0000-000000000001',
'What counterintuitive finding does the text describe?', 'More choices lead to better decisions|||Satisficers make worse choices but feel better|||Maximisers make objectively better choices but feel less satisfied|||People prefer fewer options in all circumstances', 2,
'The text states that maximisers "despite often making objectively better choices, report lower levels of satisfaction."'),
('e1000001-0000-0000-0000-000000000003', 'c1100001-0000-0000-0000-000000000001',
'What ethical concern is raised about libertarian paternalism?', 'It is too expensive to implement|||It removes all freedom of choice|||It raises questions about who decides what is beneficial and whether it undermines autonomy|||It only works in healthcare settings', 2,
'The text raises questions "about who decides what constitutes a beneficial outcome and whether such subtle manipulation undermines genuine autonomy."'),

('e1000001-0000-0000-0000-000000000004', 'c1100001-0000-0000-0000-000000000002',
'What historical parallel does the text draw?', 'The invention of the printing press|||The camera being seen as a threat to painting|||The development of electric instruments in music|||The transition from radio to television', 1,
'The text says "the camera was initially dismissed as a threat to painting, yet photography eventually emerged as an art form."'),
('e1000001-0000-0000-0000-000000000005', 'c1100001-0000-0000-0000-000000000002',
'According to the text, what distinguishes human creativity?', 'The ability to use advanced tools|||Technical artistic skill|||The capacity for meaning-making and shared human experience|||Speed and efficiency of output', 2,
'The text says what distinguishes human creativity is "the capacity for meaning-making, the ability to imbue a work with significance."'),
('e1000001-0000-0000-0000-000000000006', 'c1100001-0000-0000-0000-000000000002',
'How does the author suggest we should view AI in relation to creativity?', 'As a replacement for human artists|||As a threat to originality|||As a collaborator that expands artistic possibilities|||As irrelevant to genuine art', 2,
'The text suggests viewing AI "as a collaborator that expands the boundaries of what is artistically possible."');
