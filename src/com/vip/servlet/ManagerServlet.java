package com.vip.servlet;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.base.util.BaseServlet;
import com.base.util.PrintUtils;
import com.mlm.utils.JDBCUtil;
import com.mlm.utils.JDBCuTest;

@WebServlet(urlPatterns="/manage/*")
public class ManagerServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7965988820563599405L;
	/**
	 * 查询套餐和剩余天数
	 * @param request
	 * @param response
	 * 2016年12月15日
	 */
	public void QueryByID(HttpServletRequest request,HttpServletResponse response){
		String token=request.getParameter("token");
		if(token==null || !token.equals("2c4589a08c414e3bb12c29ef94098d8b")){
			PrintUtils.sendMessageToPhone(response, "error");
			return;
		}
		String BID=request.getParameter("BID");
		JDBCUtil ju = new JDBCUtil();
		String sql="select *,datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) as num from QDZS_VIP where B_ID=?";
		Map<Object,Object> map=ju.QueryToMap(sql, new Object[]{BID});
		String result=CodeToJson(1,0, map);
		if(map==null){
			result=CodeToJson(0,0, map);
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
	/**
	 * 充值
	 * @param request
	 * @param response
	 * 2016年12月15日
	 */
	public void Recharge(HttpServletRequest request,HttpServletResponse response){
		String token=request.getParameter("token");
		if(token==null || !token.equals("2c4589a08c414e3bb12c29ef94098d8b")){
			PrintUtils.sendMessageToPhone(response, "error");
			return;
		}
		String BID=request.getParameter("BID");
		String MAX_YJ=request.getParameter("MAX_YJ");
		String MONTHS=request.getParameter("MONTHS");
		JDBCUtil ju = new JDBCUtil();
		String sql="update QDZS_VIP set MONTHS=MONTHS+?,MAX_YJ=? where B_ID=?";
		int count=ju.update(sql, new Object[]{MONTHS,MAX_YJ,BID});
		String str="充值失败";
		if(count>0){
			str="充值成功";
		}
		String result=CodeToJson(1, 1, str);
		PrintUtils.sendMessageToPhone(response, result);
	}
	/**
	 * 新开用户
	 * @param request
	 * @param response
	 * 2016年12月15日
	 */
	public void AddUser(HttpServletRequest request,HttpServletResponse response){
		String token=request.getParameter("token");
		if(token==null || !token.equals("2c4589a08c414e3bb12c29ef94098d8b")){
			PrintUtils.sendMessageToPhone(response, "error");
			return;
		}
		String BID=request.getParameter("BID");
		String MAX_YJ=request.getParameter("MAX_YJ");
		String MONTHS=request.getParameter("MONTHS");
		String VERSION=request.getParameter("VERSION");
		JDBCUtil ju = new JDBCUtil();
		Connection conn=JDBCuTest.getConnection();
		String str="添加新用户失败";
		try{
			String sql="select B_ID from blacklist where B_ID=?";
			boolean has=ju.queryhas(sql, new Object[]{BID}, conn);
			if(!has){
				sql="select B_ID from QDZS_VIP where B_ID=?";
				has=ju.queryhas(sql, new Object[]{BID}, conn);
				if(!has){
					sql="insert into QDZS_VIP (B_ID,MAX_YJ,SHZT,TJSJ,SHSJ,MONTHS,VERSION) values(?,?,1,convert(varchar(20),getdate(),120),convert(varchar(20),getdate(),120),?,?)";
					int count=ju.update(sql, new Object[]{BID,MAX_YJ,MONTHS,VERSION},conn);
					if(count>0){
						str="添加新用户成功";
						conn.commit();
					}
					else{
						conn.rollback();
					}
				}
				else{
					str="VIP用户,试图重复添加";
				}
			}
			else{
				str="用户是小黑";
			}
		}
		catch(Exception e){
			str="服务器异常";
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		String result=CodeToJson(1, 1, str);
		PrintUtils.sendMessageToPhone(response, result);
	}
	/**
	 * code:0-fail,1-success
	 * type:0-query,1-update
	 * @param code
	 * @param smap
	 * @return
	 * 2016年12月15日
	 */
	public String CodeToJson(int code,int type,Object obj){
		Map<String,Object> map=new HashMap<String,Object>();
		if(code==0){
			map.put("ret", "操作失败");
		}
		else{
			map.put("ret", "操作成功");
		}
		if(type==0){
			map.put("type", "查询操作");
		}
		else{
			map.put("type", "更新操作");
		}
		map.put("result", obj);
		return JSONObject.fromObject(map).toString();
	}
}
