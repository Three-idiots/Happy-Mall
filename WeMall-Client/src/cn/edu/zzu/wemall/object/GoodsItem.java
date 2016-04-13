package cn.edu.zzu.wemall.object;

import java.io.Serializable;

/**
 * Author Liudewei
 * 
 * 商品类,包含商品的所有属性
 * 
 */
public class GoodsItem implements Serializable {

	private static final long serialVersionUID = 1L;
	private int id;
	private int typeid;
	private String name;
	private String image;
	private String intro;
	private Double price;
	private Double priceno;

	public int getId() {
		return id;
	}

	public int getTypeId() {
		return typeid;
	}

	public String getName() {
		return name;
	}

	public String getImage() {
		return image;
	}

	public String getIntro() {
		return intro;
	}

	public Double getPrice() {
		return price;
	}

	public Double getPriceno() {
		return priceno;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setTypeId(int typeid) {
		this.typeid = typeid;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setImage(String image) {
		this.image = image;
	}

	public void setIntro(String intro) {
		this.intro = intro;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	public void setPriceno(Double priceno) {
		this.priceno = priceno;
	}

}
