<template>
<v-flex>
    <v-container id="template">
        <v-card outlined style="padding: 16px;">
            <v-form ref="form" v-model="valid">

                <v-text-field v-model="template.name" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.templatesManagement.name','Name')" required></v-text-field>

                <v-text-field v-model="template.description" :label="$t('exoplatform.LeadCapture.templatesManagement.description','Description')"></v-text-field>

                <v-select v-model="template.event" :items="eventList" item-text="title" item-value="value" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.templatesManagement.event','Event')" required></v-select>
                <v-row>
                    <v-col cols="12" sm="5">
                        <v-text-field v-if="template.event==='newResponse'" v-model="template.form" :label="$t('exoplatform.LeadCapture.templatesManagement.form','Form')"></v-text-field>
                    </v-col>
                    <v-col cols="12" sm="5">
                        <v-text-field v-if="template.event==='newResponse'" v-model="template.field" :label="$t('exoplatform.LeadCapture.templatesManagement.fieldValue','Field value')"></v-text-field>
                    </v-col>
                </v-row>
                <v-card elevation="1" style="padding: 16px;">
                    <v-card-title class="" primary-title> {{ $t('exoplatform.LeadCapture.templatesManagement.contents','Contents')}}: </v-card-title>

                    <v-select v-model="selectedLanguage" :items="languageList" item-text="title" item-value="value" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.templatesManagement.language','Language')" @change="setContentByLang(selectedLanguage)" required></v-select>
                    <v-text-field v-model="selectedContent.subject" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.templatesManagement.subject','Subject')" required></v-text-field>

                    <ck-editor ref="ck" :content="selectedContent.content" />
                    <div>*{{$t('exoplatform.LeadCapture.templatesManagement.mailTemplateNotice1','You can use')}} $FIRST_NAME, $LAST_NAME, $MAIL, $RESOURCE and $RESOURCE_NAME {{$t('exoplatform.LeadCapture.templatesManagement.mailTemplateNotice2',"for lead's fields")}}</div>

                </v-card>

                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <div class="uiAction">
                        <button :disabled="!valid" @click="save()" class="btn btn-primary" type="button">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}
                        </button>
                        <button @click="backToList()" class="btn" type="button">{{$t('exoplatform.LeadCapture.leadManagement.close','Close')}}
                        </button>
                    </div>
                </v-card-actions>

            </v-form>
        </v-card>

    </v-container>
</v-flex>
</template>

<script>
import ckEditor from '../commons/ckEditor.vue';

export default {
    components: {
        ckEditor
    },
    props: ['template'],
    data: () => ({
        valid: true,
        selectedContent: {},
        subject: '',
        selectedLanguage: 'en',
        languageList: [{
                title: 'English',
                value: 'en'
            },
            {
                title: 'French',
                value: 'fr'
            },
            {
                title: 'German',
                value: 'de'
            },
            {
                title: 'Arabic',
                value: 'ar'
            }
        ],
        eventList: [{
                title: 'New Lead Captured',
                value: 'newLead'
            }, {
                title: 'New Response Added',
                value: 'newResponse'
            },
            {
                title: 'New Community Registration',
                value: 'newCommunityRegistration'
            },
            {
                title: 'New Trial Registration',
                value: 'newTrialRegistration'
            },
            {
                title: 'Compaign',
                value: 'compaign'
            }
        ],
        confirmDialog: false,
        fab: false,
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
        }
    }),

    methods: {
        backToList() {
            this.$emit('backToList');
        },

        initializeContent(content) {
            this.selectedLanguage = content.language
            this.selectedContent = content
            this.$refs.ck.setContent(content.content)
            this.subject = content.subject
        },

        setContentByLang(lang) {
            if (this.$refs.ck !== undefined) {
                this.selectedContent.content = this.$refs.ck.getContent();
            }
            this.selectedLanguage = lang
            this.selectedContent = this.template.contents.find(x => x.language === lang)
            this.$refs.ck.setContent(this.selectedContent.content)
            this.subject = this.selectedContent.subject
        },

        save() {
            if (this.$refs.ck !== undefined) {
                this.selectedContent.content = this.$refs.ck.getContent();
            }
            if (this.template.id === null || typeof (this.template.id) === 'undefined') {
                this.$emit('create', this.template);
            } else {
                this.$emit('update', this.template);
            }

        }

    },
};
</script>