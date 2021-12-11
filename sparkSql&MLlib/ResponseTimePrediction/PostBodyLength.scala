package edu.ucr.cs.cs226.tmaju002

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col,length}

object PostBodyLength {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")

    df.createOrReplaceTempView("Table")
    var projected = spark.sql("select t._ID as PostID,t._Body as Body from Table t where t._PostTypeId=1")
    projected.show()
    projected=projected.withColumn("BodyLength",length(col("Body")))
    projected=projected.drop("Body")
    projected.show()
    projected.printSchema()
    projected.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path/BodyLength")


  }

}
