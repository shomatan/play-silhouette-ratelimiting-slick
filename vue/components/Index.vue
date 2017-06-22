<template>
    <div>
        please <router-link to="/login">Login.</router-link>

        <div>
            <strong>Hello</strong>
            <p>Your tasks here.</p>
        </div>
    </div>
</template>
<script>
    import http from '../services/http'

    export default {
        mounted() {
            this.fetchTasks()
        },
        data() {
            return {
                tasks: [],
                name: '',
                showAlert: false,
                alertMessage: '',
            }
        },
        methods: {
            fetchTasks () {
                http.get('/limit', res => {
                    this.tasks = res.data
                    console.log(this.tasks)
                })
            }
        }
    }
</script>