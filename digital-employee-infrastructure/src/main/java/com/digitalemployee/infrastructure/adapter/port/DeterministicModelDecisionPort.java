package com.digitalemployee.infrastructure.adapter.port;

import com.digitalemployee.domain.conversation.adapter.port.IModelDecisionPort;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionRequestVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionTypeVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelDecisionVO;
import com.digitalemployee.domain.conversation.model.valobj.ModelProviderVO;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DeterministicModelDecisionPort implements IModelDecisionPort {

    private static final String FILE_READ_TOOL_NAME = "file_read";
    private static final String FILE_WRITE_TOOL_NAME = "file_write";
    private static final String FILE_EDIT_TOOL_NAME = "file_edit";
    private static final String BASH_TOOL_NAME = "bash";
    private static final Pattern FILE_TOKEN_PATTERN = Pattern.compile("([A-Za-z0-9_./-]+\\.[A-Za-z0-9_-]+)");

    @Override
    public ModelProviderVO provider() {
        return ModelProviderVO.builder()
                .provider("deterministic")
                .model("local-rules")
                .external(false)
                .apiKeyEnvName("")
                .build();
    }

    @Override
    public ModelDecisionVO decideNextAction(ModelDecisionRequestVO request) {
        String message = request.getUserMessage();
        if (isPriorUserMessagePrompt(message)) {
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.DIRECT_RESPONSE)
                    .directAnswer("上一条用户消息是：" + extractLastHistoricalUserMessage(request.getMemoryContext()))
                    .build();
        }
        String filePath = extractFilePath(message);
        if (isBashPrompt(message) && isToolAvailable(request, BASH_TOOL_NAME)) {
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.TOOL_CALL)
                    .toolName(BASH_TOOL_NAME)
                    .toolInput(extractBashCommand(message))
                    .build();
        }
        if (filePath != null && isFileEditPrompt(message) && isToolAvailable(request, FILE_EDIT_TOOL_NAME)) {
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.TOOL_CALL)
                    .toolName(FILE_EDIT_TOOL_NAME)
                    .toolInput(filePath + "\n" + extractOldText(message) + "\n" + extractNewText(message))
                    .build();
        }
        if (filePath != null && isFileWritePrompt(message) && isToolAvailable(request, FILE_WRITE_TOOL_NAME)) {
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.TOOL_CALL)
                    .toolName(FILE_WRITE_TOOL_NAME)
                    .toolInput(filePath + "\n" + extractWriteContent(message))
                    .build();
        }
        if (filePath != null && isFileInspectionPrompt(message) && isToolAvailable(request, FILE_READ_TOOL_NAME)) {
            return ModelDecisionVO.builder()
                    .type(ModelDecisionTypeVO.TOOL_CALL)
                    .toolName(FILE_READ_TOOL_NAME)
                    .toolInput(filePath)
                    .build();
        }
        return ModelDecisionVO.builder()
                .type(ModelDecisionTypeVO.DIRECT_RESPONSE)
                .directAnswer("我已经接收到你的消息。当前纵切支持读取工作区文件，例如：请读取 AGENTS.md。")
                .build();
    }

    private boolean isPriorUserMessagePrompt(String message) {
        if (message == null) {
            return false;
        }
        return message.contains("上一条用户消息")
                || message.contains("上一条消息")
                || message.toLowerCase(Locale.ROOT).contains("previous user message");
    }

    private String extractLastHistoricalUserMessage(String memoryContext) {
        if (memoryContext == null || memoryContext.isBlank()) {
            return "";
        }
        String lastUserMessage = "";
        String[] lines = memoryContext.split("\\R");
        for (String line : lines) {
            if (line.startsWith("user: ")) {
                lastUserMessage = line.substring("user: ".length()).trim();
            }
        }
        return lastUserMessage;
    }

    private boolean isBashPrompt(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("运行 ")
                || lower.contains("执行 ")
                || lower.contains("run ");
    }

    private String extractBashCommand(String message) {
        if (message == null) {
            return "";
        }
        String lower = message.toLowerCase(Locale.ROOT);
        int runIndex = lower.indexOf("run ");
        if (runIndex >= 0) {
            return message.substring(runIndex + "run ".length()).trim();
        }
        int executeIndex = message.indexOf("执行 ");
        if (executeIndex >= 0) {
            return message.substring(executeIndex + "执行 ".length()).trim();
        }
        int runChineseIndex = message.indexOf("运行 ");
        if (runChineseIndex >= 0) {
            return message.substring(runChineseIndex + "运行 ".length()).trim();
        }
        return "";
    }

    private boolean isFileEditPrompt(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("编辑")
                || lower.contains("替换为")
                || lower.contains("replace");
    }

    private String extractOldText(String message) {
        if (message == null) {
            return "";
        }
        int start = message.indexOf("把");
        int end = message.indexOf("替换为");
        if (start >= 0 && end > start) {
            return message.substring(start + "把".length(), end).trim();
        }
        return "";
    }

    private String extractNewText(String message) {
        if (message == null) {
            return "";
        }
        int start = message.indexOf("替换为");
        if (start >= 0) {
            return message.substring(start + "替换为".length()).trim();
        }
        return "";
    }

    private boolean isFileWritePrompt(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("写入")
                || lower.contains("write");
    }

    private String extractWriteContent(String message) {
        if (message == null) {
            return "";
        }
        int markerIndex = message.indexOf("内容");
        if (markerIndex >= 0) {
            return message.substring(markerIndex + "内容".length()).trim();
        }
        int writeIndex = message.toLowerCase(Locale.ROOT).indexOf("write");
        if (writeIndex >= 0) {
            return message.substring(writeIndex + "write".length()).replaceFirst(FILE_TOKEN_PATTERN.pattern(), "").trim();
        }
        return "";
    }

    private boolean isFileInspectionPrompt(String message) {
        if (message == null) {
            return false;
        }
        String lower = message.toLowerCase(Locale.ROOT);
        return lower.contains("读取")
                || lower.contains("read")
                || lower.contains("看一下")
                || lower.contains("查看")
                || lower.contains("检查")
                || lower.contains("inspect");
    }

    private String extractFilePath(String message) {
        if (message == null || message.trim().isEmpty()) {
            return null;
        }
        Matcher matcher = FILE_TOKEN_PATTERN.matcher(message);
        String match = null;
        while (matcher.find()) {
            match = matcher.group(1);
        }
        if (match == null) {
            return null;
        }
        return match.replace("。", "")
                .replace("，", "")
                .replace(",", "")
                .trim();
    }

    private boolean isToolAvailable(ModelDecisionRequestVO request, String toolName) {
        return request.getAvailableTools().stream()
                .anyMatch(tool -> toolName.equals(tool.getName()));
    }

}
