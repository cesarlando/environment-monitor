const API_URL = "http://localhost:8080/api/environments/status";

export async function getEnvironments() {

    const response = await fetch(API_URL);

    if (!response.ok) {
        throw new Error("Failed to load environments.");
    }

    return await response.json();

}