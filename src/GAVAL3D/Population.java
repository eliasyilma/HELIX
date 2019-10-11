/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GAVAL3D;


import SAM3D.Structure3;
import UI.GAResults;

/**
 *
 * @author user
 */
public class Population {

    //individuals
    Individual[] individuals;
    float averageFitness;

    //initialize population
    public Population(int populationSize, boolean initialize, int geneLength,int NumMembers, boolean uniax) {
        //initialization of population
        individuals = new Individual[populationSize];
        if (initialize) {
            for (int i = 0; i < populationSize; i++) {
                Individual newIndividual = new Individual(geneLength);
                newIndividual.generateStructure3Genes(NumMembers,uniax);
                saveIndividual(i, newIndividual);
            }
        }
    }

    public Population(int populationSize, boolean initialize, int geneLength) {
        //initialization of population
        individuals = new Individual[populationSize];
        for (int i = 0; i < populationSize; i++) {
            Individual newIndividual = new Individual(geneLength);
            saveIndividual(i, newIndividual);
        }
    }

    //get fittest individual from population


    //get individual located at index
    public Individual getIndividual(int index) {
        return individuals[index];
    }

    public Individual[] getIndividuals() {
        return individuals;
    }

    //get the number of individuals within a population
    public int getSize() {
        return individuals.length;
    }

    //save individual at the specified index within the population
    public void saveIndividual(int index, Individual indiv) {
        individuals[index] = indiv;
    }

    //STRUCTURE RELATED STUFF BEGINS HERE
    //------------------------------------------------------------------------------------------
    public Individual getFittestStructure() {
        Individual fittest = individuals[0];
        for (int i = 0; i < individuals.length; i++) {
            if (fittest.getFitness() >= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }
    
    public Individual getMostFitStructure() {
        Individual fittest = individuals[0];
        for (int i = 0; i < individuals.length; i++) {
            if (fittest.getFitness() >= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    public Individual getLeastFitStructure() {
        Individual fittest = individuals[0];
        for (int i = 0; i < individuals.length; i++) {
            if (fittest.getFitness() <= getIndividual(i).getFitness()) {
                fittest = getIndividual(i);
            }
        }
        return fittest;
    }

    public void computeFitnessforAll(Structure3 s,GAResults res) {
        for (int i = 0; i < individuals.length; i++) {
            getIndividual(i).Structure3Fitness(s);
            res.updatecurr(i, getIndividual(i).weight, getIndividual(i).getFitness(),getIndividual(i).penalty);
            s.sparsityTest=true;
        }
    }

    public void computeInterFitforAll() {
        float mostFit = this.getFittestStructure().getFitness();
        float leastFit = this.getLeastFitStructure().getFitness();
        for (int i = 0; i < individuals.length; i++) {
            Individual ind = getIndividual(i);
            ind.setInterFit((mostFit - leastFit) - ind.getFitness());
        }

    }

    public void computeAverageInterfit() {
        float averageFitness = 0;
        for (int l = 0; l < individuals.length; l++) {
            Individual a = getIndividual(l);
            averageFitness += a.getInterFit();
        }
        averageFitness = averageFitness / (individuals.length);
        this.averageFitness = averageFitness;
    }

    public void computeFitnessFactorforAll() {
        float averageFit = this.averageFitness;
        for (int l = 0; l < individuals.length; l++) {
            Individual a = getIndividual(l);
            a.setFitnessFactor(a.getInterFit()/averageFit);
        }
    }

//------------------------------------------------------------------------------------------
    public static void main(String[] args) {

    }
}
