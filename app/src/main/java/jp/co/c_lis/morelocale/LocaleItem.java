package jp.co.c_lis.morelocale;

import java.io.Serializable;

import io.realm.RealmObject;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class LocaleItem extends RealmObject implements Serializable {

    private String label;
    private boolean hasLabel;

    private String country;
    private String language;

    private long lastUsedDate;

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public boolean isHasLabel() {
        return hasLabel;
    }

    public void setHasLabel(boolean hasLabel) {
        this.hasLabel = hasLabel;
    }

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
