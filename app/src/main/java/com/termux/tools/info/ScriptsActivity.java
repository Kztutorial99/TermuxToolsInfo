package com.termux.tools.info;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.termux.tools.info.util.TermuxScanner;

import java.util.ArrayList;
import java.util.List;

public class ScriptsActivity extends AppCompatActivity {

    private ListView listView;
    private ArrayAdapter<String> adapter;
    private List<String> scripts;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TextView emptyText;
    private TermuxScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scripts);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Scripts");
        }

        listView = findViewById(R.id.scriptsListView);
        swipeRefreshLayout = findViewById(R.id.scriptsSwipeRefresh);
        emptyText = findViewById(R.id.emptyText);
        scanner = new TermuxScanner(this);

        scripts = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, scripts);
        listView.setAdapter(adapter);

        swipeRefreshLayout.setOnRefreshListener(this::loadScripts);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        );

        listView.setOnItemClickListener((parent, view, position, id) -> {
            String scriptPath = scripts.get(position);
            Toast.makeText(this, "Script: " + scriptPath, Toast.LENGTH_SHORT).show();
        });

        loadScripts();
    }

    private void loadScripts() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            List<String> foundScripts = scanner.scanScripts();
            runOnUiThread(() -> {
                scripts.clear();
                scripts.addAll(foundScripts);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);

                if (scripts.isEmpty()) {
                    emptyText.setVisibility(View.VISIBLE);
                    listView.setVisibility(View.GONE);
                } else {
                    emptyText.setVisibility(View.GONE);
                    listView.setVisibility(View.VISIBLE);
                    Toast.makeText(this, "Found " + scripts.size() + " scripts", Toast.LENGTH_SHORT).show();
                }
            });
        }).start();
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
