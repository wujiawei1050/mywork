package zookeeper;


import java.io.IOException;
import java.util.List;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.EventType;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;


public class GetChildrenSync implements Watcher{
	
	
    private static ZooKeeper zooKeeper;
	public static void main(String[] args) throws IOException, InterruptedException, KeeperException {
				
		zooKeeper = new ZooKeeper("172.16.114.133:2181",5000,new GetChildrenSync());
		System.out.println(zooKeeper.getState().toString());
			
		Thread.sleep(Integer.MAX_VALUE);
		

	}
	
	private void doSomething(ZooKeeper zooKeeper){
		 
		try {
			
			List<String> children =  zooKeeper.getChildren("/", true);
			System.out.println(children);
			
		} catch (Exception e) {
			// TODO: handle exception
		}
		
	}
	
	public void regeist(ZooKeeper zooKeeper,String node) throws KeeperException, InterruptedException {
		List<String> nodes=zooKeeper.getChildren(node,true);
		for(int i=0;i<nodes.size();i++) {
			if(nodes.get(i).equals("zookeeper"))
				continue;
			if(!node.equals("/")) {
				regeist(zooKeeper,node+"/"+nodes.get(i));
			}else {
				regeist(zooKeeper,node+nodes.get(i));
			}
			
				
		}
	}

	@Override
	public void process(WatchedEvent event) {
		if (event.getState()==KeeperState.SyncConnected){
			if (event.getType()==EventType.None && null==event.getPath()){
				doSomething(zooKeeper);
			}else{
				if (event.getType()==EventType.NodeChildrenChanged){
					try {
						System.out.println(zooKeeper.getChildren(event.getPath(),true));
						regeist(zooKeeper,event.getPath());
					} catch (KeeperException e) {
						e.printStackTrace();
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}		
		}
	}

}
