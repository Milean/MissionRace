package nl.milean.missionrace.missions;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Collections;
import java.util.Map;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.MissionDisplay;
import nl.milean.missionrace.missiondata.Mission;

public class MissionFragment extends Fragment {

    private MissionDisplay mCallback;
    private int mNumber;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            mCallback = (MissionDisplay) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString()
                    + " must extend MissionDisplay");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        mNumber = mission.getMissionNumber();

        return view;
    }

    protected Map<String,String> loadData() { return mCallback.loadMissionData(mNumber); }

    protected boolean hasData(String key) { return mCallback.hasMissionData(mNumber, key); }

    protected void saveData(Map<String,String> data) {
        mCallback.saveMissionData(mNumber, data);
    }

    protected void saveData(String key, String val) { mCallback.saveMissionData(mNumber, Collections.singletonMap(key,val)); }

    protected void completeMission() { mCallback.the_viewMission_is_now_finished(true); }

    protected void completeMission(boolean vibrate) { mCallback.the_viewMission_is_now_finished(vibrate); }

    protected void vibrate_fail() {
        mCallback.vibrate_fail();
    }

    protected void vibrate_success() {
        mCallback.vibrate_success();
    }

    protected void showMessage(String message) { mCallback.showMessage(message); }

    protected void setStatus(String status) { mCallback.setStatus(status); }

    public void onLocationChanged(Location location) {
        return;
    }

}
