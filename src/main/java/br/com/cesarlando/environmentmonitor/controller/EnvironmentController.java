package br.com.cesarlando.environmentmonitor.controller;

import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentStatusUseCase;
import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentUseCase;
import br.com.cesarlando.environmentmonitor.controller.mapper.EnvironmentResponseMapper;
import br.com.cesarlando.environmentmonitor.controller.mapper.EnvironmentStatusResponseMapper;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentResponse;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentStatusResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/environments")
public class EnvironmentController {

    private final LoadEnvironmentUseCase loadEnvironmentUseCase;
    private final LoadEnvironmentStatusUseCase loadEnvironmentStatusUseCase;
    private final EnvironmentResponseMapper environmentResponseMapper;
    private final EnvironmentStatusResponseMapper environmentStatusResponseMapper;

    public EnvironmentController(
            LoadEnvironmentUseCase loadEnvironmentUseCase,
            LoadEnvironmentStatusUseCase loadEnvironmentStatusUseCase,
            EnvironmentResponseMapper environmentResponseMapper,
            EnvironmentStatusResponseMapper environmentStatusResponseMapper) {

        this.loadEnvironmentUseCase = loadEnvironmentUseCase;
        this.loadEnvironmentStatusUseCase = loadEnvironmentStatusUseCase;
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
}