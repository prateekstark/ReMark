package com.example.remark.ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.remark.R;
import com.example.remark.model.UserManager;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    private ImageView ivProfilePicture;
    private EditText etNickname;
    private Button mBtChangePicture;

    private UserManager mUserManager;
    private boolean mCanExit;
    private boolean mDialogOn;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);

        mUserManager = UserManager.getInstance();
        mCanExit = true;
        mDialogOn = false;

        etNickname = (EditText) rootView.findViewById(R.id.et_profile_nickname);
        mBtChangePicture = (Button) rootView.findViewById(R.id.bt_profile_change_picture);
        ivProfilePicture = (ImageView) rootView.findViewById(R.id.iv_profile_picture);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setTextNickname();

        setProfilePicture();

        mBtChangePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (canExit()) {
                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main_vg_fragment, new TauntFragment(), "taunt")
                            .commit();
                }
            }
        });
    }

    public void setTextNickname() {
        etNickname.setText(mUserManager.getName());

        etNickname.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (!hasFocus) {
                    setCanExit();
                }
            }
        });
    }

    private void setProfilePicture() {
        String picturePath = mUserManager.getProfilePicture();

        Log.d(TAG, "onViewCreated: " + picturePath);

        if (picturePath != null) {
            ivProfilePicture.setImageBitmap(BitmapFactory.decodeFile(picturePath));
        }
        else {
            ivProfilePicture.setImageResource(R.drawable.profilephoto);
        }
    }

    private void showDialogEmptyNickname() {
        // only one dialog at a time
        if (!mDialogOn) {
            mDialogOn = true;
            new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.error)
                    .setMessage(getString(R.string.empty_nickname))
                    .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                            mDialogOn = false;
                        }
                    })
                    .show();
        }
    }

    private void setCanExit() {
        if (etNickname.getText().toString().equals("")) {
            mCanExit = false;
            showDialogEmptyNickname();
        } else {
            mCanExit = true;
            mUserManager.setName(etNickname.getText().toString());
        }
    }

    public boolean canExit() {
        setCanExit();
        return mCanExit;
    }
}
