package br.com.maelton.core;

import br.com.maelton.domain.Classroom;

public class Individual {
    private Classroom[] chromosome; // chromosome[groupId-1] = Classroom instance
    private double fitness;

    private Individual(int numberOfClasses) {
        this.chromosome = new Classroom[numberOfClasses];
    }
}
