package nl.milean.missionrace.missions;

import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

public class DistancePhotoHiddenFragment extends MissionFragment {

    protected Location mCurrentLocation;
    protected Location mDestinationLocation;

    protected ImageView mImageView;
    protected TextView mText;
    protected float mDistanceThreshold;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_distance_photo_hidden, container, false);

        mImageView = (ImageView) view.findViewById(R.id.distance_photo_hidden_image);
        mText = (TextView) view.findViewById(R.id.distance_photo_hidden_explanation);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initDistanceThresholdData(mission);

        return view;
    }

    private void initDistanceThresholdData(Mission mission){
        String photoName = (String) mission.getData("photo");
        mImageView.setImageResource(getResources().getIdentifier(photoName, "drawable", getActivity().getPackageName()));

        mDestinationLocation = new Location("target");
        mDestinationLocation.setLatitude((Double) mission.getData("targetLat"));
        mDestinationLocation.setLongitude((Double) mission.getData("targetLon"));
        mDistanceThreshold = (Float)mission.getData("threshold");

        if (mission.containsData("text")) {
            mText.setText((String) mission.getData("text"));
        }
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
