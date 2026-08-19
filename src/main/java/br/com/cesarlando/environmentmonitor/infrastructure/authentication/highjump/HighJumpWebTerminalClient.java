package br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump;

import org.springframework.stereotype.Component;

import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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

    public WebTerminalResult executeInitialRequest(
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

            int responseCode = connection.getResponseCode();

            var responseStream =
                    responseCode >= 400
                            ? connection.getErrorStream()
                            : connection.getInputStream();

            String responseBody = "";

            if (responseStream != null) {
                responseBody =
                        new String(
                                responseStream.readAllBytes(),
                                StandardCharsets.UTF_8
                        );
            }

            System.out.println(
                    "WebTerminal initial response summary"
                            + " | http=" + responseCode
                            + " | bodyLength=" + responseBody.length()
                            + " | hasConnectNow="
                            + responseBody.contains("Connect Now!")
            );

            return new WebTerminalResult(
                    responseCode,
                    responseBody
            );

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

    public WebTerminalResult executeRedirectRequest(
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
            connection.setInstanceFollowRedirects(true);

            int responseCode = connection.getResponseCode();

            var responseStream =
                    responseCode >= 400
                            ? connection.getErrorStream()
                            : connection.getInputStream();

            String responseBody = "";

            if (responseStream != null) {
                responseBody =
                        new String(
                                responseStream.readAllBytes(),
                                StandardCharsets.UTF_8
                        );
            }

            return new WebTerminalResult(
                    responseCode,
                    responseBody
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao executar redirect do WebTerminal",
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    public String extractRedirectUrl(
            String webServerName,
            String responseBody) {

        String marker = "href=";

        int hrefStart = responseBody.indexOf(marker);

        int hrefEnd = responseBody.indexOf(">Connect Now!");

        if (hrefStart == -1 || hrefEnd == -1) {
            throw new IllegalStateException(
                    "Redirect do WebTerminal não encontrado"
            );
        }

        String redirectPath =
                responseBody.substring(
                        hrefStart + marker.length(),
                        hrefEnd
                );

        return webServerName + redirectPath;
    }

    public record WebTerminalResult(
            int responseCode,
            String responseBody
    ) {
    }

}
