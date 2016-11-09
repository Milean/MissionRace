package nl.milean.missionrace.missions;

import android.app.AlertDialog;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.HashMap;
import java.util.Map;

import nl.milean.missionrace.Constants;
import nl.milean.missionrace.R;
import nl.milean.missionrace.missiondata.ChestLocationInfo;
import nl.milean.missionrace.missiondata.Mission;

public class ChestFragment extends MissionFragment implements OnMapReadyCallback {
    protected AlertDialog mChestPopup;
    protected AlertDialog mItemPopup;
    protected GoogleMap mMap;
    protected Marker mSelf;

    protected boolean mAtLoc;
    protected ChestLocationInfo[] mLocs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.content_mission_chests, container, false);

        Mission mission = (Mission) getArguments().getSerializable(Constants.MISSION_INTENT_EXTRA);
        initChestData(mission);

        // TODO: mission.getData("text") nog ergens wegzetten.

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.chests_map);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        updateInventory();
    }

    @Override
    public void onResume() {
        super.onResume();
        killChestPopup();
    }

    private void initChestData(Mission mission){
        mLocs = (ChestLocationInfo[]) mission.getData("chests");
        if (!hasData("started")) {
            Map<String,String> initial = new HashMap<String,String>();
            initial.put("started","1");
            initial.put("inv" + ChestLocationInfo.RED,"0");
            initial.put("inv" + ChestLocationInfo.BLUE,"0");
            initial.put("inv" + ChestLocationInfo.YELLOW,"0");
            initial.put("inv" + ChestLocationInfo.A,"0");
            initial.put("inv" + ChestLocationInfo.B,"0");
            initial.put("inv" + ChestLocationInfo.C,"0");
            for (int loc = 0; loc < mLocs.length; loc++) {
                for (int chest = 0; chest < mLocs[loc].chests.length; chest++) {
                    initial.put("l" + loc + "c" + chest,"0");
                }
            }
            saveData(initial);
        }
    }

    private void updateInventory() {
        TextView keyR = (TextView) getView().findViewById(R.id.chests_keyR_count);
        TextView keyY = (TextView) getView().findViewById(R.id.chests_keyY_count);
        TextView keyB = (TextView) getView().findViewById(R.id.chests_keyB_count);
        ImageView partA = (ImageView) getView().findViewById(R.id.chests_partA);
        ImageView partB = (ImageView) getView().findViewById(R.id.chests_partB);
        ImageView partC = (ImageView) getView().findViewById(R.id.chests_partC);

        keyR.setText("" + countItem(ChestLocationInfo.RED));
        keyY.setText("" + countItem(ChestLocationInfo.YELLOW));
        keyB.setText("" + countItem(ChestLocationInfo.BLUE));

        boolean hasA = checkItem(ChestLocationInfo.A);
        boolean hasB = checkItem(ChestLocationInfo.B);
        boolean hasC = checkItem(ChestLocationInfo.C);

        int resA, resB, resC;
        if (hasA) { resA = R.drawable.part_a; } else {resA = R.drawable.part_a_nope; }
        if (hasB) { resB = R.drawable.part_b; } else {resB = R.drawable.part_b_nope; }
        if (hasC) { resC = R.drawable.part_c; } else {resC = R.drawable.part_c_nope; }

        partA.setImageResource(resA);
        partB.setImageResource(resB);
        partC.setImageResource(resC);
    }

    private void arriveAt(int i) {

        mAtLoc = true;

        if (mItemPopup != null && mItemPopup.isShowing()) {
            return;
        }

        if (mChestPopup != null && mChestPopup.isShowing()) {
            return;
        }

        ScrollView view = new ScrollView(getActivity());
        LinearLayout chestList = new LinearLayout(getActivity());
        view.addView(chestList);
        chestList.setOrientation(LinearLayout.VERTICAL);
        FrameLayout.LayoutParams params = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        chestList.setLayoutParams(params);

        for (int j = 0; j < mLocs[i].chests.length; j++) {
            ChestLocationInfo.Chest chest = mLocs[i].chests[j];
            chestList.addView(mkChestView(i, j, chest));
        }

        AlertDialog.Builder diag = new AlertDialog.Builder(getActivity());
        diag.setTitle(mLocs[i].mName);
        diag.setView(view);
        diag.setCancelable(false);
        mChestPopup = diag.create();
        mChestPopup.show();
    }

    private static int[][] CHESTS = new int[][]{new int[]{R.drawable.chest_r_free, R.drawable.chest_r_lock, R.drawable.chest_r_open},
                                                new int[]{R.drawable.chest_y_free, R.drawable.chest_y_lock, R.drawable.chest_y_open},
                                                new int[]{R.drawable.chest_b_free, R.drawable.chest_b_lock, R.drawable.chest_b_open},
                                                new int[]{-1                     , R.drawable.chest_g_lock, R.drawable.chest_g_open}}; // No free greens.

    private static String[] NOKEY = new String[]{"Geen rode sleutel","Geen gele sleutel","Geen blauwe sleutel","Nog niet genoeg klokken horen luiden"};

    private View mkChestView(final int loc, final int chest, final ChestLocationInfo.Chest info) {
        LinearLayout row = new LinearLayout(getActivity());
        row.setOrientation(LinearLayout.HORIZONTAL);


        final int chestState;
        if (Integer.valueOf(loadData().get("l" + loc + "c" + chest)) == 1) {
            chestState = ChestLocationInfo.OPEN;
        } else {
            chestState = info.lock;
        }
        final int chestCol = info.col;
        final int chestRes = CHESTS[chestCol][chestState];

        ImageView chestImg = new ImageView(getActivity());
        chestImg.setImageResource(chestRes);
        row.addView(chestImg);

        Button button = new Button(getActivity());
        switch (chestState) {
            case ChestLocationInfo.OPEN: button.setText("Kist is al open");
                                         button.setEnabled(false);
                                         break;
            case ChestLocationInfo.LOCK: if (!canOpen(chestCol)) {
                                           button.setText(NOKEY[chestCol]);
                                           button.setEnabled(false);
                                           break;
                                         }
                                         // Fall-through wanneer we de kist kunnen openen.
            case ChestLocationInfo.FREE: button.setText("Open deze kist");
                                         button.setOnClickListener(new View.OnClickListener() {
                                             @Override
                                             public void onClick(View v) {
                                                 openChest(loc, chest);
                                             }
                                         });

        }
        row.addView(button);

        return row;
    }


    private void unVisit() {
        killChestPopup();
        mAtLoc = false;
    }

    /* Inventory methods */

    private void getItem(int item) {
        String key = "inv" + item;
        int curCount = Integer.valueOf(loadData().get(key));
        saveData(key,"" + (curCount + 1));
    }

    private void useItem(int item) {
        String key = "inv" + item;
        int curCount = Integer.valueOf(loadData().get(key));
        saveData(key,"" + (curCount - 1));
    }

    private boolean checkItem(int item) {
        return countItem(item) > 0;
    }

    private int countItem(int item) {
        String key = "inv" + item;
        int curCount = Integer.valueOf(loadData().get(key));
        return curCount;
    }

    /* Chest methods */

    private boolean canOpen(int chestCol) {
        if (chestCol == ChestLocationInfo.PARTS) {
            return checkItem(ChestLocationInfo.A) &&
                   checkItem(ChestLocationInfo.B) &&
                   checkItem(ChestLocationInfo.C);
        } else {
            return checkItem(chestCol);
        }
    }

    private void openChest(int loc, int chest) {
        ChestLocationInfo.Chest info = mLocs[loc].chests[chest];
        saveData("l" + loc + "c" + chest,"1");
        if (info.lock == ChestLocationInfo.LOCK && info.col != ChestLocationInfo.PARTS) {
            useItem(info.col);
        }
        killChestPopup();
        youGet(info.contents);
        updateInventory();
    }

    private static String[] ITEMNAME = new String[]{"Rode sleutel","Gele sleutel","Blauwe sleutel",
            "Een klok! *BIM*","Een klok! *BAM*","Een klok! *BOM*",
            "De klepel!"};
    private static int[] ITEMS = new int[]{R.drawable.key_red, R.drawable.key_yellow, R.drawable.key_blue,
                                           R.drawable.part_a, R.drawable.part_b, R.drawable.part_c, R.drawable.bell};

    private void youGet(int item) {
        ImageView view = new ImageView(getActivity());
        view.setImageResource(ITEMS[item]);

        AlertDialog.Builder diag = new AlertDialog.Builder(getActivity());
        diag.setTitle(ITEMNAME[item]);
        diag.setView(view);
        diag.setPositiveButton(android.R.string.ok, null);
        mItemPopup = diag.create();
        mItemPopup.show();

        if (item == ChestLocationInfo.EGG) {
            completeMission();
        } else {
            getItem(item);
        }
    }

    private void killChestPopup() {
        if (mChestPopup != null) {
            mChestPopup.dismiss();
            mChestPopup = null;
        }
    }

    /* Location methods */

    @Override
    public void onLocationChanged(Location location) {
        // Map has not loaded yet, let's not do anything rash here.
        if (mMap == null) {
            return;
        }

        mSelf.setPosition(new LatLng(location.getLatitude(), location.getLongitude()));

        if (mLocs == null) { return; }

        for (int i = 0; i < mLocs.length; i++) {
            ChestLocationInfo info = mLocs[i];
            Location destLoc = new Location("chests");
            destLoc.setLatitude(info.mLat);
            destLoc.setLongitude(info.mLon);
            if (location.distanceTo(destLoc) <= info.mThreshold) {
                arriveAt(i);
                return;
            }
        }
        if (mAtLoc) { unVisit(); }
    }

    /* Maps methods */

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        for (ChestLocationInfo info : mLocs) {
            LatLng pos = new LatLng(info.mLat,info.mLon);
            // TODO: beter icoontje voor kistjeslocatie.
            mMap.addMarker(new MarkerOptions().position(pos));
        }

        // TODO: beter icoontje voor deelnemerlocatie.
        mSelf = mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

        adjustMap();
    }

    private void adjustMap() {
        LatLngBounds.Builder builder = new LatLngBounds.Builder();
        for (ChestLocationInfo info : mLocs) {
            LatLng pos = new LatLng(info.mLat,info.mLon);
            builder.include(pos);
        }

        LatLngBounds bounds = builder.build();

        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, 100);
        mMap.moveCamera(cu);

    }
}
