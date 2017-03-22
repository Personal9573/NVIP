package com.bvip.servlet;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.base.util.BaseServlet;
import com.jdbc.utils.JDBCUtil;
import com.zhht.utils.MyUtils;
@WebServlet(urlPatterns="/announ/*")
public class AnnouncementServlet extends BaseServlet {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public void index(HttpServletRequest request,HttpServletResponse response){
		String sql="select LSH,GGBT,FBSJ from Sys_Announcement where GGLX='0' and GGZT='1' order by FBSJ desc";
		JDBCUtil ju = new JDBCUtil();
		Object[] params={};
		String result=ju.QueryToJson(sql, params);
		MyUtils.sendMessageToBrowser(response, result);
	}
	public void query(HttpServletRequest request,HttpServletResponse response){
		String id=request.getParameter("id");
		String sql="select LSH,GGBT,URL,FBSJ from Sys_Announcement where LSH=?";
		JDBCUtil ju = new JDBCUtil();
		Object[] params={id};
		String result=ju.QueryToJson(sql, params);
		MyUtils.sendMessageToBrowser(response, result);
	}
}
