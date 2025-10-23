package br.com.maelton.core;

public class GeneticAlgorithm {
    private int populationSize;
    private int generations;
    private double mutationRate;

    public GeneticAlgorithm(int populationSize, int generations, int mutationRate) {
        this.populationSize = populationSize;
        this.generations = generations;
        this.mutationRate = mutationRate;
    }
}
