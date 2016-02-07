package com.transapps.callprotector.IncomingCalls;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.IBinder;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.widget.Toast;

import com.transapps.callprotector.Requests.GetRequests;
import com.transapps.callprotector.Requests.RetrofitAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by Hiciu on 2/7/2016.
 */
public class BackgroundCallsService extends Service {

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Let it continue running until it is stopped.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onCreate() {
        super.onCreate();


        IntentFilter filter = new IntentFilter();
        filter.addAction("android.provider.Telephony.SMS_RECEIVED");
        filter.addAction(android.telephony.TelephonyManager.ACTION_PHONE_STATE_CHANGED);

        registerReceiver(receiver, filter);
    }

    TelephonyManager telephony;
    private final BroadcastReceiver receiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (telephony == null) {
                telephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
                telephony.listen(new PhoneStateListener() {
                    @Override
                    public void onCallStateChanged(int state, final String incomingNumber) {
                        super.onCallStateChanged(state, incomingNumber);
                        if( state ==  TelephonyManager.CALL_STATE_RINGING ) {
                            GetRequests service = RetrofitAdapter.createService(GetRequests.class, "admin", "1234");
                            service.getNumber(incomingNumber).enqueue(new Callback<String>() {
                                @Override
                                public void onResponse(Call<String> call, Response<String> response) {
                                    Toast.makeText(BackgroundCallsService.this, "For "+incomingNumber +" " + response.message(), Toast.LENGTH_SHORT).show();
                                }

                                @Override
                                public void onFailure(Call<String> call, Throwable t) {
                                    Toast.makeText(BackgroundCallsService.this,"For "+incomingNumber +" " +  t.getMessage(), Toast.LENGTH_SHORT).show();
                                }
                            });

                        }
                    }
                }, PhoneStateListener.LISTEN_CALL_STATE);

            }
        }
    };


}
