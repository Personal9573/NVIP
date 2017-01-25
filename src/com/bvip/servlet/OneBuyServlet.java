package com.bvip.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.base.util.BaseServlet;
import com.base.util.PrintUtils;
import com.mlm.utils.JDBCUtil;

public class OneBuyServlet extends BaseServlet {
	public void index(HttpServletRequest request,HttpServletResponse response){
		String sql="select top 20 * from notice order by ptime desc";
		JDBCUtil ju = new JDBCUtil();
		String result=null;
		try{
			result=ju.QueryToJson(sql, new Object[]{});
		}
		catch(Exception e){
			e.printStackTrace();
			result=null;
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
}
