package com.termux.tools.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class PackageDetailsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_package_details);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Package Details");
        }

        String packageName = getIntent().getStringExtra("package_name");
        String version = getIntent().getStringExtra("package_version");
        String description = getIntent().getStringExtra("package_description");

        TextView nameText = findViewById(R.id.detailName);
        TextView versionText = findViewById(R.id.detailVersion);
        TextView descriptionText = findViewById(R.id.detailDescription);

        if (packageName != null) nameText.setText(packageName);
        if (version != null) versionText.setText("Version: " + version);
        if (description != null) descriptionText.setText(description);
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
