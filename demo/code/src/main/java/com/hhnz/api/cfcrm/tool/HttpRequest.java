package com.hhnz.api.cfcrm.tool;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;
import java.util.Map;

import com.hhnz.api.cfcrm.constants.ConstantsConfig;
import com.tuhanbao.io.base.Constants;
import com.tuhanbao.util.log.LogManager;

/**
 * 高梓恒一次性使用
 * 不纳入代码规范
 * 
 * @author Administrator
 *
 */
public class HttpRequest {

    /**
     * 向指定URL发送GET方法的请求
     * 
     * @param url
     *            发送请求的URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * @return URL 所代表远程资源的响应结果
     */
    public static String sendGet(String url, Map<String, Object> param) {
        String result = "";
        BufferedReader in = null;
        String urlNameString = "";
        try {
            if (param != null) {
                url = url + "?";
                for (Map.Entry<String, Object> entry : param.entrySet()) {
                    url += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), Constants.UTF_8) + "&";
                }
                urlNameString = url.substring(0, url.length() - 1);
            }
            LogManager.info(urlNameString);

            // String urlNameString = url + "?" + param;
            URL realUrl = new URL(urlNameString);
            // 打开和URL之间的连接
            HttpURLConnection connection = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            connection.setRequestProperty("accept", "*/*");
            connection.setRequestProperty("connection", "Keep-Alive");
            /*
             * connection.setRequestProperty("user-agent",
             * "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
             */
            connection.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");

            // 建立实际的连接
            connection.connect();
            // 获取所有响应头字段
            Map<String, List<String>> map = connection.getHeaderFields();
            // 遍历所有的响应头字段
            for (String key : map.keySet()) {
                System.out.println(key + "--->" + map.get(key));
            }
            // 定义 BufferedReader输入流来读取URL的响应
            in = new BufferedReader(new InputStreamReader(connection.getInputStream(), Constants.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LogManager.info("发送GET请求出现异常！" + e.getMessage());
        }
        // 使用finally块来关闭输入流
        finally {
            try {
                if (in != null) {
                    in.close();
                }
            } catch (Exception e2) {
                LogManager.error(e2);
            }
        }
        return result;
    }

    /**
     * 向指定 URL 发送POST方法的请求
     * 
     * @param url
     *            发送请求的 URL
     * @param param
     *            请求参数，请求参数应该是 name1=value1&name2=value2 的形式。
     * 
     *            修改后的直接传map
     * 
     * @return 所代表远程资源的响应结果
     */
    public static String sendPost(String url, Map<String, Object> p) {
        PrintWriter out = null;
        BufferedReader in = null;
        String result = "";
        HttpURLConnection conn = null;
        String param = "";
        try {
            if (!p.isEmpty()) {
                for (Map.Entry<String, Object> entry : p.entrySet()) {
                    param += entry.getKey() + "=" + URLEncoder.encode(entry.getValue().toString(), Constants.UTF_8) + "&";
                }
                param = param.substring(0, param.length() - 1);
            }
            // logger.info("param--->"+param);
            URL realUrl = new URL(url);
            // 打开和URL之间的连接
            conn = (HttpURLConnection) realUrl.openConnection();
            // 设置通用的请求属性
            // conn.setRequestProperty("accept", "*/*");
            conn.setRequestProperty("connection", "Keep-Alive");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1;SV1)");
            conn.setRequestProperty("user-agent", "Mozilla/4.0 (compatible; MSIE 5.0; Windows NT; DigExt)");
            // 发送POST请求必须设置如下两行
            conn.setRequestProperty("ContentType", "text/xml;charset=UTF-8");
            conn.setRequestProperty("charset", "UTF-8");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(ConstantsConfig.CONNECT_TIME_OUT);
            conn.setReadTimeout(ConstantsConfig.READ_TIME_OUT);
            // 获取URLConnection对象对应的输出流
            // OutputStreamWriter osw = new
            // OutputStreamWriter(con.getOutputStream(), "UTF-8");
            out = new PrintWriter(conn.getOutputStream());

            // 发送请求参数
            out.print(param);
            // flush输出流的缓冲
            out.flush();
            // 定义BufferedReader输入流来读取URL的响应
            LogManager.info("返回code：" + conn.getResponseCode());
            in = new BufferedReader(new InputStreamReader(conn.getInputStream(), Constants.UTF_8));
            String line;
            while ((line = in.readLine()) != null) {
                result += line;
            }
        } catch (Exception e) {
            LogManager.info("发送 POST 请求出现异常！" + e.getMessage());
            String str = e.toString();
            int len = str.length();
            if (len > 3000) {
                str = str.substring(0, 3000);
            }
            return str;
        }
        // 使用finally块来关闭输出流、输入流
        finally {
            try {
                if (out != null) {
                    out.close();
                }
                if (in != null) {
                    in.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (Exception e) {
                String str = e.toString();
                int len = str.length();
                if (len > 3000) {
                    str = str.substring(0, 3000);
                }
                LogManager.info(str);
                return str;
            }
        }
        return result;
    }

    public static String sendBaiduPost(String[] postUrls, String pathUrl) {
        String ss = null;
        try {
            // String pathUrl =
            // "http://data.zz.baidu.com/urls?site=trade.mmbao.com&token=43n6fh4osi6tZwVc";
            // String pathUrl =
            // "http://data.zz.baidu.com/urls?site=trade.mmbao.com&token=43n6fh4osi6tZwVc";
            // 建立连接
            URL url = new URL(pathUrl);
            HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();

            // 设置连接属性
            httpConn.setDoOutput(true);// 使用 URL 连接进行输出
            httpConn.setDoInput(true);// 使用 URL 连接进行输入
            httpConn.setUseCaches(false);// 忽略缓存
            httpConn.setRequestMethod("POST");// 设置URL请求方法
            StringBuffer urlStr = new StringBuffer();
            if (postUrls != null && postUrls.length > 0) {
                for (int i = 0; i < postUrls.length; i++) {
                    urlStr.append(postUrls[i] + "\n");
                }
            }
            String requestString = urlStr.toString();
            // String requestString = "http://www.mmbao.com/cms/info_258.html";
            // System.out.println(requestString);
            // 设置请求属性
            // 获得数据字节数据，请求数据流的编码，必须和下面服务器端处理请求流的编码一致
            byte[] requestStringBytes = requestString.getBytes(Constants.UTF_8);
            httpConn.setRequestProperty("User-Agent", "curl/7.12.1");
            httpConn.setRequestProperty("Content-Type", "text/plain");
            httpConn.setRequestProperty("Content-length", "" + requestStringBytes.length);
            // httpConn.setRequestProperty("Connection", "Keep-Alive");// 维持长连接
            // httpConn.setRequestProperty("Charset", "UTF-8");

            // 建立输出流，并写入数据
            OutputStream outputStream = httpConn.getOutputStream();
            outputStream.write(requestStringBytes);
            outputStream.close();
            // 获得响应状态
            int responseCode = httpConn.getResponseCode();

            if (HttpURLConnection.HTTP_OK == responseCode) {// 连接成功
                // 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(httpConn.getInputStream(), Constants.UTF_8));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append(Constants.ENTER);
                }
                responseReader.close();
                ss = sb.toString();

            } else {
                InputStream is = httpConn.getErrorStream();

                // 当正确响应时处理数据
                StringBuffer sb = new StringBuffer();
                String readLine;
                BufferedReader responseReader;
                // 处理响应流，必须与服务器响应流输出的编码一致
                responseReader = new BufferedReader(new InputStreamReader(is, Constants.UTF_8));
                while ((readLine = responseReader.readLine()) != null) {
                    sb.append(readLine).append(Constants.ENTER);
                }
                responseReader.close();
                ss = sb.toString();

            }
        } catch (Exception ex) {
            LogManager.error(ex);
        }
        return ss;
    }

}
