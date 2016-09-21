package cn.com.nbd.nbdmobile.utility;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import com.nbd.article.manager.ArticleManager;
import com.nbd.network.bean.CrashBean;


import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Environment;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;
  
/** 
 * UncaughtException处理类,当程序发生Uncaught异常的时候,由该类来接管程序,并记录发送错误报告. 
 *  
 * @author richerHe 
 *  
 */  
public class CrashHandler implements UncaughtExceptionHandler {
	
    private static final String TAG = "CrashHandler";
    private Thread.UncaughtExceptionHandler mDefaultHandler;// 系统默认的UncaughtException处理类
    private static CrashHandler INSTANCE = new CrashHandler();// CrashHandler实例
    private Context mContext;// 程序的Context对象
    private Map<String, String> info = new HashMap<String, String>();// 用来存储设备信息和异常信息
    private SimpleDateFormat format = new SimpleDateFormat(
            "yyyy年MM月dd日HH时mm分ss秒");// 用于格式化日期,作为日志文件名的一部分
    
    
    private CrashBean mCrashBean;
    
    /** 保证只有一个CrashHandler实例 */
    private CrashHandler() {

    }
  
    /** 获取CrashHandler实例 ,单例模式 */
    public static CrashHandler getInstance() {
        return INSTANCE;
    }
  
    /**
     * 初始化
     * 
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mCrashBean = new CrashBean();
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();// 获取系统默认的UncaughtException处理器
        Thread.setDefaultUncaughtExceptionHandler(this);// 设置该CrashHandler为程序的默认处理器 
    }
  
    /**
     * 当UncaughtException发生时会转入该重写的方法来处理 
     */  
    @Override  
    public void uncaughtException(Thread thread, Throwable ex) {
        if (!handleException(ex) && mDefaultHandler != null) {
            // 如果自定义的没有处理则让系统默认的异常处理器来处理
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);// 如果处理了，让程序继续运行3秒再退出，保证文件保存并上传到服务器 
            } catch (InterruptedException e) {
            	Log.e(TAG, "error : ", e);  
            }
            // 退出程序
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }
  
    /** 
     * 自定义错误处理,收集错误信息 发送错误报告等操作均在此完成.
     *  
     * @param ex 
     *            异常信息 
     * @return true 如果处理了该异常信息;否则返回false. 
     */  
    public boolean handleException(Throwable ex) {
        if (ex == null)
            return false;
        new Thread() {
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, "很抱歉,手机端程序出现异常,即将退出", 0).show();
                Looper.loop();
            }
        }.start();
        // 收集设备参数信息
        collectDeviceInfo(mContext);
        saveCrashInfoToFile(ex);
        
//        Log.e("HFF  CRASH-->", ex.getCause().toString());
//        mCrashBean.setException(ex.getCause().toString());
        // 保存日志文件 
        Log.e(TAG, "error : ", ex);
        return true;
    }

    private String saveCrashInfoToFile(Throwable ex) {
    	
    	FileOutputStream fos = null;
//        StringBuffer sb = new StringBuffer();
//        for (Map.Entry<String, String> entry : info.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            sb.append(key + "=" + value + "\r\n");
//        }
        Writer writer = new StringWriter();
        PrintWriter pw = new PrintWriter(writer);
        ex.printStackTrace(pw);
        Throwable cause = ex.getCause();
        // 循环着把所有的异常信息写入writer中
        while (cause != null) {
            cause.printStackTrace(pw);
            cause = cause.getCause();
        }
        pw.close();// 记得关闭
        String result = writer.toString();
//        sb.append(result);
        long timetamp = System.currentTimeMillis();
        String time = format.format(new Date());
        String dataTime = data(time); 
        
        mCrashBean.setException(result);
        mCrashBean.setTime(dataTime);
        
        ArticleManager.getInstence().uploadCrashInfo(mCrashBean);
//        // 保存文件
//        String fileName = "crash-" + time + "-" + timetamp + ".txt";
//        if (Environment.getExternalStorageState().equals(
//                Environment.MEDIA_MOUNTED)) {
//            try {
//            	
//				boolean sdExist = Environment.getExternalStorageState().equals(
//						android.os.Environment.MEDIA_MOUNTED);
//
//				if (sdExist) {
//					// 获取SDCard的路径
//					File dir = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "crash");  
//	                Log.i("CrashHandler", dir.toString());
//	                if (!dir.exists())
//	                    dir.mkdir();
//	                fos = new FileOutputStream(new File(dir,
//	                         fileName));
//	                fos.write(sb.toString().getBytes()); 
//	             
//	                return fileName;
//				}
//
//            } catch (FileNotFoundException e) {
//                e.printStackTrace();
//            } catch (IOException e) { 
//                e.printStackTrace();
//            } finally {
//            	try {
//            		if(fos != null) {
//            			fos.close();
//            		}
//				} catch (IOException e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
//            }
//        }
//        
        return null;
    }


/** 
* 收集设备参数信息
*  
* @param context
*/
public void collectDeviceInfo(Context context) {
 try {
     PackageManager pm = context.getPackageManager();// 获得包管理器
     PackageInfo pi = pm.getPackageInfo(context.getPackageName(),
             PackageManager.GET_ACTIVITIES);// 得到该应用的信息，即主Activity
     if (pi != null) {
         String versionName = pi.versionName == null ? "null"
                 : pi.versionName;
         String versionCode = pi.versionCode + "";
         
         mCrashBean.setVersionName(versionName);
         mCrashBean.setVersionCode(versionCode);
         mCrashBean.setDeviceModel(android.os.Build.MODEL);
         mCrashBean.setSystemVersion(android.os.Build.VERSION.SDK);
         
         
//         info.put("Product Model", android.os.Build.MODEL);
//         info.put("Sdk", android.os.Build.VERSION.SDK);
//         info.put("Version", android.os.Build.VERSION.RELEASE);
     }
 } catch (NameNotFoundException e) {
     e.printStackTrace();
 }
}

public String data(String time) {
	SimpleDateFormat sdr = new SimpleDateFormat("yyyy年MM月dd日HH时mm分ss秒",
			Locale.CHINA);
	Date date;
	String times = null;
	try {
		date = sdr.parse(time);
		long l = date.getTime();
		String stf = String.valueOf(l);
		times = stf.substring(0, 10);
		Log.e(TAG, times);
	} catch (ParseException e) {
		e.printStackTrace();
	}
	return times;
}

}