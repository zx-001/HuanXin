package com.huanxindemo.dell.huanxin.util;

import android.content.Context;
import android.widget.Toast;

public class ToastUtil {

    public static Toast mToast;

    public static void show(Context context, String info) {
        if (null != context && null != info && !info.equals("")) {
            Toast.makeText(context, info, Toast.LENGTH_SHORT).show();
        }
    }
}
