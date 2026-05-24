package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFileWritePort;
import com.digitalemployee.domain.conversation.model.valobj.WorkspaceFileVO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

@Service
public class WorkspaceFileWritePort implements IWorkspaceFileWritePort {

    private final Path workspaceRoot;

    public WorkspaceFileWritePort(@Value("${digital-employee.workspace.root:${user.dir}}") String workspaceRoot) {
        this.workspaceRoot = Path.of(workspaceRoot).toAbsolutePath().normalize();
    }

    @Override
    public WorkspaceFileVO writeFile(String relativePath, String content) {
        Path resolved = workspaceRoot.resolve(relativePath).normalize();
        if (!resolved.startsWith(workspaceRoot)) {
            throw new IllegalArgumentException("file path must stay inside the workspace");
        }
        try {
            Path parent = resolved.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            String safeContent = content == null ? "" : content;
            Files.writeString(resolved, safeContent, StandardCharsets.UTF_8);
            return WorkspaceFileVO.builder()
                    .path(workspaceRoot.relativize(resolved).toString())
                    .content(safeContent)
                    .build();
        } catch (IOException e) {
            throw new IllegalStateException("failed to write file: " + relativePath, e);
        }
    }

}
