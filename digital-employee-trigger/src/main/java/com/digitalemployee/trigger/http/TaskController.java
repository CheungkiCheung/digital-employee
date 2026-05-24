package com.digitalemployee.trigger.http;

import com.digitalemployee.api.dto.TaskCreateRequestDTO;
import com.digitalemployee.api.dto.TaskResponseDTO;
import com.digitalemployee.api.response.Response;
import com.digitalemployee.cases.ITaskCaseService;
import com.digitalemployee.domain.task.model.entity.DigitalEmployeeTaskEntity;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;
import com.digitalemployee.types.enums.ResponseCode;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/tasks")
public class TaskController {

    private final ITaskCaseService taskCaseService;

    public TaskController(ITaskCaseService taskCaseService) {
        this.taskCaseService = taskCaseService;
    }

    @PostMapping
    public Response<TaskResponseDTO> createTask(@RequestBody TaskCreateRequestDTO request) {
        DigitalEmployeeTaskEntity task = taskCaseService.createTask(
                TaskTypeVO.valueOf(request.getType()),
                request.getDescription()
        );
        return Response.<TaskResponseDTO>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(toDTO(task))
                .build();
    }

    @GetMapping("/{taskId}")
    public Response<TaskResponseDTO> getTask(@PathVariable String taskId) {
        return success(toDTO(taskCaseService.findTask(taskId)));
    }

    @GetMapping
    public Response<List<TaskResponseDTO>> listTasks() {
        return success(taskCaseService.listTasks().stream()
                .map(this::toDTO)
                .toList());
    }

    @PostMapping("/{taskId}/start")
    public Response<TaskResponseDTO> startTask(@PathVariable String taskId) {
        return success(toDTO(taskCaseService.startTask(taskId)));
    }

    @PostMapping("/{taskId}/complete")
    public Response<TaskResponseDTO> completeTask(@PathVariable String taskId) {
        return success(toDTO(taskCaseService.completeTask(taskId)));
    }

    private <T> Response<T> success(T data) {
        return Response.<T>builder()
                .code(ResponseCode.SUCCESS.getCode())
                .info(ResponseCode.SUCCESS.getInfo())
                .data(data)
                .build();
    }

    private TaskResponseDTO toDTO(DigitalEmployeeTaskEntity task) {
        return TaskResponseDTO.builder()
                .id(task.getId())
                .type(task.getType().name())
                .status(task.getStatus().name())
                .description(task.getDescription())
                .startTime(task.getStartTime())
                .endTime(task.getEndTime())
                .outputFile(task.getOutputFile())
                .build();
    }

}
