<template>
 <div>
         <div :class="alert_type" class="alert" id v-if="alert">
        <i :class="alertIcon"></i>
        {{message}}
    </div>
<v-card elevation="0"   v-show="showTable">
   <v-card-text>
    <v-layout>
        <v-overlay opacity=0.7 :value="!context.leadCaptureConfigured" z-index=1000>
            <v-btn v-if="context.isManager" outlined x-large href="/portal/g/:platform:administrators/lead_capture_settings">
                <v-icon x-large>mdi-settings</v-icon> <br>
                <div>{{$t('exoplatform.LeadCapture.leadManagement.configrationWarning','The lead capture feature should be configured')}}</div>
            </v-btn>
            <div v-else class="ConfWarning">
                <v-icon x-large>mdi-alert</v-icon> <br>
                {{$t('exoplatform.LeadCapture.leadManagement.configrationWarningMassage',"The lead capture feature is not configured, please contact your system administrator")}}

            </div>

        </v-overlay>
        <v-data-table :headers="headers" :items="leadList" :options.sync="options" :server-items-length="totalLeads" :loading="loading"  elevation="0" >
            <template v-slot:top>
                <v-toolbar color="white" flat>
                        <template>
 
                            <button class="btn btn-primary pull-left" type="button" @click="openAddLeadDrawer">
          <i class="uiIconSocSimplePlus uiIconSocWhite"></i> {{$t('exoplatform.LeadCapture.leadManagement.addLead',"Add Lead")}}
        </button>
                       


                     <download-excel :fetch="exportData" :fields="json_fields" name="leads.xls">
                            <button class="btn btn-export" type="button">
          <i class="uiIconExport"></i> {{$t('exoplatform.LeadCapture.leadManagement.export',"Export")}}
        </button>                    </download-excel>
                     

                    <v-spacer />
                    <v-col cols="12" md="3" sm="6">
                        <v-text-field placeholder="Look for leads" prepend-inner-icon="mdi-filter" single-line label="" v-model="search"></v-text-field>
                    </v-col>

                   <a class="caption primary--text drawersBtn" @click="toggleFilterDrawer"> <v-icon color="primary" left>mdi-tune</v-icon> Filter </a>

                    </template>
                </v-toolbar>
            </template>
            <template v-slot:item.name="{ item }">
                <a @click="edit(item)">
                    <b>{{item.firstName}} {{item.lastName}}</b>
                </a>
            </template>
            <template v-slot:item.mail="{ item }">
                <a @click="edit(item)">
                    {{item.mail}}
                </a>
            </template>

            <template v-slot:item.captureMethod="{ item }">

                {{$t(`exoplatform.LeadCapture.leadManagement.${item.captureMethod}`,item.captureMethod)}}
            </template>

            <template v-slot:item.assignee="{ item }">
                <select v-model="item.assignee" @change="onAssign(item)">
                    <option :value="null">{{$t('exoplatform.LeadCapture.leadManagement.notAssigned','Not assigned')}}
                    </option>
                    <option :key="option.userName" v-bind:value="option.userName" v-for="option in assignees">
                        {{option.fullName}}
                    </option>
                </select>

            </template>
            <template v-slot:item.status="{ item }">
                <select v-model="item.status" @change="changeStatus(item)">

                    <option :key="option" v-bind:value="option" v-for="option in statusList">
                        {{$t(`exoplatform.LeadCapture.status.${option}`,option)}}
                    </option>
                </select>

            </template>

            <template v-slot:no-data>{{$t('exoplatform.LeadCapture.leadManagement.noLeads','No Leads')}}</template>
        </v-data-table>
    </v-layout>

    </v-card-text>
</v-card>
        <filter-drawer ref="filterDrawer" :assigneesFilter="assigneesFilter" v-on:addFilter="addFilter"/>
         <add-lead-drawer ref="addLeadDrawer"  v-on:save="save"/>

    <lead-details :lead="selectedLead" :formResponses="formResponses" :timeline="timeline" :comments="comments" :tasks="tasks" :context="context" :assignees="assignees" v-on:backToList="backToList" v-on:remove="delete_" v-on:changeStatus="changeStatus" v-on:saveLead="editItem" v-on:assigne="assignLead" v-show="showDetails" />

</div>
</template>

<script>
import leadDetails from './LeadDetails.vue';
import filterDrawer from './FilterDrawer.vue';
import addLeadDrawer from './AddLeadDrawer.vue';
import downloadExcel from "vue-json-excel";
export default {
    components: {
        downloadExcel,
        leadDetails,
        filterDrawer,
        addLeadDrawer
    },

    data: () => ({
        json_fields: {
            'id': 'id',
            'Mail': 'mail',
            'First name': 'firstName',
            'Last name': 'lastName',
            'status': 'status',
            'Created date': 'formattedCreatedDate',
            'Updated date': 'formattedUpdatedDate',
            'Language': 'language',
            'Company': 'company',
            'Country': 'inferredCountry',
            'Geo zone': 'geographiqueZone',
            'Phone': 'phone',
            'Position': 'position',
            'Industry': 'industry',
            'captureMethod': 'captureMethod',
            'Capture type': 'captureType',
            'Capture details': 'captureSourceInfo',
            'Person Source': 'personSource',
            'Original referrer': 'originalReferrer',
            'Community registration': 'communityRegistration',
            'Community UserName': 'communityUserName',
            'Community registration method': 'communityRegistrationMethod',
            'Community registration date': 'formattedCommunityRegistrationDate',
            'Blog subscription': 'blogSubscription',
            'Blog subscription date': 'formattedBlogSubscriptionDate',
            'Marketing suspended': 'marketingSuspended',
            'Marketing suspended cause': 'marketingSuspendedCause',
            'Goal': 'goal',
            'Users number': 'usersNumber',
            'How': 'howHear',
            'Solution type': 'solutionType',
            'Employees number': 'employeesNumber',
            'shortlistVendors': 'shortlistVendors',
            'Landing page info': 'landingPageInfo',
            'Company Website': 'companyWebsite',
            'Current solution': 'currentSolution',
        },
        //filtered: "grey-color",
        totalLeads: 0,
        loading: true,
        options: {},
        statusList: ['Raw', 'Open', 'Attempted', 'Contacted', 'Qualified', 'Recycled', 'Accepted', 'Bad_Data', 'Duplicate'],
        selectedStatus: "active",
        selectedMethod: "",
        selectedOwner: "",
        selectedGeoZone: "",
        userNumberMax:0,
        userNumberMin:0,
        fromDate: "",
        toDate: "",
        valid: true,
        notassigned: false,
        myLeads: false,
        currentUser: 'test1',
        assignees: [],
        assigneesFilter: [{
            "fullName": "All",
            "userName": "",
            "email": ""
        }],
        showTable: false,
        showDetails: false,
        search: '',
        awaitingSearch: false,
        dialog: false,
        itemToDelete: 0,
        alert: false,
        message: '',
        alert_type: '',
        alertIcon: '',
        alert_edit: false,
        message_edit: '',
        alert_type_add: '',
        leadList: [],
        allLeads: [],
        editedIndex: -1,
        context: {
            leadCaptureConfigured: true
        },
        formResponses: [],
        timeline: [],
        comments: [],
        tasks: [],
        selectedLead: {},
    }),
    created() {
        fetch(`/portal/rest/leadcapture/lcsettings/context`, {
                credentials: 'include',
            })
            .then((resp) => resp.json())
            .then((resp) => {
                this.context = resp;
                if (this.context.leadCaptureConfigured) {
                    this.initialize()
                }
            });
        fetch(`/portal/rest/leadcapture/leadsmanagement/marketers`, {
                credentials: 'include',
            })
            .then((resp) => resp.json())
            .then((resp) => {
                this.assignees = resp;
                this.assigneesFilter.push(...this.assignees);
            });
    },
    watch: {
        options: {
            handler() {
                if (this.context.leadCaptureConfigured) {
                    this.getLeads()
                        .then(data => {
                            this.leadList = data.items
                            this.totalLeads = data.total
                        })
                }
            },
            deep: true,
        },
        dialog(val) {
            return val === true || this.close() === true;
        },

        search: function (val) {
            if (!this.awaitingSearch) {
                setTimeout(() => {
                    this.getLeads().then(data => {
                        this.leadList = data.items
                        this.totalLeads = data.total
                    })
                    this.awaitingSearch = false;
                }, 1000); // 1 sec delay
            }
            this.awaitingSearch = true;

        },

    },
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
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Duplicate'),
                    value: 'Duplicate'
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
        },
        headers() {
            return [{
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.fullName`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'name',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.mail`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'mail',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.createdDate`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'formattedCreatedDate',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.captureMethod`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'captureMethod',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.owner`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'assignee',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.country`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'inferredCountry',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.geographiqueZone`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'geographiqueZone',
                },
                {
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.status`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'status',
                },
            ]
        },
        filtered(){
            if (this.selectedStatus !== "" || this.selectedMethod !== "" || this.selectedOwner !== "" || this.fromDate !== "" || this.toDate !== "" || this.selectedGeoZone !== "" || this.notassigned || this.myLeads) {
                return "uiIconBlue"
            }
            return "grey-color"
        }

    },
    methods: {
        addFilter(val) {
            this.selectedStatus = val.selectedStatus
            this.selectedMethod = val.selectedMethod
            this.selectedOwner = val.selectedOwner
            this.notassigned = val.notassigned
            this.fromDate = val.fromDate
            this.toDate = val.toDate
            if(val.selectedGeoZone!=="All"){this.selectedGeoZone = val.selectedGeoZone}
            this.myLeads = val.myLeads
            if(val.userNumberMax!==""){this.userNumberMax = val.userNumberMax}
            if(val.userNumberMin!==""){this.userNumberMin = val.userNumberMin}
            this.getLeads().then(data => {
                this.leadList = data.items
                this.totalLeads = data.total
            })
        },
        toggleFilterDrawer() {
            this.$refs.filterDrawer.open()
        },
        openAddLeadDrawer() {
            this.$refs.addLeadDrawer.open()
        },

        

        initialize() {
            const leadId = this.getUrlParameterByName("leadid");
            if (leadId != null) {
                const lead = this.getLeadById(leadId)
                if (this.lead === null) {
                    this.getLeads()
                        .then(data => {
                            this.leadList = data.items
                            this.totalLeads = data.total
                        })
                }
            }

        },

        getLeadById(id) {
            fetch(`/portal/rest/leadcapture/leadsmanagement/leads/` + id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    const item = resp
                    if (item !== null) {
                        this.edit(item)
                        return item
                    }
                    return null
                });
        },

        edit(item) {
            this.selectedLead = item
            const telemarketer = this.assignees.find(x => x.userName === item.assignee)
            if (typeof (telemarketer) !== 'undefined') {
                this.selectedLead.telemarketerFullName = telemarketer.fullName
                this.selectedLead.telemarketerMail = telemarketer.email
            }
            this.showTable = false;
            this.showDetails = true;
            let url = window.location.href;
            if (url.includes('?leadid=')) {
                url = url.split('?leadid=')[0];
            }
            url = url + "?leadid=" + item.id;
            history.pushState({
                id: 'leadDetail'
            }, 'Lead detail', url);
            this.showTable = false
            this.showDetails = true
            fetch(`/portal/rest/leadcapture/leadsmanagement/responses/` + item.id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.formResponses = resp;
                });

            fetch(`/portal/rest/leadcapture/leadsmanagement/timeline/` + item.id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.timeline = resp.sort((a, b) => b.time - a.time);; 
                });    

            fetch(`/portal/rest/leadcapture/leadsmanagement/ptask/` + item.id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.tasks = resp;
                });

            if (item.taskId != null) {
                fetch(`/portal/rest/leadcapture/leadsmanagement/comments/` + item.taskId, {
                        credentials: 'include',
                    })
                    .then((resp) => resp.json())
                    .then((resp) => {
                        this.comments = resp;;
                    });

            }

        },

        editItem(item) {
            fetch(`/portal/rest/leadcapture/leadsmanagement/leads/` + item.id, {
                    method: 'PUT',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(item),
                })
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.initialize();
                    this.displaySusccessMessage('lead updated');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },
        delete_(item) {
            fetch(`/portal/rest/leadcapture/leadsmanagement/leads/` + item.id, {
                    method: 'delete',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                })
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.confirmDialog = false;
                    this.initialize();
                    this.displaySusccessMessage('lead deleted');
                    this.showDetails = false;
                    this.showTable = true;
                })
                .catch((result) => {
                    this.confirmDialog = false;
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        close() {
            this.dialog = false;
            this.editedItem = this.defaultItem;
            setTimeout(() => {
                this.editedIndex = -1;
                this.initialize();
            }, 300);
        },

        save(editedItem) {

            this.leadList.push(editedItem);
            this.allLeads.push(editedItem);
            fetch(`/portal/rest/leadcapture/leadsmanagement/create`, {
                    method: 'post',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(editedItem),
                })
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.initialize();
                    this.displaySusccessMessage('Lead created');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });

            this.close();

        },

        onAssign(item) {
            this.assigne(item);
        },

        assignLead(item) {
            this.assigne(item);
            this.selectedLead.assignee = item.assignee           
        },


        changeStatus(item) {
            const lead = {
                "id": item.id,
                "mail": item.mail,
                "status": item.status
            }

            fetch(`/portal/rest/leadcapture/leadsmanagement/status`, {
                    method: 'PATCH',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(lead),
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.selectedLead = resp
                })
            this.close();
        },

        assigne(item) {
            const lead = {
                "id": item.id,
                "mail": item.mail,
                "assignee": item.assignee
            }

            fetch(`/portal/rest/leadcapture/leadsmanagement/assign`, {
                    method: 'PATCH',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(lead),
                })
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.displaySusccessMessage('Lead assigned');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });

        },

        displaySusccessMessage(message) {
            this.message = message;
            this.alert_type = 'alert-success';
            this.alertIcon = 'uiIconSuccess';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
            this.editedItem = this.defaultItem;
        },
        displayErrorMessage(message) {
            this.isUpdating = false;

            this.message = message;
            this.alert_type = 'alert-error';
            this.alertIcon = 'uiIconError';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
        },
        backToList() {
            this.showDetails = false;
            this.showTable = true;
            let url = window.location.href;
            if (url.includes('?leadid=')) {
                url = url.split('?leadid=')[0];
                history.pushState({
                    id: 'leadsList'
                }, 'Leads List', url);
            }
        },

        getUrlParameterByName(name) {
            const url = window.location.href;
            name = name.replace(/[\[\]]/g, "\\$&")
            const regex = new RegExp("[?&]" + name + "(=([^&#]*)|&|#|$)")
            const results = regex.exec(url)
            if (!results) {return null}
            if (!results[2]) {return null}
            return decodeURIComponent(results[2].replace(/\+/g, " "))
        },    

        getLeads(all) {
            this.loading = true
            return new Promise((resolve, reject) => {
                const {
                    sortBy,
                    sortDesc,
                    page,
                    itemsPerPage
                } = this.options
                let sort = ""
                let desc = false
                let owner = ""
                let page_ = page
                let itemsPerPage_ = itemsPerPage
                if (all) {
                    page_ = 1
                    itemsPerPage_ = -1
                }
                if (this.selectedStatus === "All") {
                    this.selectedStatus = ""
                }
                if (this.selectedMethod === "All") {
                    this.selectedMethod = ""
                }
                if (this.myLeads) {
                    owner = this.context.currentUser
                } else {
                    owner = this.selectedOwner
                }
                if (sortBy.length > 0) {
                    sort = sortBy[0]
                    if (sort === "name") {
                        sort = "firstName"
                    }
                    if (sort === "formattedCreatedDate") {
                        sort = "createdDate"
                    }
                    if (sortDesc.length > 0) {
                        desc = sortDesc[0]
                    }
                }
                fetch(`/portal/rest/leadcapture/leadsmanagement/leads?search=${this.search}&status=${this.selectedStatus}&method=${this.selectedMethod}&owner=${owner}&notassigned=${this.notassigned}&from=${this.fromDate}&to=${this.toDate}&zone=${this.selectedGeoZone}&min=${this.userNumberMin}&max=${this.userNumberMax}&sortby=${sort}&sortdesc=${desc}&page=${page_}&limit=${itemsPerPage_}`, {
                        credentials: 'include',
                    })
                    .then((resp) => resp.json())
                    .then((resp) => {
                        //this.leadList = resp.
                        // this.allLeads = resp
                        this.showTable = true
                        this.showDetails = false
                        const items = resp.leads
                        const total = resp.size

                        this.loading = false
                        resolve({
                            items,
                            total,
                        })

                    })
            });

        },

        async exportData() {
            const response = await this.getLeads(true);
            console.log(response);
            return response.items;
        },
    },

};
</script>

<style>
.ConfWarning {
    border: 1px solid white;
    border-radius: 5px;
    padding: 8px;
    text-align: center;
    text-transform: uppercase;
    font-size: 16px;
}

#LeftNavigation {
    z-index: 1100;
}

.VuetifyApp .v-text-field input {
    padding: 0 !important;
}

.addBtn {
    align-items: center;
    color: white;
    display: flex;
    flex: 1 0 auto;
    justify-content: inherit;
    line-height: normal;
    position: relative;
}

.v-data-table__wrapper {
    padding-top: 30px;
}

.v-data-table-header {
    border-bottom: solid #d0d0d0;
}

.btn-export {
    border-style: solid !important;
    margin-left: 10px;
}


</style>
