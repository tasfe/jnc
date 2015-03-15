java抓取、java网络爬虫实例项目jnc源码托管在google svn上：


在实际中，一般抓取分为定向抓取和通用抓取，所谓定向就是针对某一个或者某一类网站做特定分析，抓取；比如你的公司是做招聘行业的，那么你就只关心招聘相关的内容，所以抓取的时候就可以只针对51job等网站做特定分析；所谓通用抓取就是看到网页就会抓取，但是它只提取网页的title,keywords,description等内容，因为每个网页的布局都不一样，不可能智能到提取你想要的任何内容。因为我也没什么特定的需求，所以就写了一个简单的通用的java抓取，我给它命名为jnc.
> 下面介绍下jnc的主要特点和流程(本来有张图的，但是放这里太小了，看不清，在程序的docs目录下，下载以后可打开看下)：
1.在一个主机下抓取完它的所有网页后才切换到另外一个主机下抓取，每一个主机下可以指定抓取线程数。比如：我现在正在csdn.net主机下抓取，然后配置了5条抓取线程，在这个主机下抓取的时候，肯定会收集到其他主机的url，比如网易的url，但是我不会立即到刚刚收集的这个网易的URL里去抓取，因为它不是我正在抓取的csdn这个主机下的，我是把它保存在一个待抓取主机队列中，当csdn这个主机下的所有网页都抓取完了，才会从这个待抓取主机队列里获取一个新的主机来继续抓取，这个过程我把它定义为切换主机，这个过程是在`HostTarget`和`UrlScheduler`(URL调度器)来完成，正如这两个类的名字一样，在一个目标主机下抓取（`HostTarget`）,然后找个助手(`UrlScheduler`)来帮我管理这些URL。你可能想同时抓取多个主机，比如同时抓取网易，新浪，csdn，这当然是可以的，你只需要在一个for循环中实例化多`个HostTarget`和`UrlScheduler`
2.抓取回来的网页在Analyzer中进行分析，提取你想要的内容
3.保存提取出的内容，根据自己的需要，比如我这里只是打印看一下，当然也可以保存到数据库或者其他文件系统里面
4.过滤，比如你想收集你初恋女友的信息（哈哈），你就只关心含有她名字的网页，其他的都不处理。`^-^`

需要注意的是，在`UrlScheduler`(URL调度器)中，有四个队列，分别是：

对于主机：
1.新的未抓取的主机列表
2.已经抓取的主机列表（避免重复抓取）

对于当前正在抓取的主机下的url：
1.这个主机下待抓取的URL
2.这个主机下已经抓取的URL（避免重复抓取）

根据上面说的抓取流程，我们可以理解：对于当前正在抓取的主机下的url队列，每当切换主机后，这两个队列就会清空，然后保存切换后的主机下的URL，所以内存会有增有减；但是另外两个队列是一直增大的，内存会一直增加；当程序运行久后内存就有可能溢出，哈哈，这不是问题，这四个队列其实很容易用数据库或者内存服务器比如redis等来替换，只是因为我这里不想和这些东西耦合，只提供一个独立java抓取jar包，所以才这样做。

测试：直接运行test包下的Crawl.java
```

UrlBean host = UrlUtil.getUrlBean("http://www.sina.com.cn/");

UrlScheduler.addNewHost(host);//添加一个主机（在程序的运行中会不断添加新发现的主机）


Config config = new Config(); //抓取的各种配置
config.setAnalyzer(new Analyzer()); //分析器
config.setStorage(new Storage()); //存储器
config.setThreadCount(4); //每个主机下的线程数
config.setTimeOut(3000); //超时时间
config.setUrlScheduler(new UrlScheduler());
Set<String> url_uncontains = new HashSet<String>(); //URL中不能包含的字符
url_uncontains.add("javascript");
url_uncontains.add("\\?");
config.setUrlUnContains(url_uncontains);


HostTarget target = new HostTarget(config);
target.start(); //开始抓取

```

jnc对于一般入门，测试还是有帮助的，因为毕竟只是花了2个星期六和星期天的时间写的，也未进行过任何优化，所以在大牛们面前就实在献丑了，但是在我自己的笔记本电脑上我边看电视边运行抓取，还好，2天1夜连续抓取未间断！后面是我自己关掉了，毕竟我还要睡觉`^-^`