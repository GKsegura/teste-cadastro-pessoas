import { createApp } from 'vue'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'
import App from './App.vue'

const app = createApp(App)
app.use(Toast, { position: 'bottom-right', timeout: 3000 })
app.mount('#app')