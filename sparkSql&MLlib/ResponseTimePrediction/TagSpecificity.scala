package edu.ucr.cs.cs226.tmaju002

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.functions.{col, to_timestamp}

import scala.collection.mutable.ListBuffer

object TagSpecificity {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[4]")
      .appName("spark-scala")
      .getOrCreate()
    import spark.implicits._
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")
    df.createOrReplaceTempView("Post")
    var DF = spark.sql("select count(*) as totalQuestions from Post p where p._PostTypeId=1")
    DF.show()
    DF.printSchema()
    val c1=DF.collect()
    var total_questions : Long = 0
    c1.take(1).foreach(row=>{
      total_questions=row.getLong(0)
    }
    )
    println(total_questions)

    var df1=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"false"))
      .csv("postIdTagPairs.csv")

    df1.printSchema()
    df1.show(5)
    //Only few columns are proec
    var df2=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("tagPairsNew.csv")
    df2.show()
    df2.printSchema()

    var df3=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("tagPopularity.csv")
    df3.show()
    df3.printSchema()
    val hashMapSingleTag = scala.collection.mutable.HashMap.empty[String,Int]
    val hashMapPairTag = scala.collection.mutable.HashMap.empty[String,Int]

    val col=df3.collect()
    col.foreach(row=>{
      var tag=row.getString(0)
      var count=row.getInt(1)
      hashMapSingleTag+=(tag -> count)
    }
    )
    val col2=df2.collect()
    col2.foreach(row=>{
      var tagPair=row.getString(1)
      var count=row.getInt(0)
      hashMapPairTag+=(tagPair -> count)
    }
    )
    val colData=df1.collect()
    var list1 = new ListBuffer[(Int,Long)]()
    colData.foreach(row=>
    {
      var a=row.getInt(0)
      var b=row.getString(1)
      val tagPairCount=hashMapPairTag(b)
      var tags=b.split(',')
      var tag1_count=hashMapSingleTag(tags(0))
      val tag2_count=hashMapSingleTag(tags(1))
      var togetherness=(tagPairCount*total_questions)/(tag1_count*tag2_count)
      list1+=((a,togetherness))

    })
    println(list1)
    val rdd=spark.sparkContext.parallelize(list1)
    val dfFromRDD1 = rdd.toDF("PostId","TagsSpecificity")
    dfFromRDD1.printSchema()
    dfFromRDD1.show(5)
    dfFromRDD1.createOrReplaceTempView("Table")
    var avgTagSpec = spark.sql("select t.PostID as PostID,avg(t.TagsSpecificity) as TagSpecificity from Table t group by t.PostID")
    avgTagSpec.show()
    avgTagSpec.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path/TagSpecificity")
  }
}
