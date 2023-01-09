package com.example.notificationapp.view.fragments

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultCallback
import androidx.activity.result.contract.ActivityResultContract
import androidx.fragment.app.Fragment
import com.example.notificationapp.Constants
import com.example.notificationapp.data.network.ProfilePicResponse
import com.example.notificationapp.data.network.UserResponse
import com.example.notificationapp.data.network.api.RetrofitAccessObject
import com.example.notificationapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
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
    private lateinit var mAuth: FirebaseAuth
    private var launcher =
        registerForActivityResult<Void?, Uri>(Contract(), ActivityResultCallback { result ->
            if (result == null) {
                return@ActivityResultCallback
            }
            try {
//                val selectedImage = compressAsyncBitmap(result)
//                binding.profilePic.setImageBitmap(selectedImage)
                Log.d(TAG, "calling updateUserProfile")
                updateProfilePicture(result)
            } catch (ex: Exception) {
                ex.printStackTrace()
            }
        })

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
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

    private fun updateProfilePicture(imageUri: Uri) {
        val preferences = requireContext().getSharedPreferences(
            Constants.SHARED_PREFERENCE,
            Context.MODE_PRIVATE
        )

        Log.d(TAG, "updateUserProfile function started")

        var storage = Firebase.storage
        val storageRef = storage.reference
        val profilePicRef =
            storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

        var uploadTask = profilePicRef.putFile(imageUri)

        val urlTask = uploadTask.continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d(TAG, "not success$it")
                    throw it
                }
            }
            profilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val downloadUri = task.result
                Log.d(TAG, downloadUri.toString())
                Log.d(TAG, "started retrofit .........")

                RetrofitAccessObject.getRetrofitAccessObject().updateProfilePic(
                    preferences.getString(Constants.TOKEN, ""),
                    ProfilePicResponse(downloadUri.toString())
                )
                    .enqueue(object : Callback<ProfilePicResponse?> {
                        override fun onResponse(
                            call: Call<ProfilePicResponse?>,
                            response: Response<ProfilePicResponse?>
                        ) {
                            if (response.isSuccessful && response.body() != null) {
                                user!!.avatar = response.body()!!.avatar
                                Toast.makeText(context, "Saved on mongoDB", Toast.LENGTH_SHORT).show()
                                Log.d(TAG, "saved on mongodb")
                                setProfileValues()
                            }else{
                                Log.d(TAG, response.code().toString())
                            }
                        }
                        override fun onFailure(call: Call<ProfilePicResponse?>, t: Throwable) {
                            Log.d(TAG, "retrofit failed")
                            Toast.makeText(context, "failed retrofit", Toast.LENGTH_SHORT).show()
                        }
                    }
                    )
            } else {
                Toast.makeText(context, "Saved but still error", Toast.LENGTH_SHORT).show()
                Log.d(TAG, "saved but still error")
            }
        }
    }

//    private fun encodeImage(bm: Bitmap?): String {
//        val baos = ByteArrayOutputStream()
//        bm?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
//        val b = baos.toByteArray()
//        return Base64.encodeToString(b, Base64.DEFAULT)
//    }

//    private fun decodeImage(imageString: String?): Bitmap {
//        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
//        return BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
//    }

    private fun getUserData() {
        val preferences =
            requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE)
        RetrofitAccessObject.getRetrofitAccessObject()
            .getUserData(preferences.getString(Constants.TOKEN, ""))
            .enqueue(object : Callback<UserResponse?> {
                override fun onResponse(
                    call: Call<UserResponse?>,
                    response: Response<UserResponse?>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        user = response.body()!!
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

//        if (user!!.avatar?.isNotEmpty() == true) {
//            //
//        }
    }

    //    fun compressBitmap(uri : Uri): Bitmap? = CoroutineScope.launch { compressAsyncBitmap(uri) }
    private fun compressAsyncBitmap(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val ins = requireContext().contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(ins, null, options)
        ins?.close()

        var scale = 1
        while (options.outWidth / scale / 2 >= 100 && options.outHeight / scale / 2 >= 100) {
            scale *= 2
        }

        val finalOptions = BitmapFactory.Options()
        finalOptions.inSampleSize = scale

        val inputStream = requireContext().contentResolver.openInputStream(uri)
        val out = BitmapFactory.decodeStream(inputStream, null, finalOptions)
        inputStream?.close()
        return out
    }
}