package com.vip.servlet;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.Random;

import javax.naming.NamingException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.base.util.BaseServlet;
import com.base.util.PrintUtils;
import com.mlm.entity.UserReg;
import com.mlm.utils.JDBCUtil;
import com.mlm.utils.JDBCuTest;

@WebServlet(urlPatterns="/qdzs/*")
public class QdzsServlet extends BaseServlet {

	/**
	 * 
	 */
	private static final long serialVersionUID = 8549572952888468416L;

	public void login(HttpServletRequest request,HttpServletResponse response) throws NamingException{
		String params=request.getParameter("params");
		String[] pa=params.split(",");
		//select *,datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) from QDZS_VIP order by datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ))
		String sql="select q.B_ID,q.MAX_YJ,q.VERSION,datediff(day,getdate(),dateadd(day,q.MONTHS*30,SHSJ)) as SYTS from UserReg u,QDZS_VIP q where u.U_ID=q.B_ID and u.SJHM=? and u.YHMM=? and datediff(second,getdate(),dateadd(day,q.MONTHS*30,SHSJ))>0 and SHZT='1'";
		JDBCUtil ju = new JDBCUtil();
		Object[] obj=ju.QueryToValue(sql, new Object[]{pa[0],pa[1]});
		UserReg userreg=new UserReg();
		if(obj[0]!=null){
			userreg.setU_ID(new BigDecimal(obj[0].toString()));
			request.getSession().setAttribute("UserReg", userreg);
			PrintUtils.sendMessageToPhone(response, obj[1]+","+obj[2]+","+obj[3]);
		}
		else{
			PrintUtils.sendMessageToPhone(response, "wcz");
		}
	}
	public void big(HttpServletRequest request,HttpServletResponse response) throws NamingException{
		UserReg userreg=(UserReg) request.getSession().getAttribute("UserReg");
		Object B_ID=userreg.getU_ID();
		Connection conn=JDBCuTest.getConnection();
		String params=request.getParameter("params");
		String[] pa=params.split(",");
		String result="r2d0";
		try{
			conn.setAutoCommit(false);
			result=sub_big(pa,B_ID,conn);
			if(result.equals("r2d0")){
				conn.rollback();
			}
			else{
				conn.commit();
			}
		}
		catch(Exception e){
			try {
				conn.rollback();
			} catch (SQLException e1) {
				e1.printStackTrace();
			}
			PrintUtils.sendMessageToPhone(response, "r2d0");
		}
		finally{
			try {
				conn.close();
			} catch (SQLException e) {
				e.printStackTrace();
			}
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
	private String sub_big(String[] pa,Object B_ID,Connection conn){
		JDBCUtil ju = new JDBCUtil();
		String sql="select B_ID from IsBigBlack where B_ID=?";
		boolean has=ju.queryhas(sql, new Object[]{B_ID}, conn);
		String result="r2d0";
		int count=0;
		if(has){
			sql="insert into IsBig (B_ID,DAYS,ISWIN,TJSJ) values(?,?,0,convert(varchar(20),getdate(),120))";
			count=ju.update(sql, new Object[]{B_ID,-Integer.valueOf(pa[0])}, conn);
			if(count<=0){
				return "r2d0";
			}
			sql="update QDZS_VIP set SHSJ=convert(varchar(20),dateadd(day,?,SHSJ),120) where B_ID=?";
			count=ju.update(sql, new Object[]{-Integer.valueOf(pa[0]),B_ID}, conn);
			if(count<=0){
				return "r2d0";
			}
			sql="select datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) as SYTS from QDZS_VIP where B_ID=?";
			Object[] day=ju.QueryToValue(sql, new Object[]{B_ID}, conn);
			result="r0d"+day[0].toString();
		}
		else{
			sql="select isnull(sum(DAYS),0) as WINDAY from IsBig";
			Object[] obj=ju.QueryToValue(sql, new Object[]{}, conn);
			if(Integer.valueOf(obj[0].toString())>30){
				sql="insert into IsBig (B_ID,DAYS,ISWIN,TJSJ) values(?,?,0,convert(varchar(20),getdate(),120))";
				count=ju.update(sql, new Object[]{B_ID,-Integer.valueOf(pa[0])}, conn);
				if(count<=0){
					return "r2d0";
				}
				sql="update QDZS_VIP set SHSJ=convert(varchar(20),dateadd(day,?,SHSJ),120) where B_ID=?";
				count=ju.update(sql, new Object[]{-Integer.valueOf(pa[0]),B_ID}, conn);
				if(count<=0){
					return "r2d0";
				}
				sql="select datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) as SYTS from QDZS_VIP where B_ID=?";
				Object[] day=ju.QueryToValue(sql, new Object[]{B_ID}, conn);
				result="r0d"+day[0].toString();
			}
			else{
				double ret=0.5;
				boolean tag=false;
				sql="select sum(DAYS) as ds from [dbo].[IsBig] where B_ID=?";
				Object[] bid_obj=ju.QueryToValue(sql, new Object[]{B_ID}, conn);
				if(bid_obj[0]!=null){
					if(Integer.valueOf(bid_obj[0].toString())>30){
						ret=ret/4;
						tag=true;
					}
				}
				int big=Integer.valueOf(pa[1]);
				int days=Integer.valueOf(pa[0]);
				boolean is_big=true;
				if(days>=10 || tag){
					int months=days/10;
					if(days<=30){
						for(int j=1;j<months;j++){
							ret=ret/2;
						}
					}
					else if(days<=60){
						for(int j=1;j<months;j++){
							ret=ret/4;
						}
					}
					else{
						for(int j=1;j<months;j++){
							ret=ret/8;
						}
					}
					is_big=Math.random()>ret;
				}
				else{
					Random r=new Random();
					is_big=r.nextInt(2)!=big;
				}
				if(is_big){
					sql="insert into IsBig (B_ID,DAYS,ISWIN,TJSJ) values(?,?,0,convert(varchar(20),getdate(),120))";
					count=ju.update(sql, new Object[]{B_ID,-Integer.valueOf(pa[0])}, conn);
					if(count<=0){
						return "r2d0";
					}
					sql="update QDZS_VIP set SHSJ=convert(varchar(20),dateadd(day,?,SHSJ),120) where B_ID=?";
					count=ju.update(sql, new Object[]{-Integer.valueOf(pa[0]),B_ID}, conn);
					if(count<=0){
						return "r2d0";
					}
					sql="select datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) as SYTS from QDZS_VIP where B_ID=?";
					Object[] day=ju.QueryToValue(sql, new Object[]{B_ID}, conn);
					result="r0d"+day[0].toString();
				}
				else{
					sql="insert into IsBig (B_ID,DAYS,ISWIN,TJSJ) values(?,?,0,convert(varchar(20),getdate(),120))";
					count=ju.update(sql, new Object[]{B_ID,Integer.valueOf(pa[0])}, conn);
					if(count<=0){
						return "r2d0";
					}
					sql="update QDZS_VIP set SHSJ=convert(varchar(20),dateadd(day,?,SHSJ),120) where B_ID=?";
					count=ju.update(sql, new Object[]{Integer.valueOf(pa[0]),B_ID}, conn);
					if(count<=0){
						return "r2d0";
					}
					sql="select datediff(day,getdate(),dateadd(day,MONTHS*30,SHSJ)) as SYTS from QDZS_VIP where B_ID=?";
					Object[] day=ju.QueryToValue(sql, new Object[]{B_ID}, conn);
					result="r1d"+day[0].toString();
				}
			}
		}
		return result;
	}
	public void KeepCONN(HttpServletRequest request,HttpServletResponse response) throws NamingException{
		UserReg userreg=(UserReg) request.getSession().getAttribute("UserReg");
		Object B_ID=userreg.getU_ID();
		String result="script";
		if(B_ID==null){
			result="script";
		}
		else{
			String sql="select count(1) as num from B_Order where DDZT=0 and B_ID=?";
			JDBCUtil ju = new JDBCUtil();
			Object[] count=ju.QueryToValue(sql, new Object[]{B_ID});
			result=count[0].toString();
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
	public void place(HttpServletRequest request,HttpServletResponse response){
		UserReg userreg=(UserReg) request.getSession().getAttribute("UserReg");
		Object B_ID=userreg.getU_ID();
		if(B_ID==null){
			PrintUtils.sendMessageToPhone(response, "script");
			return;
		}
		String params=request.getParameter("params");
		String[] pa=params.split(",");
		String result = null;
		try {
			result = place(B_ID.toString(),pa[0],pa[1],pa[2],pa[3]);
		} catch (SQLException e) {
			e.printStackTrace();
		}
		PrintUtils.sendMessageToPhone(response, result);
	}
	public static String place(String u_ID,String min,String max,String computer,String money) throws SQLException{
		int count=0;
		Object[] pramas = {u_ID};
		JDBCUtil ju = new JDBCUtil();
		Connection conn=JDBCuTest.getConnection();
		conn.setAutoCommit(false);
		String ret="0";
		try{
			String has_sql="select B_ID from B_Order where DDZT='0' and B_ID not in (26430,28120,31188) and B_ID=?";
			boolean has=ju.queryhas(has_sql, pramas, conn);
			if(has){
				ret="1";
			}
			else{
				String syds_sql="select MTRWDS,SJRSF,SJRNL,SJRXB,SJRXY,isnull(TQZ,0) as TQZ,isnull(GWPJ,0) as GWPJ from Buyer where B_ID=?";
				String rwlx;
				String sid_sql="select distinct b.S_ID from dbo.v_XDSID b,S_Shop s where b.WWID=s.WWID and b.B_ID=? and datediff(day,b.XDSJ,getdate())<=isnull(s.JDJG,20)";
				if(money.equals("0")){
					if(computer.equals("0")){
						rwlx=" RWLX='1' and ";
					}
					else{
						rwlx=" RWLX in ('1','6') and ";
					}
					sid_sql="SELECT distinct S_ID FROM B_Order WHERE B_ID = ?  AND DATEDIFF(day,xdsj,GETDATE())<=1";
					syds_sql="select MTLLDS,SJRSF,SJRNL,SJRXB,SJRXY,isnull(TQZ,0) as TQZ,isnull(GWPJ,0) as GWPJ from Buyer where B_ID=?";
				}
				else{
					if(computer.equals("0")){
						rwlx=" RWLX in ('0','2','4','5') and ";
					}
					else{
						rwlx=" RWLX in ('0','2','3','4','5') and ";
					}
				}
				Object[] buyer_info=ju.QueryToValue(syds_sql, pramas,conn);	
				if(buyer_info[0]==null){
					ret="请更新购物评级";
				}
				else{
					if(buyer_info[0].toString().equals("0")){
						ret="当前任务类型可接单数不足";
					}
					else{
						String conditions="";
						List<Object[]> list=ju.QueryToListValue(sid_sql, pramas,conn);
						//商家黑名单
						String ssql="select S_ID from SellerBlackList where B_ID=?";
						List<Object[]> black_sid=ju.QueryToListValue(ssql, pramas, conn);
						if(black_sid.size()!=0){
							list.addAll(black_sid);
						}
						if(list.size()!=0){
							for(int i=0;i<list.size();i++){
								if(i==list.size()-1){
									conditions+=list.get(i)[0].toString();
								}
								else{
									conditions+=list.get(i)[0].toString()+",";
								}
							}
							conditions=" AND S_ID NOT IN ("+conditions+") ";
						}
						
						String sql="SELECT top 1 RWBH,GJCFB,RWLX FROM S_SumOrder WHERE "+rwlx+" (MSYJ between ? and ?)  and SYDS>0 and WFBDS>=0  "+conditions+" and "
								+ "Charindex(convert(varchar(4),?),isNUll(S_SumOrder.SF,space(1)))=0 and (? between isnull(S_SumOrder.NL_L,0) "
								+ "and isnull(S_SumOrder.NL_H,100)) and Charindex(convert(varchar(1),?),isNUll(S_SumOrder.XB,'01'))!=0 and "
								+ "?>=isnull(S_SumOrder.XY,0) and ?>=isnull(S_SumOrder.TQZ,0) and ?>=isnull(S_SumOrder.PJ,0) ORDER BY MSYJ desc ";
						Object[] task_info=ju.QueryToValue(sql, new Object[]{min,max,buyer_info[1],buyer_info[2],buyer_info[3],buyer_info[4],buyer_info[5],buyer_info[6]}, conn);
						if(task_info.length!=0){
							count=pro_place(conn,u_ID,task_info);
						}
					}
					
				}
			}
		}
		catch(Exception e){
			conn.rollback();
			ret="服务器异常,联系发哥.err-code:000";
		}
		finally{
			if(count>0){
				ret="1";
				conn.commit();
			}
			else{
				conn.rollback();
			}
			conn.close();
		}
		return ret;
	}
	//and S_ID not in (1161,8461,12862,13520,13644,13860,23076,23180,26432)
	/**
	 * 存储过程的改写（U_Order_Place 该存储过程停用）
	 * 
	 * 2016年10月28日
	 */
	public static int pro_place(Connection conn,String B_ID,Object[] task_info){
		int count=0;
		JDBCUtil ju =new JDBCUtil();
		Object[] Bid_Params={B_ID};
		String sql="select B_ID from blacklist where B_ID=?";
		boolean in_blacklist=ju.queryhas(sql, Bid_Params, conn);
		if(in_blacklist){
			return -1;
		}
		sql="SELECT TOP 1 DDBH FROM B_Order WHERE RWBH=? AND GJCFB=? AND DDZT='8' AND RWLX=?";
		Object[] ddbh=ju.QueryToValue(sql, task_info, conn);
		if(ddbh[0]==null){
			return -3;
		}
		sql="SELECT SFZXM,SJHM,QQ,WWID,YHDJ,MTRWDS,MTLLDS FROM v_Buyer WHERE B_ID=?";
		Object[] buyer_info=ju.QueryToValue(sql, Bid_Params, conn);
		sql="UPDATE B_Order SET XDSJ=CONVERT(VARCHAR(20), GETDATE(),20),B_ID=?,B_YHM=?,B_SJHM=?,QQ=?,B_WWID=?,DDZT='0' WHERE DDBH=? AND DDZT='8'";
		count=ju.update(sql, new Object[]{B_ID,buyer_info[0],buyer_info[1],buyer_info[2],buyer_info[3],ddbh[0]},conn);
		if(count==0){
			return -4;
		}
		sql="UPDATE S_Order_Manage SET WJD=WJD-1,DCZ=DCZ+1 WHERE RWBH=? and GJCFB=? and RWLX=? and WJD>0";
		count=ju.update(sql, task_info,conn);
		if(count==0){
			return -5;
		}
		sql="UPDATE S_SumOrder SET SYDS=SYDS-1 WHERE RWBH=? AND GJCFB=? AND RWLX=? and SYDS>0";
		count=ju.update(sql, task_info,conn);
		if(count==0){
			return -6;
		}
		if(task_info[2].equals("1") || task_info[2].equals("6")){
			sql="UPDATE Buyer SET YHDJ=YHDJ+1,MTLLDS=MTLLDS-1 WHERE B_ID=?";
			count=ju.update(sql, Bid_Params,conn);
			if(count==0){
				return -7;
			}
		}
		else{
			sql="UPDATE Buyer SET YHDJ=YHDJ+1,MTRWDS=MTRWDS-1 WHERE B_ID=?";
			count=ju.update(sql, Bid_Params,conn);
			if(count==0){
				return -8;
			}
		}
		return 2;
	}
}
