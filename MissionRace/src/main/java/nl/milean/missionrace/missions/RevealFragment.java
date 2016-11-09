package nl.milean.missionrace.missions;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.ImageRevealView;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

/**
 * Created by Tchakkazulu on 13/04/2016.
 */
public class RevealFragment extends MissionFragment {

    protected ImageView mPhoto;
    protected ImageRevealView mOverlay;
    protected TextView mText;

    private int mTilesW;
    private int mTilesH;
    private Integer[] mOrder;
    private List<String> mQuestions;
    private List<String[]> mAnswers;
    private boolean[] mCorrect;
    private int mTileIndex;
    private int mRightAnswers;
    private int mQuestionIndex;
    private Button mQuestionButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_reveal, container, false);

        mPhoto = (ImageView) view.findViewById(R.id.reveal_image);
        RelativeLayout wrap = (RelativeLayout) view.findViewById(R.id.reveal_frame);

        mText = (TextView) view.findViewById(R.id.reveal_explanation);

        mQuestionButton = (Button) view.findViewById(R.id.reveal_question_button);
        mQuestionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                askNextQuestion(v);
            }
        });

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initRevealData(mission);

        mOverlay = new ImageRevealView(getActivity(),mTilesW,mTilesH);
        mOverlay.setLayoutParams(mPhoto.getLayoutParams());

        wrap.addView(mOverlay);

        return view;
    }

    private void initRevealData(Mission mission){
        String photoName = (String) mission.getData("photo");
        mText.setText((String) mission.getData("message"));
        mOrder = (Integer[]) mission.getData("revealOrder");
        mTilesW = (int) mission.getData("tilesW");
        mTilesH = (int) mission.getData("tilesH");
        mQuestions = (List<String>) mission.getData("questions");

        mAnswers = (List<String[]>) mission.getData("answers");
        mPhoto.setImageResource(getResources().getIdentifier(photoName, "drawable", getActivity().getPackageName()));
        mPhoto.postInvalidate();
    }

    public void askNextQuestion(View someview) {
        LayoutInflater inflater= LayoutInflater.from(getActivity());
        View view=inflater.inflate(R.layout.popup_question, null);

        final EditText input=(EditText) view.findViewById(R.id.reveal_popup_question);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
        alertDialog.setTitle("Beantwoord de vraag");
        alertDialog.setMessage(mQuestions.get(mQuestionIndex));
        alertDialog.setView(view);
        alertDialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String answer = input.getText().toString().trim();

                boolean correct = false;
                for (String possibility : mAnswers.get(mQuestionIndex)) {
                    if (possibility.equalsIgnoreCase(answer)) {
                        correct = true;
                        break;
                    }
                }

                if (correct) {
                    showMessage("Correct!");

                    rightAnswer();

                    vibrate_success();

                } else {
                    showMessage("Dit antwoord is niet correct");

                    vibrate_fail();
                }

                input.setText("");
                mQuestionIndex = nextQuestion();
                if (mQuestionIndex == -1) {
                    mQuestionButton.setText("Alle vragen beantwoord");
                    mQuestionButton.setEnabled(false);
                }

            }
        });
        AlertDialog alert = alertDialog.create();
        alert.show();

    }

    private void rightAnswer() {
        mCorrect[mQuestionIndex] = true;
        mRightAnswers++;
        int nextIndex = (mOrder.length * mRightAnswers) / mQuestions.size();
        while (mTileIndex < nextIndex) {
            revealNext();
        }
    }

    private int nextQuestion() {
        if (mQuestionIndex == -1) {
            return -1;
        }
        for (int i = mQuestionIndex + 1; i < mQuestions.size(); i++) {
            if (!mCorrect[i]) {
                return i;
            }
        }
        for (int i = 0; i < mQuestionIndex; i++) {
            if (!mCorrect[i]) {
                return i;
            }
        }
        return -1;
    }

    private void revealNext() {
        int tileNum = mOrder[mTileIndex];
        mOverlay.reveal(tileNum / mTilesW, tileNum % mTilesW);
        mTileIndex++;
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i("RevealFragment","Pause!");
        saveState();
    }

    public void onResume() {
        super.onResume();
        Log.i("RevealFragment","Resume!");

        mRightAnswers = 0;
        mCorrect = new boolean[mQuestions.size()];
        if (!hasData("correct")) {
            Log.i("RevealFragment","Eerste keer");
            for (int i = 0; i < mQuestions.size(); i++) {
                mCorrect[i] = false;
            }
            saveState();
        } else {
            String correctStr = loadData().toString();
            Log.i("RevealFragment","Andere keer: " + correctStr + ", " + this);
            correctStr = loadData().get("correct");
            for (int i = 0; i < mQuestions.size(); i++) {
                boolean b = correctStr.charAt(i) == '1';
                mCorrect[i] = b;
                if (b) {
                    mRightAnswers++;
                }
            }
        }

        mQuestionIndex = mQuestions.size() - 1;
        mQuestionIndex = nextQuestion();

        mTileIndex = 0;
        int nextIndex = (mOrder.length * mRightAnswers) / mQuestions.size();
        while (mTileIndex < nextIndex) {
            revealNext();
        }
    }

    private void saveState() {
        Log.i("RevealFragment","this:" + this);
        StringBuilder builder = new StringBuilder();
        for (boolean b : mCorrect) {
            if (b) { builder.append("1"); } else { builder.append("0"); }
        }
        Log.i("RevealFragment","save:" + builder.toString());
        saveData("correct", builder.toString());
        Log.i("RevealFragment","Everything: " + loadData());
    }
}
