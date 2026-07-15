import axios from 'axios'

const api = axios.create({
    baseURL: 'http://localhost:8080'
})

export function listarPessoas() {
    return api.get('/pessoas')
}

export function cadastrarPessoa(pessoa) {
    return api.post('/pessoas', pessoa)
}

export function excluirPessoa(id) {
    return api.delete(`/pessoas/${id}`)
}