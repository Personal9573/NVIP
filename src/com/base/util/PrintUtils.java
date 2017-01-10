package com.base.util;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletResponse;

public class PrintUtils {
	public static void sendMessageToPhone(HttpServletResponse response,Object result){
		try {
			PrintWriter out = response.getWriter();
			out.print(result);
			out.close();
		} catch (IOException e) {
		}
		
	}
}
