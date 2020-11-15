package jp.co.c_lis.morelocale.event;

import jp.co.c_lis.morelocale.LocaleItem;

/**
 * Created by keiji_ariyama on 6/20/15.
 */
public class LocaleEdited {
    public final LocaleItem item;


    public LocaleEdited(LocaleItem item) {
        this.item = item;
    }

}
