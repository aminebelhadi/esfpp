import axios from "axios";

const apiUrl = import.meta.env.VITE_BACKEND_URL || 'http://localhost:8080';

axios.defaults.baseURL = apiUrl;