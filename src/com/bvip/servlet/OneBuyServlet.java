package com.bvip.servlet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.base.util.BaseServlet;
import com.base.util.PrintUtils;
import com.mlm.utils.JDBCUtil;

public class OneBuyServlet extends BaseServlet {
	public void index(HttpServletRequest request,HttpServletResponse response){
		String sql="select top 20 * from BuyWhat order by ptime desc";
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
	public void Order(HttpServletRequest request,HttpServletResponse response){
		//判断是否满员
			//未满员
				//买家账户扣款+资金明细
				//平台账户加款+资金明细
				//生成购买记录
				//更新商品数据（商品可购买次数-1）
			//满员
				//提示满员
	}
	
}
