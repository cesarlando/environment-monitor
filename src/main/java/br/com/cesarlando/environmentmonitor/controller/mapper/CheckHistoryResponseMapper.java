package br.com.cesarlando.environmentmonitor.controller.mapper;

import br.com.cesarlando.environmentmonitor.domain.model.CheckHistory;
import br.com.cesarlando.environmentmonitor.dto.CheckHistoryResponse;
import org.springframework.stereotype.Component;

@Component
public class CheckHistoryResponseMapper {

    public CheckHistoryResponse toResponse(CheckHistory history) {

        CheckHistoryResponse response = new CheckHistoryResponse();

        response.setId(history.getId());
        response.setEnvironmentId(history.getEnvironmentId());
        response.setEnvironmentName(history.getEnvironmentName());
        response.setEnvironmentType(history.getEnvironmentType());
        response.setStatus(history.getStatus());
        response.setResponseTime(history.getResponseTime());
        response.setDetails(history.getDetails());
        response.setCheckedAt(history.getCheckedAt());

        return response;
    }
}
