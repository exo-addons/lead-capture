<template>
  <v-flex>
    <v-container>
      <div class="container">
        <form method="post">
          <div class="row">
            <div class="col-md-3">
              <div class="profile-img">
                <img src="/rest/v1/social/users/root/avatar" alt />
              </div>
            </div>
            <div class="col-md-5">
              <div class="profile-head">
                <h1>{{lead.name}}</h1>
                <h4>{{lead.mail}}</h4>
                <h6>{{lead.company}}</h6>
                <h6>{{lead.position}}</h6>
                <h5>{{lead.country}}</h5>
                <h5>{{lead.phone}}</h5>
              </div>
            </div>
            <div class="col-md-3">
              <a role="button" class="actionIcon" @click="backToList()" style="float: right;">
                <i class="uiIconBack uiIconLightGray"></i>
              </a>

              <h3>{{lead.status}}</h3>
              <div class="tl">Telemarketer infos</div>
              <b>Name:</b>
              <p>Sonia</p>
              <b>Mail:</b>
              <p>ux-sf02@exoplatform.com</p>
              <v-btn color="blue" dark @click.stop="drawer = !drawer">Notes</v-btn>
            </div>
          </div>
          <div class="row col-md-12">
              <v-layout >
                <v-flex class="white text-center" flat>
                  <v-tabs v-model="selectedTab" grow background-color="blue-grey lighten-5 icons-and-text">
                    <v-tabs-slider color="primary" />
                    <v-tab key="leadInfo" href="#leadInfo">Lead INfo</v-tab>
                    <v-tab key="leadSourceInfo" href="#leadSourceInfo">Lead Source Info</v-tab>
                    <v-tab key="captureInfo" href="#captureInfo">Capture Info</v-tab>
                  </v-tabs>

                  <v-tabs-items v-model="selectedTab" class="infoContent">
                    <v-tab-item id="leadInfo" value="leadInfo" class="tabContent" eager>
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
                            <div>{{lead.creationDate}}</div>
                          </div>
                        </div>
                        <div class="row">
                          <div class="col-md-3">
                            <b>Update Date:</b>
                          </div>
                          <div class="col-md-8">
                            <div>{{lead.updateDate}}</div>
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
                            <div>{{lead.MarketingSuspended}}</div>
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
                    <v-tab-item  id="leadSourceInfo"  value="leadSourceInfo"  class="tabContent" eager >
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
                          <b>Landing page Info:</b>
                        </div>
                        <div class="col-md-8">
                          <div>{{lead.landingPageInfo}}</div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-md-3">
                          <b>Capture source Info:</b>
                        </div>
                        <div class="col-md-8">
                          <div>{{lead.captureSourceInfo}}</div>
                        </div>
                      </div>
                      <div class="row">
                        <div class="col-md-3">
                          <b>Person IP:</b>
                        </div>
                        <div class="col-md-8">
                          <div>{{lead.personIP}}</div>
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
                    </v-tab-item>

                    <v-tab-item id="captureInfo" value="captureInfo" class="tabContent" eager>
                      <div class="profile">
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

                        <div class="row" v-if="lead.blogSubscription==true">
                          <div class="col-md-3">
                            <b>Blog Subscription Date</b>
                          </div>
                          <div class="col-md-8">
                            <div>{{lead.blogSubscriptionDate}}</div>
                          </div>
                        </div>

                        <v-expansion-panels v-model="ressources" multiple>
                          <v-expansion-panel class="panel" v-if="lead.communityRegistration==true">
                            <v-expansion-panel-header class="panel-header">Community Registration</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <div class="row">
                                <div class="col-md-3">
                                  <b>Tribe User Name</b>
                                </div>
                                <div class="col-md-8">
                                  <div>{{lead.tribeUserName}}</div>
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-3">
                                  <b>Registration Method</b>
                                </div>
                                <div class="col-md-8">
                                  <div>{{lead.registrationMethod}}</div>
                                </div>
                              </div>
                              <div class="row">
                                <div class="col-md-3">
                                  <b>Community Registration Date</b>
                                </div>
                                <div class="col-md-8">
                                  <div>{{lead.communityRegistrationDate}}</div>
                                </div>
                              </div>
                            </v-expansion-panel-content>
                          </v-expansion-panel>
                          <v-expansion-panel class="panel" v-if="lead.demoRequest.length > 0">
                            <v-expansion-panel-header
                              class="panel-header"
                            >Demo Requests ({{lead.demoRequest.length}})</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <div
                                v-for="item in lead.demoRequest"
                                :key="item.demoMessage"
                                class="msg"
                              >
                                <p>{{item.demoMessage}}</p>
                                <span class="time_date">{{item.demoMessageDate}}</span>
                              </div>
                            </v-expansion-panel-content>
                          </v-expansion-panel>

                          <v-expansion-panel class="panel" v-if="lead.contactUs.length > 0">
                            <v-expansion-panel-header
                              class="panel-header"
                            >contact Us ({{lead.contactUs.length}})</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <div
                                v-for="item in lead.contactUs"
                                :key="item.contactMessage"
                                class="msg"
                              >
                                <p>{{item.contactMessage}}</p>
                                <span class="time_date">{{item.contactMessageDate}}</span>
                              </div>
                            </v-expansion-panel-content>
                          </v-expansion-panel>

                          <v-expansion-panel class="panel" v-if="lead.blogComments.length > 0">
                            <v-expansion-panel-header
                              class="panel-header"
                            >Blog Comments ({{lead.blogComments.length}})</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <div
                                v-for="item in lead.blogComments"
                                :key="item.blogComments"
                                class="msg"
                              >
                                <p>{{item.blogComment}}</p>
                                <span class="time_date">{{item.blogCommentDate}}</span>
                              </div>
                            </v-expansion-panel-content>
                          </v-expansion-panel>

                          <v-expansion-panel class="panel" v-if="lead.resourceDownloads.length > 0">
                            <v-expansion-panel-header
                              class="panel-header"
                            >Resource Downloads ({{lead.resourceDownloads.length}})</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <downloads-list :downloads="lead.resourceDownloads" />
                            </v-expansion-panel-content>
                          </v-expansion-panel>

                          <v-expansion-panel class="panel" v-if="lead.rewardRequest.length > 0">
                            <v-expansion-panel-header
                              class="panel-header"
                            >Reward Requests ({{lead.rewardRequest.length}})</v-expansion-panel-header>
                            <v-expansion-panel-content>
                              <reward-requests-list :requests="lead.rewardRequest" />
                            </v-expansion-panel-content>
                          </v-expansion-panel>
                        </v-expansion-panels>
                      </div>
                    </v-tab-item>
                  </v-tabs-items>
                </v-flex>
              </v-layout>
          </div>
        </form>
      </div>
    </v-container>
    <v-navigation-drawer v-model="drawer" absolute temporary right floating width="30%">
      <notes-drawer :notes="lead.notes" />
    </v-navigation-drawer>
  </v-flex>
</template>

<script>
import Vue from 'vue';
import downloadsList from './DownloadsList.vue';
import rewardRequestsList from './RewardRequestsList.vue';
import notesDrawer from './NotesDrawer.vue';

export default {
  components: {
    downloadsList,
    rewardRequestsList,
    notesDrawer,
  },
  props: ['lead'],
  data: () => ({
    selectedTab: 'leadInfo',
    drawer: null,
    items: [{title: 'Home', icon: 'dashboard'}, {title: 'About', icon: 'question_answer'}],
    panel: [0],
    ressources: [],
    valid: false,
    firstname: '',
    lastname: '',
    nameRules: [(v) => !!v || 'Name is required'],
    email: '',
    emailRules: [(v) => !!v || 'E-mail is required', (v) => /.+@.+/.test(v) || 'E-mail must be valid'],
  }),

  methods: {
    backToList() {
      this.$emit('backToList');
    },
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
.tabContent{
    padding: 20px;
}
.infoContent{
    border: 1px solid #e1e8ee;
}

</style>