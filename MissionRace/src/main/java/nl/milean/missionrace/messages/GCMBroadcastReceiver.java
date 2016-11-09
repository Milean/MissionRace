/**
 * Copyright 2015 Google Inc. All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package nl.milean.missionrace.messages;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;
import android.widget.EditText;

import com.google.android.gms.gcm.GcmListenerService;

import nl.milean.missionrace.R;

public class GCMBroadcastReceiver extends GcmListenerService {

    private static final String TAG = "GMBroadcastReceiver";

    /**
     * Called when message is received.
     *
     * @param from SenderID of the sender.
     * @param data Data bundle containing message data as key/value pairs.
     *             For Set of keys use data.keySet().
     */
    // [START receive_message]
    @Override
    public void onMessageReceived(String from, Bundle data) {
        String team = data.getString("team");
        String message = data.getString("message");
        int assignment = Integer.parseInt(data.getString("assignment"));
        Log.d(TAG, "From: " + from);
        Log.d(TAG, "For: " + team);
        Log.d(TAG, "Message: " + message);
        Log.d(TAG, "Assignment: " + assignment);

        if(message != null) {
            SharedPreferences sharedPref = getSharedPreferences(getString(R.string.missions_preferences), Context.MODE_PRIVATE);
            String savedPlayerID = sharedPref.getString(getString(R.string.playerID_key), null);

            if (savedPlayerID != null && team != null && savedPlayerID.equalsIgnoreCase(team)) {
                Intent messageIntent = new Intent("gcm_message_event");
                messageIntent.putExtra("message", message);
                messageIntent.putExtra("assignment", assignment);
                LocalBroadcastManager.getInstance(this).sendBroadcast(messageIntent);
            }
        }
    }

}
