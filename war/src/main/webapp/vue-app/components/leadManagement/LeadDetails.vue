<template>
<v-card elevation="0" class="leadDetails">
    <v-card-text>

        <v-row>
            <v-col cols="12" sm="12" md="3">
                <v-card elevation="0">
                    <v-row>
                        <v-col cols="12" sm="9" md="9">
                            <v-btn text @click="backToList()">
                                <v-icon dark>mdi-arrow-left</v-icon>
                                {{$t('exoplatform.LeadCapture.leadManagement.BackToList','Back to lead list')}}
                            </v-btn>
                        </v-col>
                        <v-col cols="12" sm="1" md="1">
                            <a v-if="context.isManager" rel="tooltip" data-placement="bottom" @click="deleteLead()">
                                <i class="uiIconDelete delete_btn"></i></a>
                        </v-col>
                    </v-row>
                    <v-card-text>
                        <div class="text-center avatar-field">
                            <v-avatar class="avatar-pl" size="130">
                                <img :src="leadAvatar">
                            </v-avatar>
                        </div>
                        <v-card elevation="0">
                            <v-card-text>
                                <v-container>

                                    <v-row>

                                        <v-col cols="12" sm="10" md="10">
                                            <h3>{{lead.firstName}} {{lead.lastName}}</h3>
                                        </v-col>
                                        <v-col cols="12" sm="2" md="2">
                                            <a @click="openEditDrawer" rel="tooltip" data-placement="bottom"><i class="uiIconEdit edit_btn"></i></a>
                                        </v-col>
                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t(`exoplatform.LeadCapture.leadManagement.owner`, "Owner")}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">
                                            <v-menu offset-y close-on-click=true>
                                                <template v-slot:activator="{ on }">
                                                    <div v-on="on" class="cl-field">
                                                        <v-avatar size="40"><img :src="ownerAvatar"></v-avatar> {{lead.telemarketerFullName}}
                                                    </div>
                                                </template>
                                                <v-list>
                                                    <v-list-item v-for="(item, index) in assignees" :key="index" @click="changeAssignee(item)">
                                                        <v-list-item-title>
                                                            <v-avatar size="24"><img :src="`/rest/v1/social/users/${item.userName}/avatar`"></v-avatar> {{ item.fullName }}
                                                        </v-list-item-title>
                                                    </v-list-item>
                                                </v-list>
                                            </v-menu>
                                        </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.mail','Mail')}}:</strong>
                                        </v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.mail}} </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.company','Company')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.company}} </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.position','Position')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.position}} </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.country','Country')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.inferredCountry}} </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.phone','Phone')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.phone}} </v-col>

                                    </v-row>

                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>Lead ID:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7" class="cl-field" @click="navigateTo(`activity?id=${lead.activityId}`)"> {{lead.id}} </v-col>
                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.language','Language')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">{{lead.language}}</v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.creationDate','Creation Date')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7"> {{lead.formattedCreatedDate}} </v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.updateDate','Update Date')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">{{lead.formattedUpdatedDate}}</v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.geographiqueZone','Geographique Zone')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">{{lead.geographiqueZone}}</v-col>

                                    </v-row>
                                    <v-row>

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.marketingSuspended','Marketing Suspended')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">
                                            <v-switch disabled v-model="lead.marketingSuspended"></v-switch>
                                        </v-col>

                                    </v-row>
                                    <v-row v-if="lead.marketingSuspended==true">

                                        <v-col cols="12" sm="6" md="5"><strong>{{$t('exoplatform.LeadCapture.leadManagement.marketingSuspendedCause','Marketing Suspended Cause')}}:</strong></v-col>
                                        <v-col cols="12" sm="6" md="7">{{lead.marketingSuspendedCause}}</v-col>

                                    </v-row>

                                </v-container>
                            </v-card-text>

                            <div style="flex: 1 1 auto;"></div>

                        </v-card>

                    </v-card-text>

                    <div style="flex: 1 1 auto;"></div>

                </v-card>
            </v-col>

            <v-col cols="12" sm="12" md="9">

                <template>
                    <v-toolbar class="leadToolBar" flat>

                        <v-toolbar-title>

                        </v-toolbar-title>

                        <v-spacer></v-spacer>
                        <a class="caption  headerBtn" @click.stop="toDodrawer = !toDodrawer" v-on="on">
                            <v-badge :value="badge > 0" :content="badge" flat color="red" left overlap>
                                <v-icon left>mdi-clipboard-text</v-icon>
                            </v-badge> {{$t('exoplatform.LeadCapture.leadManagement.todos','Todos')}}
                        </a>
                        <!-- <a class="caption  headerBtn" @click.stop="openTaskDrawer" v-on="on"> -->
                        <a v-if="lead.taskId!=null && lead.taskId!=0" class="caption  headerBtn" @click.stop="drawer = !drawer" v-on="on">
                            <v-icon left>mdi-camera-iris</v-icon> {{$t(`exoplatform.LeadCapture.status.${lead.status}`,lead.status)}}
                        </a>
                        <v-menu v-else offset-y>
                            <template v-slot:activator="{ on }">
                                <a class="caption  headerBtn" v-on="on">
                                    <v-icon left>mdi-camera-iris</v-icon> {{$t(`exoplatform.LeadCapture.status.${lead.status}`,lead.status)}}
                                </a>
                            </template>
                            <v-list>
                                <v-list-item v-for="(item, index) in statusList" :key="index" @click="changeStatus(item)">
                                    <v-list-item-title>{{ item.text }}</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>
                    </v-toolbar>
                </template>
                <v-dialog max-width="290" v-model="confirmDialog">
                    <v-card>
                        <v-card-title class="headline">Confirmation</v-card-title>

                        <v-card-text>{{$t('exoplatform.LeadCapture.leadManagement.deleteWarning','Are you sure to delete the Lead')}}</v-card-text>

                        <v-card-actions>
                            <div class="flex-grow-1"></div>
                            <div class="uiAction">
                                <button @click="remove()" class="btn btn-primary" type="button">{{$t('exoplatform.LeadCapture.leadManagement.delete','Delete')}}
                                </button>
                                <button @click="confirmDialog = false" class="btn" type="button">{{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}
                                </button>
                            </div>
                        </v-card-actions>
                    </v-card>
                </v-dialog>
                <template>

                    <v-tabs class="tabContainer" grow v-model="selectedTab">
                        <v-tabs-slider color="primary" />
                        <v-tab href="#timeline" key="timeline">{{$t('exoplatform.LeadCapture.leadManagement.timeline','Timeline')}}</v-tab>
                        <v-tab href="#projectInfo" key="projectInfo">{{$t('exoplatform.LeadCapture.leadManagement.projectInfo','Project Info')}}</v-tab>
                        <v-tab href="#captureInfo" key="captureInfo">{{$t('exoplatform.LeadCapture.leadManagement.captureInfo','Capture Info')}}</v-tab>
                    </v-tabs>

                    <v-tabs-items class="infoContent" v-model="selectedTab">
                        <v-tab-item class="tabContent" eager id="timeline" value="timeline">
                            <v-timeline dense>
                                <v-timeline-item v-for="formResponse in timeline" :key="formResponse.id" :icon="getIcon(formResponse.form)" right dense>
                                    <v-card class="elevation-2">
                                        <v-card-title>
                                            <div v-if="isTask(formResponse.form)" class="headline">{{formResponse.authorName}} {{$t('exoplatform.LeadCapture.task.statusChanged','changed lead status to ')}} {{formResponse.newStatus}}</div>
                                            <div v-else class="headline">{{$t(`exoplatform.LeadCapture.leadManagement.${formResponse.form}`,formResponse.form)}}</div>
                                            <v-spacer></v-spacer>
                                            <div class="response_date">{{formResponse.createdDate}}</div>
                                        </v-card-title>
                                        <v-card-text v-if="formResponse.fields.length>0">
                                            <v-row class="fields-text" v-for="field in formResponse.fields" :key="field">

                                                <v-col v-if="field!=='createdDate'" cols="12" sm="4" md="3"><strong>{{$t(`exoplatform.LeadCapture.leadManagement.${field}`,field)}}:</strong>
                                                </v-col>
                                                <v-col v-if="field!=='createdDate'" cols="12" sm="8" md="9"> {{formResponse[field]}} </v-col>

                                            </v-row>
                                        </v-card-text>
                                    </v-card>
                                </v-timeline-item>
                            </v-timeline>

                        </v-tab-item>

                        <v-tab-item class="tabContent" eager id="projectInfo" value="projectInfo">
                            <v-form>
                                <v-card elevation="0">
                                    <v-card-text>

                                        <v-container>

                                            <v-row>
                                                <v-col cols="12" sm="12" md="12">
                                                    <v-text-field v-model="lead.goal" :label="$t('exoplatform.LeadCapture.leadManagement.goal','Goal')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.usersNumber" :label="$t('exoplatform.LeadCapture.leadManagement.usersNumber','Users number')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.howHear" :label="$t('exoplatform.LeadCapture.leadManagement.howDidYouHear','How did you hear about us')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.currentSolution" :label="$t('exoplatform.LeadCapture.leadManagement.currentSolution','Current solution')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.solutionType" :label="$t('exoplatform.LeadCapture.leadManagement.solutionType','Solution type')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.shortlistVendors" :label="$t('exoplatform.LeadCapture.leadManagement.shortlistVendors','Shortlist vendors')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.companyWebsite" :label="$t('exoplatform.LeadCapture.leadManagement.companyWebsite','Company website')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.employeesNumber" :label="$t('exoplatform.LeadCapture.leadManagement.numberOfEmployees','Number of employees')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="6">
                                                    <v-text-field v-model="lead.industry" :label="$t('exoplatform.LeadCapture.leadManagement.industry','Industry')"></v-text-field>
                                                </v-col>
                                                <v-col cols="12" sm="12" md="12" class="editor">
                                                    <div v-on:dblclick="showFck1()" class="itemTitle v-label theme--light">{{$t('exoplatform.LeadCapture.leadManagement.solutionRequirements','Solution requirements')}}</div>
                                                    <div v-show="!showEditor1" class="textContent" v-on:dblclick="showFck1()" v-html="lead.solutionRequirements"></div>
                                                    <div v-show="showEditor1">
                                                        <ck-editor ref="ck1" :content="content" />
                                                    </div>
                                                </v-col>

                                                <v-col cols="12" sm="12" md="12" class="editor">
                                                    <div v-on:dblclick="showFck()" class="itemTitle v-label theme--light">{{$t('exoplatform.LeadCapture.leadManagement.interactionSummary','Interaction summary')}}</div>
                                                    <div v-show="!showEditor" class="textContent" v-on:dblclick="showFck()" v-html="lead.interactionSummary"></div>
                                                    <div v-show="showEditor">
                                                        <ck-editor ref="ck" :content="content" />
                                                    </div>
                                                </v-col>
                                            </v-row>
                                            <div class="uiAction">
                                                <button :disabled="!valid" @click="saveLead()" class="btn btn-primary" type="button">{{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}
                                                </button>
                                            </div>

                                        </v-container>

                                    </v-card-text>

                                    <div style="flex: 1 1 auto;"></div>

                                </v-card>
                            </v-form>
                        </v-tab-item>

                        <v-tab-item class="tabContent" eager id="captureInfo" value="captureInfo">
                            <v-row>

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.personSource','Person Source')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="9">{{$t(`exoplatform.LeadCapture.${lead.personSource}`,lead.personSource)}}</v-col>

                            </v-row>
                            <v-row>

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.captureMethod','Capture Method')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="9">{{$t(`exoplatform.LeadCapture.method.${lead.captureMethod}`,lead.captureMethod)}}</v-col>

                            </v-row>
                            <v-row>

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.captureType','Capture Type')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="8">{{lead.captureType}}</v-col>

                            </v-row>
                            <v-row>

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.captureDetail','Capture Detail')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="9">{{$t(`exoplatform.LeadCapture.detail.${lead.captureSourceInfo}`,lead.captureSourceInfo)}}</v-col>

                            </v-row>
                            <v-row>

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.originalReferrer','Original Referrer')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="9">{{lead.originalReferrer}}</v-col>

                            </v-row>
                            <v-row v-if="lead.blogSubscription==true">

                                <v-col cols="12" sm="4" md="3"><strong>{{$t('exoplatform.LeadCapture.leadManagement.blogSubscriptionDate','formattedBlogSubscriptionDate')}}:</strong></v-col>
                                <v-col cols="12" sm="8" md="9">{{lead.formattedBlogSubscriptionDate}}</v-col>

                            </v-row>
                            <form-responses :lead="lead" :formResponses="formResponses" />
                        </v-tab-item>

                    </v-tabs-items>
                </template>

            </v-col>
        </v-row>
        <task-drawer v-if="drawer" :drawer="drawer" :task="task" @updateTask="updateTask" @closeDrawer="onCloseDrawer" />
        <!--         <v-navigation-drawer absolute floating right temporary v-model="drawer" width="30%">
            <notes-drawer :lead="lead" :comments="comments" v-on:toggleDrawer="toggleDrawer" v-on:changeStatus="changeStatus" />
        </v-navigation-drawer> -->
        <v-navigation-drawer absolute floating right temporary v-model="toDodrawer" width="30%">
            <to-do-drawer :lead="lead" :tasks="tasks" v-on:toggleToDoDrawer="toggleToDoDrawer" />
        </v-navigation-drawer>
        <edit-lead-drawer ref="editLeadDrawer" :lead="lead" v-on:save="saveLead" />
    </v-card-text>
</v-card>
</template>

<script>
import Vue from 'vue';
import notesDrawer from './NotesDrawer.vue';
import toDoDrawer from './ToDoDrawer.vue';
import editLeadDrawer from './EditLeadDrawer.vue';
import FormResponses from './FormResponses.vue';
import ckEditor from '../commons/ckEditor.vue';

export default {
    components: {
        /* notesDrawer, */
        toDoDrawer,
        editLeadDrawer,
        FormResponses,
        ckEditor
    },
    props: ['lead', 'formResponses', 'timeline', 'task', 'context', 'assignees', 'tasks'],
    data: () => ({
        view: 'timeline',
        valid: true,
        confirmDialog: false,
        selectedTab: 'timeline',
        drawer: null,
        showEditor: false,
        showEditor1: false,
        edited: false,
        edited1: false,
        toDodrawer: null,
        fab: false,
        content: "",
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
            valideMail: value => value => /.+@.+/.test(v) || 'E-mail must be valid',
            startWith: value => value.startsWith("portal/rest") || "Should start with 'portal/rest', please don't include the server host",
        }
    }),

    computed: {
        leadAvatar: function () {
            if (this.lead.communityUserName === null || typeof (this.lead.communityUserName) === 'undefined') {
                return "/eXoSkin/skin/images/system/UserAvtDefault.png"
            }
            return "/rest/v1/social/users/" + this.lead.communityUserName + "/avatar"
        },
        ownerAvatar: function () {
            if (this.lead.assignee === null || typeof (this.lead.assignee) === 'undefined') {
                return "/eXoSkin/skin/images/system/UserAvtDefault.png"
            }
            return "/rest/v1/social/users/" + this.lead.assignee + "/avatar"
        },
        badge: function () {
            const undone = this.tasks.filter(item => {
                return !item.completed
            });
            return undone.length
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
    },

    methods: {
        openTaskDrawer() {
            this.drawer = true;
            document.body.style.overflow = this.drawer ? 'hidden' : 'auto';
        },
        updateTask(item) {
            this.lead.status = item.status.name
             fetch(`/portal/rest/leadcapture/leadsmanagement/timeline/` + this.lead.id, {
                    credentials: 'include',
                })
                .then((resp) => resp.json())
                .then((resp) => {
                    this.timeline = resp.sort((a, b) => b.time - a.time);;
                });
        },
        onCloseDrawer: function (drawer) {
            this.drawer = drawer;
            document.body.style.overflow = 'auto';
        },
        openEditDrawer() {
            this.$refs.editLeadDrawer.open()
        },
        toggleDrawer() {
            this.drawer = !this.drawer;
        },
        toggleToDoDrawer() {
            this.toDodrawer = !this.toDodrawer;
        },
        backToList() {
            this.$emit('backToList');
        },
        deleteLead() {
            this.confirmDialog = true;
        },
        remove() {
            this.$emit('remove', this.lead);
            this.confirmDialog = false;
        },
        changeStatus(item) {
            this.lead.status = item.value
            this.$emit('changeStatus', this.lead);
        },
        saveLead() {
            if (this.edited) {
                this.lead.interactionSummary = this.$refs.ck.getContent()
            }
            if (this.edited1) {
                this.lead.solutionRequirements = this.$refs.ck1.getContent()
            }
            this.showEditor = false;
            this.showEditor1 = false;
            this.$emit('saveLead', this.lead)
        },
        showFck() {
            this.edited = true;
            this.$refs.ck.setContent(this.lead.interactionSummary)
            this.showEditor = true;
        },

        showFck1() {
            this.edited1 = true;
            this.$refs.ck1.setContent(this.lead.solutionRequirements)
            this.showEditor1 = true;
        },
        getValueByField(formResponse, field) {
            console.log(formResponse + " _ " + field)
            return field
        },
        getIcon(form) {
            if (form.includes("demo")) {
                return "mdi-presentation"
            } else if (form.includes("contact")) {
                return "mdi-email-outline"
            } else if (form.includes("whitePaper")) {
                return "mdi-paperclip"
            } else if (form.includes("case")) {
                return "mdi-book-open"
            } else if (form.includes("reward")) {
                return "mdi-cash-usd-outline"
            } else if (form.includes("blog")) {
                return "mdi-post-outline"
            } else if (form.includes("community")) {
                return "mdi-tent"
            } else if (form.includes("task")) {
                return "mdi-calendar-check"
            } else {
                return "mdi-bookmark-outline"
            }
        },
        isTask(form) {
            return form.includes("task")
        },
        navigateTo(pagelink) {
            window.open(`${ eXo.env.portal.context }/${ eXo.env.portal.portalName }/${ pagelink }`, '_blank');
        },
        changeAssignee(item) {
            if (this.lead.assignee !== item.userName) {
                this.lead.telemarketerFullName = item.fullName
                this.lead.assignee = item.userName
                this.$emit('assigne', this.lead)
            }
        }

    },
};
</script>

<style>
.leadDetails {
    background: none !important;
    padding: 0 !important;
}

.tabContainer {
    padding-top: 20px;
}

b {
    color: #2f5e92;
    margin-right: 4px;
    display: inline-block;
    overflow: hidden;
    float: left;
    text-align: left;
}

.panel {
    margin: 5px;
}

.panel-header {
    min-height: 20px !important;
    font-size: 16px !important;
    /* color: #818182 !important; */
    font-weight: 600 !important;
}

.tl {
    font-size: 16px;
    color: #818182;
    font-weight: 600;
    margin-top: 10%;
    padding-bottom: 10px;
}

.msg p {
    background: #f2f2f5 none repeat scroll 0 0;
    border-radius: 3px;
    font-size: 14px;
    margin: 0;
    padding: 5px 10px 15px 12px;
    width: 100%;
    margin-bottom: 10px;
}

.msg {
    display: contents;

    float: right;
    text-align: left;
}

.tabContent {
    padding: 20px;
}

.infoContent {
    border-top: 1px solid #e1e8ee;
}

.v-dialog__content {
    z-index: 2000 !important;
}

.v-toolbar__content {
    padding: 5px 16px !important;
}

.tasksBtn {
    border: 1px solid #578dc9 !important;
}

.itemTitle {
    text-align: left;
    padding-top: 30px;
    padding-bottom: 20px;

}

.textContent {
    border-bottom: solid 1px rgba(0, 0, 0, 0.54);
    text-align: left;
    color: rgba(0, 0, 0, 0.87);

}

.editor {
    padding-bottom: 22px !important;
}

.leadToolBar {
    padding-bottom: 10px;
    /* border: 1px solid #e1e8ee !important; */
}

.response_date {
    font-size: small;
}

.headerBtn {
    border: 1px solid rgba(181, 181, 181, 0.87) !important;
    margin-right: 5px;
    padding: 8px 15px;
    border-radius: 3px !important;
    cursor: pointer !important;
    letter-spacing: .5px;
    color: rgba(0, 0, 0, 0.87) !important;
}

.VuetifyApp .v-application .headline {
    font-size: large !important;
}

.fields-text {
    text-align: left
}

.cl-field {
    cursor: pointer;
}

.back_btn {
    margin-top: 15px;
}

.edit_btn {
    font-size: 25px;
    top: 10px;
}

.delete_btn {
    font-size: 20px;
    top: 10px;
}

.avatar-field {
    padding: 2px;
}

.avatar-pl {
    box-shadow: 0px 0px 3px 2px rgba(111, 80, 80, 0.2), 0px 1px 1px 0px rgba(0, 0, 0, 0.14), 0px 1px 3px 0px rgba(0, 0, 0, 0.12);
    border: solid 5px white;

}
</style>
