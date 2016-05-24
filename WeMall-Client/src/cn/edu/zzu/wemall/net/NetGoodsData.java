package cn.edu.zzu.wemall.net;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import org.xmlpull.v1.XmlPullParser;
import cn.edu.zzu.wemall.config.MyConfig;
import cn.edu.zzu.wemall.object.GoodsItem;
import android.annotation.SuppressLint;
import android.util.Xml;

/*
 * Author Liudewei
 * 
 * 获取商品列表类,返回ArrayList<ListItem>类型
 */
public class NetGoodsData {

	private static ArrayList<GoodsItem> Items = null;

	@SuppressLint("UseValueOf")
	public static ArrayList<GoodsItem> getData() {
		try {
			URL url = new URL(MyConfig.SERVERADDRESS+"?tag=wemall_query_goods");
			GoodsItem Item = null;
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestProperty("user-agent", MyConfig.ClientUserAgent);
			conn.setRequestMethod("GET");
			conn.setConnectTimeout(8000);
			if (conn.getResponseCode() == 200) {
				InputStream inputstream = conn.getInputStream();
				XmlPullParser xml = Xml.newPullParser();
				xml.setInput(inputstream, "UTF-8");
				int event = xml.getEventType();
				while (event != XmlPullParser.END_DOCUMENT) {
					switch (event) {
					// 开始解析文档
					case XmlPullParser.START_DOCUMENT:
						Items = new ArrayList<GoodsItem>();
						break;
					case XmlPullParser.START_TAG:

						String value = xml.getName();
						if (value.equals("item")) {
							Item = new GoodsItem();
							Item.setId(new Integer(xml.getAttributeValue(0)));
						} else if (value.equals("name")) {
							Item.setName(xml.nextText());

						} else if (value.equals("typeid")) {
							Item.setTypeId(Integer.parseInt(xml.nextText()));
						} else if (value.equals("image")) {
							Item.setImage(xml.nextText());
						} else if (value.equals("intro")) {
							Item.setIntro(xml.nextText());
						} else if (value.equals("price")) {
							Item.setPrice(Double.parseDouble(xml.nextText()));
						} else if (value.equals("priceno")) {
							Item.setPriceno(Double.parseDouble(xml.nextText()));
						}
						break;
					case XmlPullParser.END_TAG:
						if (xml.getName().equals("item")) {
							Items.add(Item);
							Item = null;
						}
						break;
					}
					event = xml.next();
				}
				return Items;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;

	}

}
