import axios from 'axios'

const api = axios.create({
    baseURL: import.meta.env.VITE_API_URL
})

export function listarPessoas() {
    return api.get('/pessoas')
}

export function buscarPessoaPorId(id) {
    return api.get(`/pessoas/${id}`)
}

export function cadastrarPessoa(pessoa) {
    return api.post('/pessoas', pessoa)
}

export function excluirPessoa(id) {
    return api.delete(`/pessoas/${id}`)
}

export function atualizarPessoa(id, pessoa) {
    return api.put(`/pessoas/${id}`, pessoa)
}