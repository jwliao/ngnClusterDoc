# ngnClusterDoc

欢迎进入本文档！

本文档是对清华电子系ngn实验室Hadoop集群的介绍、配置、使用说明、使用案例等的集合，如果您需要使用ngn实验室的服务器计算资源，请从这里开始。

概要介绍：
集群现有节点ngn66,ngn68,ngn69,ngn84,ngn85,ngn72,ngn73,ngn74,ngn76,ngn80,ngn82,ngn91等12台服务器。全部使用hadoop16的用户操作（密码hd@104），并且两两之间都配置了ssh无密码登陆。因任务量关系和动态添加节点功能容易实现，当前并没有在全部服务器上启动服务，需要的时候可以动态添加。

集群现部署有ZooKeeper,HDFS,YARN,HBase,Spark服务。

其中ZooKeeper集群由ngn66,ngn84,ngn74三台服务器组成，任务是为HBase和Spark提供数据备份和容错支持，使得HBase或Spark在使用过程中主服务荡掉后，备份服务能够立刻从ZooKeeper读取使用状态信息，接替已牺牲服务成为新的主服务，保证工作不间断。而使用三个节点组成ZooKeeper集群的目的，也是为了使得提供服务的ZooKeeper节点荡掉后，其他节点能够顶替上去，继续为HBase和Spark提供备份和容错服务。

其中HDFS和YARN服务为Hadoop的两个组成部分，为集群提供基础的分布式文件存储和资源调度服务。
分布式文件存储HDFS的主进程设置在ngn66，用户在程序中可以通过引用如“sc.textFile("hdfs://ngn66:9000/user/input/mydata.txt")”进行访问。测试时如果需要浏览文件可以通过WedUI(http://203.91.121.66:50070)进行。如果需要上传数据，可以在ngn66服务器用户目录Hadoop文件夹下使用如“bin/hdfs dfs -put ./xx.data /user/hadoop16/xx.data”的命令实现。
YARN服务主节点配置为ngn84,实现对所有服务器硬件资源（CPU、内存）的统一管理。用户可将运行任务提交到YARN，然后YARN会自动分配资源给任务，进行计算。查看集群资源详细情况可通过WebUI（http://203.91.121.84：8088）进行。

HBase是分布式NoSQL数据库，采用基于列簇的Key-Value存储模式，具有可扩展性强的优点。主节点配置在ngn66。用户在程序中可通过引入HBase相关jar包，调用API接口方式使用。在测试时可通过WebUI(http://203.91.121.66:60010)监测HBase集群工作状态。

Spark是基于MapReduce原理的最新一代分布式计算引擎。主服务配置在ngn72节点，通过7077提供服务，用户可通过WebUI（http://203.91.121.72:8080)查看集群工作情况，在任务运行时可通过WebUI（http://203.91.121.72:4040)查看任务实时运行情况。

集群时间同步NTP服务器配置在ngn66，其他节点时间与ngn66保持同步。
