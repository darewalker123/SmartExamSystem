import os

path = r"c:\Users\prath\.gemini\antigravity\scratch\SmartExamSystem\src\database\LoadTestData.java"

with open(path, 'r', encoding='utf-8') as f:
    lines = f.readlines()

insert_statements = """            // 3. Insert Questions for Exam 1 (Java Collections) - 10 Questions
            String q1 = "INSERT INTO questions (exam_id, question_text, option_a, option_b, option_c, option_d, correct_answer) VALUES ";
            stmt.executeUpdate(q1 + "(1, 'Which data structure follows the LIFO principle?', 'Queue', 'Tree', 'Stack', 'Graph', 'C')");
            stmt.executeUpdate(q1 + "(1, 'HashMap is part of which package?', 'java.util', 'java.io', 'java.lang', 'java.net', 'A')");
            stmt.executeUpdate(q1 + "(1, 'Which is not a valid Collection interface?', 'List', 'Set', 'Vector', 'Map', 'C')");
            stmt.executeUpdate(q1 + "(1, 'Time complexity of HashMap get() operation average case?', 'O(n)', 'O(1)', 'O(log n)', 'O(n²)', 'B')");
            stmt.executeUpdate(q1 + "(1, 'Which interface does ArrayList implement?', 'Queue', 'List', 'Map', 'Stack', 'B')");
            stmt.executeUpdate(q1 + "(1, 'Can ArrayList contain duplicate elements?', 'Yes', 'No', 'Depends on JVM', 'Only if synchronized', 'A')");
            stmt.executeUpdate(q1 + "(1, 'Which collection does not allow duplicate elements?', 'ArrayList', 'HashSet', 'Vector', 'LinkedList', 'B')");
            stmt.executeUpdate(q1 + "(1, 'Which class is synchronized or thread-safe?', 'ArrayList', 'Vector', 'LinkedList', 'HashMap', 'B')");
            stmt.executeUpdate(q1 + "(1, 'What is the default initial capacity of an ArrayList in Java?', '10', '16', '0', '100', 'A')");
            stmt.executeUpdate(q1 + "(1, 'Which interface provides FIFO order?', 'List', 'Queue', 'Set', 'Map', 'B')");

            // 4. Insert Questions for Exam 2 (Operating Systems) - 10 Questions
            stmt.executeUpdate(q1 + "(2, 'What is the core or heart of an Operating System?', 'Shell', 'Kernel', 'GUI', 'Driver', 'B')");
            stmt.executeUpdate(q1 + "(2, 'Which is NOT a state of a process?', 'Running', 'Ready', 'Waiting', 'Completed', 'D')");
            stmt.executeUpdate(q1 + "(2, 'What avoids deadlock by dividing memory into equal sized blocks?', 'Paging', 'Fragmentation', 'Swapping', 'Spooling', 'A')");
            stmt.executeUpdate(q1 + "(2, 'A condition where multiple processes are locked waiting for resources?', 'Starvation', 'Deadlock', 'Mutex', 'Semaphore', 'B')");
            stmt.executeUpdate(q1 + "(2, 'Which CPU scheduling algorithm gives priority to arriving shorter tasks?', 'FCFS', 'SJF', 'Round Robin', 'Priority Scheduling', 'B')");
            stmt.executeUpdate(q1 + "(2, 'What is a lightweight process containing its own stack points?', 'Daemon', 'Thread', 'Child', 'System', 'B')");
            stmt.executeUpdate(q1 + "(2, 'Which memory provides virtual expansion back to disk swap space?', 'RAM', 'ROM', 'Virtual Memory', 'Cache', 'C')");
            stmt.executeUpdate(q1 + "(2, 'What is used to solve the critical section problem?', 'Pointers', 'Deadlock', 'Semaphore', 'Index', 'C')");
            stmt.executeUpdate(q1 + "(2, 'What does LRU stand for in page replacement?', 'Last Running Unit', 'Least Recently Used', 'Live Resource Utility', 'Low Rate Usage', 'B')");
            stmt.executeUpdate(q1 + "(2, 'Where does the Operating System load from BIOS during boot?', 'CPU', 'Hard Disk', 'ROM', 'Cache', 'B')");

            // 5. Insert Questions for Exam 3 (Web Development) - 10 Questions
            stmt.executeUpdate(q1 + "(3, 'What does HTML stand for?', 'Hyper Text Markup Language', 'High Tech Main Links', 'Hyperlinks Medium Logic', 'Home Tool Markup Layout', 'A')");
            stmt.executeUpdate(q1 + "(3, 'Which HTML tag is used for the largest heading?', '<h1>', '<h6>', '<head>', '<header>', 'A')");
            stmt.executeUpdate(q1 + "(3, 'Which property is used in CSS to change background color?', 'color', 'bgcolor', 'background-color', 'bg-color', 'C')");
            stmt.executeUpdate(q1 + "(3, 'Which HTML attribute is used to define inline styles?', 'class', 'style', 'font', 'styles', 'B')");
            stmt.executeUpdate(q1 + "(3, 'Which HTTP status code corresponds to Not Found error?', '200', '403', '404', '500', 'C')");
            stmt.executeUpdate(q1 + "(3, 'What is correct syntax for referring an external script in HTML?', '<script href=\"...\">', '<script src=\"...\">', '<script link=\"...\">', '<link src=\"...\">', 'B')");
            stmt.executeUpdate(q1 + "(3, 'Which CSS layout model provides 1-dimensional item streams?', 'Float', 'Flexbox', 'Grid', 'Relative', 'B')");
            stmt.executeUpdate(q1 + "(3, 'How do you create a function in JavaScript?', 'function:myFunc()', 'function myFunc()', 'def myFunc()', 'void myFunc()', 'B')");
            stmt.executeUpdate(q1 + "(3, 'What is used heavily to scale web displays on mobile views?', 'Frames', 'DIV triggers', 'Media Queries', 'Tables', 'C')");
            stmt.executeUpdate(q1 + "(3, 'Which tag is used specifically to draw graphics via Javascript?', '<canvas>', '<svg>', '<graphics>', '<img>', 'A')");

            // 6. Insert Dynamic Results
            stmt.executeUpdate("INSERT INTO results (student_id, exam_id, score, total_marks, time_taken) VALUES (" + studentId + ", 1, 8, 10, 25), (" + studentId + ", 2, 7, 10, 40)");
"""

full_content = "".join(lines[:29]) + insert_statements + "\n" + "".join(lines[38:])

with open(path, 'w', encoding='utf-8') as f:
    f.write(full_content)

print("Patch applied successfully!")
