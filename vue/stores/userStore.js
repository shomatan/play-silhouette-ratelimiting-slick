import http from '../services/http'

export default {
    debug: true,
    state: {
        user: {},
        authenticated: false,
    },

    login (email, password, successCb = null, errorCb = null) {
        var login_param = {email: email, password: password }
        console.log(login_param)

        http.post('/signIn', login_param, res => {
            this.state.user = res.data.user
            this.state.authenticated = true
            successCb()
        }, error => {
            errorCb()
        })
    },

    logout (successCb = null, errorCb = null) {
        http.get('/signOut', () => {
            localStorage.removeItem('jwt-token')
            this.state.authenticated = false
            successCb()
        }, errorCb)
    },

    setCurrentUser () {
        http.get('/', res => {
            this.state.user = res.data.user
            this.state.authenticated = true
        }, error => {
            errorCb()
        })
    },

    /**
     * Init the store.
     */
    init () {
        //this.setCurrentUser()
    }
}