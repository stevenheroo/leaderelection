import clusters.LeaderElection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;

import java.io.IOException;

public class Application implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static ZooKeeper zooKeeper;

    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        Application app = new Application();
        app.connectToZookeeper();
        LeaderElection election = new LeaderElection(zooKeeper);
        election.creatingNodes();
        election.reElectLeader();
        app.run();
        app.close();
    }

    public void connectToZookeeper() throws IOException {
        //create an instance of zookeeper
        zooKeeper = new ZooKeeper(ZOOKEEPER_ADDRESS, SESSION_TIMEOUT, this);
    }


    public void run() throws InterruptedException {
        synchronized (zooKeeper){
            zooKeeper.wait();
        }
    }

    public void close() throws InterruptedException {
        zooKeeper.close();
        System.out.println("Exiting application.....");
    }

    @Override
    public void process(WatchedEvent event) {
        switch (event.getType()){
            case None:
            if (event.getState() == Event.KeeperState.SyncConnected) {
                System.out.println("Successfully Connected to zookeeper");
            }
            else {
                synchronized (zooKeeper){
                    System.out.println("Disconnect from event");
                    zooKeeper.notifyAll();
                }
            }
        }
    }
}
