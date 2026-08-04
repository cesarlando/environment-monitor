import { useEffect, useState } from "react";
import { getEnvironments } from "../services/environmentService";
import Grid from "@mui/material/Grid";
import EnvironmentCard from "../components/EnvironmentCard";
import SummaryCard from "../components/SummaryCard";

import {
    Box,
    Container,
    Typography
} from "@mui/material";

export default function Dashboard() {

    const [environments, setEnvironments] = useState([]);

  useEffect(() => {

      const load = () => {
          getEnvironments()
              .then(setEnvironments)
              .catch(console.error);
      };

      load();

      const interval = setInterval(load, 30000);

      return () => clearInterval(interval);

  }, []);

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
                    Environment Monitor
                </Typography>

                <Typography
                    variant="subtitle1"
                    color="text.secondary"
                    sx={{ mb: 4 }}
                >
                    Monitoring your environments in real time
                </Typography>

                <Grid container spacing={3} sx={{ mb: 4, mt: 1 }}>

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

                <Grid container spacing={3}>
                    {environments.map((environment) => (
                        <Grid
                            key={environment.id}
                            size={{ xs: 12, md: 6, lg: 4 }}
                        >
                            <EnvironmentCard environment={environment} />
                        </Grid>
                    ))}
                </Grid>

            </Box>
        </Container>
    );
}