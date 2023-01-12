package com.example.notificationapp.view.activities

import android.content.Context
import android.content.Intent
import android.content.res.ColorStateList
import android.os.Bundle
import android.text.Editable
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import com.example.notificationapp.R
import com.example.notificationapp.api.API
import com.example.notificationapp.app.Constants
import com.example.notificationapp.app.UserInstance
import com.example.notificationapp.app.getMkdFormatter
import com.example.notificationapp.databinding.ActivityCreatePostBinding
import io.noties.markwon.Markwon
import io.noties.markwon.editor.MarkwonEditor
import io.noties.markwon.editor.MarkwonEditorTextWatcher

class CreatePostActivity : AppCompatActivity() {

    private lateinit var mClubID: String
    private lateinit var binding: ActivityCreatePostBinding

    private lateinit var mkdEditor: MarkwonEditor
    private lateinit var mkd: Markwon

    private var mEditMode = false
    private var mPostID = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreatePostBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setValues()
        setListener()
    }

    private fun setListener() {
        binding.sendPost.setOnClickListener {
            val message: String = binding.etMessage.text?.toString() ?: ""
            if (message.isEmpty()) {
                Toast.makeText(this, "Can't post empty message", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (mEditMode) {
                if (mPostID.isEmpty()) {
                    Toast.makeText(this, "Error: Post ID empty", Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }

                API.updatePost(UserInstance.getAuthToken(this), mPostID, message, {
                    Toast.makeText(this, "Post Updated", Toast.LENGTH_SHORT).show()
                    startActivity(
                        Intent(this, MainActivity::class.java).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                    )
                    finish()
                }) { Toast.makeText(this, "$it: Unable to post", Toast.LENGTH_SHORT).show() }
            } else {
                API.sendPost(UserInstance.getAuthToken(this), mClubID, message, {
                    Toast.makeText(this, "Post Sent", Toast.LENGTH_SHORT).show()
                    finish()
                }) { Toast.makeText(this, "$it: Unable to post", Toast.LENGTH_SHORT).show() }
            }
        }

        binding.etMessage.addTextChangedListener(MarkwonEditorTextWatcher.withProcess(mkdEditor))

        binding.previewChip.setOnCheckedChangeListener { _, isChecked ->
            val focus = currentFocus
            if (focus != null) {
                (getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                    .hideSoftInputFromWindow(focus.windowToken, 0)
            }

            binding.etMessage.isVisible = !isChecked
            binding.mkdTest.isVisible = isChecked
            if (isChecked) {
                mkd.setMarkdown(binding.mkdTest, binding.etMessage.text?.toString() ?: "")
                binding.previewChip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white)))
                binding.previewChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.main_color))
            } else {
                binding.previewChip.setTextColor(ColorStateList.valueOf(ContextCompat.getColor(this, R.color.black)))
                binding.previewChip.chipBackgroundColor = ColorStateList.valueOf(ContextCompat.getColor(this, R.color.white))
            }
        }
    }

    private fun setValues() {
        mkd = getMkdFormatter()
        mkdEditor = MarkwonEditor.create(mkd)

        val clubID = intent.getStringExtra(Constants.CLUB_ID)
        val clubName = intent.getStringExtra(Constants.CLUB_NAME)
        binding.clubName.text = clubName
        if (clubID == null) {
            Toast.makeText(this, "Error: Club ID NULL", Toast.LENGTH_SHORT).show()
            finish()
            return
        }
        mClubID = clubID

        mEditMode = intent.getBooleanExtra(Constants.EDIT_MODE, false)
        if (!mEditMode) return

        mPostID = intent.getStringExtra(Constants.POST_ID) ?: ""
        if (mPostID.isEmpty()) {
            Toast.makeText(this, "Error: Post ID empty", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.etMessage.text = Editable.Factory.getInstance()
            .newEditable(intent.getStringExtra(Constants.MESSAGE) ?: "")
    }
}