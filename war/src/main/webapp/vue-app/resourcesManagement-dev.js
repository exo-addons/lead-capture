import resourcesManagementApp from './components/ResourcesManagementApp.vue';

Vue.use(Vuetify);
const vueInstance = new Vue({
  el: '#resourcesManagementApp',
  render: (h) => h(resourcesManagementApp),
});
