<template>
<v-flex>
    <v-container id="lead">
        <v-tooltip bottom>
            <template v-slot:activator="{ on }">
                <v-btn class="returnBoutton" color="secondary" @click="backToList()" fab dark v-on="on">
                    <v-icon dark>mdi-arrow-left</v-icon>
                </v-btn>
            </template>
            <span>{{$t('exoplatform.LeadCapture.leadManagement.BackToList','Back to lead list')}}</span>
        </v-tooltip>

        <v-tooltip bottom>
            <template v-slot:activator="{ on }">
                <v-btn class="notesBoutton" fab dark color="secondary" @click.stop="toDodrawer = !toDodrawer" v-on="on">
                    <v-icon dark>mdi-clipboard-text</v-icon>
                </v-btn>
            </template>
            <span>Notes</span>
        </v-tooltip>
        <v-speed-dial class="manageBoutton" v-model="fab" top right direction="bottom" transition="slide-x-reverse-transition" open-on-hover>
            <template v-slot:activator>
                <v-btn v-model="fab" color="blue darken-2" dark fab>
                    <v-icon v-if="fab">mdi-close</v-icon>
                    <v-icon large v-else>mdi-account-edit</v-icon>
                </v-btn>
            </template>
            <v-btn fab dark small color="blue darken-2" @click="editLead=true">
                <v-icon>mdi-pencil</v-icon>
            </v-btn>
            <v-btn fab dark small color="blue darken-2" @click="deleteLead()">
                <v-icon>mdi-delete</v-icon>
            </v-btn>
        </v-speed-dial>

        <v-dialog max-width="290" v-model="confirmDialog">
            <v-card>
                <v-card-title class="headline">Confirmation</v-card-title>

                <v-card-text v-if="context.isManager">{{$t('exoplatform.LeadCapture.leadManagement.deleteWarning','Are you sure to delete the Lead')}}</v-card-text>
                <v-card-text v-else>{{$t('exoplatform.LeadCapture.leadManagement.permissionWarning','Only Managers can delete leads, pleas contact your manager.')}}</v-card-text>

                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <div class="uiAction">
                        <button v-if="context.isManager" @click="remove()" class="btn btn-primary" type="button">{{$t('exoplatform.LeadCapture.leadManagement.delete','Delete')}}
                        </button>
                        <button @click="confirmDialog = false" class="btn" type="button">{{$t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')}}
                        </button>
                    </div>
                </v-card-actions>
            </v-card>
        </v-dialog>

        <v-dialog v-model="editLead" fullscreen hide-overlay transition="dialog-bottom-transition" scrollable>
            <v-form ref="form" v-model="valid">
                <v-card tile>

                    <v-toolbar dark color="primary">
                        <v-btn icon dark @click="editLead = false">
                            <v-icon>mdi-close</v-icon>
                        </v-btn>
                        <v-toolbar-title>{{$t('exoplatform.LeadCapture.leadManagement.editLead','Edit Lead')}}</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-toolbar-items>
                            <v-btn :disabled="!valid" dark text @click="saveLead()">
                                {{$t('exoplatform.LeadCapture.leadManagement.save','Save')}}
                            </v-btn>
                        </v-toolbar-items>

                    </v-toolbar>
                    <v-card-text>

                        <v-container>

                            <v-row>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.firstName" :label="$t('exoplatform.LeadCapture.leadManagement.firstName','First name')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.lastName" :label="$t('exoplatform.LeadCapture.leadManagement.lastName','Last name')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field :rules="[rules.required, rules.valideMail]" v-model="lead.mail" :label="$t('exoplatform.LeadCapture.leadManagement.mail','Mail')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.company" :label="$t('exoplatform.LeadCapture.leadManagement.company','Company')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.phone" :label="$t('exoplatform.LeadCapture.leadManagement.phone','Phone')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.position" :label="$t('exoplatform.LeadCapture.leadManagement.position','Position')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.inferredCountry" :label="$t('exoplatform.LeadCapture.leadManagement.country','Country')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.language" :label="$t('exoplatform.LeadCapture.leadManagement.language','Language')"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.geographiqueZone" :label="$t('exoplatform.LeadCapture.leadManagement.geographiqueZone','Geographique Zone')"></v-text-field>

                                </v-col>
                                <v-col cols="12" sm="6" md="2">
                                    <v-switch v-model="lead.marketingSuspended" :label="$t('exoplatform.LeadCapture.leadManagement.marketingSuspended','Marketing Suspended')"></v-switch>
                                </v-col>
                                <v-col v-if="lead.marketingSuspended" cols="12" sm="6" md="10">
                                    <v-text-field v-model="lead.marketingSuspendedCause" :label="$t('exoplatform.LeadCapture.leadManagement.marketingSuspendedCause','Marketing Suspended Cause')"></v-text-field>
                                </v-col>
                            </v-row>

                        </v-container>

                    </v-card-text>

                    <div style="flex: 1 1 auto;"></div>

                </v-card>
            </v-form>
        </v-dialog>
        <div class="container">
            <form method="post">

                <div class="row">
                    <div class="col-md-3">
                        <div class="profile-img">
                            <img alt :src="leadAvatar" />
                        </div>
                    </div>
                    <div class="col-md-5">
                        <div class="profile-head">
                            <h1>{{lead.firstName}} {{lead.lastName}}</h1>
                            <h4>{{lead.mail}}</h4>
                            <h6>{{lead.company}}</h6>
                            <h6>{{lead.position}}</h6>
                            <h5>{{lead.inferredCountry}}</h5>
                            <h5>{{lead.phone}}</h5>
                        </div>
                    </div>
                    <div class="col-md-3">

                        <v-btn class="text-uppercase caption primary--text tasksBtn" outlined x-large @click.stop="drawer = !drawer" v-on="on">{{$t(`exoplatform.LeadCapture.status.${lead.status}`,lead.status)}}</v-btn>
                        <div class="tl">Telemarketer infos</div>

                        <p>{{lead.telemarketerFullName}}</p>

                    </div>
                </div>
                <div class="row col-md-12">
                    <v-layout>
                        <v-flex class="white text-center" flat>
                            <v-tabs background-color="blue-grey lighten-5 icons-and-text" grow v-model="selectedTab">
                                <v-tabs-slider color="primary" />
                                <v-tab href="#leadInfo" key="leadInfo">{{$t('exoplatform.LeadCapture.leadManagement.leadInfo','Lead Info')}}</v-tab>
                                <v-tab href="#captureInfo" key="captureInfo">{{$t('exoplatform.LeadCapture.leadManagement.captureInfo','Capture Info')}}</v-tab>
                                <v-tab href="#projectInfo" key="projectInfo">{{$t('exoplatform.LeadCapture.leadManagement.projectInfo','Project Info')}}</v-tab>
                            </v-tabs>

                            <v-tabs-items class="infoContent" v-model="selectedTab">
                                <v-tab-item class="tabContent" eager id="leadInfo" value="leadInfo">
                                    <div class="profile">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Lead ID:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.id}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.language','Language')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.language}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.creationDate','Creation Date')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.formattedCreatedDate}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.updateDate','Update Date')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.formattedUpdatedDate}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.geographiqueZone','Geographique Zone')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.geographiqueZone}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.marketingSuspended','Marketing Suspended')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <v-switch disabled v-model="lead.marketingSuspended"></v-switch>
                                            </div>
                                        </div>
                                        <div v-if="lead.marketingSuspended" class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.marketingSuspendedCause','Marketing Suspended Cause')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.marketingSuspendedCause}}</div>
                                            </div>
                                        </div>
                                    </div>
                                </v-tab-item>

                                <v-tab-item class="tabContent" eager id="captureInfo" value="captureInfo">
                                    <div class="profile">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.personSource','Person Source')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{$t(`exoplatform.LeadCapture.${lead.personSource}`,lead.personSource)}}</div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.captureMethod','Capture Method')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{$t(`exoplatform.LeadCapture.${lead.captureMethod}`,lead.captureMethod)}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.captureType','Capture Type')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{$t(`exoplatform.LeadCapture.${lead.captureType}`,lead.captureType)}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.captureDetail','Capture Detail')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.captureSourceInfo}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.originalReferrer','Original Referrer')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.originalReferrer}}</div>
                                            </div>
                                        </div>

                                        <div class="row" v-if="lead.blogSubscription==true">
                                            <div class="col-md-3">
                                                <b>{{$t('exoplatform.LeadCapture.leadManagement.blogSubscriptionDate','Blog Subscription Date')}}:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div class="infoValue">{{lead.formattedBlogSubscriptionDate}}</div>
                                            </div>
                                        </div>

                                        <form-responses :lead="lead" :formResponses="formResponses" />
                                    </div>
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
                            </v-tabs-items>
                        </v-flex>
                    </v-layout>
                </div>
            </form>

        </div>
    </v-container>
    <v-navigation-drawer absolute floating right temporary v-model="drawer" width="30%">
        <notes-drawer :lead="lead" :comments="comments" v-on:toggleDrawer="toggleDrawer" v-on:changeStatus="changeStatus" />
    </v-navigation-drawer>
    <v-navigation-drawer absolute floating right temporary v-model="toDodrawer" width="30%">
        <to-do-drawer :lead="lead" :tasks="tasks" v-on:toggleToDoDrawer="toggleToDoDrawer" />
    </v-navigation-drawer>
</v-flex>
</template>

<script>
import Vue from 'vue';
import notesDrawer from './NotesDrawer.vue';
import toDoDrawer from './ToDoDrawer.vue';
import FormResponses from './FormResponses.vue';
import ckEditor from '../commons/ckEditor.vue';

export default {
    components: {
        notesDrawer,
        toDoDrawer,
        FormResponses,
        ckEditor
    },
    props: ['lead', 'formResponses', 'comments', 'context', 'tasks'],
    data: () => ({
        valid: true,
        confirmDialog: false,
        selectedTab: 'leadInfo',
        drawer: null,
        showEditor: false,
        showEditor1: false,
        edited: false,
        edited1: false,
        toDodrawer: null,
        fab: false,
        editLead: false,
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
        }
    },

    methods: {
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
            this.editLead = false
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
        }

    },
};
</script>

<style>
body {
    background: -webkit-linear-gradient(left, #3931af, #00c6ff);
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

.profile-img {
    text-align: center;
}

.profile-img img {
    width: 70%;
    height: 100%;
}

.profile-img .file {
    position: relative;
    overflow: hidden;
    margin-top: -20%;
    width: 70%;
    border: none;
    border-radius: 0;
    font-size: 15px;
    background: #212529b8;
}

.profile-img .file input {
    position: absolute;
    opacity: 0;
    right: 0;
    top: 0;
}

.profile-head {
    margin-right: 10px;
}

.profile-head h5 {
    color: #333;
}

.profile-head h6 {
    color: #0062cc;
}

.profile-edit-btn {
    border: none;
    border-radius: 1.5rem;
    width: 70%;
    padding: 2%;
    font-weight: 600;
    color: #6c757d;
    cursor: pointer;
}

.proile-rating {
    font-size: 12px;
    color: #818182;
    margin-top: 5%;
}

.proile-rating span {
    color: #495057;
    font-size: 15px;
    font-weight: 600;
}

.profile-head .nav-tabs {
    margin-bottom: 5%;
}

.profile-head .nav-tabs .nav-link {
    font-weight: 600;
    border: none;
}

.profile-head .nav-tabs .nav-link.active {
    border: none;
    border-bottom: 2px solid #0062cc;
}

.profile-work {
    padding: 14%;
    margin-top: -15%;
}

.tl {
    font-size: 16px;
    color: #818182;
    font-weight: 600;
    margin-top: 10%;
    padding-bottom: 10px;
}

.profile-work a {
    text-decoration: none;
    color: #495057;
    font-weight: 600;
    font-size: 14px;
}

.profile-work ul {
    list-style: none;
}

.profile-tab label {
    font-weight: 600;
}

.profile-tab p {
    font-weight: 600;
    color: #0062cc;
}

.time_date {
    color: #747474;
    display: block;
    font-size: 10px;
    margin: -16px 0 0;
    text-align: right;
    padding-bottom: 8px;
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
    border: 1px solid #e1e8ee;
}

/* This is for documentation purposes and will not be needed in your application */
#lead .v-speed-dial {
    position: absolute;
}

#lead .v-btn--floating {
    position: relative;
}

.notesBoutton {
    right: 16px;
    position: absolute !important;
    top: 16px;
}

.returnBoutton {
    left: 5px;
    position: absolute !important;
    top: 16px;
}

.manageBoutton {
    top: 79px !important;
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

.infoValue {
    text-align: left;
}
</style>
