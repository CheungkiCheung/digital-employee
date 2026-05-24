package com.digitalemployee.test;

import com.alibaba.fastjson.JSON;
import com.digitalemployee.api.dto.ConversationMessageRequestDTO;
import com.digitalemployee.api.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DigitalEmployeeConversationApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldReadAllowedWorkspaceFileThroughConversationApi() throws Exception {
        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("请读取 AGENTS.md");

        String body = mockMvc.perform(post("/api/v1/conversations/conv-test/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertEquals("conv-test", data.get("conversationId"));
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("AGENTS.md"));
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("Digital Employee Agent Harness"));
        Assert.assertTrue(String.valueOf(data.get("messages")).contains("tool_result"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("file_read"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("allow"));
    }

    @Test
    public void shouldUseModelDecisionPortForNaturalFileInspectionPrompt() throws Exception {
        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("帮我看一下 AGENTS.md");

        String body = mockMvc.perform(post("/api/v1/conversations/conv-natural/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertEquals("conv-natural", data.get("conversationId"));
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("AGENTS.md"));
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("Digital Employee Agent Harness"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("file_read"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("allow"));
    }

    @Test
    public void shouldWriteWorkspaceFileThroughConversationApi() throws Exception {
        Path target = Path.of("..", "target", "test-workspace", "generated", "agent-note.txt").normalize();
        Files.deleteIfExists(target);

        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("写入 target/test-workspace/generated/agent-note.txt 内容 hello digital employee");

        String body = mockMvc.perform(post("/api/v1/conversations/write-check/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("已写入 target/test-workspace/generated/agent-note.txt"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("file_write"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("allow"));
        Assert.assertEquals("hello digital employee", Files.readString(target, StandardCharsets.UTF_8));
    }

    @Test
    public void shouldEditWorkspaceFileThroughConversationApi() throws Exception {
        Path target = Path.of("..", "target", "test-workspace", "generated", "edit-note.txt").normalize();
        Files.createDirectories(target.getParent());
        Files.writeString(target, "hello old value", StandardCharsets.UTF_8);

        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("编辑 target/test-workspace/generated/edit-note.txt 把 old 替换为 new");

        String body = mockMvc.perform(post("/api/v1/conversations/edit-check/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("已编辑 target/test-workspace/generated/edit-note.txt"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("file_edit"));
        Assert.assertEquals("hello new value", Files.readString(target, StandardCharsets.UTF_8));
    }

    @Test
    public void shouldEvaluateBashPermissionThroughConversationApiWithoutExecution() throws Exception {
        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("运行 ls docs");

        String body = mockMvc.perform(post("/api/v1/conversations/bash-check/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("exit=0"));
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("migration"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("bash"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("allow"));
    }

    @Test
    public void shouldDenyDestructiveBashCommandThroughConversationApi() throws Exception {
        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("运行 rm -rf target");

        String body = mockMvc.perform(post("/api/v1/conversations/bash-deny-rm/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("permission denied"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("bash"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("deny"));
        Assert.assertFalse(String.valueOf(data.get("answer")).contains("exit=0"));
    }

    @Test
    public void shouldDenyTraversalBashCommandThroughConversationApi() throws Exception {
        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("运行 cat ../secret.txt");

        String body = mockMvc.perform(post("/api/v1/conversations/bash-deny-traversal/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("permission denied"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("bash"));
        Assert.assertTrue(String.valueOf(data.get("toolExecutions")).contains("deny"));
        Assert.assertFalse(String.valueOf(data.get("answer")).contains("exit=0"));
    }

    @Test
    public void shouldListConversationMessageHistory() throws Exception {
        ConversationMessageRequestDTO firstRequest = new ConversationMessageRequestDTO();
        firstRequest.setMessage("hello history");
        ConversationMessageRequestDTO secondRequest = new ConversationMessageRequestDTO();
        secondRequest.setMessage("帮我看一下 AGENTS.md");

        mockMvc.perform(post("/api/v1/conversations/history-check/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(firstRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/conversations/history-check/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(secondRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk());

        String body = mockMvc.perform(get("/api/v1/conversations/history-check/messages"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        String data = String.valueOf(response.getData());
        Assert.assertTrue(data.contains("hello history"));
        Assert.assertTrue(data.contains("帮我看一下 AGENTS.md"));
        Assert.assertTrue(data.contains("file_read"));
    }

    @Test
    public void shouldIsolateConversationMessageHistoryByConversationId() throws Exception {
        ConversationMessageRequestDTO alphaRequest = new ConversationMessageRequestDTO();
        alphaRequest.setMessage("hello history alpha");
        ConversationMessageRequestDTO betaRequest = new ConversationMessageRequestDTO();
        betaRequest.setMessage("hello history beta");

        mockMvc.perform(post("/api/v1/conversations/history-alpha/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(alphaRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk());
        mockMvc.perform(post("/api/v1/conversations/history-beta/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(betaRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk());

        String alphaBody = mockMvc.perform(get("/api/v1/conversations/history-alpha/messages"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        String betaBody = mockMvc.perform(get("/api/v1/conversations/history-beta/messages"))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> alphaResponse = JSON.parseObject(alphaBody, Response.class);
        Response<?> betaResponse = JSON.parseObject(betaBody, Response.class);
        Assert.assertEquals("0000", alphaResponse.getCode());
        Assert.assertEquals("0000", betaResponse.getCode());

        String alphaData = String.valueOf(alphaResponse.getData());
        String betaData = String.valueOf(betaResponse.getData());
        Assert.assertTrue(alphaData.contains("hello history alpha"));
        Assert.assertFalse(alphaData.contains("hello history beta"));
        Assert.assertTrue(betaData.contains("hello history beta"));
        Assert.assertFalse(betaData.contains("hello history alpha"));
    }

    @Test
    public void shouldUsePriorConversationHistoryInModelDecisionContext() throws Exception {
        ConversationMessageRequestDTO firstRequest = new ConversationMessageRequestDTO();
        firstRequest.setMessage("我的代号是 alpha-memory");
        ConversationMessageRequestDTO secondRequest = new ConversationMessageRequestDTO();
        secondRequest.setMessage("上一条用户消息是什么");

        mockMvc.perform(post("/api/v1/conversations/history-context/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(firstRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk());

        String body = mockMvc.perform(post("/api/v1/conversations/history-context/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(secondRequest).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("answer")).contains("我的代号是 alpha-memory"));
    }
}
