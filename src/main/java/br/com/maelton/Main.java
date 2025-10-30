package br.com.maelton;

import java.util.Arrays;
import java.util.List;

import br.com.maelton.domain.Classroom;
import br.com.maelton.core.GeneticAlgorithm;
import br.com.maelton.domain.Classes;

public class Main {
    public static void main(String[] args) {
        List<Classroom> classrooms = Arrays.asList(
            new Classroom(1, 20),
            new Classroom(2, 25),
            new Classroom(3, 30),
            new Classroom(4, 35),
            new Classroom(5, 40)
        );
        List<Classes> groups = Arrays.asList(
            new Classes(1, 20),
            new Classes(2, 30),
            new Classes(3, 35)
        );

        GeneticAlgorithm ga = new GeneticAlgorithm(50, 200, 0.05, groups, classrooms);
        ga.run();
    }
}