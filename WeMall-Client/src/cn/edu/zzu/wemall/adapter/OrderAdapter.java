package cn.edu.zzu.wemall.adapter;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import cn.edu.zzu.wemall.R;
import cn.edu.zzu.wemall.object.OrderItem;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class OrderAdapter extends BaseAdapter {
	/**
	 * 
	 * 
	 * @author liudewei-zzu
	 * 
	 *         订单adapter
	 */

	private Activity activity;
	private ArrayList<OrderItem> data;
	private OrderItem orderitem;
	private LayoutInflater inflater = null;
	private TextView orderid, ordertime, ordertimebar, paystate, dispstate;
	private View vi;
	private Calendar cal = Calendar.getInstance();

	public OrderAdapter(Activity a, ArrayList<OrderItem> d) {
		this.activity = a;
		this.data = d;
		this.inflater = (LayoutInflater) activity
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

	}

	public int getCount() {
		return data.size();
	}

	public OrderItem getItem(int position) {

		return this.data.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	// 重设数据源,避免adapter.notifyDataSetChanged()无响应
	public void set_datasource(ArrayList<OrderItem> d) {
		this.data = d;
	}

	@SuppressLint({ "InflateParams", "SimpleDateFormat" })
	public View getView(int position, View convertView, ViewGroup parent) {
		orderitem = data.get(position);
		vi = convertView;
		// 此处需要优化
		vi = inflater.inflate(R.layout.wemall_orderitem_hastime, null);
		ordertimebar = (TextView) vi.findViewById(R.id.ordertimebar);
		orderid = (TextView) vi.findViewById(R.id.orderid2);
		ordertime = (TextView) vi.findViewById(R.id.ordertotal2);
		paystate = (TextView) vi.findViewById(R.id.paystate2);
		dispstate = (TextView) vi.findViewById(R.id.dispstate2);
		if (orderitem.isTodayfirst()) {
			try {
				cal.setTime(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
						.parse(orderitem.getTime()));
				ordertimebar.setText(cal.get(Calendar.YEAR) + "-"
						+ (cal.get(Calendar.MONTH) + 1) + "-"
						+ cal.get(Calendar.DAY_OF_MONTH));
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			orderid.setText(orderitem.getOrderid());
			ordertime.setText(orderitem.getTotalprice().toString());
			if (orderitem.getOrder_status().equals("1")) {
				dispstate.setText("已发货");
			} else {
				dispstate.setText("待发货");
			}
			// 判断支付和发货状态
			if (orderitem.getPay_status().equals("1")) {

				paystate.setText("已付款");
			} else if (orderitem.getPay_status().equals("0")
					&& orderitem.getPay_style().equals("货到付款")) {

				paystate.setText("未付款(货到付款)");
			}
		} else {
			vi = inflater.inflate(R.layout.wemall_orderitem, null);
			orderid = (TextView) vi.findViewById(R.id.orderid);
			ordertime = (TextView) vi.findViewById(R.id.ordertotal);
			paystate = (TextView) vi.findViewById(R.id.paystate);
			dispstate = (TextView) vi.findViewById(R.id.dispstate);

			orderid.setText(orderitem.getOrderid());
			ordertime.setText(orderitem.getTotalprice().toString());
			// 判断支付和发货状态
			// 判断支付和发货状态
			if (orderitem.getOrder_status().equals("1")) {
				dispstate.setText("已发货");
			} else {
				dispstate.setText("待发货");
			}
			if (orderitem.getPay_status().equals("1")) {

				paystate.setText("已付款");
			} else if (orderitem.getPay_status().equals("0")
					&& orderitem.getPay_style().equals("货到付款")) {

				paystate.setText("未付款(货到付款)");
			}

		}

		return vi;
	}
}