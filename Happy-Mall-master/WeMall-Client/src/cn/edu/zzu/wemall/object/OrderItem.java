package cn.edu.zzu.wemall.object;

/*
 * 
 *订单类 
 * 
 */
public class OrderItem {

	private int id;
	private String orderid;
	private Double totalprice;
	private String pay_style;
	private String pay_status;
	private String note;
	private String order_status;
	private String time;
	private String cartdata;
	private boolean todayfirst;//标志,用于listview分组,如果此订单属于这一天的第一个,则在此订单上方列表项上添加日期栏

	public int getId() {
		return id;
	}

	public String getOrderid() {
		return orderid;
	}

	public Double getTotalprice() {
		return totalprice;
	}

	public String getPay_style() {
		return pay_style;
	}

	public String getPay_status() {
		return pay_status;
	}

	public String getNote() {
		return note;
	}

	public String getOrder_status() {
		return order_status;
	}

	public String getTime() {
		return time;
	}

	public String getCartdata() {
		return cartdata;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setOrderid(String orderid) {
		this.orderid = orderid;
	}

	public void setTotalprice(Double totalprice) {
		this.totalprice = totalprice;
	}

	public void setPay_style(String pay_style) {
		this.pay_style = pay_style;
	}

	public void setPay_status(String pay_status) {
		this.pay_status = pay_status;
	}

	public void setNote(String note) {
		this.note = note;
	}

	public void setOrder_status(String order_status) {
		this.order_status = order_status;
	}

	public void setTime(String time) {
		this.time = time;
	}

	public void setCartdata(String cartdata) {
		this.cartdata = cartdata;
	}

	public boolean isTodayfirst() {
		return todayfirst;
	}

	public void setTodayfirst(boolean todayfirst) {
		this.todayfirst = todayfirst;
	}
}
