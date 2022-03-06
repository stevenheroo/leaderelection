package clusters;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import java.util.Collections;
import java.util.List;

public class LeaderElection {

    private static final String PARENT_PATH_NAME = "/election";
    private final ZooKeeper zooKeeper;
    private String currentNodeName;

    public LeaderElection(ZooKeeper zooKeeper) {
        this.zooKeeper = zooKeeper;
    }

    public void creatingNodes() throws InterruptedException, KeeperException {
        String nodePrefix = PARENT_PATH_NAME + "/c_";
        String fullPathNode = zooKeeper.create(nodePrefix, new byte[]{},
                ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("Z_NODE PATH::: "+ fullPathNode);
        this.currentNodeName = fullPathNode.replace(PARENT_PATH_NAME + "/", "");
    }

    public void reElectLeader() throws InterruptedException, KeeperException {
        List<String> children = zooKeeper.getChildren(PARENT_PATH_NAME, false);
        Collections.sort(children);
        //check and store node in variable
        String smallestChild = children.get(0);

        //check whether smallestChild name is same as currentNodeName
        if (smallestChild.equals(currentNodeName)){
            System.out.println("I am the Leader");
        }
        else {
            System.out.println("I am not Leader " + smallestChild + " is the leader");
//            int predecessorIndex = Collections.binarySearch(children, currentNodeName) -1;
        }

    }

}
