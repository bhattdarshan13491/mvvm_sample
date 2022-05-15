package com.oncobuddy.app.models.api_repositories

import android.util.Log
import com.google.gson.Gson
import com.oncobuddy.app.models.mvvm_implementors.AppointmentVMImplementor
import com.oncobuddy.app.models.network_data.ApiClient
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.CancelAppointmentResponse
import com.oncobuddy.app.models.pojo.SearchQueryInput
import com.oncobuddy.app.models.pojo.appointments.AddAppointmentResponse
import com.oncobuddy.app.models.pojo.appointments.input.AddAppointmentInput
import com.oncobuddy.app.models.pojo.appointments.input.AddGuestInput
import com.oncobuddy.app.models.pojo.appointments.input.PaymentInput
import com.oncobuddy.app.models.pojo.appointments.input.TimeSlotsInput
import com.oncobuddy.app.models.pojo.appointments.list_response.AppointmentsListResponse
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.doctors.time_slots_listing.TimeSlotsListingResponse
import com.oncobuddy.app.models.pojo.guest_details.GuestDetailsResponse
import com.oncobuddy.app.models.pojo.login_response.LoginDetails
import com.oncobuddy.app.models.pojo.patient_list.PatientsListResponse
import com.oncobuddy.app.models.pojo.twilio_token.TokenResponse
import com.oncobuddy.app.models.pojo.vouchers_response.VoucherListResponse
import com.oncobuddy.app.utils.Constants
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Body

/**
 * Responsitory to handle all appointment related network calls
 */

class AppointmentVMRepository : AppointmentVMImplementor {

    private var addGuestApiCall : Call<BaseResponse?>? = null
    private var requestRescheduleApiCall : Call<BaseResponse?>? = null
    private var vouchersListApiCall : Call<VoucherListResponse?>? = null
    private var getGuestApiCall : Call<GuestDetailsResponse?>? = null
    private var cancelAppointmentApiCall : Call<ResponseBody?>? = null
    private var startCallApiCall : Call<BaseResponse?>? = null
    private var endCallApiCall : Call<BaseResponse?>? = null
    private var requestCallApiCall : Call<BaseResponse?>? = null
    private var tokenApiCall : Call<TokenResponse?>? = null
    private var paymentUpdateAPiCall : Call<BaseResponse?>? = null
    private var addAppointmentCall : Call<AddAppointmentResponse?>? = null
    private var rescheduleAppointmentCall : Call<ResponseBody?>? = null
    private var getAppointmentCall : Call<AppointmentsListResponse?>? = null
    private var timeslotApiCall : Call<TimeSlotsListingResponse?>? = null
    private var doctorListingApiCall : Call<DoctorsListingResponse?>? = null
    private var patientListingApiCall : Call<PatientsListResponse?>? = null


    interface ApiCallBack<T>{
        fun onSuccess(responseData : AddAppointmentResponse?)
        fun onError(message : String?)
    }

    interface VouchersListAPiCallBack<T>{
        fun onSuccess(responseData : VoucherListResponse?)
        fun onError(message : String?)
    }

    interface GetGuestApiCallBack<T>{
        fun onGuestSuccess(responseData : GuestDetailsResponse?)
        fun onError(message : String?)
    }

    interface AddGuestApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface RequestRescheduleApiCallBack<T>{
        fun onRescheduleSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface RescheduleApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface CancelApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface StartCallApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface GetTokenApiCallBack<T>{
        fun onSuccess(responseData : TokenResponse?)
        fun onError(message : String?)
    }

    interface EndCallApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface RequestCallApiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface UpdatePaymentAPiCallBack<T>{
        fun onSuccess(responseData : BaseResponse?)
        fun onError(message : String?)
    }

    interface PatientListAPiCallBack<T>{
        fun onSuccess(responseData : PatientsListResponse?)
        fun onError(message : String?)
    }

    interface ListApiCallBack<T>{
        fun onListSuccess(responseData : Response<AppointmentsListResponse?>)
        fun onError(message : String?)
    }

    interface TimeSLotApiCallBack<T>{
        fun onTimeSLotListSuccess(responseData : TimeSlotsListingResponse?)
        fun onError(message : String?)
    }

    interface DoctorListApiCallBack<T>{
        fun onDoctorListSuccess(responseData : DoctorsListingResponse?)
        fun onError(message : String?)
    }



    override fun addOrUpdateAppointment(
        addAppointmentInput: AddAppointmentInput,
        token: String,
        callback: ApiCallBack<AddAppointmentResponse?>
    ) {
        addAppointmentCall = ApiClient.build()?.doAddOrUpdateAppointment(addAppointmentInput, token)

        addAppointmentCall?.enqueue(object : Callback<AddAppointmentResponse?>{
            override fun onFailure(call: Call<AddAppointmentResponse?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<AddAppointmentResponse?>,
                response: Response<AddAppointmentResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }

                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun updatePayment(
        token: String,
        appointmentId: String,
        paymentInput: PaymentInput,
        callback: UpdatePaymentAPiCallBack<BaseResponse?>
    ) {
        paymentUpdateAPiCall = ApiClient.build()?.doPayment(token, appointmentId, paymentInput)

        paymentUpdateAPiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("payment_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("payment_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("payment_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("payment_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun getPatientList(
        source: String,
        token: String,
        @Body searchQueryInput: SearchQueryInput,
        callback: PatientListAPiCallBack<PatientsListResponse?>
    ) {
        if(source.equals(Constants.ROLE_CARE_COMPANION)){
            patientListingApiCall = ApiClient.build()?.doGetPatientsByCareCOmpanion(token, searchQueryInput)
        }else{
            patientListingApiCall = ApiClient.build()?.doGetPatients(token)
        }


        patientListingApiCall?.enqueue(object : Callback<PatientsListResponse?>{
            override fun onFailure(call: Call<PatientsListResponse?>, t: Throwable) {
                Log.d("payment_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<PatientsListResponse?>,
                response: Response<PatientsListResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("payment_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("payment_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("payment_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun rescheduleAppointment(
        addAppointmentInput: AddAppointmentInput,
        token: String,
        appointmentId: String,
        callback: RescheduleApiCallBack<BaseResponse?>
    ) {
        rescheduleAppointmentCall = ApiClient.build()?.doRescheduleAppointment(addAppointmentInput, token, appointmentId)

        rescheduleAppointmentCall?.enqueue(object : Callback<ResponseBody?>{
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {
                if(response.code() == 500){
                    callback.onError("Something went wrong while rescheduling appointment")
                }else{
                    response?.body()?.let {baseResponse ->
                        //Log.d("cancel_app_log","1 "+baseResponse.string())
                        if(response.code() == 400){
                            Log.d("cancel_app_log", "Catched")
                        }
                        if(response.isSuccessful){
                            var str = ""+baseResponse.string()
                            val gson = Gson()
                            val baseResponseObj:BaseResponse = gson.fromJson(str, BaseResponse::class.java)


                            if(baseResponseObj.success){
                                var baseResponse = BaseResponse()
                                baseResponse.success = true
                                baseResponse.message = "Appointment rescheduled successfully"
                                callback.onSuccess(baseResponse)
                            }else{
                                callback.onError(""+baseResponseObj.message)
                            }


                            Log.d("cancel_app_log", "success2 "+response.message())
                        }else{
                            Log.d("cancel_app_log","not successful")
                            callback.onError("You cannot reschedule appointment before 24 hours")
                            Log.d("login_log", "Err2 "+response.message())
                        }
                    }
                }

              /*  response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }*/
            }
        })
    }

    override fun cancelAppointment(
        token: String,
        appointmentId: String,
        callback: CancelApiCallBack<ResponseBody?>
    ) {
        Log.d("cancel_app_log", "cancel called here")
        cancelAppointmentApiCall = ApiClient.build()?.doCancelAppointment(token, appointmentId)

        cancelAppointmentApiCall?.enqueue(object : Callback<ResponseBody?>{
            override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                Log.d("cancel_app_log", "2 "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<ResponseBody?>,
                response: Response<ResponseBody?>
            ) {

                if(response.code() == 500){
                    Log.d("cancel_app_log", "500 err")
                    callback.onError("Something went wrong while cancelling appointment")
                }else{
                    response?.body()?.let {baseResponse ->
                        var str = ""+baseResponse.string()
                        Log.d("cancel_app_log","12 "+baseResponse.string())
                        Log.d("cancel_app_log","12 str "+str)
                        if(response.code() == 400){
                            Log.d("login_log", "Catched")
                        }
                        if(response.isSuccessful){
                            val gson = Gson()
                            val baseResponseObj:CancelAppointmentResponse = gson.fromJson(str, CancelAppointmentResponse::class.java)

                            if(baseResponseObj.success){
                                var baseResponse = BaseResponse()
                                baseResponse.success = true
                                baseResponse.message = "Appointment cancelled and refund initiated successfully"
                                callback.onSuccess(baseResponse)
                            }else{
                                callback.onError(""+baseResponseObj.message)
                            }
                        }else{
                            Log.d("cancel_app_log", "other err")
                            callback.onError("Something went wrong while cancelling appointment")

                        }
                    }
                }


            }
        })
    }

    override fun startCall(
        token: String,
        appointmentId: String,
        callback: StartCallApiCallBack<BaseResponse?>
    ) {
        startCallApiCall = ApiClient.build()?.doStartCall(token, appointmentId)

        startCallApiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun getTwilioTOken(
        token: String,
        appointmentId: String,
        callback: GetTokenApiCallBack<TokenResponse?>
    ) {
        tokenApiCall = ApiClient.build()?.doGetTwilioTOken(token, appointmentId)

        tokenApiCall?.enqueue(object : Callback<TokenResponse?>{
            override fun onFailure(call: Call<TokenResponse?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<TokenResponse?>,
                response: Response<TokenResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun endCall(
        token: String,
        appointmentId: String,
        callback: EndCallApiCallBack<BaseResponse?>
    ) {
        endCallApiCall = ApiClient.build()?.doEndCall(token, appointmentId)

        endCallApiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun requestCallBack(
        token: String,
        appointmentId: String,
        callback: RequestCallApiCallBack<BaseResponse?>
    ) {
        Log.d("call_back", "repo")
        requestCallApiCall = ApiClient.build()?.doRequestCallback(token, appointmentId)

        requestCallApiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("call_back", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("call_back", "Catched")
                    }
                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("call_back", "success2 "+response.message())
                    }else{
                        try {
                            assert(response.errorBody() != null)
                            val jObjError =
                                JSONObject(response.errorBody()!!.string())


                            callback.onError( jObjError.getString("message"))
                            Log.d("call_back","Wow I caught this ! "+jObjError.getString("message"))
                        } catch (ignored: Exception) {
                            Log.d("call_back","API Failed here "+ignored.toString())
                        }

                        Log.d("call_back", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun addGuestToAppointment(
        addGuestInput: AddGuestInput,
        token: String,
        appointmentId: String,
        callback: AddGuestApiCallBack<BaseResponse?>
    ) {
        addGuestApiCall = ApiClient.build()?.doAddGuestToAppointment(addGuestInput, token, appointmentId)

        addGuestApiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("login_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("login_log", "Catched")
                    }

                    if(response.isSuccessful){
                        callback.onSuccess(baseResponse)
                        Log.d("login_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("login_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }

    override fun requestReschedule(
        token: String,
        appointmentId: String,
        callback: RequestRescheduleApiCallBack<BaseResponse?>
    ) {
        requestRescheduleApiCall = ApiClient.build()?.doRequestReSchedule(token, appointmentId)

        requestRescheduleApiCall?.enqueue(object : Callback<BaseResponse?>{
            override fun onFailure(call: Call<BaseResponse?>, t: Throwable) {
                Log.d("reschedule_log", "Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(
                call: Call<BaseResponse?>,
                response: Response<BaseResponse?>
            ) {
                response?.body()?.let {baseResponse ->
                    if(response.code() == 400){
                        Log.d("reschedule_log", "Catched")
                    }

                    if(response.isSuccessful){
                        callback.onRescheduleSuccess(baseResponse)
                        Log.d("reschedule_log", "success2 "+response.message())
                    }else{
                        callback.onError(response.message())
                        Log.d("reschedule_log", "Err2 "+response.message())
                    }
                }
            }
        })
    }


    override fun getAppointmentList(isFromCG : Boolean, shouldGetTOday : Boolean,token: String, userId: String, callback: ListApiCallBack<AppointmentsListResponse?>) {

        if(isFromCG){
              getAppointmentCall = ApiClient.build()?.doGetAppointmentListByCG(token)
        }else{
            if(shouldGetTOday){
                getAppointmentCall = ApiClient.build()?.doTodayAppointments(token)

            }else{
                getAppointmentCall = ApiClient.build()?.doGetAppointmentList(token)

            }
        }


        getAppointmentCall?.enqueue(object : Callback<AppointmentsListResponse?>{
            override fun onFailure(call: Call<AppointmentsListResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<AppointmentsListResponse?>?,
                                    response: Response<AppointmentsListResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onListSuccess(response)
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }


    override fun getDoctorsList(
        shouldGetALlied: Boolean,
        token: String,
        callback: DoctorListApiCallBack<DoctorsListingResponse?>
    ) {
        if(shouldGetALlied){
            doctorListingApiCall = ApiClient.build()?.doGetAlliedDoctorsList(token)
        }else{
            doctorListingApiCall = ApiClient.build()?.doGetDoctorsList(token)
        }

        doctorListingApiCall?.enqueue(object : Callback<DoctorsListingResponse?>{
            override fun onFailure(call: Call<DoctorsListingResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)

            }

            override fun onResponse(call: Call<DoctorsListingResponse?>?,
                                    response: Response<DoctorsListingResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onDoctorListSuccess(response.body())
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())


                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getGuestDetails(
        token: String,
        appointmentId: String,
        callback: GetGuestApiCallBack<GuestDetailsResponse?>
    ) {
        getGuestApiCall = ApiClient.build()?.doGetGuestDetails(token, appointmentId)

        getGuestApiCall?.enqueue(object : Callback<GuestDetailsResponse?>{
            override fun onFailure(call: Call<GuestDetailsResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(call: Call<GuestDetailsResponse?>?,
                                    response: Response<GuestDetailsResponse?>) {
                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }
                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onGuestSuccess(response.body())
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getTimeSlotsAvailibility(
        token: String,
        doctorUserId: String,
        timeSlotsInput: TimeSlotsInput,
        callback: TimeSLotApiCallBack<TimeSlotsListingResponse?>
    ) {
        timeslotApiCall = ApiClient.build()?.doGetDoctorTimeSlots(token,doctorUserId, timeSlotsInput)

        timeslotApiCall?.enqueue(object : Callback<TimeSlotsListingResponse?>{
            override fun onFailure(call: Call<TimeSlotsListingResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(call: Call<TimeSlotsListingResponse?>?,
                                    response: Response<TimeSlotsListingResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onTimeSLotListSuccess(response.body())
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }

    override fun getVouchersList(
        token: String,
        doctorUserId: String,
        callback: VouchersListAPiCallBack<VoucherListResponse?>
    ) {
        vouchersListApiCall = ApiClient.build()?.doGetVouchersList(token, doctorUserId)

        vouchersListApiCall?.enqueue(object : Callback<VoucherListResponse?>{
            override fun onFailure(call: Call<VoucherListResponse?>?, t: Throwable?) {
                Log.d("appointment_log", "repo Error "+t?.message)
                callback.onError(t?.message)
            }

            override fun onResponse(call: Call<VoucherListResponse?>?,
                                    response: Response<VoucherListResponse?>) {

                if(response.code() == 400){
                    Log.d("appointment_log", "Catched")
                }

                Log.d("appointment_log", "Repository code "+response.code())
                if(response.isSuccessful){
                    callback.onSuccess(response.body())
                    Log.d("appointment_log", "success2 "+response.message())
                }else{
                    try {
                        assert(response.errorBody() != null)
                        val jObjError =
                            JSONObject(response.errorBody()!!.string())
                        callback.onError( jObjError.getString("message"))
                        Log.d("appointment_log","Wow I caught this ! "+jObjError.getString("message"))
                    } catch (ignored: Exception) {
                        Log.d("appointment_log","API Failed here "+ignored.toString())
                    }

                    Log.d("appointment_log", "Err2 "+response.message())
                }
            }
        })
    }


    override fun cancel() {
          addAppointmentCall?.cancel()
    }
}