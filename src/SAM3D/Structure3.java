/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package SAM3D;

import static GAVAL3D.Algorithm.Evolve;
import GAVAL3D.Individual;


import java.io.IOException;
import java.text.DecimalFormat;
import java.util.LinkedList;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.CG;
import no.uib.cipr.matrix.sparse.CompRowMatrix;
import no.uib.cipr.matrix.sparse.DiagonalPreconditioner;
import no.uib.cipr.matrix.sparse.FlexCompRowMatrix;
import no.uib.cipr.matrix.sparse.ICC;
import no.uib.cipr.matrix.sparse.ILU;
import no.uib.cipr.matrix.sparse.IterativeSolver;
import no.uib.cipr.matrix.sparse.IterativeSolverNotConvergedException;
import no.uib.cipr.matrix.sparse.Preconditioner;
import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.ArrayRealVector;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealMatrixFormat;
import org.apache.commons.math3.linear.RealVector;
import org.apache.commons.math3.linear.SingularMatrixException;
import org.jfree.ui.RefineryUtilities;
import UI.Section;
import static UI.ShearResist.columnShearPenalty;
import static UI.ShearResist.minShearSpacing;
import static UI.ShearResist.shearPenaltyY;
import static UI.ShearResist.shearPenaltyZ;
import static UI.ShearTorsion.ShearTorsionPenalty;
import UI.SparseArray;
import static UI.TorsionResist.torsionPenalty;
import static UI.VandMPanel.internalActionCoordinates;
import static UI.bendingDesignCheck.biaxialPenalty;
import static UI.sizePenalty.totalSizePenalty;

/**
 *
 * @author user
 */
public class Structure3 {

    public CompRowMatrix globalStiffness;
    public SparseArray globalStiffnessC;
    public CompRowMatrix reducedStiffness;
    public SparseArray reducedStiffnessC;
    public CompRowMatrix reactionStiffness;
    public SparseArray reactionStiffnessC;
    public RealVector reducedLoadVector;
    public double[] reducedLoadVectorC;
    public RealVector reactionLoadVector;
    public double[] reactionLoadVectorC;
    public RealVector loadVector;
    public double[] loadVectorC;
    public RealVector displacementVector;
    public DenseVector displacementVectorC;

    public RealVector trueDisplacementVector;
    public RealVector reactionVector;
    public double[] reactionVectorC;

    public Joint3[] jointVector;
    public Member3[] memberVector;
    public Member3[] beamV, columnV;
    public int StrDof;
    public int ConstrDof;
    public int[] unconstrainedDOF;
    public int[] constrainedDOF;
    protected int numJoints = 0;
    protected int numElements = 0;
    private ArrayRealVector trueReactionVector;
    public double strPenalty;
    public double memberPenalty, biaxialPen, shearYPen, shearZPen, STPen, TorsionPen;
    public static double kb, kv, kt;
    public boolean sparsityTest = false;

    public void DetermineDof() {
        this.StrDof = (this.jointVector.length) * 6;
    }

    public void CountRestrainedDOFs() {
        int constDof = 0;
        for (int i = 0; i < jointVector.length; i++) {
            if (jointVector[i].isRrx()) {
                constDof++;
            }
            if (jointVector[i].isRry()) {
                constDof++;
            }
            if (jointVector[i].isRrz()) {
                constDof++;
            }
            if (jointVector[i].isRrmx()) {
                constDof++;
            }
            if (jointVector[i].isRrmy()) {
                constDof++;
            }
            if (jointVector[i].isRrmz()) {
                constDof++;
            }
        }
        this.ConstrDof = constDof;
    }

    //  #######################################
    public void AssembleStiffnessC() {
        double[][] K = new double[this.StrDof][this.StrDof];
        SparseArray Kc = new SparseArray(new double[this.StrDof][0], new int[this.StrDof][0], this.StrDof, this.StrDof, 0);
        //
        int k, l;
        for (Member3 mem : memberVector) {
            int sdof = mem.start.dof_index - 1;
            int edof = mem.end.dof_index - 1;
//            System.out.println("sdof:   " + sdof + "  edof:   " + edof);
            k = 0;
            for (int j = sdof; j <= sdof + 5; j++) {
                l = 0;
                for (int m = sdof; m <= sdof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        Kc.addValue(j, m, val);
                    }
                    l = l + 1;
                }
                k = k + 1;
            }

            k = 0;
            for (int j = sdof; j <= sdof + 5; j++) {
                l = 6;
                for (int m = edof; m <= edof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        Kc.addValue(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }

            k = 6;
            for (int j = edof; j <= edof + 5; j++) {
                l = 6;
                for (int m = edof; m <= edof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        Kc.addValue(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }

            k = 6;
            for (int j = edof; j <= edof + 5; j++) {
                l = 0;
                for (int m = sdof; m <= sdof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        Kc.addValue(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }
        }

        System.out.println("ss matrix sparsity: " + sparsity(K));
//        this.globalStiffness = new Array2DRowRealMatrix(K);
//       this.globalStiffnessC = Kc.toCSR();
        this.globalStiffnessC = Kc;
    }

    public void UpdateAssembleStiffnessC() {
        double[][] K = new double[this.StrDof][this.StrDof];
//        SparseArray Kc = new SparseArray(new double[this.StrDof][0], new int[this.StrDof][0], this.StrDof, this.StrDof, 0);
        globalStiffness = new CompRowMatrix(globalStiffnessC.rows, globalStiffnessC.columns, globalStiffnessC.AIndex);
        globalStiffness.zero();
//
        int k, l;
        for (Member3 mem : memberVector) {
            int sdof = mem.start.dof_index - 1;
            int edof = mem.end.dof_index - 1;
//            System.out.println("sdof:   " + sdof + "  edof:   " + edof);
            k = 0;
            for (int j = sdof; j <= sdof + 5; j++) {
                l = 0;
                for (int m = sdof; m <= sdof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        globalStiffness.add(j, m, val);
                    }
                    l = l + 1;
                }
                k = k + 1;
            }

            k = 0;
            for (int j = sdof; j <= sdof + 5; j++) {
                l = 6;
                for (int m = edof; m <= edof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        globalStiffness.add(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }

            k = 6;
            for (int j = edof; j <= edof + 5; j++) {
                l = 6;
                for (int m = edof; m <= edof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        globalStiffness.add(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }

            k = 6;
            for (int j = edof; j <= edof + 5; j++) {
                l = 0;
                for (int m = sdof; m <= sdof + 5; m++) {
                    double val = mem.globalStiffnessC.get(k, l);
                    if (Math.abs(val) > 0.0001) {
                        globalStiffness.add(j, m, val);
                    }
                    l = l + 1;

                }
                k = k + 1;
            }
        }
    }

    public void ApplyBoundaryConditionsC() {
        //non-zero stiffness terms
        int nnz = 0;
        //init a square matrix with a rank of unrestrained dofs X unrestrained dofs
        int unconstrainedDOF = this.StrDof - this.ConstrDof;
        int[] unconstrainedDOFIndex = new int[unconstrainedDOF];
        int[] constrainedDOFIndex = new int[this.ConstrDof];
        SparseArray rKc = new SparseArray(new double[unconstrainedDOF][0], new int[unconstrainedDOF][0], unconstrainedDOF, unconstrainedDOF, 0);
        double[] reducedLoadVector = new double[unconstrainedDOF];
        //identify which DOFs are free or restrained and store these inside separate arrays.
        int k = 0;
        int r = 0;
        for (int i = 0; i < jointVector.length; i++) {
            Joint3 currentJoint = jointVector[i];
            int rxdof = currentJoint.dof_index - 1;
            int rydof = rxdof + 1;
            int rzdof = rxdof + 2;
            int rmxdof = rxdof + 3;
            int rmydof = rxdof + 4;
            int rmzdof = rxdof + 5;

            if (!currentJoint.isRrx()) {
                unconstrainedDOFIndex[k] = rxdof;
                k++;
            } else {
                constrainedDOFIndex[r] = rxdof;
                r++;
            }
            if (!currentJoint.isRry()) {
                unconstrainedDOFIndex[k] = rydof;
                k++;

            } else {
                constrainedDOFIndex[r] = rydof;
                r++;
            }
            if (!currentJoint.isRrz()) {
                unconstrainedDOFIndex[k] = rzdof;
                k++;

            } else {
                constrainedDOFIndex[r] = rzdof;
                r++;
            }
            if (!currentJoint.isRrmx()) {
                unconstrainedDOFIndex[k] = rmxdof;
                k++;
            } else {
                constrainedDOFIndex[r] = rmxdof;
                r++;
            }
            if (!currentJoint.isRrmy()) {
                unconstrainedDOFIndex[k] = rmydof;
                k++;
            } else {
                constrainedDOFIndex[r] = rmydof;
                r++;
            }
            if (!currentJoint.isRrmz()) {
                unconstrainedDOFIndex[k] = rmzdof;
                k++;
            } else {
                constrainedDOFIndex[r] = rmzdof;
                r++;
            }
        }
        this.constrainedDOF = constrainedDOFIndex;
        this.unconstrainedDOF = unconstrainedDOFIndex;
        //select the structural stiffness matrix entries for the corresponding DOFs
        //and store them into the reduced stiffness matrix.
        int j = 0, l = 0;
        for (int i = 0; i < unconstrainedDOFIndex.length; i++) {
            l = 0;
            int i1 = unconstrainedDOFIndex[i];
            for (int m = 0; m < unconstrainedDOFIndex.length; m++) {
                int i2 = unconstrainedDOFIndex[m];
                double val = globalStiffnessC.get(i1, i2);
                if (Math.abs(val) > 0.0001) {
                    rKc.setValue(j, l, val);
                    nnz++;
                }
                l++;
            }
            reducedLoadVector[j] = loadVectorC[i1];
            j++;
        }
        rKc.nonzero = nnz;
        reducedStiffnessC = rKc;
        this.reducedLoadVectorC = reducedLoadVector;
    }

    public void UpdateApplyBoundaryConditionsC() {
        int j = 0, l = 0;
        double[] reducedLoadVector = new double[unconstrainedDOF.length];
        reducedStiffness = new CompRowMatrix(reducedStiffnessC.rows, reducedStiffnessC.columns, reducedStiffnessC.AIndex);
        System.out.println("rows: " + reducedStiffnessC.rows + " columns: " + reducedStiffnessC.columns);
        System.out.println("nnz : " + reducedStiffnessC.nonzero);
        for (int i = 0; i < unconstrainedDOF.length; i++) {
            l = 0;
            int i1 = unconstrainedDOF[i];
            for (int m = 0; m < unconstrainedDOF.length; m++) {
                int i2 = unconstrainedDOF[m];
                double val = globalStiffness.get(i1, i2);
                if (Math.abs(val) > 0.000000001) {
                    //                   System.out.println("j: "+j+" l: "+l);
                    reducedStiffnessC.setValue(j, l, val);
                }
                l++;
            }
            reducedLoadVector[j] = loadVectorC[i1];
            j++;
        }
        reducedStiffness = reducedStiffnessC.toCSR();
        this.reducedLoadVectorC = reducedLoadVector;
    }

    //  #######################################
    //  #######################################
    public void EquivalentLoadVectorC() {
        //array size is total DOF of the structure
        double[] eqvLoadVector = new double[this.StrDof];
        //first compute the equivalent end actions of each member
        for (int a = 0; a < memberVector.length; a++) {
            memberVector[a].AML();
        }

        //WHEN THE EQUIVALNET LOADS ARE CALCULATED FOR EACH MEMBER, AND ARE ASSIGNED
        //TO ITS START AND END JOINT'S FORCE VECTORS, THE JOINTS INSIDE OF THE JOINT
        //VECTOR ALSO GET UPDATED: IF NOT, YOU SCREWED UP ROYALLY; SO UNCOMMENT THE PREVIOUS
        //FOR LOOP.
        int q = 0;
        for (int o = 0; o < jointVector.length; o++) {
            Joint3 j = jointVector[o];
            eqvLoadVector[q] = -j.rcx;
            q++;
            eqvLoadVector[q] = -j.rcy;
            q++;
            eqvLoadVector[q] = -j.rcz;
            q++;
            eqvLoadVector[q] = -j.rcmx;
            q++;
            eqvLoadVector[q] = -j.rcmy;
            q++;
            eqvLoadVector[q] = -j.rcmz;
            q++;

        }
        this.loadVectorC = eqvLoadVector;
    }

    //  #######################################
    public void AssembleReactionStiffnessC() {
        SparseArray rxKc = new SparseArray(new double[this.ConstrDof][1], new int[this.ConstrDof][1], this.ConstrDof, this.StrDof - this.ConstrDof, 0);
        double[] reactionLoadVector = new double[this.ConstrDof];
//        printArray(constrainedDOF, 1);
//        printArray(unconstrainedDOF, 1);

        int j = 0, l = 0;
        for (int i = 0; i < constrainedDOF.length; i++) {
            l = 0;
            int i1 = constrainedDOF[i];
            for (int m = 0; m < unconstrainedDOF.length; m++) {
                int i2 = unconstrainedDOF[m];
                double val = globalStiffnessC.get(i1, i2);
                if (Math.abs(val) > 0.0001) {
                    rxKc.setValue(j, l, val);
                }
                l++;
            }
            reactionLoadVector[i] = loadVectorC[i1];
            j++;
        }
//        System.out.println("rxn matrix sparsity: " + sparsity(reactionStiffness));
        this.reactionStiffnessC = rxKc;
        this.reactionLoadVectorC = reactionLoadVector;
    }

    public void UpdateReactionStiffnessC() {
        double[] reactionLoadVector = new double[this.ConstrDof];
        reactionStiffness = new CompRowMatrix(reactionStiffnessC.rows, reactionStiffnessC.columns, reactionStiffnessC.AIndex);
        int j = 0, l = 0;
        for (int i = 0; i < constrainedDOF.length; i++) {
            l = 0;
            int i1 = constrainedDOF[i];
            for (int m = 0; m < unconstrainedDOF.length; m++) {
                int i2 = unconstrainedDOF[m];
                double val = globalStiffness.get(i1, i2);
                if (Math.abs(val) > 0.0001) {
                    reactionStiffness.set(j, l, val);
                }
                l++;
            }
            reactionLoadVector[i] = loadVectorC[i1];
            j++;
        }
//        System.out.println("rxn matrix sparsity: " + sparsity(reactionStiffness));
        this.reactionLoadVectorC = reactionLoadVector;
    }

    public double sparsity(double[][] K) {
        int nnz = 0;
        int rows = K.length;
        int columns = K[0].length;
        for (int i = 0; i < K.length; i++) {
            for (int j = 0; j < K[0].length; j++) {
                if ((Math.abs(K[i][j])) > 0.001) {
                    nnz++;
                }
            }
        }
        return nnz / (1.0 * rows * columns);
    }

    //  #######################################
    public void ComputeDisplacementsC(boolean update) {
        //A
        CompRowMatrix A;
        if (!update) {
            A = this.reducedStiffnessC.toCSR();
        } else {
            A = this.reducedStiffness;
        }
//b=reducedLoadVector
        DenseVector L = new DenseVector(reducedLoadVectorC);
        //x
        DenseVector Disp = new DenseVector(L.size());
        long steli = System.nanoTime();

        IterativeSolver solver = new CG(Disp);
//Create a Diagonal preconditioner
        Preconditioner M = new DiagonalPreconditioner(A.numRows());
//Set up the preconditioner, and attach it
        M.setMatrix(A);
        solver.setPreconditioner(M);
//Add a convergence monitor
        //       solver.getIterationMonitor().setIterationReporter(new OutputIterationReporter());
//Start the solver, and check for problems
        try {
            solver.solve(A, L, Disp);
        } catch (IterativeSolverNotConvergedException e) {
            System.err.println("Iterative solver failed to converge");
        }
        //       System.out.println(x.toString());
//        SparseArray product = rand.mmult(rand2);
        long eneli = System.nanoTime();
        double elapsedeli = (eneli - steli) / 1000000000.0;
        //       System.out.println("prod:" + product.get(0, 0));
        System.out.println("product: " + elapsedeli);
        this.displacementVectorC = Disp;
//        DecompositionSolver solver = new LUDecomposition(this.reducedStiffness).getSolver();
//        RealVector solution = null;
//        try {
//            solution = solver.solve(this.reducedLoadVector);
//        } catch (SingularMatrixException ex) {
//            System.out.println("SINGULAR STIFFNESS MATRIX: malformed connectivity vector or unassigned material property");
//        }
//        this.displacementVector = solution;
    }

    public void ComputeDisplacements() {
//        DecompositionSolver solver = new LUDecomposition(this.reducedStiffness).getSolver();
//        RealVector solution = null;
//        try {
//            solution = solver.solve(this.reducedLoadVector);
//        } catch (SingularMatrixException ex) {
//            System.out.println("SINGULAR STIFFNESS MATRIX: malformed connectivity vector or unassigned material property");
//        }
//        this.displacementVector = solution;
    }

    //  #######################################
    public void AssembleDisplacementsC() {
        double[] trueDisplacementVector = new double[this.StrDof];
        for (int i = 0; i < trueDisplacementVector.length; i++) {
            trueDisplacementVector[i] = 0.0;
        }
        for (int i = 0; i < this.unconstrainedDOF.length; i++) {
            trueDisplacementVector[unconstrainedDOF[i]] = this.displacementVectorC.get(i);
        }
        //debugging facility: REMOVE THIS VARIABLE AND ALL ITS REFERENCES WHEN LINES BELOW IT START WORKING.
        this.trueDisplacementVector = new ArrayRealVector(trueDisplacementVector);
        int j = 0;
        for (int i = 0; i < jointVector.length; i++) {
            jointVector[i].dx = trueDisplacementVector[j];
            j++;
            jointVector[i].dy = trueDisplacementVector[j];
            j++;
            jointVector[i].dz = trueDisplacementVector[j];
            j++;
            jointVector[i].rx = trueDisplacementVector[j];
            j++;
            jointVector[i].ry = trueDisplacementVector[j];
            j++;
            jointVector[i].rz = trueDisplacementVector[j];
            j++;
        }
    }

    public void AssembleReactionsC() {
        double[] trueReactionVector = new double[this.StrDof];
        for (int i = 0; i < trueReactionVector.length; i++) {
            trueReactionVector[i] = 0.0;
        }
        for (int i = 0; i < this.constrainedDOF.length; i++) {
            trueReactionVector[constrainedDOF[i]] = this.reactionVectorC[i];
        }
        //debugging facility: REMOVE THIS VARIABLE AND ALL ITS REFERENCES WHEN LINES BELOW IT START WORKING.
        this.trueReactionVector = new ArrayRealVector(trueReactionVector);
        int j = 0;
        for (int i = 0; i < jointVector.length; i++) {
            jointVector[i].rcx = trueReactionVector[j];
            j++;
            jointVector[i].rcy = trueReactionVector[j];
            j++;
            jointVector[i].rcz = trueReactionVector[j];
            j++;
            jointVector[i].rcmx = trueReactionVector[j];
            j++;
            jointVector[i].rcmy = trueReactionVector[j];
            j++;
            jointVector[i].rcmz = trueReactionVector[j];
            j++;
        }
    }

    //  #######################################
    public double[] vadd(double[] a, double[] b) {
        //add two vectors
        double[] c = new double[a.length];
        for (int i = 0; i < a.length; i++) {
            c[i] = a[i] + b[i];
        }
        return c;
    }

    public void ComputeReactionsC() {
        this.reactionVectorC = vadd(this.reactionLoadVectorC, this.reactionStiffnessC.mmultV(this.displacementVectorC.getData()));
    }

    //  #######################################
    public void MemberEndActionsC() {
        for (int i = 0; i < memberVector.length; i++) {
            Member3 m = memberVector[i];
            //           System.out.println("AML: "+Aml);
            double[] dispvecto = {m.start.dx, m.start.dy, m.start.dz, m.start.rx, m.start.ry, m.start.rz, m.end.dx, m.end.dy, m.end.dz, m.end.rx, m.end.ry, m.end.rz};
            double[] memEndActions = vadd(m.Aml, m.localStiffnessC.mmult(m.rotMatrixC).mmultV(dispvecto));
            //printVector(memEndActions, 3);
            memberVector[i].sFx = memEndActions[0];
            memberVector[i].sFy = memEndActions[1];
            memberVector[i].sFz = memEndActions[2];
            memberVector[i].sMx = memEndActions[3];
            memberVector[i].sMy = memEndActions[4];
            memberVector[i].sMz = memEndActions[5];
            memberVector[i].eFx = memEndActions[6];
            memberVector[i].eFy = memEndActions[7];
            memberVector[i].eFz = memEndActions[8];
            memberVector[i].eMx = memEndActions[9];
            memberVector[i].eMy = memEndActions[10];
            memberVector[i].eMz = memEndActions[11];
        }
    }

    public void ResetResults() {
        for (int i = 0; i < memberVector.length; i++) {
            Member3 m = memberVector[i];
            m.start.dx = 0;
            m.start.dy = 0;
            m.start.dz = 0;
            m.start.rx = 0;
            m.start.ry = 0;
            m.start.rz = 0;
            m.start.rcx = 0;
            m.start.rcy = 0;
            m.start.rcz = 0;
            m.start.rcmx = 0;
            m.start.rcmy = 0;
            m.start.rcmz = 0;
            m.end.dx = 0;
            m.end.dy = 0;
            m.end.dz = 0;
            m.end.rx = 0;
            m.end.ry = 0;
            m.end.rz = 0;
            m.end.rcx = 0;
            m.end.rcy = 0;
            m.end.rcz = 0;
            m.end.rcmx = 0;
            m.end.rcmy = 0;
            m.end.rcmz = 0;
            m.momentYCoords=new LinkedList<>();
                        m.axialCoords=new LinkedList<>();
            m.momentZCoords=new LinkedList<>();
            m.axialCoords=new LinkedList<>();
            m.shearYCoords=new LinkedList<>();
            m.shearZCoords=new LinkedList<>();
            m.torsionCoords=new LinkedList<>();

        }

    }

    public static void decode3(Structure3 s, Individual ind) {
        int cI = 0;
        //                             beam information                  column information
        // chromosome legend   [b|h|rebar|shearSpacing]     [b|h|As1|As2|Asv]
        Member3[] member = s.getMemberVector();
        //decode for beams
        for (int i = 0; i < member.length; i++) {
            Member3 m = member[i];
            int b = decodeB(ind.getGene(cI));
            int h = decodeH(ind.getGene(cI));
            cI++;
            int main = ind.getGene(cI);
            cI++;
            int exXTL = ind.getGene(cI);
            cI++;
            int exXTR = ind.getGene(cI);
            cI++;
            int exXB = ind.getGene(cI);
            cI++;
            int exYL = ind.getGene(cI);
            cI++;
            int exYR = ind.getGene(cI);
            cI++;
            double SSpacingL = decodeShearSp(ind.getGene(cI));
            cI++;
            double SSpacingR = decodeShearSp(ind.getGene(cI));
            cI++;
            Section sectTopL = new Section(main, exXTL, 20, 20, 20, SSpacingL, b, h);
            Section sectTopR = new Section(main, exXTR, 20, 20, 20, SSpacingR, b, h);
            Section sectBot = new Section(main, 20, exXB, 20, 20, 300, b, h);
            double SSpacingM = minShearSpacing(sectBot, m.getFck(), m.getFyk());
            sectBot.setShearS((int) SSpacingM);
            m.setSectionNegLeft(sectTopL);
            m.setSectionNegRight(sectTopR);
            m.setSectionPos(sectBot);

        }
    }

    public float ComputeWeight() {
        float weight = 0;
        for (int i = 0; i < this.memberVector.length; i++) {
            memberVector[i].ComputeWeight();
            weight += memberVector[i].weight;
        }
        return weight;
    }

    public static int decodeB(int index) {
        int[] breadthRef = {225,225,225,225,225,225,225,225,225,225,225,225,225,225,250,250,250,250,250,250,250,250,250,250,250,250,
275,275,275,275,275,275,275,275,275,275,275,300,300,300,300,300,300,300,300,300,300,325,325,325,325,325,325,325,325,325,350,350,
350,350,350,350,350,350,350};
        return breadthRef[index];
    }

    public static int decodeH(int index) {
        int[] heightRef = {
        225,250,275,300,325,350,375,400,425,450,475,500,525,550,275,300,325,350,375,400,425,450,475,500,525,550,300,325,350,375,
400,425,450,475,500,525,550,325,350,375,400,425,450,475,500,525,550,350,375,400,425,450,475,500,525,550,350,375,400,425,450,475,500,525,550};
        return heightRef[index];
    }

    public static double decodeShearSp(int index) {
        double[] Asv = {100, 110, 120, 130, 140, 150, 160, 170, 180, 190, 200, 210, 220, 230, 240, 250, 260, 270, 280, 290, 300};
        return Asv[index];
    }

    public static void decryptSectionInfo(Structure3 s) {
        Member3[] mems = s.getMemberVector();
        for (int i = 0; i < mems.length; i++) {
            Member3 m = mems[i];
            m.setB(m.getXsecNegLeft().getBreadth() / 1000.0);
            m.setH(m.getXsecNegLeft().getHeight() / 1000.0);

        }
    }

    public void memberPara() {
        for (int i = 0; i < this.memberVector.length; i++) {
            Member3 mem = this.memberVector[i];
            mem.computeArea();
            mem.computeIxx();
            mem.computeIyy();
            mem.computeIzz();
            mem.memLength();
            mem.directionCosines();
            mem.rotationMatrix();
            mem.localStiffness();
            mem.globalStiffness();
        }
    }

    public void memberRotPara() {
        for (int i = 0; i < this.memberVector.length; i++) {
            Member3 mem = this.memberVector[i];
            mem.rotationMatrix();
        }
    }

    //unit tests
    public static void unitTest4() {
        long st = System.nanoTime();
        Structure3 Str = new Structure3();
        double A = 0.09;
        double E = 200000000;
        double Iz = 0.000675;
        double Iy = 0.000675;
        double Ix = 0.00114075;
        double G = 80000000;
        double P = 20;
        double W = 100;

        Joint3 j1 = new Joint3(1, -5, -5, 5, 5, 5);
        Joint3 j2 = new Joint3(2, 5, -5, 5, 5, 5);
        Joint3 j3 = new Joint3(3, 5, -5, -5, 5, 5);
        Joint3 j4 = new Joint3(4, -5, -5, -5, 5, 5);
        Joint3 j5 = new Joint3(5, -5, 5, 5, 5, 5);
        Joint3 j6 = new Joint3(6, 5, 5, 5, 5, 5);
        Joint3 j7 = new Joint3(7, 5, 5, -5, 5, 5);
        Joint3 j8 = new Joint3(8, -5, 5, -5, 5, 5);

        Load3 l1 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
        Load3 l2 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
        Load3 l3 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
        Load3 l4 = new Load3(0, 0.5, 0, -W, 0, 0, 0, 0);
//        Load3 l5 = new Load3(0, 1, -P, 0, 0, 0, 0, 0);
//        Load3 l6 = new Load3(0, 1, -P, 0, 0, 0, 0, 0);
//        Load3 l7 = new Load3(0, 1, -P, 0, 0, 0, 0, 0);
//        Load3 l8 = new Load3(0, 1, -P, 0, 0, 0, 0, 0);

        Member3 t1 = new Member3(1, j1, j5);
        Member3 t2 = new Member3(2, j2, j6);
        Member3 t3 = new Member3(3, j3, j7);
        Member3 t4 = new Member3(4, j4, j8);
        Member3 t5 = new Member3(5, j5, j6);
        Member3 t6 = new Member3(6, j6, j7);
        Member3 t7 = new Member3(7, j7, j8);
        Member3 t8 = new Member3(8, j8, j5);

        t5.loading.add(l1);
        t6.loading.add(l2);
        t7.loading.add(l3);
        t8.loading.add(l4);
//        t1.loading.add(l5);
//        t2.loading.add(l6);
//        t3.loading.add(l7);
//        t4.loading.add(l8);
        LinkedList<Joint3> jointV = new LinkedList<>();
        jointV.add(j1);
        jointV.add(j2);
        jointV.add(j3);
        jointV.add(j4);
        jointV.add(j5);
        jointV.add(j6);
        jointV.add(j7);
        jointV.add(j8);

        LinkedList<Member3> memV = new LinkedList<>();
        memV.add(t1);
        memV.add(t2);
        memV.add(t3);
        memV.add(t4);
        memV.add(t5);
        memV.add(t6);
        memV.add(t7);
        memV.add(t8);

        for (int i = 0; i < memV.size(); i++) {
            memV.get(i).setA(A);
            memV.get(i).setE(E);
            memV.get(i).setG(G);
            memV.get(i).setIz(Iz);
            memV.get(i).setIy(Iy);
            memV.get(i).setIx(Ix);
            memV.get(i).memLength();
            memV.get(i).directionCosines();
            memV.get(i).rotationMatrix();
            memV.get(i).localStiffness();
            memV.get(i).globalStiffness();
            memV.get(i).printInformation();
        }

        Joint3[] j = {j1, j2, j3, j4, j5, j6, j7, j8};
        Member3[] m = {t1, t2, t3, t4, t5, t6, t7, t8};
        Str.jointVector = j;
        Str.memberVector = m;
        j4.setRrx(true);
        j4.setRry(true);
        j4.setRrz(true);
        j4.setRrmx(true);
        j4.setRrmy(true);
        j4.setRrmz(true);
        j3.setRrx(true);
        j3.setRry(true);
        j3.setRrz(true);
        j3.setRrmx(true);
        j3.setRrmy(true);
        j3.setRrmz(true);
        j2.setRrx(true);
        j2.setRry(true);
        j2.setRrz(true);
        j2.setRrmx(true);
        j2.setRrmy(true);
        j2.setRrmz(true);
        j1.setRrx(true);
        j1.setRry(true);
        j1.setRrz(true);
        j1.setRrmx(true);
        j1.setRrmy(true);
        j1.setRrmz(true);

        DecimalFormat d = new DecimalFormat("0.0000E0");
        RealMatrixFormat x = new RealMatrixFormat("[", "]", "[", "]", "\n", " ", d);
        Str.DetermineDof();
        System.out.println("-----------------------------------------------------------");
        System.out.println("STRUCTURAL DOF: " + Str.StrDof);
        System.out.println("-----------------------------------------------------------");
        Str.CountRestrainedDOFs();
        System.out.println("TOTAL NUMBER OF RESTRAINED DOF: " + Str.ConstrDof);
        System.out.println("-----------------------------------------------------------");

        st = System.nanoTime();
        Str.AssembleStiffnessC();
        long end = System.nanoTime();
        float timeel = (end - st) / 1000000000.0f;
        System.out.println("time elapsed: " + timeel + " sec");

//        Str.AssembleStiffness();
        System.out.println("STRUCTURE STIFFNESS MATRIX: ");
        System.out.println("-----------------------------------------------------------");
//        printMatrix(Str.globalStiffness);
//        System.out.println("STRUCTURE STIFFNESS MATRIX: " + Str.globalStiffnessC.rows + " X " + Str.globalStiffnessC.columns);
        System.out.println("-----------------------------------------------------------");
        //       Str.globalStiffnessC.print();
        System.out.println("");
        Str.EquivalentLoadVectorC();
        System.out.println("-----------------------------------------------------------");
        System.out.println("EQUIVALENT LOAD VECTOR: ");
        System.out.println("-----------------------------------------------------------");
//        printVector(Str.loadVector, 2);
        Str.ApplyBoundaryConditionsC();
        System.out.println("REDUCED STRUCTURAL STIFFNESS MATRIX: " + Str.reducedStiffnessC.rows + " X " + Str.reducedStiffnessC.columns);
        System.out.println("-----------------------------------------------------------");
        Str.reducedStiffnessC.print();
        System.out.println("-----------------------------------------------------------");
        System.out.println("REDUCED LOAD VECTOR: ");
        System.out.println("-----------------------------------------------------------");
//        printVector(Str.reducedLoadVector, 2);
        Str.AssembleReactionStiffnessC();
        System.out.println("-----------------------------------------------------------");
        System.out.println("REACTION STIFFNESS MATRIX: " + Str.reactionStiffnessC.rows + " X " + Str.reactionStiffnessC.columns);
        System.out.println("-----------------------------------------------------------");
        Str.reactionStiffnessC.print();
//        System.out.println("-----------------------------------------------------------");
//        System.out.println("REACTION LOAD VECTOR: ");
//        System.out.println("-----------------------------------------------------------");
//        printVector(Str.reactionLoadVector, 2);
        Str.ComputeDisplacementsC(false);
        System.out.println("-----------------------------------------------------------");
        System.out.println("UNKNOWN DISPLACEMENTS: ");
        System.out.println("-----------------------------------------------------------");
        System.out.println(Str.displacementVectorC.toString());
//        System.out.println("-----------------------------------------------------------");
//        System.out.println("UNKNOWN REACTIONS: ");
//        System.out.println("-----------------------------------------------------------");
//        printVector(Str.reactionVector, 1);
        System.out.println("        ");
        Str.AssembleDisplacementsC();
        Str.ComputeReactionsC();
        Str.MemberEndActionsC();
//        long end = System.nanoTime();
//        float timeel = (end - st) / 1000000000.0f;
//        System.out.println("time elapsed: " + timeel + " sec");

    }

    public static void analyze() {

    }

    public static void structurePenalty(Structure3 Str, boolean printout) {
        Str.strPenalty = 0.0;
        for (int k = 0; k < Str.memberVector.length; k++) {
            Member3 m1 = Str.memberVector[k];
            internalActionCoordinates(m1);
            double spenYL = 0.0;
            double spenYM = 0.0;
            double spenYR = 0.0;
            double spenY = 0.0;
            double spenZ = 0.0;
            double biaxpenl, biaxpenm, biaxpenr, biaxpen;
            if (!m1.isVertical()) {
                //check shear in the Y-direction

                spenYL = shearPenaltyY(m1, Math.abs(m1.shearYCoords.get(0).y), m1.maxMx, 0);
                spenYR = shearPenaltyY(m1, Math.abs(m1.shearYCoords.get(m1.shearYCoords.size() - 1).y), m1.maxMx, 2);
                //check shear in the Z-direction
                if (Math.abs(m1.maxVz) > 2.00) {
                    spenZ = shearPenaltyZ(m1, m1.maxVz, m1.maxMx);
                } else {
                    spenZ = 0.0;
                    m1.shearZEff = 1.0;
                }
                biaxpenl = biaxialPenalty(m1, 0);
                biaxpenm = biaxialPenalty(m1, 1);
                biaxpenr = biaxialPenalty(m1, 2);
 //               System.out.println("------------------------------------------------");
                biaxpen = (biaxpenl + biaxpenm + biaxpenr);
                m1.biaxialEff = 1 - biaxpen/3.0;
  //                          System.out.println("biaxL"+biaxpenl+"biaxM"+biaxpenm+"biaxR"+biaxpenr);

            } else {
                //column shear spacing shall not exceed the following
                //240mm,12*dia,60%*max(b,h)
                spenY = columnShearPenalty(m1);
                spenZ = 0.0;
                biaxpen = biaxialPenalty(m1, 3);
                m1.biaxialEff = 1 - (biaxpen);

            }
            //Check biaxial moment interaction efficiency
            //Calculate sizing penalty(b/h ratio, minimum reinforcement, maximum reinforcement)
            double sizepen = totalSizePenalty(m1);

            //Check shear-torsion
//            double stpen = ShearTorsionPenalty(m1);
            //Check torsion
//            double tor = torsionPenalty(m1);
            //         m1.TorsionEff=tor;
            //           double totalPenalty = spenY*kv + spenZ*kv + biaxpen*kb + stpen*kt + tor*kt;
            double totalPenalty = 0.0;
            if (m1.isVertical()) {
                totalPenalty = biaxpen * kb + (spenY + spenZ) * 0.5 * kv;// + sizepen;
            } else {
                totalPenalty = biaxpen * kb + ((spenYL + spenYM + spenYR) / 3 + spenZ) * kv;// + sizepen;                
            }

            if (printout == true) {
                System.out.println("Member: " + (k + 1) + " biaxial: " + biaxpen + " shearY: " + spenY + " shearZ: " + spenZ + " shear-torsion: " + " torsion: " + "sum: " + totalPenalty);
//                System.out.println("Member: " + (k + 1) + " biaxial: " + biaxpen + " shearY: " + shearpen + "sum: " + totalPenalty);
            }
            m1.totalPenalty = totalPenalty;
            //Check min and max reinf   
            Str.strPenalty += totalPenalty;
        }
    }

    public static void BuildingPenalty(Structure3 S) {
        //generate uniaxial interaction coordinates for all moment critical regions
        //compute biaxial penalty for all critical regions
        //compute shear penalty for all critical regions

    }

    public static void main(String[] args) throws IOException {
        unitTest4();

    }


//**********************************************************************************************
    //SETTERS AND GETTERS//
//**********************************************************************************************
    public static double getKb() {
        return kb;
    }

    public static void setKb(double kb) {
        Structure3.kb = kb;
    }

    public static double getKv() {
        return kv;
    }

    public static void setKv(double kv) {
        Structure3.kv = kv;
    }

    public static double getKt() {
        return kt;
    }

    public static void setKt(double kt) {
        Structure3.kt = kt;
    }

    public void setGlobalStiffness(CompRowMatrix globalStiffness) {
        this.globalStiffness = globalStiffness;
    }

    public void setReducedStiffness(CompRowMatrix reducedStiffness) {
        this.reducedStiffness = reducedStiffness;
    }

    public void setReactionStiffness(CompRowMatrix reactionStiffness) {
        this.reactionStiffness = reactionStiffness;
    }

    public void setReducedLoadVector(RealVector reducedLoadVector) {
        this.reducedLoadVector = reducedLoadVector;
    }

    public void setReactionLoadVector(RealVector reactionLoadVector) {
        this.reactionLoadVector = reactionLoadVector;
    }

    public void setLoadVector(RealVector loadVector) {
        this.loadVector = loadVector;
    }

    public void setDisplacementVector(RealVector displacementVector) {
        this.displacementVector = displacementVector;
    }

    public void setTrueDisplacementVector(RealVector trueDisplacementVector) {
        this.trueDisplacementVector = trueDisplacementVector;
    }

    public void setReactionVector(RealVector reactionVector) {
        this.reactionVector = reactionVector;
    }

    public void setJointVector(Joint3[] jointVector) {
        this.jointVector = jointVector;
    }

    public void setMemberVector(Member3[] memberVector) {
        this.memberVector = memberVector;
    }

    public void setBeamVector(Member3[] beamVector) {
        this.beamV = beamVector;
    }

    public void setColumnVector(Member3[] columnVector) {
        this.columnV = columnVector;
    }

    public void setStrDof(int StrDof) {
        this.StrDof = StrDof;
    }

    public void setConstrDof(int ConstrDof) {
        this.ConstrDof = ConstrDof;
    }

    public void setUnconstrainedDOF(int[] unconstrainedDOF) {
        this.unconstrainedDOF = unconstrainedDOF;
    }

    public void setConstrainedDOF(int[] constrainedDOF) {
        this.constrainedDOF = constrainedDOF;
    }

    public void setNumJoints(int numJoints) {
        this.numJoints = numJoints;
    }

    public void setNumElements(int numElements) {
        this.numElements = numElements;
    }

    public CompRowMatrix getGlobalStiffness() {
        return globalStiffness;
    }

    public CompRowMatrix getReducedStiffness() {
        return reducedStiffness;
    }

    public CompRowMatrix getReactionStiffness() {
        return reactionStiffness;
    }

    public RealVector getReducedLoadVector() {
        return reducedLoadVector;
    }

    public RealVector getReactionLoadVector() {
        return reactionLoadVector;
    }

    public RealVector getLoadVector() {
        return loadVector;
    }

    public RealVector getDisplacementVector() {
        return displacementVector;
    }

    public RealVector getTrueDisplacementVector() {
        return trueDisplacementVector;
    }

    public RealVector getReactionVector() {
        return reactionVector;
    }

    public Joint3[] getJointVector() {
        return jointVector;
    }

    public Member3[] getMemberVector() {
        return memberVector;
    }

    public Member3[] getBeamVector() {
        return beamV;
    }

    public Member3[] getColumnVector() {
        return columnV;
    }

    public int getStrDof() {
        return StrDof;
    }

    public int getConstrDof() {
        return ConstrDof;
    }

    public int[] getUnconstrainedDOF() {
        return unconstrainedDOF;
    }

    public int[] getConstrainedDOF() {
        return constrainedDOF;
    }

    public int getNumJoints() {
        return numJoints;
    }

    public int getNumElements() {
        return numElements;
    }

}
