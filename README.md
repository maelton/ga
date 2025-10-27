# Classroom Allocation with Genetic Algorithm

This project demonstrates the use of a **genetic algorithm (GA)** to solve a classroom allocation problem.

## Problem Description

We have:

* A set of **classes**, each with a specific number of students.
* A set of **classrooms**, each with a specific capacity.

The goal is to **allocate each class to a classroom** while considering the following constraints and priorities:

1. Every class must be assigned to exactly one classroom.
2. No classroom should host more than one class.
3. The number of classrooms is **greater than or equal to** the number of classes.
4. The allocation should **maximize student comfort** by assigning each class to the **largest suitable classroom** available (i.e., the smallest room that still fits the class is acceptable, but larger rooms are preferred if available).

## Approach

This project uses a **genetic algorithm** to evolve a population of possible allocations toward an optimal or near-optimal solution.

Each **chromosome** represents a complete allocation of classes to classrooms. The **fitness function** evaluates how comfortable the allocation is, rewarding:

* Feasible assignments (no overcapacity or missing classrooms).
* Allocations that maximize unused space (comfort).

## Steps of the Algorithm

1. **Initialization** – Generate a random population of valid allocations.
2. **Evaluation** – Compute the fitness of each allocation.
3. **Selection** – Choose the best-performing allocations to reproduce.
4. **Crossover** – Combine pairs of allocations to produce new ones.
5. **Mutation** – Randomly alter some allocations to maintain diversity.
6. **Iteration** – Repeat until the algorithm converges or reaches the iteration limit.

## Example

Suppose we have:

* 4 classes with 20, 25, 35, and 40 students.
* 5 classrooms with capacities of 25, 30, 35, 45, and 50 seats.

The algorithm will attempt to allocate the classes so that:

* Each class fits in a classroom large enough for its students.
* Larger classrooms are used efficiently.
* Every class has a classroom.

## Future Improvements

* Include support for time slots and schedules.
* Add multi-objective optimization (e.g., minimizing distance between rooms).
* Visualize allocation progress over generations.

---

**Keywords:** Genetic Algorithm, Optimization, Resource Allocation, Scheduling, Evolutionary Computation.
