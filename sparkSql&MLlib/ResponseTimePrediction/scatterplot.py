import numpy as np
from matplotlib import pyplot as plt

data = open('modelFeaturesReg.csv')
data = np.loadtxt(data, delimiter=',', skiprows=1)
bodyLen = data[:, 1]
tagSpec = data[:, 2]
avgPop = data[:, 3]
numPopTag = data[:, 4]
titleLen = data[:, 5]
resTime = data[:, -1]*24*60


plt.scatter(resTime, avgPop, c='blue')
plt.xlabel("Response time")
plt.ylabel("Average Popularity of Tags")
plt.savefig("avgPop_vs_time.png")