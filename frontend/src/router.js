
import Vue from 'vue'
import Router from 'vue-router'

Vue.use(Router);


import RequestManager from "./components/RequestManager"

import AuthManager from "./components/AuthManager"

import AccountManager from "./components/AccountManager"

import HistoryManager from "./components/HistoryManager"


import MyPage from "./components/MyPage"
import LoanRequestManager from "./components/LoanRequestManager"

import LoanAuthManager from "./components/LoanAuthManager"

import LoanManagerManager from "./components/LoanManagerManager"


import LoanStatus from "./components/LoanStatus"
import LoanMessengerManager from "./components/LoanMessengerManager"

export default new Router({
    // mode: 'history',
    base: process.env.BASE_URL,
    routes: [
            {
                path: '/requests',
                name: 'RequestManager',
                component: RequestManager
            },

            {
                path: '/auths',
                name: 'AuthManager',
                component: AuthManager
            },

            {
                path: '/accounts',
                name: 'AccountManager',
                component: AccountManager
            },

            {
                path: '/histories',
                name: 'HistoryManager',
                component: HistoryManager
            },


            {
                path: '/myPages',
                name: 'MyPage',
                component: MyPage
            },
            {
                path: '/loanRequests',
                name: 'LoanRequestManager',
                component: LoanRequestManager
            },

            {
                path: '/loanAuths',
                name: 'LoanAuthManager',
                component: LoanAuthManager
            },

            {
                path: '/loanManagers',
                name: 'LoanManagerManager',
                component: LoanManagerManager
            },


            {
                path: '/loanStatuses',
                name: 'LoanStatus',
                component: LoanStatus
            },
            {
                path: '/loanMessengers',
                name: 'LoanMessengerManager',
                component: LoanMessengerManager
            },



    ]
})
