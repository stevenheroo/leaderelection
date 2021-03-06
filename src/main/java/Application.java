import clusters.LeaderElection;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import registry.LeaderCallBackAction;
import registry.ServiceRegistry;
import webservers.Server;

import java.io.IOException;

public class Application implements Watcher {

    private static final String ZOOKEEPER_ADDRESS = "localhost:2181";
    private static final int SESSION_TIMEOUT = 3000;
    private static final int DEFAULT_PORT = 8080;
    private static ZooKeeper zooKeeper;


    public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
        int currentPort = args.length == 1 ? Integer.parseInt(args[0]) : DEFAULT_PORT;

        Server server = new Server(currentPort);
        server.startServer();
        System.out.println("Server is listenning on port " + currentPort);

//        Application app = new Application();
//        app.connectToZookeeper();
//        ServiceRegistry serviceRegistry = new ServiceRegistry(zooKeeper);
//        LeaderCallBackAction callBackAction = new LeaderCallBackAction(serviceRegistry, currentPort);
//        LeaderElection election = new LeaderElection(zooKeeper, callBackAction);
//        election.creatingNodes();
//        election.reElectLeader();
//        app.run();
//        app.close();
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
