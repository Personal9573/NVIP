package com.jdbc.utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

public class JDBCuTest {
//		private static DataSource dataSource1 = new ComboPooledDataSource();
		private static DataSource dataSource=getDataSource();
		private static ThreadLocal<Connection> tl = new ThreadLocal<Connection>();
		public static DataSource getDataSource() {
			Context ctx;
			try {
				ctx = new InitialContext();
				dataSource=(DataSource) ctx.lookup("java:comp/env/jdbc/zhht");
			} catch (NamingException e) {
				e.printStackTrace();
			}
			return dataSource;
		}

		public static Connection getConnection() {
			Connection con=tl.get();
			try {
				if(con!=null){
					return con;
				}
				return dataSource.getConnection();
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
		
		public static void beginTranscation() throws SQLException {
			Connection con = tl.get();
			if(con != null) {
				throw new SQLException("事务已经开启，在没有结束当前事务时，不能再开启事务！");
			}
			con = dataSource.getConnection();
			con.setAutoCommit(false);
			tl.set(con);
		}
		
		public static void commitTransaction() throws SQLException {
			Connection con = tl.get();
			if(con == null) {
				throw new SQLException("当前没有事务，所以不能提交事务！");
			}
			con.commit();
			con.close();
			tl.remove();
		}
		
		public static void rollbackTransaction() throws SQLException {
			Connection con = tl.get();
			if(con == null) {
				throw new SQLException("当前没有事务，所以不能回滚事务！");
			}
			con.rollback();
			con.close();
			tl.remove();
		}
}
