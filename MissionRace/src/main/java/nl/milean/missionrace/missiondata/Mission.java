package nl.milean.missionrace.missiondata;

import java.io.Serializable;
import java.util.HashMap;

/**
 * Created by Michiel on 13-3-2016.
 */
public class Mission implements Serializable{
    private HashMap<String, Object> data;
    private MissionTypes missionType;
    private int missionNumber;
    private int missionState;

    public Mission(MissionTypes type){
        this.missionState = MissionStates.UNAVAILABLE;
        this.missionType = type;
        this.missionNumber = -1;
        this.data = new HashMap<String, Object>();
    }

    public int getMissionState(){
        return missionState;
    }
    public void setMissionState(int newState){
        this.missionState = newState;
    }

    public int getMissionNumber() { return missionNumber; }
    public void setMissionNumber(int newNumber) { this.missionNumber = newNumber; }

    public MissionTypes getType(){
        return this.missionType;
    }

    public Object getData(String key){
        return this.data.get(key);
    }

    public Boolean containsData(String key) { return this.data.containsKey(key); }

    public boolean addData(String key, Object value){
        if(this.data.containsKey(key)){
            return false;
        }
        else {
            this.data.put(key, value);
            return true;
        }
    }
}
