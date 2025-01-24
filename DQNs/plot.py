import pandas as pd
import numpy as np
import matplotlib.pyplot as plt

# Load the dataset
dataset = pd.read_csv("friction_wind_dataset.csv")
data = dataset[['friction_coefficient', 'wind_force']].to_numpy()

# Load the medoids
medoids_df = pd.read_csv("medoids.csv")
medoids = medoids_df[['friction', 'windforce']].to_numpy()

# Scatter plot of the dataset
plt.scatter(data[:, 0], data[:, 1], c='lightblue', label='Data Points', alpha=0.6, edgecolors='w')

# Highlight medoids
plt.scatter(medoids[:, 0], medoids[:, 1], c='red', label='Medoids', marker='X', s=150)

# Add labels and legend
plt.title("Dataset and Medoids Visualization")
plt.xlabel("Friction")
plt.ylabel("Windforce")
plt.legend()
plt.grid(True)

# Show the plot
plt.show()
