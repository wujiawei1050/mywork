package com.zk.testzkclient;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class GetData {

	public static void main(String[] args) {
		ZkClient zc = new ZkClient("172.16.114.134:2181",10000,10000,new SerializableSerializer());
		System.out.println("conneted ok!");
		
		Stat stat = new Stat();
		User u = zc.readData("/node_1",stat);
		System.out.println(u.toString());
		System.out.println(stat);
		
	}
	
}
