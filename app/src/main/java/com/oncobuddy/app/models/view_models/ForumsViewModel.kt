package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.ForumsVMImplementor
import com.oncobuddy.app.models.api_repositories.ForumsVMRepository
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.comments.CommentItem
import com.oncobuddy.app.models.pojo.forums.comments.CommentsListResponse
import com.oncobuddy.app.models.pojo.forums.comments.GetCommentsInput
import com.oncobuddy.app.models.pojo.forums.post_details.PostDetailsResponseNew
import com.oncobuddy.app.models.pojo.forums.shorts.ShortDetails
import com.oncobuddy.app.models.pojo.forums.shorts.ShortsListResponse
import com.oncobuddy.app.models.pojo.forums.trending_blogs.AddCommentInput
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideosListResponse
import com.oncobuddy.app.models.pojo.patient_profile.PatientDetailsResponse
import com.oncobuddy.app.utils.Constants

class ForumsViewModel(private val forumsVMImplementor: ForumsVMImplementor) : ViewModel() {

    private val _liveBlogsesponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getBlogsResponseData: LiveData<List<TrendingVideoDetails>> = _liveBlogsesponse

    private val _liveSavedPostsResponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getSavedPostsResponseData: LiveData<List<TrendingVideoDetails>> = _liveSavedPostsResponse

    private val _liveAddCommmentResponse = MutableLiveData<BaseResponse>()
    val addCommentResponseData: LiveData<BaseResponse> = _liveAddCommmentResponse

    private val _liveDoctorDetailsResponse = MutableLiveData<DoctorDetailsResponse>()
    val doctorDetailsResponseData: LiveData<DoctorDetailsResponse> = _liveDoctorDetailsResponse

    private val _livePatientDetailsResponse = MutableLiveData<PatientDetailsResponse>()
    val patientDetailsResponseData: LiveData<PatientDetailsResponse> = _livePatientDetailsResponse

    private val _liveAddQuestionResponse = MutableLiveData<BaseResponse>()
    val addQuestionResponseData: LiveData<BaseResponse> = _liveAddQuestionResponse

    private val _liveLikePostResponse = MutableLiveData<BaseResponse>()
    val likePostResponseData: LiveData<BaseResponse> = _liveLikePostResponse

    private val _liveSavePostResponse = MutableLiveData<BaseResponse>()
    val savePostResponseData: LiveData<BaseResponse> = _liveSavePostResponse

    private val _liveReportPostResponse = MutableLiveData<BaseResponse>()
    val reportPostResponseData: LiveData<BaseResponse> = _liveReportPostResponse

    private val _livePostDetailsResponse = MutableLiveData<PostDetailsResponseNew>()
    val getPostDetailsResponseData: LiveData<PostDetailsResponseNew> = _livePostDetailsResponse

    private val _liveCommentPostResponse = MutableLiveData<BaseResponse>()
    val likeCommentResponseData: LiveData<BaseResponse> = _liveCommentPostResponse

    private val _liveCommentDeleteResponse = MutableLiveData<BaseResponse>()
    val commentDeleteResponseData: LiveData<BaseResponse> = _liveCommentDeleteResponse

    private val _livePostDeleteResponse = MutableLiveData<BaseResponse>()
    val postDeleteResponseData: LiveData<BaseResponse> = _livePostDeleteResponse

    private val _liveShortsesponse = MutableLiveData<List<ShortDetails>>()
    val getShortsResponseData: LiveData<List<ShortDetails>> = _liveShortsesponse

    private val _liveVideosesponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getVideosResponseData: LiveData<List<TrendingVideoDetails>> = _liveVideosesponse

    private val _liveHomeVideosesponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getHomeVideosResponseData: LiveData<List<TrendingVideoDetails>> = _liveHomeVideosesponse

    private val _liveQoWResponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getQoWResponseData: LiveData<List<TrendingVideoDetails>> = _liveQoWResponse

    private val _liveMyQuestionsResponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getMyQuestionResponseData: LiveData<List<TrendingVideoDetails>> = _liveMyQuestionsResponse

    private val _liveCommentsListResponse = MutableLiveData<List<CommentItem>>()
    val getCommentsResponseData: LiveData<List<CommentItem>> = _liveCommentsListResponse

    private val _liveHomePatientQueriesResponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getHomePatientQueriesResponseData: LiveData<List<TrendingVideoDetails>> = _liveHomePatientQueriesResponse

    private val _liveHomeOncoDIscussionsResponse = MutableLiveData<List<TrendingVideoDetails>>()
    val getHomeOncoDiscussionsResponseData: LiveData<List<TrendingVideoDetails>> = _liveHomeOncoDIscussionsResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError


    fun callAddCOmment(isEdit: Boolean,token : String, postId : String, addCommentInput: AddCommentInput) {


        _isViewLoading.postValue(true)
        forumsVMImplementor.addCommment(isEdit,
            token,postId, addCommentInput,
            object : ForumsVMRepository.AddCommmentApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }


                override fun onAddCommentSuccess(responseData: BaseResponse?) {
                    Log.d("add_comment_api_log", "Came here in model")
                    _liveAddCommmentResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetDoctorDetails(token : String, doctorId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.getDoctorDetails(
            token,doctorId,
            object : ForumsVMRepository.DoctorDetailsApiCallBack<DoctorDetailsResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }


                override fun onDoctorDetailsSuccess(responseData: DoctorDetailsResponse?) {
                    Log.d("details_log","2")
                    _liveDoctorDetailsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetPatientDetails(isFromDoctor: Boolean = false, token : String, patientId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.getPatientDetails(isFromDoctor,
            token, patientId,
            object : ForumsVMRepository.PatientDetailsApiCallBack<PatientDetailsResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }


                override fun onPatientDetailsSuccess(responseData: PatientDetailsResponse?) {
                    Log.d("details_log","5")
                    _livePatientDetailsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }


    fun callAddQuestion(postId: String, token : String,addQuestionInput: AddQuestionInput) {

        _isViewLoading.postValue(true)
        forumsVMImplementor.addQuestion(postId,
            token, addQuestionInput,
            object : ForumsVMRepository.AddQuestionApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onAddQuestionSuccess(responseData: BaseResponse?) {
                    _liveAddQuestionResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun likeOrUnlikePost(token : String, postId : String) {
        Log.d("video_like","1.1")
        _isViewLoading.postValue(true)
        forumsVMImplementor.likePost(
            token,postId,
            object : ForumsVMRepository.LikePostApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onPostLikeSuccess(responseData: BaseResponse?) {
                    Log.d("video_like","1")
                    _liveLikePostResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }

            })
    }

    fun saveOrUnSavePost(shouldSave: Boolean,token : String, postId : String) {
        Log.d("video_like","1.1")
        _isViewLoading.postValue(true)
        forumsVMImplementor.saveOrUnSavePost(shouldSave,
            token,postId,
            object : ForumsVMRepository.SavePostApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onPostSaveSuccess(responseData: BaseResponse?) {
                    Log.d("video_like","1")
                    _liveSavePostResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }

            })
    }

    fun reportPost(token : String, postId : String) {
        Log.d("video_like","1.1")
        _isViewLoading.postValue(true)
        forumsVMImplementor.reportPost(
            token,postId,
            object : ForumsVMRepository.GetReportPostApiCallBack<BaseResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
                override fun onReportSuccess(responseData: BaseResponse?) {
                    Log.d("video_like","1")
                    _liveReportPostResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }

            })
    }

    fun getPostDetails(token : String, postId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.getPostDetails(
            token,postId,
            object : ForumsVMRepository.PostDetailsApiCallBack<PostDetailsResponseNew?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onPostDetailsSuccess(responseData: PostDetailsResponseNew?) {
                    _livePostDetailsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }

            })
    }

    fun likeOrUnlikeComment(token : String, commentId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.likeCommment(
            token, commentId,
            object : ForumsVMRepository.LikeCommmentApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("like_log","2 err "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onCommentLikeSuccess(responseData: BaseResponse?) {
                    Log.d("like_log","3 success")
                    _liveCommentPostResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }


                /*override fun onAddCommentSuccess(responseData: BaseResponse?) {
                    _liveAddCommmentResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }*/
            })
    }

    fun deleteComment(token : String, commentId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.deleteCommment(
            token, commentId,
            object : ForumsVMRepository.DeleteCommmentApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("like_log","2 err "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onCommentDeleteSuccess(responseData: BaseResponse?) {
                    Log.d("like_log","3 success")
                    _liveCommentDeleteResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun deletePost(token : String, postId : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.deletePost(
            token, postId,
            object : ForumsVMRepository.DeletePostApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("like_log","2 err "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onPostDeleteSuccess(responseData: BaseResponse?) {
                    Log.d("like_log","3 success")
                    _livePostDeleteResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }


            })
    }


    fun callGetComments(token : String, postId : String, getCommentsInput: GetCommentsInput) {


        _isViewLoading.postValue(true)
        forumsVMImplementor.getComments(
            token,postId, getCommentsInput,
            object : ForumsVMRepository.GetCommmentsApiCallBack<CommentsListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onGetCommentSuccess(responseData: CommentsListResponse?) {
                    if (responseData != null) {
                        _liveCommentsListResponse.postValue(responseData.commentItemList)
                    }else{
                        _onMessageError.postValue("No data found")
                    }
                    _isViewLoading.postValue(false)
                }


                /*override fun onAddCommentSuccess(responseData: BaseResponse?) {
                    _liveAddCommmentResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }*/
            })
    }


    fun callGetTrendingBlogs(token : String) {


        _isViewLoading.postValue(true)
        forumsVMImplementor.getLiveBlogs(
            token,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {


                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveBlogsesponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetSavedPosts(token : String) {
        _isViewLoading.postValue(true)
        forumsVMImplementor.getSavedPosts(
            token,
            object : ForumsVMRepository.SavedPostsListApiCallBack<TrendingVideosListResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveSavedPostsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetHomeVideos(token : String, searchQueryInput: SearchQueryInput) {
        _isViewLoading.postValue(true)
        Log.d("home_trending_video","1")
        forumsVMImplementor.getAllLiveBlogs(
            token,searchQueryInput,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    Log.d("home_trending_video","2")
                    _liveHomeVideosesponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetShorts(token : String) {

        _isViewLoading.postValue(true)
        forumsVMImplementor.getShortsList(
            token,
            object : ForumsVMRepository.ShortsListApiCallBack<ShortsListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onShortsListSuccess(responseData: List<ShortDetails>?) {
                    Log.d("api_call_log","shorts success "+responseData?.size)
                    _liveShortsesponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetTrendingVideos(token : String) {


        Log.d("home_trending_video","1")
        _isViewLoading.postValue(true)
        forumsVMImplementor.getLiveVideos(
            token,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveHomeVideosesponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetAllVideos(source : String,token : String, searchQueryInput: SearchQueryInput) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        forumsVMImplementor.getAllLiveVideos(source,
            token, searchQueryInput,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {


                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveVideosesponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callQoW(token : String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        forumsVMImplementor.getQuestionOfTheWeek(
            token,
            object : ForumsVMRepository.QoWApiCallBack<TrendingVideosListResponse?> {


                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onQowSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveQoWResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetMyQuestions(token : String, shouldSHowTrendingQuestions: Boolean = false) {
        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        forumsVMImplementor.getMyQuestions(
            token,
            object : ForumsVMRepository.MyQuestionsListApiCallBack<TrendingVideosListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onQuestionsListSuccess(responseData: List<TrendingVideoDetails>?) {
                    _liveMyQuestionsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            }, shouldSHowTrendingQuestions)
    }

    fun callGetHomePatientQueries(token : String, searchQueryInput: SearchQueryInput) {
        _isViewLoading.postValue(true)
        Log.d("home_trending_video","1")
        forumsVMImplementor.getAllLiveVideos(Constants.EXPERT_QUESTIONS_LIST,
            token,searchQueryInput,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    Log.d("home_trending_video","2")
                    _liveHomePatientQueriesResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetHomeOncoDiscussions(token : String, searchQueryInput: SearchQueryInput) {
        _isViewLoading.postValue(true)
        Log.d("home_trending_video","1")
        forumsVMImplementor.getAllLiveVideos(Constants.ONCO_DISCUSSIONS_LIST,
            token,searchQueryInput,
            object : ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?> {

                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onVideosListSuccess(responseData: List<TrendingVideoDetails>?) {
                    Log.d("home_trending_video","2")
                    _liveHomeOncoDIscussionsResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

}