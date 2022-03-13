package registry;

import org.apache.zookeeper.*;
import org.apache.zookeeper.data.Stat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class ServiceRegistry implements Watcher {

    private static final String PATH_REGISTRY = "/registry_service";
    private final ZooKeeper zooKeeper;
    private String currentNode = null;
    private List<String> serviceAddresses = null;


    public ServiceRegistry(ZooKeeper zooKeeper) throws InterruptedException, KeeperException {
        this.zooKeeper = zooKeeper;
        createNodeRegistry();
    }

    private void createNodeRegistry() throws InterruptedException, KeeperException {
        if (zooKeeper.exists(PATH_REGISTRY, false) == null){
            zooKeeper.create(PATH_REGISTRY, new byte[]{}, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        }
    }

    public void registerNodeToCluster(String metaData) throws InterruptedException, KeeperException {
        String nodeNamePrefix = PATH_REGISTRY + "/n_";
        this.currentNode = zooKeeper.create(nodeNamePrefix, metaData.getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE,
                CreateMode.EPHEMERAL_SEQUENTIAL);
    }

    public void registerUpdates() throws InterruptedException, KeeperException {
        updateNodeAddress();
    }

    public synchronized List<String> getServiceAddresses() throws InterruptedException, KeeperException {
        if (serviceAddresses == null){
            updateNodeAddress();
        }
        return serviceAddresses;
    }

    public void unregisterUpdates() throws InterruptedException, KeeperException {
        if (currentNode != null && zooKeeper.exists(PATH_REGISTRY, this) != null){
            zooKeeper.delete(currentNode, -1);
            System.out.println(currentNode + " is deleted");
        }
    }

    public synchronized void updateNodeAddress() throws InterruptedException, KeeperException {
        List<String> workerNodes = zooKeeper.getChildren(PATH_REGISTRY, this);
        List<String> addresses = new ArrayList<>(workerNodes.size());

        for (String workerNode : workerNodes){
            String fullPathName = PATH_REGISTRY + "/" + workerNode;
            Stat stat = zooKeeper.exists(fullPathName, this);

            if (stat == null){
                continue;
            }

            byte[] addressBytes = zooKeeper.getData(fullPathName,this, stat);
            String address = new String(addressBytes);
            addresses.add(address);
        }
        this.serviceAddresses = Collections.unmodifiableList(addresses);
        System.out.println("The addresses are ::>>>> " + serviceAddresses);
    }

    @Override
    public void process(WatchedEvent watchedEvent) {
        try {
            updateNodeAddress();
        } catch (InterruptedException | KeeperException e) {
            e.printStackTrace();
        }
    }
}

