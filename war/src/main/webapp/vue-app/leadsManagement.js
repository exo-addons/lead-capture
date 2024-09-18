import leadsManagementApp from './components/LeadsManagementApp.vue';
import './../css/main.less';
import CKEditor from '@ckeditor/ckeditor5-vue';
Vue.use(CKEditor);
Vue.use(Vuetify);


const vuetify = new Vuetify({
  dark: true,
  iconfont: 'mdi',
});

function initLeadsManagement() {
  const lang = eXo && eXo.env && eXo.env.portal && eXo.env.portal.language;
  const urls = [`${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.addon.LeadCapture-${lang}.json`,
  `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.portlet.taskManagement-${lang}.json`];

    exoi18n.loadLanguageAsync(lang, urls).then(i18n => {
      const vueApp = new Vue({
          render: (h) => h(leadsManagementApp),
          i18n,
          vuetify
      }).$mount('#leadsManagementApp');
      Vue.prototype.$vueT = Vue.prototype.$t;
      Vue.prototype.$t = (key, defaultValue) => {
          const translation = vueApp.$vueT(key);
          return translation !== key && translation || defaultValue;
      }
  });
}

document.onreadystatechange = () => {
  if (document.readyState === "complete") {
    initLeadsManagement();
  }
};
