package com.digitalemployee.domain.task.model.entity;

import com.digitalemployee.domain.task.model.valobj.TaskStatusVO;
import com.digitalemployee.domain.task.model.valobj.TaskTypeVO;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class DigitalEmployeeTaskEntity {

    private final String id;
    private final TaskTypeVO type;
    private final TaskStatusVO status;
    private final String description;
    private final long startTime;
    private final Long endTime;
    private final String outputFile;
    private final long outputOffset;
    private final boolean notified;

    public DigitalEmployeeTaskEntity withStatus(TaskStatusVO nextStatus, Long nextEndTime) {
        return DigitalEmployeeTaskEntity.builder()
                .id(id)
                .type(type)
                .status(nextStatus)
                .description(description)
                .startTime(startTime)
                .endTime(nextEndTime)
                .outputFile(outputFile)
                .outputOffset(outputOffset)
                .notified(notified)
                .build();
    }

}
