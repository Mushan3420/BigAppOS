package com.wjwu.wpmain.net;

import android.util.Log;

import com.wjwu.wpmain.lib_base.BaseApplication;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.InetAddress;
import java.net.Socket;
import java.net.URL;

/**
 * 上传的文件
 */
public class FormFile {
    /**
     * 上传文件的数据
     */
    private byte[] data;
    private InputStream inStream;
    private File file;
    /**
     * 文件名称
     */
    private String filname;
    /**
     * 请求参数名称
     */
    private String parameterName;
    /**
     * 内容类型
     */
    private String contentType = "application/octet-stream";

    /**
     * @param filname       文件名称
     * @param data          上传的文件数据
     * @param parameterName 参数
     * @param contentType   内容类型
     */
    public FormFile(String filname, byte[] data, String parameterName, String contentType) {
        this.data = data;
        this.filname = filname;
        this.parameterName = parameterName;
        if (contentType != null) this.contentType = contentType;
    }

    /**
     * @param filname       文件名
     * @param file          上传的文件
     * @param parameterName 参数
     * @param contentType   内容内容类型
     */
    public FormFile(String filname, File file, String parameterName, String contentType) {
        this.filname = filname;
        this.parameterName = parameterName;
        this.file = file;
        try {
            this.inStream = new FileInputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        if (contentType != null) this.contentType = contentType;
    }

    public File getFile() {
        return file;
    }

    public InputStream getInStream() {
        return inStream;
    }

    public byte[] getData() {
        return data;
    }

    public String getFilname() {
        return filname;
    }

    public void setFilname(String filname) {
        this.filname = filname;
    }

    public String getParameterName() {
        return parameterName;
    }

    public void setParameterName(String parameterName) {
        this.parameterName = parameterName;
    }

    public String getContentType() {
        return contentType;
    }

    public void setContentType(String contentType) {
        this.contentType = contentType;
    }


    /**
     * 直接通过HTTP协议提交数据到服务器,实现如下面表单提交功能:
     * <FORM METHOD=POST ACTION="http://192.168.0.200:8080/ssi/fileload/test.do" enctype="multipart/form-data">
     * <INPUT TYPE="text" NAME="name">
     * <INPUT TYPE="text" NAME="id">
     * <input type="file" name="imagefile"/>
     * <input type="file" name="zip"/>
     * </FORM>
     */
    private static boolean post(FormFile[] files) throws Exception {
        final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
        final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志

        int fileDataLength = 0;
        for (FormFile uploadFile : files) {//得到文件类型数据的总长度
            StringBuilder fileExplain = new StringBuilder();
            fileExplain.append("--");
            fileExplain.append(BOUNDARY);
            fileExplain.append("\r\n");
            fileExplain.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileExplain.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            fileExplain.append("\r\n");
            fileDataLength += fileExplain.length();
            if (uploadFile.getInStream() != null) {
                fileDataLength += uploadFile.getFile().length();
            } else {
                fileDataLength += uploadFile.getData().length;
            }
        }
        //计算传输给服务器的实体数据总长度
        int dataLength = fileDataLength + endline.getBytes().length;

        URL url = new URL(RequestUrl.edit_avatar);
        int port = url.getPort() == -1 ? 80 : url.getPort();
        Log.e("", "wenjun RequestUrl.edit_avatar = " + RequestUrl.edit_avatar + ", port = " + port);
        port = 8089;
        Socket socket = new Socket(InetAddress.getByName(url.getHost()), port);
        OutputStream outStream = socket.getOutputStream();
        //下面完成HTTP请求头的发送
//        String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
        String requestmethod = "POST";
        outStream.write(requestmethod.getBytes());
        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
        outStream.write(accept.getBytes());
        String language = "Accept-Language: zh-CN\r\n";
        outStream.write(language.getBytes());
        String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
        outStream.write(contenttype.getBytes());
        String contentlength = "Content-Length: " + dataLength + "\r\n";
        outStream.write(contentlength.getBytes());
        String alive = "Connection: Keep-Alive\r\n";
        outStream.write(alive.getBytes());
        String host = "Host: " + url.getHost() + ":" + port + "\r\n";
        outStream.write(host.getBytes());
        //写完HTTP请求头后根据HTTP协议再写一个回车换行
        outStream.write("\r\n".getBytes());
        //把所有文本类型的实体数据发送出来
        //把所有文件类型的实体数据发送出来
        for (FormFile uploadFile : files) {
            StringBuilder fileEntity = new StringBuilder();
            fileEntity.append("--");
            fileEntity.append(BOUNDARY);
            fileEntity.append("\r\n");
            fileEntity.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileEntity.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            outStream.write(fileEntity.toString().getBytes());
            if (uploadFile.getInStream() != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                uploadFile.getInStream().close();
            } else {
                outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
            }
            outStream.write("\r\n".getBytes());
        }
        //下面发送数据结束标志，表示数据已经结束
        outStream.write(endline.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        StringBuffer response = new StringBuffer();

        String valueString = null;
        while ((valueString = reader.readLine()) != null) {
            response.append(valueString);
        }
        Log.e("", "wenjun response = " + response.toString());

//        if (reader.readLine().indexOf("200") == -1) {//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
//            return false;
//        }
        outStream.flush();
        outStream.close();
        reader.close();
        socket.close();
        return true;
    }


    /**
     * 发送请求
     *
     * @param encode 请求参数的编码
     */
    public static byte[] post(byte[] data, String encode) throws Exception {
        //String params = "method=save&name="+ URLEncoder.encode("老毕", "UTF-8")+ "&age=28&";//需要发送的参数
        URL url = new URL(RequestUrl.edit_avatar);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);//允许对外发送请求参数
        conn.setUseCaches(false);//不进行缓存
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("POST");
        //下面设置http请求头
        conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Type", "image/jpeg");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setRequestProperty("Connection", "Keep-Alive");

        //发送参数
        final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
        final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志
        StringBuilder fileExplain = new StringBuilder();
        fileExplain.append("--");
        fileExplain.append(BOUNDARY);
        fileExplain.append("\r\n");
        fileExplain.append("Content-Disposition: form-data;name=\"" + "testd" + "\";filename=\"" + "dddd123.jpg" + "\"\r\n");
        fileExplain.append("Content-Type: " + "image/jpeg" + "\r\n\r\n");
        fileExplain.append("\r\n");
        conn.setRequestProperty("Content-Length", String.valueOf(data.length + fileExplain.length() + endline.length()));
        Log.e("", "wenjun length = " + String.valueOf(data.length + fileExplain.length() + endline.length()));

        DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());
        outStream.write(fileExplain.toString().getBytes());
        outStream.write(data);//把参数发送出去
        outStream.write(endline.getBytes());
        outStream.flush();
        outStream.close();
        if (conn.getResponseCode() == 200) {
            ByteArrayOutputStream outSteam = new ByteArrayOutputStream();
            InputStream inStream = conn.getInputStream();
            byte[] buffer = new byte[1024];
            int len = -1;
            StringBuffer response = new StringBuffer();
            while ((len = inStream.read(buffer)) != -1) {
                outSteam.write(buffer, 0, len);
            }
            Log.e("", "wenjun response = " + new String(outSteam.toByteArray()));
            outSteam.close();
            inStream.close();
        }
        return null;
    }

    private static String upLoadByCommonPost(byte[] datas, String mimeType) {
        StringBuffer response = new StringBuffer();
        InputStream is = null;
        InputStreamReader isr = null;
        BufferedReader br = null;
        DataOutputStream dos = null;
        try {
            String end = "\r\n";
            String twoHyphens = "--";
            String boundary = "******";
            URL url = new URL(RequestUrl.edit_avatar);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url
                    .openConnection();
            httpURLConnection.setChunkedStreamingMode(1024 * 10240);// 128K
            // 允许输入输出流
            httpURLConnection.setDoInput(true);
            httpURLConnection.setDoOutput(true);
            httpURLConnection.setUseCaches(false);
            // 使用POST方法
            httpURLConnection.setRequestMethod("POST");
            httpURLConnection.setRequestProperty("Connection", "Keep-Alive");
            httpURLConnection.setRequestProperty("Charset", "UTF-8");
            httpURLConnection.setRequestProperty("Content-Type",
                    "multipart/form-data;boundary=" + boundary);
            httpURLConnection.setRequestProperty("Cookie", BaseApplication.getCookies());

            dos = new DataOutputStream(
                    httpURLConnection.getOutputStream());
            dos.writeBytes(twoHyphens + boundary + end);
            String suffix_imge = ".jpg";
            if ("image/png".equals(mimeType)) {
                suffix_imge = ".png";
            } else {
                mimeType = "image/jpeg";
            }
            Log.e("", "wenjun upload file mimeType = " + mimeType + ", suffix = " + suffix_imge);
            dos.writeBytes("Content-Disposition: form-data; name=\"wpua-file\"; filename=\""
                    + "bigapp_avatar_" + BaseApplication.getUserId() + "_" + System.currentTimeMillis() + suffix_imge + "\"" + end);
            dos.writeBytes("Content-Type: " + mimeType + end);
            dos.writeBytes(end);

            dos.write(datas);
            dos.writeBytes(end);
            dos.writeBytes(twoHyphens + boundary + twoHyphens + end);
            dos.flush();
            is = httpURLConnection.getInputStream();
            isr = new InputStreamReader(is, "utf-8");
            br = new BufferedReader(isr);
            String valueString = null;
            while ((valueString = br.readLine()) != null) {
                response.append(valueString);
            }
            Log.e("", "wenjun response = " + response.toString());
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (dos != null) {
                try {
                    dos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (isr != null) {
                try {
                    isr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return response.toString();
    }

    /**
     * 发送请求
     */
    public static byte[] post2(FormFile[] files) throws Exception {
        //String params = "method=save&name="+ URLEncoder.encode("老毕", "UTF-8")+ "&age=28&";//需要发送的参数
        URL url = new URL(RequestUrl.edit_avatar);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);//允许对外发送请求参数
        conn.setUseCaches(false);//不进行缓存
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("POST");
        //下面设置http请求头
        conn.setRequestProperty("Accept", "image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*");
        conn.setRequestProperty("Accept-Language", "zh-CN");
        conn.setRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 5.2; Trident/4.0; .NET CLR 1.1.4322; .NET CLR 2.0.50727; .NET CLR 3.0.04506.30; .NET CLR 3.0.4506.2152; .NET CLR 3.5.30729)");
        conn.setRequestProperty("Content-Type", "image/jpeg");
        conn.setRequestProperty("Connection", "Keep-Alive");

        final String BOUNDARY = "---------------------------7da2137580612"; //数据分隔线
        final String endline = "--" + BOUNDARY + "--\r\n";//数据结束标志

        int fileDataLength = 0;
        for (FormFile uploadFile : files) {//得到文件类型数据的总长度
            StringBuilder fileExplain = new StringBuilder();
            fileExplain.append("--");
            fileExplain.append(BOUNDARY);
            fileExplain.append("\r\n");
            fileExplain.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileExplain.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            fileExplain.append("\r\n");
            fileDataLength += fileExplain.length();
            if (uploadFile.getInStream() != null) {
                fileDataLength += uploadFile.getFile().length();
            } else {
                fileDataLength += uploadFile.getData().length;
            }
        }
        //计算传输给服务器的实体数据总长度
        int dataLength = fileDataLength + endline.getBytes().length;
        Log.e("", "wenjun dataLength = " + dataLength);
        conn.setRequestProperty("Content-Length", String.valueOf(dataLength));
        //发送参数
        OutputStream outStream = conn.getOutputStream();
        //下面完成HTTP请求头的发送
//        String requestmethod = "POST " + url.getPath() + " HTTP/1.1\r\n";
//        String requestmethod = "POST";
//        outStream.write(requestmethod.getBytes());
//        String accept = "Accept: image/gif, image/jpeg, image/pjpeg, image/pjpeg, application/x-shockwave-flash, application/xaml+xml, application/vnd.ms-xpsdocument, application/x-ms-xbap, application/x-ms-application, application/vnd.ms-excel, application/vnd.ms-powerpoint, application/msword, */*\r\n";
//        outStream.write(accept.getBytes());
//        String language = "Accept-Language: zh-CN\r\n";
//        outStream.write(language.getBytes());
//        String contenttype = "Content-Type: multipart/form-data; boundary=" + BOUNDARY + "\r\n";
//        outStream.write(contenttype.getBytes());
//        String contentlength = "Content-Length: " + dataLength + "\r\n";
//        outStream.write(contentlength.getBytes());
//        String alive = "Connection: Keep-Alive\r\n";
//        outStream.write(alive.getBytes());
//        String host = "Host: " + url.getHost() + ":" + 8090 + "\r\n";
//        outStream.write(host.getBytes());
        //写完HTTP请求头后根据HTTP协议再写一个回车换行
        outStream.write("\r\n".getBytes());
        //把所有文本类型的实体数据发送出来
//        outStream.write(textEntity.toString().getBytes());
        //把所有文件类型的实体数据发送出来
        for (FormFile uploadFile : files) {
            StringBuilder fileEntity = new StringBuilder();
            fileEntity.append("--");
            fileEntity.append(BOUNDARY);
            fileEntity.append("\r\n");
            fileEntity.append("Content-Disposition: form-data;name=\"" + uploadFile.getParameterName() + "\";filename=\"" + uploadFile.getFilname() + "\"\r\n");
            fileEntity.append("Content-Type: " + uploadFile.getContentType() + "\r\n\r\n");
            outStream.write(fileEntity.toString().getBytes());
            if (uploadFile.getInStream() != null) {
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = uploadFile.getInStream().read(buffer, 0, 1024)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                uploadFile.getInStream().close();
            } else {
                outStream.write(uploadFile.getData(), 0, uploadFile.getData().length);
            }
            outStream.write("\r\n".getBytes());
        }
        //下面发送数据结束标志，表示数据已经结束
        outStream.write(endline.getBytes());

        BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
        StringBuffer response = new StringBuffer();

        String valueString = null;
        while ((valueString = reader.readLine()) != null) {
            response.append(valueString);
        }
        Log.e("", "wenjun response = " + response.toString());

//        if (reader.readLine().indexOf("200") == -1) {//读取web服务器返回的数据，判断请求码是否为200，如果不是200，代表请求失败
//            return false;
//        }
        outStream.flush();
        outStream.close();
        reader.close();
        return null;
    }

    /**
     * 提交数据到服务器
     *
     * @param filePath 文件路径
     */
    public static boolean post(String filePath) throws Exception {
        FormFile file = new FormFile("" + BaseApplication.getUserId() + "_" + System.currentTimeMillis(), new File(filePath), "wpua-file", "image/jpeg");
        return post(new FormFile[]{file});
    }

    /**
     * 提交数据到服务器
     *
     * @param datas 文件内容
     */
    public static String postDatas(byte[] datas, String mimeType) {
        return upLoadByCommonPost(datas, mimeType);
    }
}