package ann;

public class FNN {
    private Matrix[] layers;
    private Vector[] biases;

    public FNN(int depth, int k){
        layers = new Matrix[depth];
        for(int i = 0;i<layers.length;++i){
            layers[i] = new Matrix(k, k, Math.random());
        }
        biases = new Vector[depth];
        for(int i = 0;i < biases.length; ++i){
            biases[i] = new Vector(k);
        } 
    }
    
    public Vector forwardPropagation(Vector input) throws WrongDimensionsException{
        Vector output = input;
        for(int i = 0;i<layers.length;++i){
            Matrix multiplicationMatrix = (Matrix.VectorToMatrix(output)).multiply(layers[i]);

            output = Vector.MatrixToVector(multiplicationMatrix);

            output.add(biases[i]);

            output.elementWiseOperation(this::Relu);
        }
        return output;
    }
    public double Relu(double x){
        if(x>=0){
            return x;
        }
        return 0;
    }

}
