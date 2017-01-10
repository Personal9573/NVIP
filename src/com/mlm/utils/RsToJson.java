package com.mlm.utils;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import net.sf.json.JSONArray;

/**
 * 20151021
 * 
 * @author zhht
 * 
 */
public class RsToJson {
	
	public static Map<Object,Object> resultSetToMap(ResultSet rs){
		ResultSetMetaData metaData;
		int columnCount;
		String columnName=null;
		Object value=null;
		String strvalue=null;
		Map<Object, Object> mapjson = new ConcurrentHashMap<Object, Object>();
		try {
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while (rs.next()) {
				for (int i = 1; i <= columnCount; i++) {
					columnName = metaData.getColumnLabel(i);
					value = rs.getObject(i);
					if(value!=null){
						strvalue=value.toString();
					}
					else{
						strvalue="";
					}
					mapjson.put(columnName, strvalue);
				}

			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return mapjson;
	}

	public static String resultSetToJson(ResultSet rs) {
		// 鑾峰彇鍒楁暟
		ResultSetMetaData metaData;
		JSONArray array=new JSONArray();
		int columnCount;
		String columnName=null;
		Object value=null;
		String strvalue=null;
		try {
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<Object, Object> mapjson = new ConcurrentHashMap<Object, Object>();
				for (int i = 1; i <= columnCount; i++) {
					columnName = metaData.getColumnLabel(i);
					value = rs.getObject(i);
					if(value!=null){
						strvalue=value.toString();
					}
					else{
						strvalue="";
					}
					mapjson.put(columnName, strvalue);
				}
				array.add(mapjson);

			}
		
		} catch (SQLException e) {
			e.printStackTrace();
		}
			return array.toString();
	}
	public static List<Map<String,Object>> resultSetToListMap(ResultSet rs) {
		ResultSetMetaData metaData;
		List<Map<String,Object>> list=new ArrayList<Map<String,Object>>();
		int columnCount;
		String columnName=null;
		Object value=null;
		String strvalue=null;
		try {
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<String, Object> mapjson = new ConcurrentHashMap<String, Object>();
				for (int i = 1; i <= columnCount; i++) {
					columnName = metaData.getColumnLabel(i);
					value = rs.getObject(i);
					if(value!=null){
						strvalue=value.toString();
					}
					else{
						strvalue="";
					}
					mapjson.put(columnName, strvalue);
				}
				list.add(mapjson);
				
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return list;
	}
	public static List<Object> resultSetToArray(ResultSet rs) {
		List<Object> list=new ArrayList<Object>();
		ResultSetMetaData metaData;
		int columnCount;
		try {
			metaData = rs.getMetaData();
			columnCount = metaData.getColumnCount();
			while (rs.next()) {
				Map<Object, Object> mapjson = new ConcurrentHashMap<Object, Object>();
				for (int i = 1; i <= columnCount; i++) {
					String columnName = metaData.getColumnLabel(i);
					Object value = rs.getObject(i);
					if(value!=null){
						value=value.toString();
					}
					else{
						value="";
					}
					mapjson.put(columnName, value);
				}

				list.add(mapjson);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}

		return list;
	}
	public static Object[] resultToStringArray(ResultSet rs){
		ResultSetMetaData metaData;
		int columnCount=0;
		Object[] obj = null;
		try {
			metaData=rs.getMetaData();
			columnCount=metaData.getColumnCount();
			obj=new Object[columnCount];
			while(rs.next()){
				for(int i=1;i<=columnCount;i++){
					obj[i-1]=rs.getObject(i);
				}
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}
	public static List<Object[]> resultToArray(ResultSet rs){
		ResultSetMetaData metaData;
		int columnCount=0;
		List<Object[]> obj = new ArrayList<Object[]>();
		try {
			metaData=rs.getMetaData();
			columnCount=metaData.getColumnCount();
			while(rs.next()){
				Object[] arr=new Object[columnCount];
				for(int i=1;i<=columnCount;i++){
					arr[i-1]=rs.getObject(i);
				}
				obj.add(arr);
			}
			
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return obj;
	}
}
