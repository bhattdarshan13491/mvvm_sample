package com.oncobuddy.app.models.mvvm_implementors

import com.oncobuddy.app.models.api_repositories.AppointmentVMRepository
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
import retrofit2.http.Body


interface AppointmentVMImplementor : BaseImplementor{


    fun addOrUpdateAppointment(addAppointmentInput: AddAppointmentInput, token: String,
                               callback: AppointmentVMRepository.ApiCallBack<AddAppointmentResponse?>)

    fun updatePayment(token: String, appointmentId: String, paymentInput: PaymentInput,
                               callback: AppointmentVMRepository.UpdatePaymentAPiCallBack<BaseResponse?>)

    fun getPatientList(source: String, token: String, @Body searchQueryInput: SearchQueryInput, callback: AppointmentVMRepository.PatientListAPiCallBack<PatientsListResponse?>)

    fun rescheduleAppointment(addAppointmentInput: AddAppointmentInput, token: String, appointmentId: String,
                               callback: AppointmentVMRepository.RescheduleApiCallBack<BaseResponse?>)

    fun cancelAppointment(token: String, appointmentId: String, callback: AppointmentVMRepository.CancelApiCallBack<ResponseBody?>)

    fun startCall(token: String, appointmentId: String, callback: AppointmentVMRepository.StartCallApiCallBack<BaseResponse?>)

    fun getTwilioTOken(token: String, appointmentId: String,
                       callback: AppointmentVMRepository.GetTokenApiCallBack<TokenResponse?>)

    fun endCall(token: String, appointmentId: String, callback: AppointmentVMRepository.EndCallApiCallBack<BaseResponse?>)

    fun requestCallBack(token: String, appointmentId: String, callback: AppointmentVMRepository.RequestCallApiCallBack<BaseResponse?>)

    fun addGuestToAppointment(addGuestInput: AddGuestInput, token: String, appointmentId: String,
                               callback: AppointmentVMRepository.AddGuestApiCallBack<BaseResponse?>)

    fun requestReschedule(token: String, appointmentId: String, callback: AppointmentVMRepository.RequestRescheduleApiCallBack<BaseResponse?>)



    fun getAppointmentList(isFromCG: Boolean = false , shouldGetTOday : Boolean,token: String, userId: String,
                           callback: AppointmentVMRepository.ListApiCallBack<AppointmentsListResponse?>)

    fun getDoctorsList(shouldGetALlied: Boolean,token: String, callback: AppointmentVMRepository.DoctorListApiCallBack<DoctorsListingResponse?>)

    fun getGuestDetails(token: String, appointmentId: String, callback: AppointmentVMRepository.GetGuestApiCallBack<GuestDetailsResponse?>)

    fun getTimeSlotsAvailibility(token: String, doctorUserId: String, timeSlotsInput: TimeSlotsInput,
                               callback: AppointmentVMRepository.TimeSLotApiCallBack<TimeSlotsListingResponse?>)


    fun getVouchersList(token: String,doctorId: String, callback: AppointmentVMRepository.VouchersListAPiCallBack<VoucherListResponse?>)


}