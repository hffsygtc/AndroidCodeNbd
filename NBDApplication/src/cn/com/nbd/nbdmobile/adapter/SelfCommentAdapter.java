package cn.com.nbd.nbdmobile.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.R.integer;
import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.nbd.nbdmobile.R;
import cn.com.nbd.nbdmobile.activity.SearchResultActivity;
import cn.com.nbd.nbdmobile.adapter.NewsFastAdapter.ViewHolder;
import cn.com.nbd.nbdmobile.utility.Options;

import com.nbd.article.article.ArticleType;
import com.nbd.article.bean.ArticleImages;
import com.nbd.article.bean.ArticleInfo;
import com.nbd.article.bean.CommentType;
import com.nbd.article.bean.MyMessageBean;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.commentBean;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * 收藏内容的适配器
 * 
 * @author riche
 */
public class SelfCommentAdapter extends BaseAdapter {

	private List<SingleComment> showBeans = new ArrayList<SingleComment>();

	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private boolean isDayTheme;

	public SelfCommentAdapter(Activity activity, List<SingleComment> comments,
			boolean isDay) {
		this.activity = activity;
		this.showBeans = comments;
		inflater = LayoutInflater.from(activity);
		options = Options.getHeadOptions(activity);

	}

	@Override
	public int getCount() {
		return showBeans.size();
	}

	@Override
	public SingleComment getItem(int position) {
		return showBeans.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {

		ViewHolder mHolder = null;
		View view = convertView;

		if (view == null) {
			view = inflater.inflate(R.layout.self_comment_item, null);
			mHolder = new ViewHolder();

			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}

		SingleComment comment = getItem(position);
		showThemeUi(mHolder, isDayTheme);

		showTypeItem(mHolder, comment);

		return view;
	}

	/**
	 * 根据内容的种类控制不同样式的显示
	 * 
	 * @param mHolder
	 * @param comment
	 */
	private void showTypeItem(ViewHolder mHolder, SingleComment comment) {
		if (comment.getCommentType() == CommentType.CHILD_SINGLE
				|| comment.getCommentType() == CommentType.CHILD_HEAD
				|| comment.getCommentType() == CommentType.CHILD_BODY
				|| comment.getCommentType() == CommentType.CHILD_TAIL) { // 这种为盖楼子评论情况

			mHolder.parentLayout.setVisibility(View.GONE);
			mHolder.childLayout.setVisibility(View.VISIBLE);
			switch (comment.getCommentType()) {
			case CHILD_SINGLE:
				mHolder.childTopGap.setVisibility(View.VISIBLE);
				mHolder.childTopAngleImg.setVisibility(View.VISIBLE);
				mHolder.childDivLine.setVisibility(View.GONE);
				mHolder.relativeArticleText.setVisibility(View.VISIBLE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_mid_single);
				break;
			case CHILD_HEAD:
				mHolder.childTopGap.setVisibility(View.VISIBLE);
				mHolder.childTopAngleImg.setVisibility(View.VISIBLE);
				mHolder.childDivLine.setVisibility(View.VISIBLE);
				mHolder.relativeArticleText.setVisibility(View.GONE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_child_top);

				break;
			case CHILD_BODY:
				mHolder.childTopGap.setVisibility(View.GONE);
				mHolder.childTopAngleImg.setVisibility(View.GONE);
				mHolder.relativeArticleText.setVisibility(View.GONE);
				mHolder.childDivLine.setVisibility(View.VISIBLE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_mid);
				break;
			case CHILD_TAIL:
				mHolder.childTopGap.setVisibility(View.GONE);
				mHolder.childTopAngleImg.setVisibility(View.GONE);
				mHolder.relativeArticleText.setVisibility(View.VISIBLE);
				mHolder.childDivLine.setVisibility(View.GONE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_child_bottom);
				break;

			default:
				break;
			}

			mHolder.childName.setText(comment.getUser_name());
			mHolder.childLocation.setText(comment.getDate_format() + "  "
					+ comment.getLocation());
			mHolder.childContent.setText(comment.getBody());
			mHolder.childPosition.setVisibility(View.GONE);
			mHolder.relativeArticleText.setText(comment.getArticle_title());

		} else { // 这种为父评论情况
			mHolder.parentLayout.setVisibility(View.VISIBLE);
			mHolder.childLayout.setVisibility(View.GONE);
			mHolder.relativeArticleText.setVisibility(View.VISIBLE);

			mHolder.parentName.setText(comment.getUser_name());
			mHolder.parentLocation.setText(comment.getDate_format() + "  "
					+ comment.getLocation());
			mHolder.goodNumText.setText(comment.getSupport_num() + "");

			mHolder.parentContent
					.setText(comment.getBody());

			// 处理是否有子评论，显示分割线与否
			if (comment.isHasChild()) {
				Log.e("PARENT==", "HAS CHILD");
				mHolder.parentBottomGap.setVisibility(View.GONE);
				mHolder.parentBottomLine.setVisibility(View.GONE);
				mHolder.relativeArticleText.setVisibility(View.GONE);
			} else {
				Log.e("PARENT==", "not HAS CHILD");
				mHolder.parentBottomGap.setVisibility(View.GONE);
				mHolder.parentBottomLine.setVisibility(View.VISIBLE);
				mHolder.relativeArticleText.setVisibility(View.VISIBLE);
			}

			imageLoader.displayImage(comment.getUser_avatar_url(),
					mHolder.parentHeadImg, options);
			mHolder.relativeArticleText.setText(comment.getArticle_title());

		}
	}

	private void initHolder(ViewHolder mHolder, View view) {
		
		mHolder.mainLayout = (LinearLayout) view.findViewById(R.id.comment_item_layout);
		
		// 父类评论的布局
		mHolder.parentLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_layout);
		
		mHolder.relativeArticleText = (TextView) view.findViewById(R.id.comment_relative_article);

		// 父类昵称
		mHolder.parentName = (TextView) view
				.findViewById(R.id.comment_parent_name);
		// 父类地址
		mHolder.parentLocation = (TextView) view
				.findViewById(R.id.comment_parent_location);
		// 父类评论的头像
		mHolder.parentHeadImg = (ImageView) view
				.findViewById(R.id.comment_parent_headimg);
		// 点赞个数 图标
		mHolder.goodNumText = (TextView) view
				.findViewById(R.id.comment_parent_good_num);
		mHolder.goodIconImg = (ImageView) view
				.findViewById(R.id.comment_parent_good_icon);
		// 评论主体
		mHolder.parentContent = (TextView) view
				.findViewById(R.id.comment_parent_three_line_content);

		// 有无子评论的底部空隙和分割线
		mHolder.parentBottomGap = (TextView) view
				.findViewById(R.id.comment_parent_gap);
		mHolder.parentBottomLine = (TextView) view
				.findViewById(R.id.comment_parent_divline);

		// 子评论的布局
		mHolder.childLayout = (RelativeLayout) view
				.findViewById(R.id.comment_child_layout);

		// 顶部给首项三角形留的间隙
		mHolder.childTopGap = (TextView) view
				.findViewById(R.id.comment_child_angle_gap);
		mHolder.childTopAngleImg = (ImageView) view
				.findViewById(R.id.comment_child_top_angle);

		// 子评论布局，用于切换背景圆角图片
		mHolder.childContentlayout = (RelativeLayout) view
				.findViewById(R.id.comment_child_content_layout);
		mHolder.childName = (TextView) view
				.findViewById(R.id.comment_child_name);
		mHolder.childLocation = (TextView) view
				.findViewById(R.id.comment_child_location);
		mHolder.childContent = (TextView) view
				.findViewById(R.id.comment_child_three_line_content);
		mHolder.childPosition = (TextView) view
				.findViewById(R.id.comment_child_position_text);
		mHolder.childDivLine = (TextView) view
				.findViewById(R.id.comment_child_divline);

		view.setTag(mHolder);
	}

	private void showThemeUi(ViewHolder mHolder, boolean isDay) {
//		if (isDay) { // 日间配色
//			mHolder.mainLayout.setBackgroundColor(activity.getResources()
//					.getColor(R.color.day_section_background));
//			mHolder.childContentlayout.setBackgroundColor(activity.getResources()
//					.getColor(R.color.day_item_background));
//			mHolder.parentContent.setTextColor(activity.getResources()
//					.getColor(R.color.day_black));
//			mHolder.childContent.setTextColor(activity.getResources().getColor(
//					R.color.day_black));
//
//		} else {
//			mHolder.mainLayout.setBackgroundColor(activity.getResources()
//					.getColor(R.color.night_common_background));
//			mHolder.childContentlayout.setBackgroundColor(activity.getResources()
//					.getColor(R.color.night_section_background));
//			mHolder.parentContent.setTextColor(activity.getResources()
//					.getColor(R.color.night_black));
//			mHolder.childContent.setTextColor(activity.getResources().getColor(
//					R.color.night_black));
//
//		}
//

	}

	static class ViewHolder {
		
		LinearLayout mainLayout;
		
		TextView relativeArticleText;
		
		/** 父类评论的布局 */
		RelativeLayout parentLayout;

		// 父类评论的头像
		ImageView parentHeadImg;
		// 父类昵称
		TextView parentName;
		// 父类地址
		TextView parentLocation;
		// 点赞个数 图标
		TextView goodNumText;
		ImageView goodIconImg;
		// 评论主体
		TextView parentContent;

		// 有无子评论的底部空隙和分割线
		TextView parentBottomGap;
		TextView parentBottomLine;

		// 子评论的布局
		RelativeLayout childLayout;

		// 顶部给首项三角形留的间隙
		TextView childTopGap;
		ImageView childTopAngleImg;

		// 子评论布局，用于切换背景圆角图片
		RelativeLayout childContentlayout;
		TextView childName;
		TextView childLocation;
		TextView childContent;
		TextView childPosition;

		TextView childDivLine;
	}

	public void setDataChange(List<SingleComment> comments) {
		this.showBeans = comments;

	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}

}
