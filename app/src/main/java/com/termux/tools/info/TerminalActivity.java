package com.termux.tools.info;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.HorizontalScrollView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayDeque;
import java.util.Deque;

public class TerminalActivity extends AppCompatActivity {

    private TextView terminalOutput;
    private TextInputEditText commandInput;
    private Button runButton;
    private FloatingActionButton fabClear;
    private ScrollView scrollView;
    
    private StringBuilder outputBuffer = new StringBuilder();
    private Deque<String> commandHistory = new ArrayDeque<>();
    private static final int MAX_HISTORY = 50;
    
    private final Handler handler = new Handler(Looper.getMainLooper());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terminal);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Terminal");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        toolbar.setNavigationOnClickListener(v -> finish());

        terminalOutput = findViewById(R.id.terminalOutput);
        commandInput = findViewById(R.id.commandInput);
        runButton = findViewById(R.id.runButton);
        fabClear = findViewById(R.id.fabClear);
        scrollView = findViewById(R.id.scrollView);

        // Welcome message
        printWelcome();

        runButton.setOnClickListener(v -> executeCommand());
        
        commandInput.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_SEND || 
                (event != null && event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                executeCommand();
                return true;
            }
            return false;
        });

        fabClear.setOnClickListener(v -> clearTerminal());
    }

    private void printWelcome() {
        String welcome = "╔═══════════════════════════════════════════════════════════╗\n" +
                         "║           TermuxToolsInfo - Embedded Terminal             ║\n" +
                         "║                    Version 1.0                            ║\n" +
                         "╚═══════════════════════════════════════════════════════════╝\n\n" +
                         "Type 'help' for available commands.\n\n";
        appendOutput(welcome);
    }

    private void executeCommand() {
        String command = commandInput.getText().toString().trim();
        if (command.isEmpty()) {
            return;
        }

        // Add to history
        addToHistory(command);
        
        // Display command
        appendOutput("$ " + command + "\n");
        commandInput.setText("");

        // Execute in background
        new Thread(() -> {
            try {
                String result = runCommand(command);
                handler.post(() -> appendOutput(result + "\n"));
            } catch (Exception e) {
                handler.post(() -> appendOutput("Error: " + e.getMessage() + "\n"));
            }
        }).start();
    }

    private String runCommand(String command) {
        StringBuilder output = new StringBuilder();
        try {
            // Handle built-in commands
            if (command.equals("help")) {
                return getHelpText();
            } else if (command.equals("clear") || command.equals("cls")) {
                runOnUiThread(this::clearTerminal);
                return "";
            } else if (command.equals("exit")) {
                runOnUiThread(this::finish);
                return "Exiting terminal...";
            } else if (command.equals("history")) {
                return getHistory();
            } else if (command.startsWith("echo ")) {
                return command.substring(5);
            } else if (command.equals("pwd")) {
                return System.getProperty("user.dir", "/data/data/com.termux/files/home");
            } else if (command.equals("whoami")) {
                return "u0_a" + android.os.Process.myUid();
            } else if (command.equals("id")) {
                return "uid=" + android.os.Process.myUid() + "(shell) gid=" + android.os.Process.myUid() + "(shell)";
            } else if (command.equals("date")) {
                return new java.text.SimpleDateFormat("EEE MMM dd HH:mm:ss zzz yyyy", java.util.Locale.US)
                    .format(new java.util.Date());
            } else if (command.equals("uname")) {
                return "Linux";
            } else if (command.equals("uname -a")) {
                return "Linux localhost " + System.getProperty("os.version") + 
                       " #1 SMP PREEMPT " + android.os.Build.VERSION.RELEASE + 
                       " " + System.getProperty("os.arch");
            } else if (command.equals("termux-info")) {
                return getTermuxInfo();
            } else if (command.equals("packages")) {
                return getPackageList();
            } else {
                // Try to execute as shell command
                Process process = Runtime.getRuntime().exec(new String[]{"sh", "-c", command});
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                BufferedReader errorReader = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                
                String line;
                while ((line = reader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                while ((line = errorReader.readLine()) != null) {
                    output.append(line).append("\n");
                }
                
                try {
                    process.waitFor();
                } catch (InterruptedException e) {
                    output.append("Command interrupted\n");
                }
                
                if (output.length() == 0) {
                    output.append("(command executed)\n");
                }
            }
        } catch (IOException e) {
            output.append("Error executing command: ").append(e.getMessage()).append("\n");
        }
        return output.toString();
    }

    private String getHelpText() {
        return "Available Commands:\n" +
               "  help          - Show this help message\n" +
               "  clear/cls     - Clear terminal screen\n" +
               "  exit          - Exit terminal\n" +
               "  history       - Show command history\n" +
               "  pwd           - Print working directory\n" +
               "  whoami        - Print current user\n" +
               "  id            - Print user ID\n" +
               "  date          - Print current date/time\n" +
               "  uname         - Print system information\n" +
               "  uname -a      - Print all system info\n" +
               "  termux-info   - Show TermuxToolsInfo details\n" +
               "  packages      - List available packages\n" +
               "  echo <text>   - Print text\n" +
               "  <shell cmd>   - Execute any shell command\n\n";
    }

    private String getTermuxInfo() {
        StringBuilder info = new StringBuilder();
        info.append("=== TermuxToolsInfo ===\n");
        info.append("App Version: 1.0\n");
        info.append("Android Version: ").append(android.os.Build.VERSION.RELEASE).append("\n");
        info.append("SDK Level: ").append(android.os.Build.VERSION.SDK_INT).append("\n");
        info.append("Device: ").append(android.os.Build.MODEL).append("\n");
        info.append("Manufacturer: ").append(android.os.Build.MANUFACTURER).append("\n");
        info.append("Architecture: ").append(System.getProperty("os.arch", "unknown")).append("\n");
        info.append("Java Version: ").append(System.getProperty("java.version", "unknown")).append("\n");
        info.append("User Home: ").append(System.getProperty("user.home", "/data/data/com.termux/files/home")).append("\n");
        return info.toString();
    }

    private String getPackageList() {
        return "Popular packages available:\n" +
               "  python, python2, python3    - Python interpreters\n" +
               "  nodejs                      - Node.js runtime\n" +
               "  git                         - Git version control\n" +
               "  vim, nano, emacs            - Text editors\n" +
               "  clang, llvm                 - C/C++ compilers\n" +
               "  ruby, php, golang           - Other languages\n" +
               "  openssh                     - SSH client/server\n" +
               "  curl, wget                  - Download tools\n" +
               "  htop, neofetch              - System monitoring\n" +
               "  jq, ripgrep, fd             - Utilities\n";
    }

    private String getHistory() {
        if (commandHistory.isEmpty()) {
            return "No command history.\n";
        }
        StringBuilder history = new StringBuilder("Command History:\n");
        int i = 1;
        for (String cmd : commandHistory) {
            history.append(i++).append(". ").append(cmd).append("\n");
        }
        return history.toString();
    }

    private void addToHistory(String command) {
        commandHistory.addLast(command);
        if (commandHistory.size() > MAX_HISTORY) {
            commandHistory.removeFirst();
        }
    }

    private void appendOutput(String text) {
        outputBuffer.append(text);
        terminalOutput.setText(outputBuffer.toString());
        // Auto-scroll to bottom
        handler.postDelayed(() -> {
            scrollView.fullScroll(View.FOCUS_DOWN);
        }, 100);
    }

    private void clearTerminal() {
        outputBuffer.setLength(0);
        terminalOutput.setText("");
    }

    @Override
    public void onBackPressed() {
        // Confirm exit
        new androidx.appcompat.app.AlertDialog.Builder(this)
            .setTitle("Exit Terminal")
            .setMessage("Do you want to exit the terminal?")
            .setPositiveButton("Yes", (dialog, which) -> finish())
            .setNegativeButton("No", null)
            .show();
    }
}
