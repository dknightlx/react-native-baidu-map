package org.lovebing.reactnative.baidumap;

/**
 * Created by dk on 2017/5/11.
 */

import android.graphics.Color;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.Polyline;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.facebook.react.bridge.ReadableMap;

import java.util.List;

public class PolylineUtil {
    private static Polyline polyline;
    private static Boolean hasLine = false;
    private static int lineColor = Color.BLUE;
    private static int lineWidth = 5;

    public static void setLineColor(int color){
        lineColor = color;
    }
    public static void setLineWidth(int width){
        lineWidth = width;
    }
    public static void drawTrackLine(BaiduMap map, List<LatLng> points){
        if(hasLine){
            polyline.remove();
        }
        OverlayOptions oPolyline = new PolylineOptions().width(lineWidth).color(lineColor).points(points);
        polyline = (Polyline) map.addOverlay(oPolyline);
        hasLine = true;

    }
    private static LatLng getLatLngFromOption(ReadableMap option) {
        double latitude = option.getDouble("latitude");
        double longitude = option.getDouble("longitude");
        return new LatLng(latitude, longitude);

    }
}
