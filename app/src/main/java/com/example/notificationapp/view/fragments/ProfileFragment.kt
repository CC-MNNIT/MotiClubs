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
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.example.notificationapp.Constants
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.FragmentProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream

class ProfileFragment : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private var user: UserResponse? = null

    private lateinit var binding: FragmentProfileBinding

    private var launcher = registerForActivityResult<Void?, Uri>(Contract(), ActivityResultCallback { result ->
        Log.d(TAG, "encodedImage")
        if (result == null) {
            return@ActivityResultCallback
        }
        try {
            val imageStream = requireContext().contentResolver.openInputStream(result)
            val selectedImage = BitmapFactory.decodeStream(imageStream)
            binding.profilePic.setImageBitmap(selectedImage)
            val encodedImage = encodeImage(selectedImage)
            Log.d(TAG, encodedImage)
            updateProfilePicture(encodedImage)
        } catch (ex: Exception) {
            ex.printStackTrace()
        }
    })

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root = binding.root
        getUserData()
        setListeners()
        return root
    }

    private fun setListeners() {
        binding.editImg.setOnClickListener { launcher.launch(null) }
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
        binding.emailTv.text = user!!.email
        binding.tvName.text = user!!.name
        binding.nameTv1.text = user!!.name
        binding.mobileTv.text = user!!.phoneNumber
        binding.tvRegNo.text = user!!.registrationNumber
        binding.yearTv.text = user!!.graduationYear
        binding.courseTv.text = user!!.course

        if (user!!.avatar.isNotEmpty()) {
            val bm = decodeImage(user!!.avatar)
            binding.profilePic.setImageBitmap(bm)
        }
    }
}