import {
    Alert,
    Box,
    Card,
    CardContent,
    Chip,
    CircularProgress,
    Dialog,
    DialogContent,
    DialogTitle,
    Divider,
    Typography
} from "@mui/material";

import FactCheckOutlinedIcon from "@mui/icons-material/FactCheckOutlined";
import ErrorOutlineOutlinedIcon from "@mui/icons-material/ErrorOutlineOutlined";
import SpeedIcon from "@mui/icons-material/Speed";

import { useEffect, useState } from "react";

export default function EnvironmentHistoryDialog({
    open,
    environment,
    onClose
}) {

    const [history, setHistory] = useState([]);
    const [summary, setSummary] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    useEffect(() => {

        if (!open || !environment) {
            return;
        }

        async function loadHistory() {

            try {
                setLoading(true);
                setError(null);
                setHistory([]);
                setSummary(null);

                const [historyResponse, summaryResponse] =
                    await Promise.all([
                        fetch(
                            `http://localhost:8080/api/environments/${environment.id}/history`
                        ),
                        fetch(
                            `http://localhost:8080/api/environments/${environment.id}/history/summary`
                        )
                    ]);

                if (!historyResponse.ok || !summaryResponse.ok) {
                    throw new Error("Erro ao carregar histórico");
                }

                const historyData = await historyResponse.json();
                const summaryData = await summaryResponse.json();

                setHistory(historyData);
                setSummary(summaryData);

            } catch (exception) {
                console.error(exception);
                setError("Não foi possível carregar o histórico.");
            } finally {
                setLoading(false);
            }
        }
            loadHistory();
}, [open, environment]);


    return (
        <Dialog
            open={open}
            onClose={onClose}
            fullWidth
            maxWidth="md"
        >
            <DialogTitle>
                Histórico - {environment?.name}
            </DialogTitle>

            <DialogContent>

            {loading && (
                <Box
                    sx={{
                        display: "flex",
                        justifyContent: "center",
                        py: 4
                    }}
                >
                    <CircularProgress />
                </Box>
            )}

            {error && (
                <Alert severity="error" sx={{ mb: 3 }}>
                    {error}
                </Alert>
            )}

            {!loading && !error && history.length === 0 && (
                <Alert severity="info">
                    Nenhum histórico disponível para este ambiente.
                </Alert>
            )}
        {!loading && !error && (
        <>
               {summary && (
                   <Box
                       sx={{
                           display: "grid",
                           gridTemplateColumns: {
                               xs: "1fr",
                               sm: "repeat(3, 1fr)"
                           },
                           gap: 2,
                           mb: 3
                       }}
                   >
                       <Card variant="outlined">
                           <CardContent>
                               <Box
                                   sx={{
                                       display: "flex",
                                       alignItems: "center",
                                       gap: 1,
                                       mb: 1
                                   }}
                               >
                                   <FactCheckOutlinedIcon color="primary" />

                                   <Typography
                                       variant="body2"
                                       color="text.secondary"
                                   >
                                       Verificações
                                   </Typography>
                               </Box>

                               <Typography variant="h5" fontWeight="bold">
                                   {summary.totalChecks}
                               </Typography>
                           </CardContent>
                       </Card>

                       <Card variant="outlined">
                           <CardContent>
                               <Box
                                   sx={{
                                       display: "flex",
                                       alignItems: "center",
                                       gap: 1,
                                       mb: 1
                                   }}
                               >
                                   <ErrorOutlineOutlinedIcon color="error" />

                                   <Typography
                                       variant="body2"
                                       color="text.secondary"
                                   >
                                       Offline
                                   </Typography>
                               </Box>

                               <Typography variant="h5" fontWeight="bold">
                                   {summary.offlineCount}
                               </Typography>
                           </CardContent>
                       </Card>

                       <Card variant="outlined">
                           <CardContent>
                               <Box
                                   sx={{
                                       display: "flex",
                                       alignItems: "center",
                                       gap: 1,
                                       mb: 1
                                   }}
                               >
                                   <SpeedIcon color="info" />

                                   <Typography
                                       variant="body2"
                                       color="text.secondary"
                                   >
                                       Tempo médio
                                   </Typography>
                               </Box>

                               <Typography variant="h5" fontWeight="bold">
                                   {summary.averageResponseTime?.toFixed(2)} ms
                               </Typography>
                           </CardContent>
                       </Card>

                   </Box>
               )}

                <Divider sx={{ mb: 2 }} />

                {history.map((item) => (
                    <Box
                        key={item.id}
                        sx={{
                            p: 2,
                            mb: 2,
                            border: "1px solid",
                            borderColor: "divider",
                            borderRadius: 2
                        }}
                    >
                        <Box
                            sx={{
                                display: "flex",
                                justifyContent: "space-between",
                                alignItems: "center",
                                mb: 1.5
                            }}
                        >
                            <Chip
                                label={item.status}
                                color={
                                    item.status === "ONLINE"
                                        ? "success"
                                        : item.status === "OFFLINE"
                                        ? "error"
                                        : "warning"
                                }
                                size="small"
                            />

                            <Typography
                                variant="body2"
                                color="text.secondary"
                            >
                                {new Date(item.checkedAt)
                                    .toLocaleString("pt-BR")}
                            </Typography>
                        </Box>

                        <Typography variant="body2" sx={{ mb: 0.5 }}>
                            Tempo de resposta:{" "}
                            <strong>
                                {item.responseTime != null
                                    ? `${item.responseTime} ms`
                                    : "Indisponível"}
                            </strong>
                        </Typography>

                        <Typography
                            variant="body2"
                            color="text.secondary"
                        >
                            {item.details ?? "Sem detalhes"}
                        </Typography>
                    </Box>
                    ))}
                    </>
                )}

            </DialogContent>
        </Dialog>
    );
}