package com.digitalemployee.domain.conversation.model.valobj;

import org.junit.Assert;
import org.junit.Test;

public class ShellCommandResultVOTest {

    @Test
    public void shouldLimitStdoutPreviewInSummary() {
        String longOutput = "a".repeat(1500);
        ShellCommandResultVO result = ShellCommandResultVO.builder()
                .command("cat large.txt")
                .exitCode(0)
                .stdout(longOutput)
                .stderr("")
                .build();

        String summary = result.summarize();

        Assert.assertTrue(summary.length() < 1300);
        Assert.assertTrue(summary.contains("truncated"));
    }

    @Test
    public void shouldLimitStderrPreviewInSummary() {
        String longError = "e".repeat(1500);
        ShellCommandResultVO result = ShellCommandResultVO.builder()
                .command("cat missing.txt")
                .exitCode(1)
                .stdout("")
                .stderr(longError)
                .build();

        String summary = result.summarize();

        Assert.assertTrue(summary.length() < 1300);
        Assert.assertTrue(summary.contains("truncated"));
    }

}
