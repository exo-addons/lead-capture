<template>
  <v-app id="leadsManagementApp" color="transaprent" class="VuetifyApp">
    <main>
      <div v-if="alert" class="alert" :class="alert_type" id>
        <i :class="alertIcon"></i>
        {{message}}
      </div>
      <v-layout>
        <v-data-table
          :headers="headers"
          :items="leadList"
          :search="search"
          sort-by="id"
          class="elevation-1"
          v-show="showTable"
        >
          <template v-slot:top>
            <v-toolbar flat color="white">
              <div class="flex-grow-1"></div>
              <v-col cols="8" sm="4" md="2">
              <v-select :items="zones" label="Geo Zone" ></v-select>
              </v-col>
              <v-divider class="mx-4"  inset vertical  ></v-divider>
               <v-switch v-model="notassigned" label="Only unassigned" class="mt-2"></v-switch>
               <v-switch v-model="myLeads" label="My Leads" class="mt-2"></v-switch>
              <v-divider class="mx-4"  inset vertical  ></v-divider>
              <v-col cols="12" sm="6" md="3">
                <v-text-field v-model="search" append-icon="search" label=""></v-text-field>
              </v-col>

              <v-dialog v-model="confirmDialog" max-width="290">
                <v-card>
                  <v-card-title class="headline">Confirmation</v-card-title>

                  <v-card-text>Are you sure to delete the Lead</v-card-text>

                  <v-card-actions>
                    <div class="flex-grow-1"></div>
                    <div class="uiAction">
                      <button type="button" class="btn btn-primary" @click="delete_()">Delete</button>
                      <button type="button" class="btn" @click="confirmDialog = false">Cancel</button>
                    </div>
                  </v-card-actions>
                </v-card>
              </v-dialog>
            </v-toolbar>
          </template>
          <template v-slot:item.name="{ item }">
            <a @click="edit(item)">
              <b>{{item.name}}</b>
            </a>
          </template>
                <template v-slot:item.assignee="{ item }">
                
                  <select v-model="item.assignee">
                                <option :value="null" disabled>Not assigned
                                </option>
                                <option v-bind:value="option" v-for="option in assignees" :key="option.userName">
                                   {{option.fullName}}
                                </option>
                   </select>

      </template>
          <template v-slot:no-data>No Leads</template>
        </v-data-table>
      </v-layout>
      <lead-details :lead="selectedLead" v-show="showDetails" v-on:backToList="backToList" />
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
     /*  {
        name: 'Gabriel Kline',
        mail: 'GabrielaJKline@dayrep.com',
        company: 'Warehouse Club, Inc.',
        position: 'Manager',
        country: 'United Kingdom',
        status: 'Recycle',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Glenn Herrington',
        mail: 'GlennJHerrington@dayrep.com',
        company: 'Kleinhans',
        position: 'Founder',
        country: 'Canada',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Lonna Savage',
        mail: 'LonnaJSavage@rhyta.com',
        company: 'Elek-Tek',
        position: 'Developer',
        country: 'France',
        status: 'Recycle',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Daniel Rodriguez',
        mail: 'DanielARodriguez@dayrep.com',
        company: 'Electric Avenue',
        position: 'Founder',
        country: 'Germany',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Michele Thompson',
        mail: 'MicheleJThompson@rhyta.com',
        company: 'Computer status',
        position: 'Hr Manager',
        country: 'France',
        status: 'Recycle',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'William Peart',
        mail: 'WilliamHPeart@fleckens.hu',
        company: 'HomeBase',
        position: 'Founder',
        country: 'Canada',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Stephanie Mooney',
        mail: 'StephanieRMooney@gustr.com',
        company: 'Life Map',
        position: 'Manager',
        country: 'Tunisia',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Susan Pope',
        mail: 'SusanRPope@fleckens.hu',
        company: 'Consumers Food and Drug',
        position: 'Hr Manager',
        country: 'Germany',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
      {
        name: 'Charlene McMillan',
        mail: 'CharleneJMcMillan@gustr.com',
        company: 'Ideal Garden Management',
        position: 'Manager',
        country: 'United Kingdom',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
          {
            demoMessage: '',
            demoMessageDate: '',
          },
        ],

        contactUs: [
          {
            contactMessage: '',
            contactMessageDate: '',
          },
        ],
        blogSubscription: {
          blogSubscriptionDate: '',
        },
        blogComments: [
          {
            blogComment: '',
            blogCommentDate: '',
          },
        ],
        communityRegistration: {
          registrationMethod: '',
          tribeUserName: '',
          communityRegistrationDate: '',
        },
        resourceDownloads: {
          resourceType: '',
          resourceName: 'incremental values',
          resourceDownloadDate: '',
        },
        rewardRequest: {
          rewardPack: '',
          rewardtoken: '',
          companyWallet: '',
          rewardRequestDate: '',
        },
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      }, */
      {
        name: 'Charlene McMillan',
        mail: 'CharleneJMcMillan@gustr.com',
        company: 'Ideal Garden Management',
        position: 'Manager',
        country: 'United Kingdom',
        status: 'Qualified',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',
        assignee:{
        userName:'test1',
        fullName:'krout MedAmine'
      },
        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

        demoRequest: [
           {
            demoMessage: 'demo message 1',
            demoMessageDate: '01/01/2020',
          },
          {
            demoMessage: 'demo message 2',
            demoMessageDate: '01/01/2020',
          },
          {
            demoMessage: 'demo message 3',
            demoMessageDate: '01/01/2020',
          }, 
        ],

        contactUs: [
          {
            contactMessage: 'Contact Message 1',
            contactMessageDate: '01/01/2020',
          },
          {
            contactMessage: 'Contact Message 2',
            contactMessageDate: '01/01/2020',
          },
          {
            contactMessage: 'Contact Message 3',
            contactMessageDate: '01/01/2020',
          },
        ],
        blogSubscription: true,
        blogSubscriptionDate: '01/01/2020',
        blogComments: [
          {
            blogComment: 'Blog Comments 2',
            blogCommentDate: '01/01/2020',
          },
          {
            blogComment: 'Blog Comments 3',
            blogCommentDate: '01/01/2020',
          },
          {
            blogComment: 'Blog Comments 1',
            blogCommentDate: '01/01/2020',
          },
        ],
        communityRegistration: true,
       registrationMethod: 'Facebook',
        tribeUserName: 'xxxxxxxxxxxx',
        communityRegistrationDate: '01/01/2020',    
        resourceDownloads: [
          {
            resourceType: 'livre blanc',
            resourceName: 'incremental values',
            resourceDownloadDate: '01/01/2020',
          },
          {
            resourceType: 'Case study',
            resourceName: 'incremental values',
            resourceDownloadDate: '01/01/2020',
          },
        ],
        rewardRequest: [
          {
            rewardPack: 'xxxxxxxxxxxxxxxxxxx',
            rewardtoken: '',
            companyWallet: 'xxxxxxxxxxxxxx',
            rewardRequestDate: '01/01/2020',
          },
          {
            rewardPack: 'ccccccccccccc',
            rewardtoken: 'ccccccccccccccccccc',
            companyWallet: 'cccccccccccccc',
            rewardRequestDate: '01/01/2020',
          },
        ],
        notes: [
           {
            noteMessage: 'noteMessage message 1',
            noteMessageDate: '01/01/2020',
          },
          {
            noteMessage: 'noteMessage message 2',
            noteMessageDate: '01/01/2020',
          },
          {
            noteMessage: 'noteMessage message 3',
            noteMessageDate: '01/01/2020',
          }, 
        ],
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      }, {
        name: 'David Martin',
        mail: 'DavidSMartin@gustr.com',
        company: 'Gemco',
        position: 'Founder',
        country: 'United Kingdom',
        status: 'Recycle',

        phone: '21628845948',
        leadID: '',
        creationDate: 'Oct 18: 2019 5 04 AM',
        updateDate: 'Oct 18: 2019 5:45 AM',
        language: 'EN/FR/ES (based on browser settings)',

        geographiqueZone: 'MEA/LATAM/APAC/Western',

        marketingSuspended: 'FALSE',
        marketingSuspendedCause: '',

        captureMethod: 'Resource download',
        captureType: 'RDL-wp/ RDL-Case study/ RDL livre blanc/ RDL etude de cas..',
        captureDetail: 'Experience collaborateur/ Digital workplace solutions…',

/*         assignee:{
        userName:'test1',
        fullName:'krout MedAmine'
      }, */
        demoRequest: [
           {
            demoMessage: 'demo message 1',
            demoMessageDate: '01/01/2020',
          },
          {
            demoMessage: 'demo message 2',
            demoMessageDate: '01/01/2020',
          },
          {
            demoMessage: 'demo message 3',
            demoMessageDate: '01/01/2020',
          }, 
        ],

        contactUs: [
          {
            contactMessage: 'Contact Message 1',
            contactMessageDate: '01/01/2020',
          },
          {
            contactMessage: 'Contact Message 2',
            contactMessageDate: '01/01/2020',
          },
          {
            contactMessage: 'Contact Message 3',
            contactMessageDate: '01/01/2020',
          },
        ],
        blogSubscription: true,
        blogSubscriptionDate: '01/01/2020',
        blogComments: [
          {
            blogComment: 'Blog Comments 2',
            blogCommentDate: '01/01/2020',
          },
          {
            blogComment: 'Blog Comments 3',
            blogCommentDate: '01/01/2020',
          },
          {
            blogComment: 'Blog Comments 1',
            blogCommentDate: '01/01/2020',
          },
        ],
        communityRegistration: true,
       registrationMethod: 'Facebook',
        tribeUserName: 'xxxxxxxxxxxx',
        communityRegistrationDate: '01/01/2020',    
        resourceDownloads: [
          {
            resourceType: 'livre blanc',
            resourceName: 'incremental values',
            resourceDownloadDate: '01/01/2020',
          },
          {
            resourceType: 'Case study',
            resourceName: 'incremental values',
            resourceDownloadDate: '01/01/2020',
          },
        ],
        rewardRequest: [
          {
            rewardPack: 'xxxxxxxxxxxxxxxxxxx',
            rewardtoken: '',
            companyWallet: 'xxxxxxxxxxxxxx',
            rewardRequestDate: '01/01/2020',
          },
          {
            rewardPack: 'ccccccccccccc',
            rewardtoken: 'ccccccccccccccccccc',
            companyWallet: 'cccccccccccccc',
            rewardRequestDate: '01/01/2020',
          },
        ],
        notes: [
           {
            noteMessage: 'noteMessage message 1',
            noteMessageDate: '01/01/2020',
          },
          {
            noteMessage: 'noteMessage message 2',
            noteMessageDate: '01/01/2020',
          },
          {
            noteMessage: 'noteMessage message 3',
            noteMessageDate: '01/01/2020',
          }, 
        ],
        personSource: 'Web referal/organic/PPC/Direct (depends on original referrer)',
        landingPageInfo: '/blog/2017/10/18/how-to-modify-a-change-set-in-liquibase/ (eXo landing page)',
        captureSourceInfo: 'Form name',
        personIP: '123.136.203.68',
        originalReferrer: 'google/ capterra/ (visitor source)',
      },
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

  computed: {},

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
               if (!this.notassigned && !this.myLeads) {return true}
               if (this.notassigned && this.myLeads) {return false}
              return (this.notassigned && !(value===null||value==={}|| typeof(value) !== 'undefined')) || (this.myLeads && (value!==null && value!=={} && typeof(value) !== 'undefined') && value.userName===this.currentUser)  
           
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
      fetch(`/portal/rest/gamification/connectors/github/hooksmanagement/hooks`, {
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
