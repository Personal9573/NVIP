package com.base.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.lang.reflect.Method;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

import com.mlm.entity.UserReg;

public class BaseServlet extends HttpServlet {


	/**
	 * 
	 */
	private static final long serialVersionUID = 5683819003848041992L;
	String auth="";
	{
		try {
			InputStream is=this.getClass().getClassLoader().getResourceAsStream("auth.vali");
			InputStreamReader isr=new InputStreamReader(is);
			BufferedReader bf= new BufferedReader(isr);
			String line="";
			while((line=bf.readLine())!=null){
				auth+=line+",";
			}
		} catch (Exception e) {
			System.out.println("加载验证文件失败");
		}
		
	}
	@Override
	protected void service(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		request.setCharacterEncoding("UTF-8");
		response.setCharacterEncoding("utf-8");
		response.setContentType("text/html;charset=utf-8");		
		PrintWriter out=response.getWriter();
		String path=request.getRequestURI();		
		String UID="0";
		if(!auth.contains(path)){
			UserReg userreg = (UserReg) request.getSession().getAttribute("UserReg");
			if(userreg==null){
				out.print("result");
				out.close();
				return;
			}
			UID=userreg.getU_ID().toString();
		}
		path=UID+path;
		System.out.println(path);
		Long uptime=(Long) request.getSession().getAttribute(path);
		Long ntime=System.currentTimeMillis();
		if(uptime!=null){
			Long subt=ntime-uptime;
			System.out.println("subt="+subt);
			request.getSession().setAttribute(path, System.currentTimeMillis());
			if(subt<100){
				return;
			}
		}
		else{
			request.getSession().setAttribute(path, System.currentTimeMillis());
		}
		String[] meths=path.split("/");
		String methname=null;
		int len=meths.length;
		if(len==3){
			if(meths[2].contains("?")){
				methname=meths[2].substring(0, meths[2].indexOf("?"));
			}
			else{
				methname=meths[2];
			}
		}
		else if(len==4){
			if(meths[3].contains("?")){
				methname=meths[3].substring(0, meths[3].indexOf("?"));
			}
			else{
				methname=meths[3];
			}
		}
		else{
			out.print(JSONObject.fromObject("result"));
			out.close();
			return;
		}
		Method method = null;
		try {
			method = this.getClass().getMethod(methname,HttpServletRequest.class, HttpServletResponse.class);
			method.invoke(this, request, response);
		} catch (Exception e) {
			e.printStackTrace();
			return;
		}
	}
	  public String getIpAddr(HttpServletRequest request) {      
	       String ip = request.getHeader("x-forwarded-for");      
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	          ip = request.getHeader("Proxy-Client-IP");      
	      }      
	      if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	          ip = request.getHeader("WL-Proxy-Client-IP");      
	       }      
	     if(ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {      
	           ip = request.getRemoteAddr();      
	      }      
	     return ip;      
	}

}
