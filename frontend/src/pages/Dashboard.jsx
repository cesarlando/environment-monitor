import { useEffect, useState } from "react";
import Grid from "@mui/material/Grid";

import {
    Box,
    Button,
    Container,
    TextField,
    Typography
} from "@mui/material";

import { getEnvironments } from "../services/environmentService";
import EnvironmentCard from "../components/EnvironmentCard";
import SummaryCard from "../components/SummaryCard";
import EnvironmentHistoryDialog from "../components/EnvironmentHistoryDialog";

export default function Dashboard() {

    const [environments, setEnvironments] = useState([]);
    const [selectedType, setSelectedType] = useState("ALL");
    const [searchTerm, setSearchTerm] = useState("");
    const [lastUpdate, setLastUpdate] = useState(null);
    const [selectedEnvironment, setSelectedEnvironment] = useState(null);
    const [historyOpen, setHistoryOpen] = useState(false);

    useEffect(() => {

        const loadEnvironments = () => {
            getEnvironments()
                .then((environments) => {
                setEnvironments(environments);
                setLastUpdate(new Date());
                })
                .catch(console.error);
        };

        loadEnvironments();

        const interval = setInterval(loadEnvironments, 30000);

        return () => clearInterval(interval);

    }, []);

    const handleOpenHistory = (environment) => {
        setSelectedEnvironment(environment);
        setHistoryOpen(true);
    };

    const handleCloseHistory = () => {
        setHistoryOpen(false);
        setSelectedEnvironment(null);
    };

    const environmentTypes = [
        "ALL",
        ...new Set(
            environments.map(environment => environment.type)
        )
    ];

    const filteredEnvironments = environments.filter((environment) => {

        const matchesType =
            selectedType === "ALL" ||
            environment.type === selectedType;

        const normalizedSearch = searchTerm
            .trim()
            .toLowerCase();

        const matchesSearch =
            normalizedSearch === "" ||
            environment.name
                .toLowerCase()
                .includes(normalizedSearch) ||
            environment.endpoint
                .toLowerCase()
                .includes(normalizedSearch) ||
            environment.type
                .toLowerCase()
                .includes(normalizedSearch);

        return matchesType && matchesSearch;
    });

    const online = environments.filter(
        environment => environment.status === "ONLINE"
    ).length;

    const offline = environments.filter(
        environment => environment.status === "OFFLINE"
    ).length;

    const warning = environments.filter(
        environment => environment.status === "WARNING"
    ).length;

    const total = environments.length;

    return (
        <Container maxWidth="lg">
            <Box sx={{ mt: 4 }}>

                <Typography
                    variant="h3"
                    fontWeight="bold"
                    gutterBottom
                >
                    Monitor de Ambientes
                </Typography>

                <Typography
                    variant="subtitle1"
                    color="text.secondary"
                    sx={{ mb: 4 }}
                >
                    Monitoramento de ambientes em tempo real
                </Typography>

                <Typography
                    variant="body2"
                    color="text.secondary"
                    sx={{ mb : 4 }}

                    >

                    Dashboard atualizado em: {" "}
                    {lastUpdate
                        ? lastUpdate.toLocaleString("pt-BR")
                        : "Aguardando atualização..."}

                </Typography>

                {/* Resumo */}
                <Grid container spacing={3} sx={{ mb: 4 }}>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <SummaryCard
                            title="Online"
                            value={online}
                            color="success.main"
                        />
                    </Grid>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <SummaryCard
                            title="Offline"
                            value={offline}
                            color="error.main"
                        />
                    </Grid>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <SummaryCard
                            title="Warning"
                            value={warning}
                            color="warning.main"
                        />
                    </Grid>

                    <Grid size={{ xs: 6, md: 3 }}>
                        <SummaryCard
                            title="Total"
                            value={total}
                            color="primary.main"
                        />
                    </Grid>

                </Grid>

                {/* Filtros */}
                <Box
                    sx={{
                        display: "flex",
                        gap: 2,
                        mb: 4,
                        flexWrap: "wrap"
                    }}
                >
                    {environmentTypes.map((filter) => (
                        <Button
                            key={filter}
                            variant={
                                selectedType === filter
                                    ? "contained"
                                    : "outlined"
                            }
                            onClick={() => setSelectedType(filter)}
                        >
                            {filter}
                        </Button>
                    ))}
                </Box>

                <TextField
                    fullWidth
                    label="Pesquisar ambiente"
                    placeholder="Digite o nome, endpoint ou tipo"
                    value={searchTerm}
                    onChange={(event) => setSearchTerm(event.target.value)}
                    sx={{ mb: 4 }}
                />

                {/* Ambientes */}
                <Grid container spacing={3}>

                    {filteredEnvironments.map((environment) => (
                        <Grid
                            key={environment.id}
                            size={{ xs: 12, md: 6, lg: 4 }}
                        >
                            <EnvironmentCard
                            environment={environment}
                            onOpenHistory={handleOpenHistory}
                             />
                        </Grid>
                    ))}

                </Grid>

            </Box>
            <EnvironmentHistoryDialog
                open={historyOpen}
                environment={selectedEnvironment}
                onClose={handleCloseHistory}
            />
        </Container>
    );
}