package org.y20k.transistor;

import java.io.Serializable;

public class Station implements Serializable {
    private static final long serialVersionUID = 1L;

    private String streamUri;
    private String stationName;
    private String stationSite;
    private String stationImage;

    public Station(String streamUri, String stationName, String stationSite, String stationImage) {
        this.streamUri = streamUri;
        this.stationName = stationName;
        this.stationSite = stationSite;
        this.stationImage = stationImage;
    }

    public Station(String streamUri) {
        this.streamUri = streamUri;
    }

    public String getStreamUri() {
        return streamUri;
    }

    public String getStationName() {
        return stationName;
    }

    public String getStationSite() {
        return stationSite;
    }

    public String getStationImage() {
        return stationImage;
    }
}
