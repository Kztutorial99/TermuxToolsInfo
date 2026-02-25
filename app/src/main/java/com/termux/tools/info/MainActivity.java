package com.termux.tools.info;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termux.tools.info.adapter.PackageAdapter;
import com.termux.tools.info.model.PackageInfo;
import com.termux.tools.info.util.TermuxScanner;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements PackageAdapter.OnPackageClickListener {

    private RecyclerView recyclerView;
    private PackageAdapter adapter;
    private List<PackageInfo> packageList;
    private SwipeRefreshLayout swipeRefreshLayout;
    private TermuxScanner scanner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setSupportActionBar(findViewById(R.id.toolbar));
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Termux Tools Info");
        }

        recyclerView = findViewById(R.id.recyclerView);
        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        FloatingActionButton fab = findViewById(R.id.fab);

        packageList = new ArrayList<>();
        scanner = new TermuxScanner(this);

        adapter = new PackageAdapter(packageList, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        fab.setOnClickListener(v -> scanPackages());

        swipeRefreshLayout.setOnRefreshListener(this::scanPackages);
        swipeRefreshLayout.setColorSchemeResources(
            R.color.colorPrimary,
            R.color.colorPrimaryDark,
            R.color.colorAccent
        );

        // Initial scan
        scanPackages();
    }

    private void scanPackages() {
        swipeRefreshLayout.setRefreshing(true);
        new Thread(() -> {
            List<PackageInfo> packages = scanner.scanInstalledPackages();
            runOnUiThread(() -> {
                packageList.clear();
                packageList.addAll(packages);
                adapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
                Toast.makeText(this, 
                    "Found " + packages.size() + " packages", 
                    Toast.LENGTH_SHORT).show();
            });
        }).start();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_terminal) {
            startActivity(new Intent(this, TerminalActivity.class));
            return true;
        } else if (id == R.id.action_scripts) {
            startActivity(new Intent(this, ScriptsActivity.class));
            return true;
        } else if (id == R.id.action_system_info) {
            startActivity(new Intent(this, SystemInfoActivity.class));
            return true;
        } else if (id == R.id.action_about) {
            showAbout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showAbout() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("About")
            .setMessage("Termux Tools Info v1.0\n\n" +
                       "A comprehensive tool for viewing installed packages, " +
                       "scripts, and system information in Termux.\n\n" +
                       "Features:\n" +
                       "• View installed packages\n" +
                       "• Browse scripts\n" +
                       "• System information\n" +
                       "• Package search")
            .setPositiveButton("OK", null)
            .show();
    }

    @Override
    public void onPackageClick(PackageInfo packageInfo) {
        Intent intent = new Intent(this, PackageDetailsActivity.class);
        intent.putExtra("package_name", packageInfo.getName());
        intent.putExtra("package_version", packageInfo.getVersion());
        intent.putExtra("package_description", packageInfo.getDescription());
        startActivity(intent);
    }
}
