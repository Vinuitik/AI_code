import pandas as pd
import numpy as np
import random
from scipy.spatial.distance import cdist

# Load the dataset
dataset = pd.read_csv("friction_wind_dataset.csv")

# Extract data points
data = dataset[['friction_coefficient', 'wind_force']].to_numpy()

def random_k_medoids_clustering(data, k, max_iter=100):
    """
    Perform random k-medoids clustering.
    Args:
        data (np.ndarray): Dataset as a numpy array.
        k (int): Number of clusters.
        max_iter (int): Maximum number of iterations.
    Returns:
        list: Final medoids.
        dict: Clusters (medoid -> points).
    """
    # Step 1: Initialize medoids randomly
    medoids = random.sample(list(data), k)

    for _ in range(max_iter):
        # Step 2: Assign points to the nearest medoid
        distances = cdist(data, medoids, metric='euclidean')
        cluster_labels = np.argmin(distances, axis=1)
        
        # Step 3: Update medoids for each cluster
        new_medoids = []
        for i in range(k):
            cluster_points = data[cluster_labels == i]
            if len(cluster_points) > 0:
                medoid = cluster_points[np.argmin(cdist(cluster_points, cluster_points).sum(axis=1))]
                new_medoids.append(medoid)
            else:
                new_medoids.append(medoids[i])
        
        # Check for convergence
        if np.allclose(medoids, new_medoids):
            break
        medoids = new_medoids

    # Step 4: Reassign points to final clusters
    final_clusters = {tuple(m): [] for m in medoids}
    distances = cdist(data, medoids, metric='euclidean')
    cluster_labels = np.argmin(distances, axis=1)
    for i, point in enumerate(data):
        medoid = tuple(medoids[cluster_labels[i]])
        final_clusters[medoid].append(tuple(point))
    
    return medoids, final_clusters

# Example usage:
k = 100  # Number of clusters
medoids, clusters = random_k_medoids_clustering(data, k)

# Save medoids to a CSV file
medoids_df = pd.DataFrame(medoids, columns=['friction', 'windforce'])
medoids_df.to_csv("medoids.csv", index=False)

print("Final medoids saved to medoids.csv.")
