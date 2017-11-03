package com.fantank.config;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.apache.commons.lang3.StringUtils;

public class HttpInterceptor extends HandlerInterceptorAdapter {
	
	// convert InputStream to String
		private static String getStringFromInputStream(InputStream is) {

			BufferedReader br = null;
			StringBuilder sb = new StringBuilder();

			String line;
			try {

				br = new BufferedReader(new InputStreamReader(is));
				while ((line = br.readLine()) != null) {
					sb.append(line);
				}

			} catch (IOException e) {
				System.out.println("IOException: " + e.getMessage());
				e.printStackTrace();
			} finally {
				if (br != null) {
					try {
						br.close();
					} catch (IOException e) {
						System.out.println("IOException: " + e.getMessage());
						e.printStackTrace();
					}
				}
			}

			return sb.toString();
		}

	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object object) throws Exception {
		//String hookData = getStringFromInputStream(request.getInputStream());
		//System.out.println("Recieved Handler: " + hookData);
		System.out.println(request.getContentType());
		return true;
	}
}
