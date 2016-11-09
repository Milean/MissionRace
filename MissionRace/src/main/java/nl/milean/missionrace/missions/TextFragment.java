package nl.milean.missionrace.missions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

public class TextFragment extends MissionFragment {

    protected TextView mTextLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_text, container, false);

        mTextLabel = (TextView) view.findViewById(R.id.text_body);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initTextData(mission);

        return view;
    }

    private void initTextData(Mission mission){
        mTextLabel.setText((String) mission.getData("message"));
    }

}
