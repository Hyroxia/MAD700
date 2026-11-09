package com.example.smartpantry;

import android.app.Activity;
import android.os.Bundle;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

public class SettingsActivity extends Activity {
    @Override
    protected void onCreate(Bundle state) {
        super.onCreate(state);

        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(24, 24, 24, 24);

        NavigationBar.addTo(this, layout);

        TextView title = new TextView(this);
        title.setText("Settings");
        title.setTextSize(24);
        layout.addView(title);

        CheckBox alerts = new CheckBox(this);
        alerts.setText("Enable expiring-soon alerts");
        layout.addView(alerts);

        setContentView(layout);
    }
}
