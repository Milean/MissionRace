package nl.milean.missionrace;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.missions_preferences), Context.MODE_PRIVATE);
        String savedPlayerID = sharedPref.getString(getString(R.string.playerID_key),null);
        if(savedPlayerID != null){
            EditText editText = (EditText) findViewById(R.id.mainPlayerID);
            editText.setText(savedPlayerID);
            editText.setEnabled(false);
//            startAssignments(savedPlayerID);
        }
    }

    @Override
    protected void onResume(){
        super.onResume();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_info) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /** Called when the user clicks "Start!" to go to the Assignments screen. */
    public void actionGotoAssignments(View view){
        EditText editText = (EditText) findViewById(R.id.mainPlayerID);
        String playerID = editText.getText().toString().trim();

        if(playerID.length()<=2){
            Toast toast = Toast.makeText(this, "Je teamnaam moet langer dan 2 tekens zijn.", Toast.LENGTH_SHORT);
            toast.show();
            editText.selectAll();
        }
        else{
            getSharedPreferences(getString(R.string.missions_preferences),MODE_PRIVATE).edit().putString(getString(R.string.playerID_key), playerID).apply();
            startAssignments(playerID);
        }
    }
    private void startAssignments(String playerID){
        Intent intent = new Intent(this, MissionDisplay.class);
        intent.putExtra(Constants.PLAYER_ID, playerID);
        startActivity(intent);
    }

    public void clearPreferences(View view){
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.missions_preferences), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.commit();
    }
}
