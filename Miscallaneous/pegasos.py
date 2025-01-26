from tabnanny import check
import numpy as np

def read(s):
    x=[]
    y=[]
    
    m = int(input("Please enter the size of feature vector: \n"))
    
    while s == "YES" or s == "yes" or s == "Yes"or s == "yEs" or s == "yeS" or s == "YEs" or s == "yES" :
        xi = []
        
        for i in range(m):
            value = int(input("Please enter the property: \n"))
            xi.append(value)
        
        yi = int(input("Please enter the label: \n"))
        y.append(yi)
        
        xi = np.array(xi)
        x.append(xi)
        s = input("Continue?\n")
    
    x = np.array(x)
    y = np.array(y)
    return x,y

def check(x):
    e= 0.00000001;
    while(np.abs(x)>e):
        if(x > 0):
            x = x-e;
        elif(x < 0):
            x = x+e;
    return x;

def pegasos(x,y,T,L):
    m = len(x[0])
    theta = np.zeros(m)
    theta_0 = 0;
    theta_0 = check(theta_0)
    
    ti = 1
    
    n = len(y)
    for t in range(T):
        for i in range(n):
            eta = np.sqrt(ti)/ti
            #print(eta);
            if(y[i] * (int(np.matmul(x[i],theta)) + theta_0)<=1):
                theta = theta*(1-eta * L) + eta * y[i]*x[i];
                theta_0 = theta_0 + eta * y[i];
            else:
                theta = theta * (1 - eta * L)
            ti+=1;
            
    return theta,theta_0

x,y = read("YES")

theta,theta_0 = pegasos(x,y,1000, 0.05)
#print
print(theta)
print(theta_0)
