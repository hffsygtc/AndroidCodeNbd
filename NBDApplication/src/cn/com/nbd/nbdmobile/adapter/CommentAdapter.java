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
public class CommentAdapter extends BaseAdapter {

	private List<SingleComment> showBeans = new ArrayList<SingleComment>();

	Activity activity;
	LayoutInflater inflater = null;

	protected ImageLoader imageLoader = ImageLoader.getInstance();
	DisplayImageOptions options;

	private Map<Integer, ViewHolder> holderPositionMap = new HashMap<Integer, ViewHolder>();

	private boolean isDayTheme;

	public enum commentClickType {
		/** 点赞 */
		GOOD,
		/** 回复 */
		REPLY,
		/** 分享 */
		SHARE,
		/** 举报 */
		ALRT,
	}

	/**
	 * 给外部界面提供的项目点击事件
	 * 
	 * @author he
	 * 
	 */
	public interface onItemFunctionClick {
		void onFunctionChoosed(commentClickType type,SingleComment comment,int position);

	}

	private onItemFunctionClick mListener;

	public void setOnItemFunctionClickListener(onItemFunctionClick listener) {
		this.mListener = listener;
	}

	public CommentAdapter(Activity activity, List<SingleComment> comments,
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
			view = inflater.inflate(R.layout.item_comment, null);
			mHolder = new ViewHolder();

			initHolder(mHolder, view);
		} else {
			mHolder = (ViewHolder) view.getTag();
		}
		SingleComment comment = getItem(position);
		if (holderPositionMap.get(position) == null) {
			holderPositionMap.put(position, mHolder);
		}

		if (comment.isOpen()) {
			mHolder.parentHandleLayout.setVisibility(View.VISIBLE);
		} else {
			mHolder.parentHandleLayout.setVisibility(View.GONE);
		}

		showTypeItem(mHolder, comment);
		// showThemeUi(mHolder, isDayTheme);

		setListener(mHolder, comment,position);

		return view;
	}

	/**
	 * 设置里面控件的监听
	 * 
	 * @param mHolder
	 * @param comment
	 */
	private void setListener(final ViewHolder mHolder,
			final SingleComment comment,final int position) {
		if (comment.getCommentType() == CommentType.CHILD_SINGLE
				|| comment.getCommentType() == CommentType.CHILD_HEAD
				|| comment.getCommentType() == CommentType.CHILD_BODY
				|| comment.getCommentType() == CommentType.CHILD_TAIL) { // 这种为盖楼子评论情况

		} else { //父评论布局里面的监听事件

			mHolder.parentContent.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (comment.isOpen()) { // 如果是展开的，就收起来
						comment.setOpen(false);
						mHolder.parentHandleLayout.setVisibility(View.GONE);
					} else {
						comment.setOpen(true);
						mHolder.parentHandleLayout.setVisibility(View.VISIBLE);
					}
				}
			});
			
			mHolder.parentMoreIcon.setOnClickListener(new View.OnClickListener() {
				
				@Override
				public void onClick(View v) {
					
					if (comment.isOpen()) {
						comment.setOpen(false);
						mHolder.parentContent.setMaxLines(3);
						mHolder.parentContent.setText(comment.getmShortContent());
					}else {
						comment.setOpen(true);
						mHolder.parentContent.setMaxLines(100);
						mHolder.parentContent.setText(comment.getBody());
					}
				}
			});
			
			/**
			 * 点击回复的事件
			 */
			mHolder.repleyLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onFunctionChoosed(commentClickType.REPLY,comment,position);
					}
				}
			});
			
//			/**
//			 * 点击分享的事件
//			 */
//			mHolder.shareLayout.setOnClickListener(new OnClickListener() {
//				
//				@Override
//				public void onClick(View v) {
//					if (mListener != null) {
//						mListener.onFunctionChoosed(commentClickType.SHARE,comment,position);
//					}
//				}
//			});
		
			/**
			 * 点击举报的事件
			 */
			mHolder.alrtLayout.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onFunctionChoosed(commentClickType.ALRT,comment,position);
					}
				}
			});
			
			/**
			 * 点点赞的事件
			 */
			mHolder.goodIconImg.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					if (mListener != null) {
						mListener.onFunctionChoosed(commentClickType.GOOD,comment,position);
					}
				}
			});
			
		}


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
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_mid_single);
				break;
			case CHILD_HEAD:
				mHolder.childTopGap.setVisibility(View.VISIBLE);
				mHolder.childTopAngleImg.setVisibility(View.VISIBLE);
				mHolder.childDivLine.setVisibility(View.VISIBLE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_child_top);

				break;
			case CHILD_BODY:
				mHolder.childTopGap.setVisibility(View.GONE);
				mHolder.childTopAngleImg.setVisibility(View.GONE);
				mHolder.childDivLine.setVisibility(View.VISIBLE);
				mHolder.childContentlayout
						.setBackgroundResource(R.drawable.comment_mid);
				break;
			case CHILD_TAIL:
				mHolder.childTopGap.setVisibility(View.GONE);
				mHolder.childTopAngleImg.setVisibility(View.GONE);
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
			mHolder.childPosition
					.setText((comment.getChildPosition() + 1) + "");

		} else { // 这种为父评论情况
			mHolder.parentLayout.setVisibility(View.VISIBLE);
			mHolder.childLayout.setVisibility(View.GONE);
			// 评论头部的种类标志显示控制
			switch (comment.getCommentType()) {
			case PARENT_HEAD_HOT:
				mHolder.parentTypeLayout.setVisibility(View.VISIBLE);
				mHolder.valueIconImg.setVisibility(View.GONE);
				mHolder.parentTypeText.setText("热门评论");
				break;
			case PARENT_HEAD_NEW:
				mHolder.parentTypeLayout.setVisibility(View.VISIBLE);
				mHolder.valueIconImg.setVisibility(View.GONE);
				mHolder.parentTypeText.setText("最新评论");
				break;
			case PARENT_HEAD_VALUE:
				mHolder.parentTypeLayout.setVisibility(View.VISIBLE);
				mHolder.valueIconImg.setVisibility(View.VISIBLE);
				mHolder.parentTypeText.setText("精华评论");
				break;
			case PARENT_NORAML_VALUE:
				mHolder.parentTypeLayout.setVisibility(View.GONE);
				mHolder.valueIconImg.setVisibility(View.VISIBLE);
				break;
			default:
				mHolder.parentTypeLayout.setVisibility(View.GONE);
				mHolder.valueIconImg.setVisibility(View.GONE);
				break;
			}

			mHolder.parentName.setText(comment.getUser_name());
			mHolder.parentLocation.setText(comment.getDate_format() + "  "
					+ comment.getLocation());
			mHolder.goodNumText.setText(comment.getSupport_num() + "");

			// 处理字符是否超出隐藏的情况
			if (comment.getBody().length() < 50) {
				comment.setmShortContent(comment.getBody());
				mHolder.parentMoreIcon.setVisibility(View.GONE);
			} else {
				comment.setmShortContent(comment.getBody().substring(0, 50)+"...");
				mHolder.parentMoreIcon.setVisibility(View.VISIBLE);
			}

			if (comment.isOpen()) {
				mHolder.parentContent.setText(comment.getBody());
			}else {
				mHolder.parentContent.setText(comment.getmShortContent());
			}
			
			// 处理是否有子评论，显示分割线与否
			if (comment.isHasChild()) {
				mHolder.parentBottomGap.setVisibility(View.GONE);
				mHolder.parentBottomLine.setVisibility(View.GONE);
			} else {
				mHolder.parentBottomGap.setVisibility(View.VISIBLE);
				mHolder.parentBottomLine.setVisibility(View.VISIBLE);
			}

			//根据评论是否已点赞，显示为标红样式
			
			if (comment.isSupported()) {
				mHolder.goodIconImg.setBackgroundResource(R.drawable.comment_good_red);
			}else {
				mHolder.goodIconImg.setBackgroundResource(R.drawable.comment_good);
			}
			
			imageLoader.displayImage(comment.getUser_avatar_url(),
					mHolder.parentHeadImg, options);

		}
	}

	private void initHolder(ViewHolder mHolder, View view) {
		// 父类评论的布局
		mHolder.parentLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_layout);
		// 父类评论中的种类布局
		mHolder.parentTypeLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_type_layout);
		mHolder.parentTypeText = (TextView) view
				.findViewById(R.id.comment_type_text);

		// 父类评论的头像
		mHolder.parentHeadImg = (ImageView) view
				.findViewById(R.id.comment_parent_headimg);
		// 精华评论的标志
		mHolder.valueIconImg = (ImageView) view
				.findViewById(R.id.comment_parent_value_icon);
		// 父类昵称
		mHolder.parentName = (TextView) view
				.findViewById(R.id.comment_parent_name);
		// 父类地址
		mHolder.parentLocation = (TextView) view
				.findViewById(R.id.comment_parent_location);
		// 点赞个数 图标
		mHolder.goodNumText = (TextView) view
				.findViewById(R.id.comment_parent_good_num);
		mHolder.goodIconImg = (ImageView) view
				.findViewById(R.id.comment_parent_good_icon);
		// 评论主体
		mHolder.parentContent = (TextView) view
				.findViewById(R.id.comment_parent_three_line_content);
		// 内容展开按钮
		mHolder.parentMoreIcon = (ImageView) view
				.findViewById(R.id.comment_parent_content_open);
		// 隐藏操作布局
		mHolder.parentHandleLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_handle_layout);
		mHolder.repleyLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_reply_layout);
//		mHolder.shareLayout = (RelativeLayout) view
//				.findViewById(R.id.comment_parent_share_layout);
		mHolder.alrtLayout = (RelativeLayout) view
				.findViewById(R.id.comment_parent_alrt_layout);

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
		if (isDay) { // 日间配色

		} else {

		}

	}

	static class ViewHolder {
		/** 父类评论的布局 */
		RelativeLayout parentLayout;
		/** 父类评论中的种类布局 */
		RelativeLayout parentTypeLayout;
		TextView parentTypeText;

		// 父类评论的头像
		ImageView parentHeadImg;
		// 精华评论的标志
		ImageView valueIconImg;
		// 父类昵称
		TextView parentName;
		// 父类地址
		TextView parentLocation;
		// 点赞个数 图标
		TextView goodNumText;
		ImageView goodIconImg;
		// 评论主体
		TextView parentContent;
		// 内容展开按钮
		ImageView parentMoreIcon;
		// 隐藏操作布局
		RelativeLayout parentHandleLayout;
		RelativeLayout repleyLayout;
//		RelativeLayout shareLayout;
		RelativeLayout alrtLayout;

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

	public void setDataChange() {

	}

	public void changeTheme(boolean isDay) {
		this.isDayTheme = isDay;
	}
	
	/**
	 * 点赞事件返回，改变图标的显示样式
	 * @param comment
	 * @param position
	 */
	public void changeSupportIcon(SingleComment comment,int position){
		
		for (int i = 0; i < showBeans.size(); i++) {
			if (showBeans.get(i).getId() == comment.getId()) {
				showBeans.get(i).setSupported(comment.isSupported());
				showBeans.get(i).setSupport_num(comment.getSupport_num());
			}
		}
		
		if (holderPositionMap.get(position) != null) {
			if (comment.isSupported()) {
				holderPositionMap.get(position).goodIconImg.setBackgroundResource(R.drawable.comment_good_red);
			}else {
				holderPositionMap.get(position).goodIconImg.setBackgroundResource(R.drawable.comment_good);
			}
			holderPositionMap.get(position).goodNumText.setText(comment.getSupport_num()+"");
			
		}
	}
}
