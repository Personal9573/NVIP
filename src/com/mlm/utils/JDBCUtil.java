package com.mlm.utils;


import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *  2015-11-4 下午02:06:34 
 *  @author ZHHT class
 *  Description:操作数据库相关函数封装。返回数据类型主要有json和字符串数组
 *  			以查询为主
 *  
 *  外部传入conn的，需要在外部调用本类的close(conn)方法
 *  
 *  
 *  单行（列数忽略不计）
 *  多行（列数忽略不计）
 */

public class JDBCUtil{

	private ResultSet rs = null;
	private PreparedStatement ps=null;

	/**
	 * 
	 * 2015-11-19 下午01:12:11
	 * 
	 * @author ZHHT method Description:return a object 
	 * params:1.sql 2. params 3.object name 
	 * return:entity object
	 * @throws SQLException 
	 * 面向后台为主
	 * 
	 * 单行记录返回到Bean
	 */
	public <T> T Query2Object(String sql, Object[] params,Class<T> clazz,Connection conn)
	{
		Method[] methods = clazz.getDeclaredMethods();
		ArrayList<Method> am = new ArrayList<Method>();
		for (Method meth : methods) {
			if (meth.toString().contains("set")) {
				am.add(meth);
			}
		}
		T t=null;
		try {
			t=clazz.newInstance();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				for (Method meth : am) {
					for (int i = 1; i <= count; i++) {
						if (compareTwoFileds(meth.toString(), rsmd
								.getColumnLabel(i), "set")) {
							try {
								meth.invoke(t, rs.getObject(i));
							} catch (IllegalArgumentException e) {
								System.out.println(meth.toString());
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}

						}
					}
				}
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return t;
	}
	public <T> T Query2Object(String sql, Object[] params,Class<T> clazz){
		Connection conn=JDBCuTest.getConnection();
		Method[] methods = clazz.getDeclaredMethods();
		ArrayList<Method> am = new ArrayList<Method>();
		for (Method meth : methods) {
			if (meth.toString().contains("set")) {
				am.add(meth);
			}
		}
		T t = null;
		try {
			t = clazz.newInstance();
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				for (Method meth : am) {
					for (int i = 1; i <= count; i++) {
						if (compareTwoFileds(meth.toString(), rsmd
								.getColumnLabel(i), "set")) {
							try {
								meth.invoke(t, rs.getObject(i));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
						}
					}
				}
			}
			
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			close(conn);
		}
		
		return t;
	}
	/**
	 * 
	 * @param sql
	 * @param params
	 * @param clazz
	 * @return 多行记录到多个对象
	 * 2016年4月12日
	 */
	public <T> List<T> Query2ListObject(String sql, Object[] params,Class<T> clazz)
	{
		Connection conn=JDBCuTest.getConnection();
		Method[] methods = clazz.getDeclaredMethods();
		ArrayList<Method> am = new ArrayList<Method>();
		for (Method meth : methods) {
			if (meth.toString().contains("set")) {
				am.add(meth);
			}
		}
		List<T> listo= new ArrayList<T>();
		T tx = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				try {
					tx = (T) clazz.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (Method meth : am) {
					for (int i = 1; i <= count; i++) {
						if (compareTwoFileds(meth.toString(), rsmd
								.getColumnLabel(i), "set")) {
							try {
								if(rs.getObject(i)!=null){
									meth.invoke(tx, rs.getObject(i));
								}
								else{
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
						}
					}
				}
				
				listo.add(tx);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			close(conn);
		}
		
		return listo;
	}
	/**
	 * 
	 * @author zhht 2015-12-4 下午03:50:56
	 * @param
	 * @return List<T>
	 * @Description  retArrayObject
	 * 面向后台
	 */
		public <T> List<T> Query2ListObject(String sql, Object[] params,Class<T> clazz,Connection conn)
		{
			Method[] methods = clazz.getDeclaredMethods();
			ArrayList<Method> am = new ArrayList<Method>();
			for (Method meth : methods) {
				if (meth.toString().contains("set")) {
					am.add(meth);
				}
			}
			List<T> listo= new ArrayList<T>();
			T tx = null;
			try {
				ps = conn.prepareStatement(sql);
				for (int i = 0; i < params.length; i++) {
					ps.setObject(i + 1, params[i]);
				}
				rs = ps.executeQuery();
				ResultSetMetaData rsmd = rs.getMetaData();
				int count = rsmd.getColumnCount();
				while (rs.next()) {
					try {
						tx = clazz.newInstance();
					} catch (InstantiationException e1) {
						e1.printStackTrace();
					} catch (IllegalAccessException e1) {
						e1.printStackTrace();
					}
					for (Method meth : am) {
						for (int i = 1; i <= count; i++) {
							if (compareTwoFileds(meth.toString(), rsmd
									.getColumnLabel(i), "set")) {
								try {
									if(rs.getObject(i)!=null){
										meth.invoke(tx, rs.getObject(i));
									}
									else{
									}
								} catch (IllegalArgumentException e) {
									e.printStackTrace();
								} catch (IllegalAccessException e) {
									e.printStackTrace();
								} catch (InvocationTargetException e) {
									e.printStackTrace();
								}

							}
						}
					}
					
					listo.add(tx);
				}

			} catch (SQLException e) {
				e.printStackTrace();
			}
			return listo;
		}
	/**
	 * 
	 * 2015-11-19 下午01:08:27
	 * 
	 * @author ZHHT method Description: 
	 * params:1.class of string 2.field of string 3."set" or "get" 
	 * return:field is contains class of substring
	 * 
	 */
	private boolean compareTwoFileds(String clastr, String fieldstr,
			String fieldBegin) {
		int start = clastr.indexOf(fieldBegin);
		int end = clastr.indexOf('(', start);
		String temp = clastr.substring(start + 3, end);
		if(temp.length()==fieldstr.length()){
			return fieldstr.toLowerCase().equals(temp.toLowerCase());
		}
		else {
			return false;
		}
	}
	
	/**
	 *  
	 *  2015-11-4 下午02:07:29 
	 *  @author ZHHT method
	 *  Description:面向前台JSON
	 */
	public String QueryToJson(String sql,Object[] params,Connection conn){
		String result=null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
				result=RsToJson.resultSetToJson(rs);
//				System.out.println(result);
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	public String QueryToJson(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		String result=null;
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					ps.setObject(i+1, params[i]);
				}
			}
			rs = ps.executeQuery();
			result=RsToJson.resultSetToJson(rs);
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return result;
	}
	public Map<Object,Object> QueryToMap(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		Map<Object,Object> map=new HashMap<Object,Object>();
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					ps.setObject(i+1, params[i]);
				}
			}
			rs = ps.executeQuery();
			map=RsToJson.resultSetToMap(rs);
		} catch (Exception e) {
			map=null;
		} finally {
			close(conn);
		}
		return map;
	}
	public Map<Object,Object> QueryToMap(String sql,Object[] params,Connection conn){
		Map<Object,Object> map=new HashMap<Object,Object>();
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					ps.setObject(i+1, params[i]);
				}
			}
			rs = ps.executeQuery();
			map=RsToJson.resultSetToMap(rs);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return map;
	}
	public List<Map<String,Object>> QueryToListMap(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		List<Map<String,Object>> result=null;
		try {
			ps = conn.prepareStatement(sql);
			if(params!=null){
				for(int i=0;i<params.length;i++){
					ps.setObject(i+1, params[i]);
				}
			}
			rs = ps.executeQuery();
			//	if(rs.next()){
			//rs.first();
			result=RsToJson.resultSetToListMap(rs);
			//	}
			//	else{
			//		result=null;
			//	}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return result;
	}

	/**
	 *
	 *  2015-11-4 下午02:10:25 
	 *  @author ZHHT method
	 *  Description:面向后台 数值组 
	 *  单行记录到值
	 */
	public Object[] QueryToValue(String sql,Object[] params,Connection conn){
		
		Object[] result=null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
/*			if(rs.next()){
				rs.beforeFirst();*/
				result=RsToJson.resultToStringArray(rs);
/*			}
			else{
				result=null;
			}*/
		} catch (Exception e) {
			e.printStackTrace();
		} 
		return result;
	}
	public Object[] QueryToValue(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		Object[] result=null;
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
//			if(rs.next()){
				//rs.beforeFirst();
			result=RsToJson.resultToStringArray(rs);
//			}
//			else{
//				result=null;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return result;
	}
	/**
	 * 
	 * @param sql
	 * @param params
	 * @return 多行记录到值 List<Object[]>
	 * 2016年4月12日
	 */
	public List<Object[]> QueryToListValue(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		List<Object[]> result=new ArrayList<Object[]>();
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
//			if(rs.next()){
			//rs.beforeFirst();
			result=RsToJson.resultToArray(rs);
//			}
//			else{
//				result=null;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return result;
	}
	public List<Object[]> QueryToListValue(String sql,Object[] params,Connection conn){
		List<Object[]> result=new ArrayList<Object[]>();
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
//			if(rs.next()){
			//rs.beforeFirst();
			result=RsToJson.resultToArray(rs);
//			}
//			else{
//				result=null;
//			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return result;
	}
	/**
	 * 
	 *  2015-11-12 下午02:44:03 
	 *  @author ZHHT method
	 *  Description:Insert or update one table whatever fields,return 0 is fail.
	 *  面向数据库
	 */
	public int update(String sql,Object[] params,Connection conn){
		
		int i=0;
		try {
			ps = conn.prepareStatement(sql);
			for(int j=0;j<params.length;j++){
				ps.setObject(j+1, params[j]);
			}
			i = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
			String result="";
			for(int j=0;j<params.length;j++){
				result+=params[j].toString()+"《 		》";
			}
			System.out.println(result);
		}
		return i;
	}
	public int update(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		int i=0;
		try {
			ps = conn.prepareStatement(sql);
			for(int j=0;j<params.length;j++){
				ps.setObject(j+1, params[j]);
			}
			i = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		finally{
			close(conn);
		}
		return i;
	}
	public int update(String sql,List<Object> params,Connection conn){
		int i=0;
		try {
			ps = conn.prepareStatement(sql);
			for(int j=0;j<params.size();j++){
				ps.setObject(j+1, params.get(j));
			}
			i = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return i;
	}
	public int update(String sql,List<Object> params){
		Connection conn=JDBCuTest.getConnection();
		int i=0;
		try {
			ps = conn.prepareStatement(sql);
			for(int j=0;j<params.size();j++){
				ps.setObject(j+1, params.get(j));
			}
			i = ps.executeUpdate();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return i;
	}
	/**
	 * 
	 *  2015-11-12 下午03:16:26 
	 *  @author ZHHT method
	 *  Description:query has return boolean.
	 *  判断存在
	 *  sql for example:"select id from ...."
	 */
	public boolean queryhas(String sql,Object[] params,Connection conn){
		try{
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		}
		catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
	public boolean queryhas(String sql,Object[] params){
		Connection conn=JDBCuTest.getConnection();
		try {
			ps = conn.prepareStatement(sql);
			for(int i=0;i<params.length;i++){
				ps.setObject(i+1, params[i]);
			}
			rs = ps.executeQuery();
			if(rs.next()){
				return true;
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			close(conn);
		}
		return false;
	}
 /**
  * 
  * @author zhht 2015-12-4 下午03:52:13
  * @param
  * @return void
  * @Description
  */
	public  void close(Connection conn){
		if(rs!=null){
			try {
				rs.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(ps!=null){
			try {
				ps.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		if(conn!=null){
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
	}
	
	//------------------以下方法不再使用-------2016-4-12----------------------//
	/**
	 * description: query ret all object from reflect tec
	 * 	暂时不用，保留
	 * @param sql
	 * @param obj
	 * @return
	 */
	@Deprecated
	public List<Object> retArrayList(String sql,Object[] obj,Connection conn){
		List<Object> lobj = null;
		
			try {
				ps=conn.prepareStatement(sql);
				for(int i=0;i<obj.length;i++){
					ps.setObject(i+1, obj[i]);
				}
				rs=ps.executeQuery();
/*				if(rs.next()){
					rs.beforeFirst();*/
					lobj=RsToJson.resultSetToArray(rs);
//				}
			} catch (SQLException e) {
				e.printStackTrace();
			}
		return lobj;
		
	}
	@Deprecated
	public List<Object> retArrayList(String sql,Object[] obj){
		Connection conn=JDBCuTest.getConnection();
		List<Object> lobj = null;
		
		try {
			ps=conn.prepareStatement(sql);
			for(int i=0;i<obj.length;i++){
				ps.setObject(i+1, obj[i]);
			}
			rs=ps.executeQuery();
/*			if(rs.next()){
				rs.beforeFirst();*/
				lobj=RsToJson.resultSetToArray(rs);
//			}
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			close(conn);
		}
		return lobj;
		
	}
	
	//返回一条记录作为一个对象
	@Deprecated
	public <T> T retobj(String sql, Object[] params,T t){
		Connection conn=JDBCuTest.getConnection();
		Class<?> clazz = t.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		ArrayList<Method> am = new ArrayList<Method>();
		for (Method meth : methods) {
			if (meth.toString().contains("set")) {
				am.add(meth);
			}
		}
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				for (Method meth : am) {
					for (int i = 1; i <= count; i++) {
						if (compareTwoFileds(meth.toString(), rsmd
								.getColumnLabel(i), "set")) {
							try {
								meth.invoke(t, rs.getObject(i));
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
						}
					}
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			close(conn);
		}
		
		return t;
	}
	@SuppressWarnings("unchecked")
	@Deprecated
	public <T> List<T> retArrayObject(String sql, Object[] params,T t)
	{
		Connection conn=JDBCuTest.getConnection();
		Class<?> clazz = t.getClass();
		Method[] methods = clazz.getDeclaredMethods();
		ArrayList<Method> am = new ArrayList<Method>();
		for (Method meth : methods) {
			if (meth.toString().contains("set")) {
				am.add(meth);
			}
		}
		List<T> listo= new ArrayList<T>();
		T tx = null;
		try {
			ps = conn.prepareStatement(sql);
			for (int i = 0; i < params.length; i++) {
				ps.setObject(i + 1, params[i]);
			}
			rs = ps.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int count = rsmd.getColumnCount();
			while (rs.next()) {
				try {
					tx = (T) clazz.newInstance();
				} catch (InstantiationException e1) {
					e1.printStackTrace();
				} catch (IllegalAccessException e1) {
					e1.printStackTrace();
				}
				for (Method meth : am) {
					for (int i = 1; i <= count; i++) {
						if (compareTwoFileds(meth.toString(), rsmd
								.getColumnLabel(i), "set")) {
							try {
								if(rs.getObject(i)!=null){
									meth.invoke(tx, rs.getObject(i));
								}
								else{
								}
							} catch (IllegalArgumentException e) {
								e.printStackTrace();
							} catch (IllegalAccessException e) {
								e.printStackTrace();
							} catch (InvocationTargetException e) {
								e.printStackTrace();
							}
							
						}
					}
				}
				
				listo.add(tx);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		finally{
			close(conn);
		}
		
		return listo;
	}

}
