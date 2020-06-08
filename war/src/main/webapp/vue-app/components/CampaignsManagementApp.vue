<template>
<v-app class="VuetifyApp" color="transaprent" id="compaignsManagementApp">
    <main>
        <div :class="alert_type" class="alert" id v-if="alert">
            <i :class="alertIcon"></i>
            {{message}}
        </div>
        <v-flex>
            <v-data-table :headers="headers" :items="compaigns" sort-by="id" sort-desc class="elevation-1">
                <template v-slot:top>
                    <v-toolbar flat color="white">
                        <v-toolbar-title>{{$t('exoplatform.LeadCapture.compaignsManagement.compaignsList','compaigns list')}}</v-toolbar-title>
                        <v-divider class="mx-4" inset vertical></v-divider>
                        <v-spacer></v-spacer>
                        <v-dialog v-model="dialog" max-width="500px">
                            <template v-slot:activator="{ on }">
                                <v-btn color="primary" dark class="mb-2" v-on="on">{{$t('exoplatform.LeadCapture.compaignsManagement.newcompaign','New compaign')}}</v-btn>
                            </template>
                            <v-card>
                                <v-card-title>
                                    <span class="headline">{{ formTitle }}</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-text-field v-model="editedItem.name" :label="$t('exoplatform.LeadCapture.compaignsManagement.compaignName','Compaign name')"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-select v-model="editedItem.field" :items="fieldList" item-text="title" item-value="value" :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.compaignsManagement.type','Type')" required></v-select>

                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-select v-model="editedItem.value" :items="statusList" item-text="text" item-value="value" :label="$t('exoplatform.LeadCapture.compaignsManagement.value','Value')"></v-select>

                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-switch v-model="editedItem.enabled" :label="$t('exoplatform.LeadCapture.compaignsManagement.enabled','enabled')"></v-switch>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-text-field v-model="editedItem.after" :label="$t('exoplatform.LeadCapture.compaignsManagement.after','after')"></v-text-field>
                                            </v-col>
                                             <v-col cols="12" sm="6" md="12">
                                                <v-select v-model="editedItem.mailTemplateDTO" :items="templatesList" item-text="name" item-value="id" return-object :rules="[rules.required]" :label="$t('exoplatform.LeadCapture.compaignsManagement.template','Template')" required></v-select>

                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue darken-1" text @click="close">{{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}</v-btn>
                                    <v-btn color="blue darken-1" text @click="save">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-toolbar>
                </template>
                <template v-slot:item.enabled="{ item }">
                        <v-switch v-model="item.enabled" disabled ></v-switch>
                    </template>
                <template v-slot:item.action="{ item }">
                     <v-icon small class="mr-2" @click="editItem(item)">
                        edit
                    </v-icon>
                    <v-icon small @click="deleteItem(item)">
                        delete
                    </v-icon>
                </template>
                <template v-slot:no-data>{{$t('exoplatform.LeadCapture.compaignsManagement.nocompaigns','No compaigns')}}</template>
            </v-data-table>
        </v-flex>
    </main>
</v-app>
</template>

<script>
export default {
    data: () => ({
        alert: false,
        message: '',
        alert_type: '',
        alertIcon: '',
        valid: true,
        compaigns: [],
        templatesList: [],
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
        },
        fieldList: [{
                title: 'Status',
                value: 'status'
            }
        ],
        dialog: false,

        editedIndex: -1,
        editedItem: {
            name: '',
            field: '',
            value: '',
            enabled: true,
            after: 0,
            mailTemplateDTO: {
                id:'',
                name: '',
            },
        },
        defaultItem: {
            name: '',
            field: '',
            value: '',
            enabled: false,
            after: 0,
            mailTemplateDTO: {
                id:'',
                name: '',
            },
        },
             
    }),

    computed: {
        formTitle() {
            return this.editedIndex === -1 ? 'New compaign' : 'Edit compaign'
        },
           statusList() {
            return [{
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
            headers() {
            return [{
                text: this.$t(`exoplatform.LeadCapture.compaignsManagement.compaignName`,''),
                align: 'center',
                sortable: true,
                value: 'name',
            },
            {
                text: this.$t(`exoplatform.LeadCapture.compaignsManagement.field`,''),
                align: 'center',
                sortable: true,
                value: 'field',
            },
            {
                text: this.$t(`exoplatform.LeadCapture.compaignsManagement.value`,''),
                align: 'center',
                sortable: true,
                value: 'value',
            },{
                text: this.$t(`exoplatform.LeadCapture.compaignsManagement.enabled`,''),
                align: 'center',
                sortable: true,
                value: 'enabled',
            },
            {
                text: this.$t(`exoplatform.LeadCapture.compaignsManagement.template`,''),
                align: 'center',
                sortable: true,
                value: 'mailTemplateDTO.name',
            },
            {
                text: 'Actions',
                align: 'center',
                sortable: true,
                value: 'action',
            },
            ]

        }
    },

    watch: {
        dialog(val) {
            return val || this.close();
        },
    },

    created() {
        this.initialize()
    },
    methods: {

        initialize() {
            fetch(`/portal/rest/leadcapture/lccompaigns/compaigns`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.compaigns = resp;
                });

            fetch(`/portal/rest/leadcapture/mailtemplatesmanagement/templates`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.templatesList = resp;
                });    
        },

        cancel() {
            this.initialize()
        },

        editItem(item) {
            this.editedIndex = this.compaigns.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem(item) {
            const index = this.compaigns.indexOf(item)
            //confirm('Are you sure you want to delete this item?') &&
            this.compaigns.splice(index, 1)
            this.delete_(item)
        },

        close() {
            this.dialog = false
            setTimeout(() => {
                this.editedItem = Object.assign({}, this.defaultItem)
                this.editedIndex = -1
            }, 300)
        },

        save() {
            if (this.editedIndex > -1) {
                Object.assign(this.compaigns[this.editedIndex], this.editedItem)
                this.update()
            } else {
                this.compaigns.push(this.editedItem)
                this.add()
            }
            this.close()
        },
        add() {
            fetch(`/portal/rest/leadcapture/lccompaigns/compaigns`, {
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
                    this.displaySusccessMessage('compaign added');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        update() {
            fetch(`/portal/rest/leadcapture/lccompaigns/compaigns`, {
                    method: 'put',
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
                    this.displaySusccessMessage('compaign updated');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        delete_(item) {
            fetch(`/portal/rest/leadcapture/lccompaigns/compaigns/` + item.id, {
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
                    this.displaySusccessMessage('compaign deleted');
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

        displaySusccessMessage(message) {
            this.message = message;
            this.alert_type = 'alert-success';
            this.alertIcon = 'uiIconSuccess';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
        },
        displayErrorMessage(message) {
            this.isUpdating = false;
            this.message = message;
            this.alert_type = 'alert-error';
            this.alertIcon = 'uiIconError';
            this.alert = true;
            setTimeout(() => (this.alert = false), 5000);
        },
        getTypeTitle(item) {
           const type =  this.fieldList.find(x => x.value === item.type)
           if(type!=null) {return type.title}
           return item.type.value
        }

    }
};
</script>

<style>
#compaignsManagementApp {
    overflow: hidden;
    padding: 10px 20px;
}

select {
    width: auto;
}

#compaignsManagementApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
}

#compaignsManagementApp .v-toolbar .v-input {
    margin-left: 18px;
}

#compaignsManagementApp .v-data-table {
 width: 100%;
}
</style>
