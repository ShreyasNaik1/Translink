package ca.ubc.cs.cpsc210.translink.providers;

import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import static ca.ubc.cs.cpsc210.translink.auth.TranslinkToken.TRANSLINK_API_KEY;

/**
 * Wrapper for Translink Bus Location Data Provider
 */
public class HttpBusLocationDataProvider extends AbstractHttpDataProvider {
    private Stop stop;

    public HttpBusLocationDataProvider(Stop stop) {
        super();
        this.stop = stop;
    }

    @Override
    /**
     * Produces URL used to query Translink web service for locations of buses serving
     * the stop specified in call to constructor.
     *
     * @returns URL to query Translink web service for arrival data
     */
    protected URL getUrl() throws MalformedURLException {
        String url = "";
        int stopNo = stop.getNumber();
        url = "http://api.translink.ca/rttiapi/v1/buses?apikey="
                + TRANSLINK_API_KEY
                + "&stopNo="
                + stopNo;
        System.out.println(url);
        return new URL(url);
    }

    @Override
    public byte[] dataSourceToBytes() throws IOException {
        return new byte[0];
    }
}
