<template>
<exo-drawer ref="filterDrawer" right class="">
    <template slot="title">
        {{$t('exoplatform.LeadCapture.leadManagement.filter','Filter')}}
    </template>
    <template slot="content">
        <div>
            <form ref="form1">
                <v-row>
                    <v-label for="status">
                        {{ $t('exoplatform.LeadCapture.leadManagement.status','Status') }}
                    </v-label>
                    <select v-model="selectedStatus" name="status"  class="input-block-level ignore-vuetify-classes my-3">
                        <option v-for="item in filterStatusList" :key="item.value" :value="item.value">
                            {{ item.text}}
                        </option>
                    </select>
                </v-row>

                <v-row>
                    <v-label for="captureMethod">
                        {{ $t('exoplatform.LeadCapture.leadManagement.captureMethod','Capture Methode') }}
                    </v-label>
                    <select v-model="selectedMethod" name="captureMethod" class="input-block-level ignore-vuetify-classes my-3">
                        <option v-for="item in methodList" :key="item.value" :value="item.value">
                            {{ item.text}}
                        </option>
                    </select>
                </v-row>

                <v-row>
                    <v-label for="geoZone">
                        {{ $t('exoplatform.LeadCapture.leadManagement.geoZone','Geographique Zone') }}
                    </v-label>
                    <select v-model="selectedGeoZone" name="geoZone" class="input-block-level ignore-vuetify-classes my-3">
                        <option v-for="item in gZoneList" :key="item" :value="item">
                            {{ item}}
                        </option>
                    </select>
                </v-row>

                <v-row>

                    <v-col class="d-flex" cols="12" sm="6">

                        <div class="d-flex flex-wrap pt-2">
                            <label for="notassigned" class="v-label theme--light my-auto float-left">
                                {{$t('exoplatform.LeadCapture.leadManagement.onlyUnassigned','Only unassigned') }}
                            </label>
                            <v-switch ref="notassigned" v-model="notassigned" class="float-left my-0 ml-4" />
                        </div>

                    </v-col>
                    <v-col class="d-flex" cols="12" sm="6">
                        <div class="d-flex flex-wrap pt-2">
                            <label for="myLeads" class="v-label theme--light my-auto float-left">
                                {{$t('exoplatform.LeadCapture.leadManagement.myLeads','My Leads') }}
                            </label>
                            <v-switch ref="myLeads" v-model="myLeads" class="float-left my-0 ml-4" />
                        </div>
                    </v-col>
                </v-row>

                <v-row v-show="!notassigned && !myLeads">
                    <v-label for="assigneesFilter">
                        {{ $t('exoplatform.LeadCapture.leadManagement.owner','Owner')}}
                    </v-label>
                    <select v-model="selectedOwner" name="assigneesFilter" class="input-block-level ignore-vuetify-classes my-3">
                        <option v-for="item in assigneesFilter" :key="item.userName" :value="item.userName">
                            {{ item.fullName}}
                        </option>
                    </select>
                </v-row>
                <v-row>
                    <v-menu ref="menu1" v-model="menu1" :close-on-content-click="false" :return-value.sync="fromDate" transition="scale-transition" offset-y min-width="290px">
                        <template v-slot:activator="{ on }">
                            <v-label for="fromDate">
                                {{ $t('exoplatform.LeadCapture.leadManagement.createdFrom','Created From')}}
                            </v-label>
                            <input ref="fromDate" v-model="fromDate" type="text" name="fromDate" v-on="on" class="input-block-level ignore-vuetify-classes my-3" />
                        </template>
                        <v-date-picker v-model="fromDate" no-title scrollable>
                            <v-spacer></v-spacer>
                            <v-btn text color="primary" @click="menu1 = false">Cancel</v-btn>
                            <v-btn text color="primary" @click="$refs.menu1.save(fromDate)">OK</v-btn>
                        </v-date-picker>
                    </v-menu>
                </v-row>
                <v-row>
                    <v-menu ref="menu2" v-model="menu2" :close-on-content-click="false" :return-value.sync="toDate" transition="scale-transition" offset-y min-width="290px">
                        <template v-slot:activator="{ on }">

                            <v-label for="toDate">
                                {{ $t('exoplatform.LeadCapture.leadManagement.createdBefore','Created Before')}}
                            </v-label>
                            <input ref="toDate" v-model="toDate" type="text" name="toDate" v-on="on" class="input-block-level ignore-vuetify-classes my-3" />

                        </template>
                        <v-date-picker v-model="toDate" no-title scrollable>
                            <v-spacer></v-spacer>
                            <v-btn text color="primary" @click="menu2 = false">Cancel</v-btn>
                            <v-btn text color="primary" @click="$refs.menu2.save(toDate)">OK</v-btn>
                        </v-date-picker>
                    </v-menu>
                </v-row>
                <v-row>
                    <v-label for="userNumberMine">
                        {{ $t('exoplatform.LeadCapture.leadManagement.userNumberMin','Min Users number') }}
                    </v-label>
                    <input ref="userNumberMine" v-model="userNumberMine" type="text" name="userNumberMine" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
                <v-row>
                    <v-label for="userNumberMax">
                        {{$t('exoplatform.LeadCapture.leadManagement.userNumberMax','Max Users number') }}
                    </v-label>
                    <input ref="userNumberMax" v-model="userNumberMax" type="text" name="userNumberMax" class="input-block-level ignore-vuetify-classes my-3" />
                </v-row>
            </form>
        </div>
    </template>
    <template slot="footer">
        <div class="d-flex">
            <v-spacer />
            <v-btn class="btn mr-2" @click="reset()">
                <template>
                    {{ $t('exoplatform.LeadCapture.leadManagement.reset','Reset')  }}
                </template>
            </v-btn>
            <v-btn class="btn mr-2" @click="cancel()">
                <template>
                    {{ $t('exoplatform.LeadCapture.leadManagement.cancel','Cancel')  }}
                </template>
            </v-btn>
            <v-btn class="btn btn-primary" @click="aplyFilter()">
                <template>
                    {{ $t('exoplatform.LeadCapture.leadManagement.apply','Apply') }}
                </template>
            </v-btn>
        </div>
    </template>
</exo-drawer>
</template>

<script>
export default {

    props: ['assigneesFilter'],
    data: () => ({
        fromDate: "",
        toDate: "",
        selectedStatus: "active",
        selectedMethod: "",
        selectedOwner: "",
        notassigned: false,
        myLeads: false,
        selectedGeoZone: "",
        userNumberMax: "",
        userNumberMin: "",
        gZoneList: ["All", "US-Canada", "Western Europe", "Eastern Europe", "LatAm", "APAC", "MEA"]
    }),

    computed: {
        filterStatusList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.leadManagement.All'),
                    value: 'All'
                }, {
                    text: this.$t('exoplatform.LeadCapture.status.active'),
                    value: 'active'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.status.Raw'),
                    value: 'Raw'
                },
                {
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
                }
            ]
        },
        methodList() {
            return [{
                    text: this.$t('exoplatform.LeadCapture.leadManagement.All'),
                    value: 'All'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.leadManagement.community-registration'),
                    value: 'Community registration'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.Blog'),
                    value: 'Blog'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.contact-us'),
                    value: 'contact-us'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.demo-request'),
                    value: 'demo-request'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.resource-download'),
                    value: 'resource-download'
                },
                {
                    text: this.$t('exoplatform.LeadCapture.method.reward-form'),
                    value: 'reward-form'
                }
            ]
        }
    },

    methods: {
        reset() {
            this.selectedStatus = "active"
            this.selectedMethod = ""
            this.selectedOwner = ""
            this.notassigned = false
            this.myLeads = false
            this.fromDate = ""
            this.toDate = ""
            this.selectedGeoZone = ""
            this.userNumberMax = ""
            this.userNumberMin = ""
        },
        aplyFilter() {
            console.log(this.fromDate)
            console.log(this.toDate)
            const filter_ = {
                selectedStatus: this.selectedStatus,
                selectedMethod: this.selectedMethod,
                selectedOwner: this.selectedOwner,
                notassigned: this.notassigned,
                myLeads: this.myLeads,
                fromDate: this.fromDate,
                toDate: this.toDate,
                selectedGeoZone: this.selectedGeoZone,
                userNumberMax: this.userNumberMax,
                userNumberMin: this.userNumberMin
            }
            this.$emit('addFilter', filter_);
            this.$refs.filterDrawer.close();
        },
        cancel() {
            this.$refs.filterDrawer.close();
        },
        open() {

            this.$refs.filterDrawer.open();

        },

    }
}
</script>

<style>
.drawerContent {
    padding: 15px 27px;
}
</style>
