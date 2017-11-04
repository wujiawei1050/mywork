package zookeeper;
import java.io.IOException;

import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;


/**
 * @author wujiawei
 * ��������
 */
public class CreateSession implements Watcher { 

	private static ZooKeeper zookeeper;
	public static void main(String[] args) throws IOException, InterruptedException {
		zookeeper = new ZooKeeper("172.16.114.133:2181",5000,new CreateSession());
		System.out.println(zookeeper.getState());
		
		Thread.sleep(Integer.MAX_VALUE);
	}
	
	@Override
	public void process(WatchedEvent event) {
		// TODO Auto-generated method stub
		System.out.println("�յ��¼���"+event);
		if (event.getState()==KeeperState.SyncConnected){
			
			if (event.getType()==EventType.None && null==event.getPath()){
				System.out.println(event.getPath());
			}
		}
	}
	
}
