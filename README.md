# 项目介绍及使用说明

## 1 介绍

    本项目的目的旨在为spring boot或者spring项目更方便地连接（PLC）Opc UA 服务，并实时监听需要的PLC信号点位，达到实时监控设备状态或者其他硬件相关的信息。支持单独读写某个PLC中的某个信号点位。
    只需要个一个配置文件，并编写一些需要的类，就可以简单实现。

## 2 打包后的jar文件下载

shdq.uaclient-1.0.jar [下载](https://pan.baidu.com/s/1NXEpyI7QvAz6XA8mVX7KGA)
提取码: n92u

shdq.uaclient-2.0.jar [下载](https://pan.baidu.com/s/11bJWqoTDUiiwfYuqdigeQQ)
提取码: 7p4u

*增加连接线程池资源释放功能*  
shdq.uaclient-3.0.jar [下载](https://pan.baidu.com/s/1ei_-Tc9pFL-WF9CSjDGS-A)
提取码: 6ntx

*移除spring retry*  
shdq.uaclient-3.1.jar [下载](https://pan.baidu.com/s/1HKsXEX57GdwbmtUyZWGbWQ)
提取码: hsia

*无相关依赖，包中使用的依赖需要自己导入*
shdq.uaclient-3.2.jar [下载](https://pan.baidu.com/s/1Up0F5L-Y1o_KiT3O4rXx7A)
提取码: gzsu

*解决在项目中依赖此项目，打包后YamlConverter找不到系统路径的问题
shdq.uaclient-3.3.jar [下载](https://pan.baidu.com/s/14qCxaukFe5lFPOZE1OR-QA)
提取码：ifwk


```java
<dependency>
  <groupId>commons-lang</groupId>
  <artifactId>commons-lang</artifactId>
  <version>2.6</version>
</dependency>
<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
</dependency>
<dependency>
  <groupId>com.esotericsoftware.yamlbeans</groupId>
  <artifactId>yamlbeans</artifactId>
  <version>1.13</version>
</dependency>
```

## 3 目录结构及说明

```text
├─lib
│      com.prosysopc.ua-2.2.0.jar 连接opc UA服务依赖包
│      org.opcfoundation.ua-1.2.337.jar
├─src
│  ├─main
│  │  ├─java
│  │  │  └─com
│  │  │      └─opc
│  │  │          └─uaclient
│  │  │              │  DemoApplication.java
│  │  │              ├─listener  所有的监听器
│  │  │              │      Test2Listener.java
│  │  │              │      TestListener.java
│  │  │              │      TransferRfidListener.java
│  │  │              │      WareHouseSidePalletRfidListener.java
│  │  │              ├─opcua
│  │  │              │  ├─core
│  │  │              │  │      ClientCreator.java  客户端创建器
│  │  │              │  │      Connector.java   客户端连接器
│  │  │              │  │      DefaultNodesParser.java  默认节点解析器
│  │  │              │  │      DefaultUaClientListener.java
│  │  │              │  │      ListenerBinder.java  客户端监听器和订阅节点绑定器
│  │  │              │  │      Mode.java  
│  │  │              │  │      NodesParser.java  订阅节点解析器接口，自己实现的节点解析器需要实现次接口
│  │  │              │  │      OpcUaConfiguration1.java  配置入口类1，已经使用@Configuration注解配置好，不需要额外配置了，只要加载了此类就会自动读取配置文件，解析，绑定，连接，订阅
│  │  │              │  │      OpcUaConfiguration2.java  配置入口类2，没有使用spring注解配置，需要把这个配置类配置到spring容器中。功能同上。
│  │  │              │  │      OpcUaProperties.java  properties对象，对应opcua.yml配置文件
│  │  │              │  │      OpcUaTemplate.java  客户端操作模板，提供读，写，订阅方法
│  │  │              │  │      Subscriber.java   节点订阅器，将对应客户端的订阅节点和监听器设置到客户端对象中
│  │  │              │  ├─exception
│  │  │              │  │      OpcUaClientException.java
│  │  │              │  ├─pojo
│  │  │              │  │      ListenerPOJO.java  客户端，监听器，订阅节点关系对象
│  │  │              │  │      Relation.java   保存ListenerPOJO集合对象以及UaClientPOJO集合对象，使用单例模式
│  │  │              │  │      UaClientPOJO.java  客户端及其基本属性关系对象
│  │  │              │  └─util
│  │  │              │          OpcUaUtil.java   工具类
│  │  │              │          YamlConverter.java  将yaml配置文件转换成propetites对象
│  │  │              │          YamlReader.java
│  │  │              ├─parser
│  │  │              │      MyParser.java   自定义解析器
│  │  │              └─web
│  │  │                  └─controller
│  │  │                          OpcUaController.java  测试读写的controller
│  │  └─resources
│  │          application.properties
│  │          opcua.yml    opcua yaml配置文件
```

### 3.1配置文件opcua.yml

**说明**

>配置文件名称固定为opcua.yml，不可更改。路径为src根目录resources目录下。

**解释**

```java
  # 监听器路径
  listenerPath: com.example.demo.listener.
  # 节点解析器
  nodesParser: com.example.demo.parser.MyParser
  # 监听器刷新率（推送响应速率）
  # 每隔publishRate毫秒客户端会给所有订阅的节点发送一次publishRequest请求，对应每一个请求返回一个publishResponse响应。
  # 如果服务端中节点的刷新速率小于publishRate，会发生漏读的现象。
  publishRate: 100
  # spring提供的RetryTemplate模板配置
  # 3.1及3.1后版本不需要配置，已经移除了spring retry
  retry:
    connBackOffPeriod: 10000
    maxAttempts: 3
    backOffPeriod: 1000
  # plc 列表，opcua服务配置
  plcList:
    # opc ua服务地址
    - address: opc.tcp://10.28.213.213:4840
      # namespaceIndex
      ns: 3
      # plc编号，自己定义
      plcNo: 1
      # 安全模式，默认使用none，根据opcua server端配置设定
      securityMode: none
      # 用户认证模式，默认为Anonymous，不用配置用户名和密码。如果配置为username，则需要配置用户名和密码
      userAuthenticationMode: anonymous
      # 连接opc ua服务的用户名
      username:
      # 连接opc ua服务的密码
      password:
      # 如果用户认证模式为certificate，需要配置证书路径等，可以为url或者文件路径+文件名
      certificateFileOrURL:
      privateKeyFileOrURL:
      privateKeyPassword:
      clientListener: com.opc.uaclient.uaclientlistener.MyUaClientListener
      # 会话过期时间
      sessionTimeOut: 1
      # 启动时判断是否要连接到这个plc提供的opc ua服务
      isConnect: true
      # 启动时判断是否要将配置的订阅节点和监听器设置到客户端中做订阅
      isSubscribe: true
      # 配置监听器名称和对应次监听器需要监听的订阅节点的列表
      # 一个客户端连接可以配置多个监听器，一个监听器可以订阅多个节点
      wareHouseSidePalletRfid: NodeBase# "OPCUA"
        - SubsribeNode# "1_PalletCode"
      transferRfid: NodeBase# "OffLineData"
        - SubsribeNode# "22_Data"
    - address: opc.tcp://FJY:53530/OPCUA/SimulationServer
      ns: 5
      plcNo: 2
      securityMode: none
      userAuthenticationMode: username
      username:
      password:
      certificateFileOrURL:
      privateKeyFileOrURL:
      privateKeyPassword:
      clientListener: com.opc.uaclient.uaclientlistener.MyUaClientListener
      sessionTimeOut: 1
      isConnect: true
      isSubscribe: true
      Test: counter,random
      Test2: counter2
```

## 4 使用说明

### 4.1配置

* 引入依赖
  
> 1.使用下载好的jar

```java
maven项目：

  把jar放在lib目录下，在pom文件中引入依赖:

    <dependency>
      <groupId>org.shdq</groupId>
      <artifactId>shdq.uaclient</artifactId>
      <version>1.0</version>
      <scope>system</scope>
      <systemPath>${basedir}/lib/shdq.uaclient-1.0.jar</systemPath>
    </dependency>

  或者把jar包安装到maven本地仓库，再引入依赖：
  
  mvn install:install-file -DgroupId=org.shdq -DartifactId=shdq.uaclient -Dversion=1.0 -Dpackaging=jar -Dfile=shdq.uaclient-1.0.jar

  <dependency>
    <groupId>org.shdq</groupId>
    <artifactId>shdq.uaclient</artifactId>
    <version>1.0</version>
  </dependency>
```

> 2.你也可以下载本项目，把com.opc.uaclient.opcua包下的所有文件都放入到自己的项目中。

* 配置文件

>提供符合上述文件格式并且文件名为opcu.yml文件，且文件路径在src根目录上，或者在resources根目录下。

* spring、spring boot项目
  
> 1.如果要使用配置入口类1，不需要额外配置，应为该类已经使用@Configuration注解配置了。  
> 2.如果使用配置入口类2，需要额外把此类在spring容器中注册为一个bean。  
> 3.编写opcua.yml配置文件，配置监听器和自己定制的节点解析器。  

*打包好的jar中的入口类是没有使用spring注解的，使用时如上2步骤配置即可。*

### 4.2编写监听器

>监听器类名称应为配置文件配置的```监听器名称+Listener```，注意类首字母需大写，配置文件中可以不用首字母大写。并且将监听器类放置在配置文件中```listenerPath```配置的路径下。最重要的一点所有的监听器类都要实现```MonitoredDataItemListener```接口，并实现```onDataChange```方法，在此方法中可以获取对应节点名称，节点的旧值，新值，实现自己的逻辑。

### 4.3自定义节点解析器

>因为对于不同的plc提供的opc ua服务，它们提供的节点格式可能不同，所以对应的解析方式也不同。为了提高程序的扩展性，提供了```NodesParser```接口，实现```getNodesList(String nodes, int plcNo)```方法，根据不同的plc编号实现不同的解析方式。最后需要把自定义的解析器配置到```opcua.yml```配置文件的```nodesParser```中。
