package com.zk.testzkclient;

import java.util.List;

import org.I0Itec.zkclient.ZkClient;
import org.I0Itec.zkclient.serialize.SerializableSerializer;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.data.Stat;

public class NodeExists {

	public static void main(String[] args) {
		ZkClient zc = new ZkClient("172.16.114.134:2181",10000,10000,new SerializableSerializer());
		System.out.println("conneted ok!");
		
		boolean e = zc.exists("/node_1");
		
		System.out.println(e);
		
	}
	
}
