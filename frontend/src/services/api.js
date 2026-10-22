import axios from 'axios';

// Cria uma instância do Axios com a URL base do seu backend
const api = axios.create({
    // Pode ser sobrescrita pela variável VITE_API_URL (ex.: em frontend/.env.local)
    baseURL: import.meta.env.VITE_API_URL || 'http://192.168.100.38:8080',
});

export default api;