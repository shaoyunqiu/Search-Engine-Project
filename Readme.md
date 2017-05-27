# Search-Engine-Project
## 打分规则
目前初步的打分的规则为：
1. pageRank, 将pageRank的权重作为每个document的权重，在建立索引时设立。此处暂时对PageRank值开更号，以减少数量级的差值。
2. 不同域的权重，改变field的setBoost, 可以实现对tf的加权，在建立filed的值设立。
  目前field的内容如下：
    title - analyse, 60
    keywords - analyse, 50
    h1 - analyse, 36
    h2 - analyse, 30
    h3 - analyse, 24
    h4 - analyse, 18
    h5 - analyse, 12
    h6 - analyse, 6
    herftext - analyse, 5
    content - analyse, 2
    bodytext - analyse, 0.5
    id - not analayse
    pr(pagerank) - not analyse
    url - not analyse
  这些都在建立field时使用setBoost函数设立