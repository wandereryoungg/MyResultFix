package com.young.myresultfix;

import android.content.Context;
import android.widget.Toast;

public class BugTest_2 {
    public void toastBug(Context context) {
        Toast.makeText(context, "bug2出现，请尽快修复", Toast.LENGTH_SHORT).show();
    }
}
