package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFilePort;
import com.digitalemployee.domain.conversation.model.valobj.WorkspaceFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class WorkspaceFilePort implements IWorkspaceFilePort {

    private final Path workspaceRoot;

    public WorkspaceFilePort(@Value("${digital-employee.workspace.root:${user.dir}}") String workspaceRoot) {
        this.workspaceRoot = Path.of(workspaceRoot).toAbsolutePath().normalize();
    }

    @Override
    public WorkspaceFileVO readFile(String relativePath) {
        Path resolved = workspaceRoot.resolve(relativePath).normalize();
        if (!resolved.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("file path must stay inside the workspace");
        }
        if (!Files.isRegularFile(resolved)) {
            throw new IllegalArgumentException("file does not exist: " + relativePath);
        }
        try {
            return WorkspaceFileVO.builder()
                    .path(workspaceRoot.relativize(resolved).toString())
                    .content(Files.readString(resolved, StandardCharsets.UTF_8))
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("failed to read file: " + relativePath, e);
        }
    }

}
