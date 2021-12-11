package edu.ucr.cs.cs226.tmaju002

import java.io.{File, PrintWriter}

import org.apache.spark.sql.SparkSession
import org.apache.spark.SparkContext
import org.apache.spark.ml.regression.LinearRegression
import org.apache.spark.ml.feature.Interaction
import org.apache.spark.ml.feature.VectorAssembler
import org.apache.spark.ml.feature.Normalizer
import org.apache.spark.sql.functions.col
object RegressionModel {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession
      .builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()

    var df1=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("bodyLength.csv")
    var df2=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("tagSpecificity.csv")
    var df3=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("averagePostPopularity.csv")
    var df4=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("numberPopularTags.csv")
    var df5=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("PostTitleLength.csv")
    var df6=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("ResponseTime.csv")
    df1.show(5)
    df1.printSchema()
    df2.show(5)
    df2.printSchema()
    df3.show(5)
    df3.printSchema()
    df4.show(5)
    df4.printSchema()
    df5.show(5)
    df5.printSchema()

    df1.createTempView("BodyLen")
    df2.createTempView("TagSpec")
    df3.createTempView("AvgPostPop")
    df4.createTempView("NumPopTag")
    df5.createTempView("TitleLen")
    df6.createTempView("ResponseTime")

    var sqL_exp="select b.PostID as PostID,b.BodyLength as BodyLen, t.TagSpecificity as TagSpec, a.AvgPopularity as AvgPop, n.NumPopularTags as NumPopTag, l.length as TitleLen, r.ResponseTime/(24*60) as ResponseTime from BodyLen b,TagSpec t, AvgPostPop a,NumPopTag n, TitleLen l, ResponseTime r where b.PostID=t.PostID and t.PostID=a.PostID and a.PostID=n.PostID and n.PostID=l._ID and l._ID=r.PostID and r.ResponseTime<14400"
    var feature_df=spark.sql(sqL_exp)

    feature_df.show(5)
    val assembler = new VectorAssembler()
      .setInputCols(Array("BodyLen", "TagSpec", "AvgPop", "NumPopTag","TitleLen"))
      .setOutputCol("features").transform(feature_df)
    assembler.show()

    val normalizer = new Normalizer()
      .setInputCol("features")
      .setOutputCol("normFeatures")
      .setP(2.0)
      .transform(assembler)

    normalizer.show()
    val Array(trainingData, testData) = normalizer.randomSplit(Array(0.7, 0.3))
    val pw = new PrintWriter(new File("modelOutput2.txt" ))

    val regParam = List(0.3,0.1,0.01)
    val elasticNetParam = List(0.0,0.3,0.8,1.0)
    for(i <- regParam){
      for(j <- elasticNetParam) {
        println(i + "," + j)
        var lr = new LinearRegression()
          .setLabelCol("ResponseTime")
          .setFeaturesCol("features")
          .setMaxIter(100)
          .setRegParam(i)
          .setElasticNetParam(j)
        var lrModel = lr.fit(trainingData)
        lrModel.transform(testData).select("features", "prediction").show()
        var trainingSummary = lrModel.summary
        pw.println("For RegParam:" + i + ", ElasticNetParam:" + j + " RMSE=" + trainingSummary.rootMeanSquaredError)
      }
    }
    pw.close()
  }
}
