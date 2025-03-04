import leadCaptureSettingsApp from './components/LeadCaptureSettingsApp.vue';
import './../css/main.less';

Vue.use(Vuetify);


const vuetify = new Vuetify({
  dark: true,
  iconfont: 'mdi',
});

const lang = eXo && eXo.env && eXo.env.portal && eXo.env.portal.language;
const url = `${eXo.env.portal.context}/${eXo.env.portal.rest}/i18n/bundle/locale.addon.LeadCapture-${lang}.json`;

exoi18n.loadLanguageAsync(lang, url).then(i18n => {
  new Vue({
    render: (h) => h(leadCaptureSettingsApp),
    i18n,
    vuetify
  }).$mount('#leadCaptureSettingsApp');
});




