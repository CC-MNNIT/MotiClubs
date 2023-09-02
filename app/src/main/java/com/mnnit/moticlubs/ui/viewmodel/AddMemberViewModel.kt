package com.mnnit.moticlubs.ui.viewmodel

import android.app.Application
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mnnit.moticlubs.domain.model.Channel
import com.mnnit.moticlubs.domain.model.Club
import com.mnnit.moticlubs.domain.model.Member
import com.mnnit.moticlubs.domain.model.User
import com.mnnit.moticlubs.domain.repository.Repository
import com.mnnit.moticlubs.domain.use_case.MemberUseCases
import com.mnnit.moticlubs.domain.use_case.UserUseCases
import com.mnnit.moticlubs.domain.util.NavigationArgs
import com.mnnit.moticlubs.domain.util.Resource
import com.mnnit.moticlubs.domain.util.getLongArg
import com.mnnit.moticlubs.domain.util.isTrimmedNotEmpty
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddMemberViewModel @Inject constructor(
    private val application: Application,
    private val repository: Repository,
    private val memberUseCases: MemberUseCases,
    private val userUseCases: UserUseCases,
    savedStateHandle: SavedStateHandle,
) : ViewModel() {

    val channelId by mutableLongStateOf(savedStateHandle.getLongArg(NavigationArgs.CHANNEL_ARG))

    var channelModel by mutableStateOf(Channel())
    var clubModel by mutableStateOf(Club())

    val courseList = listOf("B.Tech.", "M.Tech.", "M.Sc.", "MBA", "MCA", "Ph.D.")
    val branchMap = mapOf(
        Pair(
            "B.Tech.", listOf(
                "Biotechnology",
                "Civil Engineering",
                "Electrical Engineering",
                "Mechanical Engineering",
                "Computer Science and Engineering",
                "Electronics and Communication Engineering",
                "Production and Industrial Engineering",
                "Information Technology",
                "Chemical Engineering",
            )
        ),
        Pair(
            "M.Tech.", listOf(
                "Applied Mechanics",
                "Biomedical Engineering",
                "Biotechnology",
                "Computer Aided Design and Manufacturing",
                "Chemical Engineering (M.Tech)",
                "Communication Systems",
                "Computer Science and Engineering (M.Tech.)",
                "Design Engineering",
                "Computer Science & Engineering in Artificial Intelligence & Data Science",
                "Control & Instrumentation",
                "Environmental Geotechnology",
                "Digital Systems",
                "Engineering Mechanics and Design",
                "Civil Engineering (Environmental Engineering)",
                "Fluids Engineering",
                "Geoinformatics",
                "Civil Engineering (Geotechnical Engineering)",
                "Information Security",
                "Material Science and Engineering",
                "Product Design and Development",
                "Power Electronics and Drives",
                "Production Engineering",
                "Power System",
                "Mechanical Engineering (Computer Aided Design and Manufacturing - Part Time)",
                "Electrical Engineering (Control And Instrumentation - Part Time)",
                "Electronics Engineering (Digital System - Part Time)",
                "Civil Engineering (Environmental Engineering Part Time)",
                "Power Electronics and A.S.I.C. Design (Part Time)",
                "Production Engineering (Part Time)",
                "Civil (Structural) Engineering (Part Time)",
                "Software Engineering (Part Time)",
                "Signal Processing",
                "Structural Engineering",
                "Software Engineering",
                "Thermal Engineering",
                "Transportation Engineering",
                "Microelectronics and VLSI Design",
            )
        ),
        Pair("MCA", listOf("Master of Computer Application")),
        Pair("MBA", listOf("Master of Business Administration")),
        Pair("M.Sc.", listOf("Mathematics And Scientific Computing")),
        Pair(
            "Ph.D.", listOf(
                "Doctor of Philosophy - Applied Mechanics Department",
                "Doctor of Philosophy - Biotechnology Department",
                "Doctor of Philosophy - Civil Engineering Department",
                "Doctor of Philosophy - Chemistry Department",
                "Doctor of Philosophy - Chemical",
                "Doctor of Philosophy - Computer Science and Engineering Department",
                "Doctor of Philosophy - Chemistry Department",
                "Doctor of Philosophy - Electrical Engineering Department",
                "Doctor of Philosophy - Electronics and Communication Engineering Department",
                "Doctor Of Philosophy - GIS and Remote Sensing",
                "Doctor of Philosophy - Humanities and Social Sciences",
                "Doctor of Philosophy - Mathematics Department",
                "Doctor of Philosophy - Mechanical Engineering Department",
                "Doctor of Philosophy - Management",
                "Ph.D. (Physics Department)",
            )
        ),
    )

    val selectedUserMap = mutableStateMapOf<Long, User>()
    val searchUserList = mutableStateListOf<User>()
    private val allUserList = mutableStateListOf<User>()

    private val memberList = mutableStateListOf<Member>()

    val searchName = mutableStateOf("")
    val searchRegNo = mutableStateOf("")
    val searchCourse = mutableStateOf("")
    val searchBranch = mutableStateOf("")

    var isFetching by mutableStateOf(false)
    var showSelectedMemberDialog by mutableStateOf(false)
    var showProgressDialog by mutableStateOf(false)
    var courseDropDownExpanded by mutableStateOf(false)
    var branchDropDownExpanded by mutableStateOf(false)

    private var getMemberJob: Job? = null
    private var addMemberJob: Job? = null
    private var getAllUserJob: Job? = null

    private fun getAllUsers() {
        getAllUserJob?.cancel()
        allUserList.clear()
        isFetching = true
        getAllUserJob = userUseCases.getAllUsers().onEach { resource ->
            when (resource) {
                is Resource.Loading -> isFetching = true
                is Resource.Success -> {
                    allUserList.clear()
                    selectedUserMap.clear()

                    allUserList.addAll(resource.data.filter { user ->
                        !memberList.any { member -> user.userId == member.userId }
                    })
                    isFetching = false
                }

                is Resource.Error -> {
                    isFetching = false
                    Toast.makeText(application, "${resource.errCode} ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    fun filterSearch() {
        searchUserList.clear()

        if (searchName.value.isTrimmedNotEmpty()
            || searchRegNo.value.isTrimmedNotEmpty()
            || searchCourse.value.isTrimmedNotEmpty()
            || searchBranch.value.isTrimmedNotEmpty()
        ) {
            searchUserList.addAll(
                allUserList
                    .filter { user ->
                        searchName.value.split(",").any {
                            user.name.lowercase().contains(it.trim().lowercase())
                        }
                    }
                    .filter { user ->
                        searchRegNo.value.split(",").any {
                            user.regNo.lowercase().contains(it.trim().lowercase())
                        }
                    }
                    .filter { user -> user.course.lowercase().contains(searchCourse.value.lowercase()) }
                    .filter { user -> user.branch.lowercase().contains(searchBranch.value.lowercase()) }
            )
        }
    }

    fun resetSearchFields() {
        searchName.value = ""
        searchRegNo.value = ""
        searchCourse.value = ""
        searchBranch.value = ""
    }

    fun addMembers(onBackPressed: () -> Unit) {
        addMemberJob?.cancel()
        addMemberJob = memberUseCases.addMembers(
            channelModel.clubId,
            channelId,
            selectedUserMap.map { it.key }
        ).onEach { resource ->
            when (resource) {
                is Resource.Loading -> showProgressDialog = true
                is Resource.Success -> {
                    showProgressDialog = false
                    onBackPressed()
                }

                is Resource.Error -> {
                    Toast.makeText(application, "${resource.errCode} ${resource.errMsg}", Toast.LENGTH_SHORT).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    private fun getModels() {
        viewModelScope.launch {
            channelModel = repository.getChannel(channelId)
            clubModel = repository.getClub(channelModel.clubId)

            getMembers()
            getAllUsers()
        }
    }

    private fun getMembers() {
        if (channelModel.private == 0) {
            memberList.clear()
            return
        }

        getMemberJob?.cancel()
        getMemberJob = memberUseCases.getMembers(channelId).onEach { resource ->
            when (resource) {
                is Resource.Loading -> {
                    resource.data?.let { list ->
                        memberList.clear()
                        memberList.addAll(list)
                    }
                }

                is Resource.Success -> {
                    memberList.clear()
                    memberList.addAll(resource.data)
                }

                is Resource.Error -> {
                    Toast.makeText(application, "${resource.errCode}: ${resource.errMsg}", Toast.LENGTH_LONG).show()
                }
            }
        }.launchIn(viewModelScope)
    }

    init {
        getModels()
    }
}
