<template>
<v-app class="VuetifyApp" color="transaprent" id="resourcesManagementApp">
    <main>
        <div :class="alert_type" class="alert" id v-if="alert">
            <i :class="alertIcon"></i>
            {{message}}
        </div>
        <v-flex>
            <v-data-table :headers="headers" :items="resources" sort-by="id" sort-desc class="elevation-1">
                <template v-slot:top>
                    <v-toolbar flat color="white">
                        <v-toolbar-title>Resources list</v-toolbar-title>
                        <v-divider class="mx-4" inset vertical></v-divider>
                        <v-spacer></v-spacer>
                        <v-dialog v-model="dialog" max-width="500px">
                            <template v-slot:activator="{ on }">
                                <v-btn color="primary" dark class="mb-2" v-on="on">New Resource</v-btn>
                            </template>
                            <v-card>
                                <v-card-title>
                                    <span class="headline">{{ formTitle }}</span>
                                </v-card-title>

                                <v-card-text>
                                    <v-container>
                                        <v-row>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-text-field v-model="editedItem.name" label="Resource name"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-select v-model="editedItem.type" :items="typeList" item-text="title" item-value="value" :rules="[rules.required]" label="Type" required></v-select>

                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-text-field v-model="editedItem.path" label="Path"></v-text-field>
                                            </v-col>
                                            <v-col cols="12" sm="6" md="12">
                                                <v-text-field v-model="editedItem.url" label="Download Url"></v-text-field>
                                            </v-col>
                                        </v-row>
                                    </v-container>
                                </v-card-text>

                                <v-card-actions>
                                    <v-spacer></v-spacer>
                                    <v-btn color="blue darken-1" text @click="close">Cancel</v-btn>
                                    <v-btn color="blue darken-1" text @click="save">Save</v-btn>
                                </v-card-actions>
                            </v-card>
                        </v-dialog>
                    </v-toolbar>
                </template>
                <template v-slot:item.type="{ item }"> {{ getTypeTitle(item)}}</template>
                <template v-slot:item.action="{ item }">
                    <v-icon small class="mr-2" @click="editItem(item)">
                        edit
                    </v-icon>
                    <v-icon small @click="deleteItem(item)">
                        delete
                    </v-icon>
                </template>
                <template v-slot:no-data>No Resources</template>
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
        resources: [{
            name: 'DGFLA',
            type: 'caseStudy',
            path: 'customer-case-study-dgfla',
            url: 'xxx/xxxx/customer-case-study-dgfla'
        }, {
            name: 'DGFLA',
            type: 'whitePaper',
            path: 'guide-employee-engagement-white-paper',
            url: 'xxx/xxxx/guide-employee-engagement-white-paper'
        }],
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
        },
        typeList: [{
                title: 'Case study',
                value: 'caseStudy'
            },
            {
                title: 'White paper',
                value: 'whitePaper'
            }
        ],
        dialog: false,
        headers: [{
                text: 'Ressouce Name',
                align: 'center',
                sortable: true,
                value: 'name',
            },
            {
                text: 'Type',
                align: 'center',
                sortable: true,
                value: 'type',
            },
            {
                text: 'Path',
                align: 'center',
                sortable: true,
                value: 'path',
            },
            {
                text: 'Download Url',
                align: 'center',
                sortable: true,
                value: 'url',
            },
            {
                text: 'Actions',
                align: 'center',
                sortable: true,
                value: 'action',
            },

        ],
        editedIndex: -1,
        editedItem: {
            name: '',
            type: '',
            path: '',
            url: '',
        },
        defaultItem: {
            name: '',
            type: '',
            path: '',
            url: '',
        },
    }),

    computed: {
        formTitle() {
            return this.editedIndex === -1 ? 'New Resource' : 'Edit Resource'
        },
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
            fetch(`/portal/rest/leadcapture/lcresources/resources`, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.resources = resp;
                });
        },

        cancel() {
            this.initialize()
        },

        editItem(item) {
            this.editedIndex = this.resources.indexOf(item)
            this.editedItem = Object.assign({}, item)
            this.dialog = true
        },

        deleteItem(item) {
            const index = this.resources.indexOf(item)
            //confirm('Are you sure you want to delete this item?') &&
            this.resources.splice(index, 1)
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
                Object.assign(this.resources[this.editedIndex], this.editedItem)
                this.update()
            } else {
                this.resources.push(this.editedItem)
                this.add()
            }
            this.close()
        },
        add() {
            fetch(`/portal/rest/leadcapture/lcresources/resources`, {
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
                    this.displaySusccessMessage('Resource added');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        update() {
            fetch(`/portal/rest/leadcapture/lcresources/resources`, {
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
                    this.displaySusccessMessage('Resource updated');
                })
                .catch((result) => {
                    this.initialize();
                    result.text().then((body) => {
                        this.displayErrorMessage(body);
                    });
                });
        },

        delete_(item) {
            fetch(`/portal/rest/leadcapture/lcresources/resources/` + item.id, {
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
                    this.displaySusccessMessage('Resource deleted');
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
           const type =  this.typeList.find(x => x.value === item.type)
           if(type!=null) {return type.title}
           return item.type.value
        }

    }
};
</script>

<style>
#resourcesManagementApp {
    overflow: hidden;
}

select {
    width: auto;
}

#resourcesManagementApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
}

#resourcesManagementApp .v-toolbar .v-input {
    margin-left: 18px;
}

#resourcesManagementApp .v-data-table {
 width: 100%;
}
</style>
