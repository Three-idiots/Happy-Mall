package cn.edu.zzu.wemall.net;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import cn.edu.zzu.wemall.config.MyConfig;

/*
 * Author Liudewei
 * 
 * 获取商品分类,返回List<HashMap<String, Object>>类型
 */
public class NetGoodsType {
	private static ArrayList<HashMap<String, Object>> data = new ArrayList<HashMap<String, Object>>();
	@SuppressWarnings("rawtypes")
	public static ArrayList<HashMap<String, Object>> getData() throws Exception {

		Document document; // 创建Document对象
		data = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> defaulttype = new HashMap<String, Object>();
		defaulttype.put("id", "-1");
		defaulttype.put("name", "全部商品");
		data.add(defaulttype);
		try {
			document = DocumentHelper.parseText(HttpRequest.sendGet(MyConfig.SERVERADDRESS+"?tag=wemall_query_menu")); 
			Element rootElement = document.getRootElement();
			for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				HashMap<String, Object> hashMap = new HashMap<String, Object>();
				hashMap.put("id", element.attributeValue("id"));
				hashMap.put("name", element.element("name").getText());
				data.add(hashMap);
				hashMap = null;
			}

		} catch (Exception e) {
			throw e;
		}
		return data;
	}

}
