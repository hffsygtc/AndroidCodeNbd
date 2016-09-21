package cn.com.nbd.nbdmobile.utility;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.nbd.article.bean.CommentType;
import com.nbd.article.bean.SingleComment;
import com.nbd.article.bean.commentBean;

/**
 * 处理获取到的评论数据，将其处理成带标志的一个数组
 * 
 * @author he
 * 
 */
public class CommentHelper {

	/**
	 * 处理评论数据
	 * 
	 * @return 返回的带标志位的评论数据
	 */
	public static List<SingleComment> covertDatas2Single(
			List<commentBean> innerComment) {

		List<SingleComment> result = new ArrayList<SingleComment>();

		/**
		 * 根据传入的大分类
		 */
		for (int i = 0; i < innerComment.size(); i++) {
			commentBean typeBean = innerComment.get(i);
			List<SingleComment> typeComments = dealTypeComment(typeBean);
			result.addAll(typeComments);
		}

		return result;
	}

	/**
	 * 处理更多的最新评论数据
	 * 
	 * @return 返回的带标志位的评论数据
	 */
	public static List<SingleComment> covertNewDatas2Single(
			List<commentBean> innerComment) {

		List<SingleComment> result = new ArrayList<SingleComment>();

		/**
		 * 根据传入的大分类
		 */
		for (int i = 0; i < innerComment.size(); i++) {
			commentBean typeBean = innerComment.get(i);
			List<SingleComment> typeComments = dealNewsTypeComment(typeBean);
			result.addAll(typeComments);
		}

		return result;
	}

	public static int getCommentCount(List<commentBean> innerComment) {
		commentBean typeBean = innerComment.get(0);
		List<SingleComment> perComments = typeBean.getComments();
		return perComments.size();

	}

	/**
	 * 处理不同类型的评论
	 * 
	 * @param typeBean
	 */
	private static List<SingleComment> dealTypeComment(commentBean typeBean) {

		String type = typeBean.getType(); // 获取评论的类型

		List<SingleComment> perComments = typeBean.getComments();

		List<SingleComment> resultComments = new ArrayList<SingleComment>();

		if (type.equals("热门评论")) {
			Log.e("DEAL-DATA", "热门");
			for (int i = 0; i < perComments.size(); i++) {
				SingleComment dealComment = perComments.get(i);
				if (i == 0) {
					dealComment.setCommentType(CommentType.PARENT_HEAD_HOT);
				} else {
					dealComment.setCommentType(CommentType.PARENT_NORAML_HOT);
				}
				List<SingleComment> childrenComments = dealComment.getRef();
				if (childrenComments != null && childrenComments.size() > 0) { // 处理这个评论的子评论
					dealComment.setHasChild(true);
					for (int j = 0; j < childrenComments.size(); j++) {
						if (childrenComments.size() == 1) {
							childrenComments.get(j).setCommentType(
									CommentType.CHILD_SINGLE);
						} else {
							if (j == 0) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_HEAD);
							} else if (j != (childrenComments.size() - 1)) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_BODY);
							} else {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_TAIL);
							}
						}

						childrenComments.get(j).setChildPosition(j);
					}
				}

				// 处理完一条评论及其子评论，按顺序加入到同一个列表中
				dealComment.setRef(null);

				resultComments.add(dealComment);
				resultComments.addAll(childrenComments);
			}
		} else if (type.equals("最新评论")) {
			Log.e("DEAL-DATA", " 最新");
			for (int i = 0; i < perComments.size(); i++) {
				SingleComment dealComment = perComments.get(i);
				if (i == 0) {
					dealComment.setCommentType(CommentType.PARENT_HEAD_NEW);
				} else {
					dealComment.setCommentType(CommentType.PARENT_NORAML_NEW);
				}
				List<SingleComment> childrenComments = dealComment.getRef();
				if (childrenComments != null && childrenComments.size() > 0) { // 处理这个评论的子评论
					dealComment.setHasChild(true);
					for (int j = 0; j < childrenComments.size(); j++) {
						if (childrenComments.size() == 1) {
							childrenComments.get(j).setCommentType(
									CommentType.CHILD_SINGLE);
						} else {
							if (j == 0) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_HEAD);
							} else if (j != (childrenComments.size() - 1)) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_BODY);
							} else {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_TAIL);
							}
						}
						childrenComments.get(j).setChildPosition(j);
					}
				}

				// 处理完一条评论及其子评论，按顺序加入到同一个列表中
				dealComment.setRef(null);

				resultComments.add(dealComment);
				resultComments.addAll(childrenComments);
			}
		} else if (type.equals("精华评论")) {
			Log.e("DEAL-DATA", "精华");
			for (int i = 0; i < perComments.size(); i++) {
				SingleComment dealComment = perComments.get(i);
				if (i == 0) {
					dealComment.setCommentType(CommentType.PARENT_HEAD_VALUE);
				} else {
					dealComment.setCommentType(CommentType.PARENT_NORAML_VALUE);
				}
				List<SingleComment> childrenComments = dealComment.getRef();
				if (childrenComments != null && childrenComments.size() > 0) { // 处理这个评论的子评论
					dealComment.setHasChild(true);
					for (int j = 0; j < childrenComments.size(); j++) {
						if (childrenComments.size() == 1) {
							childrenComments.get(j).setCommentType(
									CommentType.CHILD_SINGLE);
						} else {
							if (j == 0) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_HEAD);
							} else if (j != (childrenComments.size() - 1)) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_BODY);
							} else {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_TAIL);
							}
						}
						childrenComments.get(j).setChildPosition(j);
					}
				}

				// 处理完一条评论及其子评论，按顺序加入到同一个列表中
				dealComment.setRef(null);

				resultComments.add(dealComment);
				resultComments.addAll(childrenComments);
			}
		}

		return resultComments;

	}

	/**
	 * 处理最新评论的子项
	 * 
	 * @param typeBean
	 */
	private static List<SingleComment> dealNewsTypeComment(commentBean typeBean) {

		String type = typeBean.getType(); // 获取评论的类型

		List<SingleComment> perComments = typeBean.getComments();

		List<SingleComment> resultComments = new ArrayList<SingleComment>();

		if (type.equals("最新评论")) {
			Log.e("DEAL-DATA", " 最新");
			for (int i = 0; i < perComments.size(); i++) {
				SingleComment dealComment = perComments.get(i);
				dealComment.setCommentType(CommentType.PARENT_NORAML_NEW);
				List<SingleComment> childrenComments = dealComment.getRef();
				if (childrenComments != null && childrenComments.size() > 0) { // 处理这个评论的子评论
					dealComment.setHasChild(true);
					for (int j = 0; j < childrenComments.size(); j++) {
						if (childrenComments.size() == 1) {
							childrenComments.get(j).setCommentType(
									CommentType.CHILD_SINGLE);
						} else {
							if (j == 0) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_HEAD);
							} else if (j != (childrenComments.size() - 1)) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_BODY);
							} else {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_TAIL);
							}
						}
						childrenComments.get(j).setChildPosition(j);
					}
				}

				// 处理完一条评论及其子评论，按顺序加入到同一个列表中
				dealComment.setRef(null);

				resultComments.add(dealComment);
				resultComments.addAll(childrenComments);
			}
		}

		return resultComments;
	}
	
	public static List<SingleComment> covertSelfDataWithTag(List<SingleComment> comments) {
		List<SingleComment> resultComments = new ArrayList<SingleComment>();

			Log.e("DEAL-DATA", " SELF COMMENT");
			for (int i = 0; i < comments.size(); i++) {
				SingleComment dealComment = comments.get(i);
				dealComment.setCommentType(CommentType.PARENT_NORAML_NEW);
				List<SingleComment> childrenComments = dealComment.getRef();
				if (childrenComments != null && childrenComments.size() > 0) { // 处理这个评论的子评论
					dealComment.setHasChild(true);
					for (int j = 0; j < childrenComments.size(); j++) {
						if (childrenComments.size() == 1) {
							childrenComments.get(j).setCommentType(
									CommentType.CHILD_SINGLE);
						} else {
							if (j == 0) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_HEAD);
							} else if (j != (childrenComments.size() - 1)) {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_BODY);
							} else {
								childrenComments.get(j).setCommentType(
										CommentType.CHILD_TAIL);
							}
						}
						childrenComments.get(j).setChildPosition(j);
					}
				}

				// 处理完一条评论及其子评论，按顺序加入到同一个列表中
				dealComment.setRef(null);

				resultComments.add(dealComment);
				resultComments.addAll(childrenComments);
			}

		return resultComments;
		
		
		
	}

}
