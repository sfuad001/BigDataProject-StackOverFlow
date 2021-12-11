package edu.ucr.cs.cs226.jmeem001
//package edu.ucr.cs.cs226.tmaju002
import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.jute.Index
import org.apache.spark.sql.functions.{coalesce, col, date_format, to_date, to_timestamp}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, types}
import org.apache.spark.sql.types.{ArrayType, IntegerType, LongType, StringType, StructType}

import scala.collection.mutable.ListBuffer
object Trends {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts_2.xml")

    val projected_df=df.select("_Id","_PostTypeId","_OwnerUserId","_CreationDate","_Tags")
    val df2=projected_df.withColumn("_CreationDate", to_date(col("_CreationDate")))
    val PostsDate = df2.withColumn("_CreationDate", date_format(col("_CreationDate"),"yyyy"))

   // PostsDate.show(5)
    //PostsDate.printSchema()

    //val x= 2014
    for(k<-2020 to 2021){
      var year =k.toString()
      PostsDate.createOrReplaceTempView("Trends_"+k)
      var tags=spark.sql("select P._Id as _Id, P._CreationDate as _CreationDate, P._Tags as _Tags from Trends_"+k+" as P where P._CreationDate="+year+" and P._PostTypeId=1")
     // tags_2014.show(5)

      import spark.implicits._
      var df4=tags.select("_Tags")
      var tags_string= df4.map(f=> f.toString()).collect.toList
      var tags_rdd=spark.sparkContext.parallelize(tags_string)
      case class Index(x:String)
      case class Val(v:String)
      var rdd2= tags_rdd.flatMap(f=>
      {
        val tags_split=f.split("<")
        var list = new ListBuffer[String]()
        for(i<-1 until tags_split.length){
          val a=tags_split(i).split(">")

          list+= a(0)
        }
        for {
          i <- 0 until list.length
        } yield {
          val x = list(i)

          ((x), 1)
        }
      })

      val r2=rdd2.reduceByKey((x,y)=>x+y)

      //r2.foreach(println)
      var countTags_df=r2.toDF()
      countTags_df=countTags_df.withColumnRenamed("_1","tagName")
      countTags_df=countTags_df.withColumnRenamed("_2","count")
      countTags_df.createOrReplaceTempView("TrendsOrdered_")
      countTags_df=spark.sql("select tagName as tagName, count as count from TrendsOrdered_ order by count desc")


      //countTags_df.printSchema()
     // countTags_df.show(5)

      countTags_df.coalesce(1)
        .write
        .option("header","true")
        .option("sep",",")
        .mode("overwrite")
        .csv("output/pathtrendsYear"+k)

    }

  }
}
