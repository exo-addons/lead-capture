<template>
<v-app class="VuetifyApp" color="transaprent" id="mailTemplatesManagementApp">
    <main>
        <templates-list />
    </main>
</v-app>
</template>

<script>
import templatesList from './mailTemplatesManagement/TemplatesList.vue';
export default {
    components: {
        templatesList,
    },
};
</script>

<style>
#mailTemplatesManagementApp {
    overflow: hidden;
}

select {
    width: auto;
}

#mailTemplatesManagementApp .v-input input {
    margin-bottom: 0;
    border: 0;
    box-shadow: none;
}

#mailTemplatesManagementApp .v-toolbar .v-input {
    margin-left: 18px;
}

#mailTemplatesManagementApp .v-data-table {
 width: 100%;
}
</style>
