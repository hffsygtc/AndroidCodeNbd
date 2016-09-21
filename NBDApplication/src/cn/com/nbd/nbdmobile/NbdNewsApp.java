package cn.com.nbd.nbdmobile;

import java.io.File;
import java.util.List;
import java.util.Set;

import org.litepal.LitePalApplication;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.util.Log;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.CrashHandler;
import cn.jpush.android.api.JPushInterface;
import cn.jpush.android.api.TagAliasCallback;

import com.lecloud.sdk.config.LeCloudPlayerConfig;
import com.nbd.article.manager.ArticleManager;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.nostra13.universalimageloader.utils.StorageUtils;
//import com.lecloud.config.LeCloudPlayerConfig;
//import com.letv.proxy.LeCloudProxy;

public class NbdNewsApp extends LitePalApplication {
	private static NbdNewsApp nbdNewsApp;

	public static NbdNewsApp getInstance() {
		return nbdNewsApp;
	}

	/**
	 * 涔�瑙�浜����濮����������
	 * 
	 * @param cxt
	 * @param pid
	 * @return
	 */
	public static String getProcessName(Context cxt, int pid) {
		ActivityManager am = (ActivityManager) cxt
				.getSystemService(Context.ACTIVITY_SERVICE);
		List<RunningAppProcessInfo> runningApps = am.getRunningAppProcesses();
		if (runningApps != null) {
			for (RunningAppProcessInfo procInfo : runningApps) {
				if (procInfo.pid == pid) {
					return procInfo.processName;
				}
			}
		}
		return null;
	}

	@Override
	public void onCreate() {
		Log.d("test", "app_creat");
		nbdNewsApp = this;
		initImageLoader(getApplicationContext());
		ArticleManager.init(this);
		CrashHandler crashHandler = CrashHandler.getInstance();
		crashHandler.init(this);
		ThemeChangeManager.getInstance().init(this);
		super.onCreate();
		

		String processName = getProcessName(this, android.os.Process.myPid());
		
	      if (getApplicationInfo().packageName.equals(processName)) {
	            //TODO CrashHandler是一个抓取崩溃log的工具类（可选）
//	            LeCloudPlayerConfig.getInstance().setPrintSdcardLog(true).setIsApp().setUseLiveToVod(true);//setUseLiveToVod 使用直播转点播功能 (直播结束后按照点播方式播放)
	            LeCloudPlayerConfig.init(getApplicationContext());
	        }
		
	      
	      
//		JPushInterface.setDebugMode(true);
		JPushInterface.init(this);
		JPushInterface.setSilenceTime(this, 22, 30, 8, 30);
		JPushInterface.setAlias(this, "233451", new TagAliasCallback() {
			
			@Override
			public void gotResult(int arg0, String arg1, Set<String> arg2) {
				// TODO Auto-generated method stub
				
			}
		});
		
	}

	/** ���濮����ImageLoader */
	public static void initImageLoader(Context context) {
		File cacheDir = StorageUtils.getOwnCacheDirectory(context,
				"nbdnews/Cache");// ��峰����扮��瀛�������褰���板��
		Log.d("hff=>cacheDir", cacheDir.getPath());
		// ���寤洪��缃�ImageLoader(������������椤归�芥�����������,���浣跨�ㄩ�ｄ��浣���������冲�����)锛�杩�涓����浠ヨ�惧�����APPLACATION������锛�璁剧疆涓哄�ㄥ��������缃�������
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				context)
				// .memoryCacheExtraOptions(480, 800) // max width, max
				// height锛���充��瀛����姣�涓�缂�瀛����浠剁�����澶ч�垮��
				// .discCacheExtraOptions(480, 800, CompressFormat.JPEG, 75,
				// null) // Can slow ImageLoader, use it carefully (Better don't
				// use it)璁剧疆缂�瀛����璇�缁�淇℃��锛����濂戒��瑕�璁剧疆杩�涓�
				.threadPoolSize(3)
				// 绾跨��姹�������杞界����伴��
				.threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				// .memoryCache(new UsingFreqLimitedMemoryCache(2 * 1024 *
				// 1024)) // You can pass your own memory cache
				// implementation浣����浠ラ��杩����宸辩�����瀛�缂�瀛�瀹����
				// .memoryCacheSize(2 * 1024 * 1024)
				// /.discCacheSize(50 * 1024 * 1024)
				.discCacheFileNameGenerator(new Md5FileNameGenerator())// 灏�淇�瀛������跺�����URI���绉扮��MD5
																		// ���瀵�
				// .discCacheFileNameGenerator(new
				// HashCodeFileNameGenerator())//灏�淇�瀛������跺�����URI���绉扮��HASHCODE���瀵�
				.tasksProcessingOrder(QueueProcessingType.LIFO)
				// .discCacheFileCount(100) //缂�瀛����File��伴��
				.discCache(new UnlimitedDiskCache(cacheDir))// ���瀹�涔�缂�瀛�璺�寰�
				// .defaultDisplayImageOptions(DisplayImageOptions.createSimple())
				// .imageDownloader(new BaseImageDownloader(context, 5 * 1000,
				// 30 * 1000)) // connectTimeout (5 s), readTimeout (30 s)瓒���舵�堕��
				.writeDebugLogs() // Remove for release app
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);// ��ㄥ�����濮����姝ら��缃�
	}

}
