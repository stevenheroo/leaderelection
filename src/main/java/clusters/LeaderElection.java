package clusters;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;
import registry.LeaderElectionCallBack;

import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    private static final String PARENT_PATH_NAME = "/election";
    private final ZooKeeper zooKeeper;
    private String currentNodeName;
    private final LeaderElectionCallBack electionCallBack;

    public LeaderElection(ZooKeeper zooKeeper, LeaderElectionCallBack electionCallBack) {
        this.zooKeeper = zooKeeper;
        this.electionCallBack = electionCallBack;
    }

    public void creatingNodes() throws InterruptedException, KeeperException {
        String nodePrefix = PARENT_PATH_NAME + "/c_";
        String fullPathNode = zooKeeper.create(nodePrefix, new byte[]{},
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Z_NODE PATH::: "+ fullPathNode);
        this.currentNodeName = fullPathNode.replace(PARENT_PATH_NAME + "/", "");
    }

    public void reElectLeader() throws InterruptedException, KeeperException, UnknownHostException {
        Stat predecessorStat = null;
        String predecessorName = "";

        while (predecessorStat == null){
            List<String> children = zooKeeper.getChildren(PARENT_PATH_NAME, false);
            Collections.sort(children);
            //check and store node in variable
            String smallestChild = children.get(0);

            //check whether smallestChild name is same as currentNodeName
            if (smallestChild.equals(currentNodeName)){
                System.out.println("I am the Leader");
                electionCallBack.onElectedLeaderCallBack();
                return;
            }
            else {
                System.out.println("I am not the Leader");
                int predecessorIndex = Collections.binarySearch(children, currentNodeName) -1;
                //store name from predecessor Index
                predecessorName = children.get(predecessorIndex);
                //using watcher stat to track state of node,
                //if failure of node successor is notified
                predecessorStat = zooKeeper.exists(PARENT_PATH_NAME+ "/" + predecessorName, this);
            }
        }
        electionCallBack.onWorkerCallback();
        System.out.println("WATCHING Z_NODE::::>> "+ predecessorName);

    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()){
            case NodeDeleted:
                try {
                    reElectLeader();
                } catch (InterruptedException | KeeperException | UnknownHostException e) {
                    e.printStackTrace();
                }
        }
    }
}
