package com.oncobuddy.app.views.fragments


import android.Manifest
import android.app.Activity
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.*
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
import android.text.format.DateFormat
import android.text.format.Time
import android.util.Log
import android.view.*
import android.widget.*
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.widget.doAfterTextChanged
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
import com.oncobuddy.app.databinding.NovFragmentPatientEditProfileBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.add_care_taker.AddCareTakerResponse
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverDetails
import com.oncobuddy.app.models.pojo.care_giver_details.CareGiverResponse
import com.oncobuddy.app.models.pojo.login_response.LoginResponse
import com.oncobuddy.app.models.pojo.login_response.Profile
import com.oncobuddy.app.models.pojo.profile.CancerType
import com.oncobuddy.app.models.pojo.profile.CancerTypesListResponse
import com.oncobuddy.app.models.pojo.registration_process.AddCareCompanionInput
import com.oncobuddy.app.models.pojo.registration_process.AddressDTO
import com.oncobuddy.app.models.pojo.registration_process.AppUser
import com.oncobuddy.app.models.pojo.registration_process.RegistrationInput
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.FileUtils
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.CancerSpinerAdapter
import com.oncobuddy.app.views.adapters.CancerSubTypeListingAdapter
import com.oncobuddy.app.views.adapters.CancerTypeListingAdapter
import com.oncobuddy.app.views.adapters.CareGiversAdapter
import com.theartofdev.edmodo.cropper.CropImage
import kotlinx.android.synthetic.main.activity_nov_account_setup_activity.*
import kotlinx.android.synthetic.main.activity_nov_account_setup_activity.spPrmarySite
import kotlinx.android.synthetic.main.dialogue_add_member.*
import kotlinx.android.synthetic.main.fragment_doctor_edit_profile_new.*
import kotlinx.android.synthetic.main.layout_cancer_selection.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.concurrent.schedule


class EditProfileFragment : BaseFragment(),
    CancerTypeListingAdapter.Interaction,
    CancerSubTypeListingAdapter.Interaction,
    CareGiversAdapter.Interaction {

    private lateinit var binding: NovFragmentPatientEditProfileBinding
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var cancerTypesList: ArrayList<CancerType>
    private lateinit var cancerTypeListingAdapter: CancerTypeListingAdapter

    private lateinit var cancerSubTypeListingAdapter: CancerSubTypeListingAdapter
    private lateinit var cancerSubTypesList: ArrayList<CancerType>

    private lateinit var selectCancerTypeDialog: Dialog
    private lateinit var selectCancerSubTypeDialog: Dialog

    private var cancerTypeId = 0L
    private var cancerSubTypeId = 0L
    private var selectedCancerType = CancerType()
    private var selectedSUbCancerType = CancerType()


    private var cancerStage = "Zero"

    private var selectedGender = "NOT_SPECIFIED"
    private var FILE_URL = ""
    private lateinit var FILE_PATH: String
    private var mCurrentPhotoPath: String? = null
    private var hasSetInitialData = false
    private var selectedDate = ""

    private val BASIC_INFO = 1
    private val ABOUT_CANCER = 2
    private val MY_CARE_GIVERS = 3
    private var SELECTED_MODULE = BASIC_INFO

    private lateinit var cancerSpinnerAdapter: CancerSpinerAdapter
    private lateinit var subCancerSpinnerAdapter: CancerSpinerAdapter

    private lateinit var careGiverList: ArrayList<CareGiverDetails>
    private lateinit var careGiverAdapter: CareGiversAdapter
    private lateinit var deleteConfirmationDialogue: Dialog

    private var IS_FIRST_TIME = true
    private var IS_FIRST_TIME_PRIMARY = true


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
            R.layout.nov_fragment_patient_edit_profile, container, false
        )
        setTitle()
        //showHideBottomButton()
        setupVM()
        setupObservers()
        setupUserData()
        setSelectionColor()
        setupClickListeners()
        initializeFIrstSpinner()
        initializeCancerSUbTypes()
    }

    private fun initializeFIrstSpinner() {
        cancerTypesList = ArrayList()
        var cancerType = CancerType()
        cancerType.id = 0
        cancerType.name = "Select Primary Cancer Site"
        cancerTypesList.add(cancerType)
        val array = arrayOfNulls<CancerType>(cancerTypesList.size)
        cancerTypesList.toArray(array)
        cancerSpinnerAdapter =
            CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
        binding.layOutCancer.spPrmarySite.adapter = cancerSpinnerAdapter

    }

    private fun initializeCancerSUbTypes() {
        cancerSubTypesList = ArrayList()
        var cancerType = CancerType()
        cancerType.id = 0
        cancerType.name = "Primary Cancer location"
        cancerSubTypesList.add(cancerType)
        val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
        cancerSubTypesList.toArray(array)
        subCancerSpinnerAdapter =
            CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
        binding.layOutCancer.spPrmaryLocation.adapter = subCancerSpinnerAdapter
    }

    private fun setTitle() {
        binding.layoutHeader.tvTitle.setText("Edit Profile")
    }

    private fun setupUserData() {

        Log.d("profile_set_log", "name set " + getUserObject().firstName)
        binding.layoutasicinfo.edFullName.setText(getUserObject().firstName)

        if (getUserObject().email.isNullOrEmpty() || getUserObject().email.equals("null", true)) {
            binding.layoutasicinfo.edEmail.setText("")

        } else {
            binding.layoutasicinfo.edEmail.setText("" + getUserObject().email)
        }

        if (getUserObject().profile.aadharNumber.isNullOrEmpty() || getUserObject().profile.aadharNumber.equals(
                "null",
                true
            )
        ) {
            binding.layoutasicinfo.edAadhar.setText("")

        } else {
            binding.layoutasicinfo.edAadhar.setText("" + getUserObject().profile.aadharNumber)
        }

        if (getUserObject().profile.height.isNullOrEmpty() || getUserObject().profile.height.equals(
                "null",
                true
            )
        ) {
            binding.layoutasicinfo.edHeight.setText("")

        } else {
            binding.layoutasicinfo.edHeight.setText("" + getUserObject().profile.height+" inch")
        }

        if (getUserObject().profile.weight.isNullOrEmpty() || getUserObject().profile.weight.equals(
                "null",
                true
            )
        ) {
            binding.layoutasicinfo.edWeight.setText("")

        } else {
            binding.layoutasicinfo.edWeight.setText("" + getUserObject().profile.weight+" kg")
        }

        if (getUserObject().profile.dateOfBirth.isNullOrEmpty() || getUserObject().profile.dateOfBirth.equals(
                "null",
                true
            )
        ) {
            binding.layoutasicinfo.edDob.setText("")

        } else {
            binding.layoutasicinfo.edDob.setText("" + getUserObject().profile.dateOfBirth)
        }

        binding.layoutasicinfo.edPhoneNumber.setText(getUserObject().phoneNumber)

        //binding.layoutasicinfo.edDob.setText(""+getUserObject().dateOfBirth)

        if (getUserObject().profile != null) {

            if (getUserObject().profile.cancerType != null) {
                cancerTypeId = getUserObject().profile.cancerType.id
                Log.d("cancer_stage_log", "set data " + cancerTypeId)
            }
            if (getUserObject().profile.cancerSubType != null) {
                cancerSubTypeId = getUserObject().profile.cancerSubType.id
                Log.d("cancer_stage_log", "set sub data " + cancerSubTypeId)

            }

            cancerStage = "Zero"//getUserObject().cancerStage

            if (getUserObject().profile.cancerStage != null) {
                cancerStage = getUserObject().profile.cancerStage
                var itemPosition =
                    FourBaseCareApp.activityFromApp.resources.getStringArray(R.array.cancer_stage)
                        .indexOf(cancerStage)
                binding.layOutCancer.spCancerSTage.setSelection(itemPosition)
            }

            //Log.d("cancer_stage_log","2 "+getUserObject().cancerStage)

            if (!getUserObject().profile.dateOfBirth.isNullOrEmpty()) {
                selectedDate = getUserObject().profile.dateOfBirth
                //binding.etDob.setText(selectedDate)
            }

            if ((getUserObject().profile.gender) != null) {
                binding.layoutasicinfo.edGender.setText(CommonMethods.returnGenderName(getUserObject().profile.gender))
                selectedGender = getUserObject().profile.gender
            } else {
                //binding.etGender.setText("")
            }
        }

        /* if(checkIFCareCOmpanion())
             if(getUserObject().headline.isNullOrEmpty())binding.etHeadLine.setText("Care Companion") else binding.etHeadLine.setText(getUserObject().headline)
         else
             if(getUserObject().headline.isNullOrEmpty())binding.etHeadLine.setText("Patient") else binding.etHeadLine.setText(getUserObject().headline)
 */
        if (!getUserObject().dpLink.isNullOrEmpty()) FILE_URL = getUserObject().dpLink
        Glide.with(FourBaseCareApp.activityFromApp)
            .load(getUserObject().dpLink)
            .circleCrop()
            .placeholder(R.drawable.ic_user_image).error(R.drawable.ic_user_image)
            .into(binding.layoutasicinfo.ivProfile)

    }

    private fun setupClickListeners() {

        binding.layOutMyCG.ivDropDownCGOne.setOnClickListener(View.OnClickListener {
            binding.layOutMyCG.spCGOneRelationShip.performClick()
        })

        binding.layoutasicinfo.edGender.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())showGenderSelectDialogue()
        })

        binding.layOutMyCG.linAddCareGiver.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                if (::careGiverList.isInitialized && careGiverList.size >= 2) {
                    //showToast(FourBaseCareApp.activityFromApp, FourBaseCareApp.activityFromApp.getString(R.string.validation_2_caregivers_max))
                } else {
                    CommonMethods.addNextFragment(
                        FourBaseCareApp.activityFromApp,
                        AddCareTakerFragment(), this, false
                    )
                }
            }
        })

        binding.layoutasicinfo.edAadhar.doAfterTextChanged { text ->
            val formattedText = text.toString().replace(" ", "").chunked(4).joinToString(" ")
            if (formattedText != text.toString()) {
                binding.layoutasicinfo.edAadhar.setText(formattedText)
                binding.layoutasicinfo.edAadhar.setSelection(binding.layoutasicinfo.edAadhar.length())
            }
        }


        binding.layOutCancer.ivDropDownOne.setOnClickListener(View.OnClickListener {
            binding.layOutCancer.spPrmarySite.performClick()
        })

        binding.layOutCancer.ivDropDownTwo.setOnClickListener(View.OnClickListener {
            binding.layOutCancer.spPrmaryLocation.performClick()
        })

        binding.layOutCancer.ivDropDownThree.setOnClickListener(View.OnClickListener {
            binding.layOutCancer.spCancerSTage.performClick()
        })

        binding.layOutCancer.spPrmarySite.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (::cancerTypesList.isInitialized) {
                    if (position != 0) {
                        Log.d("cancer_log", "loaded for first time")
                        //cancerSubTypeId = 0L
                        cancerTypeId = cancerTypesList.get(position).id.toLong()
                        selectedCancerType = cancerTypesList.get(position)
                        getCancerSubTypesFromServer(cancerTypeId.toString())
                        Log.d("cancer_log", "cancer type " + cancerTypeId)
                    }
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        binding.layOutCancer.spCancerSTage.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (position == 0) {
                    cancerStage = "Zero"
                } else {
                    cancerStage =
                        FourBaseCareApp.activityFromApp.resources.getStringArray(R.array.cancer_stage)
                            .get(position)
                }


                Log.d("cancer_stage", "selected " + cancerStage)
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }


        }

        binding.layOutCancer.spPrmaryLocation.onItemSelectedListener = object :
            AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View,
                position: Int,
                id: Long
            ) {
                if (::cancerSubTypesList.isInitialized && !IS_FIRST_TIME) {
                    cancerSubTypeId = cancerSubTypesList.get(position).id.toLong()
                    selectedSUbCancerType = cancerSubTypesList.get(position)
                    Log.d("cancer_stage_log", "After primary location spinner " + cancerSubTypeId)
                    //getCancerSubTypesFromServer(cancerTypeId.toString())
                    Log.d("cancer_log", "cancer sub type " + cancerSubTypeId)
                } else {
                    IS_FIRST_TIME = false
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }


        binding.linBasicInfo.setOnClickListener(View.OnClickListener {
            SELECTED_MODULE = BASIC_INFO
            setSelectionColor()
            Log.d("visi_log","VISIBLE")
            binding.tvSave.visibility = View.VISIBLE
        })

        binding.linABoutCancer.setOnClickListener(View.OnClickListener {
            SELECTED_MODULE = ABOUT_CANCER
            setSelectionColor()
            Log.d("visi_log","VISIBLE")
            binding.tvSave.visibility = View.VISIBLE
        })

        binding.linMyCareGivers.setOnClickListener(View.OnClickListener {
            SELECTED_MODULE = MY_CARE_GIVERS
            setSelectionColor()
            binding.tvSave.visibility = View.GONE
            Log.d("visi_log","GONE "+binding.tvSave.visibility)
        })

        binding.layoutasicinfo.edDob.setOnClickListener(View.OnClickListener {
            val calendar = Calendar.getInstance()
            val yy = calendar[Calendar.YEAR]
            val mm = calendar[Calendar.MONTH]
            val dd = calendar[Calendar.DAY_OF_MONTH]
            val datePicker =
                DatePickerDialog(
                    FourBaseCareApp.activityFromApp,
                    DatePickerDialog.OnDateSetListener { view: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
                        val chosenDate = Time()
                        chosenDate[dayOfMonth, monthOfYear] = year
                        val dtDob = chosenDate.toMillis(true)
                        var strDate =
                            DateFormat.format("yyyy-MM-dd", dtDob)
                        binding.layoutasicinfo.edDob.setText(strDate)
                        selectedDate = strDate.toString()
                    },
                    yy,
                    mm,
                    dd
                )

            datePicker.datePicker.maxDate = System.currentTimeMillis() - 1000
            datePicker.show()
        })


        /*binding.etGender.setOnClickListener(View.OnClickListener {
            showGenderSelectDialogue()
        })*/

        binding.layoutasicinfo.ivProfile.setOnClickListener(View.OnClickListener {
            if (isPermissionsAllowed()) openGalleryForImage()
            else askForPermissions()
        })

        binding.layoutasicinfo.edPhoneNumber.setOnClickListener(View.OnClickListener {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.you_cant_change_your_number)
            )
        })

        binding.tvSave.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
                if (SELECTED_MODULE == BASIC_INFO) {
                    if (isValidInput()) {

                        var addressDTO = AddressDTO()
                        addressDTO.address1 = "Address line 1"
                        addressDTO.address2 = "Address line 2"
                        addressDTO.city = "City"
                        addressDTO.postalCode = "123456"
                        addressDTO.state = "State"

                        var registrationInput = AppUser()
                        registrationInput.firstName =
                            getTrimmedText(binding.layoutasicinfo.edFullName)
                        registrationInput.lastName = ""
                        //registrationInput.headline = getTrimmedText(binding.etHeadLine)
                        if (!getUserObject().email.isNullOrEmpty()) {
                            if (!getUserObject().email.equals(getTrimmedText(binding.layoutasicinfo.edEmail))) {
                                registrationInput.email =
                                    getTrimmedText(binding.layoutasicinfo.edEmail)
                                // send e mail only if there is a change
                            }
                        } else {
                            registrationInput.email = getTrimmedText(binding.layoutasicinfo.edEmail)
                        }

                        registrationInput.phoneNumber =
                            getTrimmedText(binding.layoutasicinfo.edEmail)
                        registrationInput.addressDTO = addressDTO
                        registrationInput.dateOfBirth = selectedDate
                        registrationInput.gender = selectedGender

                        registrationInput.dpLink = FILE_URL
                        Log.d("edit_profile_log", "profile pic send " + FILE_URL)

                        var appUser = RegistrationInput()
                        appUser.userId = getUserIdd()
                        appUser.appUser = registrationInput
                        appUser.dateOfBirth = selectedDate
                        appUser.gender = selectedGender
                        appUser.aadharNumber = getTrimmedText(binding.layoutasicinfo.edAadhar)
                        if (!getTrimmedText(binding.layoutasicinfo.edHeight).isNullOrEmpty())
                            appUser.height =
                                getTrimmedText(binding.layoutasicinfo.edHeight).toDouble()
                        if (!getTrimmedText(binding.layoutasicinfo.edWeight).isNullOrEmpty())
                            appUser.weight =
                                getTrimmedText(binding.layoutasicinfo.edWeight).toDouble()

                        /*   if(!CommonMethods.getTrimmedText(binding.etGender).isNullOrEmpty()){
                            appUser.age = CommonMethods.getTrimmedText(binding.etAge)!!.toInt()
                        }*/

                        /*appUser.gender = "MALE"
                        appUser.age = 22*/

                        profileViewModel.updateProfile(
                            getUserAuthToken(),
                            appUser,
                            null,
                            getUserObject().role
                        )
                    }
                } else if (SELECTED_MODULE == ABOUT_CANCER) {
                    if (isValidCancerInput()) {

                        var appUser = AppUser()

                        appUser.firstName = getUserObject().firstName
                        appUser.lastName = ""
                        appUser.headline = ""
                        appUser.email = "" + getUserObject().email
                        appUser.phoneNumber = "" + getUserObject().phoneNumber
                        appUser.phoneNumber = getUserObject().phoneNumber
                        appUser.cancerTypeId = cancerTypeId
                        if(cancerSubTypeId != 0L) appUser.cancerSubTypeId = cancerSubTypeId
                        //registrationInput.cancerSubTypeId = cancerSubTypeId

                        var registrationInput = RegistrationInput()
                        registrationInput.userId = getUserIdd()
                        registrationInput.appUser = appUser
                        registrationInput.cancerTypeId = cancerTypeId
                        if(cancerSubTypeId != 0L) registrationInput.cancerSubTypeId = cancerSubTypeId
                        registrationInput.cancerStage = cancerStage
                        /*registrationInput.can*/
                        profileViewModel.updateProfile(
                            getUserAuthToken(),
                            registrationInput,
                            null,
                            Constants.ROLE_PATIENT
                        )

                    }
                } else if (SELECTED_MODULE == MY_CARE_GIVERS) {
                    Log.d("cg_log", "0")
                    if (isValidCTInput()) {
                        val relArray: Array<String> =
                            getResources().getStringArray(R.array.relationship_type)
                        Log.d("token_log", "token passed " + getUserObject().authToken)
                        var addCareCompanionInput = AddCareCompanionInput()
                        addCareCompanionInput.name = getTrimmedText(binding.layOutMyCG.etCGOneName)
                        addCareCompanionInput.mobileNumber =
                            getTrimmedText(binding.layOutMyCG.etMobileNumberCG1)
                        addCareCompanionInput.relationship =
                            CommonMethods.returnRelationSHipEnum(relArray.get(binding.layOutMyCG.spCGOneRelationShip.selectedItemPosition))
                        profileViewModel.addCareCompanion(
                            addCareCompanionInput,
                            getUserObject().authToken,
                            Constants.ROLE_PATIENT
                        )
                    }
                }
            }
        })

        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })
    }

    private fun showGenderSelectDialogue() {
        val genderOptions = arrayOf<CharSequence>("Female", "Male","Not specified")
        val alt_bld = AlertDialog.Builder(FourBaseCareApp.activityFromApp)
        var checkedItem = 2

        if (!selectedGender.isNullOrEmpty()) {
            if (selectedGender.equals("Male", true)) {
                checkedItem = 1
            } else if (selectedGender.equals("Female", true)) {
                checkedItem = 0
            } else {
                checkedItem = 2
            }
        }

        alt_bld.setTitle("Select Gender")
        alt_bld.setSingleChoiceItems(genderOptions, checkedItem,
            DialogInterface.OnClickListener { dialog, item ->
                selectedGender = CommonMethods.returnGenderEnum(genderOptions.get(item).toString())
                binding.layoutasicinfo.edGender.setText(genderOptions.get(item).toString())
                dialog.dismiss() // dismiss the alertbox after chose option
            })
        val alert = alt_bld.create()
        alert.show()


    }

    private fun showSelectCancerTypeDialogue() {
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
        tvTitle.setText(getString(R.string.select_cancer_type))

        if (!::cancerTypesList.isInitialized || cancerTypesList == null) {
            cancerTypesList = ArrayList()
        }

        rvDoctors.apply {
            layoutManager = LinearLayoutManager(activity)
            cancerTypeListingAdapter =
                CancerTypeListingAdapter(cancerTypesList, this@EditProfileFragment)
            adapter = cancerTypeListingAdapter
            cancerTypeListingAdapter.submitList(cancerTypesList)
        }


        selectCancerTypeDialog.show()

    }


    private fun showSelectCancerSubTypeDialogue() {
        if (cancerSubTypesList != null && cancerSubTypesList.size > 0) {
            selectCancerSubTypeDialog = Dialog(FourBaseCareApp.activityFromApp)
            selectCancerSubTypeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
            selectCancerSubTypeDialog.setContentView(R.layout.dialogue_select_or_add_doctor)

            val rvDoctors: RecyclerView = selectCancerSubTypeDialog.findViewById(R.id.rvDoctors)
            val linAddNewDoctor: LinearLayout =
                selectCancerSubTypeDialog.findViewById(R.id.linAddNewDoctor)
            val ivAddLogo: ImageView = selectCancerSubTypeDialog.findViewById(R.id.ivAddLogo)
            val tvTitle: TextView = selectCancerSubTypeDialog.findViewById(R.id.tvTitle)
            linAddNewDoctor.visibility = View.GONE
            ivAddLogo.visibility = View.GONE
            tvTitle.setText(getString(R.string.select_cancer_subtype))

            rvDoctors.apply {
                layoutManager = LinearLayoutManager(activity)
                cancerSubTypeListingAdapter =
                    CancerSubTypeListingAdapter(cancerSubTypesList, this@EditProfileFragment)
                adapter = cancerSubTypeListingAdapter
                cancerSubTypeListingAdapter.submitList(cancerSubTypesList)
            }


            selectCancerSubTypeDialog.show()

        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.no_sub_type_found)
            )
        }

    }

    private fun setCGRecyclerView(list: List<CareGiverDetails>?) {

        val arrayList = list?.let { ArrayList<CareGiverDetails>(it) }

        if (arrayList != null && arrayList.size > 0) {
            showHideNoData(false)
            binding.layOutMyCG.recyclerView.apply {
                layoutManager = LinearLayoutManager(activity)
                careGiverAdapter = CareGiversAdapter(arrayList!!, this@EditProfileFragment, true)
                adapter = careGiverAdapter
                careGiverAdapter.submitList(arrayList)
            }
        } else {
            showHideNoData(true)
        }
    }

    private fun showHideNoData(shouldShowNoData: Boolean) {
        if (shouldShowNoData) {
            binding.layOutMyCG.tvNoData.visibility = View.VISIBLE
            binding.layOutMyCG.recyclerView.visibility = View.GONE
            //binding.linAppointmentsContainer.visibility = View.GONE
            //binding.ivStartSearch.visibility = View.GONE
        } else {
            binding.layOutMyCG.tvNoData.visibility = View.GONE
            binding.layOutMyCG.recyclerView.visibility = View.VISIBLE
            //binding.linAppointmentsContainer.visibility = View.VISIBLE
            //binding.ivStartSearch.visibility = View.VISIBLE
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)
    }

    private fun setupObservers() {
        profileViewModel.responseFileUploadData.observe(this, fileUploadResponseObserver)
        profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.cancerTypesListResonseData.observe(this, cancerTypesListObserver)
        profileViewModel.cancerSubTypesListResonseData.observe(this, cancerTypesSubListObserver)
        profileViewModel.careGiverResonseData.observe(this, getCareTakerObserver)
        profileViewModel.addCareCompanionResponse.observe(this, addCareTakerObserver)
        profileViewModel.deleteCGResonseData.observe(this, removeCareTakerObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val addCareTakerObserver =
        androidx.lifecycle.Observer<AddCareTakerResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.isSuccess) {
                showToast(FourBaseCareApp.activityFromApp, "Care giver added successfully!")
                binding.layOutMyCG.etCGOneName.setText("")
                binding.layOutMyCG.etMobileNumberCG1.setText("")
                binding.layOutMyCG.spCGOneRelationShip.setSelection(0)
                binding.layOutMyCG.linCGDetails.visibility = View.GONE
                getCareGiverList()
            } else {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Something went wrong while adding care giver"
                )
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val removeCareTakerObserver =
        androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            if (responseObserver.success) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Care giver removed successfully!"
                )
                getCareGiverList()
            } else
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    "Something went wrong while removing care giver/"
                )


            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val getCareTakerObserver =
        androidx.lifecycle.Observer<CareGiverResponse> { responseObserver ->
            //binding.loginModel = loginResponseData
            careGiverList = ArrayList()
            if (responseObserver != null) {
                if (responseObserver.isSuccess) {
                    showHideNoData(false)
                    //careGiverName = responseObserver.careGiverDetails.get(0).name
                    if (responseObserver.isSuccess) {
                        careGiverList.addAll(responseObserver.careGiverDetails)
                        setCGRecyclerView(careGiverList)
                        if (careGiverList.size > 1) {
                            binding.layOutMyCG.linAddCareGiver.isEnabled = false
                        } else {
                            binding.layOutMyCG.linAddCareGiver.isEnabled = true
                        }
                    }
                } else {
                    showHideNoData(true)
                }
            }

            binding.executePendingBindings()
            binding.invalidateAll()
        }

    private val cancerTypesListObserver = Observer<CancerTypesListResponse> { responseObserver ->

        cancerTypesList = ArrayList()

        if (responseObserver.success) {
            var cancerType = CancerType()
            cancerType.id = 0
            cancerType.name = "Select Primary Cancer Site"
            cancerTypesList.add(cancerType)

            cancerTypesList.addAll(responseObserver.payLoad)

            val array = arrayOfNulls<CancerType>(cancerTypesList.size)
            cancerTypesList.toArray(array)

            cancerSpinnerAdapter =
                CancerSpinerAdapter(FourBaseCareApp.activityFromApp, R.layout.raw_spinner, array)
            binding.layOutCancer.spPrmarySite.adapter = cancerSpinnerAdapter

            Log.d("cancer_stage_log", "cancer type id after data load " + cancerTypeId)

            if (cancerTypeId != 0L) {
                var itemPosition = 0
                for (cancerItem in cancerTypesList) {
                    Log.d("match_log", "cancer type " + cancerItem.name)
                    Log.d("match_log", "cancer type " + cancerItem.id)
                    if (cancerTypeId == cancerItem.id.toLong()) {
                        Log.d("match_log", "cancer type matched " + cancerItem.name)
                        itemPosition = cancerTypesList.indexOf(cancerItem)
                    }
                }
                spPrmarySite.setSelection(itemPosition)
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

    private val updateProfileObserver = Observer<LoginResponse> { responseObserver ->
        if (responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Profile updated successfully!"
            )
            var userObj = getUserObject()
            userObj.firstName = getTrimmedText(binding.layoutasicinfo.edFullName)
            userObj.phoneNumber = getTrimmedText(binding.layoutasicinfo.edPhoneNumber)
            userObj.email = getTrimmedText(binding.layoutasicinfo.edEmail)
            userObj.cancerTypeId = cancerTypeId
            userObj.cancerSubTypeId = cancerSubTypeId

            userObj.dpLink = FILE_URL

            var profile = Profile()

            profile.gender = selectedGender
            profile.aadharNumber = getTrimmedText(binding.layoutasicinfo.edAadhar)
            profile.weight = getTrimmedText(binding.layoutasicinfo.edWeight)
            profile.height = getTrimmedText(binding.layoutasicinfo.edHeight)
            profile.dateOfBirth = getTrimmedText(binding.layoutasicinfo.edDob)

            var cancerType = com.oncobuddy.app.models.pojo.login_response.CancerType()
            cancerType.id = cancerTypeId
            cancerType.name = selectedCancerType.name

            var subCancerType = com.oncobuddy.app.models.pojo.login_response.CancerType()
            subCancerType.id = cancerSubTypeId
            subCancerType.name = selectedSUbCancerType.name

            profile.cancerType = cancerType
            profile.cancerSubType = subCancerType
            profile.cancerStage = cancerStage
            userObj.profile = profile


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

    private val cancerTypesSubListObserver = Observer<CancerTypesListResponse> { responseObserver ->
        if (responseObserver.success) {
            //cancerSubTypesList = ArrayList()
            if (responseObserver.payLoad != null && responseObserver.payLoad.size > 0) {

                cancerSubTypesList = ArrayList()
                var cancerSubType = CancerType()
                cancerSubType.id = 0
                cancerSubType.name = "Select Primary Cancer Location"
                cancerSubTypesList.add(cancerSubType)
                cancerSubTypesList.addAll(responseObserver.payLoad)
                val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
                cancerSubTypesList.toArray(array)

                subCancerSpinnerAdapter = CancerSpinerAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_spinner,
                    array
                )
                binding.layOutCancer.spPrmaryLocation.adapter = subCancerSpinnerAdapter

                Log.d("cancer_stage_log", "After subtype loading data " + cancerSubTypeId)
                if (cancerSubTypeId != 0L) {
                    var itemPosition = 0
                    for (cancerSubItem in cancerSubTypesList) {
                        Log.d("match_log", "sub cancer type " + cancerSubItem.name)
                        Log.d("match_log", "sub cancer type " + cancerSubItem.id)
                        if (cancerSubTypeId == cancerSubItem.id.toLong()) {
                            Log.d("match_log", "cancer type matched")
                            itemPosition = cancerSubTypesList.indexOf(cancerSubItem)
                        }
                    }
                    spPrmaryLocation.setSelection(itemPosition)
                }
            } else {
                cancerSubTypeId = 0L
                cancerSubTypesList = ArrayList()
                var cancerSubType = CancerType()
                cancerSubType.id = 0
                cancerSubType.name = "Select Primary Cancer Location"
                cancerSubTypesList.add(cancerSubType)
                val array = arrayOfNulls<CancerType>(cancerSubTypesList.size)
                cancerSubTypesList.toArray(array)
                subCancerSpinnerAdapter = CancerSpinerAdapter(
                    FourBaseCareApp.activityFromApp,
                    R.layout.raw_spinner,
                    array
                )
                binding.layOutCancer.spPrmaryLocation.adapter = subCancerSpinnerAdapter

                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    getString(R.string.no_sub_type_found)
                )
            }
        } else {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "There was some problem adding contact"
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


    override fun onCancerTypeSelected(position: Int, item: CancerType, view: View) {

    }

    override fun onCancerSubTypeSelected(position: Int, item: CancerType, view: View) {
    }

    private fun getCancerTypesFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getCancerTypes(getUserAuthToken())
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCareGiverList() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            Timer().schedule(Constants.FUNCTION_DELAY) {
                profileViewModel.getCareTakerDetails(getUserAuthToken())
            }
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun getCancerSubTypesFromServer(cancerTypeId: String) {

        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getCSubancerTypes(getUserAuthToken(), cancerTypeId)
        } else {
            Toast.makeText(
                context,
                getString(R.string.please_check_internet_connection),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

    private fun isValidCancerInput(): Boolean {
        if (cancerTypeId == 0L) {
            showToast(FourBaseCareApp.activityFromApp, "Please select cancer type")
            return false
        }
        return true
    }

    private fun isValidInput(): Boolean {
        Log.d("sub_type", cancerSubTypeId.toString() + " " + checkIFPatient())
        if (getTrimmedText(binding.layoutasicinfo.edFullName).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_enter_name))
            return false
        } else if (getTrimmedText(binding.layoutasicinfo.edPhoneNumber).isNullOrBlank()) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_enter_mobile_number)
            )
            return false
        } else if (getTrimmedText(binding.layoutasicinfo.edEmail).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, getString(R.string.validation_enter_email))
            return false
        } else if (!CommonMethods.isValidEmail(getTrimmedText(binding.layoutasicinfo.edEmail))) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_enter_valid_email)
            )
            return false
        } else if (getTrimmedText(binding.layoutasicinfo.edPhoneNumber).toString().trim().length != 10) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_invalid_mobile_number)
            )
            return false
        } else if (!getTrimmedText(binding.layoutasicinfo.edAadhar).isNullOrBlank() && getTrimmedText(
                binding.layoutasicinfo.edAadhar
            ).length != 14
        ) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter correct Aadhar number")
            return false
        }
        /* else if(getTrimmedText(binding.etCancerType).isNullOrEmpty() && checkIFPatient()){
            showToast(FourBaseCareApp.activityFromApp,"Please select cancer type")
            return  false
        }*/
        // Check removed 05 july 2021
        /* else if(getTrimmedText(binding.etCancerSubType).isNullOrEmpty() && checkIFPatient()){
            showToast(FourBaseCareApp.activityFromApp,"Please select sub cancer type")
            return  false
        }*/
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
                            //CommonMethods.showLog("button_height", "button hidden")
                            binding.tvSave.setVisibility(View.GONE)

                        } else {
                            Timer().schedule(object : TimerTask(){
                                override fun run() {
                                    activity?.runOnUiThread {
                                        binding.tvSave.setVisibility(View.VISIBLE)
                                    }
                                }

                            }, 10)
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
        inImage?.compress(Bitmap.CompressFormat.JPEG, 70, bytes)
        Log.d("insert_img_log", "1")


        val path = MediaStore.Images.Media.insertImage(
            inContext?.contentResolver,
            inImage,
            "IMG_" + System.currentTimeMillis(),
            "abc" + System.currentTimeMillis()
        )
        Log.d("insert_img_log", "path " + path)
        return Uri.parse(path)
    }

    private fun insertImage(
        cr: ContentResolver,
        source: Bitmap?,
        title: String,
        description: String
    ): Uri? {
        Log.d("insert_img_log", "1")
        val sdf = SimpleDateFormat("MM-dd-yyyy-hh.mm.ss.SSS.a", Locale.US)
        val date = sdf.format(Date())

        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, title)
        values.put(MediaStore.Images.Media.DISPLAY_NAME, title + date)
        values.put(MediaStore.Images.Media.DESCRIPTION, description + date)
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        // Add the date meta data to ensure the image is added at the front of the gallery
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
        values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())

        var url: Uri? = null
        var stringUrl: String? = null    /* value to be returned */

        try {
            url = cr.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)

            if (source != null) {
                val imageOut = cr.openOutputStream(url!!)
                try {
                    source.compress(Bitmap.CompressFormat.JPEG, 50, imageOut)
                } finally {
                    imageOut!!.close()
                }


            } else {
                cr.delete(url!!, null, null)
                url = null
            }
            Log.d("insert_img_log", "2")
        } catch (e: Exception) {
            if (url != null) {
                cr.delete(url, null, null)
                url = null
                Log.d("insert_img_log", "3 " + e.toString())
            }
        }

        if (url != null) {
            stringUrl = url.toString()
        }

        return url
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == Constants.PICK_GALLERY_IMAGE) {
            // binding.ivProduct.setImageURI(data?.data) // handle chosen image
            //strSelectedImagePath = data?.data.toString()
            ////
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

            Log.d("insert_img_log", "0")
            //val mImageUri: Uri? = getImageUri(FourBaseCareApp.activityFromApp,imgBitmap)//insertImage(FourBaseCareApp.activityFromApp.contentResolver, imgBitmap,"title","desc")
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
                    ).into(binding.layoutasicinfo.ivProfile)

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
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
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

    private fun setSelectionColor() {
        if (SELECTED_MODULE == BASIC_INFO) {
            binding.ivBasicInfoLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvBasicInfo.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.layoutasicinfo.linBasicInfoContainer.visibility = View.VISIBLE


        } else {
            binding.ivBasicInfoLine.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.nov_line_gray
                )
            )
            binding.tvBasicInfo.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )
            )
            binding.layoutasicinfo.linBasicInfoContainer.visibility = View.GONE


        }

        if (SELECTED_MODULE == ABOUT_CANCER) {
            binding.ivAboutCancer.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvAboutCancer.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )

            binding.layOutCancer.linCancerCOntainer.visibility = View.VISIBLE
            getCancerTypesFromServer()
        } else {
            binding.ivAboutCancer.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray
                )
            )
            binding.tvAboutCancer.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )
            )
            binding.layOutCancer.linCancerCOntainer.visibility = View.GONE
        }

        if (SELECTED_MODULE == MY_CARE_GIVERS) {
            binding.ivMyCareGivers.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.tvMyCareGivers.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.reports_blue_title
                )
            )
            binding.layOutMyCG.linMyCGCOntainer.visibility = View.VISIBLE
            getCareGiverList()
        } else {
            binding.ivMyCareGivers.setBackgroundColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray
                )
            )
            binding.tvMyCareGivers.setTextColor(
                ContextCompat.getColor(
                    FourBaseCareApp.activityFromApp,
                    R.color.gray_font
                )
            )
            binding.layOutMyCG.linMyCGCOntainer.visibility = View.GONE

        }

    }

    override fun onCGSelected(position: Int, item: CareGiverDetails, view: View) {
        if (view.id == R.id.ivCardMenu) {
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_care_giver_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when (menuItem.itemId) {
                    R.id.menu_delete -> {
                        //showDeleteConfirmDialogue(item)
                        Log.d("remove_log", "id " + item.id)
                        Log.d("remove_log", "app user " + item.appUserId)
                        showDeleteCareGiverDialogue("" + item.id)
                    }
                }
                true
            }

            // Showing the popup menu
            popupMenu.show()
        }
    }

    private fun showDeleteCareGiverDialogue(addedCGId: String) {

        deleteConfirmationDialogue = Dialog(FourBaseCareApp.activityFromApp)
        deleteConfirmationDialogue.setContentView(R.layout.dialogue_delete_records)

        val lp: WindowManager.LayoutParams = WindowManager.LayoutParams()
        lp.copyFrom(deleteConfirmationDialogue.window?.getAttributes())
        lp.width = WindowManager.LayoutParams.MATCH_PARENT
        lp.height = WindowManager.LayoutParams.WRAP_CONTENT
        lp.gravity = Gravity.CENTER
        lp.windowAnimations = R.style.DialogAnimation

        val window: Window? = deleteConfirmationDialogue?.window
        window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.MATCH_PARENT
        )
        window?.setBackgroundDrawableResource(android.R.color.transparent)

        deleteConfirmationDialogue.window?.setAttributes(lp)
        deleteConfirmationDialogue.window?.setBackgroundDrawableResource(android.R.color.transparent);

        val btnDelete: TextView = deleteConfirmationDialogue.findViewById(R.id.btnDelete)
        val tvMsg: TextView = deleteConfirmationDialogue.findViewById(R.id.tvMsg)

        tvMsg.setText("Are you sure you want to delete care giver?")


        btnDelete.setOnClickListener(View.OnClickListener {
            removeCareGiver(addedCGId)
            deleteConfirmationDialogue.dismiss()
        })

        val btnCancel: TextView = deleteConfirmationDialogue.findViewById(R.id.btnCancel)
        btnCancel.setText("Cancel")

        btnCancel.setOnClickListener(View.OnClickListener {
            deleteConfirmationDialogue.dismiss()
        })

        deleteConfirmationDialogue.show()
        //showToast("SHpwing dialogue")
    }

    private fun removeCareGiver(addedCGId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.deleteCareGiver(getUserAuthToken(), addedCGId)
        }
    }

    private fun isValidNumberRepeated(): Boolean {
        if (careGiverList.isNullOrEmpty()) {
            var isFound = false
            for (careGiver in careGiverList) {
                if (binding.layOutMyCG.etMobileNumberCG1.equals(careGiver.phoneNumber)) {
                    return true
                }
            }
        }

        return false
    }

    private fun isValidCTInput(): Boolean {
        Log.d("cg_log", "0")
        if (!careGiverList.isNullOrEmpty() && careGiverList.size >= 2) {
            showToast(
                FourBaseCareApp.activityFromApp,
                FourBaseCareApp.activityFromApp.getString(R.string.validation_2_caregivers_max)
            )
            return false
        } else if (getTrimmedText(binding.layOutMyCG.etCGOneName).isNullOrBlank()) {
            showToast(FourBaseCareApp.activityFromApp, "Please enter care giver name")
            return false
        } else if (getTrimmedText(binding.layOutMyCG.etMobileNumberCG1).isNullOrBlank()) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_enter_mobile_number)
            )
            return false
        } else if (getTrimmedText(binding.layOutMyCG.etMobileNumberCG1).toString()
                .trim().length < 10
        ) {
            showToast(
                FourBaseCareApp.activityFromApp,
                getString(R.string.validation_invalid_mobile_number)
            )
            return false
        } else if (getUserObject() != null && !getUserObject().phoneNumber.isNullOrEmpty() &&
            getTrimmedText(binding.layOutMyCG.etMobileNumberCG1).equals(getUserObject().phoneNumber)
        ) {
            showToast(
                FourBaseCareApp.activityFromApp,
                "Your mobile number cannot be added as a care giver number"
            )
            return false
        } else if (binding.layOutMyCG.spCGOneRelationShip.selectedItemPosition == 0) {
            showToast(FourBaseCareApp.activityFromApp, "Please select relationship type")
            return false
        }

        return true
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            if (SELECTED_MODULE == MY_CARE_GIVERS) {
                getCareGiverList()
            }
        }
    }


}