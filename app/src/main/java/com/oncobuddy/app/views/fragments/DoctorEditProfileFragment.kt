package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.*
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Rect
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.*
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CircleCrop
import com.google.gson.Gson
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.FragmentDoctorEditProfileNewBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.DoctorAvailibilityResponse
import com.oncobuddy.app.models.pojo.doctor_profile.doctor_availibility.Weekday
import com.oncobuddy.app.models.pojo.doctor_update.AddressDto
import com.oncobuddy.app.models.pojo.doctor_update.DoctorAvailabilityReq
import com.oncobuddy.app.models.pojo.doctor_update.DoctorRegistrationInput
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalDetails
import com.oncobuddy.app.models.pojo.hospital_listing.HospitalListingResponse
import com.oncobuddy.app.models.pojo.hospital_listing.Locations
import com.oncobuddy.app.models.pojo.login_response.Hospital
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.login_response.Profile
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.HospitalListingAdapter
import com.oncobuddy.app.views.adapters.HospitalLocationsListingAdapter
import com.oncobuddy.app.views.adapters.HospitalsAutoCOmpleteAdapter
import com.oncobuddy.app.views.adapters.LocationsAutoCompleteAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import kotlinx.android.synthetic.main.fragment_slot_selection.view.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class DoctorEditProfileFragment : BaseFragment(), HospitalListingAdapter.Interaction,
    HospitalLocationsListingAdapter.Interaction {

    private lateinit var binding: FragmentDoctorEditProfileNewBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var FILE_URL = ""
    private lateinit var FILE_PATH: String
    private var mCurrentPhotoPath: String? = null
    private var hasSetInitialData = false
    private lateinit var hospitalList: ArrayList<HospitalDetails>
    private lateinit var locationsList: ArrayList<Locations>
    private var weekDayTimingsList = ArrayList<Weekday>()
    private var serverWeekDayTimingsList = ArrayList<Weekday>()
    private lateinit var hospitalListingAdapter: HospitalListingAdapter
    private lateinit var hospitalLocationsListingAdapter: HospitalLocationsListingAdapter
    private lateinit var selectCancerTypeDialog: Dialog
    private lateinit var selectLocationDialog: Dialog

    //private var hospitalTypeId = 0
    private var hospital: Hospital? = null
    private var selectedLocation: Locations? = null
    private var SUNDAY = 6
    private var MONDAY = 0
    private var TUESDAY = 1
    private var WEDNESSDAY = 2
    private var THURSDAY = 3
    private var FRIDAY = 4
    private var SATURDAY = 5

    private var startHour = 0
    private var startMinute = 0
    private var endHour = 0
    private var endMinute = 0

    private var selectedDay = MONDAY
    private lateinit var weekDay: Weekday
    private var IS_TIMING_MODE = false
    private var IS_FIRST_TIME = false
    private var HAS_SELECTED_LOCATION = false
    private var hospitalLocationId = 1
    private var startTimeInMilis = 0L
    private var endTImeInMilis = 0L
    private val weekdaySArray = arrayOf("MONDAY", "TUESDAY", "WEDNESDAY", "THURSDAY", "FRIDAY", "SATURDAY", "SUNDAY")
    private val cc_list = arrayOf("Dietitian", "Physiotherapy", "Genetic counselling", "General")
    private var selectedCC = "General"  //By default its general


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        init(inflater, container)

        return binding.root
    }



    override fun init(inflater: LayoutInflater, container: ViewGroup?) {
        binding = DataBindingUtil.inflate(
            inflater,
            R.layout.fragment_doctor_edit_profile_new, container, false
        )

        setupClickListeners()
        showHideBottomButton()
        setupVM()
        setupObservers()
        setupUserData()
        getHospitalsFromServer()
        //getLocalHospitalsList()

        Log.d("days_log", "adding default list")
        weekDayTimingsList = ArrayList()
        for (i in 0..6) {
            var weekday = Weekday()
            weekday.id = i
            weekday.isAvailable = false
            weekday.weekday = weekdaySArray.get(i)
            weekDayTimingsList.add(weekday)
        }
        Log.d("days_log", "added list of " + weekDayTimingsList.size)


    }

    private fun getHospitalsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.getHospitalsListing(
                        getUserAuthToken()
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getDoctorTimings() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.callGetDoctorTimings(
                        getUserAuthToken(), "" + getUserObject().userIdd
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getUpdateTiming() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                    profileViewModel.callGetDoctorTimings(
                        getUserAuthToken(), "" + getUserObject().userIdd
                    )
                }
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }


    private fun getLocalHospitalsList() {
        /*  val gson = Gson()
          val hospitalListingResponse = gson.fromJson(CommonMethods.getHospitalsFromAssets(), HospitalListingResponse::class.java)
          hospitalList = ArrayList()
          hospitalList.addAll(hospitalListingResponse.payLoad)*/
        //hospitalList = ArrayList()
        //CommonMethods.getHospitalsFromAssets()?.payLoad?.let { hospitalList.addAll(it) }
    }


    private fun setupUserData() {

        var userObject = getUserObject()
        binding.etFullName.setText(userObject.firstName)
        if (userObject.email.isNullOrEmpty() || userObject.email.equals("null", true)) {
            binding.etEmail.setText("")

        } else {
            binding.etEmail.setText("" + userObject.email)

        }
        binding.etPhoneNummber.setText(userObject.phoneNumber)

        if (!getUserObject().dpLink.isNullOrEmpty()) {
            FILE_URL = getUserObject().dpLink
            Glide.with(FourBaseCareApp.activityFromApp).load(userObject.dpLink).circleCrop()
                .into(binding.ivProfile)
        }


        binding.etHeadLine.setText(userObject.headline)

        if (userObject.profile == null) {
            Log.d("profile_log", "is null")
        } else {
            /*if(userObject.profile.consultationCategory != null){
                binding.etSelectCC.setText(CommonMethods.returnConsultationCategory(userObject.profile.consultationCategory))
                selectedCC = CommonMethods.returnConsultationCategory(userObject.profile.consultationCategory)
            }*/


                if(!userObject.profile.currentLocation.isNullOrEmpty()){
                    binding.etCurrentLocation.setText(""+userObject.profile.currentLocation)
                }
            binding.etDesignation.setText(""+userObject.profile.designation)
            if (!userObject.profile.experience.isNullOrEmpty()) binding.etYearsOfExperience.setText(
                "" + userObject.profile.experience
            )
            binding.etSpecialization.setText(userObject.profile.specialization)
            var strFees = "" + userObject.profile.consultationFee
            if (strFees.equals("null", true)) {
                binding.etConsultationFees.setText("")
            } else {
                val df = DecimalFormat("#.##")
                binding.etConsultationFees.setText("" + df.format(userObject.profile.consultationFee))
            }
            if (userObject.profile.hospital != null && userObject.profile.hospital.id != null) {
                hospital = getUserObject().profile.hospital
                binding.etHospital.setText(getUserObject().profile.hospital.name)
                if (getUserObject().profile.selectedLocation != null) {
                    selectedLocation = getUserObject().profile.selectedLocation
                    binding.etLocation.setText(getUserObject().profile.selectedLocation.name)
                }
            } else {
                Log.d("hospital_set", "id is null")
            }
        }
    }

    private fun showFromTimePickerDialogue() {
        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog

        mTimePicker = TimePickerDialog(
            FourBaseCareApp.activityFromApp,
            { timePicker, selectedHour, selectedMinute ->

                Log.d("tp_log", "hour " + selectedHour)
                Log.d("tp_log", "hour " + selectedMinute)

                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCal.set(Calendar.MINUTE, selectedMinute)
                startTimeInMilis = selectedCal.timeInMillis



                var fromTime = ""

                if (selectedHour == 0) {
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"
                } else if (selectedHour == 12)
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + selectedHour) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else if (selectedHour > 12)
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else
                    fromTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"

                binding.linSLotSelection.etFromTime.setText(fromTime)
                startHour = selectedHour
                startMinute = selectedMinute
            },
            hour,
            minute,
            true
        ) //Yes 24 hour time


        mTimePicker.setTitle("Select Start Time")
        mTimePicker.show()

    }

    private fun showToDatePickerDialogue() {
        val calendar = Calendar.getInstance()
        val hour: Int = calendar.get(Calendar.HOUR_OF_DAY)
        val minute: Int = calendar.get(Calendar.MINUTE)
        val mTimePicker: TimePickerDialog

        mTimePicker = TimePickerDialog(
            FourBaseCareApp.activityFromApp,
            { timePicker, selectedHour, selectedMinute ->

                val selectedCal = Calendar.getInstance()
                selectedCal.set(Calendar.HOUR_OF_DAY, selectedHour)
                selectedCal.set(Calendar.MINUTE, selectedMinute)
                endTImeInMilis = selectedCal.timeInMillis

                var endTime = ""

                if (selectedHour == 0) {
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"
                } else if (selectedHour > 12)
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else if (selectedHour == 12)
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + selectedHour) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " PM"
                else
                    endTime =
                        CommonMethods.getStringWithOnePadding("" + (selectedHour)) + " : " + CommonMethods.getStringWithOnePadding(
                            "" + selectedMinute
                        ) + " AM"

                binding.linSLotSelection.etToTime.setText(endTime)
                endHour = selectedHour
                endMinute = selectedMinute


            },
            hour,
            minute,
            true
        ) //Yes 24 hour time

        mTimePicker.setTitle("Select End Time")
        mTimePicker.show()
    }


    private fun setSelectedDay() {
        if (!weekDayTimingsList.isNullOrEmpty()) {
            setWeekDayData()
            setDataForSelectedDay()
        }

    }

    private fun switchTabs(isProfileSelected: Boolean) {
        if (isProfileSelected) {
            binding.linSLotSelection.linTimingsContainer.visibility = View.GONE
            binding.linBasicDetails.visibility = View.VISIBLE
            binding.ivEditProfile.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.purple_700
                )
            )
            binding.ivEditTimings.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_border
                )
            )
        } else {
            binding.linBasicDetails.visibility = View.GONE
            binding.linSLotSelection.linTimingsContainer.visibility = View.VISIBLE
            binding.ivEditProfile.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_border
                )
            )
            binding.ivEditTimings.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.purple_700
                )
            )
        }
    }

    private fun setupClickListeners() {
        val spinner_purpose = ArrayAdapter(
            FourBaseCareApp.activityFromApp,
            android.R.layout.simple_spinner_dropdown_item,
            cc_list
        )

        /*binding.etSelectCC.setOnClickListener(View.OnClickListener {

            AlertDialog.Builder(FourBaseCareApp.activityFromApp)
                .setTitle("Select consultation category")
                .setAdapter(spinner_purpose,
                    DialogInterface.OnClickListener { dialog, which ->
                        binding.etSelectCC.setText(cc_list[which])
                        selectedCC = CommonMethods.returnreturnConsultationCategoryEnum(cc_list[which])
                        dialog.dismiss()
                    }).create().show()


        })*/

        /*
        *    override fun onHospitalSelected(position: Int, item: HospitalDetails, view: View) {
        hospital = Hospital()
        hospital!!.id = item.id
        hospital!!.name = item.name

        if(!item.locationsList.isNullOrEmpty()){
            locationsList = ArrayList()
            locationsList.addAll(item.locationsList)
            val adapter = LocationsAutoCompleteAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_hospital_type, locationsList)
            binding.etLocation.setAdapter(adapter)
        }

        binding.etHospital.setText(item.name)
        /*binding.etHeadLine.setText(item.name)
        binding.etCancerSubType.setText("")*/
        selectCancerTypeDialog.dismiss()
    }

    override fun onLocationSelected(position: Int, item: Locations, view: View) {
        hospitalLocationId = item.id
        selectedLocation = Locations()
        selectedLocation!!.id = item.id
        selectedLocation!! .name = item.name
        binding.etLocation.setText(item.name)
        selectLocationDialog.dismiss()
    }

        * */


        binding.etHospital.setOnItemClickListener() { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            val item = hospitalList.get(position)
            hospital = Hospital()
            hospital!!.id = item.id
            hospital!!.name = item.name

            if (!item.locationsList.isNullOrEmpty()) {
                locationsList = ArrayList()
                locationsList.addAll(item.locationsList)
                val adapter = LocationsAutoCompleteAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_hospital_type,
                    locationsList
                )
                binding.etLocation.setAdapter(adapter)
            }

            binding.etLocation.setText("")


        }

        binding.etLocation.setOnItemClickListener() { _, _, position, _ ->
            // You can get the label or item that the user clicked:
            val item = locationsList.get(position)
            hospitalLocationId = item.id
            selectedLocation = Locations()
            selectedLocation!!.id = item.id
            selectedLocation!!.name = item.name
            binding.etLocation.setText(item.name)
        }



        binding.linSLotSelection.switchAvailibility.setOnCheckedChangeListener(CompoundButton.OnCheckedChangeListener
        { buttonView, isChecked ->
            run {
                if (isChecked) binding.linSLotSelection.etDuration.isEnabled = true
                else binding.linSLotSelection.etDuration.isEnabled = false
            }

        })



        binding.linSLotSelection.etFromTime.setOnClickListener(View.OnClickListener {
            if (linSLotSelection.switchAvailibility.isChecked) {
                showFromTimePickerDialogue()
            } else {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please check availibility to edit timings"
                )
            }

        })

        binding.linSLotSelection.etToTime.setOnClickListener(View.OnClickListener {
            if (linSLotSelection.switchAvailibility.isChecked) {
                showToDatePickerDialogue()
            } else {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    "Please check availibility to edit timings"
                )
            }
        })

        binding.linSLotSelection.linMonday.setOnClickListener(View.OnClickListener {
            selectedDay = MONDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 0) {
                setDataForSelectedDay()
            } else {
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linTue.setOnClickListener(View.OnClickListener {
            selectedDay = TUESDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 1) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == TUESDAY,
                    binding.linSLotSelection.ivTue,
                    binding.linSLotSelection.linTue,
                    binding.linSLotSelection.tvTue
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linWed.setOnClickListener(View.OnClickListener {
            selectedDay = WEDNESSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 2) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == WEDNESSDAY,
                    binding.linSLotSelection.ivWed,
                    binding.linSLotSelection.linWed,
                    binding.linSLotSelection.tvWed
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linThu.setOnClickListener(View.OnClickListener {
            selectedDay = THURSDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 3) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == THURSDAY,
                    binding.linSLotSelection.ivThu,
                    binding.linSLotSelection.linThu,
                    binding.linSLotSelection.tvThu
                )
                resetCOntrollers()
            }
        })
        binding.linSLotSelection.linFri.setOnClickListener(View.OnClickListener {
            selectedDay = FRIDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 4) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == FRIDAY,
                    binding.linSLotSelection.ivFri,
                    binding.linSLotSelection.linFri,
                    binding.linSLotSelection.tvFri
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linSat.setOnClickListener(View.OnClickListener {
            selectedDay = SATURDAY
            Log.d("day_log", "0 " + selectedDay)
            setWeekDayData()
            if (weekDayTimingsList.size > 5) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SATURDAY,
                    binding.linSLotSelection.ivSat,
                    binding.linSLotSelection.linSat,
                    binding.linSLotSelection.tvSat
                )
                resetCOntrollers()
            }
        })

        binding.linSLotSelection.linSun.setOnClickListener(View.OnClickListener {
            selectedDay = SUNDAY
            setWeekDayData()
            if (weekDayTimingsList.size > 6) {
                setDataForSelectedDay()
            } else {
                setLookAsPerDay(
                    false,
                    selectedDay == SUNDAY,
                    binding.linSLotSelection.ivSun,
                    binding.linSLotSelection.linSun,
                    binding.linSLotSelection.tvSun
                )
                resetCOntrollers()
            }
        })


        binding.linProfile.setOnClickListener(View.OnClickListener {
            IS_TIMING_MODE = false
            switchTabs(true)
        })

        binding.linTimings.setOnClickListener(View.OnClickListener {
            IS_TIMING_MODE = true
            switchTabs(false)
            getDoctorTimings()
        })

        binding.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })


        binding.ivEdit.setOnClickListener(View.OnClickListener {
            if (isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        binding.etPhoneNummber.setOnClickListener(View.OnClickListener {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.you_cant_change_your_number)
            )
        })


        binding.ivSave.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp) && isValidInput()) {

                if (IS_TIMING_MODE) {
                    var doctorTimingReq = DoctorAvailabilityReq()
                    doctorTimingReq.isAvailable =
                        binding.linSLotSelection.switchAvailibility.isChecked
                    doctorTimingReq.callDuration = getTrimmedText(binding.linSLotSelection.etDuration).toInt()
                    doctorTimingReq.startHour = startHour
                    doctorTimingReq.startMinutes = startMinute
                    doctorTimingReq.endHour = endHour
                    doctorTimingReq.endMinutes = endMinute
                    doctorTimingReq.weekday = returnWeekDay()
                    profileViewModel.callUpdateDoctorTimings(
                        getUserAuthToken(),
                        "" + getUserObject().userIdd,
                        doctorTimingReq
                    )
                } else {
                    Log.d("update_log","0")
                    var addressDTO = AddressDto()
                    addressDTO.address1 = "Address line 1"
                    addressDTO.address2 = "Address line 2"
                    addressDTO.city = "City"
                    addressDTO.postalCode = "123456"
                    addressDTO.state = "State"

                    var appUserObj = com.oncobuddy.app.models.pojo.doctor_update.AppUser()
                    appUserObj.firstName = getTrimmedText(binding.etFullName)
                    appUserObj.lastName = ""
                    appUserObj.email = getTrimmedText(binding.etEmail)
                    appUserObj.phoneNumber = getTrimmedText(binding.etPhoneNummber)
                    appUserObj.headline = getTrimmedText(binding.etHeadLine)
                    appUserObj.addressDto = addressDTO
                    appUserObj.dpLink = FILE_URL


                    var doctorRegistrationInput = DoctorRegistrationInput()
                    if (!getTrimmedText(binding.etConsultationFees).isNullOrEmpty()) {
                        doctorRegistrationInput.consultationFee = getTrimmedText(binding.etConsultationFees).toDouble()
                    } else {
                        doctorRegistrationInput.consultationFee = 0.0
                    }

                    doctorRegistrationInput.currentLocation = getTrimmedText(binding.etCurrentLocation)
                    doctorRegistrationInput.specialization = getTrimmedText(binding.etSpecialization)
                    doctorRegistrationInput.designation = getTrimmedText(binding.etDesignation)
                    doctorRegistrationInput.experience = getTrimmedText(binding.etYearsOfExperience)
                    doctorRegistrationInput.hospitalId = selectedLocation!!.id
                    doctorRegistrationInput.appUser = appUserObj

                    profileViewModel.updateProfile(
                        getUserAuthToken(),
                        null,
                        doctorRegistrationInput,
                        Constants.ROLE_DOCTOR
                    )
                    Log.d("update_log","1")
                }

            }
        })
    }

    private fun resetCOntrollers() {
        binding.linSLotSelection.switchAvailibility.isChecked = false
        binding.linSLotSelection.etFromTime.setText("")
        binding.linSLotSelection.etToTime.setText("")
        binding.linSLotSelection.etDuration.setText("")
    }

    private fun returnWeekDay(): String {
        if (selectedDay == MONDAY) return "MONDAY"
        else if (selectedDay == TUESDAY) return "TUESDAY"
        else if (selectedDay == WEDNESSDAY) return "WEDNESDAY"
        else if (selectedDay == THURSDAY) return "THURSDAY"
        else if (selectedDay == FRIDAY) return "FRIDAY"
        else if (selectedDay == SATURDAY) return "SATURDAY"
        else return "SUNDAY"
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.doctorTimingResponse.observe(this, weekDayListObserver)
        profileViewModel.responseUpdateTimings.observe(this, updateTimingsObserver)
        profileViewModel.hospitalListResonseData.observe(this, hospitalListObserver)
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private fun showSelectHospitalDialogue() {
        if (this::hospitalList.isInitialized) {
            selectCancerTypeDialog = Dialog(FourBaseCareApp.activityFromApp)
            selectCancerTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectCancerTypeDialog.setContentView(R.layout.dialogue_select_or_add_doctor)

            val rvDoctors: RecyclerView = selectCancerTypeDialog.findViewById(R.id.rvDoctors)
            val linAddNewDoctor: LinearLayout =
                selectCancerTypeDialog.findViewById(R.id.linAddNewDoctor)
            val ivAddLogo: ImageView = selectCancerTypeDialog.findViewById(R.id.ivAddLogo)
            val tvTitle: TextView = selectCancerTypeDialog.findViewById(R.id.tvTitle)
            linAddNewDoctor.visibility = View.GONE
            ivAddLogo.visibility = View.GONE
            tvTitle.setText("Select hospital")

            rvDoctors.apply {
                layoutManager = LinearLayoutManager(activity)
                hospitalListingAdapter =
                    HospitalListingAdapter(hospitalList, this@DoctorEditProfileFragment)
                adapter = hospitalListingAdapter
                hospitalListingAdapter.submitList(hospitalList)
            }


            selectCancerTypeDialog.show()
        } else {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Hospitals not found")
        }


    }

    private fun showSelectHospitalLocationDialogue() {
        if (this::locationsList.isInitialized) {
            selectLocationDialog = Dialog(FourBaseCareApp.activityFromApp)
            selectLocationDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectLocationDialog.setContentView(R.layout.dialogue_select_or_add_doctor)

            val rvDoctors: RecyclerView = selectLocationDialog.findViewById(R.id.rvDoctors)
            val linAddNewDoctor: LinearLayout =
                selectLocationDialog.findViewById(R.id.linAddNewDoctor)
            val ivAddLogo: ImageView = selectLocationDialog.findViewById(R.id.ivAddLogo)
            val tvTitle: TextView = selectLocationDialog.findViewById(R.id.tvTitle)
            linAddNewDoctor.visibility = View.GONE
            ivAddLogo.visibility = View.GONE
            tvTitle.setText("Select location")

            rvDoctors.apply {
                layoutManager = LinearLayoutManager(activity)
                hospitalLocationsListingAdapter =
                    HospitalLocationsListingAdapter(locationsList, this@DoctorEditProfileFragment)
                adapter = hospitalLocationsListingAdapter
                hospitalLocationsListingAdapter.submitList(locationsList)
            }


            selectLocationDialog.show()
        } else {
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Please select hospital")
        }


    }


    private val hospitalListObserver = Observer<HospitalListingResponse> { responseObserver ->
        if (responseObserver.success) {
            hospitalList = ArrayList()
            hospitalList.addAll(responseObserver.payLoad)

            val adapter = HospitalsAutoCOmpleteAdapter(
                FourBaseCareApp.activityFromApp,
                R.layout.raw_hospital_type,
                hospitalList
            )
            binding.etHospital.setAdapter(adapter)


            if (!hospitalList.isNullOrEmpty() && !hospitalList.get(0).locationsList.isNullOrEmpty()) {
                locationsList = ArrayList()
                locationsList.addAll(hospitalList.get(0).locationsList)
                val adapter = LocationsAutoCompleteAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_hospital_type,
                    locationsList
                )
                binding.etLocation.setAdapter(adapter)
            }

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding cancer type"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private val weekDayListObserver = Observer<DoctorAvailibilityResponse> { responseObserver ->
        if (responseObserver.success) {

            var serverTimingsList = responseObserver.payLoad
            Log.d("days_log", "days came from server " + serverTimingsList.size)
            for (serverDayObj in serverTimingsList) {
                when (serverDayObj.weekday) {
                    "MONDAY" -> {
                        weekDayTimingsList.set(0, serverDayObj)
                        Log.d("days_log", "Monday set")
                    }
                    "TUESDAY" -> {
                        weekDayTimingsList.set(1, serverDayObj)
                        Log.d("days_log", "Tues set")
                    }
                    "WEDNESDAY" -> {
                        weekDayTimingsList.set(2, serverDayObj)
                        Log.d("days_log", "Wed set")
                    }
                    "THURSDAY" -> {
                        weekDayTimingsList.set(3, serverDayObj)
                        Log.d("days_log", "Thurs set")
                    }
                    "FRIDAY" -> {
                        weekDayTimingsList.set(4, serverDayObj)
                        Log.d("days_log", "Fruday set")
                    }
                    "SATURDAY" -> {
                        weekDayTimingsList.set(5, serverDayObj)
                        Log.d("days_log", "Sat set")
                    }
                    "SUNDAY" -> {
                        weekDayTimingsList.set(6, serverDayObj)
                        Log.d("days_log", "Sunday set")
                    }
                }
            }
            if (IS_FIRST_TIME) {
                selectedDay = MONDAY
                weekDay = weekDayTimingsList.get(0)

            }
            Log.d("clock_log", "0")
            setSelectedDay()


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "No timings found! Please add your suitable timings for virtual consultation"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private fun setWeekDayData() {
        for (weekday in weekDayTimingsList) {
            when (weekday.weekday) {
                "MONDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == MONDAY,
                        binding.linSLotSelection.ivMonday,
                        binding.linSLotSelection.linMonday,
                        binding.linSLotSelection.tvMon
                    )
                }
                "TUESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == TUESDAY,
                        binding.linSLotSelection.ivTue,
                        binding.linSLotSelection.linTue,
                        binding.linSLotSelection.tvTue
                    )
                }
                "WEDNESDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == WEDNESSDAY,
                        binding.linSLotSelection.ivWed,
                        binding.linSLotSelection.linWed,
                        binding.linSLotSelection.tvWed
                    )
                }
                "THURSDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable, selectedDay == THURSDAY,
                        binding.linSLotSelection.ivThu,
                        binding.linSLotSelection.linThu,
                        binding.linSLotSelection.tvThu
                    )
                }
                "FRIDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == FRIDAY,
                        binding.linSLotSelection.ivFri,
                        binding.linSLotSelection.linFri,
                        binding.linSLotSelection.tvFri
                    )
                }
                "SATURDAY" -> {

                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SATURDAY,
                        binding.linSLotSelection.ivSat,
                        binding.linSLotSelection.linSat,
                        binding.linSLotSelection.tvSat
                    )
                }
                "SUNDAY" -> {
                    setLookAsPerDay(
                        weekday.isAvailable,
                        selectedDay == SUNDAY,
                        binding.linSLotSelection.ivSun,
                        binding.linSLotSelection.linSun,
                        binding.linSLotSelection.tvSun
                    )

                }


            }
        }


    }

    private fun setAllDaysLook() {


    }

    private fun setLookAsPerDay(
        isDayAvailable: Boolean,
        isDaySelected: Boolean,
        ivImage: ImageView,
        linBg: LinearLayout,
        tvTextView: TextView
    ) {

        Log.d("selection_journey", "0")
        if (isDayAvailable) {
            if (isDaySelected) {
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.iv_circular_tick_white
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.red_border_purple_bg)
                tvTextView.setTextColor(getResourceColor(R.color.white))
            } else {
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.iv_circular_tick_white
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.no_border_purple_bg)
                tvTextView.setTextColor(getResourceColor(R.color.white))
            }

        } else {
            Log.d("selection_journey", "1")
            if (isDaySelected) {
                Log.d("selection_journey", "2")
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_add_red
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.red_border_gray_bg)
                tvTextView.setTextColor(getResourceColor(R.color.colorPrimaryDark))
            } else {
                Log.d("selection_journey", "3")
                ivImage.setImageDrawable(
                    ContextCompat.getDrawable(
                        FourBaseCareApp.activityFromApp,
                        R.drawable.ic_add_red
                    )
                )
                linBg.background =
                    getResourceDrawable(R.drawable.no_border_gray_bg)
                tvTextView.setTextColor(getResourceColor(R.color.colorPrimaryDark))
            }
        }
    }

    private fun setDataForSelectedDay() {

        weekDay = weekDayTimingsList.get(selectedDay)

        Log.d("day_log", "weekday " + weekDay.weekday)
        Log.d("day_log", "weekday " + weekDay.isAvailable)
        binding.linSLotSelection.switchAvailibility.isChecked = weekDay.isAvailable
        if (weekDay.isAvailable) {
            //Log.d("clock_log","weekday available")
            var fromTime = ""
           // startHour = weekDay.startHour
            //startMinute = weekDay.startMinutes
            Log.d("clock_log", "weekday available")
            Log.d("clock_log", "start h " + startHour)
            Log.d("clock_log", "start m" + startMinute)

            if (startHour == 0)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " AM"
            else if (startHour > 12)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " PM"
            else if (startHour == 12)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + startHour) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " PM"
            else
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (startHour)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + startMinute
                    ) + " AM"


            var endTime = ""
            //endHour = weekDay.endHour
            //endMinute = weekDay.endMinutes

            Log.d("clock_log", "end h " + endHour)
            Log.d("clock_log", "end m" + endMinute)

            if (endHour == 0)
                fromTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour + 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " AM"
            else if (endHour > 12)
                endTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour - 12)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " PM"
            else if (endHour == 12)
                endTime =
                    CommonMethods.getStringWithOnePadding("" + endHour) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " PM"
            else
                endTime =
                    CommonMethods.getStringWithOnePadding("" + (endHour)) + " : " + CommonMethods.getStringWithOnePadding(
                        "" + endMinute
                    ) + " AM"

            Log.d("clock_log", "from time set " + fromTime)
            Log.d("clock_log", "to time set " + endTime)

            // var startTime = weekDay.startHour.toString() + " : " + weekDay.startMinutes
            // var endTime = weekDay.endHour.toString() + " : " + weekDay.endMinutes
            binding.linSLotSelection.etFromTime.setText(fromTime)
            binding.linSLotSelection.etToTime.setText(endTime)
            binding.linSLotSelection.etDuration.setText("" + weekDay.callDuration)
            binding.linSLotSelection.etDuration.isEnabled = true

        } else {
            binding.linSLotSelection.etDuration.isEnabled = false
            resetCOntrollers()
        }
    }

    private val updateProfileObserver = Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            userObj.firstName = getTrimmedText(binding.etFullName)
            userObj.phoneNumber = getTrimmedText(binding.etPhoneNummber)
            userObj.email = getTrimmedText(binding.etEmail)

            //userObj.cancerTypeId = cancerTypeId
            //userObj.cancerSubTypeId = cancerSubTypeId
            userObj.dpLink = FILE_URL
            userObj.headline = getTrimmedText(binding.etHeadLine)

            if (userObj.profile == null) {
                userObj.profile = Profile()
            }
            userObj.profile.designation = getTrimmedText(binding.etDesignation)
            userObj.profile.currentLocation = getTrimmedText(binding.etCurrentLocation)

            if (!getTrimmedText(binding.etConsultationFees).isNullOrEmpty()) {
                userObj.profile.consultationFee =
                    getTrimmedText(binding.etConsultationFees).toDouble()
            } else {
                userObj.profile.consultationFee = 0.0
            }
            userObj.profile.specialization = getTrimmedText(binding.etSpecialization)
            userObj.profile.experience = getTrimmedText(binding.etYearsOfExperience)
            userObj.profile.hospital = hospital

            userObj.profile.selectedLocation = selectedLocation

            val gson = Gson()
            val userStr = gson.toJson(userObj)
            FourBaseCareApp.savePreferenceDataString(Constants.PREF_USER_OBJ, userStr)

            fragmentManager!!.popBackStack()


        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem updating profile!"
            )
        }
        binding.executePendingBindings()
        binding.invalidateAll()


    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean> { isLoading ->
        if (isLoading) showHideProgress(true, binding.layoutProgress.frProgress)
        else showHideProgress(false, binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String> { message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun isValidInput(): Boolean {
        if (IS_TIMING_MODE) {
            Log.d("timing_log","start time "+startTimeInMilis)
            Log.d("timing_log","start time "+endTImeInMilis)
            if(getTrimmedText(binding.linSLotSelection.etFromTime).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select From Time")
                return false
            }
            else if(getTrimmedText(binding.linSLotSelection.etToTime).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select to Time")
                return false
            }else if(getTrimmedText(binding.linSLotSelection.etFromTime).equals(getTrimmedText(binding.linSLotSelection.etToTime),true)){
                showToast(FourBaseCareApp.activityFromApp, "Consultation start and end timings cannot be the same")
                return false
            }else if(startTimeInMilis > endTImeInMilis){
                showToast(FourBaseCareApp.activityFromApp, "Consultation start time should be earlier than end time")
                return false
            } else if(getTrimmedText(binding.linSLotSelection.etDuration).isEmpty()){
                showToast(FourBaseCareApp.activityFromApp, "Please select consultation duration")
                return false
            }else if(getTrimmedText(binding.linSLotSelection.etDuration).toInt() == 0){
                showToast(FourBaseCareApp.activityFromApp, "Duration time cannot be 0")
                return false
            }


        } else {
            Log.d("update_log","validate")
            if (getTrimmedText(binding.etFullName).isNullOrEmpty()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_name)
                )
                return false
            } else if (getTrimmedText(binding.etPhoneNummber).isNullOrBlank()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_mobile_number)
                )
                return false
            } else if (getTrimmedText(binding.etPhoneNummber).toString().trim().length < 10) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_invalid_mobile_number)
                )
                return false
            } else if (getTrimmedText(binding.etEmail).isNullOrEmpty()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_email)
                )
                return false
            } else if (!CommonMethods.isValidEmail(getTrimmedText(etEmail))) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_valid_email)
                )
                return false
            } else if (getTrimmedText(binding.etConsultationFees).isNullOrEmpty()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_consultation_fees)
                )
                return false
            } else if (getTrimmedText(binding.etConsultationFees).toDouble() < 100.0) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_minimum_consultation_fees)
                )
                return false
            } else if (getTrimmedText(binding.etHospital).isNullOrEmpty()) {
                Log.d("update_log","validate h")
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_hospital)
                )
                return false
            }
            else if (selectedLocation == null) {
                Log.d("update_log","locarion not valid")

                showToast(FourBaseCareApp.activityFromApp, "Please select hospital location")
                return false
            } else if (!getTrimmedText(binding.etLocation).isNullOrEmpty() && !locationsList.isNullOrEmpty()) {
                var isValid = false
                for (location in locationsList) {
                    if (getTrimmedText(binding.etLocation).equals(location.name)) {
                        isValid = true
                    }
                }
                if (!isValid) {
                    showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.validation_enter_valid_location)
                    )
                    return false
                }

            } else if (getTrimmedText(binding.etDesignation).isNullOrEmpty()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_designation)
                )
                return false
            } else if (getTrimmedText(binding.etSpecialization).isNullOrEmpty()) {
                showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.validation_enter_specialization)
                )
                return false
            } else if (!getTrimmedText(binding.etYearsOfExperience).isNullOrEmpty() && getTrimmedText(
                    binding.etYearsOfExperience
                ).toInt() == 0
            ) {
                showToast(FourBaseCareApp.activityFromApp, "Years of experience cannot be 0")
                return false
            }


        }
        Log.d("update_log","validated true")
        return true
    }

    private fun showHideBottomButton() {
        binding.root.getViewTreeObserver()
            .addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
                override fun onGlobalLayout() {
                    val r = Rect()
                    binding.root.getWindowVisibleDisplayFrame(r)
                    val heightDiff: Int =
                        binding.root.getRootView().getHeight() - (r.bottom - r.top)
                    CommonMethods.showLog("button_height", "" + heightDiff)
                    if (heightDiff > 300) { // if more than 100 pixels, its probably a keyboard...
                        //ok now we know the keyboard is up...
                        CommonMethods.showLog("button_height", "button hidden")
                        //binding.tvEditProfile.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        CommonMethods.showLog("button_height", "button visible")
                        // binding.tvEditProfile.setVisibility(View.VISIBLE)
                    }
                }
            })
    }

    private fun uploadFileToS3() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {

            Log.d("upload_log", "File path1.2 " + FILE_PATH)

            val body: MultipartBody.Part
            val file = File(FILE_PATH)

            val requestFile: RequestBody =
                file.asRequestBody("multipart/form-data".toMediaTypeOrNull())
            body = MultipartBody.Part.createFormData("file", file.name, requestFile)

            profileViewModel.callFileUpload(
                getUserAuthToken(), body
            )
            Log.d("upload_log", "2 " + FILE_PATH)

        }
    }

    private val fileUploadResponseObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData

            FILE_URL = responseObserver.message
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile picture uploaded successfully! Click on Save button to update profile picture."
            )
            binding.executePendingBindings()
            binding.invalidateAll()
            Log.d("edit_profile_log", "profile pic set " + FILE_URL)
        }

    private val updateTimingsObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData

            //FILE_URL = responseObserver.message
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Timings updated Successfully")
            binding.executePendingBindings()
            binding.invalidateAll()
            getDoctorTimings()
        }

    private fun openGalleryForImage() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.setFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        intent.type = "image/*"
        startActivityForResult(intent, Constants.PICK_GALLERY_IMAGE)
    }

    private fun openCameraIntent() {
        val takePictureIntent =
            Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(FourBaseCareApp.activityFromApp.getPackageManager()) != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                try {
                    // Create the File where the photo should go
                    var photoFile: File? = null
                    try {
                        photoFile = createImageFile()
                    } catch (ignored: IOException) {
                    }
                    // Continue only if the File was successfully created
                    if (photoFile != null) {
                        val photoURI = FileProvider.getUriForFile(
                            FourBaseCareApp.activityFromApp,
                            FourBaseCareApp.activityFromApp.getPackageName() + ".provider",
                            photoFile
                        )
                        takePictureIntent.putExtra(
                            MediaStore.EXTRA_OUTPUT,
                            photoURI
                        )
                        startActivityForResult(takePictureIntent, Constants.START_CAMERA)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            } else {
                startActivityForResult(takePictureIntent, Constants.START_CAMERA)
            }
        }
    }

    @Throws(IOException::class)
    private fun createImageFile(): File? {
        // Create an image file name
        val timeStamp =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault())
                .format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir: File? =
            FourBaseCareApp.activityFromApp.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.absolutePath
        return image
    }

    private fun getResizedBitmap(image: Bitmap): Bitmap? {
        return try {
            val width = image.width / 2
            val height = image.height / 2
            Bitmap.createScaledBitmap(image, width, height, true)
        } catch (e: NullPointerException) {
            image
        }
    }

    fun getImageUri(
        inContext: Context?,
        inImage: Bitmap?
    ): Uri? {
        val bytes = ByteArrayOutputStream()
        inImage?.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            null
        )
        return Uri.parse(path)
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_GALLERY_IMAGE) {
            // binding.ivProduct.setImageURI(data?.data) // handle chosen image
            //strSelectedImagePath = data?.data.toString()

            val uri = data!!.data
            mCurrentPhotoPath =
                FileUtils.getRealPathFromURI_API19(FourBaseCareApp.activityFromApp, uri)
            var imgBitmap: Bitmap?
            imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                BitmapFactory.decodeFile(mCurrentPhotoPath)
            } else {
                val extras = data!!.extras
                extras!!["data"] as Bitmap?
            }
            try {
                if (imgBitmap != null) {
                    while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                        imgBitmap = getResizedBitmap(imgBitmap)
                    }
                }
            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
            // CALL THIS METHOD TO GET THE URI FROM THE BITMAP

            //val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
            // start cropping activity for pre-acquired image saved on the device
            CropImage.activity(uri)
                .start(FourBaseCareApp.activityFromApp, this)


        } else if (resultCode == Activity.RESULT_OK && requestCode == Constants.START_CAMERA) {
            try {
                var imgBitmap: Bitmap?
                imgBitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    BitmapFactory.decodeFile(mCurrentPhotoPath)
                } else {
                    val extras = data!!.extras
                    extras!!["data"] as Bitmap?
                }
                try {
                    if (imgBitmap != null) {
                        while (imgBitmap!!.height > 2048 || imgBitmap!!.width > 2048) {
                            imgBitmap = getResizedBitmap(imgBitmap)
                        }
                    }
                } catch (e: java.lang.Exception) {
                    e.printStackTrace()
                }

                val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp, imgBitmap)
                // start cropping activity for pre-acquired image saved on the device
                CropImage.activity(mImageUri)
                    .start(FourBaseCareApp.activityFromApp)


            } catch (e: java.lang.Exception) {
                e.printStackTrace()
            }
        } else if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            if (data != null) {
                val result: CropImage.ActivityResult = CropImage.getActivityResult(data)
                if (resultCode == Activity.RESULT_OK) {
                    val resultUri: Uri = result.getUri()
                    //val uri = data!!.data
                    Glide.with(FourBaseCareApp.activityFromApp).load(resultUri).transform(
                        CircleCrop()
                    ).into(binding.ivProfile)

                    FILE_PATH =
                        FileUtils.getRealPathFromURI_API19(
                            FourBaseCareApp.activityFromApp,
                            resultUri
                        )
                    uploadFileToS3()


                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    val error: java.lang.Exception = result.getError()
                    CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Error")
                }
            }

        }
    }

    fun isPermissionsAllowed(): Boolean {
        return ContextCompat.checkSelfPermission(
            FourBaseCareApp.activityFromApp,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun askForPermissions(): Boolean {
        if (!isPermissionsAllowed()) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    FourBaseCareApp.activityFromApp,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            ) {
                showPermissionDeniedDialog()
            } else {
                requestPermissions(
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE),
                    Constants.REQUEST_EXTERNAL_STORAGE
                )
            }
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            Constants.REQUEST_EXTERNAL_STORAGE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openGalleryForImage()
                } else {
                    CommonMethods.showToast(
                        FourBaseCareApp.activityFromApp,
                        getString(R.string.msg_allow_permission)
                    )
                }
                return
            }
        }
    }

    private fun showPermissionDeniedDialog() {
        AlertDialog.Builder(FourBaseCareApp.activityFromApp)
            .setTitle(getString(R.string.msg_permission_denied))
            .setMessage(getString(R.string.msg_permission_from_settings))
            .setPositiveButton(getString(R.string.title_app_settings),
                DialogInterface.OnClickListener { dialogInterface, i ->
                    // send to app settings if permission is denied permanently
                    val intent = Intent()
                    intent.action = Settings.ACTION_APPLICATION_DETAILS_SETTINGS
                    val uri = Uri.fromParts(
                        "package",
                        FourBaseCareApp.activityFromApp.getPackageName(),
                        null
                    )
                    intent.data = uri
                    startActivity(intent)
                })
            .setNegativeButton("Cancel", null)
            .show()
    }

    override fun onHospitalSelected(position: Int, item: HospitalDetails, view: View) {
        hospital = Hospital()
        hospital!!.id = item.id
        hospital!!.name = item.name

        if (!item.locationsList.isNullOrEmpty()) {
            locationsList = ArrayList()
            locationsList.addAll(item.locationsList)
            val adapter = LocationsAutoCompleteAdapter(
                FourBaseCareApp.activityFromApp,
                R.layout.raw_hospital_type,
                locationsList
            )
            binding.etLocation.setAdapter(adapter)
        }

        binding.etHospital.setText(item.name)
        /*binding.etHeadLine.setText(item.name)
        binding.etCancerSubType.setText("")*/
        selectCancerTypeDialog.dismiss()
    }

    override fun onLocationSelected(position: Int, item: Locations, view: View) {
        hospitalLocationId = item.id
        selectedLocation = Locations()
        selectedLocation!!.id = item.id
        selectedLocation!!.name = item.name
        binding.etLocation.setText(item.name)
        selectLocationDialog.dismiss()
    }


}