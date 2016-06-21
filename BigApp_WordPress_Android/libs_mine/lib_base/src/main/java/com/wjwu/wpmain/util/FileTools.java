package com.wjwu.wpmain.util;

import android.os.Environment;
import android.os.StatFs;

import com.wjwu.wpmain.cache.SdCacheTools;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by wjwu on 2015/8/28.
 */
public class FileTools {
    public static void saveObject(File file, Object obj) {
        FileOutputStream os = null;
        ObjectOutputStream oos = null;
        try {
            if (file.exists()) {
                file.delete();
                file.createNewFile();
            }
            if (obj == null) {
                return;
            }
            os = new FileOutputStream(file);
            oos = new ObjectOutputStream(os);
            oos.writeObject(obj);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (oos != null) {
                try {
                    oos.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    oos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static Object readObject(File file) {
        if (!file.exists()) {
            return null;
        }
        FileInputStream is = null;
        ObjectInputStream ois = null;
        Object obj = null;
        try {
            is = new FileInputStream(file);
            ois = new ObjectInputStream(is);
            obj = ois.readObject();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ois != null) {
                try {
                    ois.close();
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
        return obj;
    }

    private final static String SD_LOG_CACHE_DIR = "/ag/logs/";

    public static void saveLogs(Throwable throwable) {
        try {
            StringBuffer temp = new StringBuffer();
            if (throwable != null) {
                StackTraceElement[] stackElements = throwable.getStackTrace();
                temp.append("----------------begin-----------------"
                        + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        .format(new Date())
                        + "----------------begin-----------------\n");
                if (stackElements != null) {
                    temp.append(throwable.getMessage());
                    for (int i = 0; i < stackElements.length; i++) {
                        temp.append(stackElements[i].toString() + "\n");
                    }
                }
                if (throwable.getCause() != null) {
                    StackTraceElement[] stackElements2 = throwable.getCause()
                            .getStackTrace();
                    if (stackElements2 != null) {
                        temp.append("----------------------cause by----------------------\n"
                                + throwable.getMessage());
                        for (int i = 0; i < stackElements2.length; i++) {
                            temp.append(stackElements2[i].toString() + "\n");
                        }
                    }
                }
                temp.append("----------------end-----------------"
                        + new SimpleDateFormat("yyyy-MM-dd hh:mm:ss")
                        .format(new Date())
                        + "----------------end-----------------\n");
            }
            saveFile(Environment.getExternalStorageDirectory()
                    + SD_LOG_CACHE_DIR + "log_"
                    + new SimpleDateFormat("yyyyMMdd").format(new Date()), temp
                    .toString().getBytes());
        } catch (Exception e) {
        }
    }

    public static boolean saveFile(String filePath, byte[] data) {
        boolean result = false;
        if (data == null || data.length > SdCacheTools.getAvailableSize(true)) {
            result = false;
        } else {
            File file = new File(filePath);
            if (!file.exists()) {
                File parentFile = new File(file.getParent());
                if (parentFile.exists() && parentFile.isDirectory()) {
                } else {
                    parentFile.mkdirs();
                }
            } else {
                file.delete();
            }
            FileOutputStream outStream = null;
            try {
                file.createNewFile();
                outStream = new FileOutputStream(file, false);
                outStream.write(data);
                result = true;
            } catch (Exception e) {
            } finally {
                if (outStream != null) {
                    try {
                        outStream.close();
                    } catch (IOException e) {
                    }
                }
            }
        }
        return result;
    }

    /***
     * 下载并保存大文件
     */
    public static boolean downloadAndSaveBigFile(String urlStr, String filePath) {
        boolean result = false;
        File file = new File(filePath);
        InputStream in = null;
        FileOutputStream outStream = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Cache-Control", "no-cache");
            int status = connection.getResponseCode();
            if (file.exists()) {
                file.delete();
            }
            file.createNewFile();
            if (status == 200) {
                outStream = new FileOutputStream(file);
                in = connection.getInputStream();
                byte buffer[] = new byte[2014];
                int len = 0;
                while ((len = in.read(buffer)) != -1) {
                    outStream.write(buffer, 0, len);
                }
                result = true;
            }
        } catch (Exception e) {
        } finally {
            try {
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (outStream != null) {
                    outStream.close();
                    outStream = null;
                }
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            } catch (IOException e) {
            }
        }
        return result;
    }

    /***
     * 下载并保存文件
     */
    public static boolean downloadAndSaveFile(String urlStr, String filePath) {
        boolean result = false;
        File file = new File(filePath);
        StringBuffer response = new StringBuffer();
        InputStream in = null;
        InputStreamReader inputReader = null;
        BufferedReader reader = null;
        HttpURLConnection connection = null;
        try {
            URL url = new URL(urlStr);
            connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.setUseCaches(false);
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(60000);
            connection.setRequestProperty("Cache-Control", "no-cache");
            int status = connection.getResponseCode();
            if (status == 200) {
                in = connection.getInputStream();
                inputReader = new InputStreamReader(in, "utf-8");
                reader = new BufferedReader(inputReader);
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
            }
        } catch (Exception e) {
            response = null;
        } finally {
            try {
                if (reader != null) {
                    reader.close();
                    reader = null;
                }
                if (inputReader != null) {
                    inputReader.close();
                    inputReader = null;
                }
                if (in != null) {
                    in.close();
                    in = null;
                }
                if (connection != null) {
                    connection.disconnect();
                    connection = null;
                }
            } catch (IOException e) {
            }
        }
        String resultStr = null;
        if (response != null) {
            resultStr = response.toString();
        }
        try {
            if (resultStr != null) {
                result = saveFile(file.getPath(), resultStr.getBytes());
            }
        } catch (Exception e) {
        }
        return result;
    }
}
