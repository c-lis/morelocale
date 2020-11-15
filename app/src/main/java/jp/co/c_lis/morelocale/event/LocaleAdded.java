package jp.co.c_lis.morelocale.event;

import jp.co.c_lis.morelocale.LocaleItem;

/**
 * Created by keiji_ariyama on 6/20/15.
 */
public class LocaleAdded {
    public final LocaleItem item;


    public LocaleAdded(LocaleItem item) {
        this.item = item;
    }

}
