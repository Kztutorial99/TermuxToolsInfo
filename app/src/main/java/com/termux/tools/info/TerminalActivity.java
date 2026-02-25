package com.termux.tools.info;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;

/**
 * TerminalActivity - Embedded Termux Terminal
 * This provides a real terminal emulator inside the app
 */
public class TerminalActivity extends AppCompatActivity implements TerminalViewClient, TerminalSessionClient {

    private TerminalView terminalView;
    private TerminalSession terminalSession;
    private Process shellProcess;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create root layout
        FrameLayout rootLayout = new FrameLayout(this);
        rootLayout.setLayoutParams(new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT));

        // Setup Toolbar
        Toolbar toolbar = new Toolbar(this);
        toolbar.setTitle("Termux Terminal");
        toolbar.setTitleTextColor(0xFFFFFFFF);
        toolbar.setBackgroundColor(0xFF009688);
        toolbar.setPopupTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark);

        toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
        toolbar.setNavigationOnClickListener(v -> finish());

        // Add toolbar to layout
        rootLayout.addView(toolbar);

        // Create TerminalView
        terminalView = new TerminalView(this, null);
        terminalView.setId(R.id.terminal_view);
        terminalView.setBackgroundColor(0xFF000000);

        FrameLayout.LayoutParams terminalParams = new FrameLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT);
        terminalParams.topMargin = (int) (getResources().getDisplayMetrics().density * 56);

        rootLayout.addView(terminalView, terminalParams);

        setContentView(rootLayout);

        // Set this as the client
        terminalView.setTerminalViewClient(this);

        // Initialize terminal
        initializeTerminal();
    }

    private void initializeTerminal() {
        try {
            // Find shell
            String shell = findShell();
            if (shell == null) {
                Toast.makeText(this, "No shell found!", Toast.LENGTH_LONG).show();
                return;
            }

            // Create terminal session
            String[] processArgs = {shell};
            terminalSession = new TerminalSession(shell, "/", processArgs, getEnv(), 1024, this);
            
            // Attach session to view
            terminalView.attachSession(terminalSession);

            Toast.makeText(this, "Terminal initialized", Toast.LENGTH_SHORT).show();

        } catch (Exception e) {
            Toast.makeText(this, "Failed to initialize terminal: " + e.getMessage(), 
                    Toast.LENGTH_LONG).show();
            e.printStackTrace();
        }
    }

    private String findShell() {
        String[] shells = {"/system/bin/sh", "/bin/sh"};
        for (String s : shells) {
            if (new File(s).exists()) {
                return s;
            }
        }
        return null;
    }

    private String[] getEnv() {
        return new String[] {
            "PATH=/system/bin:/system/xbin:/bin",
            "TERM=xterm-256color",
            "HOME=" + System.getProperty("user.home", "/data/data/" + getPackageName()),
            "LANG=C.UTF-8"
        };
    }

    @Override
    public float onScale(float scale) {
        return 1.0f;
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
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
    public void copyModeChanged(boolean copyMode) {
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent e, TerminalSession session) {
        return false;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent e) {
        return false;
    }

    @Override
    public boolean onLongPress(MotionEvent event) {
        return false;
    }

    @Override
    public boolean readControlKey() {
        return false;
    }

    @Override
    public boolean readAltKey() {
        return false;
    }

    @Override
    public boolean readShiftKey() {
        return false;
    }

    @Override
    public boolean readFnKey() {
        return false;
    }

    @Override
    public boolean onCodePoint(int codePoint, boolean ctrlDown, TerminalSession session) {
        return false;
    }

    @Override
    public void onEmulatorSet() {
    }

    @Override
    public void logError(String tag, String message) {
        android.util.Log.e(tag, message);
    }

    @Override
    public void logWarn(String tag, String message) {
        android.util.Log.w(tag, message);
    }

    @Override
    public void logInfo(String tag, String message) {
        android.util.Log.i(tag, message);
    }

    @Override
    public void logDebug(String tag, String message) {
        android.util.Log.d(tag, message);
    }

    @Override
    public void logVerbose(String tag, String message) {
        android.util.Log.v(tag, message);
    }

    @Override
    public void logStackTraceWithMessage(String tag, String message, Exception e) {
        android.util.Log.e(tag, message, e);
    }

    @Override
    public void logStackTrace(String tag, Exception e) {
        android.util.Log.e(tag, "Stack trace", e);
    }

    @Override
    public void onTextChanged(TerminalSession changedSession) {
        // Text changed in terminal
    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {
        // Title changed
    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Session finished", Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onCopyTextToClipboard(TerminalSession session, String text) {
    }

    @Override
    public void onPasteTextFromClipboard(TerminalSession session) {
    }

    @Override
    public void onBell(TerminalSession session) {
    }

    @Override
    public void onColorsChanged(TerminalSession session) {
    }

    @Override
    public void onTerminalCursorStateChange(boolean state) {
    }

    @Override
    public void setTerminalShellPid(TerminalSession session, int pid) {
    }

    @Override
    public Integer getTerminalCursorStyle() {
        return android.graphics.Typeface.NORMAL;
    }

    public void onScreenPaused() {
    }

    public void onScreenResumed() {
    }
}
