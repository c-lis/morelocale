package jp.co.c_lis.morelocale.event;

import java.util.Locale;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class SetLocale {
    public final Locale locale;

    public SetLocale(Locale locale) {
        this.locale = locale;
    }
}
