import {
    Box,
    Card,
    CardContent,
    Chip,
    Divider,
    Typography
} from "@mui/material";

import PublicIcon from "@mui/icons-material/Public";
import StorageIcon from "@mui/icons-material/Storage";
import SettingsIcon from "@mui/icons-material/Settings";
import TabletMacIcon from "@mui/icons-material/TabletMac";

import AccessTimeIcon from "@mui/icons-material/AccessTime";
import CheckCircleIcon from "@mui/icons-material/CheckCircle";

function getEnvironmentIcon(type) {

    switch (type) {

        case "WEB":
            return <PublicIcon fontSize="large" color="primary" />;

        case "DATABASE":
            return <StorageIcon fontSize="large" color="secondary" />;

        case "MIDDLEWARE":
            return <SettingsIcon fontSize="large" color="warning" />;

        case "COLLECTOR":
            return <TabletMacIcon fontSize="large" color="info" />;

        default:
            return <PublicIcon fontSize="large" />;
    }

}

function getStatusColor(status) {

    switch (status) {

        case "ONLINE":
            return "success";

        case "WARNING":
            return "warning";

        case "OFFLINE":
            return "error";

        default:
            return "default";
    }

}

function getStatusIcon(status) {

    switch (status) {

        case "ONLINE":
            return <CheckCircleIcon />;

        default:
            return undefined;
    }

}

export default function EnvironmentCard({ environment }) {

    const checkedAt = environment.checkedAt
        ? new Date(environment.checkedAt).toLocaleString("pt-BR")
        : "Ainda não verificado";

    return (
        <Card
            sx={{
                height: "100%",
                borderRadius: 3,
                transition: "0.2s",
                "&:hover": {
                    transform: "translateY(-4px)",
                    boxShadow: 6
                }
            }}
        >
            <CardContent>
                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1.5,
                        mb: 2
                    }}
                >
                    {getEnvironmentIcon(environment.type)}

                    <Box>
                        <Typography variant="h6" fontWeight="bold">
                            {environment.name}
                        </Typography>

                        <Typography variant="body2" color="text.secondary">
                            {environment.type}
                        </Typography>
                    </Box>
                </Box>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{
                        mb: 2,
                        wordBreak: "break-word"
                    }}
                >
                    {environment.endpoint}
                </Typography>

                <Chip
                    label={environment.status}
                    color={getStatusColor(environment.status)}
                    icon={getStatusIcon(environment.status)}
                    sx={{ mb: 2 }}
                />

                <Divider sx={{ mb: 2 }} />

                <Box
                    sx={{
                        display: "flex",
                        alignItems: "center",
                        gap: 1,
                        mb: 1
                    }}
                >
                    <AccessTimeIcon fontSize="small" />

                    <Typography variant="body2">
                        Tempo de resposta:{" "}
                        <strong>
                            {environment.responseTime != null
                                ? `${environment.responseTime} ms`
                                : "Indisponível"}
                        </strong>
                    </Typography>
                </Box>

                <Typography variant="body2" color="text.secondary">
                    Última verificação: {checkedAt}
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mt: 1 }}
                >
                    Detalhes: {environment.details ?? "Sem detalhes"}
                </Typography>
            </CardContent>
        </Card>
    );
}