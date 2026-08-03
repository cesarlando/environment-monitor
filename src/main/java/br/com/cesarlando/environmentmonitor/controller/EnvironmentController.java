package br.com.cesarlando.environmentmonitor.controller;


import br.com.cesarlando.environmentmonitor.application.usecase.LoadEnvironmentsUseCase;
import br.com.cesarlando.environmentmonitor.controller.mapper.EnvironmentResponseMapper;
import br.com.cesarlando.environmentmonitor.dto.EnvironmentResponse;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/environments")
public class EnvironmentController {

    private final LoadEnvironmentsUseCase loadEnvironmentsUseCase;
    private final EnvironmentResponseMapper environmentResponseMapper;

    public EnvironmentController(LoadEnvironmentsUseCase loadEnvironmentsUseCase, EnvironmentResponseMapper environmentResponseMapper) {
        this.loadEnvironmentsUseCase = loadEnvironmentsUseCase;
        this.environmentResponseMapper = environmentResponseMapper;
    }

    @GetMapping
    public List<EnvironmentResponse> findAll() {

        return loadEnvironmentsUseCase.execute()
                .stream()
                .map(environmentResponseMapper::toResponse)
                .toList();
    }
}
