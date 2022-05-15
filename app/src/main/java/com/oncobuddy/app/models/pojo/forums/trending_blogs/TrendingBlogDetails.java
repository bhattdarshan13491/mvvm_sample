package com.oncobuddy.app.models.pojo.forums.trending_blogs;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Entity;

import java.util.List;
import com.google.gson.annotations.SerializedName;


public class TrendingBlogDetails implements Parcelable{

	@SerializedName("comments")
	private List<CommentsItem> comments;

	@SerializedName("post")
	private Post post;

	@SerializedName("postLikes")
	private int postLikes;

	@SerializedName("content")
	private String content;

	@SerializedName("liked")
	private boolean liked;

	public TrendingBlogDetails() {
	}

	protected TrendingBlogDetails(Parcel in) {
		postLikes = in.readInt();
		content = in.readString();
		liked = in.readByte() != 0;
	}

	public static final Creator<TrendingBlogDetails> CREATOR = new Creator<TrendingBlogDetails>() {
		@Override
		public TrendingBlogDetails createFromParcel(Parcel in) {
			return new TrendingBlogDetails(in);
		}

		@Override
		public TrendingBlogDetails[] newArray(int size) {
			return new TrendingBlogDetails[size];
		}
	};

	public List<CommentsItem> getComments() {
		return comments;
	}

	public void setComments(List<CommentsItem> comments) {
		this.comments = comments;
	}

	public Post getPost() {
		return post;
	}

	public void setPost(Post post) {
		this.post = post;
	}

	public int getPostLikes() {
		return postLikes;
	}

	public void setPostLikes(int postLikes) {
		this.postLikes = postLikes;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public boolean isLiked() {
		return liked;
	}

	public void setLiked(boolean liked) {
		this.liked = liked;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeInt(postLikes);
		parcel.writeString(content);
		parcel.writeByte((byte) (liked ? 1 : 0));
	}
}