package com.mouse.ExactlyOnce

import org.apache.kafka.clients.consumer.ConsumerRecord
import org.apache.spark.rdd.RDD

/**
  * @author 咖啡不加糖
  *
  */
object GetLogNew {
  case class MyRecord(hour: String, user_id: String, site_id: String)

  def processLogs(messages: RDD[ConsumerRecord[String, String]]) : Array[MyRecord] = {
    messages.map(_.value()).flatMap(parseLog).collect()
  }

  //解析每条日志，生成MyRecord
  def parseLog(line: String): Option[MyRecord] = {
    val ary : Array[String] = line.split(",");
    try {
      val hour = ary(0)
      val user_id = ary(1)
      val site_id = ary(2)
      return Some(MyRecord(hour,user_id,site_id))
    } catch {
      case ex : Exception => println(ex.getMessage)
    }
    return None
  }
}
