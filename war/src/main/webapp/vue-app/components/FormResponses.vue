<template>
<v-expansion-panels multiple v-model="ressources">
    <v-expansion-panel class="panel" v-if="lead.communityRegistration==true">
        <v-expansion-panel-header class="panel-header">Community
            Registration
        </v-expansion-panel-header>
        <v-expansion-panel-content>
            <div class="row">
                <div class="col-md-3">
                    <b>Tribe User Name</b>
                </div>
                <div class="col-md-8">
                    <div>{{lead.communityUserName}}</div>
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
    <v-expansion-panel class="panel" v-for="formResponse in formResponses" :key="formResponse.form.id">
        <v-expansion-panel-header class="panel-header">{{formResponse.form.name}} ({{formResponse.responses.length}})
        </v-expansion-panel-header>
        <v-expansion-panel-content>
              <responses-list :responses="formResponse.responses" :fields="formResponse.form.fields" :form="formResponse.form.name"/>
        </v-expansion-panel-content>
    </v-expansion-panel>
</v-expansion-panels>
</template>

<script>
import responsesList from './ResponsesList.vue';
export default {
    props: ['lead','formResponses'],
    components: {
    responsesList,
  },
    data() {
        return {
           // formResponses:[]
        }
    },
    created() {
     //  this.initialize()     
    },


    methods: {

    }
    };

</script>