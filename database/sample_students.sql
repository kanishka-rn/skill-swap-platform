USE skill_swap_db;

START TRANSACTION;

INSERT IGNORE INTO students
    (name, email, teach_skill, learn_skill, additional_skills)
VALUES
    ('Aanya Rao', 'skillswap.demo01@example.com', 'Java', 'Python', 'C++, SQL'),
    ('Vihaan Iyer', 'skillswap.demo02@example.com', 'Python', 'Java', 'Data Science'),
    ('Meera Nair', 'skillswap.demo03@example.com', 'Web Development', 'UI/UX Design', 'HTML, CSS'),
    ('Arjun Mehta', 'skillswap.demo04@example.com', 'UI/UX Design', 'Web Development', 'Photoshop'),
    ('Ishita Kapoor', 'skillswap.demo05@example.com', 'SQL', 'Excel', 'Python'),
    ('Kabir Shah', 'skillswap.demo06@example.com', 'Excel', 'SQL', 'Digital Marketing'),
    ('Saanvi Reddy', 'skillswap.demo07@example.com', 'HTML', 'CSS', 'Web Development'),
    ('Aditya Menon', 'skillswap.demo08@example.com', 'CSS', 'HTML', 'UI/UX Design'),
    ('Diya Kulkarni', 'skillswap.demo09@example.com', 'Data Science', 'Machine Learning', 'Python, Excel'),
    ('Rohan Das', 'skillswap.demo10@example.com', 'Machine Learning', 'Data Science', 'SQL, C++');

SET @sample_students_inserted = ROW_COUNT();
COMMIT;

SELECT @sample_students_inserted AS sample_students_inserted,
       10 - @sample_students_inserted AS sample_students_skipped_existing_email;

SELECT id, name, email, teach_skill, learn_skill, additional_skills
FROM students
WHERE email IN (
    'skillswap.demo01@example.com',
    'skillswap.demo02@example.com',
    'skillswap.demo03@example.com',
    'skillswap.demo04@example.com',
    'skillswap.demo05@example.com',
    'skillswap.demo06@example.com',
    'skillswap.demo07@example.com',
    'skillswap.demo08@example.com',
    'skillswap.demo09@example.com',
    'skillswap.demo10@example.com'
)
ORDER BY email;

WITH expected_profiles AS (
    SELECT 'skillswap.demo01@example.com' AS email, 'Aanya Rao' AS name,
           'Java' AS teach_skill, 'Python' AS learn_skill, 'C++, SQL' AS additional_skills
    UNION ALL SELECT 'skillswap.demo02@example.com', 'Vihaan Iyer',
           'Python', 'Java', 'Data Science'
    UNION ALL SELECT 'skillswap.demo03@example.com', 'Meera Nair',
           'Web Development', 'UI/UX Design', 'HTML, CSS'
    UNION ALL SELECT 'skillswap.demo04@example.com', 'Arjun Mehta',
           'UI/UX Design', 'Web Development', 'Photoshop'
    UNION ALL SELECT 'skillswap.demo05@example.com', 'Ishita Kapoor',
           'SQL', 'Excel', 'Python'
    UNION ALL SELECT 'skillswap.demo06@example.com', 'Kabir Shah',
           'Excel', 'SQL', 'Digital Marketing'
    UNION ALL SELECT 'skillswap.demo07@example.com', 'Saanvi Reddy',
           'HTML', 'CSS', 'Web Development'
    UNION ALL SELECT 'skillswap.demo08@example.com', 'Aditya Menon',
           'CSS', 'HTML', 'UI/UX Design'
    UNION ALL SELECT 'skillswap.demo09@example.com', 'Diya Kulkarni',
           'Data Science', 'Machine Learning', 'Python, Excel'
    UNION ALL SELECT 'skillswap.demo10@example.com', 'Rohan Das',
           'Machine Learning', 'Data Science', 'SQL, C++'
)
SELECT expected.email,
       expected.name AS expected_name,
       actual.name AS existing_name,
       expected.teach_skill AS expected_teach_skill,
       actual.teach_skill AS existing_teach_skill,
       expected.learn_skill AS expected_learn_skill,
       actual.learn_skill AS existing_learn_skill,
       expected.additional_skills AS expected_additional_skills,
       actual.additional_skills AS existing_additional_skills
FROM expected_profiles AS expected
JOIN students AS actual ON actual.email = expected.email
WHERE BINARY actual.name <> BINARY expected.name
   OR BINARY actual.teach_skill <> BINARY expected.teach_skill
   OR BINARY actual.learn_skill <> BINARY expected.learn_skill
   OR BINARY actual.additional_skills <> BINARY expected.additional_skills;

SELECT
    a.name AS student_a,
    a.teach_skill AS a_teaches,
    a.learn_skill AS a_wants,
    b.name AS student_b,
    b.teach_skill AS b_teaches,
    b.learn_skill AS b_wants
FROM students AS a
JOIN students AS b
    ON a.email < b.email
   AND LOWER(TRIM(a.teach_skill)) = LOWER(TRIM(b.learn_skill))
   AND LOWER(TRIM(b.teach_skill)) = LOWER(TRIM(a.learn_skill))
WHERE a.email LIKE 'skillswap.demo%@example.com'
  AND b.email LIKE 'skillswap.demo%@example.com'
ORDER BY a.email, b.email;
