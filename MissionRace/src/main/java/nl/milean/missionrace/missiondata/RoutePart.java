package nl.milean.missionrace.missiondata;

/**
 * Created by Tchakkazulu on 13/05/2016.
 */
public class RoutePart {

    public final Double lat;
    public final Double lon;
    public final String extra;

    public RoutePart(Double lat, Double lon, String extra) {
        this.lat = lat;
        this.lon = lon;
        this.extra = extra;
    }
}
