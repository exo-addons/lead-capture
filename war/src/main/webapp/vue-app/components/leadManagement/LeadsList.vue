<template>
<v-flex>
    
    <div :class="alert_type" class="alert" id v-if="alert">
        <i :class="alertIcon"></i>
        {{message}}
    </div>
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
        <v-data-table :headers="headers" :items="leadList" :options.sync="options" :server-items-length="totalLeads" :loading="loading" class="elevation-1" v-show="showTable">
            <template v-slot:top>
                <v-toolbar color="white" flat>
                    <v-dialog v-model="dialog" max-width="500px">
                        <template v-slot:activator="{ on }">
                            <v-btn x-large color="green lighten-1"  v-on="on">
                                <v-icon left>mdi-plus</v-icon>
                                {{$t('exoplatform.LeadCapture.leadManagement.addLead',"Add Lead")}} 
                            </v-btn>
                        </template>
                        <v-form ref="form" v-model="valid">
                            <v-card>
                                <v-card-title>
                                    <span class="headline">{{ formTitle }}</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required]" v-model="editedItem.firstName" :label="$t('exoplatform.LeadCapture.leadManagement.firstName','First name')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required]" v-model="editedItem.lastName" :label="$t('exoplatform.LeadCapture.leadManagement.lastName','Last name')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required, rules.valideMail]" v-model="editedItem.mail" :label="$t('exoplatform.LeadCapture.leadManagement.mail','Mail')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.company" :label="$t('exoplatform.LeadCapture.leadManagement.company','Company')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.phone" :label="$t('exoplatform.LeadCapture.leadManagement.phone','Phone')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.position" :label="$t('exoplatform.LeadCapture.leadManagement.position','Position')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.inferredCountry" :label="$t('exoplatform.LeadCapture.leadManagement.country','Country')"></v-text-field>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue darken-1" text @click="close">{{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}</v-btn>
                                    <v-btn :disabled="!valid" color="blue darken-1" text @click="save">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-form>
                    </v-dialog>
                   <v-spacer />
                    <v-col cols="12" md="3" sm="6">
                        <v-text-field append-icon="search" single-line label="" v-model="search"></v-text-field>
                    </v-col>
                    <a class="actionIcon" title="" rel="tooltip"  @click="filterDrawer=true"><i class="uiIcon uiIconFilter" :class="filtered"></i></a>
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
    <v-navigation-drawer absolute floating right temporary v-model="filterDrawer" width="30%">
        <filter-drawer :assigneesFilter="assigneesFilter" v-on:addFilter="addFilter" v-on:toggleFilterDrawer="toggleFilterDrawer" />
    </v-navigation-drawer>
    <lead-details :lead="selectedLead" :formResponses="formResponses" :comments="comments" :tasks="tasks" :context="context" v-on:backToList="backToList" v-on:remove="delete_" v-on:changeStatus="changeStatus" v-on:saveLead="editItem" v-show="showDetails" />

</v-flex>
</template>

<script>
import leadDetails from './LeadDetails.vue';
import filterDrawer from './FilterDrawer.vue';
export default {
    components: {
        leadDetails,
        filterDrawer
    },

    data: () => ({
filterDrawer:null,
filtered:"notFiltered",
        totalLeads: 0,
        loading: true,
        options: {},
        statusList: ['Raw', 'Open', 'Attempted', 'Contacted', 'Qualified', 'Recycled', 'Accepted', 'Bad_Data', 'Duplicate'],
        selectedStatus: "",
        selectedMethod: "",
        selectedOwner:"",
        valid: true,
        notassigned: false,
        myLeads: false,
        currentUser: 'test1',
        assignees: [],
        assigneesFilter: [{"fullName":"All","userName":"","email":""}],
        showTable: false,
        showDetails: false,
        search: '',
        awaitingSearch:false,
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
        editedItem: {
            name: '',
            mail: '',
            company: '',
            position: '',
            inferredCountry: '',
            status: '',
        },
        defaultItem: {
            name: '',
            mail: '',
            company: '',
            position: '',
            inferredCountry: '',
            status: '',
        },
        context: {
            leadCaptureConfigured: true
        },
        formResponses: [],
        comments: [],
        tasks: [],
        selectedLead: {},
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
            valideMail: value => /.+@.+/.test(value) || 'E-mail must be valid',
        }
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
                    text: this.$t(`exoplatform.LeadCapture.leadManagement.status`, ""),
                    align: 'center',
                    sortable: true,
                    value: 'status',
                },
            ]
        }
    },
    methods: {
        addFilter(val){
            console.log(val)
        this.selectedStatus= val.selectedStatus
        this.selectedMethod= val.selectedMethod
        this.selectedOwner= val.selectedOwner
        this.notassigned= val.notassigned
        this.myLeads= val.myLeads
            this.getLeads().then(data => {
                this.leadList = data.items
                this.totalLeads = data.total
            })  
            this.filtered=this.getFilterClass()
             this.filterDrawer = !this.filterDrawer;
        },
        toggleFilterDrawer() {
            this.filterDrawer = !this.filterDrawer;
        },

        getFilterClass(){
if(this.selectedStatus!=="" || this.selectedMethod!=="" || this.selectedOwner!=="" || this.notassigned || this.myLeads)
{
    return "uiIconBlue"
}
return "notFiltered"
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
        /*         getLeads() {
                    fetch(`/portal/rest/leadcapture/leadsmanagement/leads`, {
                            credentials: 'include',
                        })
                        .then((resp) => resp.json())
                        .then((resp) => {
                            this.leadList = resp
                            this.allLeads = resp
                            this.showTable = true
                            this.showDetails = false
                        });
        return this.leadList
                } ,*/

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

            fetch(`/portal/rest/leadcapture/leadsmanagement/task/` + item.id, {
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
                        this.comments = resp;
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

        save() {

            this.leadList.push(this.editedItem);
            this.allLeads.push(this.editedItem);
            fetch(`/portal/rest/leadcapture/leadsmanagement/create`, {
                    method: 'post',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(this.editedItem),
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
        getLeads() {
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
                fetch(`/portal/rest/leadcapture/leadsmanagement/leads?search=${this.search}&status=${this.selectedStatus}&method=${this.selectedMethod}&owner=${owner}&notassigned=${this.notassigned}&sortby=${sort}&sortdesc=${desc}&page=${page}&limit=${itemsPerPage}`, {
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

    .addBtn{
    align-items: center;
    color: white;
    display: flex;
    flex: 1 0 auto;
    justify-content: inherit;
    line-height: normal;
    position: relative;
    }
    .notFiltered{
    color: #9a9b9c;
    }
    .v-data-table__wrapper{
        padding-top: 30px;
    }
    .v-data-table-header{
        border-top: solid 1px #d0d0d0;
        border-bottom: solid #d0d0d0;
    }
</style>
