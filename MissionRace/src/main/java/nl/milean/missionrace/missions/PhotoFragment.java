package nl.milean.missionrace.missions;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.Mission;

public class PhotoFragment extends MissionFragment {

    protected ImageView mPhoto;
    protected TextView mText;
    protected Button mGMButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.content_mission_photo, container, false);

        mPhoto = (ImageView) view.findViewById(R.id.photo_image);
        mText = (TextView) view.findViewById(R.id.photo_explanation);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initTextData(mission);

        return view;
    }

    private void initTextData(Mission mission){
        String photoName = (String) mission.getData("photo");
        mPhoto.setImageResource(getResources().getIdentifier(photoName, "drawable", getActivity().getPackageName()));
        if (mission.containsData("text")) {
           mText.setText((String) mission.getData("text"));
        }
    }

}
