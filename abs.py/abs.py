import numpy as np

x = 0.0000;
e = 0.00000001
def check(x):
    e= 0.00000001;
    while(np.abs(x)>e):
        if(x > 0):
            x = x-e;
        elif(x < 0):
            x = x+e;
    return x;
print(x)