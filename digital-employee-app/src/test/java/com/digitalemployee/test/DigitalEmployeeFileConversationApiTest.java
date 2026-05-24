package com.digitalemployee.test;

import com.alibaba.fastjson.JSON;
import com.digitalemployee.api.dto.ConversationMessageRequestDTO;
import com.digitalemployee.api.response.Response;
import com.digitalemployee.domain.conversation.model.entity.ConversationMessageEntity;
import com.digitalemployee.domain.conversation.model.valobj.ConversationMessageRoleVO;
import com.digitalemployee.infrastructure.adapter.repository.FileConversationTurnRepository;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@RunWith(SpringRunner.class)
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "digital-employee.conversation.repository=file",
        "digital-employee.conversation.file-storage-path=target/test-conversation-api-history"
})
public class DigitalEmployeeFileConversationApiTest {

    private static final Path STORAGE_DIRECTORY = Path.of("target/test-conversation-api-history");

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void shouldPersistConversationApiHistoryToConfiguredFileRepository() throws Exception {
        deleteDirectory(STORAGE_DIRECTORY);

        ConversationMessageRequestDTO request = new ConversationMessageRequestDTO();
        request.setMessage("hello file history");

        String body = mockMvc.perform(post("/api/v1/conversations/file-api-history/messages")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(JSON.toJSONString(request).getBytes(StandardCharsets.UTF_8)))
                .andExpect(status().isOk())
                .andReturn()
                .getResponse()
                .getContentAsString(StandardCharsets.UTF_8);

        Response<?> response = JSON.parseObject(body, Response.class);
        Assert.assertEquals("0000", response.getCode());
        Map<?, ?> data = (Map<?, ?>) response.getData();
        Assert.assertEquals("file-api-history", data.get("conversationId"));

        FileConversationTurnRepository reloadedRepository = new FileConversationTurnRepository(STORAGE_DIRECTORY);
        List<ConversationMessageEntity> messages = reloadedRepository.listMessages("file-api-history");

        Assert.assertEquals(2, messages.size());
        Assert.assertEquals(ConversationMessageRoleVO.USER, messages.get(0).getRole());
        Assert.assertEquals("hello file history", messages.get(0).getContent());
        Assert.assertEquals(ConversationMessageRoleVO.ASSISTANT, messages.get(1).getRole());
        Assert.assertTrue(messages.get(1).getContent().contains("当前纵切支持读取工作区文件"));
    }

    private void deleteDirectory(Path directory) throws Exception {
        if (!Files.exists(directory)) {
            return;
        }
        try (var paths = Files.walk(directory)) {
            List<Path> existingPaths = paths.sorted(Comparator.reverseOrder()).toList();
            for (Path path : existingPaths) {
                Files.deleteIfExists(path);
            }
        }
    }

}
