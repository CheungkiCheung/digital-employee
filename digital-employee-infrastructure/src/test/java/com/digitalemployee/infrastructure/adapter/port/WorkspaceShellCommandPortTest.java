package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.model.valobj.ShellCommandResultVO;
import org.junit.Assert;
import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

public class WorkspaceShellCommandPortTest {

    @Test
    public void shouldExecutePwdInsideWorkspaceRoot() throws Exception {
        Path workspaceRoot = Files.createTempDirectory("digital-employee-shell-");
        WorkspaceShellCommandPort port = new WorkspaceShellCommandPort(workspaceRoot.toString());

        ShellCommandResultVO result = port.execute("pwd");

        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals(workspaceRoot.toRealPath().toString(), result.getStdout().trim());
    }

    @Test
    public void shouldExecuteReadOnlyCommandAndCaptureStdout() throws Exception {
        Path workspaceRoot = Files.createTempDirectory("digital-employee-shell-");
        Files.writeString(workspaceRoot.resolve("note.txt"), "hello shell", StandardCharsets.UTF_8);
        WorkspaceShellCommandPort port = new WorkspaceShellCommandPort(workspaceRoot.toString());

        ShellCommandResultVO result = port.execute("cat note.txt");

        Assert.assertEquals(0, result.getExitCode());
        Assert.assertEquals("hello shell", result.getStdout().trim());
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectUnsupportedCommandEvenWhenCalledDirectly() throws Exception {
        Path workspaceRoot = Files.createTempDirectory("digital-employee-shell-");
        WorkspaceShellCommandPort port = new WorkspaceShellCommandPort(workspaceRoot.toString());

        port.execute("echo unsafe");
    }

    @Test(expected = IllegalArgumentException.class)
    public void shouldRejectTraversalWhenCalledDirectly() throws Exception {
        Path workspaceRoot = Files.createTempDirectory("digital-employee-shell-");
        WorkspaceShellCommandPort port = new WorkspaceShellCommandPort(workspaceRoot.toString());

        port.execute("cat ../secret.txt");
    }

}
