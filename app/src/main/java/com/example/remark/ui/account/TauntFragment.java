package com.example.remark.ui.account;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.remark.R;
import com.example.remark.model.UserManager;

import static android.content.ContentValues.TAG;

public class TauntFragment extends Fragment {

    private static final int ACTIVITY_RESULT_SELECT_PICTURE = 1;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_taunt, container, false);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(Intent.createChooser(intent, "Select Picture"), ACTIVITY_RESULT_SELECT_PICTURE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == ACTIVITY_RESULT_SELECT_PICTURE) {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};

                Cursor cursor = getContext().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                String picturePath;

                Log.d(TAG, "onActivityResult: " + selectedImage.toString());

                picturePath = selectedImage.toString();

                if (cursor == null) {
                    picturePath = selectedImage.toString();

                } else {
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    picturePath = cursor.getString(columnIndex);
                    cursor.close();
                }
                UserManager.getInstance().setProfilePicture(picturePath);
            }
        }
        // go back anyway - if picture was chosen (and saved) or not (use of back button)
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, new ProfileFragment())
                .commit();
    }

}
