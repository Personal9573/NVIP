package com.bvip.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.base.util.BaseServlet;
import com.base.util.PrintUtils;
import com.jdbc.utils.JDBCUtil;

@WebServlet(urlPatterns="/notice/*")
public class NoticeServlet extends BaseServlet {

	public void query(HttpServletRequest request,HttpServletResponse response){
		String params=request.getParameter("params");
		String[] pa=params.split(",");
		int page=0;
		String sql="select * from notice Offset ? rows fetch next 20 rows only";
		String result=null;
		try{
			page=Integer.valueOf(pa[0]);
			page=(page-1)*20;
			JDBCUtil ju = new JDBCUtil();
			result=ju.QueryToJson(sql, new Object[]{page});
		}
		catch(Exception e){
			e.printStackTrace();
			result=null;
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
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
	public void publish(HttpServletRequest request,HttpServletResponse response){
		String sql="insert into notice() values()";
		JDBCUtil ju = new JDBCUtil();
		int count=0;
		try{
			count=ju.update(sql, new Object[]{});
		}
		catch(Exception e){
			e.printStackTrace();
			count=0;
		}
		PrintUtils.sendMessageToPhone(response, count);
	}
}
