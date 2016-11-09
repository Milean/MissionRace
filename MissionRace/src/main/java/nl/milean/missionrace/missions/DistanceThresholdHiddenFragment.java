package nl.milean.missionrace.missions;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

public class DistanceThresholdHiddenFragment extends MissionFragment {

    protected Location mCurrentLocation;
    protected Location mDestinationLocation;

    protected TextView mTextLabel;
    protected float mDistanceThreshold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_distance_threshold_hidden, container, false);

        mTextLabel = (TextView) view.findViewById(R.id.distance_threshold_hidden_message);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initDistanceThresholdData(mission);

        return view;
    }

    private void initDistanceThresholdData(Mission mission){
        mTextLabel.setText((String) mission.getData("message"));
        mDestinationLocation = new Location("target");
        mDestinationLocation.setLatitude((Double) mission.getData("targetLat"));
        mDestinationLocation.setLongitude((Double) mission.getData("targetLon"));
        mDistanceThreshold = (Float)mission.getData("threshold");
    }

    private void updateMissionStatus(){
        if (mDestinationLocation == null || mCurrentLocation == null) {
            return;
        }

        float distance = mCurrentLocation.distanceTo(mDestinationLocation);
        if(distance <= mDistanceThreshold) {
            completeMission();
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        updateMissionStatus();
    }
}
