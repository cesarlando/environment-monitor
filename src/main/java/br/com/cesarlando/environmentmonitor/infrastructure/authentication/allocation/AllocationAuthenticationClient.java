package br.com.cesarlando.environmentmonitor.infrastructure.authentication.allocation;

import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Component
public class AllocationAuthenticationClient {

    private static final Pattern VIEW_STATE_PATTERN =
            Pattern.compile("name=\"javax\\.faces\\.ViewState\"\\s+value=\"([^\"]+)\"");

    private static final Pattern ADF_CTRL_STATE_PATTERN =
            Pattern.compile("action=\"[^\"]*_adf\\.ctrl-state=([^\"&]+)");

    private static final Pattern FORM_ACTION_PATTERN =
            Pattern.compile(
                    "<form[^>]+action=\"([^\"]+)\""
            );

    public AllocationLoginPageData loadLoginPage(String endpoint) {

        HttpURLConnection connection = null;

        try {

            URL url = new URL(endpoint);

            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);

            int responseCode = connection.getResponseCode();

            if (responseCode != 200) {
                throw new IllegalStateException(
                        "Falha ao carregar página de login Allocation - HTTP " + responseCode
                );
            }

            StringBuilder html = new StringBuilder();

            try (BufferedReader reader =
                         new BufferedReader(
                                 new InputStreamReader(
                                         connection.getInputStream()
                                 )
                         )) {
                String line;

                while ((line = reader.readLine()) != null) {
                    html.append(line);
                }
            }
            Matcher viewStateMatcher = VIEW_STATE_PATTERN.matcher(html);

            if (!viewStateMatcher.find()) {
                throw new IllegalStateException(
                        "javax.faces.ViewState não encontrado"
                );
            }
            Matcher adfMatcher = ADF_CTRL_STATE_PATTERN.matcher(html);

            if (!adfMatcher.find()) {
                throw new IllegalStateException(
                        "_adf.ctrl-state não encontrado"
                );
            }

            Matcher formActionMatcher =
                    FORM_ACTION_PATTERN.matcher(html);

            if (!formActionMatcher.find()) {
                throw new IllegalStateException(
                        "Action do formulário Allocation não encontrado"
                );
            }

            String viewState = viewStateMatcher.group(1);
            String adfCtrlState = adfMatcher.group(1);
            String sessionCookie = connection.getHeaderField("Set-Cookie");
            String formAction = formActionMatcher.group(1);

            return new AllocationLoginPageData(viewState, adfCtrlState, sessionCookie, formAction);

        } catch (Exception exception) {
            throw new IllegalStateException(
                    "Erro ao carregar página de login Allocation", exception
            );
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
        }
    }

    public record AllocationLoginPageData(
            String viewState,
            String adfCtrlState,
            String sessionCookie,
            String formAction
    ) {

    }

    public record AllocationAuthenticationResult(
            int responseCode,
            String responseBody
    ) {

    }

    public AllocationAuthenticationResult authenticate(
            String endpoint,
            String username,
            String password,
            AllocationLoginPageData loginPageData) {

        HttpURLConnection connection = null;

        try {

            URI endpointUri = URI.create(endpoint);

            String baseUrl =
                    endpointUri.getScheme()
                            + "://"
                            + endpointUri.getHost()
                            + (endpointUri.getPort() != -1
                            ? ":" + endpointUri.getPort()
                            : "");

            String loginUrl =
                    baseUrl + loginPageData.formAction();


            String eventValue =
                    "<m xmlns=\"http://oracle.com/richClient/comm\">"
                            + "<k v=\"type\"><s>action</s></k>"
                            + "</m>";

            String formData =
                    "pt1:r1:0:login_it1="
                            + encode(username)
                            + "&pt1:r1:0:login_it2="
                            + encode(password)
                            + "&org.apache.myfaces.trinidad.faces.FORM="
                            + encode("f1")
                            + "&javax.faces.ViewState="
                            + encode(loginPageData.viewState())
                            + "&oracle.adf.view.rich.RENDER="
                            + encode("pt1:r1")
                            + "&event="
                            + encode("pt1:r1:0:login_cb1")
                            + "&event.pt1:r1:0:login_cb1="
                            + encode(eventValue)
                            + "&oracle.adf.view.rich.PROCESS="
                            + encode("pt1:r1");

            URL url = new URL(loginUrl);

            connection =
                    (HttpURLConnection) url.openConnection();

            connection.setRequestMethod("POST");

            connection.setRequestProperty(
                    "Content-Type",
                    "application/x-www-form-urlencoded; charset=UTF-8"
            );

            connection.setRequestProperty(
                    "Accept",
                    "*/*"
            );

            connection.setRequestProperty(
                    "Adf-Ads-Page-Id",
                    "1"
            );

            connection.setRequestProperty(
                    "Adf-Rich-Message",
                    "true"
            );

            connection.setRequestProperty(
                    "Referer",
                    loginUrl
            );

            if (loginPageData.sessionCookie() != null) {

                String cookie =
                        loginPageData.sessionCookie()
                                .split(";", 2)[0];

                connection.setRequestProperty(
                        "Cookie",
                        cookie
                );
            }

            connection.setConnectTimeout(5000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);

            try (OutputStreamWriter writer =
                         new OutputStreamWriter(
                                 connection.getOutputStream()
                         )) {

                writer.write(formData);
                writer.flush();
            }

            int responseCode = connection.getResponseCode();

            var responseStream =
                    responseCode >= 400
                            ? connection.getErrorStream()
                            : connection.getInputStream();

            String responseBody = "";

            if (responseStream != null) {

                try (BufferedReader reader =
                             new BufferedReader(
                                     new InputStreamReader(responseStream)
                             )) {

                    StringBuilder response = new StringBuilder();
                    String line;

                    while ((line = reader.readLine()) != null) {
                        response.append(line);
                    }

                    responseBody = response.toString();
                }
            }

            return new AllocationAuthenticationResult(
                    responseCode,
                    responseBody
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Erro ao executar autenticação Allocation",
                    exception
            );

        } finally {

            if (connection != null) {
                connection.disconnect();
            }
        }
    }
    private String encode(String value) {
        return URLEncoder.encode(
                value,
                StandardCharsets.UTF_8
        );
    }
}
