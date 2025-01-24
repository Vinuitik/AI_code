import numpy as np
import pandas as pd

# Parameters for dataset generation
num_samples = 20000  # Number of data points

# Define ranges for friction coefficient and wind force
friction_range = (0.1, 1.0)  # Friction coefficient range (0 = no friction, 1 = very high friction)
wind_force_range = (-10, 10)  # Wind force range (-10 = strong backward wind, 10 = strong forward wind)

# Generate random values within the defined ranges
friction_coefficients = np.random.uniform(friction_range[0], friction_range[1], num_samples)
wind_forces = np.random.uniform(wind_force_range[0], wind_force_range[1], num_samples)

# Combine into a dataset
data = {
    "friction_coefficient": friction_coefficients,
    "wind_force": wind_forces
}

dataset = pd.DataFrame(data)

# Save the dataset to a CSV file
dataset.to_csv("friction_wind_dataset.csv", index=False)

print("Dataset generated and saved as 'friction_wind_dataset.csv'.")
