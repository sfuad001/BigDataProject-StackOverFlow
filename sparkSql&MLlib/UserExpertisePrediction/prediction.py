import numpy as np
from collections import Counter
from sklearn.datasets import make_classification
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import RepeatedStratifiedKFold
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import normalize
from matplotlib import pyplot
from sklearn.tree import DecisionTreeClassifier
from sklearn import metrics
from sklearn.metrics import confusion_matrix, classification_report
import pickle

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
filename = "raw_logreg_model6.sav"
model = pickle.load(open(filename, 'rb'))
model.fit(trainX, trainY)
predY = model.predict(testX)
accuracy = metrics.accuracy_score(testY, predY)
print("Accuracy: {:.2f}".format(accuracy))
cm = confusion_matrix(testY, predY)
print('Confusion Matrix: \n', cm)
print(classification_report(testY, predY))