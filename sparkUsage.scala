import org.apache.hadoop.hbase.client.HBaseAdmin 
import org.apache.hadoop.hbase.{HBaseConfiguration, HTableDescriptor, TableName} 
import org.apache.hadoop.hbase.mapreduce.TableInputFormat
import org.apache.hadoop.hbase.mapred.TableOutputFormat//不要搞错，与上行不同！！
import org.apache.spark._ 
import org.apache.hadoop.hbase.client.ConnectionFactory
import org.apache.hadoop.hbase.client.{Put,Get,Delete}
import org.apache.hadoop.hbase.io.ImmutableBytesWritable
import org.apache.hadoop.mapred.JobConf
import org.apache.hadoop.hbase.util.Bytes
 
val conf = HBaseConfiguration.create() 
conf.set(TableInputFormat.INPUT_TABLE, "ttt") 
conf.set("hbase.zookeeper.property.clientPort", "2181")
conf.set("hbase.zookeeper.quorum", "ngn91")

	//遍历输出
    val hBaseRDD = sc.newAPIHadoopRDD(conf, classOf[TableInputFormat], 
      classOf[org.apache.hadoop.hbase.io.ImmutableBytesWritable], 
      classOf[org.apache.hadoop.hbase.client.Result]) 
    hBaseRDD.count() 
	hBaseRDD.foreach{ case (_,result) =>  val key = Bytes.toString(result.getRow);  val name = Bytes.toString(result.getValue("cff".getBytes,"x".getBytes)) ;println("Row key:"+key+" Name:"+name)}
	
	//看有没有要查的表
	val admin = new HBaseAdmin(conf) 
    if (!admin.isTableAvailable(args(0))) { 
      val tableDesc = new HTableDescriptor(TableName.valueOf(args(0))) 
      admin.createTable(tableDesc) 
    } 	
	
val conn = ConnectionFactory.createConnection(conf) 
val tb = conn.getTable(TableName.valueOf("ttt"))
try{
	//增加某行
    val p = new Put("row8".getBytes)
    p.addColumn("cff".getBytes,"x".getBytes, "wuchong".getBytes)//cff x为已有的列族和列
    tb.put(p)
	//查询某行
	val g = new Get("row3".getBytes)
    val result = tb.get(g)
	val va = org.apache.hadoop.hbase.util.Bytes.toString(r.getValue("cff".getBytes,"x".getBytes))
    println("GET row3 :"+va)
	
	//扫描列
    val s = new Scan()
    s.addColumn("cff".getBytes,"x".getBytes)
    val scanner = table.getScanner(s)
    try{
      for(r <- scanner){	// ！scaner无foreach操作
        println("Found row: "+r)
        println("Found value: "+Bytes.toString(
          r.getValue("cff".getBytes,"x".getBytes)))
      }
    }finally {   scanner.close()   }
	
	//删除某行
	val d = new Delete("row3".getBytes)
    d.addColumn("cff".getBytes,"x".getBytes)
    tb.delete(d)
	
	}
finally{ if(tb != null) tb.close() }
finally{ conn.close()              }


// RDD 写入 HBase
// 指定输出格式和输出表名
val jobConf = new JobConf(conf,this.getClass)
jobConf.setOutputFormat(classOf[TableOutputFormat])
jobConf.set(TableOutputFormat.OUTPUT_TABLE,"ttt")
// RDD 变成 Result
def convert(triple: (Int, String, Int)) = {
      val p = new Put(Bytes.toBytes(triple._1))
      p.addColumn(Bytes.toBytes("cff"),Bytes.toBytes("x"),Bytes.toBytes(triple._2))
      p.addColumn(Bytes.toBytes("cff"),Bytes.toBytes("a"),Bytes.toBytes(triple._3))
      (new ImmutableBytesWritable, p)
}

val rawData = List((1,"lilei",14), (2,"hanmei",18), (3,"someone",38))
val localData = sc.parallelize(rawData).map(convert)
//写入HBase
localData.saveAsHadoopDataset(jobConf)
