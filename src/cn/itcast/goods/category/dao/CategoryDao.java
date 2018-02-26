package cn.itcast.goods.category.dao;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.MapListHandler;

import cn.itcast.commons.CommonUtils;
import cn.itcast.goods.category.domain.Category;
import cn.itcast.jdbc.TxQueryRunner;

/**
 * 分类持久层
 * @author TH
 *
 */
public class CategoryDao {
	private QueryRunner qr = new TxQueryRunner();
	
	/**
	 * 将一个map转换为Category
	 * @param map
	 * @return
	 */
	private Category toCategory(Map<String,Object> map){
		/*
		 * map(cid:xx,cname:xx,pid:xx,desc:xx,orderBy:xx)
		 * Category(cid:xx,cname:xx,parent:(cid=pid),desc:xx)
		 */
		Category category = CommonUtils.toBean(map, Category.class);
		String pid = (String) map.get("pid");
		if(pid!=null){//如果父分类id不为空
			/**
			 * 使用一个父分类对象来拦截pid
			 * 再把父分类设置给category
			 */
			Category parent = new Category();
			parent.setCid(pid);
			category.setParent(parent);
		}
		return category;
	}
	/**
	 * 可以把多个Map(List<Map>)映射成为多个Category(List<Category>)
	 * @param mapList
	 * @return
	 */
	private List<Category> toCategoryList(List<Map<String,Object>> mapList){
		List<Category> categoryList = new ArrayList<Category>();
		for(Map<String,Object> map:mapList){
			Category c = toCategory(map);
			categoryList.add(c);
		}
		return categoryList;
	}
	/**
	 * 返回所有分类
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findAll() throws SQLException{
		/**
		 * 1.查询出所有的一级分类
		 * pid为null的是一级分类
		 */
		String sql = "select * from t_category where pid is null";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler());
		
		List<Category> parents = toCategoryList(mapList);
		/**
		 * 2.循环遍历所有一级分类，为每一个一级分类加载他的二级分类
		 */
		for(Category parent:parents){
			//查询当前父分类的所有子分类
			List<Category> children = findByParent(parent.getCid());
			//设置给父分类
			parent.setChildren(children);
		}
		return parents;
		
	}
	/**
	 * 通过父分类查询子分类
	 * @param pid
	 * @return
	 * @throws SQLException 
	 */
	public List<Category> findByParent(String pid) throws SQLException{
		//查询所有pid存在的信息，pid存在为二级分类即子分类
		String sql = "select * from t_category where pid=?";
		List<Map<String,Object>> mapList = qr.query(sql, new MapListHandler(),pid);
		return toCategoryList(mapList);
	}
}
