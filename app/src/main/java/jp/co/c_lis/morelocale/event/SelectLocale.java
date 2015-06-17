package jp.co.c_lis.morelocale.event;

import java.util.Locale;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class SelectLocale {
    public final Locale locale;

    public SelectLocale(Locale locale) {
        this.locale = locale;
    }
}
