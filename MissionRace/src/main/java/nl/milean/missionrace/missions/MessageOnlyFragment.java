package nl.milean.missionrace.missions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

/**
 * Created by Tchakkazulu on 09/06/2016.
 */
public class MessageOnlyFragment extends MissionFragment {

    protected TextView mTextLabel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_message_only, container, false);

        mTextLabel = (TextView) view.findViewById(R.id.message_only_body);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initMessageOnlyData(mission);

        return view;
    }

    private void initMessageOnlyData(Mission mission){
        mTextLabel.setText((String) mission.getData("message"));
        completeMission(false);
        setStatus("Informatie voor de volgende missie.");
    }

}
