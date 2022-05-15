package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.BaseImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_details.DoctorDetailsResponse
import com.oncobuddy.app.models.pojo.ecrf_events.Patient
import com.oncobuddy.app.models.pojo.forums.AddQuestionInput
import com.oncobuddy.app.models.pojo.forums.comments.CommentsListResponse
import com.oncobuddy.app.models.pojo.forums.comments.GetCommentsInput
import com.oncobuddy.app.models.pojo.forums.post_details.PostDetailsResponseNew
import com.oncobuddy.app.models.pojo.forums.shorts.ShortDetails
import com.oncobuddy.app.models.pojo.forums.shorts.ShortsListResponse
import com.oncobuddy.app.models.pojo.forums.trending_blogs.AddCommentInput
import com.oncobuddy.app.models.pojo.forums.trending_blogs.TrendingBlogDetails
import com.oncobuddy.app.models.pojo.forums.trending_blogs.TrendingBlogsListResponse
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideoDetails
import com.oncobuddy.app.models.pojo.forums.trending_videos.TrendingVideosListResponse
import com.oncobuddy.app.models.pojo.patient_profile.PatientDetailsResponse
import com.oncobuddy.app.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Responsitory to handle all Oncohub related network calls
 */

class ForumsVMRepository : ForumsVMImplementor {

    private var getTrendingVideosListCall : Call<TrendingVideosListResponse?>? = null
    private var getSavedPostsListCall : Call<TrendingVideosListResponse?>? = null
    private var questionOfTheWeekListCall : Call<TrendingVideosListResponse?>? = null
    private var getMyQuestionListCall : Call<TrendingVideosListResponse?>? = null
    private var getCommentsListCall : Call<CommentsListResponse?>? = null
    private var addCommmentApiCallBack : Call<BaseResponse?>? = null
    private var reportPostApiCallBack : Call<BaseResponse?>? = null
    private var addQuestionApiCallBack : Call<BaseResponse?>? = null
    private var likePostApiCallBack : Call<BaseResponse?>? = null
    private var saveUnsavePostApiCallBack : Call<BaseResponse?>? = null
    private var getPostDetailsApiCallBack : Call<PostDetailsResponseNew?>? = null
    private var likeCOmmentApiCallBack : Call<BaseResponse?>? = null
    private var deleteCOmmentApiCallBack : Call<BaseResponse?>? = null
    private var deletePostApiCallBack : Call<BaseResponse?>? = null
    private var getTrendingBlogsListCall : Call<TrendingVideosListResponse?>? = null
    private var getShortsListCall : Call<ShortsListResponse?>? = null
    private var getDoctorDetailsResponse : Call<DoctorDetailsResponse?>? = null
    private var getPatientDetailsResponse : Call<PatientDetailsResponse?>? = null

    interface DoctorDetailsApiCallBack<T>{
        fun onDoctorDetailsSuccess(responseData : DoctorDetailsResponse?)
        fun onError(message : String?)
    }

    interface PatientDetailsApiCallBack<T>{
        fun onPatientDetailsSuccess(responseData : PatientDetailsResponse?)
        fun onError(message : String?)
    }

    interface VideosListApiCallBack<T>{
        fun onVideosListSuccess(responseData : List<TrendingVideoDetails>?)
        fun onError(message : String?)
    }

    interface SavedPostsListApiCallBack<T>{
        fun onVideosListSuccess(responseData : List<TrendingVideoDetails>?)
        fun onError(message : String?)
    }

    interface QoWApiCallBack<T>{
        fun onQowSuccess(responseData : List<TrendingVideoDetails>?)
        fun onError(message : String?)
    }

    interface MyQuestionsListApiCallBack<T>{
        fun onQuestionsListSuccess(responseData : List<TrendingVideoDetails>?)
        fun onError(message : String?)
    }

    interface AddCommmentApiCallBack<T>{
        fun onAddCommentSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface AddQuestionApiCallBack<T>{
        fun onAddQuestionSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface LikePostApiCallBack<T>{
        fun onPostLikeSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface SavePostApiCallBack<T>{
        fun onPostSaveSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface LikeCommmentApiCallBack<T>{
        fun onCommentLikeSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface DeleteCommmentApiCallBack<T>{
        fun onCommentDeleteSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface DeletePostApiCallBack<T>{
        fun onPostDeleteSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface PostDetailsApiCallBack<T>{
        fun onPostDetailsSuccess(responseData : PostDetailsResponseNew?)
        fun onError(message : String?)
    }

    interface GetCommmentsApiCallBack<T>{
        fun onGetCommentSuccess(responseData : CommentsListResponse?)
        fun onError(message : String?)
    }

    interface GetReportPostApiCallBack<T>{
        fun onReportSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface BlogsListApiCallBack<T>{
        fun onBlogsListSuccess(responseData : List<TrendingBlogDetails>?)
        fun onError(message : String?)
    }

    interface ShortsListApiCallBack<T>{
        fun onShortsListSuccess(responseData : List<ShortDetails>?)
        fun onError(message : String?)
    }

    override fun getLiveVideos(
        token: String,
        callback: VideosListApiCallBack<TrendingVideosListResponse?>
    ) {

        getTrendingVideosListCall = ApiClient.build()?.doGetTrendingVideosList(token)

        getTrendingVideosListCall?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onVideosListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getAllLiveVideos(
        source: String,
        token: String,
        searchQueryInput: SearchQueryInput,
        callback: VideosListApiCallBack<TrendingVideosListResponse?>
    ) {
        if(source.equals(Constants.VIDEO_LIST)){
            getTrendingVideosListCall = ApiClient.build()?.doGetAllVideosList(token, searchQueryInput)
        }else if(source.equals(Constants.BLOG_LIST)){
            getTrendingVideosListCall = ApiClient.build()?.doGetAllBlogsList(token, searchQueryInput)
        }else if(source.equals(Constants.COMMUNITY_LIST)){
            getTrendingVideosListCall = ApiClient.build()?.doGetCommunityPost(token, searchQueryInput)
        }else if(source.equals(Constants.EXPERT_QUESTIONS_LIST)){
            getTrendingVideosListCall = ApiClient.build()?.doGetExpertQuestions(token, searchQueryInput)
        }else if(source.equals(Constants.ONCO_DISCUSSIONS_LIST)){
            getTrendingVideosListCall = ApiClient.build()?.doGetOncoDiscussions(token, searchQueryInput)
        }
        else{
            getTrendingVideosListCall = ApiClient.build()?.doGetAllQuestionsList(token, searchQueryInput)
        }


        getTrendingVideosListCall?.clone()?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onVideosListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getQuestionOfTheWeek(
        token: String,
        callback: QoWApiCallBack<TrendingVideosListResponse?>
    ) {
        questionOfTheWeekListCall = ApiClient.build()?.doQuestionOfTheWeek(token)

        questionOfTheWeekListCall?.clone()?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onQowSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getAllLiveBlogs(
        token: String,
        searchQueryInput: SearchQueryInput,
        callback: VideosListApiCallBack<TrendingVideosListResponse?>
    ) {
        getTrendingBlogsListCall = ApiClient.build()?.doGetAllBlogsList(token, searchQueryInput)

        getTrendingBlogsListCall?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onVideosListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getMyQuestions(
        token: String,
        callback: MyQuestionsListApiCallBack<TrendingVideosListResponse?>,
        shouldSHowTrendingQuestions: Boolean
    ) {
        if(shouldSHowTrendingQuestions){
            getMyQuestionListCall = ApiClient.build()?.doGetTrendingQuestions(token)
        }else{
            getMyQuestionListCall = ApiClient.build()?.doGetMyQuestions(token)
        }

        getMyQuestionListCall?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onQuestionsListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getComments(
        token: String,
        postId: String,
        getCommentsInput: GetCommentsInput,
        callback: GetCommmentsApiCallBack<CommentsListResponse?>
    ) {
        getCommentsListCall = ApiClient.build()?.getCommentsList(token, postId, getCommentsInput)

        getCommentsListCall?.enqueue(object : Callback<CommentsListResponse?>{

            override fun onFailure(call: Call<CommentsListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<CommentsListResponse?>?,
                                    response: Response<CommentsListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onGetCommentSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun reportPost(
        token: String,
        postId: String,
        callback: GetReportPostApiCallBack<BaseResponse?>
    ) {
        reportPostApiCallBack = ApiClient.build()?.doReportPost(token, postId)

        reportPostApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onReportSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun addCommment(
        isEdit:Boolean,
        token: String,
        postId: String,
        addCommentInput: AddCommentInput,
        callback: AddCommmentApiCallBack<BaseResponse?>
    ) {
        if(isEdit){
            Log.d("add_comment_api_log", "Edit called")
            addCommmentApiCallBack = ApiClient.build()?.editCommment(token, postId, addCommentInput)
        }else{
            Log.d("add_comment_api_log", "Add called")
            addCommmentApiCallBack = ApiClient.build()?.addCommment(token, postId, addCommentInput)
        }

        addCommmentApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("add_comment_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("add_comment_api_log", "Catched")
                }

                Log.d("add_comment_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onAddCommentSuccess(response.body())
                    Log.d("add_comment_api_log", "successcame "+response.body())
                    Log.d("add_comment_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("add_comment_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("add_comment_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("add_comment_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun addQuestion(
        postId: String,
        token: String,
        addQuestionInput: AddQuestionInput,
        callback: AddQuestionApiCallBack<BaseResponse?>
    ) {
        if(!postId.isNullOrEmpty()){
            addCommmentApiCallBack = ApiClient.build()?.editQuestion(token, postId, addQuestionInput)

        }else{
            addCommmentApiCallBack = ApiClient.build()?.addQuestion(token, addQuestionInput)

        }

        addCommmentApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onAddQuestionSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getLiveBlogs(token: String, callback:VideosListApiCallBack<TrendingVideosListResponse?>) {
        getTrendingBlogsListCall = ApiClient.build()?.doGetTrendingBlogsList(token)

        getTrendingBlogsListCall?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onVideosListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getSavedPosts(
        token: String,
        callback: SavedPostsListApiCallBack<TrendingVideosListResponse?>
    ) {
        getSavedPostsListCall = ApiClient.build()?.doGetSavedPosts(token)

        getSavedPostsListCall?.enqueue(object : Callback<TrendingVideosListResponse?>{

            override fun onFailure(call: Call<TrendingVideosListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<TrendingVideosListResponse?>?,
                                    response: Response<TrendingVideosListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onVideosListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getShortsList(token: String, callback: ShortsListApiCallBack<ShortsListResponse?>) {
        getShortsListCall = ApiClient.build()?.doGetShortsList(token)

        getShortsListCall?.enqueue(object : Callback<ShortsListResponse?>{

            override fun onFailure(call: Call<ShortsListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<ShortsListResponse?>?,
                                    response: Response<ShortsListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onShortsListSuccess(response.body()?.payLoad)
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun likePost(token: String, postId: String, callback: LikePostApiCallBack<BaseResponse?>) {
        Log.d("video_like","6")
        likePostApiCallBack = ApiClient.build()?.doLikeOrUnlikePost(token, postId)

        likePostApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("video_like", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("video_like", "Catched")
                }

                Log.d("video_like", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onPostLikeSuccess(response.body())
                    Log.d("video_like", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("video_like","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("video_like","API Failed here "+ignored.toString())
                    }

                    Log.d("video_like", "Err2 "+response.message())
                }
            }
        })

    }

    override fun saveOrUnSavePost(
        shoulsSave: Boolean,
        token: String,
        postId: String,
        callback: SavePostApiCallBack<BaseResponse?>
    ) {
        if(shoulsSave)
            saveUnsavePostApiCallBack = ApiClient.build()?.doSavePost(token, postId)
        else
            saveUnsavePostApiCallBack = ApiClient.build()?.doUnsavePost(token, postId)

        saveUnsavePostApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onPostSaveSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun likeCommment(token: String, postId: String, callback: LikeCommmentApiCallBack<BaseResponse?>) {
        likeCOmmentApiCallBack = ApiClient.build()?.doLikeOrUnlikeCOmment(token, postId)

        likeCOmmentApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onCommentLikeSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun deleteCommment(
        token: String,
        postId: String,
        callback: DeleteCommmentApiCallBack<BaseResponse?>
    ) {
        deleteCOmmentApiCallBack = ApiClient.build()?.doDeleteCOmment(token, postId)

        deleteCOmmentApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onCommentDeleteSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun deletePost(
        token: String,
        postId: String,
        callback: DeletePostApiCallBack<BaseResponse?>
    ) {
        deletePostApiCallBack = ApiClient.build()?.doDeletePost(token, postId)

        deletePostApiCallBack?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onPostDeleteSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }

    override fun getPostDetails(
        token: String,
        postId: String,
        callback: PostDetailsApiCallBack<PostDetailsResponseNew?>
    ) {
        getPostDetailsApiCallBack = ApiClient.build()?.getPostDetails(token, postId)

        getPostDetailsApiCallBack?.enqueue(object : Callback<PostDetailsResponseNew?>{

            override fun onFailure(call: Call<PostDetailsResponseNew?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<PostDetailsResponseNew?>?,
                                    response: Response<PostDetailsResponseNew?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onPostDetailsSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })

    }


    override fun getDoctorDetails(
        token: String,
        doctorId: String,
        callback: DoctorDetailsApiCallBack<DoctorDetailsResponse?>
    ) {
        getDoctorDetailsResponse = ApiClient.build()?.doGetDoctorDetails(token, doctorId)

        getDoctorDetailsResponse?.enqueue(object : Callback<DoctorDetailsResponse?>{

            override fun onFailure(call: Call<DoctorDetailsResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<DoctorDetailsResponse?>?,
                                    response: Response<DoctorDetailsResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onDoctorDetailsSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getPatientDetails(
        isFromDoctor: Boolean,
        token: String,
        patientId: String,
        callback: PatientDetailsApiCallBack<PatientDetailsResponse?>
    ) {
        if(isFromDoctor){
            getPatientDetailsResponse = ApiClient.build()?.doGetPatientDetailsByDoctor(token, patientId)
        }else{
            getPatientDetailsResponse = ApiClient.build()?.doGetPatientDetails(token, patientId)
        }


        getPatientDetailsResponse?.enqueue(object : Callback<PatientDetailsResponse?>{

            override fun onFailure(call: Call<PatientDetailsResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<PatientDetailsResponse?>?,
                                    response: Response<PatientDetailsResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }

                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onPatientDetailsSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }


    override fun cancel() {
          getTrendingBlogsListCall?.cancel()
    }



}

interface ForumsVMImplementor : BaseImplementor {
    fun getLiveVideos(token: String,callback: ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?>)
    fun getAllLiveVideos(source : String,token: String, searchQueryInput: SearchQueryInput, callback: ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?>)
    fun getQuestionOfTheWeek(token: String, callback: ForumsVMRepository.QoWApiCallBack<TrendingVideosListResponse?>)
    fun getAllLiveBlogs(token: String, searchQueryInput: SearchQueryInput, callback: ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?>)
    fun getMyQuestions(token: String,callback: ForumsVMRepository.MyQuestionsListApiCallBack<TrendingVideosListResponse?>, shouldSHowTrendingQuestions: Boolean)
    fun getComments(token: String, postId: String, getCommentsInput: GetCommentsInput, callback: ForumsVMRepository.GetCommmentsApiCallBack<CommentsListResponse?>)
    fun reportPost(token: String, postId: String, callback: ForumsVMRepository.GetReportPostApiCallBack<BaseResponse?>)
    fun addCommment(isEdit: Boolean = false,token: String, postId: String, addCommentInput: AddCommentInput, callback: ForumsVMRepository.AddCommmentApiCallBack<BaseResponse?>)
    fun addQuestion(postId: String= "",token: String,addQuestionInput: AddQuestionInput, callback: ForumsVMRepository.AddQuestionApiCallBack<BaseResponse?>)
    fun getLiveBlogs(token: String, callback: ForumsVMRepository.VideosListApiCallBack<TrendingVideosListResponse?>)
    fun getSavedPosts(token: String, callback: ForumsVMRepository.SavedPostsListApiCallBack<TrendingVideosListResponse?>)
    fun getShortsList(token: String, callback: ForumsVMRepository.ShortsListApiCallBack<ShortsListResponse?>)
    fun likePost(token: String, postId: String, callback: ForumsVMRepository.LikePostApiCallBack<BaseResponse?>)
    fun saveOrUnSavePost(shoulsSave: Boolean,token: String, postId: String, callback: ForumsVMRepository.SavePostApiCallBack<BaseResponse?>)
    fun likeCommment(token: String, postId: String, callback: ForumsVMRepository.LikeCommmentApiCallBack<BaseResponse?>)
    fun deleteCommment(token: String, postId: String, callback: ForumsVMRepository.DeleteCommmentApiCallBack<BaseResponse?>)
    fun deletePost(token: String, postId: String, callback: ForumsVMRepository.DeletePostApiCallBack<BaseResponse?>)
    fun getPostDetails(token: String, postId: String, callback: ForumsVMRepository.PostDetailsApiCallBack<PostDetailsResponseNew?>)
    fun getDoctorDetails(token: String, doctorId: String, callback: ForumsVMRepository.DoctorDetailsApiCallBack<DoctorDetailsResponse?>)
    fun getPatientDetails(isFromDoctor: Boolean,token: String, patientId: String, callback: ForumsVMRepository.PatientDetailsApiCallBack<PatientDetailsResponse?>)

}