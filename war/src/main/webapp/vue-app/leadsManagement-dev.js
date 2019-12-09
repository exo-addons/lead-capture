import leadsManagementApp from './components/LeadsManagementApp.vue';

Vue.use(Vuetify);
const vueInstance = new Vue({
  el: '#leadsManagementApp',
  render: (h) => h(leadsManagementApp),
});
