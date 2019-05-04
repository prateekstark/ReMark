package com.example.remark.ui.game;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.balysv.materialripple.MaterialRippleLayout;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.remark.MainActivity;
import com.example.remark.R;
import com.example.remark.api.AnswerAPIClient;
import com.example.remark.api.Bluetooth;
import com.example.remark.api.DeckAPIClient;
import com.example.remark.api.PendingViewBehaviour;
import com.example.remark.api.PlayerAPIClient;
import com.example.remark.api.PreventDoubleClickOnClickListener;
import com.example.remark.model.Deck;


public class QuestionFragment extends Fragment {

    private TextView mQuestionTitle;
    private TextView mUserAnswer;

    private MaterialRippleLayout mSubmitButton;

    private static FragmentTask fragmentTask;
    private Handler handler;
    private boolean isAbleSubmit;
    private boolean isHost;

    public static FragmentTask getFragmentTask() {
        return fragmentTask;
    }

    public static QuestionFragment newInstance() {
        Bundle args = new Bundle();
        QuestionFragment fragment = new QuestionFragment();
        fragment.setArguments(args);

        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_question, container, false);

        mQuestionTitle = (TextView) rootView.findViewById(R.id.fragment_question_text_title);
        mUserAnswer = (TextView) rootView.findViewById(R.id.fragment_question_text_answer);

        mSubmitButton = (MaterialRippleLayout) rootView.findViewById(R.id.submit_button);

        handler = new Handler();

        fragmentTask = new FragmentTask(this);
        isHost = Bluetooth.getInstance().getServer() != null;

        return rootView;
    }

    @Override
    public void onViewCreated(final View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (isHost) {
            //get question
            Deck deck = DeckAPIClient.getInstance().getDeck();

            //customize deck
            if (deck == null) {
                //layout
                View dialogView = LayoutInflater.from(getActivity()).inflate(R.layout.view_customize_deck, null);
                final EditText questionEditText = ((EditText) dialogView.findViewById(R.id.questionDialog));
                final EditText answerEditText = ((EditText) dialogView.findViewById(R.id.answerDialog));

                //create dialog
                final AlertDialog alertDialog;

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    alertDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert)
                            .setTitle(getString(R.string.customize_question))
                            .setView(dialogView)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create();
                } else {
                    alertDialog = new AlertDialog.Builder(getActivity(), android.R.style.Theme_Material_Light_Dialog_Alert)
                            .setTitle(getString(R.string.customize_question))
                            .setView(dialogView)
                            .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            }).create();
                }

                alertDialog.show();

                //dialog button action
                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        String question = questionEditText.getText().toString().trim();
                        String answer = answerEditText.getText().toString().trim();

                        if (!question.equals("") && !answer.equals("")) {
                            confirmQuestion(new Deck(question, answer));
                            alertDialog.dismiss();
                        } else {
                            Toast.makeText(getContext(), getString(R.string.question_and_answer_cannot_be_empty), Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } else {
                confirmQuestion(deck);
            }
        }

        //submit button
        mSubmitButton.setOnClickListener(new PreventDoubleClickOnClickListener() {
            @Override
            public void preventDoubleClickOnClick(View v) {
                if (isAbleSubmit) {
                    String answer = mUserAnswer.getText().toString();

                    //reject empty answer
                    if (answer.equals("")) {
                        Toast.makeText(getContext(), getString(R.string.answer_cannot_be_empty), Toast.LENGTH_SHORT).show();
                        return;
                    }

                    //send answer
                    try {
                        JSONObject data = new JSONObject();
                        data.put("address", MainActivity.getBluetoothAddress());
                        data.put("answer", answer);
                        if (isHost)
                            Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_ANSWER, data));
                        else
                            Bluetooth.getInstance().getClient().send(Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_ANSWER, data));
                    } catch (JSONException ignore) {
                    }

                    //set answer
                    AnswerAPIClient.getInstance().getAnswers().put(MainActivity.getBluetoothAddress(), answer);

                    showPendingFragment();
                }
            }
        });
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        fragmentTask = null;

        InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mUserAnswer.getWindowToken(), 0);
    }

    private void confirmQuestion(Deck deck) {
        //send question to other players
        try {
            JSONObject data = new JSONObject();
            data.put("question", deck.getQuestion());
            data.put("answer", deck.getAnswer());
            Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_QUESTION, data));
        } catch (JSONException ignore) {
        }

        //set correct answer
        AnswerAPIClient.getInstance().getAnswers().put("correct", deck.getAnswer());

        //set question
        AnswerAPIClient.getInstance().setQuestion(deck.getQuestion());
        mQuestionTitle.setText(deck.getQuestion());
        isAbleSubmit = true;
    }

    public void showPendingFragment() {
        getActivity().getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.activity_main_vg_fragment, PendingFragment.newInstance(new PendingViewBehaviour() {
                    @Override
                    public PreventDoubleClickOnClickListener getOnClickListener() {
                        return new PreventDoubleClickOnClickListener() {
                            @Override
                            public void preventDoubleClickOnClick(View v) {
                                try {
                                    Bluetooth.getInstance().getServer().sendExclude(null, Bluetooth.wrapMessage(Bluetooth.DATA_TYPE_START_SELECT_ANSWER, new JSONObject()));
                                } catch (JSONException ignore) {
                                }

                                showNextFragment();
                            }
                        };
                    }

                    @Override
                    public void showNextFragment() {
                        getFragment().getActivity().getSupportFragmentManager()
                                .beginTransaction()
                                .replace(R.id.activity_main_vg_fragment, AnswerFragment.newInstance())
                                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                                .commit();
                    }

                    @Override
                    public void populateDonePlayers() {
                        for (String address : AnswerAPIClient.getInstance().getAnswers().keySet())
                            if (!address.equals("correct"))
                                getFragment().exchangeView(PlayerAPIClient.getInstance().get(address));

                        getFragment().showNextButton();
                    }

                    @Override
                    public void done(final String address) {
                        getHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                getFragment().exchangeView(PlayerAPIClient.getInstance().get(address));

                                getFragment().showNextButton();
                            }
                        });
                    }
                }))
                .setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
                .commit();
    }

    private void updateQuestion(final String question) {
        handler.post(new Runnable() {
            @Override
            public void run() {
                AnswerAPIClient.getInstance().setQuestion(question);
                mQuestionTitle.setText(question);
                isAbleSubmit = true;
            }
        });
    }

    public static class FragmentTask {
        private QuestionFragment fragment;

        FragmentTask(Fragment fragment) {
            this.fragment = (QuestionFragment) fragment;
        }

        public void updateQuestion(String question) {
            fragment.updateQuestion(question);
        }
    }
}
