package com.oncobuddy.app.models.pojo.forums.trending_videos;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.SerializedName;

public class TrendingVideoDetails implements Parcelable {

	@SerializedName("saved")
	private Boolean saved;

	@SerializedName("questionAttachmentUrl")
	private String questionAttachmentUrl;

	@SerializedName("comments")
	private List<CommentsItem> comments;

	@SerializedName("post")
	private Post post;

	@SerializedName("videoDescription")
	private String videoDesc;

	@SerializedName("questionTitle")
	private String questionTitle;

	@SerializedName("commentsCount")
	private int commentsCount;

	@SerializedName("author")
	private Author author;

	@SerializedName("questionAttachmentType")
	private Object questionAttachmentType;

	@SerializedName("blogImageUrl")
	private String blogImageUrl;

	@SerializedName("postLikes")
	private int postLikes;

	@SerializedName("content")
	private String content;

	@SerializedName("liked")
	private boolean liked;

	public TrendingVideoDetails() {
	}

	protected TrendingVideoDetails(Parcel in) {
		questionAttachmentUrl = in.readString();
		videoDesc = in.readString();
		questionTitle = in.readString();
		commentsCount = in.readInt();
		blogImageUrl = in.readString();
		postLikes = in.readInt();
		content = in.readString();
		liked = in.readByte() != 0;
	}

	public static final Creator<TrendingVideoDetails> CREATOR = new Creator<TrendingVideoDetails>() {
		@Override
		public TrendingVideoDetails createFromParcel(Parcel in) {
			return new TrendingVideoDetails(in);
		}

		@Override
		public TrendingVideoDetails[] newArray(int size) {
			return new TrendingVideoDetails[size];
		}
	};

	public void setQuestionAttachmentUrl(String questionAttachmentUrl){
		this.questionAttachmentUrl = questionAttachmentUrl;
	}

	public String getQuestionAttachmentUrl(){
		return questionAttachmentUrl;
	}

	public void setComments(List<CommentsItem> comments){
		this.comments = comments;
	}

	public List<CommentsItem> getComments(){
		return comments;
	}

	public void setPost(Post post){
		this.post = post;
	}

	public Post getPost(){
		return post;
	}

	public void setVideoDesc(String videoDesc){
		this.videoDesc = videoDesc;
	}

	public String getVideoDesc(){
		return videoDesc;
	}

	public void setQuestionTitle(String questionTitle){
		this.questionTitle = questionTitle;
	}

	public String getQuestionTitle(){
		return questionTitle;
	}

	public void setCommentsCount(int commentsCount){
		this.commentsCount = commentsCount;
	}

	public int getCommentsCount(){
		return commentsCount;
	}

	public void setAuthor(Author author){
		this.author = author;
	}

	public Author getAuthor(){
		return author;
	}

	public void setQuestionAttachmentType(Object questionAttachmentType){
		this.questionAttachmentType = questionAttachmentType;
	}

	public Object getQuestionAttachmentType(){
		return questionAttachmentType;
	}

	public void setBlogImageUrl(String blogImageUrl){
		this.blogImageUrl = blogImageUrl;
	}

	public String getBlogImageUrl(){
		return blogImageUrl;
	}

	public void setPostLikes(int postLikes){
		this.postLikes = postLikes;
	}

	public int getPostLikes(){
		return postLikes;
	}

	public void setContent(String content){
		this.content = content;
	}

	public String getContent(){
		return content;
	}

	public void setLiked(boolean liked){
		this.liked = liked;
	}

	public boolean isLiked(){
		return liked;
	}

	public Boolean getSaved() {
		return saved;
	}

	public void setSaved(Boolean saved) {
		this.saved = saved;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel parcel, int i) {
		parcel.writeString(questionAttachmentUrl);
		parcel.writeString(videoDesc);
		parcel.writeString(questionTitle);
		parcel.writeInt(commentsCount);
		parcel.writeString(blogImageUrl);
		parcel.writeInt(postLikes);
		parcel.writeString(content);
		parcel.writeByte((byte) (liked ? 1 : 0));
	}
}