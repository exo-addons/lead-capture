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
        <v-col class="d-flex" cols="12" sm="11">
            <v-select :items="gZoneList" v-model="selectedGeoZone" :label="$t('exoplatform.LeadCapture.leadManagement.geographiqueZone','Geographique Zone')"></v-select>
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
<v-col class="d-flex" cols="12" sm="11">
     <v-menu
        ref="menu1"
        v-model="menu1"
        :close-on-content-click="false"
        :return-value.sync="fromDate"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on }">
          <v-text-field
            v-model="fromDate"
            :label="$t('exoplatform.LeadCapture.leadManagement.createdFrom','Created From')"
            prepend-icon="event"
            readonly
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="fromDate" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="menu1 = false">Cancel</v-btn>
          <v-btn text color="primary" @click="$refs.menu1.save(fromDate)">OK</v-btn>
        </v-date-picker>
      </v-menu>
      </v-col>
      <v-col class="d-flex" cols="12" sm="11">
     <v-menu
        ref="menu2"
        v-model="menu2"
        :close-on-content-click="false"
        :return-value.sync="toDate"
        transition="scale-transition"
        offset-y
        min-width="290px"
      >
        <template v-slot:activator="{ on }">
          <v-text-field
            v-model="toDate"
            :label="$t('exoplatform.LeadCapture.leadManagement.createdBefore','Created Before')"
            prepend-icon="event"
            readonly
            v-on="on"
          ></v-text-field>
        </template>
        <v-date-picker v-model="toDate" no-title scrollable>
          <v-spacer></v-spacer>
          <v-btn text color="primary" @click="menu2 = false">Cancel</v-btn>
          <v-btn text color="primary" @click="$refs.menu2.save(toDate)">OK</v-btn>
        </v-date-picker>
      </v-menu>
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
        fromDate: "",
        toDate: "",
        selectedStatus: "active",
        selectedMethod: "",
        selectedOwner: "",
        notassigned: false,
        myLeads: false,
        selectedGeoZone:"",
        gZoneList:["US-Canada","Western Europe","Eastern Europe","LatAm","APAC","MEA"]
    }),

    computed: {
        filterStatusList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.leadManagement.All'),
                    value: 'All'
                },{
                    text: this.$t('exoplatform.LeadCapture.status.active'),
                    value: 'active'
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
                    text: this.$t('exoplatform.LeadCapture.leadManagement.community-registration'),
                    value: 'Community registration'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.Blog'),
                    value: 'Blog'
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
            this.selectedStatus = "active"
            this.selectedMethod = ""
            this.selectedOwner = ""
            this.notassigned = false
            this.myLeads = false
            this.fromDate= ""
            this.toDate= ""
            this.selectedGeoZone=""
        },
        aplyFilter() {
            console.log(this.fromDate)
            console.log(this.toDate)
            const filter_ = {
                selectedStatus: this.selectedStatus,
                selectedMethod: this.selectedMethod,
                selectedOwner: this.selectedOwner,
                notassigned: this.notassigned,
                myLeads: this.myLeads,
                fromDate: this.fromDate,
                toDate: this.toDate,
                selectedGeoZone:this.selectedGeoZone
            }
            this.$emit('addFilter', filter_);
        },

    }
}
</script>

<style>
</style>
