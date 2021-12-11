package edu.ucr.cs.cs226.jmeem001

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.jute.Index
import org.apache.spark.sql.functions.{col, to_timestamp}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, types}
import org.apache.spark.sql.types.{ArrayType, IntegerType, LongType, StringType, StructType}
import org.apache.spark.sql.functions.{concat, lit}

import scala.collection.mutable.ListBuffer
//count similar tags
object TagPairs {

  def main(args: Array[String]): Unit = {

    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts_2.xml")

    import spark.implicits._
    val df4=df.select("_Tags")
    val tags_string= df4.map(f=>f.toString()).collect.toList
    val tags_rdd=spark.sparkContext.parallelize(tags_string)
    //case class Index(x:String, y:String)
    //case class Val(v:Integer)
    val rdd2= tags_rdd.flatMap(f=>
    {
      val tags_split=f.split("<")
      var list = new ListBuffer[String]()
      for(i<-1 until tags_split.length){
        val a=tags_split(i).split(">")

        list+= a(0)
      }
      for {
        i <- 0 until list.length-1
        j <- (i + 1) until list.length
      } yield {
        val x = list(i)
        val y = list(j)

        ((x,y), 1)
      }
    })
    val r2=rdd2.reduceByKey((x,y)=>x+y)
    r2.foreach(println)
    var tag_pairs_df = r2.toDF()
    tag_pairs_df=tag_pairs_df.withColumnRenamed("_1","tagPairs")
    tag_pairs_df=tag_pairs_df.withColumnRenamed("_2","count")

    tag_pairs_df=tag_pairs_df.withColumn("pairs",concat(col("tagPairs._1"),lit(','),
      col("tagPairs._2")))
    tag_pairs_df=tag_pairs_df.drop("tagPairs")

    tag_pairs_df.printSchema()
    //tag_pairs_df.show()

    tag_pairs_df.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/tagPairsNew")

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