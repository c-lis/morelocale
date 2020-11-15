package jp.co.c_lis.morelocale.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import jp.co.c_lis.morelocale.LocaleItem;
import jp.co.c_lis.morelocale.MoreLocale;

/**
 * Created by keiji_ariyama on 6/17/15.
 */
public class Utils {

    private static final String FILENAME_REALM = "locales.realm";

    public static Realm getRealmInstance(Context context) {
        Realm.init(context);
        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder()
                .directory(context.getFilesDir()).name(FILENAME_REALM)
                .build();
        return Realm.getInstance(realmConfiguration);
    }

    public static boolean equals(LocaleItem o1, LocaleItem o2) {
        if (o1 == o2) return true;
        if (o1 == null || o2 == null || o1.getClass() != o2.getClass()) return false;

        if (o1.getLabel() != null ? !o1.getLabel().equals(o2.getLabel()) : o2.getLabel() != null) return false;
        if (o1.getCountry() != null ? !o1.getCountry().equals(o2.getCountry()) : o2.getCountry() != null) return false;

        return !(o1.getLanguage() != null ? !o1.getLanguage().equals(o2.getLanguage()) : o2.getLanguage() != null);
    }

    /**
     * 端末プリセットのロケール情報を取得する
     *
     * @return Locオブジェクトのリスト
     */
    public static List<MoreLocale.Loc> getPresetLocales(AssetManager am) {

        // 返却する結果
        ArrayList<MoreLocale.Loc> result = new ArrayList<>();

        Hashtable<String, MoreLocale.Loc> arr = new Hashtable<String, MoreLocale.Loc>();

        // プリセットのロケール名の一覧を取得する
        String[] locales = am.getLocales();
        Arrays.sort(locales);

        final int origSize = locales.length;

        for (int i = 0; i < origSize; i++) {
            String localeName = locales[i];

            if (localeName.equals("ja")) {
                localeName = "ja_JP";
            }

            int len = localeName.length();

            // 取得したロケールが2文字(ja)なら
            if (len == 2) {
                // ロケールオブジェクトの生成
                Locale tmpLocale = new Locale(localeName);
                arr.put(tmpLocale.getLanguage(), new MoreLocale.Loc(
                        toTitleCase(tmpLocale.getDisplayLanguage()), tmpLocale));

                // 取得したロケールが5文字(ja_JP)なら
            } else if (len == 5) {

                // 先頭2文字は言語、4、5文字目は地域
                String language = localeName.substring(0, 2);
                String country = localeName.substring(3, 5);

                // ロケールオブジェクトの生成
                Locale tmpLocale = new Locale(language, country);

                // 現在のindexが0なら
                if (arr.size() == 0) {
                    // 先頭に入力
                    MoreLocale.Loc tmp = new MoreLocale.Loc(toTitleCase(tmpLocale.getDisplayLanguage()), tmpLocale);
                    arr.put(tmpLocale.getLanguage(), tmp);
                } else {

                    if (arr.containsKey(tmpLocale.getLanguage())) {
                        MoreLocale.Loc prevLoc = arr.get(tmpLocale.getLanguage());

                        // 現在のインデックスより前のロケール情報と指定言語が同じであれば、
                        if (prevLoc.locale.getLanguage().equals(language)) {

                            // 現在のインデックスより前の地域情報の取得
                            String prevCountry = prevLoc.locale.getCountry();

                            // 地域が設定されていなければ、
                            if (prevCountry.length() == 0) {

                                // 既存のロケールを上書き
                                prevLoc.locale = tmpLocale;
                                prevLoc.label = toTitleCase(tmpLocale.getDisplayLanguage());

                            } else {

                                // 前のロケール列のラベルを変更
                                prevLoc.label = toTitleCase(prevLoc.locale.getDisplayName());

                                // 新しいロケールを設定
                                MoreLocale.Loc tmp = new MoreLocale.Loc(toTitleCase(tmpLocale.getDisplayName()),
                                        tmpLocale);
                                arr.put(localeName, tmp);
                            }

                        }

                        // 現在のインデックスより前のロケール情報と指定言語が異なる場合
                    } else {
                        String displayName;
                        if (localeName.equals("zz_ZZ")) {
                            displayName = "Pseudo...";
                        } else {
                            displayName = toTitleCase(tmpLocale.getDisplayLanguage());
                        }

                        // 先頭に入力
                        MoreLocale.Loc tmp = new MoreLocale.Loc(toTitleCase(displayName), tmpLocale);
                        arr.put(tmpLocale.getLanguage(), tmp);

                    }
                }
            }
        }

        result = new ArrayList<>(arr.values());

        // 結果の返却
        return result;
    }

    /**
     * 先頭文字を大文字にする
     *
     * @param s
     * @return 先頭が大文字になった文字列
     */
    private static String toTitleCase(String s) {
        if (s.length() == 0) {
            return s;
        }

        return Character.toUpperCase(s.charAt(0)) + s.substring(1);
    }

}
