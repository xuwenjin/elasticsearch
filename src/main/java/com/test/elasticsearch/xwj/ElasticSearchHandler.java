package com.test.elasticsearch.xwj;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.List;

import org.elasticsearch.client.Client;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;

public class ElasticSearchHandler {

	@SuppressWarnings({ "resource", "deprecation" })
	public static void main(String[] args) {
		try {
			/* 创建客户端 */
			
			Settings settings = Settings.builder().put("cluster.name", "xwj").build();
			TransportAddress transportAddress = new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"),
					9300);
			Client client = new PreBuiltTransportClient(settings).addTransportAddress(transportAddress);
			
//			Client client = TransportClient.builder().build()
//					.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName("127.0.0.1"), 9300));

			List<String> jsonData = DataFactory.getInitJsonData();

			for (int i = 0; i < jsonData.size(); i++) {
				client.prepareIndex("blog", "article").setSource(jsonData.get(i)).get();
			}
			client.close();
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} 
	}

}
