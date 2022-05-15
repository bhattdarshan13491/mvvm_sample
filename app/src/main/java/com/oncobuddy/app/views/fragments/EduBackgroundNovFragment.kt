package com.oncobuddy.app.views.fragments

import android.app.Dialog
import android.graphics.Rect
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.AdapterView
import com.oncobuddy.app.R
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import com.oncobuddy.app.databinding.LayoutDoctorEduBgBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.ComplaintInput
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.Doctor
import com.oncobuddy.app.models.pojo.doctors.doctors_listing.DoctorsListingResponse
import com.oncobuddy.app.models.pojo.education_degrees.AddEducationInput
import com.oncobuddy.app.models.pojo.education_degrees.DegreesResponse
import com.oncobuddy.app.models.pojo.education_degrees.Education
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.views.adapters.DoctorListingForAppointmentAdapter
import com.oncobuddy.app.views.adapters.EducationAdapter


class EduBackgroundNovFragment : BaseFragment(),  EducationAdapter.Interaction{

    private lateinit var binding: LayoutDoctorEduBgBinding
    private lateinit var profileViewModel: ProfileViewModel
    private var selectedBranch = "nothing"
    private var selectedDegree = "nothing"

    val msBranches =  arrayOf("Branch","General Surgeon", "OBC", "ENT","Ortho")
    val mdBranches =  arrayOf("Branch","General Medicine","Radiation Oncology", "Pediatric Oncology")
    val mchBranches = arrayOf("Branch","Surgical Oncology", "Surgical Gastroenterology", "Head & Neck","Gynecologic Oncology")
    val dmBranches =  arrayOf("Branch","Medical Oncology", "Radiation Oncology")
    val otherBranches = arrayOf("Branch")


    private lateinit var educationAdapter: EducationAdapter
    private lateinit var educationList: ArrayList<Education>
    private lateinit var deleteConfirmationDialogue: Dialog




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
            R.layout.layout_doctor_edu_bg, container, false
        )
        Log.d("sp_branch","opened here "+Constants.IS_ACC_SETUP_MODE)
        setupClickListeners()
        setupVM()
        setupObservers()
        setupTitle()
        //setRecyclerView()
        getEducationListingFromServer()
        showHideBottomButton()

    }

    private fun getEducationListingFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callGetEducation(
                getUserAuthToken()
            )
        }
    }

    private fun setRecyclerView(list: List<Education>) {

        educationList = ArrayList()
        educationList.addAll(list)
            binding.recyclerView.apply {
                layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
                educationAdapter = EducationAdapter(educationList, this@EduBackgroundNovFragment)
                adapter = educationAdapter
                educationAdapter.submitList(educationList)
            }
    }


    private fun setupTitle() {

        if (Constants.IS_ACC_SETUP_MODE) {
            binding.layoutHeader.relTitleCOntainer.visibility = View.GONE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.VISIBLE
            binding.layoutAcSetup.tvCurrentStep.setText("Education Background")
            binding.layoutAcSetup.tvNextStep.setText("Next : Practice Information")
            Glide.with(FourBaseCareApp.activityFromApp).load(R.drawable.ic_3_of_6).into(binding.layoutAcSetup.ivStep)
        } else {
            binding.layoutHeader.tvTitle.setText("Education Background")
            binding.layoutHeader.relTitleCOntainer.visibility = View.VISIBLE
            binding.layoutAcSetup.linAcSetupHeader.visibility = View.GONE
        }
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
       // profileViewModel.loginResonseData.observe(this, updateProfileObserver)
        profileViewModel.addEducationResonseData.observe(this, addEducationObserver)
        profileViewModel.deleteEducationResonseData.observe(this, removeEducationObserver)
        profileViewModel.educationResonseData.observe(this, educationListResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val removeEducationObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Education removed successfully!"
            )
            getEducationListingFromServer()
        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while removing education")


        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val addEducationObserver = androidx.lifecycle.Observer<BaseResponse>{ responseObserver ->
        //binding.loginModel = loginResponseData
        if(responseObserver.success) {
            CommonMethods.showToast(
                FourBaseCareApp.activityFromApp,
                "Education added successfully!"
            )
            getEducationListingFromServer()
            binding.llDegree.visibility = View.GONE
            binding.llBranch.visibility = View.GONE
            binding.linAddMore.visibility = View.VISIBLE
            binding.relSaveDegree.visibility = View.GONE
        }
        else
            CommonMethods.showToast(FourBaseCareApp.activityFromApp, "Something went wrong while adding education")

        binding.executePendingBindings()
        binding.invalidateAll()
    }



    private fun setupClickListeners() {

        binding.layoutAcSetup.ivBack.setOnClickListener(View.OnClickListener {
            if(!isDoubleClick())fragmentManager?.popBackStack()
        })


        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })

        binding.ivDropDownDegree.setOnClickListener(View.OnClickListener {
            binding.spDegree.performClick()
        })

        binding.ivDropDownBranch.setOnClickListener(View.OnClickListener {
            binding.spBranch.performClick()
        })


        binding.spBranch.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("sp_branch","Nothing selected")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {


                if(position == 0){
                    selectedBranch = "nothing"
                }else{
                    if(selectedDegree.equals("MS")){
                        Log.d("sp_branch","ms_branches")
                        selectedBranch = msBranches.get(position)
                    }else if(selectedDegree.equals("MD")){
                        selectedBranch = mdBranches.get(position)
                    }else if(selectedDegree.equals("MCh")){
                        selectedBranch = mchBranches.get(position)
                    }else if(selectedDegree.equals("DM")){
                        selectedBranch = dmBranches.get(position)
                    }else{
                        selectedBranch = "nothing"
                    }
                }
            }
         }




        binding.spDegree.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                Log.d("sp_branch","Nothing selected")
            }
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                selectedBranch = "nothing"
                if(position == 0){
                    selectedDegree = "nothing"
                }else{
                    selectedDegree = resources.getStringArray(R.array.degrees_array).get(position)
                    Log.d("sp_branch","selected "+selectedDegree)
                    if(selectedDegree.equals("MS")){
                        Log.d("sp_branch","ms_branches")
                        setupBranchAdapter(msBranches)
                    }else if(selectedDegree.equals("MD")){
                        setupBranchAdapter(mdBranches)
                    }else if(selectedDegree.equals("MCh")){
                        setupBranchAdapter(mchBranches)
                    }else if(selectedDegree.equals("DM")){
                        setupBranchAdapter(dmBranches)
                    }else{
                        setupBranchAdapter(otherBranches)
                    }
                }
            }

        }



        binding.relContinue.setOnClickListener {
            decideRedirection()
        }
        binding.relSaveDegree.setOnClickListener {
            if(!isDoubleClick() && checkInterNetConnection(FourBaseCareApp.activityFromApp)){
                addEducation()
            }
        }
        binding.linAddMore.setOnClickListener(View.OnClickListener {
             if(!isDoubleClick()){
                 binding.llDegree.visibility = View.VISIBLE
                 binding.llBranch.visibility = View.VISIBLE
                 binding.linAddMore.visibility = View.GONE
                 binding.relSaveDegree.visibility = View.VISIBLE
             }
        })
    }

    private fun setupBranchAdapter(array: Array<String>) {
        val spinnerArrayAdapter: ArrayAdapter<String> =
            ArrayAdapter<String>(context!!, android.R.layout.simple_spinner_item, array)
        spinnerArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item) // The drop down view
        binding.spBranch.setAdapter(spinnerArrayAdapter)
    }

    override fun onResume() {
        super.onResume()
//        doctorSetupChangeListener!!.doctorSetupChange(this.javaClass.simpleName)
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val educationListResponseObserver = androidx.lifecycle.Observer<DegreesResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            /*if(!educationList.isNullOrEmpty()){
                educationList.clear()
                educationAdapter.notifyDataSetChanged()
            }*/
            setRecyclerView(responseObserver.payLoad)
            if(responseObserver.payLoad != null && responseObserver.payLoad.size > 0){
                binding.llDegree.visibility = View.GONE
                binding.llBranch.visibility = View.GONE
                binding.linAddMore.visibility = View.VISIBLE
                binding.relSaveDegree.visibility = View.GONE
            }


            Log.d("education","server size "+responseObserver.payLoad.size)
            Log.d("education","list size "+educationList.size)
        }

        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(context!!, message)
    }

    private fun decideRedirection() {
        if (Constants.IS_ACC_SETUP_MODE) {
            changeProfileCOmpletionLevel(4)
            CommonMethods.addNextFragment(
                FourBaseCareApp.activityFromApp,
                DocEditEstablishmentFragment(), this, false
            )
        } else {
            fragmentManager?.popBackStack()
        }
    }

    override fun onDegreeSelected(position: Int, item: Education, view: View) {
        if(view.id == R.id.ivCardMenu){
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_care_giver_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_delete -> {
                        //showDeleteConfirmDialogue(item)
                        showDeleteCareGiverDialogue(""+item.id)
                    }
                }
                true
            }

            // Showing the popup menu
            popupMenu.show()
        }
    }

    private fun showDeleteCareGiverDialogue(eduId: String) {

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

        tvMsg.setText("Are you sure you want to delete education?")


        btnDelete.setOnClickListener(View.OnClickListener {
            removeEducation(eduId)
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

    private fun removeEducation(eduId: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.callDeleteEducation(getUserAuthToken(),eduId)
        }
    }

    private fun addEducation() {
        Log.d("match_log","degree "+selectedDegree)
        Log.d("match_log","branch "+selectedBranch)
        if(selectedDegree.equals("nothing")){
            showToast(FourBaseCareApp.activityFromApp, "Please choose your degree")
        }else if(selectedBranch.equals("nothing") && !selectedDegree.equals("MBBS") && !selectedDegree.equals("MDS") && !selectedDegree.equals("Other")){
            showToast(FourBaseCareApp.activityFromApp, "Please choose your branch")
        }
        else if(::educationList.isInitialized && !educationList.isNullOrEmpty() && foundEducation()){
            showToast(FourBaseCareApp.activityFromApp, "This education has been added already")
        } else{
            var addEducationInput = AddEducationInput()
            addEducationInput.degree = selectedDegree
            if(!selectedBranch.equals("nothing"))addEducationInput.brench = selectedBranch
            profileViewModel.callAddEducation(getUserObject().authToken, addEducationInput)
        }

    }

    private fun foundEducation() : Boolean {
        var foundDegree = false
        var foundBranch = false
        for (educationObj in educationList) {
            Log.d("match_log", "education degree " + educationObj.degree)
            Log.d("match_log", "education branch " + educationObj.branch)
            if (educationObj.degree != null && educationObj.degree.equals(selectedDegree)) {
                    foundDegree = true
                    Log.d("match_log", "found degree " + educationObj.degree)
            }

            //ms,md,mch,dm
            if(selectedDegree.equals("MBBS") || selectedDegree.equals("MDS") || selectedDegree.equals("Other")){
                foundBranch = true
            }else{
                if (!selectedBranch.equals("nothing")) {
                    if (educationObj.branch != null && educationObj.branch.equals(selectedBranch)) {
                        Log.d("match_log", "found branch " + educationObj.branch)
                        foundBranch = true
                    }
                }
            }
            /*if (!selectedBranch.equals("nothing")) {
                if (educationObj.branch != null && educationObj.branch.equals(selectedBranch)) {
                    Log.d("match_log", "found degree " + educationObj.branch)
                    foundBranch = true
                }
            } else {
                foundBranch = false
            }*/
        }

        return foundDegree && foundBranch
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
                            binding.relContinue.visibility = View.GONE
                        CommonMethods.showLog("button_height", "button hidden")
                        //binding.tvEditProfile.setVisibility(View.GONE)

                    } else {
                        //ok now we know the keyboard is down...
                        if(Constants.IS_ACC_SETUP_MODE){
                            binding.relContinue.visibility = View.VISIBLE
                        }else{
                            binding.relContinue.visibility = View.GONE
                        }
                            CommonMethods.showLog("button_height", "button visible")
                        // binding.tvEditProfile.setVisibility(View.VISIBLE)
                    }
                }
            })
    }

}