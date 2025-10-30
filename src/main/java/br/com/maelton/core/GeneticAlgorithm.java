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
    private int numberOfGenerations;
    private double mutationRate;
    private List<Classes> classes;
    private List<Classroom> classrooms;

    private static final int ALLOCATION_MAX_ATTEMPTS = 10_000;
    private static final int TOURNAMENT_SIZE = 2;

    public GeneticAlgorithm(
        int populationSize, 
        int numberOfGenerations, 
        double mutationRate,
        List<Classes> classes, 
        List<Classroom> classrooms
    ) {
        this.populationSize = populationSize;
        this.numberOfGenerations = numberOfGenerations;
        this.mutationRate = mutationRate;
        this.classes = classes;
        this.classrooms = classrooms;
    }

    private Individual generateRandomValidAllocation(List<Classes> classes, List<Classroom> classrooms) {
        return generateIndividual(classes, classrooms, ALLOCATION_MAX_ATTEMPTS);
    }

    private Individual generateIndividual(List<Classes> classes, List<Classroom> classrooms, int maxAttempts) {
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
            
            Individual individual = new Individual(allocation); 
            individual.setFitness(calculateIndividualFitness(individual, classes, classrooms));
            return individual;
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
    private double calculateIndividualFitness(Individual individual, List<Classes> classes, List<Classroom> classrooms) {
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

    private Individual tournamentSelect(List<Individual> population) {
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
        
        return getBestIndividual(tournamentIndividuals);
    }

    /**
     * Partially mapped crossover (PMX) implementation.
     */
    private Individual crossover(Individual parent1, Individual parent2) {
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
        
        Individual individual = new Individual(child); 
        individual.setFitness(calculateIndividualFitness(individual, classes, classrooms));
        return individual;
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

    private Individual mutate(Individual individual) {
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

        Individual ind = new Individual(chromosome); 
        ind.setFitness(calculateIndividualFitness(ind, classes, classrooms));
        return ind;
    }

    private List<Individual> generatePopulation(int populationSize, List<Classes> classes, List<Classroom> classrooms) {
        return IntStream.range(0, populationSize).mapToObj(i -> generateRandomValidAllocation(classes, classrooms)).collect(Collectors.toList());  
    }

    private Individual getBestIndividual(List<Individual> population) {
        return Collections.max(population, Comparator.comparingDouble(Individual::getFitness));
    }

    public void run() {
        List<Individual> population = generatePopulation(this.populationSize, this.classes, this.classrooms);
                
        for (int generation = 0; generation < this.numberOfGenerations; generation++) {
            List<Individual> newPopulation = new ArrayList<>();
            
            Individual bestOfGeneration = getBestIndividual(population);
            newPopulation.add(bestOfGeneration);

            while (newPopulation.size() < population.size()) {
                Individual parent1 = tournamentSelect(population);
                Individual parent2 = tournamentSelect(population);

                Individual child = crossover(parent1, parent2);

                if (ThreadLocalRandom.current().nextDouble() < this.mutationRate)
                    child = mutate(child);
                if (child.getFitness() == Double.NEGATIVE_INFINITY)
                    continue; // It may cause infinity looping
                    // child = generateRandomValidAllocation(this.classes, this.classrooms); Possible solution
                
                newPopulation.add(child);
            }

            population = newPopulation;
            System.out.printf("Generation %d | Best fitness: %.2f%n", generation, bestOfGeneration.getFitness());
        }

        Individual bestIndividual = getBestIndividual(population);
        System.out.print("\n" + bestIndividual);
    }
}
