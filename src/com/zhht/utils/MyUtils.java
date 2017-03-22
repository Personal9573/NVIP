package com.zhht.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;



import javax.servlet.http.HttpServletResponse;

import net.sf.json.JSONObject;

public class MyUtils {

	/**
	 *
	 * @author zhht 2015-12-9 下午12:05:50
	 * @param
	 * @return void
	 * @Description if obj=null ,Object[] obj={}.now for String to int.
	 */
	//待更改
	public static int[] StringToInt(String[] obj){
		int[] intObj = new int[obj.length];
		try{
			for(int i=0;i<obj.length;i++){
				int intValue=Integer.valueOf(obj[i]);
				intObj[i]=intValue;
			}
		}
		catch(Exception e){
			return null;
		}
		return intObj;
		
	}
	public static void sendMessageToBrowser(HttpServletResponse response,Object ret){
		try {
			PrintWriter out = response.getWriter();
			out.print(ret);
			out.close();
		} catch (IOException e) {
		}
		
	}
	//返回状态
	public static void sendMessageToPhone(int count,PrintWriter out){
		Map<String,String> map=new HashMap<String,String>();
		if(count>0){
			map.put("code", "success");
			out.write(JSONObject.fromObject(map).toString());
		}else{
			map.put("code", "failure");
			out.write(JSONObject.fromObject(map).toString());
		}
		out.close();
	}
	public String getMac(String IP){
		try {
			InputStream os = Runtime.getRuntime().exec("arp -a")
					.getInputStream();
			BufferedReader br = new BufferedReader(new InputStreamReader(os,
					"gbk"));
			String line = "";
			String result = IP;
			if(result ==null || result.equals("")){
				return "IP为空";
			}
			while ((line = br.readLine()) != null) {
				if (!line.equals("") && line.trim().startsWith(result.concat(" "))) {
					line=line.replace(result, "").trim();
					break;
				}
			}
			InputStream is=MyUtils.class.getClassLoader().getResourceAsStream("testmac.properties");
			Properties pro=new Properties();
			pro.load(is);
			Iterator<Object> it=pro.keySet().iterator();
			while(it.hasNext()){
				result=it.next().toString();
				if(line !=null && line.startsWith(result)){
					break;
				}
				else{
					return "";
				}
			}
			return pro.getProperty(result);
//			System.out.println(pro.getProperty(result));
/*			if(line!=null && pro.containsKey(line.replace(result, "").trim())){
				System.out.println("contains");
			}*/
		} catch (IOException e) {
			return "未登记的测试用户";
		}
	}
/*	public static String getImgSrc(HttpServletRequest request,HttpServletResponse response){
		SmartUpload mySmartUpload = new SmartUpload();//1.实例化
		@SuppressWarnings("unused")
		long Max_Size = 5 * 500 * 1024;// 限制文件上传大小
		long File_Size = 500 * 1024;// 限制单个文件大小
		String ext = "";
		String msg = "";
		// 设置图片上载路径
		Date date = new Date(System.currentTimeMillis());
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		String url = (new SimpleDateFormat("yyyy-MM-dd")).format(cal.getTime());
		String uploadpath = "E:/upload/" + url + "/";
		// 2.初始化
		mySmartUpload.initialize(this.getServletConfig(), request, response);
		// 只允许上载此类文件（区分大小写）
		try {
			mySmartUpload.setAllowedFilesList("jpg,JPG,PNG,png,JPEG,jpeg,gif,GIF");
			// 3.上载文件
			mySmartUpload.upload();
		} catch (Exception e) {

			out.print("<script>");
			out
					.println("alert('只允许上传.jpg.JPG和.gif.GIF类型图片文件!');window.history.back();");
			out.print("</script>");

		}
		try {
			SmartFile myFile = mySmartUpload.getFiles().getFile(
					0);
			if (myFile.isMissing()) {
				out.print("<script>");
				out.println("alert('请至少选择一个要上传的文件!')"
						+ ";window.history.back();");
				out.print("</script>");

			} else {
				if (mySmartUpload.getFiles().getCount() > 5) {
					out.print("<script>");
					out
							.println("alert('上传文件数量不得超过5个!');window.history.back();");
					out.print("</script>");
					return;
				}
				for (int i = 0; i < mySmartUpload.getFiles().getCount(); i++) {
					myFile = mySmartUpload.getFiles().getFile(i);
					if (myFile.getSize() > File_Size) {
						out.print("<script>");
						out
								.println("alert('单个文件大小超过500K!');window.history.back();");
						out.print("</script>");
						return;
					}
				}

				for (int i = 0; i < mySmartUpload.getFiles().getCount(); i++) {
					myFile = mySmartUpload.getFiles().getFile(i);
					if (myFile.isMissing())
						continue;
					ext = myFile.getFileExt(); // 取得后缀名
					// 更改文件名，取得当前上传时间的毫秒数值
					Calendar calendar = Calendar.getInstance();
					String filename = String
							.valueOf(calendar.getTimeInMillis());
					if (!new File(uploadpath).isDirectory())
						new File(uploadpath).mkdirs();
					String fileAddress = uploadpath + filename + i + "." + ext; // 保存路径
					myFile.saveAs(fileAddress, mySmartUpload.SAVE_PHYSICAL);
					msg = fileAddress;
					System.out.println("msg=" + msg);
				}
				// C_name,C_type,C_date,C_nature,C_The,C_remarks1
				String cdetailed = mySmartUpload.getRequest().getParameter(
						"cdetailed");
				String cname = mySmartUpload.getRequest().getParameter("cname");
				String ctype = mySmartUpload.getRequest().getParameter("ctype");
				String cnature = mySmartUpload.getRequest().getParameter(
						"cnature");
				String cthe = mySmartUpload.getRequest().getParameter("cthe");
				String cremarks1 = mySmartUpload.getRequest().getParameter(
						"cremarks1");
				String cPrice = mySmartUpload.getRequest().getParameter(
						"cPrice");
				double cPrice1 = Double.parseDouble(cPrice);
				String cRebate = mySmartUpload.getRequest().getParameter(
						"cRebate");
				double cRebate1 = Double.parseDouble(cRebate);
				String color = mySmartUpload.getRequest().getParameter("color");
				String size = mySmartUpload.getRequest().getParameter("size");
				String integralStr=mySmartUpload.getRequest().getParameter("integral");
				double integral=Double.parseDouble(integralStr);
				String cimg = "";
				String loadpath = "download/";
				// 商品名称非空验证
				if (null == cname || cname.equals("")) {
					out
							.println("<script language=javaScript>alert('请填写商品名称!');window.history.back();</script>");
					return;
				}
				// 商品类别验证
				if (null == ctype || ctype.equals("") || ctype.equals("请选择")) {
					out
							.println("<script language=javaScript>alert('请填写商品类型标识!');window.history.back();</script>");
					return;
				}
				// 商品性质验证
				if (null == cnature || cnature.equals("")
						|| cnature.equals("请选择")) {
					out
							.println("<script language=javaScript>alert('请填写商品性质标识!');window.history.back();</script>");
					return;
				}
				if (null != msg) {
					cimg = loadpath + msg.substring(10, msg.length());
				} else {
					out
							.println("<script language=javaScript>alert('请上传照片!');window.history.back();</script>");
					return;
				}
	}*/
}
