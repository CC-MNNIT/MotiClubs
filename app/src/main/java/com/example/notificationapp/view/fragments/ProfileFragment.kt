package com.example.notificationapp.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.example.notificationapp.R
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private var user: UserResponse? = null

    private lateinit var mEmailTV: TextView
    private lateinit var mYearTV: TextView
    private lateinit var mRegNoTV: TextView
    private lateinit var mNameTV1: TextView
    private lateinit var mNameTV: TextView
    private lateinit var mCourseTV: TextView
    private lateinit var mMobileTV: TextView

    private lateinit var mEditImg: ImageView
    private lateinit var mProfilePic: ImageView

    private var launcher = registerForActivityResult<Void?, Uri>(Contract(), ActivityResultCallback { result ->
        Log.d(TAG, "encodedImage")
        if (result == null) {
            return@ActivityResultCallback
        }
        try {
            val imageStream = requireContext().contentResolver.openInputStream(result)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            mProfilePic.setImageBitmap(selectedImage)
            val encodedImage = encodeImage(selectedImage)
            Log.d(TAG, encodedImage)
            updateProfilePicture(encodedImage)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        setReferences(root)
        getUserData()
        setListeners()
        return root
    }

    private fun setListeners() {
        mEditImg.setOnClickListener { launcher.launch(null) }
    }

    private inner class Contract : ActivityResultContract<Void?, Uri?>() {
        override fun createIntent(context: Context, input: Void?): Intent {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            return intent
        }

        override fun parseResult(resultCode: Int, intent: Intent?): Uri? {
            return if (resultCode != Activity.RESULT_OK) null else intent?.data
        }
    }

    private fun updateProfilePicture(encodedImage: String) {
        val preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
    }

    private fun encodeImage(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val b = baos.toByteArray()
        return Base64.encodeToString(b, Base64.DEFAULT)
    }

    private fun decodeImage(imageString: String?): Bitmap {
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
    }

    private fun getUserData() {
        val preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject().getUserData(preferences.getString(Constants.TOKEN, ""))
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(call: Call<UserResponse?>, response: Response<UserResponse?>) {
                    if (response.isSuccessful && response.body() != null) {
                        user = response.body()
                        setProfileValues()
                    }
                }

                override fun onFailure(call: Call<UserResponse?>, t: Throwable) {}
            })
    }

    private fun setProfileValues() {
        if (user == null) return
        mEmailTV.text = user!!.email
        mNameTV.text = user!!.name
        mNameTV1.text = user!!.name
        mMobileTV.text = user!!.phoneNumber
        mRegNoTV.text = user!!.registrationNumber
        mYearTV.text = user!!.graduationYear
        mCourseTV.text = user!!.course

        if (user!!.avatar.isNotEmpty()) {
            val bm = decodeImage(user!!.avatar)
            mProfilePic.setImageBitmap(bm)
        }
    }

    private fun setReferences(root: View) {
        mEmailTV = root.findViewById(R.id.email_tv)
        mNameTV1 = root.findViewById(R.id.name_tv1)
        mNameTV = root.findViewById(R.id.tv_name)
        mMobileTV = root.findViewById(R.id.mobile_tv)
        mRegNoTV = root.findViewById(R.id.tv_regNo)
        mYearTV = root.findViewById(R.id.year_tv)
        mCourseTV = root.findViewById(R.id.course_tv)
        mEditImg = root.findViewById(R.id.edit_img)
        mProfilePic = root.findViewById(R.id.profilepic)
    }
}