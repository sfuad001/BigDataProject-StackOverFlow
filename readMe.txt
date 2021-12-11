#UserExpertise.scala
- Takes Users.xml and Badges.xml as inputs
- Selects relevant features from both files and join on userID
- Writes the output to expertiseData.csv file

#decisionTree.py
- Takes expertiseData.csv as input
- Loads data and normalize them
- Splits into train and test
- Fits model
- Writes the trained model in a file

#LogisticRegression.py
- Takes expertiseData.csv as input
- Loads data and normalize them
- Splits into train and test
- Fits model using K-fold cross validation
- Writes the trained model in a file
- Produces the boxplots for cross-validation models

#Prediction.py
- loads a pre-trained model
- generates the complete performance report on test data

#cluster.py
- takes tagPairsNew.csv as input
- converts it into the distance matrix
- generates the hierarchical clustering
- produces the plot

#usStates.py
- from location_vs_count csv file filters the rows for the US states
- writes them to another csv

#state_choropleth.py
- takes geojson for US states and post count vs states file as input
- plots the choropleth for US states

#scatterplot.py
- generates the scatter plots for 5 different features used in predicting response time vs the response time

************ Geographical distribution of Posts **************
This folder contains the scala file for counting number of posts for differnt geographical locations from Users.xml and Post.xml and python code for creating choropleth map of post distribution across 50 states of the USA.
#App.scala
   Input: Users.xml, Posts.xml
   Output: Location and Post count for each location 
           
