import { Card, CardContent, Typography } from "@mui/material";

export default function SummaryCard({ title, value, color }) {

    return (
        <Card
            sx={{
                borderRadius: 3,
                height: "100%",
                textAlign: "center",
                boxShadow: 2,
                transition: "0.2s",
                "&:hover": {
                    transform: "translateY(-3px)",
                    boxShadow: 5
                }
            }}
        >
            <CardContent>

                <Typography
                    variant="subtitle2"
                    color="text.secondary"
                    gutterBottom
                >
                    {title}
                </Typography>

                <Typography
                    variant="h3"
                    fontWeight="bold"
                    color={color}
                >
                    {value}
                </Typography>

            </CardContent>
        </Card>
    );

}