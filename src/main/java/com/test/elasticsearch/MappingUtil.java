package com.test.elasticsearch;

import java.io.IOException;
import java.util.Map;

import org.elasticsearch.common.xcontent.XContentBuilder;
import org.elasticsearch.common.xcontent.XContentFactory;

public class MappingUtil {

	public void createIndex(String type, Map<String, Map<String, String>> proNames) throws IOException {

		XContentBuilder builder = XContentFactory.jsonBuilder().startObject().startObject(type)
				.field("dynamic", "strict") // 遇到陌生字段，抛出异常
				.startObject("properties");

		if (!proNames.isEmpty()) {
			for (Map.Entry<String, Map<String, String>> proName : proNames.entrySet()) {
				if (null != proName.getKey()) {
					builder.startObject(proName.getKey());
					if (!proName.getValue().isEmpty()) {
						for (Map.Entry<String, String> filed : proName.getValue().entrySet()) {
							String pro = filed.getKey();
							String value = filed.getValue();
							if (null != pro && null != value) {
								builder.field(pro, value);
							}
						}
					}
					builder.endObject();
				}

			}
		}
		builder.endObject().endObject().endObject();
	}

}
