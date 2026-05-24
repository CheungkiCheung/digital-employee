package com.digitalemployee.domain.conversation.service;

import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.adapter.port.IShellCommandPort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFilePort;
import com.digitalemployee.domain.conversation.adapter.port.IWorkspaceFileWritePort;
import com.digitalemployee.domain.conversation.model.aggregate.ConversationTurnAggregate;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionTypeVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ShellCommandResultVO;
import com.digitalemployee.domain.conversation.model.valobj.WorkspaceFileVO;
import org.junit.Assert;
import org.junit.Test;

public class ConversationRuntimeServiceTest {

    @Test
    public void shouldReturnExplicitAnswerWhenModelRequestsUnsupportedTool() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("unknown_tool")
                .toolInput("input")
                .build();
        RecordingWorkspaceFilePort workspaceFilePort = new RecordingWorkspaceFilePort();
        ConversationRuntimeService service = new ConversationRuntimeService(workspaceFilePort, modelDecisionPort);

        ConversationTurnAggregate turn = service.handleUserMessage("conv-tools", "用未知工具");

        Assert.assertEquals("unsupported tool: unknown_tool", turn.getAnswer());
        Assert.assertTrue(turn.getToolExecutions().isEmpty());
        Assert.assertFalse(workspaceFilePort.wasCalled());
    }

    @Test
    public void shouldPassAvailableToolDescriptorsToModelDecisionPort() {
        CapturingModelDecisionPort modelDecisionPort = new CapturingModelDecisionPort();
        ConversationRuntimeService service = new ConversationRuntimeService(new RecordingWorkspaceFilePort(), modelDecisionPort);

        service.handleUserMessage("conv-context", "hello");

        Assert.assertEquals("conv-context", modelDecisionPort.request.getConversationId());
        Assert.assertEquals("hello", modelDecisionPort.request.getUserMessage());
        Assert.assertTrue(modelDecisionPort.request.getAvailableTools().stream()
                .anyMatch(tool -> "file_read".equals(tool.getName())));
    }

    @Test
    public void shouldPassMemoryContextToModelDecisionPort() {
        CapturingModelDecisionPort modelDecisionPort = new CapturingModelDecisionPort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                new RecordingWorkspaceFilePort(),
                new RecordingWorkspaceFileWritePort(),
                modelDecisionPort,
                conversationId -> "<memory_context>\n[PROJECT] Harness: keep WIP one\n</memory_context>"
        );

        service.handleUserMessage("conv-memory", "下一步做什么");

        Assert.assertTrue(modelDecisionPort.request.getMemoryContext().contains("[PROJECT] Harness"));
    }

    @Test
    public void shouldWriteWorkspaceFileWhenModelRequestsRegisteredFileWriteTool() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("file_write")
                .toolInput("notes/todo.txt\nShip the DDD runtime")
                .build();
        RecordingWorkspaceFileWritePort writePort = new RecordingWorkspaceFileWritePort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                new RecordingWorkspaceFilePort(),
                writePort,
                modelDecisionPort,
                conversationId -> "<memory_context>\n</memory_context>"
        );

        ConversationTurnAggregate turn = service.handleUserMessage("conv-write", "写入 notes/todo.txt");

        Assert.assertEquals("notes/todo.txt", writePort.path);
        Assert.assertEquals("Ship the DDD runtime", writePort.content);
        Assert.assertTrue(turn.getAnswer().contains("已写入 notes/todo.txt"));
        Assert.assertEquals("file_write", turn.getToolExecutions().get(0).getToolName());
    }

    @Test
    public void shouldEditWorkspaceFileWhenModelRequestsRegisteredFileEditTool() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("file_edit")
                .toolInput("notes/todo.txt\nold task\nnew task")
                .build();
        RecordingWorkspaceFilePort readPort = new RecordingWorkspaceFilePort("old task");
        RecordingWorkspaceFileWritePort writePort = new RecordingWorkspaceFileWritePort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                readPort,
                writePort,
                modelDecisionPort,
                conversationId -> "<memory_context>\n</memory_context>"
        );

        ConversationTurnAggregate turn = service.handleUserMessage("conv-edit", "编辑 notes/todo.txt");

        Assert.assertEquals("notes/todo.txt", writePort.path);
        Assert.assertEquals("new task", writePort.content);
        Assert.assertTrue(turn.getAnswer().contains("已编辑 notes/todo.txt"));
        Assert.assertEquals("file_edit", turn.getToolExecutions().get(0).getToolName());
    }

    @Test
    public void shouldRecordBashPermissionWithoutExecutingCommand() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("bash")
                .toolInput("ls docs")
                .build();
        RecordingWorkspaceFilePort readPort = new RecordingWorkspaceFilePort();
        RecordingWorkspaceFileWritePort writePort = new RecordingWorkspaceFileWritePort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                readPort,
                writePort,
                modelDecisionPort,
                conversationId -> "<memory_context>\n</memory_context>"
        );

        ConversationTurnAggregate turn = service.handleUserMessage("conv-bash", "列一下 docs");

        Assert.assertEquals("bash", turn.getToolExecutions().get(0).getToolName());
        Assert.assertEquals("ls docs", turn.getToolExecutions().get(0).getInput());
        Assert.assertTrue(turn.getAnswer().contains("bash execution is not enabled yet"));
        Assert.assertFalse(readPort.wasCalled());
        Assert.assertFalse(writePort.wasCalled());
    }

    @Test
    public void shouldDenyUnsafeBashCommandBeforeExecutionBoundary() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("bash")
                .toolInput("rm -rf target")
                .build();
        RecordingWorkspaceFilePort readPort = new RecordingWorkspaceFilePort();
        RecordingWorkspaceFileWritePort writePort = new RecordingWorkspaceFileWritePort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                readPort,
                writePort,
                modelDecisionPort,
                conversationId -> "<memory_context>\n</memory_context>"
        );

        ConversationTurnAggregate turn = service.handleUserMessage("conv-bash-deny", "删掉 target");

        Assert.assertTrue(turn.getAnswer().contains("permission denied"));
        Assert.assertEquals("bash", turn.getToolExecutions().get(0).getToolName());
        Assert.assertFalse(readPort.wasCalled());
        Assert.assertFalse(writePort.wasCalled());
    }

    @Test
    public void shouldUseConfiguredShellCommandPortForAllowedBashCommand() {
        IModelDecisionPort modelDecisionPort = request -> ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.TOOL_CALL)
                .toolName("bash")
                .toolInput("pwd")
                .build();
        RecordingShellCommandPort shellCommandPort = new RecordingShellCommandPort();
        ConversationRuntimeService service = new ConversationRuntimeService(
                new RecordingWorkspaceFilePort(),
                new RecordingWorkspaceFileWritePort(),
                shellCommandPort,
                modelDecisionPort,
                conversationId -> "<memory_context>\n</memory_context>"
        );

        ConversationTurnAggregate turn = service.handleUserMessage("conv-bash-exec", "运行 pwd");

        Assert.assertEquals("pwd", shellCommandPort.command);
        Assert.assertTrue(turn.getAnswer().contains("/workspace"));
        Assert.assertTrue(turn.getToolExecutions().get(0).getResult().contains("exit=0"));
    }

    private static class CapturingModelDecisionPort implements IModelDecisionPort {

        private com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO request;

        @Override
        public ModelDecisionVO decideNextAction(com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO request) {
            this.request = request;
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.DIRECT_RESPONSE)
                    .directAnswer("ok")
                    .build();
        }
    }

    private static class RecordingWorkspaceFilePort implements IWorkspaceFilePort {

        private boolean called;
        private final String content;

        private RecordingWorkspaceFilePort() {
            this("");
        }

        private RecordingWorkspaceFilePort(String content) {
            this.content = content;
        }

        @Override
        public WorkspaceFileVO readFile(String relativePath) {
            called = true;
            return WorkspaceFileVO.builder()
                    .path(relativePath)
                    .content(content)
                    .build();
        }

        private boolean wasCalled() {
            return called;
        }
    }

    private static class RecordingWorkspaceFileWritePort implements IWorkspaceFileWritePort {

        private String path;
        private String content;
        private boolean called;

        @Override
        public WorkspaceFileVO writeFile(String relativePath, String content) {
            called = true;
            this.path = relativePath;
            this.content = content;
            return WorkspaceFileVO.builder()
                    .path(relativePath)
                    .content(content)
                    .build();
        }

        private boolean wasCalled() {
            return called;
        }
    }

    private static class RecordingShellCommandPort implements IShellCommandPort {

        private String command;

        @Override
        public ShellCommandResultVO execute(String command) {
            this.command = command;
            return ShellCommandResultVO.builder()
                    .command(command)
                    .exitCode(0)
                    .stdout("/workspace")
                    .stderr("")
                    .build();
        }
    }

}
