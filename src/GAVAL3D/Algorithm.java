/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GAVAL3D;


import java.io.IOException;
import static java.lang.Math.pow;
import org.jfree.chart.ChartUtilities;
import org.jfree.data.xy.XYDataset;
import org.jfree.data.xy.XYSeries;
import org.jfree.ui.RefineryUtilities;

/**
 *
 * @author user
 */
public class Algorithm {

    private static double mutationProbability = 0.08;
    private static double crossoverProbability = 0.5;
    private static int tournamentSize = 5;

    public static Individual crossover2(Individual i1, Individual i2,double crossoverProbability) {
        Individual offspring = new Individual(i1.genes.length);
        //uniform crossover
        for (int i = 0; i < i1.genes.length; i++) {
            if (Math.random() <= crossoverProbability) {
                offspring.setGene(i, i1.getGene(i));
            } else {
                offspring.setGene(i, i2.getGene(i));
            }
        }
        return offspring;
    }

    public static Individual crossover(Individual m, Individual f) {
        Individual c1 = new Individual(m.genes.length);
        //   int crossover_point=(int)(Math.random()*m.gene_length);
        int crossover_point = (int) 0.5 * m.genes.length;
        System.arraycopy(m.genes, 0, c1.genes, 0, crossover_point);
        System.arraycopy(f.genes, crossover_point, c1.genes, crossover_point, m.genes.length - crossover_point);
        return c1;
    }

    public static void mutate(Individual i1,double mutationProbability) {
        //mutation logic here
        for (int i = 0; i < i1.size(); i++) {
            if (Math.random() <= mutationProbability) {
                int mutation = (int) Math.round(Math.random() * i1.getGene(i));
                i1.setGene(i, mutation);
            }
        }

    }
    public static Population Evolve(Population p1,double mutationProb,double crossoverprob) {
        Population Descendants = new Population(p1.getSize(), false, p1.individuals[1].genes.length);
        //elite selection
        Descendants.saveIndividual(0, p1.getFittestStructure());
        //keep the best individual
        for (int i = 1; i < p1.getSize(); i++) {
            //select fit individual
            Individual parent1 = tournamentSelection(p1);
            Individual parent2 = tournamentSelection(p1);
            //crossover
            Individual offspring = crossover2(parent1, parent2,crossoverprob);
            Descendants.saveIndividual(i, offspring);
        }
        //mutate each individual
        for (int i = 1; i < Descendants.getSize(); i++) {
            mutate(Descendants.getIndividual(i),mutationProb);
        }
        return Descendants;
    }

//    public static Population Evolve(Population p1) {
//        Population Descendants = new Population(p1.getSize(), false, p1.individuals[1].genes.length);
//        //elite selection
//        Descendants.saveIndividual(0, p1.getFittestStructure());
//        //keep the best individual
//        for (int i = 1; i < p1.getSize(); i++) {
//            //select fit individual
//            Individual parent1 = tournamentSelection(p1);
//            Individual parent2 = tournamentSelection(p1);
//            //crossover
//            Individual offspring = crossover2(parent1, parent2);
//            Descendants.saveIndividual(i, offspring);
//        }
//        //mutate each individual
//        for (int i = 1; i < Descendants.getSize(); i++) {
//            mutate(Descendants.getIndividual(i));
//        }
//        return Descendants;
//    }

    public static Individual tournamentSelection(Population p1) {
        Individual fit_ind;
        //initialize population size, but do not initialize it
        //since the tournament population is composed of random 
        //individuals(with already initialized genes) and not new
        //guys.
        Population tournament = new Population(tournamentSize, false, p1.individuals[1].genes.length);
        for (int i = 0; i < tournamentSize; i++) {
            int randomId = (int) (Math.random() * p1.getSize());
            tournament.saveIndividual(i, p1.getIndividual(randomId));
        }
        //from the tournament population select the fittest
        fit_ind = tournament.getFittestStructure();
        return fit_ind;
    }

    public static void main(String[] args) throws IOException {
     }

}
