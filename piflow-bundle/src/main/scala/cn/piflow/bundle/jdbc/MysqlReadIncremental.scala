package cn.piflow.bundle.jdbc

import cn.piflow.{JobContext, JobInputStream, JobOutputStream, ProcessContext}
import cn.piflow.conf.{ConfigurableIncrementalStop, Port, StopGroup}
import cn.piflow.conf.bean.PropertyDescriptor
import cn.piflow.conf.util.{ImageUtil, MapUtil}
import org.apache.spark.sql.SparkSession

/**
  * Created by xjzhu@cnic.cn on 7/15/19
  */
class MysqlReadIncremental extends ConfigurableIncrementalStop{
  override var incrementalField: String = _
  override var incrementalStart: String = _
  override val authorEmail: String = "xjzhu@cnic.cn"
  override val description: String = "Read data from jdbc database"
  override val inportList: List[String] = List(Port.NonePort)
  override val outportList: List[String] = List(Port.DefaultPort)

  var url:String = _
  var user:String = _
  var password:String = _
  var sql:String = _

  override def perform(in: JobInputStream, out: JobOutputStream, pec: JobContext): Unit = {
    val spark = pec.get[SparkSession]()
    val dbtable = "( "  + sql + ") AS Temp"
    val jdbcDF = spark.read.format("jdbc")
      .option("url", url)
      .option("driver", "com.mysql.jdbc.Driver")
      .option("dbtable", dbtable)
      .option("user", user)
      .option("password",password)
      .load()

    out.write(jdbcDF)
  }
  override def setProperties(map: Map[String, Any]): Unit = {
    url = MapUtil.get(map,"url").asInstanceOf[String]
    user = MapUtil.get(map,"user").asInstanceOf[String]
    password = MapUtil.get(map,"password").asInstanceOf[String]
    sql = MapUtil.get(map,"sql").asInstanceOf[String]
    incrementalField = MapUtil.get(map,"incrementalField").asInstanceOf[String]
    incrementalStart = MapUtil.get(map,"incrementalStart").asInstanceOf[String]

  }

  override def getPropertyDescriptor(): List[PropertyDescriptor] = {
    var descriptor : List[PropertyDescriptor] = List()

    val url=new PropertyDescriptor()
      .name("url")
      .displayName("Url")
      .description("The Url, for example jdbc:mysql://127.0.0.1/dbname")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = url :: descriptor

    val user=new PropertyDescriptor()
      .name("user")
      .displayName("User")
      .description("The user name of database")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = user :: descriptor

    val password=new PropertyDescriptor()
      .name("password")
      .displayName("Password")
      .description("The password of database")
      .defaultValue("")
      .required(true)
      .example("")
      .sensitive(true)
    descriptor = password :: descriptor

    val sql=new PropertyDescriptor()
      .name("sql")
      .displayName("Sql")
      .description("The sql sentence you want to execute")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = sql :: descriptor

    val incrementalField=new PropertyDescriptor()
      .name("incrementalField")
      .displayName("IncrementalField")
      .description("The incremental field")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = incrementalField :: descriptor

    val incrementalStart=new PropertyDescriptor()
      .name("incrementalStart")
      .displayName("IncrementalStart")
      .description("The incremental start value")
      .defaultValue("")
      .required(true)
      .example("")
    descriptor = incrementalStart :: descriptor

    descriptor
  }

  override def getIcon(): Array[Byte] = {
    ImageUtil.getImage("icon/jdbc/jdbcRead.png")
  }

  override def getGroup(): List[String] = {
    List(StopGroup.JdbcGroup)
  }

  override def initialize(ctx: ProcessContext): Unit = {}


}
