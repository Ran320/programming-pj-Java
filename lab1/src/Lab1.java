import java.util.ArrayList;
import java.util.List;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;

//对学生分数进行排序并输出
class Student implements Comparable<Student> {
    String name;
    int score;

    public Student(String name, int score) {
        this.name = name;
        this.score = score;
    }

    @Override
    public int compareTo(Student other) {
        return other.score - this.score;
    }

    public void print() {
        System.out.println(name + " " + score);
    }
}

public class Lab1 {
    public static void main(String[] args) {
        String filepath = "src/grade.txt";
        List<Student> students = new ArrayList<>();

        // 确保资源自动关闭
        try (BufferedReader reader = new BufferedReader(new FileReader(filepath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(" ");
                String name = parts[0];
                int score = Integer.parseInt(parts[1]);
                Student student = new Student(name, score);
                students.add(student);
            }
        } catch (IOException e) {
            // 捕获异常并打印错误信息
            System.err.println("读取文件时发生错误: " + e.getMessage());
        }

        // 计算平均成绩
        if (!students.isEmpty()) {
            int totalScore = 0;
            for (Student student : students) {
                totalScore += student.score;
            }
            double averageScore = totalScore / (double) students.size();
            System.out.println("平均成绩: " + averageScore);
        }

        // 对学生按成绩排序
        Collections.sort(students);

        // 打印排序后的学生成绩
        System.out.println("成绩排名:");
        for (Student student : students) {
            student.print(); // 打印每个学生的姓名和成绩
        }
    }
}
