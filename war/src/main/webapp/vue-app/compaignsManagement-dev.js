import compaignsManagementApp from './components/CompaignsManagementApp.vue';

Vue.use(Vuetify);
const vueInstance = new Vue({
    el: '#compaignssManagementApp',
    render: (h) => h(compaignsManagementApp),
});