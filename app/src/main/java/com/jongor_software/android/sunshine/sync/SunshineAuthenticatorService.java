package com.jongor_software.android.sunshine.sync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * This is the service which allows the sync adapter framework to access the authenticator
 */
public class SunshineAuthenticatorService extends Service {
    // Instance field storing the authenticator
    private SunshineAuthenticator mAuthenticator;

    @Override
    public void onCreate() {
        // Create a new authenticator object
        mAuthenticator = new SunshineAuthenticator(this);
    }

    /**
     * When the system binds to this Service to make the RPC, return the authenticator's IBinder
     */
    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
