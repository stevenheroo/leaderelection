# leader-election

A simple distributed systems logic JAVA

**`Tools`**

**1**. Maven
**2**. Zookeeper
**3**. Java
# Setup Apache Zookeeper #
    #you need JDK 1.8 or later installed on machine first ..
Step
1. follow link https://zookeeper.apache.org/releases.html to download
2. unzip or unrar, paste in a preferred location
3. locate conf folder & change zoo_sample.cfg file to -> zoo.cfg
4. open terminal >>> in bin location and run ./zkService start
(logs folder will be created automatically in zookeeper folder)
5. update dataDir in zoo.cfg file ->>>> eg.(mine) dataDir=home/***/zookeeper/logs

--bin dir <> contains CL tools for zookeeper

Demo includes
1. create a child nodes to join cluster
2. using Zookeeper to simplify selection a leader in a cluster( zookeeper algorithm makes it simple to elect a node)
3. using watchers to trigger alerts on failure of a node , i.e= stopped, deleted, noResponse, etc
   _1. Fault-tolerant implementation_
4. re-election a node to become a leader,
   _1. adding nodes dynamically
   2. prevent hard effect elimination of node with no bottlenecks_
5. Implementation of Service Registry & service discovery
6. Add callback to leader election in cluster
7. Build & Implement multithreaded http server with standard java libraries
   _1. the server will have two endpoints
   2. /status - handles GET requests(Reports the health of the application)
   3. /task - handles POST requests(sends results)_
8. creating custom headers 
9. Testing Http server created(Use cURL to test the endpoints)
   _1. eg. curl --request GET -v localhost:8080/status - to get status of server
   2. eg. curl --request POST -v --data "xxx" localhost:8080/task - to post data to server_


    
