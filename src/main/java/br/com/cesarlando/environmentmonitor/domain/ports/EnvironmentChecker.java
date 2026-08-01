package br.com.cesarlando.environmentmonitor.domain.ports;

import br.com.cesarlando.environmentmonitor.domain.model.CheckResult;
import br.com.cesarlando.environmentmonitor.domain.model.Environment;

public interface EnvironmentChecker {
    CheckResult check (Environment environment);
}
