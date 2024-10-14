package com.example;

public class Vector {
    private int dimensions;
    private double[] vector;

    // Default constructor
    public Vector() {
        dimensions = 0;
        vector = new double[0];
    }

    // Constructor with an array
    public Vector(double[] array) {
        if (array == null) {
            throw new IllegalArgumentException("Array cannot be null.");
        }
        dimensions = array.length;
        vector = array.clone();  // Create a copy of the array
    }

    // Copy constructor
    public Vector(Vector vector) {
        if (vector == null) {
            throw new IllegalArgumentException("Vector cannot be null.");
        }
        dimensions = vector.getDimensions();
        this.vector = vector.getArray().clone();  // Create a deep copy
    }

    public double[] getArray() {
        return vector.clone();  // Return a copy of the array to prevent modification
    }

    public int getDimensions() {
        return dimensions;
    }

    public void add(Vector addVector) throws WrongDimensionsException {
        if (addVector == null) {
            throw new IllegalArgumentException("Vector to add cannot be null.");
        }
        if (addVector.getDimensions() != dimensions) {
            throw new WrongDimensionsException("Vectors must have the same dimensions for addition.");
        }
        for (int i = 0; i < dimensions; ++i) {
            vector[i] += addVector.getArray()[i];
        }
    }

    public double dot(Vector v) throws WrongDimensionsException {
        if (v == null) {
            throw new IllegalArgumentException("Vector cannot be null.");
        }
        if (v.getDimensions() != dimensions) {
            throw new WrongDimensionsException("Vectors must have the same dimensions for dot product.");
        }
        double res = 0.0;
        for (int i = 0; i < dimensions; ++i) {
            res += vector[i] * v.getArray()[i];
        }
        return res;
    }
}
