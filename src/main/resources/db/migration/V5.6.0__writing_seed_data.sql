-- Seed data: Writing exercises for all CEFR levels

INSERT INTO writing_exercises (id, prompt, level, topic, min_words, max_words, created_at) VALUES

-- ===== A1 - Beginner =====
('f1000001-0000-0000-0000-000000000001',
'Write about yourself. Include your name, age, where you live, and what you like to do in your free time.',
'a1', 'introduction', 30, 80, NOW()),

('f1000001-0000-0000-0000-000000000002',
'Describe your family. How many people are in your family? What are their names? What do they do?',
'a1', 'family', 30, 80, NOW()),

('f1000001-0000-0000-0000-000000000003',
'Write about your favourite food. What is it? When do you eat it? Why do you like it?',
'a1', 'food', 30, 80, NOW()),

-- ===== A2 - Elementary =====
('f2000001-0000-0000-0000-000000000001',
'Write about your last holiday. Where did you go? What did you do? Did you enjoy it? Would you go again?',
'a2', 'travel', 50, 120, NOW()),

('f2000001-0000-0000-0000-000000000002',
'Describe your daily routine on a typical work day or school day. Include times and activities from morning to night.',
'a2', 'daily life', 50, 120, NOW()),

('f2000001-0000-0000-0000-000000000003',
'Write about your best friend. How did you meet? What do you like to do together? Why is this person special to you?',
'a2', 'people', 50, 120, NOW()),

-- ===== B1 - Intermediate =====
('f3000001-0000-0000-0000-000000000001',
'Do you think social media has a positive or negative effect on society? Give reasons and examples to support your opinion.',
'b1', 'technology', 80, 200, NOW()),

('f3000001-0000-0000-0000-000000000002',
'Write about a time when you had to solve a difficult problem. What was the situation? What did you do? What did you learn from the experience?',
'b1', 'personal growth', 80, 200, NOW()),

('f3000001-0000-0000-0000-000000000003',
'Some people prefer to live in a big city, while others prefer the countryside. Compare the advantages and disadvantages of each and explain your preference.',
'b1', 'lifestyle', 80, 200, NOW()),

-- ===== B2 - Upper Intermediate =====
('f4000001-0000-0000-0000-000000000001',
'Many universities now offer online degrees. Discuss the advantages and disadvantages of online education compared to traditional classroom learning. Which do you think is more effective and why?',
'b2', 'education', 120, 300, NOW()),

('f4000001-0000-0000-0000-000000000002',
'Some people believe that governments should invest more in public transport rather than building new roads. To what extent do you agree or disagree? Support your argument with specific examples.',
'b2', 'environment', 120, 300, NOW()),

('f4000001-0000-0000-0000-000000000003',
'Write a review of a book, film, or TV series that has had a significant impact on you. Analyse what makes it compelling and explain why you would or would not recommend it to others.',
'b2', 'culture', 120, 300, NOW()),

-- ===== C1 - Advanced =====
('f5000001-0000-0000-0000-000000000001',
'Critically evaluate the argument that artificial intelligence will create more jobs than it destroys. Consider historical precedents, current trends, and potential future scenarios in your analysis.',
'c1', 'technology', 150, 400, NOW()),

('f5000001-0000-0000-0000-000000000002',
'To what extent should freedom of speech be absolute? Discuss the tension between protecting individual expression and preventing harm, drawing on specific examples from contemporary society.',
'c1', 'society', 150, 400, NOW()),

('f5000001-0000-0000-0000-000000000003',
'Some economists argue that perpetual economic growth is incompatible with environmental sustainability. Examine this claim, considering alternative economic models and their feasibility in the current global context.',
'c1', 'economics', 150, 400, NOW());
