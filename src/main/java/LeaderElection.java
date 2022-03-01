import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class LeaderElection implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private ZooKeeper zooKeeper;

    public static void main(String [] args) throws IOException, InterruptedException {
        LeaderElection election = new LeaderElection();
        election.connectToZookeeper();
        election.run();
        election.close();
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
