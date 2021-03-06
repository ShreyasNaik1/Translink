package ca.ubc.cs.cpsc210.translink.ui;

import android.content.Context;
import ca.ubc.cs.cpsc210.translink.BusesAreUs;
import ca.ubc.cs.cpsc210.translink.model.Route;
import ca.ubc.cs.cpsc210.translink.model.RoutePattern;
import ca.ubc.cs.cpsc210.translink.model.Stop;
import ca.ubc.cs.cpsc210.translink.model.StopManager;
import ca.ubc.cs.cpsc210.translink.util.Geometry;
import ca.ubc.cs.cpsc210.translink.util.LatLon;
import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;
import org.osmdroid.bonuspack.overlays.Polyline;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// A bus route drawer
public class BusRouteDrawer extends MapViewOverlay {
    /**
     * overlay used to display bus route legend text on a layer above the map
     */
    private BusRouteLegendOverlay busRouteLegendOverlay;
    /**
     * overlays used to plot bus routes
     */
    private List<Polyline> busRouteOverlays;

    /**
     * Constructor
     *
     * @param context the application context
     * @param mapView the map view
     */
    public BusRouteDrawer(Context context, MapView mapView) {
        super(context, mapView);
        busRouteLegendOverlay = createBusRouteLegendOverlay();
        busRouteOverlays = new ArrayList<>();
    }

    /**
     * Plot each visible segment of each route pattern of each route going through the selected stop.
     */
    public void plotRoutes(int zoomLevel) {
        Stop selected = StopManager.getInstance().getSelected();
        updateVisibleArea();
        busRouteOverlays.clear();
        busRouteLegendOverlay.clear();
        if (selected != null) {
            for (Route route : selected.getRoutes()) {
                int colour = busRouteLegendOverlay.add(route.getNumber());
                for (RoutePattern routePattern : route.getPatterns()) {
                    Polyline polyline = new Polyline(context);
                    List<GeoPoint> geoPoints = new ArrayList<>();
                    plotRoutesSinglePattern(routePattern, geoPoints, polyline, colour, zoomLevel);
                }
            }
        }
    }

    public void plotRoutesSinglePattern(RoutePattern routePattern, List<GeoPoint> geoPoints, Polyline polyline,
                                        int colour, int zoomLevel) {
        for (int i = 0; i < routePattern.getPath().size() - 1; i++) {
            LatLon first = routePattern.getPath().get(i);
            LatLon second = routePattern.getPath().get(i + 1);
            GeoPoint oneGeo = Geometry.gpFromLatLon(first);
            GeoPoint twoGeo = Geometry.gpFromLatLon(second);
            geoPoints.add(oneGeo);
            geoPoints.add(twoGeo);
            if (Geometry.rectangleIntersectsLine(northWest, southEast, first, second)) {
                polyline.setPoints(geoPoints);
                polyline.setColor(colour);
                polyline.setWidth(getLineWidth(zoomLevel));
                busRouteOverlays.add(polyline);
            }
        }
    }

    public List<Polyline> getBusRouteOverlays() {
        return Collections.unmodifiableList(busRouteOverlays);
    }

    public BusRouteLegendOverlay getBusRouteLegendOverlay() {
        return busRouteLegendOverlay;
    }


    /**
     * Create text overlay to display bus route colours
     */
    private BusRouteLegendOverlay createBusRouteLegendOverlay() {
        ResourceProxy rp = new DefaultResourceProxyImpl(context);
        return new BusRouteLegendOverlay(rp, BusesAreUs.dpiFactor());
    }

    /**
     * Get width of line used to plot bus route based on zoom level
     *
     * @param zoomLevel the zoom level of the map
     * @return width of line used to plot bus route
     */
    private float getLineWidth(int zoomLevel) {
        if (zoomLevel > 14) {
            return 7.0f * BusesAreUs.dpiFactor();
        } else if (zoomLevel > 10) {
            return 5.0f * BusesAreUs.dpiFactor();
        } else {
            return 2.0f * BusesAreUs.dpiFactor();
        }
    }
}
