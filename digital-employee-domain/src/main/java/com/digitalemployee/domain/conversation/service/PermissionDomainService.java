package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.PermissionDecisionVO;

public class PermissionDomainService {

    private static final String UNSAFE_COMMAND_REASON = "bash command is outside the safe allowlist";

    public PermissionDecisionVO decideFileRead(String relativePath) {
        PermissionDecisionVO invalidDecision = decideWorkspacePath(relativePath);
        if (invalidDecision.getBehavior() == PermissionBehaviorVO.DENY) {
            return invalidDecision;
        }
        return PermissionDecisionVO.builder()
                .behavior(PermissionBehaviorVO.ALLOW)
                .reason("workspace file read is allowed")
                .build();
    }

    public PermissionDecisionVO decideFileWrite(String relativePath) {
        PermissionDecisionVO invalidDecision = decideWorkspacePath(relativePath);
        if (invalidDecision.getBehavior() == PermissionBehaviorVO.DENY) {
            return invalidDecision;
        }
        return PermissionDecisionVO.builder()
                .behavior(PermissionBehaviorVO.ALLOW)
                .reason("workspace file write is allowed")
                .build();
    }

    public PermissionDecisionVO decideBashCommand(String command) {
        if (command == null || command.trim().isEmpty()) {
            return deny("bash command is required");
        }
        String normalized = command.trim();
        if (containsShellControlSyntax(normalized)) {
            return deny(UNSAFE_COMMAND_REASON);
        }

        String[] tokens = normalized.split("\\s+");
        String executable = tokens[0];
        if ("pwd".equals(executable)) {
            return tokens.length == 1 ? allowBash() : deny(UNSAFE_COMMAND_REASON);
        }
        if ("ls".equals(executable)) {
            return decideSafeBashPathArgument(tokens, 1);
        }
        if ("cat".equals(executable)) {
            return tokens.length == 2 ? decideSafeBashPathArgument(tokens, 1) : deny(UNSAFE_COMMAND_REASON);
        }
        return deny(UNSAFE_COMMAND_REASON);
    }

    private PermissionDecisionVO decideWorkspacePath(String relativePath) {
        if (relativePath == null || relativePath.trim().isEmpty()) {
            return deny("file path is required");
        }
        String normalized = relativePath.replace('\\', '/');
        if (normalized.startsWith("/") || hasParentDirectorySegment(normalized)) {
            return deny("file path must stay inside the workspace");
        }
        return PermissionDecisionVO.builder()
                .behavior(PermissionBehaviorVO.ALLOW)
                .reason("workspace path is allowed")
                .build();
    }

    private PermissionDecisionVO allowBash() {
        return PermissionDecisionVO.builder()
                .behavior(PermissionBehaviorVO.ALLOW)
                .reason("safe bash command is allowed")
                .build();
    }

    private PermissionDecisionVO decideSafeBashPathArgument(String[] tokens, int pathStartIndex) {
        if (tokens.length == pathStartIndex) {
            return allowBash();
        }
        if (tokens.length != pathStartIndex + 1) {
            return deny(UNSAFE_COMMAND_REASON);
        }
        PermissionDecisionVO pathDecision = decideWorkspacePath(tokens[pathStartIndex]);
        if (pathDecision.getBehavior() == PermissionBehaviorVO.DENY) {
            return deny(UNSAFE_COMMAND_REASON);
        }
        return allowBash();
    }

    private boolean containsShellControlSyntax(String command) {
        return command.contains("&&")
                || command.contains("||")
                || command.contains(";")
                || command.contains("|")
                || command.contains(">")
                || command.contains("<")
                || command.contains("`")
                || command.contains("$(");
    }

    private PermissionDecisionVO deny(String reason) {
        return PermissionDecisionVO.builder()
                .behavior(PermissionBehaviorVO.DENY)
                .reason(reason)
                .build();
    }

    private boolean hasParentDirectorySegment(String normalizedPath) {
        if (normalizedPath.equals("..")) {
            return true;
        }
        String[] segments = normalizedPath.split("/");
        for (String segment : segments) {
            if ("..".equals(segment)) {
                return true;
            }
        }
        return false;
    }

}
