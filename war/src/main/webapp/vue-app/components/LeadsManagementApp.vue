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

.drawerTitle {
    font-size: 18px;
    font-weight: bold;
    color: @darkGrey;
}

.headerBorder {
    border-color: #f0f4fe !important;
}

.drawersBtn {
    border: 1px solid #1976d27a !important;
    margin-right: 5px;
    padding: 8px 15px;
    border-radius: 3px !important;
    cursor: pointer !important;
    letter-spacing: .5px;
}

.drawerContent {
    margin-top: 50px;
    margin-bottom: 60px;
}

.drawerHeader {
    position: fixed;
    top: 0;
    left: 0;
    width: 100%;
    border-bottom: 1px solid #0000001f;
    background: white;
    z-index: 9999;
}

.drawerIcons {
    flex-direction: row !important;
}

.drawerFooter {
    padding: 10px !important;
    position: fixed;
    bottom: 0;
    right: 0;
    width: 100%;
    border-top: 1px solid #f0f4fe !important;
    background: white;
}



</style>
