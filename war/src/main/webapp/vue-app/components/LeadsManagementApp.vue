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
     /* padding: 10px 20px; */
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
</style>
