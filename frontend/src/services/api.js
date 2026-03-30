import axios from 'axios';

// Cria uma instância do Axios com a URL base do seu backend
const api = axios.create({
    baseURL: 'http://localhost:8080',
});

export default api;