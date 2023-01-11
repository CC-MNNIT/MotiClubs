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
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.api.API
import com.example.notificationapp.databinding.FragmentProfileBinding
import com.example.notificationapp.view.activities.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import com.squareup.picasso.NetworkPolicy
import com.squareup.picasso.Picasso
import java.io.ByteArrayOutputStream

class ProfileFragment(private val mainActivity: MainActivity) : Fragment() {

    companion object {
        private const val TAG = "ProfileFragment"
    }

    private lateinit var binding: FragmentProfileBinding
    private var launcher =
        registerForActivityResult<Void?, Uri>(Contract(), ActivityResultCallback { result ->
            if (result == null) {
                return@ActivityResultCallback
            }
            try {
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
        setProfileValues()
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
        val storageRef = Firebase.storage.reference
        val profilePicRef =
            storageRef.child("profile_images").child(FirebaseAuth.getInstance().currentUser!!.uid)
                .child(FirebaseAuth.getInstance().currentUser!!.uid)

        val bitmap = compressBitmap(imageUri)
        if (bitmap == null) {
            Toast.makeText(requireContext(), "Internal bitmap error", Toast.LENGTH_SHORT).show()
            return
        }
        val boas = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, boas)
        profilePicRef.putBytes(boas.toByteArray()).continueWithTask { task ->
            if (!task.isSuccessful) {
                task.exception?.let {
                    Log.d(TAG, "not success$it")
                    throw it
                }
            }
            profilePicRef.downloadUrl
        }.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Log.d(TAG, "updateProfilePicture: got url")
                val downloadUri = task.result
                API.updateProfilePic(UserInstance.getAuthToken(requireContext()), downloadUri.toString(), {
                    Toast.makeText(requireContext(), "Profile pic updated", Toast.LENGTH_SHORT).show()
                    Log.d(TAG, "saved on mongodb")
                    UserInstance.setAvatar(it.avatar)
                    setProfileValues()
                }) { Toast.makeText(requireContext(), "$it: Could not update profile pic", Toast.LENGTH_SHORT).show() }
            } else {
                Toast.makeText(requireContext(), "DB: Could not update profile pic", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setProfileValues() {
        binding.emailTv.text = UserInstance.getEmail()
        binding.tvName.text = UserInstance.getName()
        binding.nameTv1.text = UserInstance.getName()
        binding.mobileTv.text = UserInstance.getPhoneNumber()
        binding.tvRegNo.text = UserInstance.getRegNo()
        binding.yearTv.text = UserInstance.getGradYear()
        binding.courseTv.text = UserInstance.getCourse()

        val avatar = UserInstance.getAvatar()
        if (avatar.isEmpty()) return

        Picasso.get().load(avatar).networkPolicy(NetworkPolicy.OFFLINE).into(binding.profilePic, object : com.squareup.picasso.Callback {
            override fun onSuccess() = mainActivity.setValues()

            override fun onError(e: java.lang.Exception?) {
                Picasso.get().load(avatar).into(binding.profilePic)
                mainActivity.setValues()
            }
        })
    }

    private fun compressBitmap(uri: Uri): Bitmap? {
        val options = BitmapFactory.Options()
        options.inJustDecodeBounds = true
        val ins = requireContext().contentResolver.openInputStream(uri)
        BitmapFactory.decodeStream(ins, null, options)
        ins?.close()

        var scale = 1
        while (options.outWidth / scale / 2 >= 200 && options.outHeight / scale / 2 >= 200) {
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