package nl.milean.missionrace.missions;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.Map;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;
import nl.milean.missionrace.missiondata.MissionStates;

public class QuestionFragment extends MissionFragment {

    protected TextView mQuestionLabel;
    protected EditText mAnswerText;
    protected String[] mAnswers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_question, container, false);

        mQuestionLabel = (TextView) view.findViewById(R.id.question_question);
        mAnswerText = (EditText) view.findViewById(R.id.question_answer);

        Button answer = (Button) view.findViewById(R.id.question_submit);
        answer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(v);
            }
        });

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initQuestionData(mission);
        return view;
    }

    private void initQuestionData(Mission mission){
        mQuestionLabel.setText((String) mission.getData("question"));
        if (mission.getMissionState() == MissionStates.FINISHED) {
            mAnswerText.setText((String) loadData().get("ans"));
            mAnswerText.setEnabled(false);
        }
        mAnswers = (String[]) mission.getData("answers");
    }

    public void submitAnswer(View view){
        String answer = mAnswerText.getText().toString().trim();
        boolean correct = false;
        for(String possibility : mAnswers){
            if(possibility.equalsIgnoreCase(answer)){
                correct = true;
                break;
            }
        }

        if(correct){
            showMessage("Correct!");

            Map<String,String> save = new HashMap<>();
            save.put("ans",answer);
            saveData(save);

            mAnswerText.setEnabled(false);

            completeMission();
        }
        else{
            showMessage("Dit antwoord is niet correct");

            vibrate_fail();

            mAnswerText.setText("");
        }
    }
}
