package com.termux.tools.info;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.termux.tools.info.util.TermuxScanner;

public class SystemInfoActivity extends AppCompatActivity {

    private TextView systemInfoText;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TermuxScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_system_info);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("System Information");
        }

        systemInfoText = findViewById(R.id.systemInfoText);
        swipeRefreshLayout = findViewById(R.id.systemInfoSwipeRefresh);
        scanner = new TermuxScanner(this);

        swipeRefreshLayout.setOnRefreshListener(this::loadSystemInfo);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        );

        loadSystemInfo();
    }

    private void loadSystemInfo() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            String info = scanner.getSystemInfo();
            runOnUiThread(() -> {
                systemInfoText.setText(info);
                swipeRefreshLayout.setRefreshing(false);
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
