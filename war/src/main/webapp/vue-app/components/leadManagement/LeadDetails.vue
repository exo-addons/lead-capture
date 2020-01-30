<template>
<v-flex>
    <v-container id="lead">
        <v-tooltip bottom>
            <template v-slot:activator="{ on }">
                <v-btn class="returnBoutton" color="secondary" @click="backToList()" fab dark v-on="on">
                    <v-icon dark>mdi-arrow-left</v-icon>
                </v-btn>
            </template>
            <span>Back to lead list</span>
        </v-tooltip>

        <v-tooltip bottom>
            <template v-slot:activator="{ on }">
                <v-btn class="notesBoutton" fab dark color="secondary" @click.stop="drawer = !drawer" v-on="on">
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

                <v-card-text v-if="context.isManager">Are you sure to delete the Lead</v-card-text>
                <v-card-text v-else>Only Managers can delete leads, pleas contact your manager.</v-card-text>

                <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <div class="uiAction">
                        <button v-if="context.isManager" @click="remove()" class="btn btn-primary" type="button">Delete
                        </button>
                        <button @click="confirmDialog = false" class="btn" type="button">Cancel
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
                        <v-toolbar-title>Edit Lead</v-toolbar-title>
                        <v-spacer></v-spacer>
                        <v-toolbar-items>
                            <v-btn :disabled="!valid" dark text @click="saveLead()">
                                Save
                            </v-btn>
                        </v-toolbar-items>

                    </v-toolbar>
                    <v-card-text>

                        <v-container>

                            <v-row>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.firstName" label="First name"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.lastName" label="Last name"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field :rules="[rules.required, rules.valideMail]" v-model="lead.mail" label="Mail"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.company" label="Company"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.position" label="Position"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.country" label="Country"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.language" label="Language"></v-text-field>
                                </v-col>
                                <v-col cols="12" sm="6" md="6">
                                    <v-text-field v-model="lead.geographiqueZone" label="Geographique Zone"></v-text-field>

                                </v-col>
                                <v-col cols="12" sm="6" md="2">
                                    <v-switch v-model="lead.MarketingSuspended" label="Marketing Suspended"></v-switch>
                                </v-col>
                                <v-col cols="12" sm="6" md="10">
                                    <v-text-field v-model="lead.marketingSuspendedCause" label="Marketing Suspended Cause"></v-text-field>
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
                            <h5>{{lead.country}}</h5>
                            <h5>{{lead.phone}}</h5>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <v-menu offset-y>
                            <template v-slot:activator="{ on }">
                                <v-btn class="statusBtn" color="primary" dark v-on="on">
                                    {{lead.status}}
                                </v-btn>
                            </template>
                            <v-list>
                                <v-list-item v-for="(item, index) in statusList" :key="index" @click="changeStatus(item)">
                                    <v-list-item-title>{{ item.title }}</v-list-item-title>
                                </v-list-item>
                            </v-list>
                        </v-menu>
                        <div class="tl">Telemarketer infos</div>

                        <p>{{lead.telemarketerFullName}}</p>

                    </div>
                </div>
                <div class="row col-md-12">
                    <v-layout>
                        <v-flex class="white text-center" flat>
                            <v-tabs background-color="blue-grey lighten-5 icons-and-text" grow v-model="selectedTab">
                                <v-tabs-slider color="primary" />
                                <v-tab href="#leadInfo" key="leadInfo">Lead INfo</v-tab>
                                <v-tab href="#captureInfo" key="captureInfo">Capture Info</v-tab>
                                <v-tab href="#projectInfo" key="projectInfo">Project Info</v-tab>
                            </v-tabs>

                            <v-tabs-items class="infoContent" v-model="selectedTab">
                                <v-tab-item class="tabContent" eager id="leadInfo" value="leadInfo">
                                    <div class="profile">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Lead ID:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.leadID}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Language:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.language}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Creation Date:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.formattedCreatedDate}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Update Date:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.formattedUpdatedDate}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Geographique Zone:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.geographiqueZone}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Marketing Suspended:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <v-checkbox disabled v-model="lead.MarketingSuspended" class="editLead"></v-checkbox>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Marketing Suspended Cause:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.marketingSuspendedCause}}</div>
                                            </div>
                                        </div>
                                    </div>
                                </v-tab-item>

                                <v-tab-item class="tabContent" eager id="captureInfo" value="captureInfo">
                                    <div class="profile">
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Person Source:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.personSource}}</div>
                                            </div>
                                        </div>

                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Capture Method</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.captureMethod}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Capture Type</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.captureType}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Capture Detail</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.captureDetail}}</div>
                                            </div>
                                        </div>
                                        <div class="row">
                                            <div class="col-md-3">
                                                <b>Original Referrer:</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.originalReferrer}}</div>
                                            </div>
                                        </div>

                                        <div class="row" v-if="lead.blogSubscription==true">
                                            <div class="col-md-3">
                                                <b>Blog Subscription Date</b>
                                            </div>
                                            <div class="col-md-8">
                                                <div>{{lead.formattedBlogSubscriptionDate}}</div>
                                            </div>
                                        </div>

                                        <form-responses :lead="lead" :formResponses="formResponses" />
                                    </div>
                                </v-tab-item>

                                <v-tab-item class="tabContent" eager id="projectInfo" value="projectInfo">
                                    <v-text-field v-model="lead.goal" label="Goal"></v-text-field>
                                    <v-text-field v-model="lead.usersNumber" label="Users number"></v-text-field>
                                    <v-text-field v-model="lead.currentSolution" label="Current solution"></v-text-field>
                                    <v-textarea v-model="lead.interactionSummary" label="Interaction summary"></v-textarea>
                                    <div class="uiAction">
                                        <button :disabled="!valid" @click="saveLead()" class="btn btn-primary" type="button">Save
                                        </button>
                                    </div>

                                </v-tab-item>
                            </v-tabs-items>
                        </v-flex>
                    </v-layout>
                </div>
            </form>

        </div>
    </v-container>
    <v-navigation-drawer absolute floating right temporary v-model="drawer" width="30%">
        <notes-drawer :lead="lead" :comments="comments" />
    </v-navigation-drawer>
</v-flex>
</template>

<script>
import Vue from 'vue';
import notesDrawer from './NotesDrawer.vue';
import FormResponses from './FormResponses.vue';

export default {
    components: {
        notesDrawer,
        FormResponses
    },
    props: ['lead', 'formResponses', 'comments', 'context'],
    data: () => ({
        statusList: [{
                title: 'Open'
            },
            {
                title: 'Attempted'
            },
            {
                title: 'Contacted'
            },
            {
                title: 'Qualified'
            },
            {
                title: 'Recycled'
            },
            {
                title: 'Accepted'
            },
            {
                title: 'Qualified'
            },
        ],
        valid: true,
        confirmDialog: false,
        selectedTab: 'leadInfo',
        drawer: null,
        fab: false,
        editLead: false,
        rules: {
            required: value => !!value || 'Required.',
            counter: value => value.length >= 3 || 'Min 3 characters',
            valideMail: value => value => /.+@.+/.test(v) || 'E-mail must be valid',
            startWith: value => value.startsWith("portal/rest") || "Should start with 'portal/rest', please don't include the server host",
        }
    }),

    computed: {
        leadAvatar: function () {
            console.log(this.lead.communityUserName)
            if (this.lead.communityUserName === null || typeof (this.lead.communityUserName) === 'undefined') {
                return "/eXoSkin/skin/images/system/UserAvtDefault.png"
            }
            return "/rest/v1/social/users/" + this.lead.communityUserName + "/avatar"
        }
    },
    methods: {
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
            this.lead.status = item.title
            this.$emit('changeStatus', this.lead);
        },
        saveLead() {
            this.editLead = false
            this.$emit('saveLead', this.lead)
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
    color: #818182 !important;
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

.statusBtn {
    margin-top: 27px;
    padding: 5px;
    min-width: 80% !important;
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
</style>
