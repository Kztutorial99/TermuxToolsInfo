package com.termux.tools.info;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ContextMenu;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;

/**
 * TerminalActivity - Embedded Termux Terminal (Like official Termux app)
 * Implements proper TerminalView with TerminalSession
 */
public class TerminalActivity extends AppCompatActivity implements TerminalViewClient, TerminalSessionClient {

    private static final String TAG = "TerminalActivity";
    
    private TerminalView terminalView;
    private TerminalSession terminalSession;
    private boolean hasError = false;

    // Extra keys row
    private static final String[] EXTRA_KEYS = {
        "ESC", "TAB", "CTRL", "ALT", "↑", "↓", "←", "→", "HOME", "END", "PGUP", "PGDN", "DEL", "BKSP"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate started");

        try {
            // Create root layout
            LinearLayout rootLayout = new LinearLayout(this);
            rootLayout.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT));
            rootLayout.setOrientation(LinearLayout.VERTICAL);

            // Setup Toolbar
            Toolbar toolbar = new Toolbar(this);
            toolbar.setTitle("Termux Terminal");
            toolbar.setTitleTextColor(0xFFFFFFFF);
            toolbar.setBackgroundColor(0xFF009688);
            toolbar.setPopupTheme(androidx.appcompat.R.style.ThemeOverlay_AppCompat_Dark);
            toolbar.setNavigationIcon(androidx.appcompat.R.drawable.abc_ic_ab_back_material);
            toolbar.setNavigationOnClickListener(v -> finish());
            toolbar.inflateMenu(R.menu.terminal_menu);
            toolbar.setOnMenuItemClickListener(this::handleMenuItems);

            rootLayout.addView(toolbar);

            // Create TerminalView (like official Termux)
            terminalView = new TerminalView(this, null);
            terminalView.setId(R.id.terminal_view);
            terminalView.setBackgroundColor(0xFF000000);
            terminalView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));

            // Register for context menu (long press)
            registerForContextMenu(terminalView);

            rootLayout.addView(terminalView);

            setContentView(rootLayout);

            // Set TerminalViewClient
            terminalView.setTerminalViewClient(this);

            // Initialize terminal session after a short delay
            new Handler(Looper.getMainLooper()).postDelayed(this::createTerminalSession, 100);

            Log.d(TAG, "onCreate completed");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showErrorAndExit("Failed to create terminal: " + e.getMessage(), e);
        }
    }

    private void createTerminalSession() {
        if (hasError) return;

        try {
            Log.d(TAG, "Creating terminal session...");

            // Find shell
            String shell = findShell();
            if (shell == null) {
                showErrorAndExit("No shell found! Tried /system/bin/sh and /bin/sh", null);
                return;
            }
            Log.d(TAG, "Using shell: " + shell);

            // Setup environment (like Termux)
            String[] env = getEnvironment();

            // Create TerminalSession (this creates TerminalEmulator internally)
            terminalSession = new TerminalSession(
                shell,                              // shell path
                getFilesDir().getAbsolutePath(),    // working directory
                new String[]{shell},                // arguments
                env,                                // environment
                1024,                               // terminal rows
                this                                // client
            );

            Log.d(TAG, "TerminalSession created, attaching to TerminalView...");

            // Attach session to TerminalView
            terminalView.attachSession(terminalSession);

            Toast.makeText(this, "Terminal ready!", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Terminal session created and attached successfully");

        } catch (Exception e) {
            Log.e(TAG, "Error creating terminal session", e);
            showErrorAndExit("Failed to create terminal session: " + e.getMessage(), e);
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

    private String[] getEnvironment() {
        return new String[] {
            "PATH=/system/bin:/system/xbin:/bin",
            "TERM=xterm-256color",
            "HOME=" + getFilesDir().getAbsolutePath(),
            "LANG=C.UTF-8",
            "PWD=" + getFilesDir().getAbsolutePath(),
            "EXTERNAL_STORAGE=" + getExternalFilesDir(null),
            "PREFIX=/data/data/com.termux/files/usr"
        };
    }

    private boolean handleMenuItems(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_reset) {
            resetTerminal();
            return true;
        } else if (id == R.id.action_copy) {
            copyText();
            return true;
        } else if (id == R.id.action_paste) {
            pasteText();
            return true;
        } else if (id == R.id.action_more) {
            showMoreOptions();
            return true;
        }
        return false;
    }

    private void resetTerminal() {
        if (terminalSession != null) {
            terminalSession.reset();
            Toast.makeText(this, "Terminal reset", Toast.LENGTH_SHORT).show();
        }
    }

    private void copyText() {
        if (terminalView != null && terminalView.mClient != null) {
            terminalView.mClient.copyModeChanged(true);
            Toast.makeText(this, "Selection copied", Toast.LENGTH_SHORT).show();
        }
    }

    private void pasteText() {
        if (terminalView != null && terminalSession != null) {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
            if (clipboard != null && clipboard.hasPrimaryClip()) {
                ClipData clipData = clipboard.getPrimaryClip();
                if (clipData != null && clipData.getItemCount() > 0) {
                    CharSequence text = clipData.getItemAt(0).getText();
                    if (text != null) {
                        terminalSession.write(text.toString());
                    }
                }
            }
        }
    }

    private int currentFontSize = 14;

    private void showMoreOptions() {
        new AlertDialog.Builder(this)
            .setTitle("Terminal Options")
            .setItems(new String[]{"Increase Font", "Decrease Font", "Toggle Fullscreen"}, (dialog, which) -> {
                if (which == 0) {
                    currentFontSize = Math.min(72, currentFontSize + 2);
                    terminalView.setTextSize(currentFontSize);
                } else if (which == 1) {
                    currentFontSize = Math.max(8, currentFontSize - 2);
                    terminalView.setTextSize(currentFontSize);
                } else if (which == 2) {
                    toggleFullscreen();
                }
            })
            .show();
    }

    private void toggleFullscreen() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            View decorView = getWindow().getDecorView();
            int uiOptions = View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY;
            decorView.setSystemUiVisibility(uiOptions);
        }
    }

    private void showErrorAndExit(String message, Exception e) {
        hasError = true;
        String fullMessage = message + (e != null ? "\n\n" + e.getClass().getSimpleName() : "");
        Log.e(TAG, fullMessage, e);

        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                new AlertDialog.Builder(this)
                    .setTitle("Terminal Error")
                    .setMessage(fullMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            } catch (Exception ex) {
                Toast.makeText(this, fullMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    // ========== TerminalViewClient Implementation ==========

    @Override
    public float onScale(float scale) {
        if (terminalView == null) return 1.0f;
        currentFontSize = (int) Math.max(8, Math.min(72, currentFontSize * scale));
        terminalView.setTextSize(currentFontSize);
        return scale;
    }

    @Override
    public void onSingleTapUp(MotionEvent e) {
        // Request focus to show keyboard
        if (terminalView != null) {
            terminalView.requestFocus();
        }
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
        return true;
    }

    @Override
    public boolean isTerminalViewSelected() {
        return true;
    }

    @Override
    public void copyModeChanged(boolean copyMode) {
        Log.d(TAG, "Copy mode: " + copyMode);
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
        Log.d(TAG, "Emulator set - terminal ready!");
    }

    // ========== Logging ==========

    @Override
    public void logError(String tag, String message) {
        Log.e(tag, message);
    }

    @Override
    public void logWarn(String tag, String message) {
        Log.w(tag, message);
    }

    @Override
    public void logInfo(String tag, String message) {
        Log.i(tag, message);
    }

    @Override
    public void logDebug(String tag, String message) {
        Log.d(tag, message);
    }

    @Override
    public void logVerbose(String tag, String message) {
        Log.v(tag, message);
    }

    @Override
    public void logStackTraceWithMessage(String tag, String message, Exception e) {
        Log.e(tag, message, e);
    }

    @Override
    public void logStackTrace(String tag, Exception e) {
        Log.e(tag, "Stack trace", e);
    }

    // ========== TerminalSessionClient Implementation ==========

    @Override
    public void onTextChanged(TerminalSession changedSession) {
        // Terminal content changed
    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {
        String title = changedSession.getTitle();
        if (title != null && !title.isEmpty()) {
            runOnUiThread(() -> {
                if (getSupportActionBar() != null) {
                    getSupportActionBar().setTitle(title);
                }
            });
        }
    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Session finished", Toast.LENGTH_SHORT).show();
        });
    }

    @Override
    public void onCopyTextToClipboard(TerminalSession session, String text) {
        ClipboardManager clipboard = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);
        if (clipboard != null) {
            clipboard.setPrimaryClip(ClipData.newPlainText("Terminal", text));
        }
    }

    @Override
    public void onPasteTextFromClipboard(TerminalSession session) {
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
    public void onBell(TerminalSession session) {
        Log.d(TAG, "Bell!");
    }

    @Override
    public void onColorsChanged(TerminalSession session) {
        Log.d(TAG, "Colors changed");
    }

    @Override
    public void onTerminalCursorStateChange(boolean state) {
        // Cursor visibility changed
    }

    @Override
    public void setTerminalShellPid(TerminalSession session, int pid) {
        Log.d(TAG, "Shell PID: " + pid);
    }

    @Override
    public Integer getTerminalCursorStyle() {
        return android.graphics.Typeface.NORMAL;
    }

    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        if (v == terminalView) {
            menu.setHeaderTitle("Terminal Options");
            menu.add(0, 1, 0, "Copy");
            menu.add(0, 2, 1, "Paste");
            menu.add(0, 3, 2, "Reset");
            menu.add(0, 4, 3, "More");
        }
    }

    @Override
    public boolean onContextItemSelected(MenuItem item) {
        if (item.getItemId() == 1) {
            copyText();
            return true;
        } else if (item.getItemId() == 2) {
            pasteText();
            return true;
        } else if (item.getItemId() == 3) {
            resetTerminal();
            return true;
        } else if (item.getItemId() == 4) {
            showMoreOptions();
            return true;
        }
        return false;
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");
        if (terminalSession != null) {
            try {
                terminalSession.finishIfRunning();
            } catch (Exception e) {
                Log.e(TAG, "Error finishing session", e);
            }
        }
        super.onDestroy();
    }
}
