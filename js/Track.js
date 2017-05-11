import {
  requireNativeComponent,
  NativeModules,
  Platform,
  DeviceEventEmitter
} from 'react-native';

import React, {
  Component,
  PropTypes
} from 'react';


const _module = NativeModules.BaiduTrackModule;

export default {
    queryEntityList(activeTime){
        return new Promise((resolve,reject)=>{
            try{
                _module.queryEntityList(activeTime);
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onEntityListCallback', resp => {
                resolve(resp);
            });
        });
    },
    queryInActiveEntityList(inactiveTime){
        return new Promise((resolve,reject)=>{
            try{
                _module.queryInActiveEntityList(inactiveTime);
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onEntityListCallback', resp => {
                resolve(resp);
            });
        });
    },
    queryHistoryTrack(name,startTime,endTime){
        return new Promise((resolve,reject)=>{
            try{
                _module.queryHistoryTrack(name,startTime,endTime);
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onHistoryTrackCallback', resp => {
                resolve(resp);
            });
        });
    },
    initTrace(entity){
        return new Promise((resolve,reject)=>{
            try{
                _module.initTrace(entity);
            } catch(e){
                reject(e);
                return;
            }
            
        });
    },
    startTrack(){
        return new Promise((resolve,reject)=>{
            try{
                _module.startTrack();
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onStartTraceCallback', resp => {
                resolve(resp);
            });
        });
        
    },
    stopTrack(){
        return new Promise((resolve,reject)=>{
            try{
                _module.stopTrack();
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onStopTraceCallback', resp => {
                resolve(resp);
            });
        });
    },
    startGather(){
        return new Promise((resolve,reject)=>{
            try{
                _module.startGather();
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onStartGatherCallback', resp => {
                resolve(resp);
            });
        });
        
    },
    stopGather(){
        return new Promise((resolve,reject)=>{
            try{
                _module.stopGather();
            } catch(e){
                reject(e);
                return;
            }
            DeviceEventEmitter.once('onStopGatherCallback', resp => {
                resolve(resp);
            });
        });
    },
    
}