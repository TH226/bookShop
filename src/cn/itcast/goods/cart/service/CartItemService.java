package cn.itcast.goods.cart.service;

import java.sql.SQLException;
import java.util.List;

import org.apache.commons.beanutils.converters.CharacterArrayConverter;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.cart.dao.CartItemDao;
import cn.itcast.goods.cart.domain.CartItem;

public class CartItemService {
	private CartItemDao cartItemDao = new CartItemDao();
	/**
	 * 加载多个cartItem
	 * @param cartItemIds
	 * @return
	 */
	public List<CartItem> loadCartItems(String cartItemIds){
		try {
			return cartItemDao.loadCartItems(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 修改购物车条目数量
	 * @param cartItemId
	 * @param quantity
	 * @return
	 */
	public CartItem updateQuantity(String cartItemId,int quantity){
		try {
			cartItemDao.updateQuantity(cartItemId, quantity);
			return cartItemDao.findByCartItemId(cartItemId);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/**
	 * 批量删除
	 * @param cartItemIds
	 */
	public void batchDelete(String cartItemIds){
		try {
			cartItemDao.batchDelete(cartItemIds);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
	}
	
	/*
	 * 添加条目
	 */
	public void add(CartItem cartItem){
		/**
		 * 1.使用uid和bid去数据库查询这个条目是否存在
		 * cartItem2 数据库查出的订单条目
		 * cartItem 是新增加的条目
		 */
		try {
			CartItem cartItem2 = cartItemDao.findByUidAndBid(
					cartItem.getUser().getUid(),cartItem.getBook().getBid());
			
			if(cartItem2==null){ //如果原来没有这个条目，添加
				cartItem.setCartItemId(CommonUtils.uuid());
				cartItemDao.addCartItem(cartItem);
			}else{//如果原来有 修改数量
				//使用原有数量和新条目数量之和，作为新的数量
				int quantity = cartItem.getQuantity()+cartItem2.getQuantity();
				//修改老条目的数量
				cartItemDao.updateQuantity(cartItem2.getCartItemId(), quantity);
			}
			
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
	/**
	 * 我的购物车功能
	 * @param uid
	 * @return
	 */
	public List<CartItem> myCart(String uid){
		try {
			return cartItemDao.findByUser(uid);
		} catch (SQLException e) {
			throw new RuntimeException(e);
		}
		
	}
}
