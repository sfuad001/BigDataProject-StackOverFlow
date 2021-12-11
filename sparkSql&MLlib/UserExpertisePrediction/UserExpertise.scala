package edu.ucr.cs.cs226.schak026
import com.databricks.spark.xml.XmlDataFrameReader
import org.apache.spark.sql.{SparkSession, types}

object UserExpertise {
  def main(args: Array[String]): Unit = {
    val spark = SparkSession.builder().master("local[*]")
      .appName("spark-scala")
      .getOrCreate()
    val df1 = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("data/Badges.xml")
    val df2 = spark.read
      .format("com.databricks.spark.xml")
      .option("rowTag", "row")
      .xml("data/Users.xml")
    df1.printSchema()
    df2.printSchema()
    val projected_df1 = df1.select("_UserId", "_Class")
    projected_df1.show()
    projected_df1.createOrReplaceTempView("Badges")
    val projected_df2 = df2.select("_AccountId","_Reputation","_Views","_UpVotes","_DownVotes")
    projected_df2.show()
    projected_df2.createOrReplaceTempView("Users")
    var joinDF = spark.sql("select distinct(b._UserID), _Reputation, _Views, _UpVotes, _DownVotes, _Class from Users u,Badges b where u._AccountId=b._UserId")
    joinDF.show()

    joinDF.coalesce(1)
      .write
      .option("header","true")
      .option("sep",",")
      .mode("overwrite")
      .csv("data/output/expertise")
  }
}
