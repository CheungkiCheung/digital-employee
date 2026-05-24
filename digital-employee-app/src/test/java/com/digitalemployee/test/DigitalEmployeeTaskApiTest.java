package com.digitalemployee.test;

import com.alibaba.fastjson.JSON;
import com.digitalemployee.api.dto.TaskCreateRequestDTO;
import com.digitalemployee.api.response.Response;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class DigitalEmployeeTaskApiTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldCreateDigitalEmployeeTaskThroughApi() throws Exception {
        TaskCreateRequestDTO request = new TaskCreateRequestDTO();
        request.setType("LOCAL_AGENT");
        request.setDescription("inspect repository");

        String body = mockMvc.perform(post("/api/v1/tasks")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());

        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertTrue(String.valueOf(data.get("id")).startsWith("a"));
        Assert.assertEquals("LOCAL_AGENT", data.get("type"));
        Assert.assertEquals("PENDING", data.get("status"));
        Assert.assertEquals("inspect repository", data.get("description"));
    }

    @Test
    public void shouldQueryListAndTransitionTaskThroughApi() throws Exception {
        String taskId = createTask("LOCAL_AGENT", "run repository check");

        Map<?, ?> foundTask = getDataFromResponse(get("/api/v1/tasks/" + taskId));
        Assert.assertEquals(taskId, foundTask.get("id"));
        Assert.assertEquals("PENDING", foundTask.get("status"));

        Map<?, ?> startedTask = getDataFromResponse(post("/api/v1/tasks/" + taskId + "/start"));
        Assert.assertEquals(taskId, startedTask.get("id"));
        Assert.assertEquals("RUNNING", startedTask.get("status"));

        List<?> tasks = getListDataFromResponse(get("/api/v1/tasks"));
        Assert.assertTrue(tasks.stream()
                .map(item -> (Map<?, ?>) item)
                .anyMatch(item -> taskId.equals(item.get("id")) && "RUNNING".equals(item.get("status"))));

        Map<?, ?> completedTask = getDataFromResponse(post("/api/v1/tasks/" + taskId + "/complete"));
        Assert.assertEquals(taskId, completedTask.get("id"));
        Assert.assertEquals("COMPLETED", completedTask.get("status"));
        Assert.assertNotNull(completedTask.get("endTime"));
    }

    private String createTask(String type, String description) throws Exception {
        TaskCreateRequestDTO request = new TaskCreateRequestDTO();
        request.setType(type);
        request.setDescription(description);

        Map<?, ?> data = getDataFromResponse(post("/api/v1/tasks")
                .contentType(MediaType.APPLICATION_JSON)
                .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)));
        return String.valueOf(data.get("id"));
    }

    private Map<?, ?> getDataFromResponse(org.springframework.test.web.servlet.RequestBuilder request) throws Exception {
        Response<?> response = performSuccess(request);
        return (Map<?, ?>) response.getData();
    }

    private List<?> getListDataFromResponse(org.springframework.test.web.servlet.RequestBuilder request) throws Exception {
        Response<?> response = performSuccess(request);
        return (List<?>) response.getData();
    }

    private Response<?> performSuccess(org.springframework.test.web.servlet.RequestBuilder request) throws Exception {
        String body = mockMvc.perform(request)
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);
        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());
        return response;
    }

}
