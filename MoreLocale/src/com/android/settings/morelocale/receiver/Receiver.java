/*
 * Copyright (C) 2010 C-LIS CO., LTD.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

/*
 * Copyright (C) 2007 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.settings.morelocale.receiver;

import java.util.Locale;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.RemoteException;
import android.util.Log;

public class Receiver extends BroadcastReceiver {
    private static final boolean DEBUG_FLG = false;
    private static final String LOG_TAG = "MoreLocaleReceiver";

    private static final String KEY_LANGUAGE = "LOCALE_LANGUAGE";
    private static final String KEY_COUNTRY = "LOCALE_COUNTRY";
    private static final String KEY_VARIANT = "LOCALE_VARIANT";

    /*
     * (non-Javadoc)
     * 
     * @see android.content.BroadcastReceiver#onReceive(android.content.Context,
     * android.content.Intent)
     */
    @Override
    public void onReceive(Context context, Intent intent) {

        String language = intent.getStringExtra(KEY_LANGUAGE);
        String country = intent.getStringExtra(KEY_COUNTRY);
        String variant = intent.getStringExtra(KEY_VARIANT);

        if (language == null && country == null && variant == null) return;

        Locale l = new Locale(language, country, variant);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();

            config.locale = l;
            config.userSetLocale = true;

            am.updateConfiguration(config);

        } catch (RemoteException e) {
            Log.e(LOG_TAG, e.getMessage());
        }

    }

}
