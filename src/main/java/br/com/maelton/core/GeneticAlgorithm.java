package br.com.maelton.core;

import java.util.ArrayList;
import java.util.Arrays;
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
     * 
     * The individual is considered invalid and receives a fitness value of negative 
     * infinity if any of its classes is assigned to a classroom that cannot 
     * accommodate all of its students.
     */
    public double calculateIndividualFitness(Individual individual, List<Classes> classes, List<Classroom> classrooms) {
        int[] allocation = individual.getChromosome();
        double totalComfort = 0;
        for (int i = 0; i < allocation.length; i++) {
            Classes clazz = classes.get(i);
            Classroom room = classrooms.get(allocation[i]);
            if (room.getCapacity() < clazz.getSize()) {
                return Double.NEGATIVE_INFINITY;
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

    /**
     * Partially mapped crossover (PMX) implementation.
     */
    public Individual crossover(Individual parent1, Individual parent2) {
        int length = parent1.getChromosome().length;
        int[] child = new int[length];
        Arrays.fill(child, -1);

        /** Choose inclusive distinct crossover points. */
        int point1 = ThreadLocalRandom.current().nextInt(length);
        int point2 = ThreadLocalRandom.current().nextInt(length);
        do {
            point2 = ThreadLocalRandom.current().nextInt(length);
        } while (point1 == point2);
        if (point1 > point2) {
            int temp = point1;
            point1 = point2;
            point2 = temp;
        }

        /** Copies genes from interval [point1, point2] in parent1 to child. */
        for (int i = point1; i <= point2; i++) {
            child[i] = parent1.getChromosome()[i];
        }

        /** Fill the remaining positions of the child using genes from parent2. */
        for (int i = 0; i < length; i++) {
            if (i >= point1 && i <= point2) continue;

            int gene = parent2.getChromosome()[i];
            while (contains(child, gene)) {
                gene = parent2.getChromosome()[indexOf(parent1.getChromosome(), gene)];
            }
            child[i] = gene;
        }

        return new Individual(child);
    }

    /**
     * Returns true or false whether @param arr contains @param value or not.
     */
    private boolean contains(int[] arr, int value) {
        for (int v : arr) if (v == value) return true;
        return false;
    }

    /**
     * Returns the index of @param value in @param arr .
     */
    private int indexOf(int[] arr, int value) {
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == value) return i;
        }
        return -1;
    }

    public Individual mutate(Individual individual) {
        return mutate(individual, this.mutationRate);
    }

    /**
     * Swaps random genes of @param individual at a given mutation rate ( @param mutationRate ).
     * 
     * Be aware that the mutation process may produce invalid individuals - a classroom might
     * be assigned to a class with more students than the classroom can accommodate.
     */
    private Individual mutate(Individual individual, double mutationRate) {
        int[] chromosome = individual.getChromosome().clone();
        if (ThreadLocalRandom.current().nextDouble() < mutationRate) {
            int i = ThreadLocalRandom.current().nextInt(chromosome.length);
            int j = ThreadLocalRandom.current().nextInt(chromosome.length);
            int temp = chromosome[i];
            chromosome[i] = chromosome[j];
            chromosome[j] = temp;
        }
        return new Individual(chromosome);
    }
}
