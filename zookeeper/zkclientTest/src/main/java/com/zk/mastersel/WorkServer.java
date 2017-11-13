package com.zk.mastersel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.I0Itec.zkclient.IZkDataListener;
import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.exception.ZkException;
import org.I0Itec.zkclient.exception.ZkInterruptedException;
import org.I0Itec.zkclient.exception.ZkNoNodeException;
import org.I0Itec.zkclient.exception.ZkNodeExistsException;
import org.apache.zookeeper.CreateMode;

public class WorkServer {
	/**
	 * 用于记录服务器的状态
	 */
	private volatile boolean running = false;

	private ZkClient zkClient;
	/**
	 * master节点对应zookeeper中的节点路径
	 */
	private static final String MASTER_PATH = "/master";
	/**
	 * 监听master节点的删除事件
	 */
	private IZkDataListener dataListener;
	/**
	 * 用于记录当前服务器的基本信息
	 */
	private RunningData serverData;
	/**
	 * 用于记录集群中master节点的基本信息
	 */
	private RunningData masterData;
	
	private ScheduledExecutorService delayExector = Executors.newScheduledThreadPool(1);
	private int delayTime = 5;

	public WorkServer(RunningData rd) {
		this.serverData = rd;
		this.dataListener = new IZkDataListener() {

			public void handleDataDeleted(String dataPath) throws Exception {
				
				//用于应对网络抖动，引起数据迁移，导致不必要的系统开销
				if (masterData!=null && masterData.getName().equals(serverData.getName())){
					takeMaster();
					
				}else{
					delayExector.schedule(new Runnable(){
						public void run(){
							takeMaster();
						}
					}, delayTime, TimeUnit.SECONDS);
					
				}
				
				
			}

			public void handleDataChange(String dataPath, Object data)
					throws Exception {
				// TODO Auto-generated method stub

			}
		};
	}

	public void start() throws Exception {
		if (running) {
			throw new Exception("server has startup...");
		}
		running = true;
		zkClient.subscribeDataChanges(MASTER_PATH, dataListener);
		takeMaster();

	}

	public void stop() throws Exception {
		if (!running) {
			throw new Exception("server has stoped");
		}
		running = false;
		
		delayExector.shutdown();

		zkClient.unsubscribeDataChanges(MASTER_PATH, dataListener);

		releaseMaster();

	}
	
	/**
	 * 用于争抢master权利
	 */
	private void takeMaster() {
		if (!running)
			return;

		try {
			zkClient.create(MASTER_PATH, serverData, CreateMode.EPHEMERAL);
			masterData = serverData;
			System.out.println(serverData.getName()+" is master");
		} catch (ZkNodeExistsException e) {
			RunningData runningData = zkClient.readData(MASTER_PATH, true);
			if (runningData == null) {
				takeMaster();
			} else {
				masterData = runningData;
			}
		} catch (Exception e) {
			// ignore;
		}

	}

	/**
	 * 用于释放master权利
	 */
	private void releaseMaster() {
		if (checkMaster()) {
			zkClient.delete(MASTER_PATH);

		}

	}

	/**
	 * 用于检测自己是不是master
	 */
	private boolean checkMaster() {
		try {
			RunningData eventData = zkClient.readData(MASTER_PATH);
			masterData = eventData;
			if (masterData.getName().equals(serverData.getName())) {
				return true;
			}
			return false;
		} catch (ZkNoNodeException e) {
			return false;
		} catch (ZkInterruptedException e) {
			return checkMaster();
		} catch (ZkException e) {
			return false;
		}
	}
	
	
	
	public ZkClient getZkClient() {
		return zkClient;
	}

	public void setZkClient(ZkClient zkClient) {
		this.zkClient = zkClient;
	}

}
