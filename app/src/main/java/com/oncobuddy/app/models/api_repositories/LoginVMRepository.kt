package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.LoginVMImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.initial_login.VerifyLoginOtpInput
import com.oncobuddy.app.models.pojo.login_response.LoginInput
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

/**
 * Responsitory to handle all login related calls
 */

class LoginVMRepository : LoginVMImplementor {

    private var loginApicall : Call<LoginResponse?>? = null
    private var getOtpApiCall : Call<BaseResponse?>? = null
    private var verifyOtpApiCall : Call<LoginResponse?>? = null


    interface ApiCallBack<T>{
        fun onSuccess(responseData : Response<LoginResponse?>)
        fun onError(message : String?)
    }

    interface BaseResponseApiCallBack<T>{
        fun onSuccess(responseData : Response<BaseResponse?>)
        fun onError(message : String?)
    }



    override fun retriveLoginData(loginInput: LoginInput,
                                  callback: ApiCallBack<LoginResponse?>) {

        loginApicall = ApiClient.build()?.doLogin(loginInput)

        loginApicall?.enqueue(object : Callback<LoginResponse?>{

             override fun onFailure(call: Call<LoginResponse?>, t: Throwable?) {
                 Log.d("login_log", "Error "+t?.message)
                 callback.onError(t?.message)

             }


            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                if(response.code() == 400){
                    Log.d("login_log", "Catched")
                }

                Log.d("login_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response)
                    Log.d("login_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("login_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("login_log","API Failed here "+ignored.toString())
                    }

                    Log.d("login_log", "Err2 "+response.message())
                }
            }



        })

    }

    override fun getOTPForLogin(
        loginInput: com.oncobuddy.app.models.pojo.initial_login.LoginInput,
        callback: BaseResponseApiCallBack<BaseResponse?>, isFOrRegistration: Boolean
    )
    {
        if(isFOrRegistration){

            getOtpApiCall = ApiClient.build()?.doGetOtpForRegistration(loginInput)
        }else{
            getOtpApiCall = ApiClient.build()?.doGetOtpForLogin(loginInput)
        }


        getOtpApiCall?.enqueue(object : Callback<BaseResponse?>{

            override fun onFailure(call: Call<BaseResponse?>, t: Throwable?) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)

            }


            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                if(response.code() == 400){
                    Log.d("login_log", "Catched")
                }

                Log.d("login_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response)
                    Log.d("login_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("login_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("login_log","API Failed here "+ignored.toString())
                    }

                    Log.d("login_log", "Err2 "+response.message())
                }
            }



        })
    }

    override fun verifyOTP(
        verifyLoginOtpInput: VerifyLoginOtpInput,
        callback: ApiCallBack<LoginResponse?>
    ) {
        verifyOtpApiCall = ApiClient.build()?.doVeriFyOtp(verifyLoginOtpInput)

        verifyOtpApiCall?.enqueue(object : Callback<LoginResponse?>{

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable?) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                if(response.code() == 400){
                    Log.d("login_log", "Catched")
                }

                Log.d("login_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response)
                    Log.d("login_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("login_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("login_log","API Failed here "+ignored.toString())
                    }

                    Log.d("login_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun cancel() {
          loginApicall?.cancel()
    }
}