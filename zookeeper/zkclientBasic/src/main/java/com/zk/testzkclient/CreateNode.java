package com.zk.testzkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;

public class CreateNode {

	public static void main(String[] args) {
		ZkClient zc = new ZkClient("172.16.114.134:2181",10000,10000,new SerializableSerializer());
		System.out.println("conneted ok!");
		
		
		User u = new User();
		u.setId(1);
		u.setName("wu");
		String path = zc.create("/node_1", u, CreateMode.PERSISTENT);
		System.out.println("created path:"+path);
	}
	
}
