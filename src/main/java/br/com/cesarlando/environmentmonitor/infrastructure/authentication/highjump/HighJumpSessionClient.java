package br.com.cesarlando.environmentmonitor.infrastructure.authentication.highjump;

import org.springframework.stereotype.Component;
import tools.jackson.databind.ObjectMapper;

import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
public class HighJumpSessionClient {

    private final ObjectMapper objectMapper;
    public HighJumpSessionClient(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
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
    public String buildIdentityClaimDescriptionUrl(
            String baseUrl,
            String identityClaimId) {

        URI uri = URI.create(baseUrl);

        String base =
                uri.getScheme()
                        + "://"
                        + uri.getHost()
                        + (uri.getPort() != -1
                        ? ":" + uri.getPort()
                        : "");

        String path = uri.getPath();

        String startPath;

        if (path == null || path.isBlank() || "/".equals(path)) {
            startPath = base + "/odata";
        } else {
            startPath = base + path;
        }

        return startPath
                + "/IdentityClaimModels(guid'"
                + identityClaimId
                + "')";
    }

    public List<IdentityClaimValue> extractIdentityClaims(
            String responseBody) {

        try {
            var root = objectMapper.readTree(responseBody);

            var userNode = root.get("User");

            if (userNode == null || userNode.isNull()) {
                throw new IllegalStateException(
                        "User não encontrado no retorno do RegisterChildSession"
                );
            }

            var claimsNode = userNode.get("IdentityClaimsValues");

            if (claimsNode == null || !claimsNode.isArray()) {
                throw new IllegalStateException(
                        "IdentityClaimsValues não encontrado no retorno do RegisterChildSession"
                );
            }

            List<IdentityClaimValue> claims =
                    new ArrayList<>();

            for (var claimNode : claimsNode) {

                var identityClaimIdNode =
                        claimNode.get("IdentityClaimId");

                var valueNode =
                        claimNode.get("Value");

                if (identityClaimIdNode == null
                        || identityClaimIdNode.isNull()) {
                    continue;
                }

                String identityClaimId =
                        identityClaimIdNode.asText();

                String value =
                        valueNode != null && !valueNode.isNull()
                                ? valueNode.asText()
                                : "";

                claims.add(
                        new IdentityClaimValue(
                                identityClaimId,
                                value
                        )
                );
            }

            return claims;

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Erro ao extrair IdentityClaimsValues da sessão HighJump",
                    exception
            );
        }
    }

    public Map<String, String> resolveIdentityClaimParameters(
            String baseUrl,
            String authenticationTicket,
            List<IdentityClaimValue> claims) {

        Map<String, String> parameters = new HashMap<>();

        for (IdentityClaimValue claim : claims) {

            String descriptionUrl =
                    buildIdentityClaimDescriptionUrl(
                            baseUrl,
                            claim.identityClaimId()
                    );

            String description =
                    fetchIdentityClaimDescription(
                            descriptionUrl,
                            authenticationTicket
                    );

            parameters.put(
                    description,
                    claim.value()
            );
        }

        return parameters;
    }

    private String fetchIdentityClaimDescription(
            String url,
            String authenticationTicket) {

        HttpURLConnection connection = null;

        try {

            connection =
                    (HttpURLConnection) new URL(url)
                            .openConnection();

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
                    "AuthenticationTicket",
                    authenticationTicket
            );

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode =
                    connection.getResponseCode();

            if (responseCode != 200) {
                throw new IllegalStateException(
                        "Falha ao consultar IdentityClaim - HTTP "
                                + responseCode
                );
            }

            String responseBody =
                    new String(
                            connection.getInputStream().readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            var root =
                    objectMapper.readTree(responseBody);

            var descriptionNode =
                    root.get("Description");

            if (descriptionNode == null
                    || descriptionNode.isNull()
                    || descriptionNode.asText().isBlank()) {

                throw new IllegalStateException(
                        "Description não encontrada no IdentityClaim"
                );
            }

            return descriptionNode.asText();

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao consultar descrição do IdentityClaim",
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

    public record IdentityClaimValue(
            String identityClaimId,
            String value
    ) {
    }
}

