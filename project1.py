import numpy as np 
def read(s):
    def read(s):
    x = []
    y = [] 
    
    m = int(input("Write the amount an properties in feature set: \n")) 
    n = 0 
    while s == "YES":
        xi =[]
        for i in range(m): 
            xii = int(input("Please enter property: \n")) 
            xi.append(xii)
        x.append(xi)
        yi = int(input("Please enter the label: \n"))
        y.append(yi)
        n = n+1
        s = input("Continue? \n")
    x = np.array(x) 
    y = np.array(y)
    return x,y,n,m

def hinge_loss_single(feature_vector, label, theta, theta_0):
    l = 1 - label * ( float(np.matmul(feature_vector.transpose(), theta)) + theta_0)
    if l<=0:
        l = 0
    return l

x,y,n,m = read("YES")
theta = np.array([0.4,0])

theta_0 = 0

l = hinge_loss_single(x,y,theta,theta_0)
 

print(l)

##print(l)
