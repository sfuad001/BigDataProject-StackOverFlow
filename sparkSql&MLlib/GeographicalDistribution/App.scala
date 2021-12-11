package edu.ucr.cs.cs226.mlazi003

import org.apache.spark.{SparkConf, SparkContext}

/**
 * @author ${user.name}
 */

/* Location */
import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.jute.Index
import org.apache.spark.sql.functions.{col, to_timestamp}
import org.apache.spark.{SparkConf, SparkContext}
import org.apache.spark.sql.{SparkSession, types}
import org.apache.spark.sql.types.{ArrayType, IntegerType, LongType, StringType, StructType}

import scala.collection.mutable.ListBuffer

object App {

  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()

    val dfPosts = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Posts.xml")

    val dfUsers = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("Users.xml")

    import spark.implicits._
    val dfAccountId=dfUsers.select("_AccountId")
    val dfLocation=dfUsers.select("_Location")
    val dfOwnerUserId=dfPosts.select("_OwnerUserId")

    import spark.implicits._
    val df4=dfUsers.select("_AccountId","_Location")
    val temp= df4.map(f=>f.toString()).collect.toList

    val temp_rdd=spark.sparkContext.parallelize(temp)

    val rdd2= temp_rdd.map(f=>
    {
      val temp_split=f.split(",")
      val UserID = temp_split(0).substring(1,temp_split(0).length)
      var x= temp_split(temp_split.length-1)
      val Location = x.substring(0,x.length-1)

      (UserID, Location)
    })

    var location_user = rdd2.toDF()

    location_user=location_user.withColumnRenamed("_1","_AccountId")
    location_user=location_user.withColumnRenamed("_2","_Location")

    location_user.createOrReplaceTempView("User_Location")
    location_user = spark.sql("select _AccountId as _AccountId, _Location as _Location from User_Location where _Location<>'null'").persist()


    location_user.createOrReplaceTempView("Location_User")
    dfPosts.createOrReplaceTempView("Posts")
    var sql_text="select L._Location as Location, count(*) as count from Location_User L, Posts P where P._OwnerUserId = L._AccountId group by L._Location order by count desc"
    var final_df=spark.sql(sql_text).persist()


    final_df.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("output/path")
    
    
  }
}