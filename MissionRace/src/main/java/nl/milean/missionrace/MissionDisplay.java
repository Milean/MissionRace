package nl.milean.missionrace;

import android.Manifest;
import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import nl.milean.missionrace.messages.QuickstartPreferences;
import nl.milean.missionrace.messages.RegistrationIntentService;
import nl.milean.missionrace.missiondata.Mission;
import nl.milean.missionrace.missiondata.MissionParams;
import nl.milean.missionrace.missiondata.MissionStates;
import nl.milean.missionrace.missions.MissionFragment;

/**
 * Let's try it again. With fragments!
 * Created by Tchakkazulu on 20/05/2016.
 */
public class MissionDisplay extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener, LocationListener {

    protected SharedPreferences sharedPref;

    //global state info
    protected String mPlayerID;
    private static int currentMission;

    //local state info
    private static int viewMission;
    protected int mState = MissionStates.UNAVAILABLE;

    //derived info
    protected TextView mMissionStatusLabel;
    protected String mInfo;
    protected String mTitle;
    protected Button mNextButton;
    protected Button mPrevButton;
    protected MissionFragment curMission;

    protected GoogleApiClient mGoogleApiClient;
    protected LocationRequest mLocationRequest;
    protected Location mLastLocation;
    protected Long lastLocationUpdateTime;
    protected Long lastGMUpdateTime;
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    private BroadcastReceiver mMessageReceiver;


    /**
     * Lifecycle methods
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Set the display etc
        setContentView(R.layout.activity_mission_display);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        //init local parameters
        lastLocationUpdateTime = 0l;
        lastGMUpdateTime = 0l;
        mLastLocation = null;

        //retrieve UI values
        mMissionStatusLabel = (TextView) findViewById(R.id.mission_status);
        mNextButton = (Button) findViewById(R.id.button_next);
        mPrevButton = (Button) findViewById(R.id.button_previous);

        //load stored values
        sharedPref = getSharedPreferences(getString(R.string.missions_preferences), Context.MODE_PRIVATE);
        currentMission = sharedPref.getInt("currentMission", 0);
        viewMission = currentMission;
        mPlayerID = sharedPref.getString(getString(R.string.playerID_key), null);

//        startMessaging();
//        if(mGoogleApiClient == null) {
//            startGPS();
//        }
        // If we're being restored from a previous state,
        // then we don't need to do anything and should return or else
        // we could end up with overlapping fragments.
        if (savedInstanceState == null) {
            showMission(viewMission);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Mission mission = MissionParams.getInstance().getMission(viewMission);
        curMission = loadMissionFragment(mission);
        init_all_mission_and_fragment_fields();
        startMessaging();
        startGPS();
    }

    @Override
    protected void onDestroy() {
        stopMessaging();
        stopGPS();
        //store values

        super.onDestroy();
    }

    /**
     * End lifecycle methods
     */

    /**
     * Navigate between missions
     */

    //the gm approved a specific mission
    public void gm_approval(int mission_approved){
        if(currentMission == mission_approved){
            increase_currentMission_by_one();
            init_all_mission_and_fragment_fields();
            vibrate_success();
            sendNotification("Missie goedgekeurd door spelleider.");
        }
    }
    //The current viewMission is finished now.
    public void the_viewMission_is_now_finished(boolean vibrate){
        if(mState != MissionStates.FINISHED){
            mState = MissionStates.FINISHED;
            saveViewMissionState();
            increase_currentMission_by_one();
            init_all_mission_and_fragment_fields();
            if(vibrate){
                vibrate_success();
            }
        }
//        else{
//            showMessage("You already finished this mission.");
//        }
    }
    private void increase_currentMission_by_one(){
        sharedPref.edit().putInt("currentMission",(currentMission+1)).apply();
        currentMission++;
        sendGMUpdate(true);
    }

    public void init_all_mission_and_fragment_fields(){
        //load mission state
        mState = getSharedPreferences(getString(R.string.missions_preferences),MODE_PRIVATE).getInt("mission" + viewMission, MissionStates.UNAVAILABLE);

        //check if it needs updating
        //Check if this mission is already finished.
        if(currentMission > viewMission){
            if(mState != MissionStates.FINISHED){
                mState = MissionStates.FINISHED;
                saveViewMissionState();
            }
        }

        //Since the mission is not yet finished, lets update the status label first
        initMissionStatusLabel();

        //If this mission is new (after checking whethe, we should only display the 'new' status label once.
        //To get this effect, we immediately update the mission state.
        if (mState == MissionStates.UNAVAILABLE) {
            mState = MissionStates.STARTED;
            saveViewMissionState();
        }

        //deduce the rest of the local mission variables.
        setButtons();
    }
    private void saveViewMissionState(){
        getSharedPreferences(getString(R.string.missions_preferences),MODE_PRIVATE).edit().putInt("mission" + viewMission, mState).apply();
    }

    private void showMission(int missionNumber) {

        Mission mission = MissionParams.getInstance().getMission(missionNumber);
        mInfo = (String) mission.getData("info");
        mTitle = (String) mission.getData("title");

        getSupportActionBar().setTitle(mTitle);

        MissionFragment showMissionFrag = loadMissionFragment(mission);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(missionNumber >= viewMission){
            ft.setCustomAnimations(R.anim.enter_right, R.anim.exit_left);
        }
        else {
            ft.setCustomAnimations(R.anim.enter_left, R.anim.exit_right);
        }
        ft.replace(R.id.mission_fragment_holder, showMissionFrag, "mission").commit();

        curMission = showMissionFrag;

        //the only place where viewMission gets updated
        viewMission = missionNumber;

        //get mission variables and set GUI
        init_all_mission_and_fragment_fields();

        mission.setMissionState(mState);
        mission.setMissionNumber(missionNumber);
    }

    private void setButtons() {
        mNextButton.setEnabled(viewMission < currentMission && viewMission < MissionParams.getInstance().size() - 1);
        mPrevButton.setEnabled(viewMission > 0);
    }


    //Handle navigation
    // These two are here to react on button taps.
    public void previousMission(View view) {
        //Check if the previous mission really can be loaded
        setButtons();
        if(mPrevButton.isEnabled()){
            int toView = viewMission-1;
            showMission(toView);
        }
    }
    public void nextMission(View view) {
        //Check if the next mission really should be loaded.
        setButtons();
        if(mNextButton.isEnabled()) {
            int toView = viewMission+1;
            showMission(toView);
        }
    }
    private MissionFragment loadMissionFragment(Mission m) {
        MissionFragment frag = m.getType().implFrag();
        Bundle args = new Bundle();
        args.putSerializable(Constants.MISSION_INTENT_EXTRA, m);
        frag.setArguments(args);
        return frag;
    }



    public void cheatGamemaster(){
        the_viewMission_is_now_finished(false);
    }

    /**
     * Startup & Shutdown methods
     */

    protected void stopMessaging(){
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mMessageReceiver);
    }

    protected void startMessaging(){
        //unregister previous receivers, just in case.
        stopMessaging();
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                SharedPreferences sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
                boolean sentToken = sharedPreferences.getBoolean(QuickstartPreferences.SENT_TOKEN_TO_SERVER, false);
                if (!sentToken) {
                    showMessage("Error occurred while registering for Google Cloud Messaging. Try stopping/starting the app.");
                }
            }
        };
        mMessageReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                String message = intent.getStringExtra("message");
                int assignment = intent.getIntExtra("assignment", 0);

                if(message.equals("Approved")){
                    gm_approval(assignment);
                }
                else{
                    showMessage("Unknown push message received: "+message);
                }
            }
        };

        if (checkPlayServices()) {
            // Start IntentService to register this application with GCM.
            Intent intent = new Intent(this, RegistrationIntentService.class);
            startService(intent);
        }

        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(QuickstartPreferences.REGISTRATION_COMPLETE));
        LocalBroadcastManager.getInstance(this).registerReceiver(mMessageReceiver, new IntentFilter("gcm_message_event"));
        Log.i("MissionDisplay", "Messaging started.");

    }

    private void startGPS() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            Log.i("MissionDisplay", "Created API Client.");

        }

        if(mGoogleApiClient.isConnected()){
            startLocationUpdates();
        }
        else{
            mGoogleApiClient.connect();
            Log.i("MissionDisplay", "Asked API Client to connect.");

        }
    }

    private void stopGPS() {
        if (mGoogleApiClient == null) {
            throw new IllegalStateException("stopGPS() called when Location API already disconnected.");
        }
        stopLocationUpdates();
        mGoogleApiClient.disconnect();
    }

    public void setStatus(String status) {
        mMissionStatusLabel.setText(status);
    }

    public void initMissionStatusLabel(){
        if(mState == MissionStates.UNAVAILABLE) {
            setStatus(getResources().getString(R.string.mission_new));
        } else if (mState == MissionStates.STARTED) {
            setStatus(getResources().getString(R.string.mission_current));
        } else if (mState == MissionStates.FINISHED) {
            setStatus(getResources().getString(R.string.mission_finished));
        }
    }

    public Map<String,String> loadMissionData(int mission) {
        String prefix = "m" + mission + "_";
        Map<String,String> result = new HashMap<>();
        for (Map.Entry<String,?> entry : sharedPref.getAll().entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();
            if (key.startsWith(prefix) && value instanceof String) {
                result.put(key.substring(prefix.length()),(String) value);
            }
        }
        return result;
    }

    public boolean hasMissionData(int mission, String key) {
        String prefix = "m" + mission + "_";
        return sharedPref.contains(prefix + key);
    }

    public void saveMissionData(int mission, Map<String, String> data) {
        String prefix = "m" + mission + "_";
        SharedPreferences.Editor e = sharedPref.edit();
        for (Map.Entry<String,String> entry : data.entrySet()) {
            e.putString(prefix + entry.getKey(), entry.getValue());
        }
        e.commit();
    }

    public void startLocationUpdates(){
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(Constants.LOCATION_UPDATE_INTERVAL_MSEC);
        mLocationRequest.setFastestInterval(Constants.LOCATION_UPDATE_FASTEST_MSEC);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        Log.i("MissionDisplay", "Requested location updates.");

    }
    public void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }



    /**
     * Useful methods, not bound to any lifecycle state
     */

    public void vibrate_success() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0, 500, 500,500};
        v.vibrate(pattern, -1);
    }

    public void vibrate_fail() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        long[] pattern = {0,1000};
        v.vibrate(pattern, -1);
    }

    public void showMessage(String message){
        Toast toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);
        toast.show();
    }

    /**
     * Lets the phone vibrate and shows a push message.
     *
     * @param message GCM message received.
     */
    private void sendNotification(String message) {
        Intent intent = new Intent(this, MissionDisplay.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent,
                PendingIntent.FLAG_ONE_SHOT);

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.ic_stat_ic_notification)
                .setContentTitle("Mission Race")
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

        NotificationManager notificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }

    /**
     * Check the device to make sure it has the Google Play Services APK. If
     * it doesn't, display a dialog that allows users to download the APK from
     * the Google Play Store or enable it in the device's system settings.
     */
    private boolean checkPlayServices() {
        GoogleApiAvailability apiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = apiAvailability.isGooglePlayServicesAvailable(this);
        if (resultCode != ConnectionResult.SUCCESS) {
            if (apiAvailability.isUserResolvableError(resultCode)) {
                apiAvailability.getErrorDialog(this, resultCode, 9000)
                        .show();
            } else {
                Log.i("MainActivity", "This device is not supported.");
                finish();
            }
            return false;
        }
        return true;
    }


    /** menu methods **/

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_mission, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {

            case R.id.action_info:
                infoPopup();
                return true;
            /*
            case R.id.action_cheat:
            cheatGamemaster();
            return true;
            */

            default:
                // If we got here, the user's action was not recognized.
                // Invoke the superclass to handle it.
                return super.onOptionsItemSelected(item);

        }
    }

    public void infoPopup() {
        LayoutInflater inflater= LayoutInflater.from(this);
        View view = inflater.inflate(R.layout.popup_info, null);

        TextView textview=(TextView)view.findViewById(R.id.popup_text);
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(this);
        alertDialog.setTitle("Info");
        alertDialog.setMessage(mInfo);
        alertDialog.setView(view);
        alertDialog.setPositiveButton("OK", null);
        AlertDialog alert = alertDialog.create();
        alert.show();
    }

    /** Messaging to and from the GM app **/

    private void sendGMUpdate(boolean immediate) {
        Long currentTime = new Date().getTime();
        Log.i("SENDLOCATION", "lastTime: " + lastGMUpdateTime + " current: " + currentTime + " diff: " + (currentTime - lastGMUpdateTime));
        if (immediate || currentTime - lastGMUpdateTime >= 56000l) {
            //Team
            String sendTeamName = new String(mPlayerID);
            String sendMissionNo = ""+currentMission;
            String sendLatitude = "";
            String sendLongitude = "";

            if(mLastLocation != null) {
                sendLatitude = "" + mLastLocation.getLatitude();
                sendLongitude = "" + mLastLocation.getLongitude();
                Log.i("SENDLOCATION", "Real location sent!");
            }

            new SendUpdateTask().execute(sendTeamName, sendMissionNo, sendLatitude, sendLongitude);

            lastGMUpdateTime = currentTime;
        }
    }

    private class SendUpdateTask extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... message){
            if (message.length != 4){
                return null;
            }
            else {
                String team = message[0];
                String mission = message[1];
                String lat = message[2];
                String lon = message[3];


                try {
                    JSONObject jGcmData = new JSONObject();
                    jGcmData.put("to", "/topics/global");

                    JSONObject data = new JSONObject();
                    data.put("team", team);
                    data.put("mission", mission);
                    data.put("lat", lat);
                    data.put("lon", lon);

                    jGcmData.put("data", data);
                    Log.i("SENDLOCATION", "Sending: " + team + " " + mission + " " + lat + " " + lon);

                    // Create connection to send GCM Message request.
                    URL url = new URL("https://android.googleapis.com/gcm/send");
                    HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                    conn.setRequestProperty("Authorization", "key="+getString(R.string.GCM_GMApp_API_KEY));
                    conn.setRequestProperty("Content-Type", "application/json");
                    conn.setRequestMethod("POST");
                    conn.setDoOutput(true);

                    Log.i("SENDLOCATION", "About to send update " + message + ".");

                    // Send GCM message content.
                    OutputStream outputStream = conn.getOutputStream();
                    outputStream.write(jGcmData.toString().getBytes());

                    Log.i("SENDLOCATION", "GCM Message sent.");

                    // Read GCM response.
                    InputStream inputStream = conn.getInputStream();
                    Scanner s = new Scanner(inputStream).useDelimiter("\\A");
                    if (s.hasNext()) {
                        String resp = s.next();
                        s.close();
                        Log.i("SENDLOCATION", "GCM Response: "+resp);
                        return resp;
                    }
                } catch (IOException ex) {
                    ex.printStackTrace();
                } catch (JSONException ex) {
                    ex.printStackTrace();
                }
                return null;
            }
        }
        protected void onPostExecute(String result) {
            Log.i("Message", "GCM Response: " + result);
        }
    }


    /** Events -- location and google API methods **/

    @Override
    public void onConnected(@Nullable Bundle bundle) { startLocationUpdates(); }

    @Override
    public void onConnectionSuspended(int i) { mGoogleApiClient.connect(); }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.i(this.getClass().getName(), "Connection failed: ConnectionResult.getErrorCode() = " + connectionResult.getErrorCode());
    }

    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;
        lastLocationUpdateTime = new Date().getTime();

        sendGMUpdate(false);

        if(curMission != null) {
            curMission.onLocationChanged(location);
        }
    }
}
