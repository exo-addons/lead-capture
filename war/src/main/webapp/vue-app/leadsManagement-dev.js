import leadCaptureSettingsApp from './components/LeadCaptureSettingsApp.vue';
Vue.use(Vuetify);
const vueInstance = new Vue({
  el: '#leadCaptureSettingsApp',
  render: (h) => h(leadCaptureSettingsApp),
});
