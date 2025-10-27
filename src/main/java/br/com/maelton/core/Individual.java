package br.com.maelton.core;

public class Individual {
    /**
     * Each position (index) in this array represents a class.
     * 
     * The value stored at each position corresponds to the classroom
     * assigned to that class.
     * 
     * For example:
     * chromosome[classIndex] = classroomAssignedToThatClass
     */
    private int[] chromosome;
    private double fitness;

    public int[] getChromosome() {
        return chromosome;
    }

    public void setChromosome(int[] chromosome) {
        this.chromosome = chromosome;
    }

    public double getFitness() {
        return fitness;
    }

    public void setFitness(double fitness) {
        this.fitness = fitness;
    }

    public Individual(int[] chromosome) {
        this.chromosome = chromosome;
    }

    public Individual() {}
}
