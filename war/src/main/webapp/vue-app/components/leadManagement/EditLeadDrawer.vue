<template>
<exo-drawer ref="editLeadDrawer" right class="">
    <template slot="title">
       {{$t('exoplatform.LeadCapture.leadManagement.editLead','Edit Lead')}}
    </template>
    <template slot="content">
        <div>
            <form ref="form1">
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
                    <v-label for="geoZone">
                         {{ $t('exoplatform.LeadCapture.leadManagement.geographiqueZone','Geographique zone') }}
                    </v-label>
                    <select v-model="editedLead.geographiqueZone" name="geoZone" class="input-block-level ignore-vuetify-classes my-3">
                        <option v-for="item in gZoneList" :key="item" :value="item">
                            {{$t(`exoplatform.LeadCapture.leadManagement.${item}`,item)}}
                        </option>
                    </select>
                </v-row>              
                <v-row>
                        <div class="d-flex flex-wrap pt-2">
                            <label for="marketingSuspended" class="v-label theme--light my-auto float-left">
                                {{$t('exoplatform.LeadCapture.leadManagement.customer','Customer') }}
                            </label>
                            <v-switch ref="customer" v-model="editedLead.customer" class="float-left my-0 ml-4" />
                        </div>

                </v-row>              
                <v-row>
                        <div class="d-flex flex-wrap pt-2">
                            <label for="marketingSuspended" class="v-label theme--light my-auto float-left">
                                {{$t('exoplatform.LeadCapture.leadManagement.marketingSuspended','Marketing Suspended') }}
                            </label>
                            <v-switch ref="marketingSuspended" v-model="editedLead.marketingSuspended" class="float-left my-0 ml-4" />
                        </div>

                </v-row>
                <v-row v-if="editedLead.marketingSuspended">
                <v-label for="marketingSuspendedCause">
                        {{ $t('exoplatform.LeadCapture.leadManagement.marketingSuspendedCause','Marketing Suspended Causee') }}
                    </v-label>
                    <input ref="marketingSuspendedCause" v-model="editedLead.marketingSuspendedCause" type="text" name="marketingSuspendedCause" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
            </form>
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

    props: ['lead'],
    data: () => ({
        editedLead: {},
        gZoneList: ["US-Canada", "Europe", "LatAm", "APAC", "MEA"]
    }),
    computed: {
        editedLead: function () {
            return this.lead
        },
        
    },

    methods: {
        save() {
            this.lead=this.editedLead
            this.$emit('save', this.lead)
            this.$refs.editLeadDrawer.close()
        },
        cancel() {
            this.$refs.editLeadDrawer.close()
        },
        open() {
            this.editedLead=this.lead
            this.$refs.editLeadDrawer.open()
        },

    }
}
</script>

<style>
.drawerContent {
    padding: 15px 27px;
}
</style>
