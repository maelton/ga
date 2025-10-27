package br.com.maelton;

import java.util.Arrays;
import java.util.List;

import br.com.maelton.domain.Classroom;
import br.com.maelton.domain.Group;

public class Main {
    public static void main(String[] args) {
        List<Classroom> classrooms = Arrays.asList(
            new Classroom(1, 20),
            new Classroom(2, 25),
            new Classroom(3, 35),
            new Classroom(4, 40)
        );
        List<Group> groups = Arrays.asList(
            new Group(1, 25),
            new Group(2, 30),
            new Group(3, 35),
            new Group(4, 45),
            new Group(5, 50)
        );
    }
}