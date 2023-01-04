package com.example.notificationapp.view.fragments;

import static android.app.Activity.RESULT_OK;
import static android.opengl.ETC1.encodeImage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContract;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.example.notificationapp.R;
import com.example.notificationapp.data.network.api.RetrofitAccessObject;
import com.example.notificationapp.data.network.model.UserResponse;
import com.example.notificationapp.utils.Constants;
import com.github.dhaval2404.imagepicker.ImagePicker;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ProfileFragment extends Fragment {
    UserResponse user;
    TextView email_tv,year_tv,regno_tv,name_tv1,name_tv,course_tv,mobile_tv;
    ImageView edit_img,profilePic;

    ActivityResultLauncher<Void> launcher = registerForActivityResult(new Contract(), new ActivityResultCallback<Uri>() {
        @Override
        public void onActivityResult(Uri result) {
            Log.d("Hello: ", "encodedImage");
            if (result == null) {
                return;
            }
            try {
                InputStream imageStream = requireContext().getContentResolver().openInputStream(result);
                Bitmap selectedImage = BitmapFactory.decodeStream(imageStream);
                profilePic.setImageBitmap(selectedImage);
                String encodedImage = encodeImage(selectedImage);
                Log.d("Hello: ", encodedImage);
                updateProfilePicture(encodedImage);
            } catch (Exception ex){
                ex.printStackTrace();
            }
        }
    });

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_profile, container, false);
        setRefrences(root);
        getUserData();
        setListeners();
        return root;
    }

    private void setListeners() {
        edit_img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launcher.launch(null);
            }
        });
    }

    private class Contract extends ActivityResultContract<Void, Uri> {

        @NonNull
        @NotNull
        @Override
        public Intent createIntent(@NonNull @NotNull Context context, Void v) {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            return intent;
        }

        @Override
        public Uri parseResult(int resultCode, @Nullable Intent intent) {
            if (resultCode != RESULT_OK)
                return null;
            if (intent == null)
                return null;
            return intent.getData();
        }
    }

    private void updateProfilePicture(String encodedImage) {
        SharedPreferences preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);


    }

    private String encodeImage(Bitmap bm) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bm.compress(Bitmap.CompressFormat.JPEG,100,baos);
        byte[] b = baos.toByteArray();
        String encImage = Base64.encodeToString(b, Base64.DEFAULT);
        return encImage;
    }

    private Bitmap decodeImage(String imageString){
        byte[] imageBytes = Base64.decode(imageString, Base64.DEFAULT);
        Bitmap decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);
        return decodedImage;
    }

    private void getUserData() {
        SharedPreferences preferences = requireContext().getSharedPreferences(Constants.SHARED_PREFERENCE, Context.MODE_PRIVATE);
        RetrofitAccessObject.getRetrofitAccessObject().getUserData(preferences.getString(Constants.TOKEN, "")).enqueue(new Callback<UserResponse>() {
            @Override
            public void onResponse(Call<UserResponse> call, Response<UserResponse> response) {
                if(response.isSuccessful() && response.body() != null){
                    user = response.body();
                    setProfileValues();
                }
            }
            @Override
            public void onFailure(Call<UserResponse> call, Throwable t) {
            }
        });
    }

    private void setProfileValues() {
        if(user == null) return;
        email_tv.setText(user.getEmail());
        name_tv.setText(user.getName());
        name_tv1.setText(user.getName());
        mobile_tv.setText(user.getPhoneNumber());
        regno_tv.setText(user.getRegistrationNumber());
        year_tv.setText(user.getGraduationYear());
        course_tv.setText(user.getCourse());
        if(user.getAvatar() != null){
            Bitmap bm = decodeImage(user.getAvatar());
            profilePic.setImageBitmap(bm);
        }
    }

    private void setRefrences(View root) {
        email_tv = root.findViewById(R.id.email_tv);
        name_tv1 = root.findViewById(R.id.name_tv1);
        name_tv = root.findViewById(R.id.tv_name);
        mobile_tv = root.findViewById(R.id.mobile_tv);
        regno_tv = root.findViewById(R.id.tv_regNo);
        year_tv = root.findViewById(R.id.year_tv);
        course_tv = root.findViewById(R.id.course_tv);
        edit_img = root.findViewById(R.id.edit_img);
        profilePic = root.findViewById(R.id.profilepic);
    }

}
