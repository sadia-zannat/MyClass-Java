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
        try {
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
                boolean valid = false;

                while (!valid) {
                    try {
                        System.out.print("Enter correct option (1-4): ");
                        ans = sc.nextInt();
                        sc.nextLine();
                        if (ans >= 1 && ans <= 4) {
                            valid = true;
                        } else {
                            throw new InputMismatchException("Option must be between 1 and 4");
                        }
                    } catch (InputMismatchException e) {
                        System.out.println("Invalid input: " + e.getMessage());
                        sc.nextLine(); // clear buffer
                    }
                }
                if (q.isCorrect(ans)) correct++;
            }

            int wrong = qs.size() - correct;
            Result res = new Result(this.id, quiz.getId(), correct, wrong);
            results.add(res);
            System.out.println("Quiz Completed! Correct: " + correct + ", Wrong: " + wrong);

        } catch (Exception e) {
            System.out.println("Error during quiz attempt: " + e.getMessage());
        }
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

    public void addQuestion(String text, String[] options, int correctIndex) {
        questions.add(new Question(text, options, correctIndex));
    }

    public int getId() { return quizId; }
    public String getTitle() { return title; }
    public List<Question> getQuestions() { return questions; }
}

// Question class
class Question {
    private String text;
    private String[] options;
    private int correctIndex;

    public Question(String text, String[] options, int correctIndex) {
        this.text = text;
        this.options = options;
        this.correctIndex = correctIndex;
    }

    public String getText() { return text; }

    public void displayOptions() {
        for (int i = 0; i < options.length; i++) {
            System.out.println((i + 1) + ". " + options[i]);
        }
    }

    public boolean isCorrect(int ans) {
        return ans == correctIndex;
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

    public int getQuizId() { return quizId; }
    public int getCorrect() { return correct; }
    public int getWrong() { return wrong; }
    public int getScore() { return correct; }
}

// Main class
public class MyClass {
    static Scanner sc = new Scanner(System.in);
    static Map<String, Teacher> teachers = new HashMap<>();
    static Map<String, Student> students = new HashMap<>();

    public static void main(String[] args) {
        while (true) {
            try {
                System.out.println("\n1. Register\n2. Login\n3. Exit");
                int c = inputInt(1, 3);
                if (c == 1) register();
                else if (c == 2) login();
                else break;
            } catch (Exception e) {
                System.out.println("Unexpected error: " + e.getMessage());
            }
        }
    }

    static void register() {
        try {
            System.out.print("Register as (1.Teacher / 2.Student): ");
            int role = inputInt(1, 2);
            System.out.print("ID: "); String id = sc.nextLine();
            System.out.print("Name: "); String name = sc.nextLine();
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
        } catch (Exception e) {
            System.out.println("Error during registration: " + e.getMessage());
        }
    }

    static void login() {
        try {
            System.out.print("Enter ID: "); String id = sc.nextLine();
            System.out.print("Enter Password: "); String pass = sc.nextLine();

            if (teachers.containsKey(id) && teachers.get(id).login(id, pass)) {
                teacherPanel(teachers.get(id));
            } else if (students.containsKey(id) && students.get(id).login(id, pass)) {
                studentPanel(students.get(id));
            } else {
                throw new Exception("Invalid ID or password.");
            }
        } catch (Exception e) {
            System.out.println("Login failed: " + e.getMessage());
        }
    }

    static void teacherPanel(Teacher t) {
        t.showProfile();
        while (true) {
            try {
                System.out.println("\n1. Create Quiz\n2. Logout");
                int c = inputInt(1, 2);
                if (c == 1) {
                    System.out.print("Enter Quiz Title: ");
                    String title = sc.nextLine();
                    Quiz quiz = t.createQuiz(title);
                    System.out.print("Number of Questions: ");
                    int n = inputInt(1, 100);
                    for (int i = 0; i < n; i++) {
                        System.out.print("Q" + (i + 1) + ": ");
                        String text = sc.nextLine();
                        String[] options = new String[4];
                        for (int j = 0; j < 4; j++) {
                            System.out.print("Option " + (j + 1) + ": ");
                            options[j] = sc.nextLine();
                        }
                        System.out.print("Correct Option (1-4): ");
                        int correct = inputInt(1, 4);
                        quiz.addQuestion(text, options, correct);
                    }
                    System.out.println("Quiz created successfully.");
                } else break;
            } catch (Exception e) {
                System.out.println("Error in Teacher Panel: " + e.getMessage());
            }
        }
    }

    static void studentPanel(Student s) {
        s.showProfile();
        while (true) {
            try {
                System.out.println("\n1. Attempt Quiz\n2. View Result\n3. Logout");
                int c = inputInt(1, 3);
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
                        throw new Exception("No quiz with this ID.");
                    } else {
                        s.attemptQuiz(selQuiz, sc);
                    }
                } else if (c == 2) {
                    s.viewResults();
                } else break;
            } catch (Exception e) {
                System.out.println("Error in Student Panel: " + e.getMessage());
            }
        }
    }

    static int inputInt(int min, int max) {
        int num = 0;
        while (true) {
            try {
                num = sc.nextInt();
                sc.nextLine();
                if (num >= min && num <= max) return num;
                else throw new InputMismatchException("Number must be between " + min + " and " + max);
            } catch (InputMismatchException e) {
                System.out.println("Invalid input: " + e.getMessage());
                sc.nextLine();
            }
        }
    }
}
