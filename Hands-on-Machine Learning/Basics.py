import matplotlib.pyplot as plt # getting the plotting lib
import numpy as np  # the maths lin
import pandas as pd # the datasets lib
import sklearn.linear_model  #the model lib



# Load the data
oecd_bli = pd.read_csv("oecd_bli_2015.csv", thousands=',')
gdp_per_capita = pd.read_csv("gdp_per_capita.csv",thousands=',',delimiter='\t',encoding='latin1', na_values="n/a")



# Prepare the data
country_stats = prepare_country_stats(oecd_bli, gdp_per_capita)
X = np.c_[country_stats["GDP per capita"]]
y = np.c_[country_stats["Life satisfaction"]]
# Visualize the data
country_stats.plot(kind='scatter', x="GDP per capita", y='Life satisfaction')
plt.show()
# Select a linear model
model = sklearn.linear_model.LinearRegression()
# Train the model
model.fit(X, y) ## so training the model is pretty chill
# Make a prediction for Cyprus
X_new = [[22587]] # Cyprus' GDP per capita  // and maing predictions is kinda side ways
print(model.predict(X_new)) # outputs [[ 5.96242338]]



#import sklearn.neighbors
#model = sklearn.neighbors.KNeighborsRegressor(n_neighbors=3)