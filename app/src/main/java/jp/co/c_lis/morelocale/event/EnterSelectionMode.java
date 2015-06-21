package jp.co.c_lis.morelocale.event;

import jp.co.c_lis.morelocale.LocaleItem;

/**
 * Created by keiji_ariyama on 6/20/15.
 */
public class EnterSelectionMode {

    public final LocaleItem localeItem;

    public EnterSelectionMode(LocaleItem localeItem) {
        this.localeItem = localeItem;
    }
}
