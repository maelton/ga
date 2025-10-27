package br.com.maelton.core;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import br.com.maelton.domain.Classes;
import br.com.maelton.domain.Classroom;

public class GeneticAlgorithm {
    private int populationSize;
    private int generations;
    private double mutationRate;

    public GeneticAlgorithm(int populationSize, int generations, double mutationRate) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
    }

    public Individual generateRandomValidAllocation(List<Classes> classes, List<Classroom> classrooms) {
        /** Inclusive */
        final int INITIAL_ATTEMPT = 0;
        /** Exclusive */
        final int MAX_ATTEMPTS = 100;
        return generateRandomValidAllocation(classes, classrooms, INITIAL_ATTEMPT, MAX_ATTEMPTS);
    }

    private Individual generateRandomValidAllocation(List<Classes> classes, List<Classroom> classrooms, int attempt, int maxAttempts) {
        for (int i = attempt; i < maxAttempts; i++) {            
            List<Integer> classroomIndexes = IntStream.range(0, classrooms.size()).boxed().collect(Collectors.toList());
            Collections.shuffle(classroomIndexes);
        
            /**
             * Each position of this array represents a class, each value is the index of a classroom.
             */
            int[] allocation = new int[classes.size()];
            boolean allocationFailed = false;
            for (int j = 0; j < classes.size(); j++) {
                final int classIndex = j;
                List<Integer> feasible = classroomIndexes.stream()
                    .filter(classroomIndex -> classrooms.get(classroomIndex).getCapacity() >= classes.get(classIndex).getSize())
                    .collect(Collectors.toList());
                
                if (feasible.isEmpty()) {
                    allocationFailed = true;
                    break;
                }

                int classroomIndex = feasible.get(ThreadLocalRandom.current().nextInt(feasible.size()));
                allocation[classIndex] = classroomIndex;
                classroomIndexes.remove(Integer.valueOf(classroomIndex));
            }

            if (allocationFailed)
                continue;
            return new Individual(allocation);
        }

        throw new RuntimeException("Number of attempts of generating a valid allocation exceeded");
    }

}
