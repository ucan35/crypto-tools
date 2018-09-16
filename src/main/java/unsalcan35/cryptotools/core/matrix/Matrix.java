package unsalcan35.cryptotools.core.matrix;

public class Matrix {

    private double[][] matrixArray;

    public Matrix(double[][] matrixArray) throws MatrixException {
        if (!checkMatrix(matrixArray)) {
            throw new MatrixException("Invalid matrix dimension");
        }
        this.matrixArray = matrixArray;
    }

    public Matrix(Matrix matrix) throws MatrixException {
        this(matrix.matrixArray);
    }

    // Workaround to instance matrix objects without exceptions inside the class
    private Matrix(double[][] matrixArray, int dummy) {
        // Assume matrixArray is proper
        this.matrixArray = matrixArray;
    }

    private static boolean checkMatrix(double[][] matrixArray) {
        for (int i = 0; i < matrixArray.length; i++) {
            if (matrixArray[i].length != matrixArray[0].length) {
                return false;
            }
        }
        return true;
    }

    private static double modulo(double n, int m) {
        return ((n % m) + m) % m;
    }

    public Matrix multiply(double n) {
        double[][] resultMatrixArray = new double[matrixArray.length][matrixArray[0].length];
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrixArray[0].length; j++) {
                resultMatrixArray[i][j] = matrixArray[i][j] * n;
            }
        }
        return new Matrix(resultMatrixArray, 0);
    }

    public Matrix multiply(Matrix matrix) throws MatrixException {
        if (matrixArray[0].length != matrix.matrixArray.length) {
            throw new MatrixException(String.format("Invalid matrix multiplication [%d,%d] by [%d,%d]",
                    matrixArray.length, matrixArray[0].length,
                    matrix.matrixArray.length, matrix.matrixArray[0].length));
        }
        double resultMatrixArray[][] = new double[matrixArray.length][matrix.matrixArray[0].length];
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrix.matrixArray[0].length; j++) {
                for (int k = 0; k < matrixArray[0].length; k++) {
                    resultMatrixArray[i][j] += matrixArray[i][k] * matrix.matrixArray[k][j];
                }
            }
        }
        return new Matrix(resultMatrixArray, 0);
    }

    public double determinant() throws MatrixException {
        if (!checkSquareMatrix()) {
            throw new MatrixException(String.format("Non-square matrix [%d,%d]",
                    matrixArray.length, matrixArray[0].length));
        }
        return determinant(this);
    }

    private double determinant(Matrix matrix) {
        switch (matrix.matrixArray.length) {
            case 1: {
                return matrix.matrixArray[0][0];
            }
            case 2: {
                return matrix.matrixArray[0][0] * matrix.matrixArray[1][1] -
                        matrix.matrixArray[1][0] * matrix.matrixArray[0][1];
            }
            default: {
                int sum = 0;
                for (int i = 0; i < matrix.matrixArray.length; i++) {
                    sum += Math.pow(-1, i) * matrix.matrixArray[i][0] * minor(matrix, i, 0);
                }
                return sum;
            }
        }
    }

    public Matrix inverse() {
        if (!checkSquareMatrix()) {
            String.format("Non-square matrix [%d,%d]",
                    matrixArray.length, matrixArray[0].length);
        }
        double[][] tempArray = new double[matrixArray.length][matrixArray.length];
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrixArray.length; j++) {
                tempArray[j][i] = Math.pow(-1, i + j) * minor(this, i, j);
            }
        }
        return new Matrix(tempArray, 0).multiply(1 / determinant(this));
    }

    private double minor(Matrix matrix, int i, int j) {
        double[][] minorMatrixArray = new double[matrix.matrixArray.length - 1][matrix.matrixArray.length - 1];
        int x, y;
        x = y = 0;
        for (int a = 0; a < minorMatrixArray.length; a++) {
            for (int b = 0; b < minorMatrixArray.length; b++) {
                if (a == i) {
                    x = 1;
                }
                if (b == j) {
                    y = 1;
                }
                minorMatrixArray[a][b] = matrix.matrixArray[a + x][b + y];
            }
            y = 0;
        }
        return determinant(new Matrix(minorMatrixArray, 0));
    }

    public Matrix modulo(int m) {
        double[][] resultMatrixArray = new double[matrixArray.length][matrixArray[0].length];
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrixArray[0].length; j++) {
                resultMatrixArray[i][j] = modulo(matrixArray[i][j], m);
            }
        }
        return new Matrix(resultMatrixArray, 0);
    }

    public Matrix round() {
        double[][] resultMatrixArray = new double[matrixArray.length][matrixArray[0].length];
        for (int i = 0; i < matrixArray.length; i++) {
            for (int j = 0; j < matrixArray[0].length; j++) {
                resultMatrixArray[i][j] = (int) matrixArray[i][j];
            }
        }
        return new Matrix(resultMatrixArray, 0);
    }

    public boolean checkSquareMatrix() {
        return matrixArray.length == matrixArray[0].length;
    }

    public int[] length() {
        return new int[]{matrixArray.length, matrixArray[0].length};
    }

    public double[][] toArray() {
        return matrixArray;
    }

    public String toString() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < matrixArray.length; i++) {
            sb.append('[');
            for (int j = 0; j < matrixArray[0].length; j++) {
                sb.append(String.format("% .2f\t", matrixArray[i][j]));
            }
            sb.setLength(sb.length() - 1);
            sb.append("]\n");
        }
        sb.setLength(sb.length() - 1);
        return sb.toString();
    }
}