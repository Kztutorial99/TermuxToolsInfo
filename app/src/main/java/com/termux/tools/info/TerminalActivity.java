package com.termux.tools.info;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ScrollView;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.termux.terminal.TerminalSession;
import com.termux.terminal.TerminalSessionClient;
import com.termux.view.TerminalView;
import com.termux.view.TerminalViewClient;

import java.io.File;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.IOException;

/**
 * TerminalActivity - Embedded Termux Terminal
 * This provides a real terminal emulator inside the app
 */
public class TerminalActivity extends AppCompatActivity implements TerminalViewClient, TerminalSessionClient {

    private static final String TAG = "TerminalActivity";
    
    private TerminalView terminalView;
    private TerminalSession terminalSession;
    private boolean hasError = false;
    private Process shellProcess;
    private TextView terminalOutput;
    private boolean useSimpleTerminal = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "TerminalActivity onCreate started");

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

            // Add toolbar to layout
            rootLayout.addView(toolbar);

            if (useSimpleTerminal) {
                // Use simple TextView-based terminal (more stable)
                setupSimpleTerminal(rootLayout);
            } else {
                // Use TerminalView (may crash on some devices)
                setupTerminalView(rootLayout);
            }

            setContentView(rootLayout);
            Log.d(TAG, "TerminalActivity onCreate completed");

        } catch (Exception e) {
            Log.e(TAG, "Error in onCreate", e);
            showErrorAndExit("Failed to create terminal: " + e.getMessage(), e);
        }
    }

    private void setupSimpleTerminal(LinearLayout rootLayout) {
        try {
            Log.d(TAG, "Setting up simple terminal...");
            
            terminalOutput = new TextView(this);
            terminalOutput.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            terminalOutput.setBackgroundColor(0xFF000000);
            terminalOutput.setTextColor(0xFF00FF00);
            terminalOutput.setPadding(16, 16, 16, 16);
            terminalOutput.setTypeface(android.graphics.Typeface.MONOSPACE);
            terminalOutput.setTextSize(12);
            terminalOutput.setText("Termux Terminal\n");
            terminalOutput.setMovementMethod(android.text.method.ScrollingMovementMethod.getInstance());

            ScrollView scrollView = new ScrollView(this);
            scrollView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));
            scrollView.addView(terminalOutput);

            rootLayout.addView(scrollView);

            // Start shell in background
            startSimpleShell();

            Toast.makeText(this, "Simple terminal mode", Toast.LENGTH_SHORT).show();
            Log.d(TAG, "Simple terminal setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up simple terminal", e);
            showErrorAndExit("Failed to setup simple terminal: " + e.getMessage(), e);
        }
    }

    private void startSimpleShell() {
        new Thread(() -> {
            try {
                Log.d(TAG, "Starting shell process...");
                
                String shell = findShell();
                if (shell == null) {
                    runOnUiThread(() -> showErrorAndExit("No shell found!", null));
                    return;
                }

                shellProcess = Runtime.getRuntime().exec(shell);
                
                // Read output
                BufferedReader reader = new BufferedReader(
                    new InputStreamReader(shellProcess.getInputStream()));
                
                String line;
                while ((line = reader.readLine()) != null && shellProcess.isAlive()) {
                    final String outputLine = line + "\n";
                    runOnUiThread(() -> {
                        if (terminalOutput != null) {
                            terminalOutput.append(outputLine);
                        }
                    });
                }
                
                // Read any remaining output
                while ((line = reader.readLine()) != null) {
                    final String outputLine = line + "\n";
                    runOnUiThread(() -> {
                        if (terminalOutput != null) {
                            terminalOutput.append(outputLine);
                        }
                    });
                }

                Log.d(TAG, "Shell process finished");
                
            } catch (Exception e) {
                Log.e(TAG, "Error in shell process", e);
                runOnUiThread(() -> {
                    if (!hasError && terminalOutput != null) {
                        terminalOutput.append("\nError: " + e.getMessage() + "\n");
                    }
                });
            }
        }).start();
    }

    private void setupTerminalView(LinearLayout rootLayout) {
        try {
            Log.d(TAG, "Setting up TerminalView...");
            
            // Create TerminalView
            terminalView = new TerminalView(this, null);
            terminalView.setId(R.id.terminal_view);
            terminalView.setBackgroundColor(0xFF000000);

            terminalView.setLayoutParams(new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT, 1.0f));

            rootLayout.addView(terminalView);

            // Set this as the client
            terminalView.setTerminalViewClient(this);

            // Initialize terminal with delay
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                if (!hasError) {
                    initializeTerminal();
                }
            }, 200);

            Log.d(TAG, "TerminalView setup completed");

        } catch (Exception e) {
            Log.e(TAG, "Error setting up TerminalView", e);
            showErrorAndExit("Failed to setup TerminalView: " + e.getMessage(), e);
        }
    }

    private void initializeTerminal() {
        if (hasError) return;
        
        try {
            Log.d(TAG, "Initializing terminal...");
            
            String shell = findShell();
            if (shell == null) {
                showErrorAndExit("No shell found on this device!", null);
                return;
            }
            
            Log.d(TAG, "Shell found: " + shell);

            String[] processArgs = {shell};
            String[] env = getEnv();
            
            Log.d(TAG, "Creating TerminalSession...");
            terminalSession = new TerminalSession(shell, "/", processArgs, env, 1024, this);
            
            runOnUiThread(() -> {
                try {
                    Log.d(TAG, "Attaching session to TerminalView...");
                    terminalView.attachSession(terminalSession);
                    Toast.makeText(TerminalActivity.this, "Terminal initialized", Toast.LENGTH_SHORT).show();
                    Log.d(TAG, "Terminal initialized successfully");
                } catch (Exception e) {
                    Log.e(TAG, "Error attaching session", e);
                    showErrorAndExit("Failed to attach terminal: " + e.getMessage(), e);
                }
            });

        } catch (Exception e) {
            Log.e(TAG, "Error initializing terminal", e);
            showErrorAndExit("Failed to initialize terminal: " + e.getMessage(), e);
        }
    }

    private String findShell() {
        String[] shells = {"/system/bin/sh", "/bin/sh"};
        for (String s : shells) {
            File file = new File(s);
            if (file.exists()) {
                return s;
            }
        }
        return null;
    }

    private String[] getEnv() {
        return new String[] {
            "PATH=/system/bin:/system/xbin:/bin",
            "TERM=xterm-256color",
            "HOME=" + getFilesDir().getAbsolutePath(),
            "LANG=C.UTF-8",
            "PWD=" + getFilesDir().getAbsolutePath()
        };
    }

    private void showErrorAndExit(String message, Exception e) {
        hasError = true;
        
        String fullMessage = message;
        if (e != null) {
            fullMessage += "\n\nError: " + e.getClass().getSimpleName();
            Log.e(TAG, "Full error details", e);
        }
        
        final String errorMessage = fullMessage;
        
        new Handler(Looper.getMainLooper()).post(() -> {
            try {
                new AlertDialog.Builder(this)
                    .setTitle("Terminal Error")
                    .setMessage(errorMessage)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setPositiveButton("OK", (dialog, which) -> finish())
                    .setCancelable(false)
                    .show();
            } catch (Exception ex) {
                Log.e(TAG, "Error showing dialog", ex);
                Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
                finish();
            }
        });
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
        Log.d(TAG, "Emulator set");
    }

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

    @Override
    public void onTextChanged(TerminalSession changedSession) {
    }

    @Override
    public void onTitleChanged(TerminalSession changedSession) {
    }

    @Override
    public void onSessionFinished(TerminalSession finishedSession) {
        runOnUiThread(() -> {
            Toast.makeText(this, "Session finished", Toast.LENGTH_SHORT).show();
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
        Log.d(TAG, "Terminal shell PID: " + pid);
    }

    @Override
    public Integer getTerminalCursorStyle() {
        return android.graphics.Typeface.NORMAL;
    }

    public void onScreenPaused() {
    }

    public void onScreenResumed() {
    }
    
    @Override
    protected void onDestroy() {
        Log.d(TAG, "TerminalActivity onDestroy");
        if (terminalSession != null) {
            try {
                terminalSession.finishIfRunning();
            } catch (Exception e) {
                Log.e(TAG, "Error finishing session", e);
            }
        }
        if (shellProcess != null) {
            try {
                shellProcess.destroy();
            } catch (Exception e) {
                Log.e(TAG, "Error destroying process", e);
            }
        }
        super.onDestroy();
    }
}
