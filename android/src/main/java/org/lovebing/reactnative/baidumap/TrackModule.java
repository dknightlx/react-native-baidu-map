package org.lovebing.reactnative.baidumap;

import android.support.annotation.Nullable;
import android.util.Log;

import com.baidu.trace.LBSTraceClient;
import com.baidu.trace.Trace;
import com.baidu.trace.api.entity.EntityInfo;
import com.baidu.trace.api.entity.EntityListRequest;
import com.baidu.trace.api.entity.EntityListResponse;
import com.baidu.trace.api.entity.FilterCondition;
import com.baidu.trace.api.entity.OnEntityListener;
import com.baidu.trace.api.track.DistanceResponse;
import com.baidu.trace.api.track.HistoryTrackRequest;
import com.baidu.trace.api.track.HistoryTrackResponse;
import com.baidu.trace.api.track.LatestPointResponse;
import com.baidu.trace.api.track.OnTrackListener;
import com.baidu.trace.api.track.TrackPoint;
import com.baidu.trace.model.CoordType;
import com.baidu.trace.model.LatLng;
import com.baidu.trace.model.OnTraceListener;
import com.baidu.trace.model.Point;
import com.baidu.trace.model.ProcessOption;
import com.baidu.trace.model.PushMessage;
import com.baidu.trace.model.StatusCodes;
import com.baidu.trace.model.TransportMode;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.WritableArray;
import com.facebook.react.bridge.WritableMap;

import java.util.List;

/**
 * Created by dk on 2017/5/11.
 */

public class TrackModule extends BaseModule {
    private static String REACT_CLASS ="BaiduTrackModule";
    private static String TAG = "track";

    private Trace mTrace;
    private String entityName = "default";
    private LBSTraceClient mTraceClient;
    private OnTraceListener mTraceListener;
    private OnTrackListener mTrackListener;
    private OnEntityListener entityListener;

    public TrackModule(ReactApplicationContext reactContext) {
        super(reactContext);
        context = reactContext;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }

    @ReactMethod
    public void startTrack() {
        if(mTraceClient == null){
            Log.d(TAG, "startTrack: init");
            initTrace(entityName);
        }
        Log.d(TAG, "startTrack: start");
        mTraceClient.startTrace(mTrace,mTraceListener);
    }
    @ReactMethod
    public void startGather() {
        Log.d(TAG, "startGather: ");
        if(mTraceClient == null){
            initTrace(entityName);
        }
        mTraceClient.startGather(mTraceListener);
    }
    @ReactMethod
    public void stopGather() {
        Log.d(TAG, "stopGather: ");
        if(mTraceClient == null){
            initTrace(entityName);
        }
        mTraceClient.stopGather(mTraceListener);
    }
    @ReactMethod
    public void stopTrack() {
        Log.d(TAG, "stopTrack: ");
        if(mTraceClient == null){
            initTrace(entityName);
        }
        mTraceClient.stopTrace(mTrace,mTraceListener);
    }
    @ReactMethod
    public void queryHistoryTrack(String name,int startTime,int endTime){
        int tag = (int) (System.currentTimeMillis()/1000);
        HistoryTrackRequest historyTrackRequest = new HistoryTrackRequest(tag,TrackConstants.SERVICE_ID,name);
        historyTrackRequest.setStartTime(startTime);
        historyTrackRequest.setEndTime(endTime);
        historyTrackRequest.setProcessed(true);

        // 创建纠偏选项实例
        ProcessOption processOption = new ProcessOption();
        // 设置需要去噪
        processOption.setNeedDenoise(true);
        // 设置需要抽稀
        processOption.setNeedVacuate(false);
        // 设置需要绑路
        processOption.setNeedMapMatch(false);
        // 设置精度过滤值(定位精度大于100米的过滤掉)
//        processOption.setRadiusThreshold(100);
        // 设置交通方式为驾车
        processOption.setTransportMode(TransportMode.walking);
        // 设置纠偏选项
        historyTrackRequest.setProcessOption(processOption);
        mTraceClient.queryHistoryTrack(historyTrackRequest,mTrackListener);
    }
    //init trace module
    @ReactMethod
    public void initTrace(String name){
        if(name!=null&&name.equals("")&&name.equals("default")){
            entityName = name;
        }
        mTrace = new Trace(TrackConstants.SERVICE_ID,entityName,TrackConstants.IS_NEED_OBJECT_STORAGE);
        mTraceClient = new LBSTraceClient(getReactApplicationContext());
        mTraceClient.setInterval(TrackConstants.GATHER_INTERVAL,TrackConstants.PACK_INTERVAL);
        mTraceListener = new OnTraceListener() {
            @Override
            public void onStartTraceCallback(int i, String s) {
                Log.d(TAG, "onStartTraceCallback: "+s);
                WritableMap params = Arguments.createMap();
                params.putInt("code",i);
                params.putString("message",s);
                sendEvent("onStartTraceCallback", params);
            }

            @Override
            public void onStopTraceCallback(int i, String s) {
                Log.d(TAG, "onStopTraceCallback: "+s);
                WritableMap params = Arguments.createMap();
                params.putInt("code",i);
                params.putString("message",s);
                sendEvent("onStopTraceCallback", params);
            }

            @Override
            public void onStartGatherCallback(int i, String s) {
                Log.d(TAG, "onStartGatherCallback: "+s);
                WritableMap params = Arguments.createMap();
                params.putInt("code",i);
                params.putString("message",s);
                sendEvent("onStartGatherCallback", params);
            }

            @Override
            public void onStopGatherCallback(int i, String s) {
                Log.d(TAG, "onStopGatherCallback: "+s);
                WritableMap params = Arguments.createMap();
                params.putInt("code",i);
                params.putString("message",s);
                sendEvent("onStopGatherCallback", params);
            }

            @Override
            public void onPushCallback(byte b, PushMessage pushMessage) {
                Log.d(TAG, "onPushCallback: "+pushMessage.toString());
            }
        };
        mTrackListener = new OnTrackListener() {
            @Override
            public void onHistoryTrackCallback(HistoryTrackResponse historyTrackResponse) {
                super.onHistoryTrackCallback(historyTrackResponse);
                if(historyTrackResponse.getStatus() == StatusCodes.SUCCESS){
                    List<TrackPoint> trackPoints = historyTrackResponse.getTrackPoints();
                    WritableArray trackPointsFormat = Arguments.createArray();
                    if(trackPoints!=null&&historyTrackResponse.getSize()>0){
                        for(TrackPoint p:trackPoints){
                            WritableMap item = Arguments.createMap();
                            item.putString("createTime",p.getCreateTime());
                            item.putString("objectName",p.getObjectName());
                            item.putMap("point",queryPoint(p));
                            trackPointsFormat.pushMap(item);
                        }
                    }

                    WritableMap endPoints = queryPoint(historyTrackResponse.getEndPoint());
                    WritableMap startPoints = queryPoint(historyTrackResponse.getStartPoint());
                    WritableMap params = Arguments.createMap();
                    params.putString("message",historyTrackResponse.getMessage());
                    params.putInt("total",historyTrackResponse.getTotal());
                    params.putInt("size",historyTrackResponse.getSize());
                    params.putString("entityName",historyTrackResponse.getEntityName());
                    params.putDouble("distance",historyTrackResponse.getDistance());
                    params.putDouble("tollDistance",historyTrackResponse.getTollDistance());
                    params.putMap("endPoint",endPoints);
                    params.putInt("code",historyTrackResponse.getStatus());
                    params.putMap("startPoint",startPoints);
                    params.putArray("trackPoints",trackPointsFormat);
                    sendEvent("onHistoryTrackCallback", params);
                }else{
                    WritableMap params = Arguments.createMap();
                    params.putString("message",historyTrackResponse.getMessage());
                    params.putInt("code",historyTrackResponse.getStatus());
                    sendEvent("onHistoryTrackCallback", params);
                }

            }

            @Override
            public void onDistanceCallback(DistanceResponse distanceResponse) {
                super.onDistanceCallback(distanceResponse);
                Log.d(TAG, "onDistanceCallback: "+"距离返回");
            }

            @Override
            public void onLatestPointCallback(LatestPointResponse latestPointResponse) {
                super.onLatestPointCallback(latestPointResponse);
                Log.d(TAG, "onLatestPointCallback: "+"最后点返回");
            }
        };

        // 初始化监听器
        entityListener = new OnEntityListener() {
            @Override
            public void onEntityListCallback(EntityListResponse response) {
                WritableMap params = Arguments.createMap();

                if(response.getStatus() == StatusCodes.SUCCESS){
                    WritableArray entities = Arguments.createArray();

                    List<EntityInfo> entitys = response.getEntities();
                    for(EntityInfo entityInfo:entitys){
                        WritableMap item = Arguments.createMap();
                        WritableMap latest = Arguments.createMap();
                        latest = queryPoint(entityInfo.getLatestLocation());
                        latest.putString("floor",entityInfo.getLatestLocation().getFloor());
                        item.putString("createTime",entityInfo.getCreateTime());
                        item.putString("entityDesc",entityInfo.getEntityDesc());
                        item.putString("entityName",entityInfo.getEntityName());
                        item.putString("modifyTime",entityInfo.getModifyTime());
                        item.putMap("latestLocation",latest);

                        entities.pushMap(item);
                    }
                    params.putArray("entities",entities);
                    params.putInt("total",response.getTotal());
                    params.putInt("size",response.getSize());
                }


                params.putInt("status",response.getStatus());
                params.putString("message",response.getMessage());

                sendEvent("onEntityListCallback",params);
            }
        };
    };

    @ReactMethod
    public void queryEntityList(@Nullable int activeTime){
        int tag = (int) (System.currentTimeMillis()/1000);
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setActiveTime(activeTime);
        CoordType coordTypeOutput = CoordType.bd09ll;
        EntityListRequest request = new EntityListRequest(tag,TrackConstants.SERVICE_ID,filterCondition,coordTypeOutput,1,100);

        mTraceClient.queryEntityList(request,entityListener);
    }
    @ReactMethod
    public void queryInActiveEntityList(@Nullable int inactiveTime){
        int tag = (int) (System.currentTimeMillis()/1000);
        FilterCondition filterCondition = new FilterCondition();
        filterCondition.setInActiveTime(inactiveTime);
        CoordType coordTypeOutput = CoordType.bd09ll;
        EntityListRequest request = new EntityListRequest(tag,TrackConstants.SERVICE_ID,filterCondition,coordTypeOutput,1,100);

        mTraceClient.queryEntityList(request,entityListener);
    }

    private WritableMap queryPoint(Point p){
        try{
            WritableMap points = Arguments.createMap();
            LatLng latLng = p.getLocation();
            points.putDouble("latitude",latLng.getLatitude());
            points.putDouble("longitude",latLng.getLongitude());
            points.putInt("locTime", (int)p.getLocTime());
            points.putInt("direction",p.getDirection());
            points.putDouble("height",p.getHeight());
            points.putString("coorType",p.getCoordType().toString());
            points.putDouble("radius",p.getRadius());
            points.putDouble("speed",p.getSpeed());
            return points;
        }catch (NullPointerException e){
            WritableMap points = Arguments.createMap();
            points.putString("message","获取失败");
            return points;
        }

    };
}
