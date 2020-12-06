package com.madroid.demoaidl;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.madroid.aidlsdkserver.ISensorAidlInterface;
import com.madroid.aidlsdkserver.SensorService;
import com.madroid.demoaidl.databinding.ActivityMainBinding;

public class MainActivity extends AppCompatActivity {
    private ISensorAidlInterface mSensorInfo;
    private ActivityMainBinding mBinding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        initBind();

    }

    /*For demonstrating sensor info capture is working*/
    private void getSensorInfo() {
        try {
            String info = mSensorInfo.getSensorData();
            mBinding.setSensorInfo(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initBind() {
        Intent intent = new Intent(this, SensorService.class);
        bindService(intent, serviceConnection, BIND_AUTO_CREATE);
    }

    ServiceConnection serviceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            mSensorInfo = ISensorAidlInterface.Stub.asInterface(service);
            getSensorInfo();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {

        }
    };


}