# Search-Engine-Project
## 打分规则
1、目前初步的打分的规则为：
2、pageRank, 将pageRank的权重作为每个document的权重，在建立索引时设立。此处暂时对PageRank值开更号，以减少数量级的差值。
不同域的权重，改变field的setBoost, 可以实现对tf的加权，在建立filed的值设立
   目前field的内容如下：
    title - analyse, 60, 10
    keywords - analyse, 40 1/2
    h1 - analyse, 14 1/3
    h2 - analyse, 12 1/4
    h3 - analyse, 10 1/5               
    h4 - analyse, 8 1/6                  
    h5 - analyse, 6 1/7                    
    h6 - analyse, 4 1/8                       
    herftext - analyse, 2 1/8                     
    content - analyse, 1 5                      
    url - not analyse，100 ，10                       
    id - not                                 
    pr(pagerank) - not analyse                                        
                                                
  前一个这些都在建立field时使用setBoost函数设立，后一个在Muiltiquery是时候设立                 


	public MultiFieldQueryParser(Version matchVersion, String[] fields, Analyzer analyzer, Map<String,Float> boosts)
	Creates a MultiFieldQueryParser. Allows passing of a map with term to Boost, and the boost to apply to each term.
	It will, when parse(String query) is called, construct a query like this (assuming the query consists of two terms and you 	specify the two fields title and body):

	(title:term1 body:term1) (title:term2 body:term2)
	When setDefaultOperator(AND_OPERATOR) is set, the result will be:
	
	+(title:term1 body:term1) +(title:term2 body:term2)
	When you pass a boost (title=>5 body=>10) you can get
	
	+(title:term1^5.0 body:term1^10.0) +(title:term2^5.0 body:term2^10.0)
	In other words, all the query's terms must appear, but it doesn't matter in what fields they appear.    
	

  以上为文档中的内容，没有和理解boosts设置的含义，可能得再调整。

  加了搜索方式的选择，choice 为0， 为自定义的Similarity, 否则为系统自带的Similarity. 目前前端默认为0 。

3、垂直搜索相关
  加了图片域，选择article标签的第一个img,返回规范后之后的url, 否则返回空，不计入评分公式。
  
4、垂直搜索类调整
  出现了路径问题和编码问题。编码上更改了VerticalIndexs打开文件的语句，使用UTF-8打开。
  路径上由于是在启动服务器时加载的，要放到和forIndex对等的位置，目前是在forIndex中新建了一个verticalIndex文件夹，把原来indexs里的文件放进去。

5. Trie树相关，主要是新建两个类TrieNode和Trie。 用Trie的main函数做过小规模测试，目前可以通过。在服务器中需要有一个Trie树的对象，在初始化的时候调用构造函数就可以把树给建起来，之后使用Search接口，可以返回一个LIst,可以通过修改maxnum更改传回的最大数组长度，目前默认是5.
使用之前，需要先构建文件，运行一下MyJosonDealear可以得到articles.txt, 默认生成路劲为"forIndex/titles/alltitle.txt"。这样应该可以在服务器里使用这个树了，但没试过。我在读写文件的时候使用Unicode编码，不知道之后会不会遇到编码问题
  
