package edu.ucr.cs.cs226.tmaju002

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.col

import scala.collection.mutable.ListBuffer

object TagCount
{
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
        i <- 0 until list.length
      } yield {
        val x=list(i)
        (x,1)
      }
    })

    //r2.foreach(println)
    val r2=rdd2.reduceByKey((x,y)=>x+y)

    //r2.foreach(println)
    var countTags_df=r2.toDF()
    countTags_df=countTags_df.withColumnRenamed("_1","tagName")
    countTags_df=countTags_df.withColumnRenamed("_2","count")

    countTags_df.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/TagsPopularity)

  }

}
