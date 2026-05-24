package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.adapter.port.IShellCommandPort;
import com.digitalemployee.domain.conversation.model.valobj.ShellCommandResultVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

@Service
public class WorkspaceShellCommandPort implements IShellCommandPort {

    private static final Duration COMMAND_TIMEOUT = Duration.ofSeconds(3);

    private final Path workspaceRoot;

    public WorkspaceShellCommandPort(@Value("${digital-employee.workspace.root:${user.dir}}") String workspaceRoot) {
        this.workspaceRoot = Path.of(workspaceRoot).toAbsolutePath().normalize();
    }

    @Override
    public ShellCommandResultVO execute(String command) {
        List<String> safeCommand = parseSafeCommand(command);
        try {
            Process process = new ProcessBuilder(safeCommand)
                    .directory(workspaceRoot.toFile())
                    .redirectErrorStream(false)
                    .start();
            boolean finished = process.waitFor(COMMAND_TIMEOUT.toMillis(), TimeUnit.MILLISECONDS);
            if (!finished) {
                process.destroyForcibly();
                return ShellCommandResultVO.builder()
                        .command(command)
                        .exitCode(124)
                        .stdout("")
                        .stderr("command timed out")
                        .build();
            }
            return ShellCommandResultVO.builder()
                    .command(command)
                    .exitCode(process.exitValue())
                    .stdout(new String(process.getInputStream().readAllBytes(), StandardCharsets.UTF_8))
                    .stderr(new String(process.getErrorStream().readAllBytes(), StandardCharsets.UTF_8))
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("failed to execute shell command", e);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("shell command interrupted", e);
        }
    }

    private List<String> parseSafeCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            throw new IllegalArgumentException("shell command is required");
        }
        if (containsShellControlSyntax(command)) {
            throw new IllegalArgumentException("shell command is outside the safe allowlist");
        }
        String[] tokens = command.trim().split("\\s+");
        String executable = tokens[0];
        if ("pwd".equals(executable) && tokens.length == 1) {
            return List.of("pwd");
        }
        if ("ls".equals(executable) && tokens.length <= 2) {
            return commandWithOptionalWorkspacePath(tokens);
        }
        if ("cat".equals(executable) && tokens.length == 2) {
            return commandWithOptionalWorkspacePath(tokens);
        }
        throw new IllegalArgumentException("shell command is outside the safe allowlist");
    }

    private List<String> commandWithOptionalWorkspacePath(String[] tokens) {
        List<String> safeCommand = new ArrayList<>();
        safeCommand.add(tokens[0]);
        if (tokens.length == 2) {
            safeCommand.add(resolveWorkspacePath(tokens[1]).toString());
        }
        return safeCommand;
    }

    private Path resolveWorkspacePath(String relativePath) {
        Path resolved = workspaceRoot.resolve(relativePath).normalize();
        if (!resolved.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("shell command path must stay inside the workspace");
        }
        if (!Files.exists(resolved)) {
            throw new IllegalArgumentException("shell command path does not exist: " + relativePath);
        }
        return resolved;
    }

    private boolean containsShellControlSyntax(String command) {
        return Arrays.asList("&&", "||", ";", "|", ">", "<", "`", "$(").stream()
                .anyMatch(command::contains);
    }

}
