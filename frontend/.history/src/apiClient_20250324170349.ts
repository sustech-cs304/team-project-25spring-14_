import axios, { AxiosInstance } from 'axios';

const apiClient: AxiosInstance = axios.create({
    baseURL: 'https://api.example.com',
    timeout: 5000,
});

apiClient.interceptors.request.use(
    (config) => {
        const jwtToken = localStorage.getItem('jwtToken');
        if (jwtToken) {
            config.headers.Authorization = `Bearer ${jwtToken}`;
        }