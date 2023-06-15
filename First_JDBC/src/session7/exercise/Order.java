package session7.exercise;

import java.math.BigDecimal;
import java.util.Date;

public class Order {
	private Date orderDate;
	private String customerName;
	private int goodsName;
	private int goodsPrice;
	private int buyQuantity;
	
	public Date getOrderDate() {
		return orderDate;
	}
	public void setOrderDate(Date orderDate) {
		this.orderDate = orderDate;
	}
	public String getCustomerName() {
		return customerName;
	}
	public void setCustomerName(String customerName) {
		this.customerName = customerName;
	}
	
	public int getGoodsName() {
		return goodsName;
	}
	public void setGoodsName(int goodsName) {
		this.goodsName = goodsName;
	}
	public int getGoodsPrice() {
		return goodsPrice;
	}
	public void setGoodsPrice(int goodsPrice) {
		this.goodsPrice = goodsPrice;
	}
	public int getBuyQuantity() {
		return buyQuantity;
	}
	public void setBuyQuantity(int buyQuantity) {
		this.buyQuantity = buyQuantity;
	}
	
	@Override
	public String toString() {
		return "Order [orderDate=" + orderDate + ", customerName="
				+ customerName + ", goodsName=" + goodsName + ", goodsPrice="
				+ goodsPrice + ", buyQuantity=" + buyQuantity + "]";
	}	
}
