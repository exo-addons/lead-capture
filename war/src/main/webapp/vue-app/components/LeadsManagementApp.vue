<template>
<v-app class="VuetifyApp" color="transaprent" id="leadsManagementApp">
    <main>
        <leads-list />
    </main>
</v-app>
</template>

<script>
import LeadsList from './leadManagement/LeadsList.vue';
export default {
    components: {
        LeadsList,
    },
};
</script>

<style>
#leadsManagementApp {
    overflow: hidden;
    padding: 10px 20px; 
}

select {
    width: auto;
}

#leadsManagementApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
}


#leadsManagementApp .v-input textarea {
    margin-bottom: 0;
    border: 0 !important;
    box-shadow: none;
}


#leadsManagementApp .v-toolbar .v-input {
    margin-left: 18px;
}
#leadsManagementApp .v-data-table {
 width: 100%;
}

#leadsManagementApp .v-navigation-drawer--temporary {
    z-index: 3000;
}

#leadsManagementApp .drawer-title{
    font-size: 18px;
    font-weight: bold;
    color: #4d5466;
}

#leadsManagementApp .drawerContent .v-label{
    font-weight: 700!important;;
}

#leadsManagementApp .v-badge__badge {
    height: 10px !important;
    min-width: 10px !important;
}

</style>
