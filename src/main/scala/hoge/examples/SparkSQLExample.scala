package hoge.examples

import org.apache.spark.sql.Row
import org.apache.spark.sql.SparkSession
import org.apache.spark.sql.types._

class SparkSQLExample {

  case class Person(name: String, age: Long)

  def main(args: Array[String]): Unit = {

    val spark = SparkSession
      .builder()
      .appName("Spark SQL basic example")
      .config("spark.some.config.option", "some-value")
      .getOrCreate()

    // For implicit conversions like converting RDDs to DataFrames
    import spark.implicits._

    runBasicDataFrameExample(spark)
    runDatasetCreationExample(spark)
    runInferSchemaExample(spark)
    runProgrammaticSchemaExample(spark)

    spark.stop()
  }

  private def runBasicDataFrameExample(spark: SparkSession): Unit = {

    val df = spark.read.json("examples/src/main/resources/people.json")

    df.show()
    // +----+-------+
    // | age|   name|
    // +----+-------+
    // |null|Michael|
    // |  30|   Andy|
    // |  19| Justin|
    // +----+-------+

    // This import is needed to use the $-notation
    import spark.implicits._

    df.printSchema()
    // root
    // |-- age: long (nullable = true)
    // |-- name: string (nullable = true)

    df.select("name").show()
    // +-------+
    // |   name|
    // +-------+
    // |Michael|
    // |   Andy|
    // | Justin|
    // +-------+

    df.select($"name", $"age" + 1).show()
    // +-------+---------+
    // |   name|(age + 1)|
    // +-------+---------+
    // |Michael|     null|
    // |   Andy|       31|
    // | Justin|       20|
    // +-------+---------+

    df.filter($"age" > 21).show()
    // +---+----+
    // |age|name|
    // +---+----+
    // | 30|Andy|
    // +---+----+

    df.groupBy("age").count().show()
    // +----+-----+
    // | age|count|
    // +----+-----+
    // |  19|    1|
    // |null|    1|
    // |  30|    1|
    // +----+-----+

    df.createOrReplaceTempView("people")

    val sqlDF = spark.sql("SELECT * FROM people")
    sqlDF.show()
    // +----+-------+
    // | age|   name|
    // +----+-------+
    // |null|Michael|
    // |  30|   Andy|
    // |  19| Justin|
    // +----+-------+

    df.createGlobalTempView("people")

    spark.sql("SELECT * FROM global_temp.people").show()
    // +----+-------+
    // | age|   name|
    // +----+-------+
    // |null|Michael|
    // |  30|   Andy|
    // |  19| Justin|
    // +----+-------+

    spark.newSession().sql("SELECT * FROM global_temp.people").show()
    // +----+-------+
    // | age|   name|
    // +----+-------+
    // |null|Michael|
    // |  30|   Andy|
    // |  19| Justin|
    // +----+-------+
  }

  private def runDatasetCreationExample(spark: SparkSession): Unit = {

    import spark.implicits._

    val caseClassDS = Seq(Person("Andy", 32)).toDS()
    caseClassDS.show()
    // +----+---+
    // |name|age|
    // +----+---+
    // |Andy| 32|
    // +----+---+

    val primitiveDS = Seq(1, 2, 3).toDS()
    primitiveDS.map(_ + 1).collect() // Returns: Array(2, 3, 4)

    val path = "examples/src/main/resources/people.json"
    val peopleDS = spark.read.json(path).as[Person]
    peopleDS.show()
    // +----+-------+
    // | age|   name|
    // +----+-------+
    // |null|Michael|
    // |  30|   Andy|
    // |  19| Justin|
    // +----+-------+
  }

  private def runInferSchemaExample(spark: SparkSession): Unit = {

    import spark.implicits._

    val peopleDF = spark.sparkContext
      .textFile("examples/src/main/resources/people.txt")
      .map(_.split(","))
      .map(attributes => Person(attributes(0), attributes(1).trim.toInt))
      .toDF()

    peopleDF.createOrReplaceTempView("people")

    val teenagersDF =
      spark.sql("SELECT name, age FROM people WHERE age BETWEEN 13 AND 19")

    teenagersDF.map(teenager => "Name: " + teenager(0)).show()
    // +------------+
    // |       value|
    // +------------+
    // |Name: Justin|
    // +------------+

    teenagersDF
      .map(teenager => "Name: " + teenager.getAs[String]("name"))
      .show()
    // +------------+
    // |       value|
    // +------------+
    // |Name: Justin|
    // +------------+

    implicit val mapEncoder =
      org.apache.spark.sql.Encoders.kryo[Map[String, Any]]

    teenagersDF
      .map(teenager => teenager.getValuesMap[Any](List("name", "age")))
      .collect()
    // Array(Map("name" -> "Justin", "age" -> 19))
  }

  private def runProgrammaticSchemaExample(spark: SparkSession): Unit = {

    import spark.implicits._

    val peopleRDD =
      spark.sparkContext.textFile("examples/src/main/resources/people.txt")

    val schemaString = "name age"

    val fields = schemaString
      .split(" ")
      .map(fieldName => StructField(fieldName, StringType, nullable = true))
    val schema = StructType(fields)

    val rowRDD = peopleRDD
      .map(_.split(","))
      .map(attributes => Row(attributes(0), attributes(1).trim))

    val peopleDF = spark.createDataFrame(rowRDD, schema)

    peopleDF.createOrReplaceTempView("people")

    val results = spark.sql("SELECT name FROM people")

    results.map(attributes => "Name: " + attributes(0)).show()
    // +-------------+
    // |        value|
    // +-------------+
    // |Name: Michael|
    // |   Name: Andy|
    // | Name: Justin|
    // +-------------+
  }
}
