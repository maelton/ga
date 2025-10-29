package br.com.maelton.core;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private static final int ALLOCATION_MAX_ATTEMPTS = 100;
    private static final int TOURNAMENT_SIZE = 2;

    public GeneticAlgorithm(int populationSize, int generations, double mutationRate) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
    }

    public Individual generateRandomValidAllocation(List<Classes> classes, List<Classroom> classrooms) {
        return generateRandomValidAllocation(classes, classrooms, ALLOCATION_MAX_ATTEMPTS);
    }

    private Individual generateRandomValidAllocation(List<Classes> classes, List<Classroom> classrooms, int maxAttempts) {
        for (int i = 0; i < maxAttempts; i++) {
            List<Integer> classroomIndexes = IntStream.range(0, classrooms.size()).boxed().collect(Collectors.toList());
            Collections.shuffle(classroomIndexes);

            /**
             * Each position of this array represents a class, each value is the index of a
             * classroom.
             */
            int[] allocation = new int[classes.size()];
            boolean allocationFailed = false;
            for (int j = 0; j < classes.size(); j++) {
                final int classIndex = j;
                List<Integer> feasible = classroomIndexes.stream()
                        .filter(classroomIndex -> classrooms.get(classroomIndex).getCapacity() >= classes
                                .get(classIndex).getSize())
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

    /**
     * Fitness is based on the comfort a classroom provides for its allocated class.
     * 
     * The larger classrooms are for its relative classes the higher the fitness of
     * an individual.
     */
    public double calculateIndividualFitness(Individual individual, List<Classes> classes, List<Classroom> classrooms) {
        int[] allocation = individual.getChromosome();
        double totalComfort = 0;
        for (int i = 0; i < allocation.length; i++) {
            Classes clazz = classes.get(i);
            Classroom room = classrooms.get(allocation[i]);
            if (room.getCapacity() < clazz.getSize()) {
                return 0.0;
            }
            totalComfort += (room.getCapacity() - clazz.getSize());
        }
        return totalComfort / allocation.length;
    }

    public Individual tournamentSelect(List<Individual> population) {
        return tournamentSelect(population, TOURNAMENT_SIZE);
    }

    /**
     * Tournament selection implementation that selects the most fit individual from
     * a random subset of a population.
     */
    private Individual tournamentSelect(List<Individual> population, int tournamentSize) {
        List<Individual> shuffled = new ArrayList<>(population);
        Collections.shuffle(shuffled);
        List<Individual> tournamentIndividuals = shuffled.subList(0, tournamentSize);
        return Collections.max(tournamentIndividuals, Comparator.comparingDouble(Individual::getFitness));
    }
}
