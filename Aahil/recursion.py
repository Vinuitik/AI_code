import time

fibonacciDict = {}

def fibonacci1(n:int):
    # 1) base case 
    # 2) return a value
    # 3) call itself at least once
    if(n<1):
        return 0
    if(n<3):
        return 1
    if(fibonacciDict.get(n)!=None):
        return fibonacciDict.get(n)
        
    leftValue = fibonacci1(n-1)
    rightValue = fibonacci1(n-2)
    
    fibonacciDict[n] = leftValue+rightValue
    
    return fibonacciDict.get(n)

def fibonacci2(n:int):
    # 1) base case 
    # 2) return a value
    # 3) call itself at least once
    if(n<1):
        return 0
    if(n<3):
        return 1
        
    leftValue = fibonacci2(n-1)
    rightValue = fibonacci2(n-2)
    
    return leftValue+rightValue

start = time.time()
print(fibonacci1(100))
end = time.time()
print("Time taken for fibonacci1:", end - start)

start = time.time()
print(fibonacci2(25))
end = time.time()
print("Time taken for fibonacci1:", end - start)