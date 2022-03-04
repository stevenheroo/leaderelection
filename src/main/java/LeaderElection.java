import org.apache.zookeeper.*;

import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class LeaderElection implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final String ELECTION_NAMESPACE = "/election";
    private String currentZnodeName;
    private ZooKeeper zooKeeper;

    public static void main(String [] args) throws IOException, InterruptedException, KeeperException {
        LeaderElection election = new LeaderElection();
        election.connectToZookeeper();
        election.volunteerForLeadership();
        election.run();
        election.close();
    }

    //create a volunteer nodes for selection
    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath = zooKeeper.create(znodePrefix, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("znode-->>> "+ znodeFullPath);

        this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
        electLeader();
    }

    //elect leader process
    public void electLeader() throws InterruptedException, KeeperException {
        //store all created nodes in a list
        List<String> zChildren = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
        //check for the smallest index in array and store
        String smallestChild = zChildren.get(0);

        if (smallestChild.equalsIgnoreCase(currentZnodeName))
            System.out.println("I am the Leader");
        else
            System.out.println("I'm not Leader!! " + currentZnodeName + "is the leader");

    }

    public void connectToZookeeper() throws IOException {
        this.zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }

    public void run() throws InterruptedException {
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        this.zooKeeper.close();
        System.out.println("Disconnected!!... Exiting application..");
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()){
            case None:
                if (event.getState() == Event.KeeperState.SyncConnected){
                    System.out.println("Successfully Connected to ZooKeeper");
                }
                else {
                    synchronized (zooKeeper){
                        System.out.println("Disconnected from event");
                        zooKeeper.notifyAll();
                    }
                }
        }
    }
}
