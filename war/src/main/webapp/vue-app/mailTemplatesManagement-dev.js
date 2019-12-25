import mailTemplatesManagementApp from './components/MailTemplatesManagementApp.vue';
import CKEditor from '@ckeditor/ckeditor5-vue';
Vue.use(CKEditor);
Vue.use(Vuetify);
const vueInstance = new Vue({
  el: '#mailTemplatesManagementApp',
  render: (h) => h(mailTemplatesManagementApp),
});
