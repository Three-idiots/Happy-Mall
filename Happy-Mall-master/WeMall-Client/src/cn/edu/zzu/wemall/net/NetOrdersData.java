package cn.edu.zzu.wemall.net;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import android.annotation.SuppressLint;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.object.OrderItem;

/*
 * Author Liudewei
 * 
 * 获取订单列表类,返回ArrayList<OrderItem>类型
 */
public class NetOrdersData {

	private static ArrayList<OrderItem> Items = null;

	@SuppressLint("SimpleDateFormat")
	@SuppressWarnings("rawtypes")
	public static ArrayList<OrderItem> getData(final String param) {

		Items = new ArrayList<OrderItem>();
		Calendar cal = Calendar.getInstance();
		int day = 0;
		Document document; // 创建Document对象
		try {
			OrderItem Item = null;
			document =DocumentHelper.parseText(HttpRequest.sendPost(MyConfig.SERVERADDRESS+"?tag=wemall_query_myorder",param)); // 联网获取该XMl文档
			/*
			 * 接下来的是读取该文档的结构,并返回数据
			 */
			Element rootElement = document.getRootElement();
			for (Iterator i = rootElement.elementIterator(); i.hasNext();) {
				Item = new OrderItem();
				Element element = (Element) i.next();
				/**
				 * 
				 * 判断订单里的某一天的第一个,方便在列表中分组(在某一天的第一个订单头项上加日期)
				 * 
				 */
				cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(element.element("time").getText()));
				if (!(day == cal.get(Calendar.DAY_OF_MONTH))) {
					Item.setTodayfirst(true);
				} else {
					Item.setTodayfirst(false);
				}
				day = cal.get(Calendar.DAY_OF_MONTH);
				Item.setId(Integer.parseInt(element.attributeValue("id").toString()));
				Item.setOrderid(element.element("orderid").getText().toString());
				Item.setTotalprice(Double.parseDouble(element.element("totalprice").getText().toString()));
				Item.setPay_style(element.element("totalprice").getText().toString());
				Item.setPay_status(element.element("pay_status").getText().toString());
				Item.setNote(element.element("note").getText().toString());
				Item.setOrder_status(element.element("order_status").getText()
						+ "");
				Item.setTime(element.element("time").getText().toString());
				Item.setCartdata(element.element("cartdata").getText().toString());
				Items.add(Item);
				Item = null;
			}

		} catch (Exception e) {
			return null;
		}
		return Items;

	}

}
