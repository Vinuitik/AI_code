
# Online Python - IDE, Editor, Compiler, Interpreter

matrix = []

n = int(input("Please enter height\n"))
m = int(input("Please enter width\n"))

def getDictionary(matrix):
    # some operation
    result = {}
    
    for i in range(len(matrix)):
        sum_row = sum(matrix[i])
        result[i+1] = sum_row
    
    return result

for i in range(n):
    row = []
    for j in range(m):
        value = float(input("Please enter a value\n"))
        row.append(value)
    matrix.append(row)
dict = getDictionary(matrix)
print(dict)