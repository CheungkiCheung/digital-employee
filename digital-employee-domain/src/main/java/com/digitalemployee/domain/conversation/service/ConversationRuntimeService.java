package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.adapter.port.IMemoryContextPort;
import com.digitalemployee.domain.conversation.adapter.port.IShellCommandPort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFilePort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFileWritePort;
import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.entity.ToolExecutionEntity;
import com.digitalemployee.domain.conversation.model.valobj.ConversationMessageRoleVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionTypeVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.PermissionBehaviorVO;
import com.digitalemployee.domain.conversation.model.valobj.PermissionDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ShellCommandResultVO;
import com.digitalemployee.domain.conversation.model.valobj.WorkspaceFileVO;

import java.util.ArrayList;
import java.util.List;

public class ConversationRuntimeService {

    private static final String FILE_READ_TOOL_NAME = "file_read";
    private static final String FILE_WRITE_TOOL_NAME = "file_write";
    private static final String FILE_EDIT_TOOL_NAME = "file_edit";
    private static final String BASH_TOOL_NAME = "bash";

    private final IWorkspaceFilePort workspaceFilePort;
    private final IWorkspaceFileWritePort workspaceFileWritePort;
    private final IShellCommandPort shellCommandPort;
    private final IModelDecisionPort modelDecisionPort;
    private final IMemoryContextPort memoryContextPort;
    private final PermissionDomainService permissionDomainService;
    private final ToolRegistryDomainService toolRegistryDomainService;

    public ConversationRuntimeService(IWorkspaceFilePort workspaceFilePort, IModelDecisionPort modelDecisionPort) {
        this(workspaceFilePort, unsupportedWritePort(), disabledShellCommandPort(), modelDecisionPort, conversationId -> "<memory_context>\n</memory_context>");
    }

    public ConversationRuntimeService(IWorkspaceFilePort workspaceFilePort,
                                      IWorkspaceFileWritePort workspaceFileWritePort,
                                      IModelDecisionPort modelDecisionPort,
                                      IMemoryContextPort memoryContextPort) {
        this(workspaceFilePort, workspaceFileWritePort, disabledShellCommandPort(), modelDecisionPort, memoryContextPort);
    }

    public ConversationRuntimeService(IWorkspaceFilePort workspaceFilePort,
                                      IWorkspaceFileWritePort workspaceFileWritePort,
                                      IShellCommandPort shellCommandPort,
                                      IModelDecisionPort modelDecisionPort,
                                      IMemoryContextPort memoryContextPort) {
        this.workspaceFilePort = workspaceFilePort;
        this.workspaceFileWritePort = workspaceFileWritePort;
        this.shellCommandPort = shellCommandPort;
        this.modelDecisionPort = modelDecisionPort;
        this.memoryContextPort = memoryContextPort;
        this.permissionDomainService = new PermissionDomainService();
        this.toolRegistryDomainService = ToolRegistryDomainService.defaultRegistry();
    }

    public ConversationTurnAggregate handleUserMessage(String conversationId, String message) {
        List<ConversationMessageEntity> messages = new ArrayList<>();
        List<ToolExecutionEntity> toolExecutions = new ArrayList<>();
        messages.add(record(ConversationMessageRoleVO.USER, message));

        ModelDecisionRequestVO decisionRequest = ModelDecisionRequestVO.builder()
                .conversationId(conversationId)
                .userMessage(message)
                .memoryContext(memoryContextPort.loadMemoryContext(conversationId))
                .availableTools(toolRegistryDomainService.listDescriptors())
                .build();
        ModelDecisionVO decision = modelDecisionPort.decideNextAction(decisionRequest);
        if (decision.getType() != ModelDecisionTypeVO.TOOL_CALL) {
            String answer = decision.getDirectAnswer();
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(answer)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        if (!toolRegistryDomainService.isRegistered(decision.getToolName())) {
            String answer = "unsupported tool: " + decision.getToolName();
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(answer)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        if (!decision.isToolCall(FILE_READ_TOOL_NAME)) {
            if (decision.isToolCall(FILE_WRITE_TOOL_NAME)) {
                return handleFileWrite(conversationId, decision, messages, toolExecutions);
            }
            if (decision.isToolCall(FILE_EDIT_TOOL_NAME)) {
                return handleFileEdit(conversationId, decision, messages, toolExecutions);
            }
            if (decision.isToolCall(BASH_TOOL_NAME)) {
                return handleBash(conversationId, decision, messages, toolExecutions);
            }
            String answer = "unsupported tool: " + decision.getToolName();
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(answer)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        String requestedPath = decision.getToolInput();
        PermissionDecisionVO permissionDecision = permissionDomainService.decideFileRead(requestedPath);
        messages.add(record(ConversationMessageRoleVO.TOOL_CALL, FILE_READ_TOOL_NAME + ":" + requestedPath));

        if (permissionDecision.getBehavior() != PermissionBehaviorVO.ALLOW) {
            String result = "permission denied: " + permissionDecision.getReason();
            toolExecutions.add(toolExecution(FILE_READ_TOOL_NAME, requestedPath, permissionDecision, result));
            messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(result)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        WorkspaceFileVO file = workspaceFilePort.readFile(requestedPath);
        String preview = file.preview(1200);
        String result = "read " + file.getPath() + "\n" + preview;
        String answer = "已读取 " + file.getPath() + "，内容摘要：\n" + preview;
        toolExecutions.add(toolExecution(FILE_READ_TOOL_NAME, requestedPath, permissionDecision, result));
        messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
        messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));

        return ConversationTurnAggregate.builder()
                .conversationId(conversationId)
                .answer(answer)
                .messages(messages)
                .toolExecutions(toolExecutions)
                .build();
    }

    private ConversationTurnAggregate handleBash(String conversationId,
                                                 ModelDecisionVO decision,
                                                 List<ConversationMessageEntity> messages,
                                                 List<ToolExecutionEntity> toolExecutions) {
        String command = decision.getToolInput() == null ? "" : decision.getToolInput().trim();
        PermissionDecisionVO permissionDecision = permissionDomainService.decideBashCommand(command);
        messages.add(record(ConversationMessageRoleVO.TOOL_CALL, BASH_TOOL_NAME + ":" + command));

        if (permissionDecision.getBehavior() != PermissionBehaviorVO.ALLOW) {
            String result = "permission denied: " + permissionDecision.getReason();
            toolExecutions.add(toolExecution(BASH_TOOL_NAME, command, permissionDecision, result));
            messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(result)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        ShellCommandResultVO shellResult = shellCommandPort.execute(command);
        String result = shellResult.summarize();
        toolExecutions.add(toolExecution(BASH_TOOL_NAME, command, permissionDecision, result));
        messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
        messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
        return ConversationTurnAggregate.builder()
                .conversationId(conversationId)
                .answer(result)
                .messages(messages)
                .toolExecutions(toolExecutions)
                .build();
    }

    private ConversationTurnAggregate handleFileEdit(String conversationId,
                                                     ModelDecisionVO decision,
                                                     List<ConversationMessageEntity> messages,
                                                     List<ToolExecutionEntity> toolExecutions) {
        FileEditInput input = parseFileEditInput(decision.getToolInput());
        PermissionDecisionVO permissionDecision = permissionDomainService.decideFileWrite(input.path());
        messages.add(record(ConversationMessageRoleVO.TOOL_CALL, FILE_EDIT_TOOL_NAME + ":" + input.path()));

        if (permissionDecision.getBehavior() != PermissionBehaviorVO.ALLOW) {
            String result = "permission denied: " + permissionDecision.getReason();
            toolExecutions.add(toolExecution(FILE_EDIT_TOOL_NAME, input.path(), permissionDecision, result));
            messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(result)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        WorkspaceFileVO current = workspaceFilePort.readFile(input.path());
        if (!current.getContent().contains(input.oldText())) {
            String result = "edit failed: expected text not found";
            toolExecutions.add(toolExecution(FILE_EDIT_TOOL_NAME, input.path(), permissionDecision, result));
            messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(result)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        String updatedContent = current.getContent().replace(input.oldText(), input.newText());
        WorkspaceFileVO updated = workspaceFileWritePort.writeFile(input.path(), updatedContent);
        String result = "edited " + updated.getPath();
        String answer = "已编辑 " + updated.getPath();
        toolExecutions.add(toolExecution(FILE_EDIT_TOOL_NAME, input.path(), permissionDecision, result));
        messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
        messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));
        return ConversationTurnAggregate.builder()
                .conversationId(conversationId)
                .answer(answer)
                .messages(messages)
                .toolExecutions(toolExecutions)
                .build();
    }

    private ConversationTurnAggregate handleFileWrite(String conversationId,
                                                      ModelDecisionVO decision,
                                                      List<ConversationMessageEntity> messages,
                                                      List<ToolExecutionEntity> toolExecutions) {
        FileWriteInput input = parseFileWriteInput(decision.getToolInput());
        PermissionDecisionVO permissionDecision = permissionDomainService.decideFileWrite(input.path());
        messages.add(record(ConversationMessageRoleVO.TOOL_CALL, FILE_WRITE_TOOL_NAME + ":" + input.path()));

        if (permissionDecision.getBehavior() != PermissionBehaviorVO.ALLOW) {
            String result = "permission denied: " + permissionDecision.getReason();
            toolExecutions.add(toolExecution(FILE_WRITE_TOOL_NAME, input.path(), permissionDecision, result));
            messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
            messages.add(record(ConversationMessageRoleVO.ASSISTANT, result));
            return ConversationTurnAggregate.builder()
                    .conversationId(conversationId)
                    .answer(result)
                    .messages(messages)
                    .toolExecutions(toolExecutions)
                    .build();
        }

        WorkspaceFileVO file = workspaceFileWritePort.writeFile(input.path(), input.content());
        String result = "wrote " + file.getPath();
        String answer = "已写入 " + file.getPath();
        toolExecutions.add(toolExecution(FILE_WRITE_TOOL_NAME, input.path(), permissionDecision, result));
        messages.add(record(ConversationMessageRoleVO.TOOL_RESULT, result));
        messages.add(record(ConversationMessageRoleVO.ASSISTANT, answer));
        return ConversationTurnAggregate.builder()
                .conversationId(conversationId)
                .answer(answer)
                .messages(messages)
                .toolExecutions(toolExecutions)
                .build();
    }

    private FileWriteInput parseFileWriteInput(String toolInput) {
        if (toolInput == null) {
            return new FileWriteInput("", "");
        }
        int newlineIndex = toolInput.indexOf('\n');
        if (newlineIndex < 0) {
            return new FileWriteInput(toolInput.trim(), "");
        }
        return new FileWriteInput(
                toolInput.substring(0, newlineIndex).trim(),
                toolInput.substring(newlineIndex + 1)
        );
    }

    private FileEditInput parseFileEditInput(String toolInput) {
        if (toolInput == null) {
            return new FileEditInput("", "", "");
        }
        String[] parts = toolInput.split("\n", 3);
        String path = parts.length > 0 ? parts[0].trim() : "";
        String oldText = parts.length > 1 ? parts[1] : "";
        String newText = parts.length > 2 ? parts[2] : "";
        return new FileEditInput(path, oldText, newText);
    }

    private ConversationMessageEntity record(ConversationMessageRoleVO role, String content) {
        return ConversationMessageEntity.builder()
                .role(role)
                .content(content)
                .build();
    }

    private ToolExecutionEntity toolExecution(String toolName, String input, PermissionDecisionVO decision, String result) {
        return ToolExecutionEntity.builder()
                .toolName(toolName)
                .input(input)
                .permissionDecision(decision)
                .result(result)
                .build();
    }

    private static IWorkspaceFileWritePort unsupportedWritePort() {
        return (relativePath, content) -> {
            throw new UnsupportedOperationException("workspace file write port is not configured");
        };
    }

    private static IShellCommandPort disabledShellCommandPort() {
        return command -> ShellCommandResultVO.builder()
                .command(command)
                .exitCode(126)
                .stdout("")
                .stderr("bash execution is not enabled yet: " + command)
                .build();
    }

    private record FileWriteInput(String path, String content) {
    }

    private record FileEditInput(String path, String oldText, String newText) {
    }

}
