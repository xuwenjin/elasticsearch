package com.test.elasticsearch;

import org.elasticsearch.action.delete.DeleteResponse;
import org.elasticsearch.action.get.GetResponse;
import org.elasticsearch.action.search.SearchResponse;
import org.junit.Test;

/**
 * 简单查询数据、删除数据
 * 
 * @author xuwenjin
 */
public class SimpleQueryTest extends BaseConnect {

	@Test
	public void queryObject() {
		try {
			GetResponse res = client.prepareGet(ComKeys.INDEX, ComKeys.TYPE, "1").get();
			if (res.isExists()) {
				logger.info("根据ID查询到数据，主要内容：" + res.getSource().get("content"));
			} else {
				logger.info("根据ID未查询到数据！");
			}
		} catch (Exception e) {
			logger.error("根据ID查询记录失败！", e);
		}
	}

	@Test
	public void queryList() {
		try {
//			String key = "周恩来";
//			QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery(key, "name", "content");

			SearchResponse res = client.prepareSearch().setIndices(ComKeys.INDEX).setTypes(ComKeys.TYPE).get();
			System.out.println("查询到的总记录个数为：" + res.getHits().getTotalHits());
			
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
			DeleteResponse res = client.prepareDelete(ComKeys.INDEX, ComKeys.TYPE, "1").get();

			logger.info("删除动作执行状态：" + res.status());
		} catch (Exception e) {
			logger.error("删除数据失败！", e);
		}
	}

}
