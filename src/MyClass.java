import java.util.*;

    // Abstract User class
    abstract class User {
        protected String id, name, dept, email, password, subject;

        public User(String id, String name, String dept, String email, String password, String subject) {
            this.id = id;
            this.name = name;
            this.dept = dept;
            this.email = email;
            this.password = password;
            this.subject = subject;
        }

        public boolean login(String enteredId, String enteredPass) {
            return this.id.equals(enteredId) && this.password.equals(enteredPass);
        }

        public abstract void showProfile(); 
    }

    // Teacher class
    class Teacher extends User {
        List<Quiz> quizList = new ArrayList<>();

        public Teacher(String id, String name, String dept, String email, String password, String subject) {
            super(id, name, dept, email, password, subject);
        }

        public void showProfile() {
            System.out.println("Teacher Profile:");
            System.out.println("ID: " + id + ", Name: " + name + ", Dept: " + dept + ", Subject: " + subject);
        }

        public Quiz createQuiz(String quizTitle) {
            Quiz quiz = new Quiz(quizTitle, this.id);
            quizList.add(quiz);
            return quiz;
        }
    }

    // Student class
    class Student extends User {
        List<Result> results = new ArrayList<>();

        public Student(String id, String name, String dept, String email, String password, String subject) {
            super(id, name, dept, email, password, subject);
        }

        public void showProfile() {
            System.out.println("Student Profile:");
            System.out.println("ID: " + id + ", Name: " + name + ", Dept: " + dept);
        }

        public void attemptQuiz(Quiz quiz, Scanner sc) {
            for (Result r : results) {
                if (r.getQuizId() == quiz.getId()) {
                    System.out.println("You've already attempted this quiz.");
                    return;
                }
            }

            int correct = 0;
            List<Question> qs = quiz.getQuestions();
            for (Question q : qs) {
                System.out.println("\n" + q.getText());
                q.displayOptions();
                int ans = 0;
                while (true) {
                    System.out.print("Enter correct option (1-4): ");
                    if (sc.hasNextInt()) {
                        ans = sc.nextInt();
                        sc.nextLine();
                        if (ans >= 1 && ans <= 4) break;
                    } else {
                        sc.nextLine();
                    }
                    System.out.println("[Invalid] Please enter a number (1-4).");
                }
                if (q.isCorrect(ans)) correct++;
            }

            int wrong = qs.size() - correct;
            Result res = new Result(this.id, quiz.getId(), correct, wrong);
            results.add(res);
            System.out.println("Quiz Completed! Correct: " + correct + ", Wrong: " + wrong);
        }

        public void viewResults() {
            if (results.isEmpty()) {
                System.out.println("No results to show!");
                return;
            }
            for (Result r : results) {
                System.out.println("Quiz ID: " + r.getQuizId() + " | Score: " + r.getScore() + "/" + (r.getCorrect() + r.getWrong()));
            }
        }
    }

    // Quiz class
    class Quiz {
        private static int counter = 1;
        private int quizId;
        private String title;
        private String teacherId;
        private List<Question> questions;

        public Quiz(String title, String teacherId) {
            this.quizId = counter++;
            this.title = title;
            this.teacherId = teacherId;
            this.questions = new ArrayList<>();
        }

        public void addQuestion(String text, String[] options, int correctAns) {
            questions.add(new Question(text, options, correctAns));
        }

        public List<Question> getQuestions() {
            return questions;
        }

        public int getId() {
            return quizId;
        }

        public String getTitle() {
            return title;
        }
    }

    // Question class
    class Question {
        private String text;
        private String[] options;
        private int correct;

        public Question(String text, String[] options, int correct) {
            this.text = text;
            this.options = options;
            this.correct = correct;
        }

        public String getText() {
            return text;
        }

        public void displayOptions() {
            for (int i = 0; i < options.length; i++) {
                System.out.println((i + 1) + ": " + options[i]);
            }
        }

        public boolean isCorrect(int choice) {
            return choice == correct;
        }
    }

    // Result class
    class Result {
        private String studentId;
        private int quizId;
        private int correct, wrong;

        public Result(String studentId, int quizId, int correct, int wrong) {
            this.studentId = studentId;
            this.quizId = quizId;
            this.correct = correct;
            this.wrong = wrong;
        }

        public int getQuizId() {
            return quizId;
        }

        public int getCorrect() {
            return correct;
        }

        public int getWrong() {
            return wrong;
        }

        public int getScore() {
            return correct;
        }
    }

    // Main class
    public class MyClass {
        static Map<String, Teacher> teachers = new HashMap<>();
        static Map<String, Student> students = new HashMap<>();
        static Scanner sc = new Scanner(System.in);

        public static void main(String[] args) {
            while (true) {
                System.out.println("\n1) Register\n2) Login\n3) Exit");
                int choice = inputInt(1,3);
                if (choice == 1) register();
                else if (choice == 2) login();
                else break;
            }
            sc.close();
        }

        static int inputInt(int min, int max) {
            int val = -1;
            while (true) {
                if (sc.hasNextInt()) {
                    val = sc.nextInt();
                    sc.nextLine();
                    if (val >= min && val <= max) return val;
                } else {
                    sc.nextLine();
                }
                System.out.print("Enter valid number ("+min+"-"+max+"): ");
            }
        }

        static void register() {
            System.out.println("Enter 1 for Teacher, 2 for Student:");
            int role = inputInt(1, 2);
            System.out.print("Name: "); String name = sc.nextLine();
            System.out.print("ID (unique): "); String id = sc.nextLine();
            if (teachers.containsKey(id) || students.containsKey(id)) {
                System.out.println("This ID is already taken.");
                return;
            }
            System.out.print("Dept: "); String dept = sc.nextLine();
            System.out.print("Email: "); String email = sc.nextLine();
            System.out.print("Password: "); String pass = sc.nextLine();
            System.out.print("Subject: "); String sub = sc.nextLine();

            if (role == 1) {
                teachers.put(id, new Teacher(id, name, dept, email, pass, sub));
            } else {
                students.put(id, new Student(id, name, dept, email, pass, sub));
            }
            System.out.println("Registered successfully.");
        }

        static void login() {
            System.out.print("Enter ID: "); String id = sc.nextLine();
            System.out.print("Enter Password: "); String pass = sc.nextLine();

            if (teachers.containsKey(id) && teachers.get(id).login(id, pass)) {
                teacherPanel(teachers.get(id));
            } else if (students.containsKey(id) && students.get(id).login(id, pass)) {
                studentPanel(students.get(id));
            } else {
                System.out.println("Invalid ID or password.");
            }
        }

        static void teacherPanel(Teacher t) {
            t.showProfile();
            while (true) {
                System.out.println("\n1. Create Quiz\n2. Logout");
                int c = inputInt(1,2);
                if (c == 1) {
                    System.out.print("Enter Quiz Title: ");
                    String title = sc.nextLine();
                    Quiz quiz = t.createQuiz(title);
                    System.out.print("Number of Questions: ");
                    int n = inputInt(1,100);
                    for (int i = 0; i < n; i++) {
                        System.out.print("Q" + (i + 1) + ": ");
                        String text = sc.nextLine();
                        String[] options = new String[4];
                        for (int j = 0; j < 4; j++) {
                            System.out.print("Option " + (j + 1) + ": ");
                            options[j] = sc.nextLine();
                        }
                        System.out.print("Correct Option (1-4): ");
                        int correct = inputInt(1,4);
                        quiz.addQuestion(text, options, correct);
                    }
                    System.out.println("Quiz created successfully.");
                } else break;
            }
        }

        static void studentPanel(Student s) {
            s.showProfile();
            while (true) {
                System.out.println("\n1. Attempt Quiz\n2. View Result\n3. Logout");
                int c = inputInt(1,3);
                if (c == 1) {
                    List<Quiz> allQuizzes = new ArrayList<>();
                    for (Teacher t : teachers.values()) {
                        allQuizzes.addAll(t.quizList);
                    }
                    if (allQuizzes.isEmpty()) {
                        System.out.println("No quizzes available!");
                        continue;
                    }
                    System.out.println("--- Available Quizzes ---");
                    for (Quiz q : allQuizzes) {
                        System.out.println("Quiz ID: " + q.getId() + " | Title: " + q.getTitle());
                    }
                    System.out.print("Enter Quiz ID to attempt: ");
                    int qid = inputInt(1, Integer.MAX_VALUE);
                    Quiz selQuiz = null;
                    for (Quiz q : allQuizzes) {
                        if (q.getId() == qid) {
                            selQuiz = q;
                            break;
                        }
                    }
                    if (selQuiz == null) {
                        System.out.println("No quiz with this ID.");
                    } else {
                        s.attemptQuiz(selQuiz, sc);
                    }
                } else if (c == 2) {
                    s.viewResults();
                } else break;
            }
        }
    }


