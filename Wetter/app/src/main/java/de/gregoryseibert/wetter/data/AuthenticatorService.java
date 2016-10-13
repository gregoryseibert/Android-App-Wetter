package de.gregoryseibert.wetter.data;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

/**
 * Created by gs71756 on 13.10.2016.
 */

public class AuthenticatorService extends Service {
    private Authenticator mAuthenticator;

    @Override
    public void onCreate() {
        mAuthenticator = new Authenticator(this);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mAuthenticator.getIBinder();
    }
}
