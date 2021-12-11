package edu.ucr.cs.cs226.tmaju002

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, to_timestamp}
import org.apache.spark.sql.types.LongType

object responseTime {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")
    df.printSchema()

    val projected_df=df.select("_Id","_PostTypeId","_ParentId","_OwnerUserId","_CreationDate","_Tags","_Title","_ViewCount")
    projected_df.show()
    projected_df.select("_Tags").show()

    //Changed the date_time string to actual spark datetime type
    val df2=projected_df.withColumn("_CreationDate", to_timestamp(col("_CreationDate")))
    df2.show(5)
    df2.printSchema()

    //Run Sql query on a DF
    df2.createOrReplaceTempView("Post")
    var joinDF = spark.sql("select q._Id as Question_Id, q._CreationDate as Question_Time,p._Id as Answer_Id,p._CreationDate as Answer_Time from Post p,Post q where p._ParentId=q._Id and p._PostTypeId=2")
    joinDF.show()
    //joinDF.printSchema()

    joinDF=joinDF.withColumn("Response_Time", (col("Answer_Time").cast(LongType)-col("Question_Time").cast(LongType))/60)
    joinDF.show()
    joinDF.createOrReplaceTempView("Post_Response")
    joinDF=spark.sql("select p.Question_Id as PostID,Min(p.Response_Time) as ResponseTime from Post_Response as p group by p.Question_Id order by p.Question_Id ASC")
    joinDF.show()
    joinDF.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path/ResponseTime")
  }
}
