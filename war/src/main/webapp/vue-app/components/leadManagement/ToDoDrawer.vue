<template>
<exo-drawer ref="todoDrawer" right class="">
    <template slot="title">
        {{$t('exoplatform.LeadCapture.leadManagement.toDoList','Todo list')}}
    </template>
    <template slot="titleIcons">
<v-switch v-model="showAll" :label="$t('exoplatform.LeadCapture.status.All','All')"></v-switch>
                <v-btn text small @click="addNote()">
                    <v-icon>mdi-plus</v-icon>
                    {{$t('exoplatform.LeadCapture.leadManagement.addItem','Add Item')}}
                </v-btn>
                <v-btn small icon @click="navigateTo(`tasks`)">
                     <v-icon>mdi-open-in-new</v-icon>
                </v-btn>

    </template>
    <template slot="content">
        <div>
          <v-card v-show="dialog">
            <v-menu ref="datePicker" v-model="datePicker" :close-on-content-click="false" :return-value.sync="date" transition="scale-transition" offset-y min-width="290px">
                <template v-slot:activator="{ on }">
                    <v-text-field v-model="date" :label="$t('exoplatform.LeadCapture.dueDate','Due Date')" prepend-icon="event" readonly v-on="on"></v-text-field>
                </template>
                <v-date-picker v-model="date" no-title scrollable>
                    <v-spacer></v-spacer>
                    <v-btn text color="primary" @click="datePicker = false">{{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}</v-btn>
                    <v-btn text color="primary" @click="$refs.datePicker.save(date)">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}</v-btn>
                </v-date-picker>
            </v-menu>
            <v-card-text>
                <v-text-field v-model="title" :label="$t('exoplatform.LeadCapture.title','Title')"></v-text-field>
            </v-card-text>
            <v-card-actions>
                <v-spacer></v-spacer>
                <v-btn @click="close()" color="blue darken-1" text>{{$t('exoplatform.LeadCapture.leadManagement.close','Close')}}</v-btn>
                <v-btn @click="saveTask()" color="blue darken-1" text>{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}</v-btn>
            </v-card-actions>
        </v-card>
        <v-list>
            <v-list-item :key="item.title" v-for="item in tasksList" class="taskItem">
                <v-list-item-action>
                    <v-btn icon>
                        <v-icon v-if="item.completed" color="green lighten-1" large>mdi-check-circle</v-icon>
                        <v-icon v-else color="grey lighten-1" large @click="completeTask(item)">mdi-check-circle</v-icon>
                    </v-btn>

                </v-list-item-action>
                <v-list-item-content>
                    <v-list-item-title v-text="item.title"></v-list-item-title>
                    <v-list-item-subtitle v-text="item.formattedDueDate"></v-list-item-subtitle>
                </v-list-item-content>
            </v-list-item>
        </v-list>
        </div>
    </template>

</exo-drawer>
</template>

<script>
export default {
    props: ['lead', 'tasks'],
    data: () => ({
        date: new Date().toISOString().substr(0, 10),
        dialog: false,
        title: "",
        datePicker: false,
        newItem: {},
        showAll: false,

    }),
    computed: {
        tasksList() {
            if (this.showAll) {
                return this.tasks
            }
            return this.tasks.filter(item => {
                return item.completed !== true
            })

        }
    },
    watch: {
        showAll: function (val) {
            if (!val) {
                this.tasksList = this.tasks.filter(item => {
                    return item.completed === true
                })
            } else {
                this.tasksList = this.tasks
            }
        },
    },
    methods: {
        navigateTo(pagelink) {
            window.open(`${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/${ pagelink }`, '_blank');
        },

        toggleDrawer() {
            this.$emit('toggleToDoDrawer');
        },
        addNote() {
            this.dialog = true
        },
         cancel() {
            this.$refs.todoDrawer.close()
        },
        open() {
            this.$refs.todoDrawer.open()
            
        },
        close() {
            this.newNote = {}
            this.dialog = false
            this.$refs.todoDrawer.close()
        },
        saveTask() {
            this.newItem.lead = this.lead
            this.newItem.formattedDueDate = this.date
            this.newItem.title = this.title

            fetch(`/portal/rest/leadcapture/leadsmanagement/ptask`, {
                    method: 'post',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(this.newItem),
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.tasks.unshift(resp);
                });
            this.title = ""
            this.dialog = false

        },
        completeTask(item) {
            item.completed = true

            fetch(`/portal/rest/leadcapture/leadsmanagement/ptask/` + item.id, {
                    method: 'put',
                    credentials: 'include',
                    headers: {
                        'Content-Type': 'application/json',
                    },
                    body: JSON.stringify(item),
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    //this.comments.unshift(resp);
                });
        }
    }
}
</script>

<style>
.taskItem {
    background-color: #f5f5f5;
    margin-bottom: 5px;
}

.commentItem {
    width: 100%;
}

.avatarXSmall {
    max-height: 36px;
    max-width: 36px;
}

.commmentLeft+.commentRight {
    margin-left: 50px;
}

.date {
    margin-left: 10px;
    font-size: 12px;
    padding-right: 16px;
    float: right;
}

.comment {
    width: 100%;
}
</style>
