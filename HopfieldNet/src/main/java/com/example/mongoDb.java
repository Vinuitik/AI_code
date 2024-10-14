package com.example;

import java.util.ArrayList;
import java.util.List;

import org.bson.Document;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;

public class mongoDb {
    private String mongoUri;
    private String databaseName;
    private String collectionName;

    public mongoDb() {
        mongoUri = "mongodb://root:29032006@localhost:27017/admin";
        databaseName = "hopfieldDB";
        collectionName = "matrices";
    }

    public void saveMatrixToDB(double[][] matrix) {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            List<List<Double>> matrixList = convertToList(matrix);

            Document doc = new Document("matrix", matrixList);
            collection.insertOne(doc);
            System.out.println("Matrix saved to DB as an array");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void deleteAll() {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            collection.deleteMany(new Document());
            System.out.println("Matrices deleted");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void clearWeights() {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("weights");

            collection.deleteMany(new Document());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public double[][] getWeights() {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("weights");
    
            Document weightDocument = collection.find().first();
    
            if (weightDocument != null) {
                // Convert the stored list back to a 2D array
                List<List<Double>> matrixList = (List<List<Double>>) weightDocument.get("matrix");
                return convertToArray(matrixList);
            } else {
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    

    public List<double[][]> getAllMatricesFromDB() {
        List<double[][]> matrices = new ArrayList<>();

        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection(collectionName);

            MongoCursor<Document> cursor = collection.find().iterator();
            while (cursor.hasNext()) {
                Document matrixDocument = cursor.next();
                double[][] matrix = (double[][]) matrixDocument.get("matrix");
                matrices.add(matrix);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return matrices;
    }

    // Save or update the matrix in the "weights" collection
    public void saveOrUpdateMatrixInDB(double[][] matrix) {
        try (MongoClient mongoClient = MongoClients.create(mongoUri)) {
            MongoDatabase database = mongoClient.getDatabase(databaseName);
            MongoCollection<Document> collection = database.getCollection("weights");
    
            Document existingMatrix = collection.find().first();
    
            // Convert the 2D array to a list of lists
            List<List<Double>> matrixList = convertToList(matrix);
    
            if (existingMatrix != null) {
                // Update the existing matrix
                Document update = new Document("$set", new Document("matrix", matrixList));
                collection.updateOne(new Document(), update); // empty filter matches the first document
                System.out.println("Matrix updated in DB");
            } else {
                // Insert a new matrix if none exists
                Document doc = new Document("matrix", matrixList);
                collection.insertOne(doc);
                System.out.println("Matrix saved to DB as an array");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    

    private List<List<Double>> convertToList(double[][] array) {
        List<List<Double>> list = new ArrayList<>();
        for (double[] row : array) {
            List<Double> sublist = new ArrayList<>();
            for (double value : row) {
                sublist.add(value);
            }
            list.add(sublist);
        }
        return list;
    }

    private double[][] convertToArray(List<List<Double>> list) {
        int rows = list.size();
        int cols = list.get(0).size();
        double[][] array = new double[rows][cols];
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                array[i][j] = list.get(i).get(j);
            }
        }
        return array;
    }
    
}
