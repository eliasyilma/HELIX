/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package UI;

import java.util.ArrayList;
import java.util.Arrays;
import no.uib.cipr.matrix.DenseVector;
import no.uib.cipr.matrix.sparse.CompRowMatrix;

/**
 *
 * @author user
 */
public class SparseArray {

    //a storage for an array containing arrays holding the non-zero values of each row 
    //eg. {{3,4}{1,7,1}{2,8,1,4}}
    public double[][] AValue;
    //an index storage array consisting of arrays that hold the column positions of each non-zero entry 
    public int[][] AIndex;
    public int nonzero;
    public int rows, columns;

    public SparseArray(double[][] A, int[][] B, int rows, int columns, int Nonzero) {
        this.AValue = A;
        this.AIndex = B;
        this.nonzero = Nonzero;
        this.rows = rows;
        this.columns = columns;
    }

    //computes A*b, where b is a vector
    public double[] mmultV(double[] b) {
        double sum = 0.0;
        double[] product = new double[b.length];
        int numRows = AIndex.length;
        for (int i = 0; i < numRows; i++) {
            int[] cindex = AIndex[i];
            double[] cvalue = AValue[i];
            int rlength = AIndex[i].length;
            for (int j = 0; j < rlength; j++) {
                sum += cvalue[j] * b[cindex[j]];
            }
            product[i] = sum;
            sum = 0;
        }
        return product;
    }

    public double[][] toMatrix() {
        double[][] mat = new double[rows][columns];
        for (int i = 0; i < rows; i++) {
            int[] aindex = AIndex[i];
            double[] avalue = AValue[i];

            for (int j = 0; j < aindex.length; j++) {
                mat[i][aindex[j]] = avalue[j];
            }
        }
        return mat;
    }

    public CompRowMatrix toCSR() {
        CompRowMatrix mat = new CompRowMatrix(rows, columns, AIndex);
        for (int i = 0; i < rows; i++) {
            int[] aindex = AIndex[i];
            double[] avalue = AValue[i];
            for (int j = 0; j < aindex.length; j++) {
                mat.set(i, aindex[j], avalue[j]);
            }
        }
        return mat;
    }

    public DenseVector toDenseV() {
        DenseVector mat = new DenseVector(rows);
        for (int i = 0; i < rows; i++) {
            int[] aindex = AIndex[i];
            double[] avalue = AValue[i];

            for (int j = 0; j < aindex.length; j++) {
                //               System.out.println("i "+i+" j "+j+" "+avalue[j]);
                mat.set(i, avalue[j]);

            }
        }
        return mat;
    }

    public void print() {
        printM(toMatrix());
    }

    public void printM(double[][] mat) {
        for (int i = 0; i < mat.length; i++) {
            for (int j = 0; j < mat[0].length; j++) {
                System.out.printf("%12.2f", mat[i][j]);
            }
            System.out.println("");
        }
    }

    public double[] mmultTV(double[] b) {
        double[] product = new double[b.length];
        int numRows = AValue.length;
        double value = 0.0;
        double[] valrow;
        int[] indexrow;
        int vlength;
        for (int i = 0; i < numRows; i++) {
            valrow = AValue[i];
            indexrow = AIndex[i];
            vlength = valrow.length;
            value = b[i];
            for (int j = 0; j < vlength; j++) {
                product[indexrow[j]] += valrow[j] * value;
            }
        }
        return product;
    }

    public SparseArray mmult(SparseArray B) {
        int dimension = B.columns;
        double[][] Cvalue = new double[dimension][1];
        int[][] Cindex = new int[dimension][1];
        int[] temp = new int[dimension];
        double[] tempValue = new double[dimension];
        int[] tempIndex = new int[dimension];
        double[][] Bvalue = B.AValue;
        int[][] Bindex = B.AIndex;
        int nonzero = 0;
        double scalar = 0;
        int len = -1;
        int index = 0;
        int jcol = 0;
        int jpos = 0;
        for (int i = 0; i < temp.length; i++) {
            temp[i] = -1;
        }
        long l1 = System.currentTimeMillis();
        for (int i = 0; i < AValue.length; i++) {
            double[] avalue = AValue[i];
            int[] aindex = AIndex[i];
            for (int j = 0; j < avalue.length; j++) {
                scalar = avalue[j];
                index = aindex[j];
                double[] bvalue = Bvalue[index];
                int[] bindex = Bindex[index];
                for (int k = 0; k < bvalue.length; k++) {
                    jcol = bindex[k];
                    jpos = temp[jcol];
                    if (jpos == -1) {
                        len++;
                        nonzero++;
                        tempIndex[len] = jcol;
                        temp[jcol] = len;
                        tempValue[len] = scalar * bvalue[k];
                    } else {
                        tempValue[jpos] += scalar * bvalue[k];
                    }
                }
            }
            double[] cvalue = new double[len + 1];
            int[] cindex = new int[len + 1];
            System.arraycopy(tempValue, 0, cvalue, 0, len + 1);
            System.arraycopy(tempIndex, 0, cindex, 0, len + 1);
            Cvalue[i] = cvalue;
            Cindex[i] = cindex;
            for (int ii = 0; ii < len + 1; ii++) {
                temp[tempIndex[ii]] = -1;
            }
            len = -1;
        }

        return new SparseArray(Cvalue, Cindex, rows, B.columns, nonzero);
    }

    //NEW TASK: a method for removing entire columns and rows in a sparse array.
    //  COLUMN'S GONNA BE A TOUGH SUCKER TO ACHIEVE. 
    public SparseArray removeColumns(int[] index) {

        SparseArray modded = new SparseArray(new double[rows][1], new int[rows][1], rows, columns, nonzero);
        int rlen = rows;
        for (int i = 0; i < rlen; i++) {
//iterate through rows
            int[] ind = AIndex[i];
            double[] val = AValue[i];
            sort(val, ind);
            ArrayList<Integer> remColIndices = new ArrayList<>();
            int maxIndex=ind[0];
            for (int j = 0; j < ind.length; j++) {
                int a = Arrays.binarySearch(index, ind[j]);
                if(maxIndex<ind[j]){
                    maxIndex=ind[j];
                }
                if (a >= 0) {
                    //ind[j] doesn't exist in the list of indices to be removed
                    remColIndices.add(j);
                }
            }
            modded.AValue[i] = removeAllValue(remColIndices, ind, val, remColIndices.size());
            modded.AIndex[i] = removeAllIndex(remColIndices, ind, index, columns,maxIndex);
        }
        return modded;
    }

    public double[] removeAllValue(ArrayList<Integer> rindices, int[] index, double[] val, int size) {
        double[] mod = new double[val.length - size];
        int m = 0;
        for (int i = 0; i < val.length; i++) {

            if (!rindices.contains(i)) {
                mod[m] = val[i];
                m++;
            }
        }
        return mod;
    }

    public int[] removeAllIndex(ArrayList<Integer> removeColindices, int[] val, int[] tobeRemovedColIndices, int rows,int maxIndex) {
        int[] mod = new int[val.length - removeColindices.size()];
        int r = 0, j = 0;
        for (int i = 0; i < 3000; i++) {
            if (contains(tobeRemovedColIndices, i)) {//is this column to be removed?
                //if this column is not a zero
            } else {
                if (contains(val, i)) {
                    mod[r] = j;
                    r++;
                    j++;
                } else {//it is a zero
                    j++;
                }
            }
//            if(mod.length<i){
//                break;
//            }
        }
        //       System.out.println("x="+x);
        return mod;
    }

    public boolean contains(int[] array, int key) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == key) {
                return true;
            }
        }
        return false;
    }

    public SparseArray removeRow(int index) {
        SparseArray modded = new SparseArray(new double[rows - 1][1], new int[rows - 1][1], rows - 1, columns, nonzero);
        int rlen = rows;
        int l = 0;
        for (int i = 0; i < rlen; i++) {
            if (index == i) {
                //do nothing
            } else {
                modded.AIndex[l] = AIndex[i];
                modded.AValue[l] = AValue[i];
                l++;
            }
        }
        return modded;
    }

    public SparseArray removeRows(int[] indices) {
        SparseArray modded = new SparseArray(new double[rows - indices.length][1], new int[rows - indices.length][1], rows - indices.length, columns, nonzero);
        int rlen = rows;
        int l = 0;
        for (int i = 0; i < rlen; i++) {
            int s = Arrays.binarySearch(indices, i);
            if (s < 0) {
                modded.AIndex[l] = AIndex[i];
                modded.AValue[l] = AValue[i];
                l++;
            } else {

            }
        }
        return modded;
    }

    public static void sort2(double[] values, int[] indices) {
        for (int i = 0; i < indices.length - 1; i++) {
            int minIndex = i;
            for (int j = i + 1; j < indices.length; j++) {
                if (indices[minIndex] > indices[j]) {
                    minIndex = j;

                    int temp = indices[minIndex];
                    indices[minIndex] = indices[i];
                    indices[i] = temp;

                    double dtemp = values[minIndex];
                    values[minIndex] = values[i];
                    values[i] = dtemp;
                }
            }
        }
    }

    /**
     * The method for sorting the numbers
     */
    public static void sort(double[] values, int[] indices) {
        for (int i = 0; i < indices.length - 1; i++) {

            // Find the minimum in the list[i..list.length-1]
            int currentMinInd = indices[i];
            double currentMinVal = values[i];
            int currentMinIndex = i;

            for (int j = i + 1; j < indices.length; j++) {

                if (currentMinInd > indices[j]) {
                    currentMinInd = indices[j];
                    currentMinVal = values[j];
                    currentMinIndex = j;
                }
            }

            // Swap list[i] with list[currentMinIndex] if necessary;
            if (currentMinIndex != i) {
                indices[currentMinIndex] = indices[i];
                indices[i] = currentMinInd;
                values[currentMinIndex] = values[i];
                values[i] = currentMinVal;

            }
        }
    }

//get Value at cell(i,j)
    public double get(int i, int j) {
        int[] columnIndices = AIndex[i];
        double[] columnValues = AValue[i];

        int indLen = columnIndices.length;
        for (int k = 0; k < indLen; k++) {
            //if the suggested j index exists inside of the current column index
            //array, then it has a value at that index.
            if (columnIndices[k] == j) {
                return columnValues[k];
            } else {

            }
        }
        //otherwise 0 is at that location
        return 0;
    }

    //to string
    public void printM() {
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < columns; j++) {
                System.out.printf("%12.2f", get(i, j));
            }
            System.out.println();
        }
    }

    public boolean setValue2(int i, int j, double value) {
        double[] avalue = AValue[i];
        int[] aindex = AIndex[i];
        boolean test = true;
        for (int ii = 0; ii < aindex.length; ii++) {
            if (aindex[ii] == j) {
                avalue[ii] = value;
                test = false;
            }
        }
        if (test) {
            double[] valuenew = new double[aindex.length + 1];
            int[] indexnew = new int[aindex.length + 1];
            for (int ii = 0; ii < aindex.length; ii++) {
                valuenew[ii] = avalue[ii];
                indexnew[ii] = aindex[ii];
            }
            valuenew[valuenew.length - 1] = value;
            indexnew[indexnew.length - 1] = j;
            AValue[i] = valuenew;
            AIndex[i] = indexnew;
        }
        return test;
    }

    public boolean addValue(int i, int j, double value) {
        double[] avalue = AValue[i];
        int[] aindex = AIndex[i];
        boolean test = true;
        for (int ii = 0; ii < aindex.length; ii++) {
            if (aindex[ii] == j) {
                avalue[ii] += value;
                test = false;
            }
        }
        if (test) {
            double[] valuenew = new double[aindex.length + 1];
            int[] indexnew = new int[aindex.length + 1];
            for (int ii = 0; ii < aindex.length; ii++) {
                valuenew[ii] = avalue[ii];
                indexnew[ii] = aindex[ii];
            }
            valuenew[valuenew.length - 1] = value;
            indexnew[indexnew.length - 1] = j;
            AValue[i] = valuenew;
            AIndex[i] = indexnew;
        }
        return test;
    }

    public boolean setValue(int i, int j, double value) {
        double[] avalue = AValue[i];
        int[] aindex = AIndex[i];
        boolean test = true;
        for (int ii = 0; ii < aindex.length; ii++) {
            if (aindex[ii] == j) {
                avalue[ii] = value;
                test = false;
            }
        }
        if (test) {
            double[] valuenew = new double[aindex.length + 1];
            int[] indexnew = new int[aindex.length + 1];
            System.arraycopy(avalue, 0, valuenew, 0, avalue.length);
            System.arraycopy(aindex, 0, indexnew, 0, aindex.length);
            valuenew[valuenew.length - 1] = value;
            indexnew[indexnew.length - 1] = j;
            AValue[i] = valuenew;
            AIndex[i] = indexnew;
        }
        return test;
    }

    public static SparseArray random(int i, int j) {
        double[][] tempval = new double[i][1];
        int[][] tempind = new int[i][1];
        int nnz = 0;
        SparseArray rand = new SparseArray(tempval, tempind, i, j, 0);
        for (int k = 0; k < i; k++) {
            for (int l = 0; l < j; l++) {
                if (Math.random() < 0.008) {
                    rand.setValue(k, l, Math.abs(Math.random() * 100));
                    nnz++;
                }
            }
        }
        rand.nonzero = nnz;
        return rand;
    }
//generate random sparse arrays with varying sparse percentage
    //    public SparseArray random(int rows,int columns,double ratio){
    //        SparseArray ran;
    //        
    //        
    //        return ran;
    //    }

    public void printSA() {
        System.out.println("Indices: ");
        for (int i = 0; i < AIndex.length; i++) {
            for (int j = 0; j < AIndex[i].length; j++) {
                System.out.print("  " + AIndex[i][j]);
            }
            System.out.println("");

        }
        System.out.println("Values: ");
        for (int i = 0; i < AValue.length; i++) {
            for (int j = 0; j < AValue[i].length; j++) {
                System.out.printf("%12.2f", AValue[i][j]);
            }
            System.out.println("");

        }
    }

    public static void main(String[] args) {
        double[][] val = {{2001.5, 4.44}, {1338.3, 10}, {4.44, 10, 44.44}};
        double[][] v2 = {{500.5, 1, -500}, {500.5, 1, -0.5, 1}, {1, 1, 5.33, -1, 1.33}, {-500, 500.5, 1}, {-0.5, -1, 500.5, -1}, {1, 1.33, 1, -1, 5.33}};
        int[][] index3 = {{0, 2, 3}, {1, 2, 4, 5}, {0, 1, 2, 4, 5}, {0, 2, 5}, {1, 2, 4, 5}, {1, 2, 3, 4, 5}};

        int[][] index = {{0, 2}, {1, 2}, {0, 1, 2}};
        int[][] index2 = {{0}, {0}, {0}, {0}, {0}, {0}};

        double[][] vec1 = {{10}, {-24}, {-24}, {0}, {-24}, {24}};
        SparseArray m = new SparseArray(v2, index3, 6, 6, 24);

        int[] vecx = {1, 3, 32, 4, 2, 7, 6, 8};
        double[] aaa = {5.33, 3.4, 5.665, 3.1, 7.9, 88.6, 2.1, 3.66};

        sort(aaa, vecx);
        for (int l = 0; l < vecx.length; l++) {
            System.out.print("  " + vecx[l]);
        }
        System.out.println("");
        for (int k = 0; k < aaa.length; k++) {
            System.out.print("  " + aaa[k]);
        }
        System.out.println("");

//        SparseArray prod = m.mmult(m);
//        SparseArray rand = random(8000, 8000);
//        SparseArray randV = new SparseArray(vec1, index2, 6, 1, 6);
//
////        long steli = System.nanoTime();
//
////        double[][] a = rand.toMatrix();
        CompRowMatrix D = m.toCSR();
        D.set(2,7,12);
////              Matrix XX =new Matrix(8000, 8000);
////                      D.mult(D, XX);
//        CompRowMatrix A = m.toCSR();
////        A.DenseVector b = randV.toDenseV();
//        DenseVector x = new DenseVector(6);
//        System.out.println(A.toString());
//        long eneli = System.nanoTime();
//        double elapsedeli = (eneli - steli) / 1000000000.0;
//
//        steli = System.nanoTime();
//
//        IterativeSolver solver = new CG(x);
//Create a Cholesky preconditioner
//        Preconditioner M = new ILU(A);
//Set up the preconditioner, and attach it
//        M.setMatrix(A);
//        solver.setPreconditioner(M);
//Add a convergence monitor
        //       solver.getIterationMonitor().setIterationReporter(new OutputIterationReporter());
//Start the solver, and check for problems
//        try {
////            solver.solve(A, b, x);
//        } catch (IterativeSolverNotConvergedException e) {
//            System.err.println("Iterative solver failed to converge");
//        }
        //       System.out.println(x.toString());
//        SparseArray product = rand.mmult(rand2);
//        eneli = System.nanoTime();
//        elapsedeli = (eneli - steli) / 1000000000.0;
//        //       System.out.println("prod:" + product.get(0, 0));
//        System.out.println("product: " + elapsedeli);
        //       System.out.println(
        //               "sparsity index:" + rand.nonzero / (8000.0 * 8000.0));
        m.printM();
        System.out.println("");
        m.printSA();
//        SparseArray a = m.removeRow(1);
//        System.out.println("");
//        //a.printM();
        int[] vv = {0, 2, 4};
//        SparseArray aw = m.removeRows(vv);
//        System.out.println("");
//        aw.printSA();
//        
        SparseArray aw = m.removeColumns(vv);
        System.out.println("");
        aw.printSA();
//        
//        int ax = Arrays.binarySearch(index3[0], -9);
//        System.out.println("ax; " + ax);
//        //       prod.printM();
    }
}
