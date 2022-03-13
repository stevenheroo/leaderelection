package registry;

import org.apache.zookeeper.KeeperException;

import java.net.UnknownHostException;

public interface LeaderElectionCallBack {

    void onElectedLeaderCallBack() throws InterruptedException, KeeperException;

    void onWorkerCallback() throws InterruptedException, KeeperException, UnknownHostException;
}
