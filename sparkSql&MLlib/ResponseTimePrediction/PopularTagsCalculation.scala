package edu.ucr.cs.cs226.tmaju002

import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.SparkSession

import scala.collection.mutable.ListBuffer

object PopularTagsCalculation {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[4]")
      .appName("spark-scala")
      .getOrCreate()
    import spark.implicits._
    val df = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")
    df.printSchema()
   // df.where("_TagName=='python'").show()
    val df2=df.select("_Id","_Tags")
    val tags_string= df2.map(f=>f.toString()).collect.toList
    val tags_rdd=spark.sparkContext.parallelize(tags_string)
    val rdd2= tags_rdd.flatMap(f=>
    {
      val tags_split=f.split(",")
      val Id = tags_split(0).substring(1,tags_split(0).length)
      var tags = tags_split(1).split("<")
      var list = new ListBuffer[String]()
      for(i<-1 until tags.length){
        val a=tags(i).split(">")

        list+= a(0)
      }
      for (i <- 0 until list.length) yield {
        val x = list(i)

        (Id,x)
      }
    })
    var tag_df = rdd2.toDF()
    tag_df=tag_df.withColumnRenamed("_1","PostId")
    tag_df=tag_df.withColumnRenamed("_2","Tag")
    tag_df.printSchema()
    tag_df.show()
    var df3=spark.read.options(Map("inferSchema"->"true","delimiter"->",","header"->"true"))
      .csv("tagPopularity.csv")
    df3.show()
    df3.printSchema()
    val hashMapSingleTag = scala.collection.mutable.HashMap.empty[String,Int]
    val col=df3.collect()
    col.foreach(row=>{
      var tag=row.getString(0)
      var count=row.getInt(1)
      hashMapSingleTag+=(tag -> count)
    }
    )
    val colData=tag_df.collect()
    var list1 = new ListBuffer[(String,Int)]()
    var list2 = new ListBuffer[(String,Int)]()

    colData.foreach(row=>
    {
      var a=row.getString(0)
      var b=row.getString(1)
      list1+=((a,hashMapSingleTag(b)))
      if(hashMapSingleTag(b)>50){
        list2+=((a,1))
      }
    })

    var rdd=spark.sparkContext.parallelize(list1)
    var dfFromRDD1 = rdd.toDF("PostId","Tags")
    dfFromRDD1.printSchema()
    dfFromRDD1.show(10)
    dfFromRDD1.createOrReplaceTempView("Table")
    var avgTagSpec = spark.sql("select t.PostID as PostID,avg(t.Tags) as AvgPopularity from Table t group by t.PostID")
    avgTagSpec.show()
    avgTagSpec.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path/AveragePostPopularity")

    rdd=spark.sparkContext.parallelize(list2)
    dfFromRDD1 = rdd.toDF("PostId","PopularTags")
    dfFromRDD1.printSchema()
    dfFromRDD1.show(10)
    dfFromRDD1.createOrReplaceTempView("Table")
    val numPopularTags = spark.sql("select t.PostID as PostID,sum(t.PopularTags) as NumPopularTags from Table t group by t.PostID")
    numPopularTags.show()
    numPopularTags.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path/NumberPopularTags")

  }

}
