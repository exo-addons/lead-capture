<template>
<v-flex>
    <div :class="alert_type" class="alert" id v-if="alert">
        <i :class="alertIcon"></i>
        {{message}}
    </div>
    <v-layout>
        <v-data-table :headers="headers" :items="leadList" :search="search" class="elevation-1" sort-by="id" v-show="showTable">
            <template v-slot:top>
                <v-toolbar color="white" flat>
                    <div class="flex-grow-1"></div>
                    <v-col cols="8" md="2" sm="4">
                        <v-select :items="zones" label="Geo Zone"></v-select>
                    </v-col>
                    <v-divider class="mx-4" inset vertical></v-divider>
                    <v-switch class="mt-2" label="Only unassigned" v-model="notassigned"></v-switch>
                    <v-switch class="mt-2" label="My Leads" v-model="myLeads"></v-switch>
                    <v-divider class="mx-4" inset vertical></v-divider>
                    <v-col cols="12" md="3" sm="6">
                        <v-text-field append-icon="search" label="" v-model="search"></v-text-field>
                    </v-col>
                    <v-dialog v-model="dialog" max-width="500px">
                        <template v-slot:activator="{ on }">
                            <v-btn color="primary" fab dark class="mb-2" v-on="on">
                                <v-icon>mdi-plus</v-icon>
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
                                                <v-text-field :rules="[rules.required]" v-model="editedItem.firstName" label="First name"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required]" v-model="editedItem.lastName" label="Last name"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field :rules="[rules.required, rules.valideMail]" v-model="editedItem.mail" label="Mail"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.company" label="Company"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.position" label="Position"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="4">
                                                <v-text-field v-model="editedItem.country" label="Country"></v-text-field>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
                                    <v-btn :disabled="!valid" color="blue darken-1" text @click="save">Save</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-form>
                    </v-dialog>
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
            <template v-slot:item.assignee="{ item }">
                <!--                                 <v-select :items="assignees" item-text="fullName" item-value="userName" label="" :append-icon="''" dense solo></v-select>
 --> <select v-model="item.assignee" @change="onAssign(item)">
                    <option :value="null" disabled>Not assigned
                    </option>
                    <option :key="option.userName" v-bind:value="option.userName" v-for="option in assignees">
                        {{option.fullName}}
                    </option>
                </select>

            </template>
            <template v-slot:no-data>No Leads</template>
        </v-data-table>
    </v-layout>
    <lead-details :lead="selectedLead" :formResponses="formResponses" v-on:backToList="backToList" v-on:remove="delete_" v-on:changeStatus="changeStatus" v-on:save="editItem" v-show="showDetails" />
</v-flex>
</template>

<script>
import leadDetails from './LeadDetails.vue';
export default {
    components: {
        leadDetails,
    },
    data: () => ({
        valid: true,
        notassigned: false,
        myLeads: false,
        currentUser: 'test1',
        assignees: [],
        showTable: true,
        showDetails: false,
        search: '',
        dialog: false,

        itemToDelete: 0,
        alert: false,
        message: '',
        alert_type: '',
        alertIcon: '',
        alert_edit: false,
        message_edit: '',
        alert_type_add: '',
        zones: ['MEA', 'LATAM', 'APAC', 'Western Eu', 'Eastern EU', 'US-CA'],

        leadList: [],
        editedIndex: -1,
        editedItem: {
            name: '',
            mail: '',
            company: '',
            position: '',
            country: '',
            status: '',
        },
        defaultItem: {
            name: '',
            mail: '',
            company: '',
            position: '',
            country: '',
            status: '',
        },
        formResponses: [],
        selectedLead: {},
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
            valideMail: value => /.+@.+/.test(value) || 'E-mail must be valid',
        }
    }),
    created() {
        this.initialize()
    },
    watch: {
        dialog(val) {
            return val === true || this.close() === true;
        },
    },
    computed: {
        headers() {
            return [{
                    text: 'Full Name',
                    align: 'center',
                    sortable: true,
                    value: 'name',
                },
                {
                    text: 'E mail',
                    align: 'center',
                    sortable: true,
                    value: 'mail',
                },
                {
                    text: 'Company',
                    align: 'center',
                    sortable: true,
                    value: 'company',
                },
                {
                    text: 'Position',
                    align: 'center',
                    sortable: true,
                    value: 'position',
                },
                {
                    text: 'Assignee',
                    align: 'center',
                    sortable: true,
                    value: 'assignee',
                    filter: value => {
                        console.log(value)
                        if (!this.notassigned && !this.myLeads) {
                            return true
                        }
                        if (this.notassigned && this.myLeads) {
                            return false
                        }
                        return (this.notassigned && !(value === null || value === {} || typeof (value) === 'undefined')) || (this.myLeads && (value !== null && value !== {} && typeof (value) !== 'undefined') && value.userName === this.currentUser)

                    }
                },
                {
                    text: 'Country',
                    align: 'center',
                    sortable: true,
                    value: 'country',
                },
                {
                    text: 'Status',
                    align: 'center',
                    sortable: true,
                    value: 'status',
                },
            ]
        }
    },
    methods: {
        initialize() {
            fetch(`/portal/rest/leadcapture/leadsmanagement/leads`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.leadList = resp;
                });

            fetch(`/portal/rest/leadcapture/leadsmanagement/marketers`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.assignees = resp;
                });
        },

        edit(item) {
            this.selectedLead = item
            const telemarketer = this.assignees.find(x => x.userName === item.assignee)
            this.selectedLead.telemarketerFullName = telemarketer.fullName
            this.selectedLead.telemarketerMail = telemarketer.mail
            this.showTable = false;
            this.showDetails = true;
            fetch(`/portal/rest/leadcapture/leadsmanagement/responses/` + item.id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.formResponses = resp;
                });
        },
        backToList() {
            this.showDetails = false;
            this.showTable = true;
        },
        editItem(item) {
            fetch(`portal/rest/leadcapture/leadsmanagement/responses/` + item.id, {
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
            const newLead = {
                "lead": this.editedItem
            }

            fetch(`/portal/rest/leadcapture/leadsmanagement/leads`, {
                    method: 'post',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(newLead),
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
                .then((result) => {
                    if (!result.ok) {
                        throw result;
                    }
                })
                .then((response) => {
                    this.displaySusccessMessage('Lead status updated');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });

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
    },
};
</script>
