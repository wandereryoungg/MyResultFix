package com.young.myresultfix;

import android.content.Context;
import android.widget.Toast;

public class BugTest {
    public void toastBug(Context context) {
        Toast.makeText(context, "bug1出现，请尽快修复", Toast.LENGTH_LONG).show();
    }
}
