# react-native-baidu-map [![npm version](https://img.shields.io/npm/v/react-native-baidu-map.svg?style=flat)](https://www.npmjs.com/package/react-native-baidu-map)

Baidu Map SDK modules and view for React Native(Android & IOS), support react native 0.40+

百度地图 React Native 模块，支持 react native 0.40+

![Android](https://raw.githubusercontent.com/lovebing/react-native-baidu-map/master/images/android.jpg)
![IOS](https://raw.githubusercontent.com/lovebing/react-native-baidu-map/master/images/ios.jpg)

### Install 安装
    npm install react-native-baidu-map --save
### Import 导入

#### Android Studio
- settings.gradle `
include ':react-native-baidu-map'
project(':react-native-baidu-map').projectDir = new File(settingsDir, '../node_modules/react-native-baidu-map/android')`

- build.gradle `compile project(':react-native-baidu-map')`

- MainApplication`new BaiduMapPackage(getApplicationContext())`
- AndroidMainifest.xml `<meta-data
            android:name="com.baidu.lbsapi.API_KEY" android:value="xx"/>`

    `<service
        android:name="com.baidu.trace.LBSTraceService"
        android:enabled="true"
        android:process=":remote">
    </service>`

    权限：
    `<uses-permission android:name="android.permission.ACCESS_LOCATION_EXTRA_COMMANDS" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.READ_PHONE_STATE" />
    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
    <uses-permission android:name="android.permission.ACCESS_NETWORK_STATE"/>
    <uses-permission android:name="com.android.launcher.permission.READ_SETTINGS" />
    <uses-permission android:name="android.permission.WAKE_LOCK"/>
    <uses-permission android:name="android.permission.CHANGE_WIFI_STATE" />
    <uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
    <uses-permission android:name="android.permission.GET_TASKS" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE"/>
    <uses-permission android:name="android.permission.WRITE_SETTINGS" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.MOUNT_UNMOUNT_FILESYSTEMS" />
    <uses-permission android:name="android.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS"/>`

#### Xcode
- Project navigator->Libraries->Add Files to 选择 react-native-baidu-map/ios/RCTBaiduMap.xcodeproj
- Project navigator->Build Phases->Link Binary With Libraries 加入 libRCTBaiduMap.a
- Project navigator->Build Settings->Search Paths， Framework search paths 添加 react-native-baidu-map/ios/lib，Header search paths 添加 react-native-baidu-map/ios/RCTBaiduMap
- 添加依赖, react-native-baidu-map/ios/lib 下的全部 framwordk， CoreLocation.framework和QuartzCore.framework、OpenGLES.framework、SystemConfiguration.framework、CoreGraphics.framework、Security.framework、libsqlite3.0.tbd（xcode7以前为 libsqlite3.0.dylib）、CoreTelephony.framework 、libstdc++.6.0.9.tbd（xcode7以前为libstdc++.6.0.9.dylib）
- 添加 BaiduMapAPI_Map.framework/Resources/mapapi.bundle

- 其它一些注意事项可参考百度地图LBS文档

##### AppDelegate.m init 初始化
    #import "RCTBaiduMapViewManager.h"
    - (BOOL)application:(UIApplication *)application didFinishLaunchingWithOptions:(NSDictionary *)launchOptions
    {
        ...
        [RCTBaiduMapViewManager initSDK:@"api key"];
        ...
    }

### Usage 使用方法

    import { MapView, MapTypes, MapModule, Geolocation,Track } from 'react-native-baidu-map

#### MapView Props 属性
| Name                    | Type  | Default  | Extra
| ----------------------- |:-----:| :-------:| -------
| zoomControlsVisible     | bool  | true     | Android only
| trafficEnabled          | bool  | false    |
| baiduHeatMapEnabled     | bool  | false    | 开启热力图
| mapType                 | number| 1        |
| zoom                    | number| 10       | 地图缩放
| center                  | object| null     | {latitude: 0, longitude: 0}
| marker                  | object| null     | {latitude: 0, longitude: 0, title: ''}
| markers                 | array | []       | [marker, maker]
| polyline                | array | undefined| Android only [{latitude:0.0,longitude:0.0}] 至少两个点 画线/画轨迹
| polylineColor           | string | blue | Android only '#FF0000' 格式必须这样 线颜色
| polylineWidth           | number | 10 | Android only 线宽
| onMapStatusChangeStart  | func  | undefined| Android only
| onMapStatusChange       | func  | undefined|
| onMapStatusChangeFinish | func  | undefined| Android only
| onMapLoaded             | func  | undefined|
| onMapClick              | func  | undefined|
| onMapDoubleClick        | func  | undefined|
| onMarkerClick           | func  | undefined|
| onMapPoiClick           | func  | undefined|

#### MapModule Methods (Deprecated)
    setMarker(double lat, double lng)
    setMapType(int mapType)
    moveToCenter(double lat, double lng, float zoom)
    Promise reverseGeoCode(double lat, double lng)
    Promise reverseGeoCodeGPS(double lat, double lng)
    Promise geocode(String city, String addr),
    Promise getCurrentPosition()

#### Geolocation Methods

| Method                    | Result
| ------------------------- | -------
| Promise reverseGeoCode(double lat, double lng) | `{"address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}`
| Promise reverseGeoCodeGPS(double lat, double lng) |  `{"address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}`
| Promise geocode(String city, String addr) | {"latitude": 0.0, "longitude": 0.0}
| Promise getCurrentPosition() | IOS: `{"latitude": 0.0, "longitude": 0.0, "address": "", "province": "", "cityCode": "", "city": "", "district": "", "streetName": "", "streetNumber": ""}` Android: `{"latitude": 0.0, "longitude": 0.0, "direction": -1, "altitude": 0.0, "radius": 0.0, "address": "", "countryCode": "", "country": "", "province": "", "cityCode": "", "city": "", "district": "", "street": "", "streetNumber": "", "buildingId": "", "buildingName": ""}`

#### Track Methods
|Method|Result|Remark
|------|-------|-----
| initTrace (String name)||百度地图的鉴权需要时间，因而可以在componentWillMount中初始化鹰眼服务
| Promise startTrack() | Android:'{"code":"","message":""}' | 启动鹰眼服务
| Promise stopTrack() | Android:'{"code":"","message":""}' | 关闭鹰眼服务并关闭采集信息
| Promise startGather() | Android:'{"code":"","message":""}'| 开始采集信息
| Promise stopGather() | Android:'{"code":"","message":""}' | 关闭采集信息
| Promise queryHistoryTrack(String name,int startTime,int endTime) | Android:'{"trackPoints": [{"point": {"coorType": "bd09ll","speed": 0,"height": 0,"direction": 0,"radius": 0,"locTime": 0,"longitude": 0.0,"latitude": 0.0},"objectName": null,"createTime": "2017-05-0810: 41: 26"}],"startPoint": {"coorType": "bd09ll","speed": 0,"height": 0,"direction": 0,"radius": 0,"locTime": 0,"longitude": 0.0,"latitude": 0.0},"endPoint": {"coorType": "bd09ll","speed": 0,"height": 0,"direction": 0,"radius": 0,"locTime": 0.0,"longitude": 0.0,"latitude": 0.0},"tollDistance": 0,"entityName": "default","size": 0,"code": 0,"distance": 0,"total": 0,"message": "成功"}'| 获取name的历史轨迹
| Promise queryEntityList(int activeTime) | Android:'{"message": "成功","status": 0,"size": 0,"total": 0,"entities": [{"latestLocation": {"floor": "","coorType": "bd09ll","speed": 0,"height": 0,"direction": 0,"radius": 0,"locTime": 0,"longitude": 0.0,"latitude": 0.0},"modifyTime": "2017-05-08 14:54:24","entityName": "default","entityDesc": null,"createTime": "2017-05-08 10:41:26"}]}' | 获取活跃用户（以activeTime为限，在这个时间之后有上传位置数据的即为活跃用户，否则为离线）
| Promise queryEntityList(int inactiveTime) | Android:同上 | 获取不活跃用户（以inactiveTime为限，在这个时间之后有上传位置数据的即为活跃用户，否则为离线）