import leadsManagementApp from './components/LeadsManagementApp.vue';
import './../css/main.less';
import CKEditor from '@ckeditor/ckeditor5-vue';
Vue.use(CKEditor);
Vue.use(Vuetify);


const vuetify = new Vuetify({
  dark: true,
  iconfont: 'mdi',
});

$(document).ready(() => {
    new Vue({
      render: (h) => h(leadsManagementApp),
      vuetify,
    }).$mount('#leadsManagementApp');

});