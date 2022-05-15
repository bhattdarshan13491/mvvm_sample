package com.oncobuddy.app.views.fragments


import android.app.Dialog
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.widget.PopupMenu
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.oncobuddy.app.FourBaseCareApp
import com.oncobuddy.app.R
import com.oncobuddy.app.databinding.LayoutEstDetailsBinding
import com.oncobuddy.app.models.injectors.ProfileInjection
import com.oncobuddy.app.models.pojo.BaseResponse
import com.oncobuddy.app.models.pojo.doctor_locations.DOctorLOcationListResponse
import com.oncobuddy.app.models.pojo.doctor_locations.Establishment
import com.oncobuddy.app.models.pojo.education_degrees.Education
import com.oncobuddy.app.utils.CommonMethods
import com.oncobuddy.app.utils.Constants
import com.oncobuddy.app.utils.base_classes.BaseFragment
import com.oncobuddy.app.view_models.ProfileViewModel
import com.oncobuddy.app.views.adapters.EducationAdapter
import com.oncobuddy.app.views.adapters.EstablishmentsAdapter

class DoctorEstablishmentListingFragment : BaseFragment(), EstablishmentsAdapter.Interaction {

    private lateinit var binding: LayoutEstDetailsBinding
    private lateinit var profileViewModel: ProfileViewModel

    private lateinit var myEstablismentAdapter: EstablishmentsAdapter
    private var myEstablishmentList = ArrayList<Establishment>()

    private lateinit var visitingEstablismentAdapter: EstablishmentsAdapter
    private var visitingEstablismentList = ArrayList<Establishment>()

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
            R.layout.layout_est_details, container, false
        )
        binding.layoutHeader.tvTitle.setText("Establishment Details")
        setupClickListener()
        setupVM()
        setupObservers()
        getEstablishmentsFromServer()

        binding.relContinue.visibility = View.GONE
    }

    private fun getEstablishmentsFromServer() {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.getEstablishments(
                getUserAuthToken()
            )
        }
    }

    private fun setupClickListener() {
        binding.layoutHeader.ivBack.setOnClickListener(View.OnClickListener {
            fragmentManager?.popBackStack()
        })



        binding.linAddClinic.setOnClickListener(View.OnClickListener {
            if (!isDoubleClick()) {
                CommonMethods.addNextFragment(
                    FourBaseCareApp.activityFromApp,
                    DocEditEstablishmentFragment(), this, false
                )
            }
        })
    }

    private fun openEditFragment(establishment: Establishment?) {
        var b = Bundle()
        b.putParcelable("establishment", establishment)
        var doctorEstablishmentFragment = DocEditEstablishmentFragment()
        doctorEstablishmentFragment.arguments = b
        CommonMethods.addNextFragment(
            FourBaseCareApp.activityFromApp,
            doctorEstablishmentFragment, this, false
        )
    }

    private fun setupVM() {
        profileViewModel = ViewModelProvider(
            this,
            ProfileInjection.provideViewModelFactory()
        ).get(ProfileViewModel::class.java)

    }
    private fun setupObservers() {
        profileViewModel.deleteLocationRsonseData.observe(this, deleteLocationResponseObserver)
        profileViewModel.addLocationRsonseData.observe(this, addLocationResponseObserver)
        profileViewModel.locationListRsonseData.observe(this, locationResponseObserver)
        profileViewModel.isViewLoading.observe(this, isViewLoadingObserver)
        profileViewModel.onMessageError.observe(this, errorMessageObserver)
    }

    private val addLocationResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
        if (responseObserver.success) {
            if(responseObserver.success) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    ""+responseObserver.message
                )
                getEstablishmentsFromServer()
            }
            else
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)

        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val deleteLocationResponseObserver = androidx.lifecycle.Observer<BaseResponse> { responseObserver ->
        if (responseObserver.success) {
            if(responseObserver.success) {
                CommonMethods.showToast(
                    FourBaseCareApp.activityFromApp,
                    ""+responseObserver.message
                )
                getEstablishmentsFromServer()
            }
            else
                CommonMethods.showToast(FourBaseCareApp.activityFromApp, ""+responseObserver.message)

        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val locationResponseObserver = androidx.lifecycle.Observer<DOctorLOcationListResponse> { responseObserver ->
        if (responseObserver.isSuccess) {
            setupRecyclerView(responseObserver.payLoad)

        }
        binding.executePendingBindings()
        binding.invalidateAll()
    }

    private val isViewLoadingObserver = androidx.lifecycle.Observer<Boolean>{isLoading ->
        Log.d("list_log","is loading "+isLoading)
        if(isLoading) showHideProgress(true,binding.layoutProgress.frProgress)
        else showHideProgress(false,binding.layoutProgress.frProgress)

    }

    private val errorMessageObserver = androidx.lifecycle.Observer<String>{message ->
        CommonMethods.showToast(FourBaseCareApp.activityFromApp, message)
    }

    private fun setRecyclerView() {

        myEstablishmentList = ArrayList()
        binding.rvOwnCLinic.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            myEstablismentAdapter = EstablishmentsAdapter(myEstablishmentList, this@DoctorEstablishmentListingFragment)
            adapter = myEstablismentAdapter
        }

        visitingEstablismentList = ArrayList()
        binding.rvVisitingCLinic.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            visitingEstablismentAdapter = EstablishmentsAdapter(visitingEstablismentList, this@DoctorEstablishmentListingFragment)
            adapter = visitingEstablismentAdapter
        }
    }

    private fun setupRecyclerView(list: List<Establishment>) {

        myEstablishmentList = ArrayList()
        visitingEstablismentList = ArrayList()


        if(!myEstablishmentList.isNullOrEmpty()){
            myEstablishmentList.clear()
            myEstablismentAdapter.notifyDataSetChanged()
        }
        if(!visitingEstablismentList.isNullOrEmpty()){
            visitingEstablismentList.clear()
            visitingEstablismentAdapter.notifyDataSetChanged()
        }
        for(establishment in list){
            if(establishment.doctorConsultationLocationType.equals(Constants.OWN_CLINIC)){
                myEstablishmentList.add(establishment)
            }else{
                visitingEstablismentList.add(establishment)
            }
        }
        if(!myEstablishmentList.isNullOrEmpty()) binding.tvOwnClinic.visibility = View.VISIBLE else binding.tvOwnClinic.visibility = View.GONE
        if(!visitingEstablismentList.isNullOrEmpty()) binding.tvVisitOrg.visibility = View.VISIBLE else binding.tvVisitOrg.visibility = View.GONE

        Log.d("log_size","size "+visitingEstablismentList.size)
        binding.rvOwnCLinic.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            myEstablismentAdapter = EstablishmentsAdapter(myEstablishmentList, this@DoctorEstablishmentListingFragment)
            adapter = myEstablismentAdapter
            myEstablismentAdapter.submitList(myEstablishmentList)
        }

        binding.rvVisitingCLinic.apply {
            layoutManager = LinearLayoutManager(FourBaseCareApp.activityFromApp)
            visitingEstablismentAdapter = EstablishmentsAdapter(visitingEstablismentList, this@DoctorEstablishmentListingFragment)
            adapter = visitingEstablismentAdapter
            visitingEstablismentAdapter.submitList(visitingEstablismentList)
        }
    }

    override fun onEstablishmentSelected(position: Int, item: Establishment, view: View) {
        if(view.id == R.id.ivMenu){
            val popupMenu = PopupMenu(FourBaseCareApp.activityFromApp, view)

            // Inflating popup menu from popup_menu.xml file

            // Inflating popup menu from popup_menu.xml file
            popupMenu.menuInflater.inflate(R.menu.menu_discussion_options, popupMenu.menu)
            popupMenu.setOnMenuItemClickListener { menuItem -> // Toast message on menu item clicked

                when(menuItem.itemId){
                    R.id.menu_edit ->{
                        popupMenu.dismiss()
                        openEditFragment(item)

                    }
                    R.id.menu_delete -> {
                        //showDeleteConfirmDialogue(item)
                        showDeleteConfirmationDialogue(""+item.id)
                        popupMenu.dismiss()
                    }
                }
                true
            }

            // Showing the popup menu
            popupMenu.show()
        }
    }


    private fun showDeleteConfirmationDialogue(id: String) {

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

        tvMsg.setText("Are you sure you want to delete establishment?")


        btnDelete.setOnClickListener(View.OnClickListener {
            removeEstablishment(id)
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

    private fun removeEstablishment(id: String) {
        if (checkInterNetConnection(FourBaseCareApp.activityFromApp)) {
            profileViewModel.deleteEstablishment(getUserAuthToken(),id)
        }
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if(Constants.IS_LIST_UPDATED){
            getEstablishmentsFromServer()
            Constants.IS_LIST_UPDATED = false
        }
    }


}