/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package GAVAL3D;


import SAM3D.Structure3;
import static SAM3D.Structure3.decode3;
import static SAM3D.Structure3.decryptSectionInfo;
import static SAM3D.Structure3.structurePenalty;

/**
 *
 * @author user
 */
public class Individual {

    int defaultGeneLength;
    int noDesignVariables = 4;
    int[] genes;
    byte[] solution = {1, 4, 7, 13, 2, 5, 1, 8, 7, 12, 6, 4, 2, 9, 1, 7, 4, 13, 2, 7, 5, 9, 1, 2, 6, 4, 11, 7, 1, 3};
    private float fitnessFactor;
    private float fitness;
    private float interFit;
    public float weight;
    public float penalty;

    public Individual(int geneLength) {
        defaultGeneLength = geneLength;
        genes = new int[defaultGeneLength];

    }

    //setgene
    public void setGene(int index, int value) {
        genes[index] = value;
    }

    //getgene
    public int getGene(int index) {
        return genes[index];
    }

    public float getFitnessFactor() {
        return fitnessFactor;
    }

    public void setFitnessFactor(float fitnessFactor) {
        this.fitnessFactor = fitnessFactor;
    }

    //size
    public int size() {
        return genes.length;
    }

    public float getInterFit() {
        return interFit;
    }

    public void setInterFit(float interFit) {
        this.interFit = interFit;
    }

    public void generateBuilding3Genes(int NoOfMembers, int beamNo, int columnNo) {
        int cI = -1;
        //                              beam information                  column information
        // chromosome legend            [b|h|Asmain|Aextray|Aextraz|Sleft|Sright]             [b|h|As1|As2|Asv]
        int geneLength = beamNo * 11 + columnNo * 6;
        genes = new int[geneLength];
        for (int i = 0; i < beamNo; i++) {
            //1 breadth
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 16);
            //2 height
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 16);
            //3 basic
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 5);
            //4 extraYT left
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            //5 extraYT right
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            //6 extraYB
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            //7 extraZL
            cI++;
            genes[cI] = 20;
            //8 extraZR
            cI++;
            genes[cI] = 20;
            //9 shearSp left
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 22);
            //10 shearSp mid
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 22);
            //11 shearSp right
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 22);
        }
        for (int j = 0; j < columnNo; j++) {

            //1 breadth
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 16);
            //2 width
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 16);
            //3 basic
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 5);
            //4 extraX left
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            //5 extraX right
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            //6 shear Spacing
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 22);
        }
    }

    public void generateStructure3Genes(int NoOfMembers, boolean uniax) {
        int cI = -1;
        //                              beam information                  column information
        // chromosome legend            [b|h|Asmain|Aextray|Aextraz|Sleft|Sright]             [b|h|As1|As2|Asv]
        int geneLength = NoOfMembers * 9;
        genes = new int[geneLength];
        for (int i = 0; i < NoOfMembers; i++) {
            // breadth*height
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 64);
             // main
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 5);
            // extraYTL
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            // extraYTR
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            // extraYB
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
            // extraZL
            cI++;
            if (uniax) {
                genes[cI] = 20;
            } else {
                genes[cI] = (int) Math.round(Math.random() * 20);
            }// extraZR
            cI++;
            if (uniax) {
                genes[cI] = 20;
            } else {
                genes[cI] = (int) Math.round(Math.random() * 20);
            }
            // shearSp left
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);
             // shearSp right
            cI++;
            genes[cI] = (int) Math.round(Math.random() * 20);

        }
    }

    //setGeneLength
    public void setGeneLength(int length) {
        genes = new int[length];
    }

    //convert gene sequence to string
    @Override
    public String toString() {
        String geneSequence = new String();
        for (int i = 0; i < genes.length; i++) {
            geneSequence += " " + genes[i];
        }
        return geneSequence;
    }



    public float getFitness() {
        return fitness;
    }

    public void setFitness(float fitness) {
        this.fitness = fitness;
    }

    public void Structure3Fitness(Structure3 s) {
        long st, en;
        decode3(s, this);
        decryptSectionInfo(s);
        s.memberPara();
        s.ResetResults();
        s.DetermineDof();

        long stT = System.nanoTime();
        st = System.nanoTime();
        s.CountRestrainedDOFs();
        en = System.nanoTime();
        //  System.out.println("elapsed countDOF:" + (en - st) / 1000000000.0);

        st = System.nanoTime();
        if (s.sparsityTest) {
            s.UpdateAssembleStiffnessC();
        } else {
            s.AssembleStiffnessC();
        }
        en = System.nanoTime();
        // System.out.println("elapsed assembleStiff:" + (en - st) / 1000000000.0);

        st = System.nanoTime();
        s.EquivalentLoadVectorC();
        en = System.nanoTime();
        //  System.out.println("elapsed equLoad:" + (en - st) / 1000000000.0);

        st = System.nanoTime();

        if (s.sparsityTest) {
            s.UpdateApplyBoundaryConditionsC();
        } else {
            s.ApplyBoundaryConditionsC();
        }
        //       System.out.println("sparse: "+s.sparsityTest);
        en = System.nanoTime();
        // System.out.println("elapsed applybound:" + (en - st) / 1000000000.0);

        st = System.nanoTime();

        if (s.sparsityTest) {
            s.UpdateReactionStiffnessC();
        } else {
            s.AssembleReactionStiffnessC();
        }

        en = System.nanoTime();
        //    System.out.println("elapsed reactionStiff:" + (en - st) / 1000000000.0);

        st = System.nanoTime();
        s.ComputeDisplacementsC(s.sparsityTest);
        en = System.nanoTime();
        //      System.out.println("elapsed compDisp:" + (en - st) / 1000000000.0);

//        s.ComputeReactionsOJ();
        st = System.nanoTime();
        s.AssembleDisplacementsC();
        en = System.nanoTime();
        //       System.out.println("elapsed assembleDisp:" + (en - st) / 1000000000.0);

        st = System.nanoTime();
        s.MemberEndActionsC();
        en = System.nanoTime();
        //      System.out.println("elapsed mem end act:" + (en - st) / 1000000000.0);

        long enT = System.nanoTime();
        System.out.println("elapsed total:" + (enT - stT) / 1000000000.0);

        structurePenalty(s, false);
        float penalty = (float) s.strPenalty;
        float w = s.ComputeWeight();
        float penalizedWeightFunction = w * (1 + penalty);
//        System.out.println("[weight: " + w + "] penalty: " + penalty + "] penalized Weight: " + penalizedWeightFunction);
        fitness = penalizedWeightFunction;
        this.setFitness(fitness);
        this.weight = w;
        this.penalty = penalty;
    }

    public void printV(double[] mat) {
        for (int i = 0; i < mat.length; i++) {
            System.out.printf("%12.2f", mat[i]);
        }
    }

    public void DecodeAndDisplay(Structure3 s) {
        decode3(s, this);
        decryptSectionInfo(s);
        s.memberPara();
        s.ResetResults();
        s.DetermineDof();
        System.out.println("-----------------------------------------------------------");
        System.out.println("STRUCTURAL DOF: " + s.StrDof);
        System.out.println("-----------------------------------------------------------");
        s.CountRestrainedDOFs();
        System.out.println("TOTAL NUMBER OF RESTRAINED DOF: " + s.ConstrDof);
        System.out.println("-----------------------------------------------------------");
        s.AssembleStiffnessC();
        System.out.println("STRUCTURE STIFFNESS MATRIX: ");
        System.out.println("-----------------------------------------------------------");
        s.globalStiffnessC.print();
        System.out.println("");
        s.EquivalentLoadVectorC();
        System.out.println("-----------------------------------------------------------");
        System.out.println("EQUIVALENT LOAD VECTOR: ");
        System.out.println("-----------------------------------------------------------");
        printV(s.loadVectorC);
        s.ApplyBoundaryConditionsC();
        System.out.println("REDUCED STRUCTURAL STIFFNESS MATRIX: ");
        System.out.println("-----------------------------------------------------------");
        s.reducedStiffnessC.print();
        System.out.println("-----------------------------------------------------------");
        System.out.println("REDUCED LOAD VECTOR: ");
        System.out.println("-----------------------------------------------------------");
        printV(s.reducedLoadVectorC);
        s.AssembleReactionStiffnessC();
        System.out.println("-----------------------------------------------------------");
        System.out.println("REACTION STIFFNESS MATRIX: ");
        System.out.println("-----------------------------------------------------------");
        s.reactionStiffnessC.print();
        System.out.println("-----------------------------------------------------------");
        System.out.println("REACTION LOAD VECTOR: ");
        System.out.println("-----------------------------------------------------------");
        printV(s.reactionLoadVectorC);
        s.ComputeDisplacementsC(false);
        System.out.println("-----------------------------------------------------------");
        System.out.println("UNKNOWN DISPLACEMENTS: ");
        System.out.println("-----------------------------------------------------------");
        System.out.println(s.displacementVectorC.toString());
        s.ComputeReactionsC();
        System.out.println("-----------------------------------------------------------");
        System.out.println("UNKNOWN REACTIONS: ");
        System.out.println("-----------------------------------------------------------");
        printV(s.reactionVectorC);
        s.AssembleDisplacementsC();
        s.MemberEndActionsC();
        structurePenalty(s, true);

        float penalty = (float) s.strPenalty;
        float w = s.ComputeWeight();
        float penalizedWeightFunction = w * (1 + penalty);
//        System.out.println("[weight: " + w + "] penalty: " + penalty + "] penalized Weight: " + penalizedWeightFunction);
        fitness = penalizedWeightFunction;
        this.setFitness(fitness);
        this.weight = w;
        this.penalty = penalty;

    }

    public static void main(String[] args) {
        Individual test1 = new Individual(24);
        //test1.generateGenes();
//        test1.generateStructureGenes(3, 3);
//        System.out.println(test1.fitness(true));
        System.out.println(test1.toString());

    }
}
