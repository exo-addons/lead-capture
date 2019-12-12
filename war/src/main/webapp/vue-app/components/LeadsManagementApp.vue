<template>
    <v-app class="VuetifyApp" color="transaprent" id="leadsManagementApp">
        <main>
            <div :class="alert_type" class="alert" id v-if="alert">
                <i :class="alertIcon"></i>
                {{message}}
            </div>
            <v-layout>
                <v-data-table
                        :headers="headers"
                        :items="leadList"
                        :search="search"
                        class="elevation-1"
                        sort-by="id"
                        v-show="showTable"
                >
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

                            <v-dialog max-width="290" v-model="confirmDialog">
                                <v-card>
                                    <v-card-title class="headline">Confirmation</v-card-title>

                                    <v-card-text>Are you sure to delete the Lead</v-card-text>

                                    <v-card-actions>
                                        <div class="flex-grow-1"></div>
                                        <div class="uiAction">
                                            <button @click="delete_()" class="btn btn-primary" type="button">Delete
                                            </button>
                                            <button @click="confirmDialog = false" class="btn" type="button">Cancel
                                            </button>
                                        </div>
                                    </v-card-actions>
                                </v-card>
                            </v-dialog>
                        </v-toolbar>
                    </template>
                    <template v-slot:item.name="{ item }">
                        <a @click="edit(item)">
                            <b>{{item.firstName}} {{item.lastName}}</b>
                        </a>
                    </template>
                    <template v-slot:item.assignee="{ item }">

                        <select v-model="item.assignee">
                            <option :value="null" disabled>Not assigned
                            </option>
                            <option :key="option.userName" v-bind:value="option" v-for="option in assignees">
                                {{option.fullName}}
                            </option>
                        </select>

                    </template>
                    <template v-slot:no-data>No Leads</template>
                </v-data-table>
            </v-layout>
            <lead-details :lead="selectedLead" v-on:backToList="backToList" v-show="showDetails"/>
        </main>
    </v-app>
</template>

<script>
import leadDetails from './LeadDetails.vue';
export default {
  components: {
    leadDetails,
  },
  data: () => ({
    notassigned:false,
    myLeads:false,
    currentUser:'test1',
    assignees:[
      {
        userName:'test1',
        fullName:'krout MedAmine'
      },{
        userName:'test2',
        fullName:'Patrice'
      },{
        userName:'test3',
        fullName:'Wassim'
      }
    ],
    showTable: true,
    showDetails: false,
    search: '',
    dialog: false,
    confirmDialog: false,
    itemToDelete: 0,
    alert: false,
    message: '',
    alert_type: '',
    alertIcon: '',
    alert_edit: false,
    message_edit: '',
    alert_type_add: '',
    zones:['MEA','LATAM','APAC','Western Eu','Eastern EU','US-CA'],
    
    leadList: [
       ],
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
    selectedLead: {},
  }),
  created() {
        this.initialize()
    },
  watch: {
    dialog(val) {
      return val === true || this.close() === true;
    },
  },
  computed:{
  headers () {
        return [
      {
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
               if (!this.notassigned && !this.myLeads) {return true}
               if (this.notassigned && this.myLeads) {return false}
              return (this.notassigned && !( value===null||value==={}|| typeof(value) === 'undefined')) || (this.myLeads && (value!==null && value!=={} && typeof(value) !== 'undefined') && value.userName===this.currentUser)  
           
      }
      },
      {
        text: 'Country',
        align: 'center',
        sortable: true,
        value: 'country',
      },
]}
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
    },

    edit(item) {
      this.selectedLead = item;
      this.showTable = false;
      this.showDetails = true;
    },
    backToList() {
      this.showDetails = false;
      this.showTable = true;
    },
    editItem(item) {
      fetch(`/portal/rest/gamification/connectors/github/hooksmanagement/hooks/` + item.id, {
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
          this.displaySusccessMessage('Webhook updated');
        })
        .catch((result) => {
          this.initialize();
          result.text().then((body) => {
            this.displayErrorMessage(body);
          });
        });
    },

    getHooks() {
      this.editedIndex = this.leadList.indexOf(item);
      this.editedItem = item;
      this.dialog = true;
    },

    deleteItem(item) {
      this.itemToDelete = item.id;
      this.confirmDialog = true;
    },

    delete_() {
      fetch(`/portal/rest/gamification/connectors/github/hooksmanagement/hooks/` + this.itemToDelete, {
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
          this.displaySusccessMessage('Webhook deleted');
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
      if (this.editedItem.organization === '' || this.editedItem.repo === '') {
        this.alert_type_add = 'alert-error';
        this.alert_edit = true;
        this.message_edit = 'All fields should be filled';

        setTimeout(() => (this.alert_edit = false), 5000);
      } else {
        const i = this.editedIndex;
        if (this.editedIndex > -1) {
          fetch(`/portal/rest/gamification/connectors/github/hooksmanagement/hooks/` + this.editedItem.id, {
            method: 'PUT',
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
              this.displaySusccessMessage('Webhook updated');
            })
            .catch((result) => {
              this.initialize();
              result.text().then((body) => {
                this.displayErrorMessage(body);
              });
            });
        } else {
          this.leadList.push(this.editedItem);

          fetch(`/portal/rest/gamification/connectors/github/hooksmanagement/hooks`, {
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
              this.displaySusccessMessage('Webhook created');
            })
            .catch((result) => {
              this.initialize();
              result.text().then((body) => {
                this.displayErrorMessage(body);
              });
            });
        }
        this.close();
      }
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
    #leadsManagementApp{
          overflow: hidden;
    }
    select {
    width: auto;
    }

    #leadsManagementApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
    }

    #leadsManagementApp .v-toolbar .v-input {
    margin-left: 18px;
    }


</style>
