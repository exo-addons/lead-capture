<template>
<v-flex>
    <v-container id="template">
        <!--         <v-tooltip bottom>
            <template v-slot:activator="{ on }">
                <v-btn class="returnBoutton" color="secondary" @click="backToList()" fab dark v-on="on">
                    <v-icon dark>mdi-arrow-left</v-icon>
                </v-btn>
            </template>
            <span>Back to template list</span>
        </v-tooltip> -->

        <v-card outlined style="padding: 16px;">
            <v-form ref="form" v-model="valid">

                <v-text-field v-model="template.name" :rules="[rules.required]" label="Name" required></v-text-field>

                <v-text-field v-model="template.description" label="Description"></v-text-field>

                <v-select v-model="template.event" :items="eventList" item-text="title" item-value="value" :rules="[rules.required]" label="Event" required></v-select>
               <v-row>
               <v-col cols="12" sm="5">
                <v-text-field v-if="template.event==='newResponse'" v-model="template.form" label="Form"></v-text-field>
                </v-col>
                <v-col cols="12" sm="5">
                <v-text-field v-if="template.event==='newResponse'" v-model="template.field" label="Field value"></v-text-field>
                 </v-col>
                 </v-row>
                <v-card elevation="1" style="    padding: 16px;">
                    <v-card-title class="" primary-title> Contents: </v-card-title>

                    <v-select v-model="selectedLanguage" :items="languageList" item-text="title" item-value="value" :rules="[rules.required]" label="Language" @change="setContentByLang(selectedLanguage)" required></v-select>
                    <v-text-field v-model="selectedContent.subject" :rules="[rules.required]" label="Subject" required></v-text-field>

                    <ck-editor ref="ck" :content="selectedContent.content" />
                    <div>*You can use $FIRST_NAME, $LAST_NAME, $MAIL for lead's fields</div>

                </v-card>
                
                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <div class="uiAction">
                        <button :disabled="!valid" @click="save()" class="btn btn-primary" type="button">Save
                        </button>
                        <button @click="backToList()" class="btn" type="button">close
                        </button>
                    </div>
                </v-card-actions>

            </v-form>
        </v-card>

    </v-container>
</v-flex>
</template>

<script>
import ckEditor from './ckEditor.vue';

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

<style>
body {
    background: -webkit-linear-gradient(left, #3931af, #00c6ff);
}
</style>
