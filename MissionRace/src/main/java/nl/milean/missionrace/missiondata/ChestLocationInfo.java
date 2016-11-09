package nl.milean.missionrace.missiondata;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Tchakkazulu on 13/06/2016.
 */
public class ChestLocationInfo implements Serializable {
    public final double mLat;
    public final double mLon;
    public final double mThreshold;
    public final String mName;
    public final Chest[] chests;

    public ChestLocationInfo(String name, double lat, double lon, double threshold, int... chestDescr) {
        assert(chestDescr.length % 3 == 0);
        mLat = lat;
        mLon = lon;
        mName = name;
        this.mThreshold = threshold;
        int total = chestDescr.length / 3;
        this.chests = new Chest[total];

        for (int i = 0; i < total; i ++) {
            int lock = chestDescr[3*i];
            int col  = chestDescr[3*i + 1];
            int cont = chestDescr[3*i + 2];
            chests[i] = new Chest(lock,col,cont);
        }
    }

    public static class Chest implements Serializable {
        public final int lock;
        public final int col;
        public final int contents;

        public Chest(int lock, int col, int contents) {
            this.lock = lock;
            this.col = col;
            this.contents = contents;
        }
    }

    public static final int FREE = 0;
    public static final int LOCK = 1;
    public static final int OPEN = 2;

    public static final int RED = 0;
    public static final int YELLOW = 1;
    public static final int BLUE = 2;
    public static final int PARTS = 3; // rendered as green
    public static final int A = 3;
    public static final int B = 4;
    public static final int C = 5;
    public static final int EGG = 6;

}
