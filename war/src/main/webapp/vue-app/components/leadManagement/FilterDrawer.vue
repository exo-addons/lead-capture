<template>
<v-container class="">
    <v-row class="mx-0 drawerHeader">
        <v-list-item>
            <v-list-item-content>
                <span class="drawerTitle">{{
              $t('exoplatform.LeadCapture.leadManagement.filter','Filter')
            }}</span>
            </v-list-item-content>
            <v-list-item-action class="appLauncherDrawerIcons">
                <i class="uiCloseIcon" @click="toggleDrawer()"></i>
            </v-list-item-action>
        </v-list-item>
    </v-row>
    <v-divider :inset="inset" class="my-0 headerBorder" />
    <v-row class="drawerContent">
        <v-col class="d-flex" cols="12" sm="11">
            <v-select :items="filterStatusList" v-model="selectedStatus" item-value="value" item-text="text" :label="$t('exoplatform.LeadCapture.leadManagement.status','Status')"></v-select>
        </v-col>
        <v-col class="d-flex" cols="12" sm="11">
            <v-select :items="methodList" v-model="selectedMethod" item-value="value" item-text="text" :label="$t('exoplatform.LeadCapture.leadManagement.captureMethod','Capture Methode')"></v-select>
        </v-col>
        <v-col class="d-flex" cols="12" sm="5">
            <v-switch :label="$t('exoplatform.LeadCapture.leadManagement.onlyUnassigned','Only unassigned')" v-model="notassigned"></v-switch>
        </v-col>
        <v-col class="d-flex" cols="12" sm="5">
            <v-switch :label="$t('exoplatform.LeadCapture.leadManagement.myLeads','My Leads')" v-model="myLeads"></v-switch>
        </v-col>
        <v-col class="d-flex" cols="12" sm="11">
            <v-select v-show="!notassigned && !myLeads" :items="assigneesFilter" v-model="selectedOwner" item-value="userName" item-text="fullName" :label="$t('exoplatform.LeadCapture.leadManagement.owner','Owner')"></v-select>
        </v-col>
    </v-row>

    <v-row class="drawerFooter mx-0">
        <v-card flat tile class="d-flex flex justify-end mx-2">
            <a class="text-uppercase caption primary--text drawersBtn" @click="reset()">{{ $t('exoplatform.LeadCapture.leadManagement.reset','Reset') }}</a>
            <a class="text-uppercase caption  drawersBtn" @click="aplyFilter()">{{ $t('exoplatform.LeadCapture.leadManagement.apply','Apply') }}</a>
        </v-card>
    </v-row>

</v-container>
</template>

<script>
export default {

    props: ['assigneesFilter'],
    data: () => ({
        selectedStatus: "",
        selectedMethod: "",
        selectedOwner: "",
        notassigned: false,
        myLeads: false,
    }),

    computed: {
        filterStatusList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.leadManagement.All'),
                    value: 'All'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Raw'),
                    value: 'Raw'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Open'),
                    value: 'Open'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Attempted'),
                    value: 'Attempted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Contacted'),
                    value: 'Contacted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Qualified'),
                    value: 'Qualified'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Recycled'),
                    value: 'Recycled'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Accepted'),
                    value: 'Accepted'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Bad_Data'),
                    value: 'Bad_Data'
                }
            ]
        },
        methodList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.leadManagement.All'),
                    value: 'All'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.contact-us'),
                    value: 'contact-us'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.demo-request'),
                    value: 'demo-request'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.resource-download'),
                    value: 'resource-download'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.reward-form'),
                    value: 'reward-form'
                }
            ]
        }
    },

    methods: {
        toggleDrawer() {
            this.$emit('toggleFilterDrawer');
        },

        reset() {
            this.selectedStatus = ""
            this.selectedMethod = ""
            this.selectedOwner = ""
            this.notassigned = false
            this.myLeads = false
        },
        aplyFilter() {
            const filter_ = {
                selectedStatus: this.selectedStatus,
                selectedMethod: this.selectedMethod,
                selectedOwner: this.selectedOwner,
                notassigned: this.notassigned,
                myLeads: this.myLeads,
            }
            this.$emit('addFilter', filter_);
        },

    }
}
</script>

<style>
</style>
