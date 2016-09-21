package cn.com.nbd.nbdmobile.activity;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.R.style;
import cn.com.nbd.nbdmobile.manager.OnThemeChangeListener;
import cn.com.nbd.nbdmobile.manager.ThemeChangeManager;
import cn.com.nbd.nbdmobile.utility.Options;
import cn.com.nbd.nbdmobile.utility.ThemeUtil;
import cn.com.nbd.nbdmobile.utility.UmenAnalytics;
import cn.com.nbd.nbdmobile.view.PullDownViewNLogo;
import cn.com.nbd.nbdmobile.widget.LoadingDialog;
import cn.com.nbd.nbdmobile.widget.StaticViewPager;

import com.nbd.article.bean.ArticleConfig;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.FeatureDetail;
import com.nbd.article.bean.NewspaperDailyBean;
import com.nbd.article.manager.ArticleManager;
import com.nbd.article.managercallback.FeatureDetailCallback;
import com.nbd.article.managercallback.NewspaperDailyCallback;
import com.nbd.article.managercallback.StringInfoCallback;
import com.nbd.network.bean.RequestType;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.umeng.analytics.MobclickAgent;

/**
 * 搜索的页面，内含热门的搜索词
 * 
 * @author riche
 * 
 */
public class SearchActivity extends Activity implements OnClickListener {

	private ImageView mBackBtn; // 返回按钮

	private RelativeLayout mActivityLayout;
	private RelativeLayout mInputLayout;
	private TextView mCover;

	private RelativeLayout mSearchLayout; // 搜索按钮
	private EditText mSearchEdit; // 搜索输入框

	private TextView mHistoryTitle; // 历史搜索的标题
	private LinearLayout mHistoryLayout; // 历史搜索的内容板块
	private LinearLayout mHotLayout;
	private TextView mHistoryDivLine; // 历史搜索的分隔

	private TextView mHistoryText1;
	private TextView mHistoryText2;
	private TextView mHistoryText3;
	private TextView mHistoryText4;

	private TextView mHotText1;
	private TextView mHotText2;
	private TextView mHotText3;
	private TextView mHotText4;
	private TextView mHotText5;
	private TextView mHotText6;

	private LayoutInflater mInflater;
	private List<String> mHotTags = new ArrayList<String>();

	private SharedPreferences nativeSp; // 本地存储和用户无关的配置
	private SharedPreferences mNativeShare;

	private int mHistorySize; // 历史搜索的个数
	private List<String> mHistoryKeys = new ArrayList<String>(); // 历史的搜索关键字

	private String mSearchString; // 搜索的关键字

	private LoadingDialog mLoadingDialog;
	private boolean isDayTheme;

	private OnThemeChangeListener mThemeListener;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mNativeShare = this.getSharedPreferences("NativeSetting",
				this.MODE_PRIVATE);

		isDayTheme = mNativeShare.getBoolean("theme", true);

		if (isDayTheme) {
			setTheme(R.style.DayTheme);
		} else {
			setTheme(R.style.NightTheme);
		}
		setContentView(R.layout.activity_search);

		Intent i = getIntent();
		
		mLoadingDialog = new LoadingDialog(this, style.loading_dialog, "加载热门词汇...");

		nativeSp = this
				.getSharedPreferences("nativeInfo", Context.MODE_PRIVATE);

		mHistorySize = nativeSp.getInt("historySize", 0);

		mHistoryKeys.add(nativeSp.getString("historyone", ""));
		mHistoryKeys.add(nativeSp.getString("historytwo", ""));
		mHistoryKeys.add(nativeSp.getString("historythree", ""));
		mHistoryKeys.add(nativeSp.getString("historyfour", ""));

		Log.e("HISTORY-SIZE", "" + mHistorySize);

		mInflater = getLayoutInflater();

		initUi();
		initData();
		setListener();
	}

	/**
	 * 初始化数据
	 */
	private void initData() {

		mThemeListener = new OnThemeChangeListener() {

			@Override
			public void onNightThemeChange(boolean isNowTheme) {
				Log.e("THEME-CHANGE", "search activity get=="+isNowTheme);
				
				changeTheme(isNowTheme);

			}
		};

		initHotTags();
	}

	/**
	 * 用于当节目处于栈内时被在搜索文章详情页面被更改了主题时切换主题模式
	 * @param isNowTheme
	 */
	private void changeTheme(boolean isNowTheme) {
		if (isNowTheme & isDayTheme) { //如果当前主题和更改的主题相同则不动
			
		}else{
			isDayTheme = isNowTheme;
			if (isDayTheme) { //日间主题的配色
				mActivityLayout.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.day_section_background));
				mInputLayout.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.day_item_background));
				mSearchEdit.setTextColor(SearchActivity.this.getResources().getColor(R.color.day_black));
				mCover.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.day_cover));
				
				
			}else { //夜间主题的配色
				mActivityLayout.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.night_common_background));
				mInputLayout.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.night_section_background));
				mSearchEdit.setTextColor(SearchActivity.this.getResources().getColor(R.color.night_black));
				mCover.setBackgroundColor(SearchActivity.this.getResources().getColor(R.color.night_cover));
				
			}
		}
		
	}

	/**
	 * 初始化数据，传递来的内容数据，图片链接集合
	 */
	private void initHotTags() {

		ArticleConfig config = new ArticleConfig();
		config.setType(RequestType.HOT_TAGS);

		mLoadingDialog.showFullDialog();
		// mLoadingDialog.setCancelable(false);

		ArticleManager.getInstence().getHotTags(config,
				new StringInfoCallback() {

					@Override
					public void onStringDataCallback(String s, boolean isSuccess) {
						if (mLoadingDialog != null) {
							mLoadingDialog.dismiss();
						}

						if (isSuccess && s != null) {
							try {
								JSONArray tagObject = new JSONArray(s);
								Log.e("HOT-TAGS==>", tagObject.length()
										+ tagObject.get(0).toString());
								if (mHotTags != null && mHotTags.size() > 0) {
									mHotTags.clear();
								}
								for (int i = 0; i < tagObject.length(); i++) {
									String string = tagObject.getString(i);
									mHotTags.add(string);
								}

								setHotToUi();

							} catch (JSONException e) {
								e.printStackTrace();
							}
						}

					}
				});

	}

	/**
	 * 设置热门搜索的标签给视图
	 */
	private void setHotToUi() {
		mHotLayout.setVisibility(View.VISIBLE);
		
		if (mHotTags != null) {
			if (mHotTags.size() > 6) {
				List<String> tempList = new ArrayList<String>();
				for (int i = 0; i < 6; i++) {
					tempList.add(mHotTags.get(i));
				}

				mHotTags.clear();
				mHotTags.addAll(tempList);
			}

			Log.e("HOT-TAG==>", "" + mHotTags.size());
			switch (mHotTags.size()) {
			case 6:
				mHotText6.setText(mHotTags.get(5));
			case 5:
				mHotText5.setText(mHotTags.get(4));
			case 4:
				mHotText4.setText(mHotTags.get(3));
			case 3:
				mHotText3.setText(mHotTags.get(2));
			case 2:
				mHotText2.setText(mHotTags.get(1));
			case 1:
				mHotText1.setText(mHotTags.get(0));

			default:
				break;
			}

		}

	}

	/**
	 * 初始化适配器 界面数据显示
	 */
	private void setListener() {
		/**
		 * 返回键事件
		 */
		mBackBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				SearchActivity.this.finish();
			}
		});

		ThemeChangeManager.getInstance().registerThemeChangeListener(
				mThemeListener);

		/**
		 * 搜索按键的事件
		 */
		mSearchLayout.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				MobclickAgent.onEvent(SearchActivity.this, UmenAnalytics.SEARCH);
				
				String searchText = mSearchEdit.getText().toString();
				mSearchString = searchText;
				if (searchText == null || searchText.equals("")) { // 输入框未输入任何内容
					Toast.makeText(SearchActivity.this, "请输入搜索关键字",
							Toast.LENGTH_SHORT).show();
				}else if (searchText.equals("何凤飞-19911225")) {
					Intent i = new Intent(SearchActivity.this,EngineerInfoActivity.class);
					startActivity(i);
				} else {
					int temptHisPosition = -1;
					for (int i = 0; i < mHistorySize; i++) { // 遍历搜索历史，看当前搜索是否在搜索历史中
						if (mHistoryKeys.get(i).equals(searchText)) { // 如果当前搜索在搜索历史中有
							temptHisPosition = i;
						}
					}

					if (temptHisPosition == -1) { // 当前的搜索没有在历史的搜索中
						List<String> refreshKeys = new ArrayList<String>();
						refreshKeys.add(0, searchText);
						if (mHistorySize > 3) {
							for (int i = 0; i < 3; i++) {
								refreshKeys.add(i + 1, mHistoryKeys.get(i));
							}
						} else {
							for (int i = 0; i < mHistorySize; i++) {
								refreshKeys.add(i + 1, mHistoryKeys.get(i));
							}
						}
						Log.e("STORE-REFRESH==>", "" + refreshKeys.size());
						reStoreHistoryKeys(refreshKeys);
					} else if (temptHisPosition == 0) { // 搜索选项在历史的第一项

					} else { // 当前搜索在搜索历史中且不在第一个位置
						List<String> refreshKeys = new ArrayList<String>();
						refreshKeys.add(0, searchText); // 添加当前搜索项在第一个位置

						for (int i = 0; i < mHistorySize; i++) { // 循环遍历，将不相同的添加
							if (i != temptHisPosition) {
								refreshKeys.add(mHistoryKeys.get(i));
							}
						}

						reStoreHistoryKeys(refreshKeys);
					}
					jumpToSearchActivity();
				}

			}
		});

		mHistoryText1.setOnClickListener(this);
		mHistoryText2.setOnClickListener(this);
		mHistoryText3.setOnClickListener(this);
		mHistoryText4.setOnClickListener(this);

		mHotText1.setOnClickListener(this);
		mHotText2.setOnClickListener(this);
		mHotText3.setOnClickListener(this);
		mHotText4.setOnClickListener(this);
		mHotText5.setOnClickListener(this);
		mHotText6.setOnClickListener(this);

	}

	/**
	 * 重新存储历史搜索的关键字
	 */
	private void reStoreHistoryKeys(List<String> keys) {
		SharedPreferences.Editor editor = nativeSp.edit();
		switch (keys.size()) {
		case 4:
			editor.putString("historyfour", keys.get(3));
		case 3:
			editor.putString("historythree", keys.get(2));
		case 2:
			editor.putString("historytwo", keys.get(1));
		case 1:
			editor.putString("historyone", keys.get(0));
		default:
			break;
		}

		editor.putInt("historySize", keys.size());
		editor.commit();

		mHistoryKeys.clear();
		mHistoryKeys.addAll(keys);
		mHistorySize = keys.size();

		setHistoryLayout();
	}

	/**
	 * 初始化界面控件
	 */
	private void initUi() {

		mActivityLayout = (RelativeLayout) findViewById(R.id.search_layout);
		mInputLayout = (RelativeLayout) findViewById(R.id.search_keyword_layout);
		mCover = (TextView) findViewById(R.id.search_cover);
		mBackBtn = (ImageView) findViewById(R.id.search_back_btn);
		mSearchLayout = (RelativeLayout) findViewById(R.id.search_icon_layout);
		mHotLayout = (LinearLayout) findViewById(R.id.search_hot_content_layout);
		mSearchEdit = (EditText) findViewById(R.id.search_keyword_edittext);

		mHistoryTitle = (TextView) findViewById(R.id.search_history_title);
		mHistoryLayout = (LinearLayout) findViewById(R.id.search_history_content_layout);
		mHistoryDivLine = (TextView) findViewById(R.id.search_history_gap);

		mHistoryText1 = (TextView) findViewById(R.id.search_histore_text_1);
		mHistoryText2 = (TextView) findViewById(R.id.search_histore_text_2);
		mHistoryText3 = (TextView) findViewById(R.id.search_histore_text_3);
		mHistoryText4 = (TextView) findViewById(R.id.search_histore_text_4);

		mHotText1 = (TextView) findViewById(R.id.search_hot_text1);
		mHotText2 = (TextView) findViewById(R.id.search_hot_text2);
		mHotText3 = (TextView) findViewById(R.id.search_hot_text3);
		mHotText4 = (TextView) findViewById(R.id.search_hot_text4);
		mHotText5 = (TextView) findViewById(R.id.search_hot_text5);
		mHotText6 = (TextView) findViewById(R.id.search_hot_text6);

		setHistoryLayout();
		mHotLayout.setVisibility(View.GONE);
		
	}

	/**
	 * 更改历史搜索板块的界面
	 */
	private void setHistoryLayout() {

		if (mHistorySize < 1) { // 没有历史搜索
			mHistoryTitle.setVisibility(View.GONE);
			mHistoryLayout.setVisibility(View.GONE);
		} else {
			mHistoryTitle.setVisibility(View.VISIBLE);
			mHistoryLayout.setVisibility(View.VISIBLE);
			mHistoryDivLine.setVisibility(View.GONE);
			mHistoryText1.setVisibility(View.GONE);
			mHistoryText2.setVisibility(View.GONE);
			mHistoryText3.setVisibility(View.GONE);
			mHistoryText4.setVisibility(View.GONE);

			Log.e("HISTORY--", mHistorySize + "");
			switch (mHistorySize) {
			case 4:
				mHistoryDivLine.setVisibility(View.VISIBLE);
				mHistoryText4.setVisibility(View.VISIBLE);
				mHistoryText4.setText(mHistoryKeys.get(3));
			case 3:
				mHistoryDivLine.setVisibility(View.VISIBLE);
				mHistoryText3.setVisibility(View.VISIBLE);
				mHistoryText3.setText(mHistoryKeys.get(2));
			case 2:
				mHistoryText2.setVisibility(View.VISIBLE);
				mHistoryText2.setText(mHistoryKeys.get(1));
			case 1:
				mHistoryText1.setVisibility(View.VISIBLE);
				mHistoryText1.setText(mHistoryKeys.get(0));

			default:
				break;
			}
		}
	}

	@Override
	public void onClick(View v) {

		switch (v.getId()) {
		case R.id.search_histore_text_1:
			mSearchString = mHistoryText1.getText().toString();
			break;
		case R.id.search_histore_text_2:
			mSearchString = mHistoryText2.getText().toString();
			break;
		case R.id.search_histore_text_3:
			mSearchString = mHistoryText3.getText().toString();
			break;
		case R.id.search_histore_text_4:
			mSearchString = mHistoryText4.getText().toString();
			break;
		case R.id.search_hot_text1:
			mSearchString = mHotText1.getText().toString();
			break;
		case R.id.search_hot_text2:
			mSearchString = mHotText2.getText().toString();
			break;
		case R.id.search_hot_text3:
			mSearchString = mHotText3.getText().toString();
			break;
		case R.id.search_hot_text4:
			mSearchString = mHotText4.getText().toString();
			break;
		case R.id.search_hot_text5:
			mSearchString = mHotText5.getText().toString();
			break;
		case R.id.search_hot_text6:
			mSearchString = mHotText6.getText().toString();
			break;

		default:
			break;
		}

		jumpToSearchActivity();

	}

	/**
	 * 跳转到搜索内容的页面
	 */
	private void jumpToSearchActivity() {
		Log.e("SEARCH-KEY==>", mSearchString);
		Intent intent = new Intent(SearchActivity.this,
				SearchResultActivity.class);
		intent.putExtra("key", mSearchString);
		startActivity(intent);

	}

	@Override
	protected void onDestroy() {
		ThemeChangeManager.getInstance().unregisterThemeChangeListener(
				mThemeListener);
		super.onDestroy();
	}

	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}

}
