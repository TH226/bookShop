package cn.itcast.goods.user.web.servlet;

import java.sql.SQLException;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.commons.dbutils.handlers.ScalarHandler;

import cn.itcast.jdbc.TxQueryRunner;

public class test {
	public static void main(String[] args) throws SQLException {
		System.out.println("begin...");
		QueryRunner qr = new TxQueryRunner();
		System.out.println("begin2...");
		String sql = "select count(1) from t_user where loginname=?";
		System.out.println("begin3...");
		Number number = (Number) qr.query(sql, new ScalarHandler(),"zhangSan");
	    System.out.println(number);
	    System.out.println("end....");
	}
}
