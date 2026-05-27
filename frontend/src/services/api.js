import axios from 'axios';

// Cria uma instância do Axios com a URL base do seu backend
const api = axios.create({
    baseURL: 'http://192.168.100.38:8080',
});

export default api;