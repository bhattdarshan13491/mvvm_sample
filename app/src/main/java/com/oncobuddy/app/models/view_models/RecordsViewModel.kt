package com.oncobuddy.app.view_models

import android.util.Log
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModel
import com.oncobuddy.app.models.api_repositories.RecordsVMRepository
import com.oncobuddy.app.models.api_repositories.RecrodsVMImplementor
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.extract_doc_response.UploadDocResponse
import com.oncobuddy.app.models.pojo.genetic_report_response.GeneticReportResponse
import com.oncobuddy.app.models.pojo.records_list.AddRecordInput
import com.oncobuddy.app.models.pojo.records_list.Record
import com.oncobuddy.app.models.pojo.records_list.RecordsListResponse
import com.oncobuddy.app.models.pojo.response_categories.ResponseCategories
import com.oncobuddy.app.models.pojo.response_sub_categories.CategoryIds
import com.oncobuddy.app.models.pojo.response_sub_categories.ResponseSubCategories
import com.oncobuddy.app.utils.CommonMethods
import okhttp3.MultipartBody

class RecordsViewModel(private val recordsVMImplementor: RecrodsVMImplementor) : ViewModel() {

    private val _liveResponse = MutableLiveData<BaseResponse>()
    val responseData: LiveData<BaseResponse> = _liveResponse

    private val _liveViewReportResponse = MutableLiveData<GeneticReportResponse>()
    val viewReportResponseData: LiveData<GeneticReportResponse> = _liveViewReportResponse

    private val _liveViewSummaryResponse = MutableLiveData<GeneticReportResponse>()
    val viewSummaryResponseData: LiveData<GeneticReportResponse> = _liveViewSummaryResponse

    private val _liveFileUploadResponse = MutableLiveData<BaseResponse>()
    val responseFileUploadData: LiveData<BaseResponse> = _liveFileUploadResponse

    private val _liveDocUploadResponse = MutableLiveData<UploadDocResponse>()
    val uploadDocData: LiveData<UploadDocResponse> = _liveDocUploadResponse

    private val _liveDeleteRecordResponse = MutableLiveData<BaseResponse>()
    val responseDeleteRecord: LiveData<BaseResponse> = _liveDeleteRecordResponse

    private val _liveRecordAddOrUpdateResponse = MutableLiveData<BaseResponse>()
    val responseRecordAddOrUpdate: LiveData<BaseResponse> = _liveRecordAddOrUpdateResponse

    private val _liveCategoriesResponse = MutableLiveData<ResponseCategories>()
    val responseCategoriesData: LiveData<ResponseCategories> = _liveCategoriesResponse

    private val _liveSubCategoriesResponse = MutableLiveData<ResponseSubCategories>()
    val responseSubCategoriesData: LiveData<ResponseSubCategories> = _liveSubCategoriesResponse

    private val _liveListResponse = MutableLiveData<List<Record>>()
    @JvmField
    val recordsList: LiveData<List<Record>> = _liveListResponse

    private val _liveDBList = MutableLiveData<List<Record>>()
    val allDBRecordsList: LiveData<List<Record>> = _liveDBList

    private var hasQueriedToServer = false

    private val _isViewLoading = MutableLiveData<Boolean>()
    val isViewLoading: LiveData<Boolean> = _isViewLoading

    private val _onMessageError = MutableLiveData<String>()
    val onMessageError: LiveData<String> = _onMessageError

    fun callGetRecords(source : String, token: String, userId: String, patientId: String) {
        CommonMethods.showLog("record_log","6")
        _isViewLoading.postValue(true)

        recordsVMImplementor.getRecordsList(source, token, patientId,
            object : RecordsVMRepository.ListApiCallBack<RecordsListResponse?> {

                override fun onError(message: String?) {
                    Log.d("record_log", "Error "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onListSuccess(responseData: List<Record>?) {
                    _isViewLoading.postValue(false)
                    //_liveListResponse.postValue(responseData)
                    if (responseData != null) {
                        CommonMethods.showLog("record_log","7")

                        //getRecordsFromDB()*/
                       _liveListResponse.postValue(responseData)
                    }

                }


            })
    }

    fun callGetGeneticReportPdf(token: String, patientId: String) {
        CommonMethods.showLog("record_log","6")
        _isViewLoading.postValue(true)

        recordsVMImplementor.getGeneticReport(token, patientId,
            object : RecordsVMRepository.ViewReportApiCallBack<GeneticReportResponse?> {

                override fun onError(message: String?) {
                    Log.d("record_log", "Error "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onReportSuccess(responseData: GeneticReportResponse?) {
                    _isViewLoading.postValue(false)
                    //_liveListResponse.postValue(responseData)
                    if (responseData != null) {
                        CommonMethods.showLog("record_log","7")

                        //getRecordsFromDB()*/
                        _liveViewReportResponse.postValue(responseData)
                    }

                }


            })
    }

    fun callGetSummaryPdf(token: String, patientId: String) {
        CommonMethods.showLog("record_log","6")
        _isViewLoading.postValue(true)

        recordsVMImplementor.getSUmmary(token, patientId,
            object : RecordsVMRepository.ViewReportApiCallBack<GeneticReportResponse?> {

                override fun onError(message: String?) {
                    Log.d("record_log", "Error "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onReportSuccess(responseData: GeneticReportResponse?) {
                    _isViewLoading.postValue(false)
                    //_liveListResponse.postValue(responseData)
                    if (responseData != null) {
                        CommonMethods.showLog("record_log","7")

                        //getRecordsFromDB()*/
                        _liveViewReportResponse.postValue(responseData)
                    }

                }


            })
    }

    fun callAddRecord(source: String,
                      addRecordInput: AddRecordInput,
                      token:String,
                      userId: String) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        recordsVMImplementor.addRecord(source, addRecordInput, token, userId,
            object : RecordsVMRepository.ApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveRecordAddOrUpdateResponse.postValue(responseData)


                }


            })
    }

    fun callupdateRecord(source: String, addRecordInput: AddRecordInput,
                      token:String,patientId: String = "",
                      userId: String, recordId: String) {

        Log.d("appointment_log", "came here")
        _isViewLoading.postValue(true)
        recordsVMImplementor.updateRecord(source,addRecordInput, token, userId, recordId,patientId,
            object : RecordsVMRepository.ApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveRecordAddOrUpdateResponse.postValue(responseData)
                }


            })
    }

    fun callDeleteRecord(source: String, patientId: String = "",token:String, recordId: String) {

        Log.d("record_log", "came here "+source)
        _isViewLoading.postValue(true)
        recordsVMImplementor.deleteRecord(source, patientId, token, recordId,
            object : RecordsVMRepository.ApiCallBack<BaseResponse?> {

                override fun onError(message: String?) {
                    Log.d("appointment_log", "Error")
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: BaseResponse?) {
                    Log.d("appointment_log", "Success")
                    Log.d("appointment_log", "is_loading is 1")
                    _isViewLoading.postValue(false)
                    _liveDeleteRecordResponse.postValue(responseData)
                }


            })
    }

    fun callFileUpload( token:String, file: MultipartBody.Part) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        recordsVMImplementor.uploadFileToS3(token,file,
            object : RecordsVMRepository.UploadS3ApiCallBack<BaseResponse?> {

                override fun onFileUploadError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onFileUploadSuccess(responseData: BaseResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveFileUploadResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }


            })
    }

    fun callDocUpload(patientId: String = "",token:String, file: MultipartBody.Part) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        recordsVMImplementor.uploadDoc(patientId ,token , file,
            object : RecordsVMRepository.UploadDocApiCallBack<UploadDocResponse?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onDocUploadSuccess(responseData: UploadDocResponse?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveDocUploadResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }


            })
    }


    fun callGetCategories( token:String) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        recordsVMImplementor.getCategories(token,
            object : RecordsVMRepository.CategoriesApiCallBack<ResponseCategories?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: ResponseCategories?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveCategoriesResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }

    fun callGetSubCategories( token:String, categoryIds: CategoryIds) {

        Log.d("upload_log","3")
        _isViewLoading.postValue(true)

        recordsVMImplementor.getSubCategories(token,categoryIds,
            object : RecordsVMRepository.SubCategoriesApiCallBack<ResponseSubCategories?> {

                override fun onError(message: String?) {
                    Log.d("upload_log","4 "+message)
                    _isViewLoading.postValue(false)
                    _onMessageError.postValue(message)
                }

                override fun onSuccess(responseData: ResponseSubCategories?) {
                    Log.d("upload_log","5 "+responseData)
                    _liveSubCategoriesResponse.postValue(responseData)
                    _isViewLoading.postValue(false)
                }
            })
    }




}