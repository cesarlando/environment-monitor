import { useEffect, useState } from "react";
import { getEnvironments } from "../services/environmentService";
import Grid from "@mui/material/Grid";
import EnvironmentCard from "../components/EnvironmentCard";

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