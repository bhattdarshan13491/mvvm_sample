package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.oncobuddy.app.models.mvvm_implementors.VerifyOTPVMImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.otp_process.ForgotPwdInput
import com.oncobuddy.app.models.pojo.otp_process.GenerateOTPInput
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


/**
 * Responsitory to handle forgot pwd and change pwd related network calls
 */
class VerifyOTPVMRepository : VerifyOTPVMImplementor{

    private var forgotPwdApicall : Call<BaseResponse?>? = null
    private var generateOTPApicall : Call<BaseResponse?>? = null

    interface ApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface VerifyOtpApiApiCallBack<T>{
        fun onSuccess(responseData : Response<BaseResponse?>)
        fun onError(message : String?)
    }

    override fun generateOTP(
        generateOTPInput: GenerateOTPInput,
        callback: ApiCallBack<BaseResponse?>
    ) {
        generateOTPApicall = ApiClient.build()?.doGenerateOTP(generateOTPInput)

        generateOTPApicall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("change_pwd_log", "Error "+t?.message)
                Log.d("change_pwd_log","4")
                callback.onError(t?.message)
            }

            override fun onResponse(call: Call<BaseResponse?>?, response: Response<BaseResponse?>?) {
                Log.d("change_pwd_log", "Ended 0")
                Log.d("change_pwd_log", "Ended response "+response.toString())

                if(response?.body() == null){
                    Log.d("change_pwd_log", "Response body is null")
                    callback.onError("User not found")

                }else{
                    response?.body()?.let {baseResponse ->
                        Log.d("change_pwd_log", "Ended 1 "+response.code())
                        if(response.code() == 400){
                            Log.d("change_pwd_log", "Catched")
                            Log.d("change_pwd_log","5")
                        }

                        if(response.isSuccessful){
                            callback.onSuccess(baseResponse)
                            Log.d("change_pwd_log","6")
                            Log.d("change_pwd_log", "success2 "+response.message())
                        }else {
                            try {
                                assert(response.errorBody() != null)
                                val jObjError =
                                    JSONObject(response.errorBody()!!.string())

                                Log.d("change_pwd_log", "Assert err")
                                callback.onError(jObjError.getString("message"))
                                Log.d(
                                    "change_pwd_log",
                                    "Wow I caught this ! " + jObjError.getString("message")
                                )
                            } catch (ignored: Exception) {
                                Log.d("change_pwd_log", "ignored")
                                Log.d("change_pwd_log", "API Failed here " + ignored.toString())
                            }

                            Log.d("change_pwd_log", "Err2 " + response.message())
                            Log.d("change_pwd_log", "Err 2")
                        }
                    }
                }


            }



        })
    }

    override fun forgotPassword(
        forgotPwdInput: ForgotPwdInput,
        callback: ApiCallBack<BaseResponse?>
    ) {
        Log.d("change_pwd_log", "Came here in repo")
        forgotPwdApicall = ApiClient.build()?.doCallForgotPassword(forgotPwdInput)
        Log.d("change_pwd_log", "enqueued")
        forgotPwdApicall?.enqueue(object : Callback<BaseResponse?> {
            override fun onFailure(call: Call<BaseResponse?>?, t: Throwable?) {
                Log.d("change_pwd_log", "Error " + t?.message)
                Log.d("change_pwd_log", "FError " + t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<BaseResponse?>, response: Response<BaseResponse?>) {
                Log.d("change_pwd_log", "repo response " + response.code())
                if (response.code() == 400) {
                    Log.d("change_pwd_log", "Catched")
                    Log.d("change_pwd_log", "Error catched")
                }

                if (response.isSuccessful) {
                    callback.onSuccess(response.body())
                    Log.d("change_pwd_log", "Success")
                    Log.d("change_pwd_log", "success2 " + response.message())
                } else {
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())

                        Log.d("change_pwd_log", "Assert err")
                        callback.onError(jObjError.getString("message"))
                        Log.d(
                            "change_pwd_log",
                            "Wow I caught this ! " + jObjError.getString("message")
                        )
                    } catch (ignored: Exception) {
                        Log.d("change_pwd_log", "ignored")
                        Log.d("change_pwd_log", "API Failed here " + ignored.toString())
                    }

                    Log.d("change_pwd_log", "Err2 " + response.message())
                    Log.d("change_pwd_log", "Err 2")
                }
            }
        })
    }

    override fun cancel() {
          forgotPwdApicall?.cancel()
    }


}