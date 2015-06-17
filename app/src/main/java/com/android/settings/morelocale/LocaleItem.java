package com.android.settings.morelocale;

import io.realm.RealmObject;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class LocaleItem extends RealmObject {

    private String country;
    private String language;

    private long lastUsedDate;

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public long getLastUsedDate() {
        return lastUsedDate;
    }

    public void setLastUsedDate(long lastUsedDate) {
        this.lastUsedDate = lastUsedDate;
    }
}
