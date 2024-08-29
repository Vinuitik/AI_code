package com.example;

public class Matrix {
    private int rows;
    private int columns;
    private double[][] matrix;
    Matrix(){
        rows = 0;
        columns = 0;
        matrix = new double[0][0];
    }
    Matrix(double[][] matrix){
        if(matrix==null){
            throw new IllegalArgumentException("Matrix cannot be null.");
        }
        rows = matrix.length;
        columns = matrix[0].length;
        this.matrix = matrix.clone();
    }
    Matrix(Matrix matrix){
        if(matrix==null){
            throw new IllegalArgumentException("Matrix cannot be null.");
        }
        rows = matrix.getRows();
        columns = matrix.getColumns();
        this.matrix = matrix.getMatrix().clone();
    }
    Matrix(int rows, int columns){
        if (rows <= 0 || columns <= 0) {
            throw new IllegalArgumentException("Matrix dimensions must be positive.");
        }
        this.rows = rows;
        this.columns =columns;
        matrix = new double[rows][columns];
    }
    public int getRows(){
        return rows;
    }
    public int getColumns(){
        return columns;
    }
    public double[][] getMatrix(){
        return matrix;
    }
    public void setValue(int row, int col, int val){
        matrix[row][col] = val;
    }
    public Matrix add(Matrix addMatrix) throws WrongDimensionsException {
        if (addMatrix == null) {
            throw new IllegalArgumentException("Matrix to add cannot be null.");
        }
        if (addMatrix.getRows() != rows || addMatrix.getColumns() != columns) {
            throw new WrongDimensionsException("Matrices must have the same dimensions for addition.");
        }
        Matrix result = new Matrix(rows, columns);
        for (int i = 0; i < rows; ++i) {
            for (int j = 0; j < columns; ++j) {
                result.matrix[i][j] = matrix[i][j] + addMatrix.matrix[i][j];
            }
        }
        return result;
    }


    
    public Matrix multiply(Matrix multMatrix) throws WrongDimensionsException {
        if (multMatrix == null) {
            throw new IllegalArgumentException("Matrix to multiply cannot be null.");
        }
        if (columns != multMatrix.getRows()) {
            throw new WrongDimensionsException("Matrices have incompatible dimensions for multiplication.");
        }

        return multiplyRecursive(this, multMatrix);
    }
    private Matrix multiplyRecursive(Matrix A, Matrix B) {
        int n = A.getRows();
        Matrix result = new Matrix(n, B.getColumns());

        if (n <= 1) {
            // Base case: simple multiplication of 1x1 matrices
            result.matrix[0][0] = A.matrix[0][0] * B.matrix[0][0];
        } else {
            int newSize = n / 2;
            Matrix A11 = new Matrix(newSize, newSize);
            Matrix A12 = new Matrix(newSize, newSize);
            Matrix A21 = new Matrix(newSize, newSize);
            Matrix A22 = new Matrix(newSize, newSize);
            Matrix B11 = new Matrix(newSize, newSize);
            Matrix B12 = new Matrix(newSize, newSize);
            Matrix B21 = new Matrix(newSize, newSize);
            Matrix B22 = new Matrix(newSize, newSize);

            // Split matrices into 4 submatrices
            splitMatrix(A, A11, A12, A21, A22);
            splitMatrix(B, B11, B12, B21, B22);

            // Compute the 7 products using Steinmeier’s approach
            Matrix M1 = multiplyRecursive(add(A11, A22), add(B11, B22)); // (A11 + A22) * (B11 + B22)
            Matrix M2 = multiplyRecursive(add(A21, A22), B11); // (A21 + A22) * B11
            Matrix M3 = multiplyRecursive(A11, subtract(B12, B22)); // A11 * (B12 - B22)
            Matrix M4 = multiplyRecursive(A22, subtract(B21, B11)); // A22 * (B21 - B11)
            Matrix M5 = multiplyRecursive(add(A11, A12), B22); // (A11 + A12) * B22
            Matrix M6 = multiplyRecursive(subtract(A21, A11), add(B11, B12)); // (A21 - A11) * (B11 + B12)
            Matrix M7 = multiplyRecursive(subtract(A12, A22), add(B21, B22)); // (A12 - A22) * (B21 + B22)

            // Combine the results
            Matrix C11 = add(subtract(add(M1, M4), M5), M7);
            Matrix C12 = add(M3, M5);
            Matrix C21 = add(M2, M4);
            Matrix C22 = add(subtract(add(M1, M3), M2), M6);

            combine(result, C11, C12, C21, C22);
        }

        return result;
    }

    private Matrix add(Matrix A, Matrix B) {
        int n = A.getRows();
        Matrix C = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C.matrix[i][j] = A.matrix[i][j] + B.matrix[i][j];
            }
        }
        return C;
    }

    private Matrix subtract(Matrix A, Matrix B) {
        int n = A.getRows();
        Matrix C = new Matrix(n, n);
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                C.matrix[i][j] = A.matrix[i][j] - B.matrix[i][j];
            }
        }
        return C;
    }

    private void splitMatrix(Matrix A, Matrix A11, Matrix A12, Matrix A21, Matrix A22) {
        int newSize = A.getRows() / 2;
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                A11.matrix[i][j] = A.matrix[i][j];
                A12.matrix[i][j] = A.matrix[i][j + newSize];
                A21.matrix[i][j] = A.matrix[i + newSize][j];
                A22.matrix[i][j] = A.matrix[i + newSize][j + newSize];
            }
        }
    }

    private void combine(Matrix C, Matrix C11, Matrix C12, Matrix C21, Matrix C22) {
        int newSize = C.getRows() / 2;
        for (int i = 0; i < newSize; i++) {
            for (int j = 0; j < newSize; j++) {
                C.matrix[i][j] = C11.matrix[i][j];
                C.matrix[i][j + newSize] = C12.matrix[i][j];
                C.matrix[i + newSize][j] = C21.matrix[i][j];
                C.matrix[i + newSize][j + newSize] = C22.matrix[i][j];
            }
        }
    }
}
