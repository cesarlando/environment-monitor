package br.com.cesarlando.environmentmonitor.controller;

import br.com.cesarlando.environmentmonitor.application.usecase.LoadCheckHistoryUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentHistorySummaryUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentStatusUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.controller.mapper.CheckHistoryResponseMapper;
import br.com.cesarlando.environmentmonitor.controller.mapper.EnvironmentResponseMapper;
import br.com.cesarlando.environmentmonitor.controller.mapper.EnvironmentStatusResponseMapper;
import br.com.cesarlando.environmentmonitor.dto.CheckHistoryResponse;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentHistorySummaryResponse;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentResponse;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/environments")
public class EnvironmentController {

    private final LoadEnvironmentUseCase loadEnvironmentUseCase;
    private final LoadEnvironmentStatusUseCase loadEnvironmentStatusUseCase;
    private final LoadCheckHistoryUseCase loadCheckHistoryUseCase;
    private final LoadEnvironmentHistorySummaryUseCase loadEnvironmentHistorySummaryUseCase;
    private final CheckHistoryResponseMapper checkHistoryResponseMapper;
    private final EnvironmentResponseMapper environmentResponseMapper;
    private final EnvironmentStatusResponseMapper environmentStatusResponseMapper;

    public EnvironmentController(LoadEnvironmentUseCase loadEnvironmentUseCase, LoadEnvironmentStatusUseCase loadEnvironmentStatusUseCase, LoadCheckHistoryUseCase loadCheckHistoryUseCase, LoadEnvironmentHistorySummaryUseCase loadEnvironmentHistorySummaryUseCase, CheckHistoryResponseMapper checkHistoryResponseMapper, EnvironmentResponseMapper environmentResponseMapper, EnvironmentStatusResponseMapper environmentStatusResponseMapper) {
        this.loadEnvironmentUseCase = loadEnvironmentUseCase;
        this.loadEnvironmentStatusUseCase = loadEnvironmentStatusUseCase;
        this.loadCheckHistoryUseCase = loadCheckHistoryUseCase;
        this.loadEnvironmentHistorySummaryUseCase = loadEnvironmentHistorySummaryUseCase;
        this.checkHistoryResponseMapper = checkHistoryResponseMapper;
        this.environmentResponseMapper = environmentResponseMapper;
        this.environmentStatusResponseMapper = environmentStatusResponseMapper;
    }

    @GetMapping
    public List<EnvironmentResponse> findAll() {

        return loadEnvironmentUseCase.execute()
                .stream()
                .map(environmentResponseMapper::toResponse)
                .toList();
    }

    @GetMapping("/status")
    public List<EnvironmentStatusResponse> findLatestStatus() {

        return loadEnvironmentStatusUseCase.execute()
                .stream()
                .map(environmentStatusResponseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}/history")
    public List<CheckHistoryResponse> findHistoryByEnvironment(
            @PathVariable Long id) {

        return loadCheckHistoryUseCase.execute(id)
                .stream()
                .map(checkHistoryResponseMapper::toResponse)
                .toList();
    }

    @GetMapping("/{id}/history/summary")
    public EnvironmentHistorySummaryResponse findHistorySummary(
            @PathVariable Long id) {

        return loadEnvironmentHistorySummaryUseCase.execute(id);
    }
}