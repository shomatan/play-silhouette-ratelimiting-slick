import 'bootstrap/dist/css/bootstrap.css';
import Vue from 'vue'

import VueRouter from 'vue-router'
import App from './App.vue'
import http from './services/http.js'
import userStore from './stores/userStore.js'

Vue.config.productionTip = false

Vue.use(VueRouter)


// Pointing routes to the components they should use
var router = new VueRouter({
    routes: [
        { name: 'index', path: '/',      component: require('./components/Index.vue') },
        { name: 'about', path: '/about', component: require('./components/About.vue') },
        { name: 'login', path: '/login', component: require('./components/Login.vue') }
    ],
    scrollBehavior (to, from, savedPosition) {
        if (savedPosition) {
            return savedPosition
        } else {
            return { x: 0, y: 0 }
        }
    },
})

new Vue({
    el: '#app',
    router: router,
    template: '<App/>',
    components: { App },
    created: function () {
        http.init()
        userStore.init()
    },
})