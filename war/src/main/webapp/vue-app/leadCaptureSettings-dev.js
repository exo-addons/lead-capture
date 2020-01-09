import leadsManagementApp from './components/LeadsManagementApp.vue';
import CKEditor from '@ckeditor/ckeditor5-vue';
Vue.use(CKEditor);
Vue.use(Vuetify);
const vueInstance = new Vue({
  el: '#leadsManagementApp',
  render: (h) => h(leadsManagementApp),
});
