package cn.itcast.goods.order.domain;

import java.util.List;

import cn.itcast.goods.user.domain.User;
/**
 * 订单
 * @author TH
 *
 */
public class Order {
	private String oid;//主键
	private String ordertime;//下订单的时间
	private double total; //总计
	private int status;//订单状态：1 已付款  2.已付款但未发货 3.已发货未确认收货 4.确认收货交易成功 
	                   //5.已取消（只有未付款才能取消）
	private String address;
	private User owner;//订单所有者
	
	private List<OrderItem> orderItemList;
	
	public List<OrderItem> getOrderItemList() {
		return orderItemList;
	}
	public void setOrderItemList(List<OrderItem> orderItemList) {
		this.orderItemList = orderItemList;
	}
	public String getOid() {
		return oid;
	}
	public void setOid(String oid) {
		this.oid = oid;
	}
	public String getOrdertime() {
		return ordertime;
	}
	public void setOrdertime(String ordertime) {
		this.ordertime = ordertime;
	}
	public double getTotal() {
		return total;
	}
	public void setTotal(double total) {
		this.total = total;
	}
	public int getStatus() {
		return status;
	}
	public void setStatus(int status) {
		this.status = status;
	}
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public User getOwner() {
		return owner;
	}
	public void setOwner(User owner) {
		this.owner = owner;
	}
	
	
}
