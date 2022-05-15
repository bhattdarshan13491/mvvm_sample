package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.BaseImplementor
import com.oncobuddy.app.models.mvvm_implementors.VerifyOTPVMImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.DeleteAccountInput
import com.oncobuddy.app.models.pojo.chats.MessageInput
import com.oncobuddy.app.models.pojo.chats.chat_groups.ChatGroupResponse
import com.oncobuddy.app.models.pojo.chats.chat_messages.ChatListResponse
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput
import com.oncobuddy.app.utils.Constants
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Responsitory to handle all chat related network calls
 */
class ChatVMRepository : ChatVMImplementor{

    private var getChatGroupsApiCall : Call<ChatGroupResponse?>? = null
    private var sendMessageApiCall : Call<BaseResponse?>? = null
    private var startMeessageApiCall : Call<BaseResponse?>? = null
    private var getchatListingApiCall : Call<ChatListResponse?>? = null

    interface ChatGroupsApiCallBack<T>{
        fun onSuccess(responseData : ChatGroupResponse?)
        fun onError(message : String?)
    }

    interface SendMessageApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface StartCHatApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface ChatListApiCallBack<T>{
        fun onSuccess(responseData : ChatListResponse?)
        fun onError(message : String?)
    }

    override fun getCHatGroups(token: String, callBack: ChatGroupsApiCallBack<ChatGroupResponse?>) {
        getChatGroupsApiCall = ApiClient.build()?.doGetActiveGroups(token)

        getChatGroupsApiCall?.enqueue(object : Callback<ChatGroupResponse?>{

            override fun onFailure(call: Call<ChatGroupResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<ChatGroupResponse?>?,
                                    response: Response<ChatGroupResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }
                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun sendMessage(token: String, messageInput: MessageInput, callBack: SendMessageApiCallBack<BaseResponse?>) {
        sendMessageApiCall = ApiClient.build()?.doSendMessage(token, messageInput)

        sendMessageApiCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }
                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun startChat(
        token: String,
        receiverId: String,
        callBack: StartCHatApiCallBack<BaseResponse?>
    ) {
        startMeessageApiCall = ApiClient.build()?.doStartChat(token, receiverId)

        startMeessageApiCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>?,
                                    response: Response<BaseResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }
                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callBack.onError( jObjError.getString("message"))
                        Log.d("emergency_api_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("emergency_api_log","API Failed here "+ignored.toString())
                    }

                    Log.d("emergency_api_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getCHatList(token: String,groupId: String , callBack: ChatListApiCallBack<ChatListResponse?>) {
        getchatListingApiCall = ApiClient.build()?.doGetAllChats(token, groupId)

        getchatListingApiCall?.enqueue(object : Callback<ChatListResponse?>{

            override fun onFailure(call: Call<ChatListResponse?>?, t: Throwable?) {
                Log.d("emergency_api_log", "repo Error "+t?.message)
                callBack.onError(t?.message)

            }

            override fun onResponse(call: Call<ChatListResponse?>?,
                                    response: Response<ChatListResponse?>) {

                if(response.code() == 400){
                    Log.d("emergency_api_log", "Catched")
                }
                Log.d("emergency_api_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callBack.onSuccess(response.body())
                    Log.d("emergency_api_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callBack.onError( jObjError.getString("message"))
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
          getChatGroupsApiCall?.cancel()
    }

}
interface ChatVMImplementor : BaseImplementor {
    fun getCHatGroups(token: String, callBack: ChatVMRepository.ChatGroupsApiCallBack<ChatGroupResponse?>)
    fun sendMessage(token: String, messageInput: MessageInput, callBack: ChatVMRepository.SendMessageApiCallBack<BaseResponse?>)
    fun startChat(token: String, receiverId: String, callBack: ChatVMRepository.StartCHatApiCallBack<BaseResponse?>)
    fun getCHatList(token: String,groupId: String, callBack: ChatVMRepository.ChatListApiCallBack<ChatListResponse?>)
}