from scipy.cluster.hierarchy import dendrogram, linkage, cut_tree
from matplotlib import pyplot as plt
import numpy as np

file = open("tagPairsNewSorted.csv", 'r')
line = file.readline().strip()
i, j = 0, 0
data = []
label = []
while i < 50:
    line = file.readline().strip().split(',')
    u, v = line[1][1:], line[2][:-1]
    m, n = 0, 0
    if u not in label:
        label.append(u)
        m = j
        j += 1
    if v not in label:
        label.append(v)
        n = j
        j += 1
    m, n = label.index(u), label.index(v)
    data.append(((m, n), float(line[0])))
    i += 1
print(data)
print(len(label))
print(label)
dist = np.zeros((len(label), len(label)))
for items in data:
    dist[items[0][0]][items[0][1]] = items[1]
    dist[items[0][1]][items[0][0]] = items[1]
maxVal = np.max(dist)
print(maxVal)
for i in range(len(label)):
    dist[i][i] = maxVal + 100.0
dist = 1.0 - dist/np.max(dist)
Z = linkage(dist, 'average')
clusterCount = 20
print(cut_tree(Z, n_clusters=clusterCount))
clusters = [[] for i in range(clusterCount)]
i = 0
for tag in cut_tree(Z, n_clusters=clusterCount):
    clusters[tag[0]].append(label[i])
    i += 1
print(clusters)
fig = plt.figure()
dn = dendrogram(Z, orientation='right', labels=label)
plt.tight_layout()
#plt.show()
plt.savefig('cluster20_50x50_51_average.png')