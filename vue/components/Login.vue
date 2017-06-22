<template>
    <div>
        <div class="container">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <div class="panel panel-default">
                        <div class="panel-heading">Login</div>
                        <div class="panel-body">


                            <div class="alert alert-danger" role="alert">
                                Wrong email or password.
                            </div>

                            <div class="form-group">
                                <label for="email" class="col-md-4 control-label">E-Mail Address</label>
                                <div class="col-md-6">
                                    <input id="email" type="email" v-model="email" class="form-control" required autofocus>
                                </div>
                            </div>

                            <div class="form-group">
                                <label for="password" class="col-md-4 control-label">Password</label>
                                <div class="col-md-6">
                                    <input id="password" type="password" v-model="password" class="form-control" required autofocus>
                                </div>
                            </div>

                            <div class="form-group">
                                <div class="col-md-8 col-md-offset-4">
                                    <button v-on:click="login" class="btn btn-primary">
                                        Login
                                    </button>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</template>

<script>
    import userStore from '../stores/userStore'
    import http from '../services/http'

    export default {

        data() {
            return {
                email: '',
                password: '',
                showAlert: false,
                alertMessage: '',
            }
        },
        methods: {
            login () {
                userStore.login(this.email, this.password, res => {

                    this.$router.push('/')
                }, error => {
                    this.showAlert = true
                    this.alertMessage = 'Wrong email or password.'
                })
            },
        }
    }
</script>