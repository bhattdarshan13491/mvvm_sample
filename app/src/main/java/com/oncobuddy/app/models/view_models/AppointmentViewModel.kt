package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.AppointmentVMRepository
import com.oncobuddy.app.models.mvvm_implementors.AppointmentVMImplementor
import com.oncobuddy.app.models.pojo.BaseResponse
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
import com.oncobuddy.app.models.pojo.patient_list.PatientsListResponse
import com.oncobuddy.app.models.pojo.twilio_token.TokenResponse
import com.oncobuddy.app.models.pojo.vouchers_response.VoucherListResponse
import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body

class AppointmentViewModel(private val appointmentVMImplementor: AppointmentVMImplementor) : ViewModel() {

    private val _liveResponse = MutableLiveData<AddAppointmentResponse>()
    val responseData: LiveData<AddAppointmentResponse> = _liveResponse

    private val _liveGuestDetailsResponse = MutableLiveData<GuestDetailsResponse>()
    val guestDetailsResponseData: LiveData<GuestDetailsResponse> = _liveGuestDetailsResponse

    private val _livePatientListResponse = MutableLiveData<PatientsListResponse>()
    val patientListResponseData: LiveData<PatientsListResponse> = _livePatientListResponse

    private val _liveBaseResponse = MutableLiveData<BaseResponse>()
    val baseResponseData: LiveData<BaseResponse> = _liveBaseResponse

    private val _liveRequestRescheduleResponse  = MutableLiveData<BaseResponse>()
    val requestRescheduleResponseData: LiveData<BaseResponse> = _liveRequestRescheduleResponse

    private val _liveCallBackBaseResponse = MutableLiveData<BaseResponse>()
    val callBackResponseData: LiveData<BaseResponse> = _liveCallBackBaseResponse

    private val _livePaymentStatusResponse = MutableLiveData<BaseResponse>()
    val paymentResponseData: LiveData<BaseResponse> = _livePaymentStatusResponse

    private val _liveVoucherResponse = MutableLiveData<VoucherListResponse>()
    val voucherListResponseData: LiveData<VoucherListResponse> = _liveVoucherResponse

    private val _liveCancelAppointmentResponse = MutableLiveData<BaseResponse>()
    val cancelAppointmentResponseData: LiveData<BaseResponse> = _liveCancelAppointmentResponse

    private val _liveStartCallResponse = MutableLiveData<BaseResponse>()
    val startCallResponseData: LiveData<BaseResponse> = _liveStartCallResponse

    private val _liveGetToken = MutableLiveData<TokenResponse>()
    @JvmField
    val getTokenResponseData: LiveData<TokenResponse> = _liveGetToken

    private val _liveEndCallResponse = MutableLiveData<BaseResponse>()
    val endCallResponseData: LiveData<BaseResponse> = _liveEndCallResponse

    private val _liveRescheduleResponse = MutableLiveData<BaseResponse>()
    val rescheduleResponseData: LiveData<BaseResponse> = _liveRescheduleResponse

    private val _liveDoctorListResponse = MutableLiveData<DoctorsListingResponse>()
    val doctorListResponse: LiveData<DoctorsListingResponse> = _liveDoctorListResponse

    private val _liveTimeSlotsResponse = MutableLiveData<TimeSlotsListingResponse>()
    val timeSlotsListResponse: LiveData<TimeSlotsListingResponse> = _liveTimeSlotsResponse

    private val _liveListResponse = MutableLiveData<Response<AppointmentsListResponse?>>()
    val appointmentsList: LiveData<Response<AppointmentsListResponse?>> = _liveListResponse

    private val _isViewLoading = MutableLiveData<Boolean>()
    @JvmField
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    @JvmField
    val onMessageError: LiveData<String> = _onMessageError

    private val _onvoucherListError = MutableLiveData<String>()
    val onVoucherListError: LiveData<String> = _onvoucherListError

    fun callAddGuest(addGuestInput: AddGuestInput, token: String, appointmentId: String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.addGuestToAppointment(
            addGuestInput,token,appointmentId,
            object : AppointmentVMRepository.AddGuestApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveBaseResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun callVoucherList(token: String, doctorId: String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getVouchersList(
            token,doctorId,
            object : AppointmentVMRepository.VouchersListAPiCallBack<VoucherListResponse?> {
                override fun onSuccess(responseData: VoucherListResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveVoucherResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onvoucherListError.postValue(message)
                }
            })
    }


    fun callGetGuestDetails(token: String, appointmentId: String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getGuestDetails(token,appointmentId,
            object : AppointmentVMRepository.GetGuestApiCallBack<GuestDetailsResponse?> {
                override fun onGuestSuccess(responseData: GuestDetailsResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveGuestDetailsResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }


            })
    }

    fun callCancelAppointment(token: String, appointmentId: String) {

        Log.d("login_log", "came here "+appointmentId)
        Log.d("login_log", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.cancelAppointment(token,appointmentId,
            object : AppointmentVMRepository.CancelApiCallBack<ResponseBody?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveCancelAppointmentResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("login_log", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }



    fun callAddAppointment(addAppointmentInput: AddAppointmentInput, token: String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.addOrUpdateAppointment(
            addAppointmentInput,token,
            object : AppointmentVMRepository.ApiCallBack<AddAppointmentResponse?> {
                override fun onSuccess(responseData: AddAppointmentResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun updatePaymentStatus(token: String, appointmentId: String, paymentInput: PaymentInput) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.updatePayment(
            token, appointmentId, paymentInput,
            object : AppointmentVMRepository.UpdatePaymentAPiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _livePaymentStatusResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }


    fun callRescheduleAppointment(addAppointmentInput: AddAppointmentInput, token: String, appointmentId: String) {

        Log.d("login_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.rescheduleAppointment(
            addAppointmentInput,token,appointmentId,
            object : AppointmentVMRepository.RescheduleApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveRescheduleResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }



    fun callGetAppointment(isFromCG: Boolean = false , isToday : Boolean,token: String, userId: String) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getAppointmentList(isFromCG, isToday,
            token,userId,
            object : AppointmentVMRepository.ListApiCallBack<AppointmentsListResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onListSuccess(responseData: Response<AppointmentsListResponse?>) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveListResponse.postValue(responseData)
                }


            })
    }

    fun callGetPatientList(source : String, token: String, @Body searchQueryInput: SearchQueryInput) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getPatientList(
            source,
            token,
            searchQueryInput,
            object : AppointmentVMRepository.PatientListAPiCallBack<PatientsListResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: PatientsListResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _livePatientListResponse.postValue(responseData)
                }


            })
    }

    fun callGetDoctorListing(shouldGetALlied: Boolean = false,token: String) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getDoctorsList(shouldGetALlied,
            token,
            object : AppointmentVMRepository.DoctorListApiCallBack<DoctorsListingResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onDoctorListSuccess(responseData: DoctorsListingResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveDoctorListResponse.postValue(responseData)
                }

                /*override fun onDoc(responseData: Response<DoctorsListingResponse?>) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveListResponse.postValue(responseData)
                }*/


            })
    }

    fun callGetTimeSlots(token: String, doctorId: String, timeSlotsInput: TimeSlotsInput) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getTimeSlotsAvailibility(
            token,doctorId, timeSlotsInput,
            object : AppointmentVMRepository.TimeSLotApiCallBack<TimeSlotsListingResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onTimeSLotListSuccess(responseData: TimeSlotsListingResponse?) {
                    _isViewLoading.postValue(false)
                    _liveTimeSlotsResponse.postValue(responseData)
                }
            })
    }

    fun callStartCall(token: String, appointmentId: String) {

        Log.d("login_log", "came here "+appointmentId)
        Log.d("login_log", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.startCall(token,appointmentId,
            object : AppointmentVMRepository.StartCallApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveStartCallResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("login_log", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun callGetToken(token: String, appointmentId: String) {

        Log.d("login_log", "came here "+appointmentId)
        Log.d("login_log", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.getTwilioTOken(token,appointmentId,
            object : AppointmentVMRepository.GetTokenApiCallBack<TokenResponse?> {
                override fun onSuccess(responseData: TokenResponse?) {
                    Log.d("login_log", "Success")
                    Log.d("login_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveGetToken.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("login_log", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }


    fun callEndCall(token: String, appointmentId: String) {

        Log.d("v_call_log", "came here "+appointmentId)
        Log.d("v_call_log", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.endCall(token,appointmentId,
            object : AppointmentVMRepository.EndCallApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("v_call_log", "Success")
                    Log.d("v_call_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveEndCallResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("v_call_log", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun callRequestReschedule(token: String, appointmentId: String) {

        Log.d("rewschule_log", "came here "+appointmentId)
        Log.d("rewschule_log", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.requestReschedule(token,appointmentId,
            object : AppointmentVMRepository.RequestRescheduleApiCallBack<BaseResponse?> {
                override fun onRescheduleSuccess(responseData: BaseResponse?) {
                    Log.d("rewschule_log", "Success")
                    Log.d("rewschule_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveRequestRescheduleResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("rewschule_log", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }

    fun requestCallbackCall(token: String, appointmentId: String) {

        Log.d("call_back", "came here "+appointmentId)
        Log.d("call_back", "came here "+token)
        _isViewLoading.postValue(true)
        appointmentVMImplementor.requestCallBack(token,appointmentId,
            object : AppointmentVMRepository.RequestCallApiCallBack<BaseResponse?> {
                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("call_back", "Success")
                    Log.d("call_back", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveCallBackBaseResponse.postValue(responseData)
                }
                override fun onError(message: String?) {
                    Log.d("call_back", "Err ")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }
            })
    }




}