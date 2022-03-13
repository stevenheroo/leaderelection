package registry;

import org.apache.zookeeper.KeeperException;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.List;

public class LeaderCallBackAction implements LeaderElectionCallBack{
    private final ServiceRegistry serviceRegistry;
    private final int port;

    public LeaderCallBackAction(ServiceRegistry serviceRegistry, int port) {
        this.serviceRegistry = serviceRegistry;
        this.port = port;
    }

    @Override
    public void onElectedLeaderCallBack() throws InterruptedException, KeeperException {
        serviceRegistry.unregisterUpdates();
        serviceRegistry.registerUpdates();
    }

    @Override
    public void onWorkerCallback() throws InterruptedException, KeeperException, UnknownHostException {
        String currentServerAddress =
                String.format("http://%s:%d", InetAddress.getLocalHost().getCanonicalHostName(), port);
        serviceRegistry.registerNodeToCluster(currentServerAddress);
    }
}
