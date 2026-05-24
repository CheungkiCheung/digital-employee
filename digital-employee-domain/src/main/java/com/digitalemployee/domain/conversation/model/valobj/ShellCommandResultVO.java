package com.digitalemployee.domain.conversation.model.valobj;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class ShellCommandResultVO {

    private static final int OUTPUT_PREVIEW_LIMIT = 1000;

    private final String command;
    private final int exitCode;
    private final String stdout;
    private final String stderr;

    public String summarize() {
        StringBuilder summary = new StringBuilder("exit=").append(exitCode);
        if (stdout != null && !stdout.isBlank()) {
            summary.append("\nstdout:\n").append(preview(stdout));
        }
        if (stderr != null && !stderr.isBlank()) {
            summary.append("\nstderr:\n").append(preview(stderr));
        }
        return summary.toString();
    }

    private String preview(String output) {
        if (output.length() <= OUTPUT_PREVIEW_LIMIT) {
            return output;
        }
        return output.substring(0, OUTPUT_PREVIEW_LIMIT)
                + "\n[truncated "
                + (output.length() - OUTPUT_PREVIEW_LIMIT)
                + " chars]";
    }

}
