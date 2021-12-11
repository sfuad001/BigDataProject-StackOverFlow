import numpy as np
from collections import Counter
from sklearn.model_selection import cross_val_score
from sklearn.model_selection import RepeatedStratifiedKFold
from sklearn.linear_model import LogisticRegression
from sklearn.preprocessing import normalize
from matplotlib import pyplot
import pickle

# get a list of models to evaluate
def get_models():
	models = dict()
	for p in [0.0, 0.0001, 0.001, 0.01, 0.1, 1.0]:
		# create name for model
		key = '%.4f' % p
		# turn off penalty in some cases
		if p == 0.0:
			# no penalty in this case
			models[key] = LogisticRegression(multi_class='multinomial', solver='lbfgs', penalty='none')
		else:
			models[key] = LogisticRegression(multi_class='multinomial', solver='lbfgs', penalty='l2', C=p)
	return models


# evaluate a give model using cross-validation
def evaluate_model(model, X, y):
	# define the evaluation procedure
	cv = RepeatedStratifiedKFold(n_splits=10, n_repeats=3, random_state=1)
	# evaluate the model
	scores = cross_val_score(model, X, y, scoring='accuracy', cv=cv, n_jobs=-1)
	return scores


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

# get the models to evaluate
models = get_models()
# evaluate the models and store results
results, names = list(), list()
i = 1
for name, model in models.items():
	# evaluate the model and collect the scores
	scores = evaluate_model(model, trainX, trainY)
	pickle.dump(model, open('raw_logreg_model'+str(i)+'.sav', 'wb'))
	# store the results
	results.append(scores)
	names.append(name)
	i += 1
	# summarize progress along the way
	print('>%s %.3f (%.3f)' % (name, np.mean(scores), np.std(scores)))
# plot model performance for comparison
pyplot.boxplot(results, labels=names, showmeans=True)
#pyplot.show()
pyplot.savefig("nonnorm_logreg.png")