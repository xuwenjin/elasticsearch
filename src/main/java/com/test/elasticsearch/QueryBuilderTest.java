package com.test.elasticsearch;

import java.util.Map;

import org.elasticsearch.action.search.SearchRequestBuilder;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.common.unit.TimeValue;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.junit.Test;

/**
 * 复杂查询，使用QueryBuilder进行bool查询
 * 
 * @author xuwenjin
 */
public class QueryBuilderTest extends BaseConnect {

	public String text = "科技视频";

	/**
	 * 单个精确值查找(termQuery)
	 */
	@Test
	public void termQuery() {
		QueryBuilder queryBuilder = QueryBuilders.termQuery("code", "01");
		queryBuilder = QueryBuilders.termQuery("isDelete", true);
		queryBuilder = QueryBuilders.termQuery("my_title", "我的标题12323abcd");
		searchFunction(queryBuilder);
	}

	/**
	 * 多个值精确查找(termsQuery)
	 * 
	 * 一个查询相匹配的多个value
	 */
	@Test
	public void termsQuery() {
		QueryBuilder queryBuilder = QueryBuilders.termsQuery("code", "01", "03", "04");
		searchFunction(queryBuilder);
	}

	/**
	 * 查询相匹配的文档在一个范围(rangeQuery)
	 */
	@Test
	public void rangeQuery() {
		QueryBuilder queryBuilder = QueryBuilders
				.rangeQuery("code") // 查询code字段
				.from("02")
				.to("04")
				.includeLower(true) // 包括下界
				.includeUpper(false);// 不包括上界
		searchFunction(queryBuilder);
	}

	/**
	 * 查询相匹配的文档在一个范围(prefixQuery)
	 */
	@Test
	public void prefixQuery() {
		QueryBuilder queryBuilder = QueryBuilders.prefixQuery("my_title", "我的");
		searchFunction(queryBuilder);
	}

	/**
	 * 通配符检索(wildcardQuery)
	 * 
	 * 值使用用通配符，常用于模糊查询
	 * 
	 * 匹配具有匹配通配符表达式（ (not analyzed ）的字段的文档。 支持的通配符： 
	 * 	*，它匹配任何字符序列（包括空字符序列）
	 * 	?，它匹配任何单个字符。
	 * 
	 * 请注意，此查询可能很慢，因为它需要遍历多个术语。 为了防止非常慢的通配符查询，通配符不能以任何一个通配符*或？开头。
	 */
	@Test
	public void wildcardQuery() {
		QueryBuilder queryBuilder = QueryBuilders.wildcardQuery("my_title", "*6789*");
		queryBuilder = QueryBuilders.wildcardQuery("my_title", "*345");
		queryBuilder = QueryBuilders.wildcardQuery("name", "?闻");
		searchFunction(queryBuilder);
	}

	/**
	 * 正则表达式检索(regexpQuery) 不需要^、$
	 */
	@Test
	public void regexpQuery() {
		QueryBuilder queryBuilder = QueryBuilders.regexpQuery("my_title", "我的.+f");
		searchFunction(queryBuilder);
	}

	/**
	 * 使用模糊查询匹配文档查询(fuzzyQuery)
	 */
	@Test
	public void fuzzyQuery() {
		QueryBuilder queryBuilder = QueryBuilders.fuzzyQuery("name", "科技");
		searchFunction(queryBuilder);
	}

	/**
	 * 类型检索(typeQuery)
	 * 
	 * 查询该类型下的所有数据
	 */
	@Test
	public void typeQuery() {
		QueryBuilder queryBuilder = QueryBuilders.typeQuery(ComKeys.TYPE);
		searchFunction(queryBuilder);
	}

	/**
	 * Ids检索, 返回指定id的全部信息 (idsQuery)
	 * 
	 * 在idsQuery(type)方法中，也可以指定具体的类型
	 */
	@Test
	public void idsQuery() {
		QueryBuilder queryBuilder = QueryBuilders.idsQuery().addIds("1", "4", "10");
		searchFunction(queryBuilder);
	}

	/************************************************************ 全文检索 ************************************************************/

	/**
	 * 单个匹配 (matchQuery)
	 * 
	 * 感觉跟termQuery效果一样
	 */
	@Test
	public void matchQuery() {
		QueryBuilder queryBuilder = QueryBuilders.matchQuery("name", "科技");
		searchFunction(queryBuilder);
	}
	
	/**
	 * 查询匹配所有文件 (matchAllQuery)
	 */
	@Test
	public void matchAllQuery() {
		QueryBuilder queryBuilder = QueryBuilders.matchAllQuery();
		searchFunction(queryBuilder);
	}
	
	/**
	 * 匹配多个字段, field可以使用通配符(multiMatchQuery)
	 */
	@Test
	public void multiMatchQuery() {
		QueryBuilder queryBuilder = QueryBuilders.multiMatchQuery("132445dfgdfg", "my_title", "name", "you_title");
		queryBuilder = QueryBuilders.multiMatchQuery("132445dfgdfg", "*title"); //字段使用通配符
		searchFunction(queryBuilder);
	}
	
	/**
	 * 字符串检索(queryString)
	 * 
	 * 一个使用查询解析器解析其内容的查询。
	 *  query_string查询提供了以简明的简写语法执行多匹配查询 multi_match queries ，布尔查询 bool queries ，提升得分 boosting ，模糊
	 *  匹配 fuzzy matching ，通配符 wildcards ，正则表达式 regexp 和范围查询 range queries 的方式。
	 *  
     *  支持参数达10几种
	 */
	@Test
	public void queryString() {
		QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("*技术");   //通配符查询
//		QueryBuilder queryBuilder = QueryBuilders.queryStringQuery("我的.+f");
		searchFunction(queryBuilder);
	}
	
	/**
	 * must 相当于and，就是都满足 
	 * should 相当于or，满足一个或多个 
	 * must_not 都不满足
	 */
	@Test
	public void testQueryBuilder2() {
		// "科技视频"分词的结果是"科技", "视频", "频"
		BoolQueryBuilder queryBuilder = QueryBuilders.boolQuery();
		// queryBuilder.must(QueryBuilders.wildcardQuery("name", "*科技*"));
		// queryBuilder.must(QueryBuilders.wildcardQuery("info", "*共*"));
		// queryBuilder.must(QueryBuilders.wildcardQuery("content", "*美丽*"));

		queryBuilder.should(QueryBuilders.wildcardQuery("name", "*科技*"));
		queryBuilder.should(QueryBuilders.wildcardQuery("info", "*共*"));
		queryBuilder.should(QueryBuilders.wildcardQuery("content", "*美丽*"));
		queryBuilder.minimumShouldMatch(2); // 最少匹配数

		// queryBuilder.mustNot(QueryBuilders.wildcardQuery("name", "*科技*"));
		// queryBuilder.mustNot(QueryBuilders.wildcardQuery("info", "*共*"));
		// queryBuilder.mustNot(QueryBuilders.wildcardQuery("content", "*美丽*"));
		searchFunction(queryBuilder);
	}

	/**
	 * 查询遍历抽取
	 * 
	 * 查询结果是根据分值排序(从大到小)
	 * 
	 * @param queryBuilder
	 */
	private void searchFunction(QueryBuilder queryBuilder) {
		SearchRequestBuilder requestBuilder = client.prepareSearch().setIndices(ComKeys.INDEX).setTypes(ComKeys.TYPE)
				.setScroll(new TimeValue(60000)).setQuery(queryBuilder);
		SearchResponse response = requestBuilder.setFrom(0).setSize(100).execute().actionGet();
		System.out.println("--------------查询结果：----------------------");
		for (SearchHit hit : response.getHits()) {
			System.out.println("分值：" + hit.getScore()); // 相关度
			Map<String, Object> map = hit.getSource();
			for (String sKey : map.keySet()) {
				System.out.println(sKey + ": " + map.get(sKey));
			}
			System.out.println("--------------");
		}
		System.out.println("-----------------------------------");
	}
	

}
