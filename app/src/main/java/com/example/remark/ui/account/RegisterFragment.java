package com.example.remark.ui.account;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import com.example.remark.R;
import com.example.remark.model.UserManager;
import com.example.remark.ui.MainFragment;

public class RegisterFragment extends Fragment{

    private Button mRegisterButton;
    private EditText etNickname;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_register, container, false);

        mRegisterButton = (Button) rootView.findViewById(R.id.bt_register);
        etNickname = (EditText) rootView.findViewById(R.id.et_profile_nickname);

        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String nickname = etNickname.getText().toString();
                if (!nickname.equals("")) {
                    UserManager.getInstance().setName(nickname);

                    getActivity().getSupportFragmentManager()
                            .beginTransaction()
                            .replace(R.id.activity_main_vg_fragment, MainFragment.newInstance(), "main")
                            .commit();
                } else {
                    showDialogEmptyNickname();
                }
            }
        });
    }

    private void showDialogEmptyNickname() {
        final AlertDialog alertDialog = new AlertDialog.Builder(getActivity()).create();
        alertDialog.setTitle(R.string.error);
        alertDialog.setMessage(getString(R.string.empty_nickname));
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.ok),
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        alertDialog.show();
    }

}
