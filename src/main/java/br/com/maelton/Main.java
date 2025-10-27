package br.com.maelton;

import java.util.Arrays;
import java.util.List;

import br.com.maelton.domain.Classroom;
import br.com.maelton.domain.Classes;

public class Main {
    public static void main(String[] args) {
        List<Classroom> classrooms = Arrays.asList(
            new Classroom(1, 20),
            new Classroom(2, 25),
            new Classroom(3, 35),
            new Classroom(4, 40)
        );
        List<Classes> groups = Arrays.asList(
            new Classes(1, 25),
            new Classes(2, 30),
            new Classes(3, 35),
            new Classes(4, 45),
            new Classes(5, 50)
        );
    }
}