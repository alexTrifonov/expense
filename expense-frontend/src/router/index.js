import Vue from 'vue'
import VueRouter from 'vue-router'
import Subcategory from '@/components/Subcategory.vue'
import Category from '@/components/Category.vue'
import Expense from '@/components/Expense.vue'
import ExpenseEdit from '@/components/ExpenseEdit.vue'
import ExpenseTable from '@/components/ExpenseTable.vue'
import NotFoundComponent from '@/components/NotFoundComponent'

import ExpenseBar from '@/components/ExpenseBar'

Vue.use(VueRouter)
const routes = [
    {
        path: '/',
        name: 'Expense',
        component: Expense
    },
    {
        path: '/category-action',
        name: 'Category',
        component: Category
    },
    {
        path: '/subcategory-action',
        name: 'Subcategory',
        component: Subcategory
    },
    {
        path: '/expense-table',
        name: 'ExpenseTable',
        component: ExpenseTable
    },
    {
        path: '/expense-edit/:id',
        name: 'ExpenseEdit',
        component: ExpenseEdit
    },    
    {
        path: '/bar',
        component: ExpenseBar
    },
    {
        path: '*',
        component: NotFoundComponent
    }
    
]

const router = new VueRouter({
    mode: 'history',
    //base: process.env.BASE_URL,
    base: '/expense-backend/',
    routes
})

export default router
