package com.test.elasticsearch;

import java.util.HashMap;
import java.util.Map;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

/**
 * 数据存储操作测试
 * 
 * @time 2018年3月2日13:33:12
 *
 */
public class StorageTest extends AbstractJunitTest {

//	private static final String INDEX = "test_index1";
//	private static final String TYPE = "test_type1";
	private static final String INDEX2 = "test_index2";
	private static final String TYPE2 = "test_type2";

	@Test
	public void saveData() {
		try {
			Map<String, Object> source = new HashMap<String, Object>();
			source.put("code", "01");
			source.put("name", "科技");
			source.put("info", "中共十九大");
			source.put("content", "中共十九大");
			
			Map<String, Object> source2 = new HashMap<String, Object>();
			source2.put("code", "02");
			source2.put("name", "新闻");
			source2.put("info", "中国周恩来");
			source2.put("content", "中国周恩来");
			
			Map<String, Object> source3 = new HashMap<String, Object>();
			source3.put("code", "03");
			source3.put("name", "科学技术");
			source3.put("info", "用友建筑大武汉");
			source3.put("content", "用友建筑大武汉");
			
			Map<String, Object> source4 = new HashMap<String, Object>();
			source4.put("code", "04");
			source4.put("name", "快手视频");
			source4.put("info", "中国特色社会主义");
			source4.put("content", "中国特色社会主义");
			
			Map<String, Object> source5 = new HashMap<String, Object>();
			source5.put("code", "05");
			source5.put("name", "科技视频");
			source5.put("info", "最美天安门");
			source5.put("content", "最美天安门");
			
			Map<String, Object> source6 = new HashMap<String, Object>();
			source6.put("code", "06");
			source6.put("name", "最快的技术");
			source6.put("info", "美丽大武汉");
			source6.put("content", "美丽大武汉");
			
			client.prepareIndex(INDEX2, TYPE2).setId("1").setSource(source).get();
			client.prepareIndex(INDEX2, TYPE2).setId("2").setSource(source2).get();
			client.prepareIndex(INDEX2, TYPE2).setId("3").setSource(source3).get();
			client.prepareIndex(INDEX2, TYPE2).setId("4").setSource(source4).get();
			client.prepareIndex(INDEX2, TYPE2).setId("5").setSource(source5).get();
			client.prepareIndex(INDEX2, TYPE2).setId("6").setSource(source6).get();
			
		} catch (Exception e) {
			logger.error("保存数据失败！", e);
		}
	}

	@Test
	public void queryObject() {
		try {
			GetResponse res = client.prepareGet("test_index1", "test_type1", "1").get();
			if (res.isExists()) {
				logger.info("根据ID查询到数据，主要内容：" + res.getSource().get("content"));
			} else {
				logger.info("根据ID未查询到数据！");
			}
		} catch (Exception e) {
			logger.error("根据ID查询记录失败！", e);
		}
	}

	/**
	 * 使用QueryBuilder termQuery("key", obj) 完全匹配 
	 * termsQuery("key", obj1, obj2..) 一次匹配多个值
	 * matchQuery("key", Obj) 单个匹配, field不支持通配符, 前缀具高级特性
	 * multiMatchQuery("text", "field1", "field2"..); 匹配多个字段, field有通配符忒行
	 * matchAllQuery(); 匹配所有文件
	 */
	@Test
	public void testQueryBuilder() {
		//"科技视频"分词的结果是"科技", "视频", "频"
		String text = "科技视频";
		QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(text, "name"); 
//		QueryBuilder queryBuilder = QueryBuilders.termQuery("name", new ArrayList<String>().add(text));
//		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
		searchFunction(queryBuilder);
	}
	
	/**
	 * must 相当于and，就是都满足
	 * should 相当于or，满足一个或多个
	 * must_not 都不满足
	 */
	@Test
	public void testQueryBuilder2() {
		//"科技视频"分词的结果是"科技", "视频", "频"
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
//		queryBuilder.must(QueryBuilders.wildcardQuery("name", "*科技*"));
//		queryBuilder.must(QueryBuilders.wildcardQuery("info", "*共*"));
//		queryBuilder.must(QueryBuilders.wildcardQuery("content", "*美丽*"));
		
		queryBuilder.should(QueryBuilders.wildcardQuery("name", "*科技*"));
		queryBuilder.should(QueryBuilders.wildcardQuery("info", "*共*"));
		queryBuilder.should(QueryBuilders.wildcardQuery("content", "*美丽*"));
		
//		queryBuilder.mustNot(QueryBuilders.wildcardQuery("name", "*科技*"));
//		queryBuilder.mustNot(QueryBuilders.wildcardQuery("info", "*共*"));
//		queryBuilder.mustNot(QueryBuilders.wildcardQuery("content", "*美丽*"));
		searchFunction(queryBuilder);
	}

	@Test
	public void queryList() {
		try {
			String key = "周恩来";
			QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(key, "name", "content");

			SearchResponse res = client.prepareSearch().setIndices("test_index1").setTypes("test_type1")
					.setQuery(queryBuilder).get();

			logger.info("查询到的总记录个数为：" + res.getHits().getTotalHits());
			for (int i = 0; i < res.getHits().getTotalHits(); i++) {
				logger.info("第" + (i + 1) + "条记录主要内容为：" + res.getHits().getAt(i).getSource().get("content"));
			}
		} catch (Exception e) {
			logger.error("查询列表失败！", e);
		}
	}

	@Test
	public void deleteData() {
		try {
			DeleteResponse res = client.prepareDelete("test_index1", "test_type1", "1").get();

			logger.info("删除动作执行状态：" + res.status());
		} catch (Exception e) {
			logger.error("删除数据失败！", e);
		}
	}

	/**
	 * 查询遍历抽取
	 * 
	 * 查询结果是根据分值排序(从大到小)
	 * 
	 * @param queryBuilder
	 */
	private void searchFunction(QueryBuilder queryBuilder) {
		SearchResponse response = client.prepareSearch().setIndices(INDEX2).setTypes(TYPE2).setScroll(new TimeValue(60000))
				.setQuery(queryBuilder).setSize(100).execute().actionGet();
		System.out.println("--------------查询结果：----------------------");
		for (SearchHit hit : response.getHits()) {
			System.out.println("分值：" + hit.getScore()); //相关度
			Map<String, Object> map = hit.getSource();
			for (String sKey : map.keySet()) {
				System.out.println(sKey + ": " + map.get(sKey));
			}
		}
		System.out.println("-----------------------------------");
	}

}
