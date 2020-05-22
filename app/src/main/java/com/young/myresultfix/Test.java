package com.young.myresultfix;

import android.content.Context;

public class Test {
    public void toastBug(Context context) {
        new BugTest().toastBug(context);
    }

    public void toastBug2(Context context) {
        new BugTest_2().toastBug(context);
    }
}
