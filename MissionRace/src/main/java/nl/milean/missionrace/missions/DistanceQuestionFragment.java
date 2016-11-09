package nl.milean.missionrace.missions;

import android.location.Location;
import android.os.Bundle;
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

public class DistanceQuestionFragment extends MissionFragment {

    protected Location mCurrentLocation;
    protected Location mDestinationLocation;

    protected TextView mDistanceLabel;
    protected Button mSubmit;

    protected TextView mQuestionLabel;
    protected EditText mAnswerText;
    protected String[] mAnswers;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_distance_question, container, false);

        mSubmit = (Button) view.findViewById(R.id.distance_question_submit);

        mDistanceLabel = (TextView) view.findViewById(R.id.distance_question_distance);
        mQuestionLabel = (TextView) view.findViewById(R.id.distance_question_question);
        mAnswerText = (EditText) view.findViewById(R.id.distance_question_answer);

        mSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitAnswer(v);
            }
        });

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initDistanceQuestionData(mission);

        return view;
    }

    private void initDistanceQuestionData(Mission mission){
        mQuestionLabel.setText((String) mission.getData("question"));
        mAnswers = (String[]) mission.getData("answers");

        mDestinationLocation = new Location("target");
        mDestinationLocation.setLatitude((Double) mission.getData("targetLat"));
        mDestinationLocation.setLongitude((Double) mission.getData("targetLon"));
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
            Toast toast = Toast.makeText(getActivity(), "Well done!", Toast.LENGTH_SHORT);
            toast.show();

            Map<String,String> save = new HashMap<>();
            save.put("ans",answer);
            saveData(save);

            mAnswerText.setEnabled(false);

            completeMission();
        }
        else{
            Toast toast = Toast.makeText(getActivity(), "This is not the correct answer.", Toast.LENGTH_SHORT);
            toast.show();

            vibrate_fail();

            mAnswerText.setText("");
        }
    }

    private void updateMissionStatus(){
        if (mDestinationLocation == null || mCurrentLocation == null) {
            return;
        }

        float distance = mCurrentLocation.distanceTo(mDestinationLocation);
        mDistanceLabel.setText(String.format(java.util.Locale.US, "%.0f " + getResources().getString(R.string.mission_distance_question_explanation2), distance));
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMissionStatus();
    }
}
