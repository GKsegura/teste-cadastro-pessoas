import { createApp } from 'vue'
import Toast from 'vue-toastification'
import 'vue-toastification/dist/index.css'

import 'bootstrap/dist/css/bootstrap.min.css'
import 'bootstrap/dist/js/bootstrap.bundle.min.js'

import App from './App.vue'
import router from './router'

import '@/assets/css/styles.css'

const app = createApp(App)

app.use(router)
app.use(Toast, {
    position: 'bottom-right',
    timeout: 3000
})

app.mount('#app')
