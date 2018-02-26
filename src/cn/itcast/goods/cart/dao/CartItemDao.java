package cn.itcast.goods.cart.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapHandler;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.book.domain.Book;
import cn.itcast.goods.cart.domain.CartItem;
import cn.itcast.goods.user.domain.User;
import cn.itcast.jdbc.TxQueryRunner;

public class CartItemDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 用来生成where子句
	 * @param len
	 * @return
	 */
	private String toWhereSql(int len){
		StringBuilder sb = new StringBuilder("cartItemId in(");
		for(int i=0;i<len;i++){
			sb.append("?");
			if(i<len-1){
				sb.append(",");
			}
		}
		sb.append(")");
		return sb.toString();
	}
	
	public List<CartItem> loadCartItems(String cartItemIds) throws SQLException{
		/**
		 * 1.把cartItemIds转化为数组
		 */
		Object[] cartItemIdArray = cartItemIds.split(",");
		/**
		 * 2.生成where子句
		 */
		String whereSql = toWhereSql(cartItemIdArray.length);
		/**
		 * 3.生成sql语句
		 */
		String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and "+whereSql;
		/**
		 * 4.执行sql返回List<CartItem>
		 */
		return toCartItemList(qr.query(sql, new MapListHandler(),cartItemIdArray));
	}
	
	/**
	 * 通过购物车条目id查询条目信息
	 * @param cartItemId
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByCartItemId(String cartItemId) throws SQLException{
		String sql = "select * from t_cartItem c, t_book b where c.bid=b.bid and c.cartItemId=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),cartItemId);
		return toCartItem(map);
	}
	
	/**
	 * 批量删除
	 * @param cartItemIds
	 * @throws SQLException
	 */
	public void batchDelete(String cartItemIds) throws SQLException{
		
		/**
		 * 需要把CartItemIds转换为数组
		 * 1.吧cartItemIds转换为where子句
		 * 2.与delete from连接在一起执行
		 */
		//split()按照括号内指定字符截取字符串，并形成数组
		Object[] cartItemIdArray = cartItemIds.split(",");
		String whereSql = toWhereSql(cartItemIdArray.length);
		String sql = "delete from t_cartitem where " + whereSql;
		qr.update(sql,cartItemIdArray);//其中cartItemIdArray必须是Object类型的数组
	}
	
	/**
	 * 查询某个用户的某本图书的购物车条目是否存在
	 * @param uid
	 * @param bid
	 * @return
	 * @throws SQLException
	 */
	public CartItem findByUidAndBid(String uid,String bid) throws SQLException{
		String sql = "select * from t_cartitem where uid=? and bid=?";
		Map<String,Object> map = qr.query(sql, new MapHandler(),uid,bid);
		CartItem cartItem = toCartItem(map);
		return cartItem;
	}
	/**
	 * 修改指定条目数量
	 * @param cartItemId
	 * @param quantity
	 * @throws SQLException
	 */
	public void updateQuantity(String cartItemId,int quantity) throws SQLException{
		String sql = "update t_cartitem set quantity=? where cartItemId=?";
		qr.update(sql,quantity,cartItemId);
	}
	/**
	 * 添加条目
	 * @param cartItem
	 * @return
	 * @throws SQLException 
	 */
	public void addCartItem(CartItem cartItem) throws SQLException{
		String sql = "insert into t_cartitem(cartItemId,quantity,bid,uid)"+
	                 "values(?,?,?,?)";
		Object[] params = {cartItem.getCartItemId(),cartItem.getQuantity(),
				cartItem.getBook().getBid(),cartItem.getUser().getUid()};
		qr.update(sql,params);
	}
	
	/**
	 * 吧一个Map映射为CArtItem
	 * @param map
	 * @return
	 */
	private CartItem toCartItem(Map<String,Object> map){
		if(map==null ||map.size()==0) return null;
		CartItem cartItem = CommonUtils.toBean(map, CartItem.class);
		Book book = CommonUtils.toBean(map, Book.class);
		User user = CommonUtils.toBean(map, User.class);
		cartItem.setBook(book);
		cartItem.setUser(user);
		return cartItem;
	}
	
	
	/**
	 * 把多个Map(List<Map>)映射成多个CartItem（List<CartItem>）
	 * @param mapList
	 * @return
	 */
	private List<CartItem> toCartItemList(List<Map<String,Object>> mapList){
		List<CartItem> cartItemList = new ArrayList<CartItem>();
		for(Map<String,Object> map : mapList){
			CartItem cartItem = toCartItem(map);
			cartItemList.add(cartItem);
		}
		return cartItemList;
	}
	
	/**
	 * 通过用户查询购物车条目
	 * @param uid
	 * @return
	 * @throws SQLException 
	 */
	public List<CartItem> findByUser(String uid) throws SQLException{
		String sql = "select * from t_cartitem c,t_book b where c.bid=b.bid and uid=? order by c.orderBy";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),uid);
		return toCartItemList(mapList);
	}
}
