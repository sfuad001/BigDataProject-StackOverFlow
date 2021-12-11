import numpy as np
from collections import Counter
from sklearn.preprocessing import normalize
from sklearn.tree import DecisionTreeClassifier
from sklearn import metrics
from sklearn.metrics import confusion_matrix, classification_report


data = open("expert.csv")
data = np.loadtxt(data, delimiter=",", skiprows=1)
print(data.shape)
np.random.shuffle(data)
data, label = data[:, 1:-1], data[:, -1].astype("int")
data = normalize(data, axis=0, norm='max')
trainX = data[:int(0.9*data.shape[0])]
testX = data[int(0.9*data.shape[0]):]
trainY = label[:int(0.9*data.shape[0])] - 1
testY = label[int(0.9*data.shape[0]):] - 1
print("train: ", trainX.shape, trainY.shape)
print("test: ", testX.shape, testY.shape)
print(Counter(trainY))
print(Counter(testY))


tree = DecisionTreeClassifier(criterion='entropy', max_depth=3, random_state=2)
tree.fit(trainX, trainY)
predY = tree.predict(testX)
accuracy = metrics.accuracy_score(testY, predY)
print("Accuracy: {:.2f}".format(accuracy))
cm = confusion_matrix(testY, predY)
print('Confusion Matrix: \n', cm)
print(classification_report(testY, predY))