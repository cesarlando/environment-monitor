package br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump;

import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;

@Component
public class HighJumpSessionClient {

    public String buildRegisterChildSessionUrl(String baseUrl) {

        URI uri = URI.create(baseUrl);

        String base =
                uri.getScheme()
                        + "://"
                        + uri.getHost()
                        + (uri.getPort() != -1
                        ? ":" + uri.getPort()
                        : "");

        String path = uri.getPath();

        if (path == null || path.isBlank() || "/".equals(path)) {
            return base
                    + "/odata/RegisterChildSession"
                    + "?applicationId=%27SCA%20inMotion%27"
                    + "&connectionType=%27platform%27";
        }

        return base
                + path
                + "/RegisterChildSession"
                + "?applicationId=%27SCA%20inMotion%27"
                + "&connectionType=%27platform%27";
    }

    public HighJumpSessionResult registerChildSession(
            String registerUrl,
            String authenticationTicket) {

        HttpURLConnection connection = null;

        try {

            connection =
                    (HttpURLConnection) new URL(registerUrl)
                            .openConnection();

            connection.setRequestMethod("POST");
            connection.setRequestProperty(
                    "Content-Type",
                    "application/json"
            );
            connection.setRequestProperty(
                    "Accept",
                    "application/json"
            );
            connection.setRequestProperty(
                    "AuthenticationTicket",
                    authenticationTicket
            );

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            try (OutputStreamWriter writer =
                         new OutputStreamWriter(
                                 connection.getOutputStream()
                         )) {

                writer.flush();
            }

            int responseCode =
                    connection.getResponseCode();

            InputStream responseStream =
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

            return new HighJumpSessionResult(
                    responseCode,
                    responseBody
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao registrar sessão SCA inMotion",
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public record HighJumpSessionResult(
            int responseCode,
            String responseBody
    ) {
    }
}

