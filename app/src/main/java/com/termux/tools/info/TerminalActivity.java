package com.termux.tools.info;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.StyledString;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;

/**
 * Terminal Activity using Real Termux Libraries (like AIDE-Termux)
 * This provides the authentic Termux terminal experience
 */
public class TerminalActivity extends AppCompatActivity implements TerminalViewClient {

    private TerminalView terminalView;
    private TerminalSession terminalSession;
    private FloatingActionButton fabClear;
    private FloatingActionButton fabKeyboard;
    
    private final Handler handler = new Handler(Looper.getMainLooper());
    private static final String HOME_PATH = System.getProperty("user.home", "/data/data/com.termux/files/home");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Create root layout
        android.widget.FrameLayout rootLayout = new android.widget.FrameLayout(this);
        rootLayout.setLayoutParams(new android.widget.FrameLayout.LayoutParams(
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
            android.widget.FrameLayout.LayoutParams.MATCH_PARENT
        ));
        rootLayout.setBackgroundColor(0xFF000000); // Black background
        
        setContentView(rootLayout);

        // Setup Toolbar
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Termux Terminal");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setBackgroundColor(0xFF1a1a2e);
        toolbar.setPopupTheme(new com.google.android.material.R.style.ThemeOverlay_Material3_Dark);
        
        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());
        
        rootLayout.addView(toolbar);

        // Create TerminalView
        terminalView = new TerminalView(this);
        terminalView.setId(R.id.terminal_view);
        terminalView.setBackgroundColor(0xFF000000);
        
        android.widget.FrameLayout.LayoutParams terminalParams = 
            new android.widget.FrameLayout.LayoutParams(
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT
            );
        terminalParams.topMargin = (int) (getStatusBarHeight() + getResources().getDimensionPixelSize(androidx.appcompat.R.dimen.abc_action_bar_default_height_material));
        terminalView.setLayoutParams(terminalParams);
        
        // Keep screen on
        terminalView.setKeepScreenOn(true);
        
        rootLayout.addView(terminalView);

        // Create Terminal Session
        terminalSession = createTerminalSession();
        terminalView.attachSession(terminalSession, this);

        // FAB Clear
        fabClear = new FloatingActionButton(this);
        fabClear.setImageResource(android.R.drawable.ic_menu_delete);
        fabClear.setContentDescription("Clear");
        fabClear.setBackgroundColor(0xFF1a1a2e);
        
        android.widget.FrameLayout.LayoutParams paramsClear = 
            new android.widget.FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini),
                getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini)
            );
        paramsClear.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
        paramsClear.rightMargin = getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_image_size);
        paramsClear.bottomMargin = getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini) + 16;
        fabClear.setLayoutParams(paramsClear);
        fabClear.setOnClickListener(v -> clearTerminal());
        
        rootLayout.addView(fabClear);

        // FAB Keyboard
        fabKeyboard = new FloatingActionButton(this);
        fabKeyboard.setImageResource(android.R.drawable.ic_menu_sort_by_size);
        fabKeyboard.setContentDescription("Show Keyboard");
        fabKeyboard.setBackgroundColor(0xFF1a1a2e);
        
        android.widget.FrameLayout.LayoutParams paramsKeyboard = 
            new android.widget.FrameLayout.LayoutParams(
                getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini),
                getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_size_mini)
            );
        paramsKeyboard.gravity = android.view.Gravity.BOTTOM | android.view.Gravity.END;
        paramsKeyboard.rightMargin = getResources().getDimensionPixelSize(com.google.android.material.R.dimen.design_fab_image_size);
        paramsKeyboard.bottomMargin = 16;
        fabKeyboard.setLayoutParams(paramsKeyboard);
        fabKeyboard.setOnClickListener(v -> showKeyboard());
        
        rootLayout.addView(fabKeyboard);

        // Show welcome message after delay
        handler.postDelayed(this::showWelcome, 500);
    }

    private TerminalSession createTerminalSession() {
        // Create a simple shell session
        String[] processArgs = new String[]{"sh"};
        return new TerminalSession(
            processArgs,
            HOME_PATH,
            getEnvironment(),
            1000 // transcript size
        );
    }

    private String[] getEnvironment() {
        return new String[]{
            "PATH=/data/data/com.termux/files/usr/bin:" + System.getenv("PATH"),
            "HOME=" + HOME_PATH,
            "PREFIX=/data/data/com.termux/files/usr",
            "TERM=xterm-256color",
            "LANG=en_US.UTF-8"
        };
    }

    private void showWelcome() {
        if (terminalSession != null) {
            String welcome = "\u001B[1;32m╔═══════════════════════════════════════════════════════════╗\u001B[0m\r\n" +
                             "\u001B[1;32m║\u001B[0m           \u001B[1;33mTermuxToolsInfo - Real Termux Terminal\u001B[0m            \u001B[1;32m║\u001B[0m\r\n" +
                             "\u001B[1;32m║\u001B[0m                    \u001B[1;36mVersion 1.0\u001B[0m                            \u001B[1;32m║\u001B[0m\r\n" +
                             "\u001B[1;32m╚═══════════════════════════════════════════════════════════╝\u001B[0m\r\n\r\n" +
                             "\u001B[32mTermux emulator initialized successfully!\u001B[0m\r\n\r\n" +
                             "\u001B[1;33mType commands below:\u001B[0m\r\n\r\n";
            terminalSession.write(welcome);
        }
    }

    private void clearTerminal() {
        if (terminalSession != null) {
            terminalSession.reset();
            Toast.makeText(this, "Terminal cleared", Toast.LENGTH_SHORT).show();
        }
    }

    private void showKeyboard() {
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm != null) {
            terminalView.requestFocus();
            imm.showSoftInput(terminalView, InputMethodManager.SHOW_IMPLICIT);
        }
    }

    private int getStatusBarHeight() {
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return getResources().getDimensionPixelSize(resourceId);
        }
        return 0;
    }

    // TerminalViewClient implementation
    @Override
    public void onTextChanged(TerminalView view) {
        // Text changed
    }

    @Override
    public void onTitleChanged(TerminalView view, String title) {
        // Title changed
    }

    @Override
    public void onSessionFinished(TerminalView view, TerminalSession session) {
        // Session finished
    }

    @Override
    public void onCopyTextToClipboard(TerminalView view, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Terminal", text));
        }
    }

    @Override
    public void onPasteTextFromClipboard(TerminalView view) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null && clipboard.hasPrimaryClip()) {
            ClipData clipData = clipboard.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                String text = clipData.getItemAt(0).getText().toString();
                if (terminalSession != null) {
                    terminalSession.write(text);
                }
            }
        }
    }

    @Override
    public void onBell(TerminalView view) {
        // Bell
    }

    @Override
    public void onColorsChanged(TerminalView view) {
        // Colors changed
    }

    @Override
    public void onTerminalCursorStateChange(boolean state) {
        // Cursor state change
    }

    @Override
    public void exitApp(TerminalView view) {
        finish();
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    @Override
    public boolean shouldBackButtonBeMappedToEscape() {
        return false;
    }

    @Override
    public boolean shouldEnforceCharBasedInput() {
        return false;
    }

    @Override
    public boolean shouldUseCtrlSpaceWorkaround() {
        return false;
    }

    @Override
    public boolean isTerminalViewSelected() {
        return true;
    }

    @Override
    public void copyModeChanged(TerminalView view, boolean copyMode) {
        // Copy mode changed
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event, TerminalView view) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event, TerminalView view) {
        return false;
    }

    @Override
    public boolean onKeyLongPress(int keyCode, KeyEvent event, TerminalView view) {
        return false;
    }

    @Override
    public boolean onKeyMultiple(int keyCode, int repeatCount, KeyEvent event, TerminalView view) {
        return false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (terminalView != null) {
            terminalView.onScreenPaused();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (terminalView != null) {
            terminalView.onScreenResumed();
        }
    }

    @Override
    public void onBackPressed() {
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit Terminal")
            .setMessage("Do you want to exit the Termux terminal?")
            .setPositiveButton("Yes", (dialog, which) -> finish())
            .setNegativeButton("No", null)
            .show();
    }
}
