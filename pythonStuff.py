from array import array
from cmath import pi
from time import thread_time
import numpy as np


def Perceptron_with_offset(x,y,T, index=0 ): # T = 1000
    k = max
    mistakes = 0
    n = len(y)
    m = len(x[0])
    #print(m)
    theta = np.zeros([1,m])
    offset=0
    for i in range(index, n):
        if ( y[i] * (int(np.matmul(x[i], theta.transpose())) + offset) <= 0 ):
                print("")
                print(theta, i+1)
                theta = theta + y[i]*x[i]
                offset = offset + y[i]
                print(theta)
                print("")

    j=0
    for j in range(T-1):
        for i in range(n):
            if ( y[i] * (int(np.matmul(x[i], theta.transpose())) + offset) <= 0):
                print("")
                print(theta, i+1)
                theta = theta + y[i]*x[i]
                offset = offset + y[i]
                print(theta)
                print("")

    return theta,offset 

def Perceptron_without_offset(x,y,T, index=0 ): # T = 1000
    k = max
    mistakes = 0
    n = len(y)
    m = len(x[0])
    #print(m)
    theta = np.zeros([1,m])
    for i in range(index, n):
        if ( y[i] * (int(np.matmul(x[i], theta.transpose())) ) <= 0 ):
                print("")
                print(theta, i+1)
                theta = theta + y[i]*x[i]
                print(theta)
                print("")

    j=0
    for j in range(T-1):
        for i in range(n):
            if ( y[i] * (int(np.matmul(x[i], theta.transpose()))) <= 0):
                print("")
                print(theta, i+1)
                theta = theta + y[i]*x[i]
                print(theta)
                print("")

    return theta 

def Perceptron(x,y,T):
    n = len(y)
    m = len(x[0])
    theta = np.zeros([1,m]) ## Make sure you initialize theta to a 1D array of shape (n,) and not a 2D array of shape (1, n).
    offset = 0
    for j in range(T):
        for i in range(n):
            if( (y[i] * (int(np.matmul(x[i], theta.transpose())) + offset)) <= 0):
                theta = theta + y[i]*x[i]
                offset = offset + y[i]
    return theta, offset

def Pegasos(label, feature_vector, theta, theta_0):
    if( label * (np.matmul(feature_vector, theta) + theta_0) <= 0):
        theta = theta + label*feature_vector
        theta_0 = theta_0 + label
            

def get_example(n):
    xi = []
    yi = 0
    for i in range (n):
        a = int(input("Please enter the property: \n"))
        xi.append(a)
    yi = int(input("What is the answer to this?\n"))
    return xi,yi

def read(s):
    x = []
    y = []
    ##n = int(input("What is the number of examples?:\n"))  We will ount by ourselves
    m = int(input("What is the amount of properties?:\n"))
    n = 0
    while s == "YES":
        xi,yi = get_example(m)
        x.append(xi)
        y.append(yi)
        n = n+1
        s = input("Continue? \n")
    return x,y,n
        

def fill(d):
    x = []
    for t in range (d):
        xt = []
        for i in range (d):
            p = np.cos(pi * (t+1) ) * (t == i)
            xt.append(p)
        x.append(xt)
    return x

x,y,n = read("YES")
x = np.array(x)


y = np.array(y)

theta,offset = Perceptron_with_offset(x,y,1000)
print(theta,offset)

