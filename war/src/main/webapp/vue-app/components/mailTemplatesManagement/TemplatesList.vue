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
            <div>The lead capture feature should be configured</div>
        </v-btn>
        <div v-else class="ConfWarning">
            <v-icon x-large>mdi-alert</v-icon> <br>
            The lead capture feature is not configured, please contact your system administrator
        </div>

    </v-overlay>
    <v-data-table :headers="headers" :items="templatesList" :search="search" class="elevation-1" sort-by="id" sort-desc v-show="showTable">
        <template v-slot:top>
            <v-toolbar flat color="white">
                <v-toolbar-title>Mail templates list</v-toolbar-title>
                <v-divider class="mx-4" inset vertical></v-divider>
                <v-spacer></v-spacer>
                <v-btn color="primary" fab dark class="mb-2" @click="editItem(defaultItem)">
                    <v-icon>mdi-plus</v-icon>
                </v-btn>
            </v-toolbar>
        </template>
        <template v-slot:item.action="{ item }">
            <v-icon class="mr-2" @click="editItem(item)"> edit </v-icon>
            <v-icon @click="remove(item)"> delete </v-icon>
        </template>
        <template v-slot:no-data>No Mail Templates</template>
    </v-data-table>
    </v-layout>

    <v-dialog max-width="290" v-model="confirmDialog">
        <v-card>
            <v-card-title class="headline">Confirmation</v-card-title>

            <v-card-text>Are you sure to delete the template</v-card-text>

            <v-card-actions>
                <div class="flex-grow-1"></div>
                <div class="uiAction">
                    <button @click="remove()" class="btn btn-primary" type="button">Delete
                    </button>
                    <button @click="confirmDialog = false" class="btn" type="button">Cancel
                    </button>
                </div>
            </v-card-actions>
        </v-card>
    </v-dialog>
    <mail-details ref="templateDetails" :template="editedItem" v-on:backToList="backToList" v-on:update="edit" v-on:create="save" v-show="showDetails" />
</v-flex>
</template>

<script>
import mailDetails from './MailDetails.vue';
export default {
    components: {
        mailDetails,
    },
    data: () => ({
        showTable: true,
        showDetails: false,
        itemToDelete: 0,
        alert: false,
        message: '',
        alert_type: '',
        alertIcon: '',
        alert_edit: false,
        message_edit: '',
        alert_type_add: '',

        templatesList: [],
        editedIndex: -1,
        editedItem: {
            name: '',
            description: '',
            contents: [{
                language: 'en',
                subject: '',
                content: ''
            }]
        },
        context: {
            leadCaptureConfigured: true
        },
        defaultItem: {
            name: '',
            description: '',
            contents: [{
                language: 'en',
                subject: '',
                content: ''
            }, {
                language: 'fr',
                subject: '',
                content: ''
            }]
        },
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
    },

    computed: {
        headers() {
            return [{
                    text: 'Template Name',
                    align: 'center',
                    sortable: true,
                    value: 'name',
                },
                {
                    text: 'Description',
                    align: 'center',
                    sortable: true,
                    value: 'description',
                },
                {
                    text: 'Event',
                    align: 'center',
                    sortable: true,
                    value: 'event',
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
    methods: {
        initialize() {
            fetch(`/portal/rest/leadcapture/mailtemplatesmanagement/templates`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.templatesList = resp;
                });
        },

        editItem(item) {
            this.editedItem = item
            const lang = item.contents[0].language
            this.$refs.templateDetails.initializeContent(item.contents[0])
            this.showTable = false;
            this.showDetails = true;
        },
        backToList() {
            this.initialize();
            this.showDetails = false;
            this.showTable = true;
        },
        edit(item) {
            fetch(`/portal/rest/leadcapture/mailtemplatesmanagement/templates`, {
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
                    this.displaySusccessMessage('template updated');
                    this.backToList()
                })
                .catch((result) => {
                    // this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        deleteTemplate() {
            this.confirmDialog = true;
        },
        remove(item) {
            this.delete_(item);
            this.confirmDialog = false;
        },

        delete_(item) {
            fetch(`/portal/rest/leadcapture/mailtemplatesmanagement/templates/` + item.id, {
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

        save(item) {

            this.templatesList.push(item);

            fetch(`/portal/rest/leadcapture/mailtemplatesmanagement/templates`, {
                    method: 'post',
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
</style>
