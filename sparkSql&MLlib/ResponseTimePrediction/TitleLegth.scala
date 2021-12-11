package edu.ucr.cs.cs226.jmeem001

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.jute.Index
import org.apache.spark.sql.functions.{col, to_timestamp}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, types}
import org.apache.spark.sql.types.{ArrayType, IntegerType, LongType, StringType, StructType}
import org.apache.spark.sql.functions.{concat, lit}
import org.apache.spark.rdd.RDD
import org.apache.spark.sql.functions.length

import scala.collection.mutable.ListBuffer
//count similar tags
object TitleLegth {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")

    df.createOrReplaceTempView("Similar")
    var new_df=spark.sql("select _Id,_Title from Similar where _Title<>'null'")
    new_df=new_df.withColumn("length",length(col("_Title")))
    new_df.show()
    new_df.printSchema()
    /*import spark.implicits._
    val df4=new_df.select("_Title")

    val titles_rdd= df4.map(f=>f.length.toString())
    //val titles_rdd=spark.sparkContext.parallelize(title_string

    var titles_df = titles_rdd.toDF()
    titles_df.show(4)
    titles_df.printSchema()
*/
/*
    titles_df=titles_df.withColumnRenamed("_1","PostId")
    titles_df=titles_df.withColumnRenamed("_2","TitleLength")
    titles_df=titles_df.withColumn()
    titles_df.printSchema()
    titles_df.show(5)*/

    new_df.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/TitleLength")

    //temp_df.write.csv()
    /*
    tag_pairs_df.createOrReplaceTempView("Similar")
    tag_pairs_df=spark.sql("select s.tagPairs._1,s.tagPairs._2,s.count from Similar s")
    tag_pairs_df.show()
    tag_pairs_df=tag_pairs_df.withColumn("tagPairs",concat(col("_1"),lit(','),
      col("_2")))
    tag_pairs_df=tag_pairs_df.drop("_1")
    tag_pairs_df=tag_pairs_df.drop("_2")
    tag_pairs_df.show()
    tag_pairs_df.printSchema()
     */
    //temp_df=temp_df.withColumn("new",col("tagPairs")("_1")+","+col("tagPairs")("_2"))
    //temp_df.show()
    //joinDF.show()
    //temp_df.write.option("header","false").csv("similar_tags.csv")
    /*
        df.printSchema()
        //df.show(5)
        //Only few columns are proected
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
        joinDF=spark.sql("select p.Question_Id,Min(p.Response_Time) as Min_Response_Time from Post_Response as p group by p.Question_Id order by p.Question_Id ASC")
        joinDF.show()
        //joinDF.createOrReplaceTempView("Post_Response")
        //joinDF=spark.sql("select * from Post_Response as p, Post_Response p +" +
        //  "")


    */
  }
}