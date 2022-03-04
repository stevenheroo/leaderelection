# leaderelection

A simple distributed system logic using Zookeeper to simplify selection of znodes,

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
