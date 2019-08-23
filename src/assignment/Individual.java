package assignment;

public class Individual implements Comparable<Individual> {
	int numComponents;
	double[] components;
	double fitness;
	public Individual(int numComponents) {
		this.numComponents = numComponents;
		components = new double[numComponents];
	}
	public void unitize(){
		double length = 0;
		for(double coordinate: components)
			length += coordinate * coordinate;
		length = Math.sqrt(length);
		if(length == 0)
			return;
		for(int i = 0; i < numComponents; i++) {
			components[i]/=length;
		}
	}
	public int compareTo(Individual other) {
		return Double.compare(other.fitness, fitness);
	}
}