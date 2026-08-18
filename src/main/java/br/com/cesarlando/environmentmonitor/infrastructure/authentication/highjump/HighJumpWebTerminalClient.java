package br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;

@Component
public class HighJumpWebTerminalClient {

    public String buildInitialUrl(
            String webServerName,
            String appServerName,
            String portNumber,
            String terminalName,
            String screenSize) {

        return webServerName
                + "//webtrmgw//webtrmgw.dll"
                + "?MfcISAPICommand=ProcessConnectionForm"
                + "&NC_value=0"
                + "&server_name=" + appServerName
                + "&port_number=" + portNumber
                + "&terminal_name=" + terminalName
                + "&screen_size=" + screenSize
                + "&save_info=Save";
    }

    public int executeInitialRequest(
            String url,
            String authenticationTicket) {

        HttpURLConnection connection = null;

        try {

            connection =
                    (HttpURLConnection) new URL(url).openConnection();

            connection.setRequestMethod("GET");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );
            connection.setRequestProperty(
                    "HJ1UserDataDetail-Agent",
                    "Mozilla/5.0"
            );
            connection.setRequestProperty(
                    "Connection",
                    "Keep-Alive"
            );

            if (authenticationTicket != null
                    && !authenticationTicket.isBlank()) {

                connection.setRequestProperty(
                        "AuthenticationTicket",
                        authenticationTicket
                );
            }

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setInstanceFollowRedirects(false);

            return connection.getResponseCode();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao executar requisição inicial do WebTerminal",
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
}
