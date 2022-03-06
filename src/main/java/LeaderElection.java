import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.io.IOException;
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

    //create a child nodes for to join cluster
    public void volunteerForLeadership() throws InterruptedException, KeeperException {
        String znodePrefix = ELECTION_NAMESPACE + "/c_";
        String znodeFullPath = zooKeeper.create(znodePrefix, new byte[] {}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
        System.out.println("znode ::: "+ znodeFullPath);

        this.currentZnodeName = znodeFullPath.replace(ELECTION_NAMESPACE + "/", "");
//        electLeader();
    }

    //electing / re-electing a leader
    public void reElectLeader() throws InterruptedException, KeeperException {
        Stat predecessorStat = null;
        String predecessorName = "";

        while (predecessorStat == null){
            List<String> zChildren = zooKeeper.getChildren(ELECTION_NAMESPACE, false);
            Collections.sort(zChildren);
            String smallestChild = zChildren.get(0);

            if (smallestChild.equals(currentZnodeName)){
                System.out.println("I am the Leader");
                return;
            }
            else {
                int predecessorIndex = Collections.binarySearch(zChildren, currentZnodeName) - 1;
                predecessorName = zChildren.get(predecessorIndex);
                predecessorStat = zooKeeper.exists(ELECTION_NAMESPACE + "/" + predecessorName,this);
            }
        }
        System.out.println("watching :::>> " + predecessorName);
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
            case NodeDeleted:
                try {
                    reElectLeader();
                } catch (InterruptedException | KeeperException e) {
                    e.printStackTrace();
                }
        }
    }
}
