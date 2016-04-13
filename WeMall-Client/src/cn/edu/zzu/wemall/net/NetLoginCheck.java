package cn.edu.zzu.wemall.net;

import java.util.HashMap;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;

import cn.edu.zzu.wemall.config.MyConfig;

/*
 * Author Liudewei
 * 
 * 获取登录返回信息,返回List<HashMap<String, Object>>类型
 */
public class NetLoginCheck {
	@SuppressWarnings("rawtypes")
	public static HashMap<String, Object> getData(final String path) throws Exception {
		HashMap<String, Object> data = new HashMap<String, Object>();
		Document document; // 创建Document对象
		try {
			document = DocumentHelper.parseText(HttpRequest.sendPost(MyConfig.SERVERADDRESS+"?tag=wemall_login_check",path)); // 联网获取该XMl文档
			/*
			 * 接下来的是读取该文档的结构,并返回数据
			 */
			Element rootElement = document.getRootElement();
			for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
				Element element = (Element) i.next();
				data.put("state", element.attributeValue("state"));
				data.put("uid", element.element("uid").getText());
				data.put("name", element.element("name").getText());
				data.put("phone", element.element("phone").getText());
				data.put("address", element.element("address").getText());
			}

		} catch (DocumentException e) {
			return null;
		}
		
		return data;
	}

}
