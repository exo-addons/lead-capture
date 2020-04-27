<template>
<exo-drawer ref="addLeadDrawer" right class="">
    <template slot="title">
        {{$t('exoplatform.LeadCapture.leadManagement.addLead','Add Lead')}}
    </template>
    <template slot="content">
        <div>
            <v-form ref="form" v-model="valid">
                <v-row>
                    <v-label for="firstName">
                        {{ $t('exoplatform.LeadCapture.leadManagement.firstName','First name') }}
                    </v-label>
                    <input ref="firstName" v-model="editedLead.firstName" type="text" name="firstName" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="lastName">
                        {{ $t('exoplatform.LeadCapture.leadManagement.lastName','Last name') }}
                    </v-label>
                    <input ref="lastName" v-model="editedLead.lastName" type="text" name="lastName" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="mail">
                        {{ $t('exoplatform.LeadCapture.leadManagement.mail','Mail') }}
                    </v-label>
                    <input ref="mail" v-model="editedLead.mail" type="text" name="mail" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="company">
                        {{ $t('exoplatform.LeadCapture.leadManagement.company','Company') }}
                    </v-label>
                    <input ref="company" v-model="editedLead.company" type="text" name="company" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="phone">
                        {{ $t('exoplatform.LeadCapture.leadManagement.phone','Phone') }}
                    </v-label>
                    <input ref="phone" v-model="editedLead.phone" type="text" name="phone" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="position">
                        {{ $t('exoplatform.LeadCapture.leadManagement.position','Position') }}
                    </v-label>
                    <input ref="position" v-model="editedLead.position" type="text" name="position" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="country">
                        {{ $t('exoplatform.LeadCapture.leadManagement.country','Country') }}
                    </v-label>
                    <input ref="country" v-model="editedLead.inferredCountry" type="text" name="country" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="language">
                        {{ $t('exoplatform.LeadCapture.leadManagement.language','Language') }}
                    </v-label>
                    <input ref="language" v-model="editedLead.language" type="text" name="language" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="captureDetail">
                        {{ $t('exoplatform.LeadCapture.leadManagement.captureDetail','Capture Detail') }}
                    </v-label>
                    <input ref="captureDetail" v-model="editedLead.captureSourceInfo" type="text" name="captureDetail" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>

            </v-form>
        </div>
    </template>
    <template slot="footer">
        <div class="d-flex">
            <v-spacer />
            <v-btn class="btn mr-2" @click="cancel()">
                <template>
                    {{ $t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')  }}
                </template>
            </v-btn>
            <v-btn class="btn btn-primary" @click="save()">
                <template>
                    {{ $t('exoplatform.LeadCapture.leadManagement.save','Save') }}
                </template>
            </v-btn>
        </div>
    </template>
</exo-drawer>
</template>

<script>
export default {

    data: () => ({
        defaultItem: {
            name: '',
            mail: '',
            company: '',
            position: '',
            inferredCountry: '',
            status: '',
            captureSourceInfo: '',
        },
        editedLead: {},
        valid: true,
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
            valideMail: value => /.+@.+/.test(value) || 'E-mail must be valid',
        }
    }),

    methods: {
        save() {
            this.$emit('save', this.editedLead)
            this.editedLead=this.defaultItem
            this.$refs.addLeadDrawer.close()
        },
        cancel() {
            this.editedLead=this.defaultItem
            this.$refs.addLeadDrawer.close()
        },
        open() {
            this.$refs.addLeadDrawer.open()
        },

    }
}
</script>

<style>
.drawerContent {
    padding: 15px 27px;
}
</style>
