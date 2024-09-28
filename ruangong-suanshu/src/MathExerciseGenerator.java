import java.io.*;
import java.util.*;
import javax.script.*;

public class MathExerciseGenerator {
    private static final Random random = new Random();
    private static final ScriptEngine engine = new ScriptEngineManager().getEngineByName("JavaScript");

    public static void main(String[] args) {
        if (args.length == 0) {
            System.out.println("Usage: Myapp.exe -n <number> -r <range> or -e <exercisefile>.txt -a <answerfile>.txt");
            return;
        }

        if (args[0].equals("-n")) {
            int num = Integer.parseInt(args[1]);
            int range = Integer.parseInt(args[3]);
            generateExercises(num, range);
        } else if (args[0].equals("-e")) {
            String exerciseFile = args[1];
            String answerFile = args[3];
            checkAnswers(exerciseFile, answerFile);
        } else {
            System.out.println("Invalid parameters.");
        }
    }

    private static void generateExercises(int num, int maxNum) {
        Set<String> exercises = new LinkedHashSet<>();
        while (exercises.size() < num) {
            String expr = generateExpression(3, maxNum);
            exercises.add(expr);
        }
        writeToFile("Exercises.txt", exercises);

        List<String> answers = new ArrayList<>();
        for (String exercise : exercises) {
            answers.add(formatFraction(evaluateExpression(exercise)));
        }
        writeToFile("Answers.txt", answers);
    }

    private static String generateExpression(int depth, int maxNum) {
        if (depth == 0) {
            return String.valueOf(random.nextInt(maxNum) + 1); // 避免生成0
        }

        String left = generateExpression(depth - 1, maxNum);
        String right = generateExpression(depth - 1, maxNum);
        String operator = getRandomOperator();

        return String.format("(%s %s %s)", left, operator, right);
    }

    private static String getRandomOperator() {
        String[] operators = {"+", "-", "*", "/"};
        return operators[random.nextInt(operators.length)];
    }

   //计算
    private static String evaluateExpression(String expr) {
        try {
            Object result = engine.eval(expr);
            return String.valueOf(result);
        } catch (ScriptException e) {
            e.printStackTrace();
            return "0";
        }
    }

    //转换为真分数
    private static String formatFraction(String result) {
        double value = Double.parseDouble(result);
        int whole = (int) value;
        double fractional = value - whole;

        if (fractional == 0) {
            return String.valueOf(whole);
        }

        int denominator = 8; // 可以根据需要调整分母
        int numerator = (int) Math.round(fractional * denominator);

        return (whole > 0 ? whole + "'" : "") + numerator + "/" + denominator;
    }

    private static void checkAnswers(String exerciseFile, String answerFile) {
        List<String> exercises = readLinesFromFile(exerciseFile);
        List<String> answers = readLinesFromFile(answerFile);

        if (exercises.size() != answers.size()) {
            System.out.println("Error: The number of exercises and answers do not match.");
            return;
        }

        List<Integer> correct = new ArrayList<>();
        List<Integer> wrong = new ArrayList<>();

        for (int i = 0; i < exercises.size(); i++) {
            String exercise = exercises.get(i);
            String userAnswer = answers.get(i);
            String correctAnswer = formatFraction(evaluateExpression(exercise));

            if (correctAnswer.equals(userAnswer)) {
                correct.add(i + 1);
            } else {
                wrong.add(i + 1);
            }
        }

        writeResults(correct, wrong);
    }

    private static List<String> readLinesFromFile(String filename) {
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return lines;
    }

    private static void writeToFile(String filename, Set<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeToFile(String filename, List<String> content) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : content) {
                writer.write(line);
                writer.newLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void writeResults(List<Integer> correct, List<Integer> wrong) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("Grade.txt"))) {
            writer.write("Correct: " + correct.size() + " (" + correct.toString().replaceAll("[\\[\\],]", "") + ")\n");
            writer.write("Wrong: " + wrong.size() + " (" + wrong.toString().replaceAll("[\\[\\],]", "") + ")\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}