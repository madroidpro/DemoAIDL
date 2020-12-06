package com.madroid.aidlsdkserver;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.os.IBinder;
import android.os.RemoteException;

import com.madroid.aidlsdkserver.databinding.ActivityClientBinding;

import java.util.List;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public class ClientActivity extends AppCompatActivity {
    private ActivityClientBinding mBinding;
    private ISensorAidlInterface mSensorInfo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_client);
        initBind();
    }

    private void getSensorInfo() {
        try {
            String info = mSensorInfo.getSensorData();
            mBinding.setSensorInfo(info);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private void initBind() {
        Intent intent = new Intent("com.example.aidlsdkserver.AIDL"); //AIDL server intent
        bindService(convertImplicitIntentToExplicitIntent(intent, this), serviceConnection, BIND_AUTO_CREATE);
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

    /*We will need to convert intent to explicit intent to access the service from other APP*/
    public static Intent convertImplicitIntentToExplicitIntent(Intent implicitIntent, Context context) {
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfoList = pm.queryIntentServices(implicitIntent, 0);

        if (resolveInfoList == null || resolveInfoList.size() != 1) {
            return null;
        }
        ResolveInfo serviceInfo = resolveInfoList.get(0);
        ComponentName component = new ComponentName(serviceInfo.serviceInfo.packageName, serviceInfo.serviceInfo.name);
        Intent explicitIntent = new Intent(implicitIntent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }
}