package assignment;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Queue;
import javax.swing.KeyStroke;

public class GalacticBrain extends JBrainTetris{
	private static final long serialVersionUID = 1L;
	ArrayList<Individual> population = new ArrayList<Individual>();
	int generation = -1;
	double topFitness = 0;
	double goalFitness = 100000;
	
	public static void main(String[] args) {
		createGUI(new GalacticBrain());
	}
	
	public GalacticBrain() {
		super();
		registerKeyboardAction(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
            	for(int i = 0; i < 100; i++) {
            		double a = Math.random() * -1;
            		double b = Math.random();
            		double c = Math.random() * -1;
            		double d = Math.random() * -1;
            		Individual individual = new Individual(4);
            		individual.components[0] = a;
            		individual.components[1] = b;
            		individual.components[2] = c;
            		individual.components[3] = d;
            		individual.unitize();
            		population.add(individual);
            	}
            	while(topFitness < goalFitness) {
            		for(int i = 0; i < 100; i++) {
            			Individual individual = population.get(i);
            			double a = individual.components[0];
            			double b = individual.components[1];
            			double c = individual.components[2];
            			double d = individual.components[3];
            			for(int j = 0; j < 5; j++) {
            				while(gameOn) {
            					Queue<Board.Action> nextMove = nextMove(board,a,b,c,d);
                        		while(!nextMove.isEmpty()) {
                        			tick(nextMove.poll());
                        		}
            				}
            				population.get(i).fitness += count;
                    		stopGame();
                    		startGame();
            			}
            			population.get(i).fitness/= 5.0;
            			if(population.get(i).fitness > topFitness) {
            				topFitness = population.get(i).fitness;
            			}
            		}
            		Collections.sort(population);
            		if(generation == -1) {
            			System.out.println("Original Population");
            			for(int i = 0; i < 100; i++) {
            				Individual individual = population.get(i);
            				System.out.println("Individual " + (i+1) + ":\nFitness: " + individual.fitness + "\nAggregate Height: " +individual.components[0] + "\nRows Cleared: "
            				+ individual.components[1] +"\nHoles: " + individual.components[2] + "\nBumpiness: " + individual.components[3]);
                			System.out.println();
            			}
            			generation++;
            			nextPopulation();
            		}
            		else {
            			System.out.println("Generation " + generation);
            			for(int i = 0; i < 100; i++) {
            				Individual individual = population.get(i);
            				System.out.println("Individual " + (i+1) + ":\nFitness: " + individual.fitness + "\nAggregate Height: " +individual.components[0] + "\nRows Cleared: "
                    				+ individual.components[1] +"\nHoles: " + individual.components[2] + "\nBumpiness: " + individual.components[3]);
                			System.out.println();
            			}
            			generation++;
            			nextPopulation();
            		}
            	}
            }
        },
        "geneticAlgorithm", KeyStroke.getKeyStroke('g'), WHEN_IN_FOCUSED_WINDOW);
	}
	
	public void nextPopulation() {
		ArrayList<Individual> newPopulation = new ArrayList<Individual>();
		for(int i = 0; i < 30; i++) {
			//tournament selection
			Collections.shuffle(population);
			ArrayList<Individual> tournamentSelection = new ArrayList<Individual>();
			for(int j = 0; j < 10; j++) {
				tournamentSelection.add(population.get(j));
			}
			Collections.sort(tournamentSelection);
			Individual parentOne = tournamentSelection.get(0);
			Individual parentTwo = tournamentSelection.get(1);
			//weighted crossover
			Individual child = new Individual(4);
			for(int k = 0; k < 4; k++) {
				child.components[k] = parentOne.components[k] * parentOne.fitness + parentTwo.components[k] * parentTwo.fitness;
			}
			child.unitize();
			newPopulation.add(child);
		}
		//mutate
		for(int i = 0; i < 30; i++) {
			int j = (int)(Math.random() * 4);
			double mutation = Math.random() * 0.4 - 0.2;
			if(Math.random()>=.95) {
				newPopulation.get(i).components[j] += mutation;
				newPopulation.get(i).unitize();
			}
		}
		//replace
		Collections.sort(population);
		for(int i = 70; i < 100; i++) {
			population.set(i, newPopulation.get(i-70));
		}
	}
}