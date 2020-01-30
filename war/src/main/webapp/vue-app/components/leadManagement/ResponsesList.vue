<template>
<v-data-table :headers="headers" :items="responses" :items-per-page="5"></v-data-table>
</template>

<script>
export default {
    props: ['responses', 'fields', 'form'],
    data() {
        return {}

    },

    computed: {
        headers() {

            const fieldsHeader = []
            this.fields.forEach(element => fieldsHeader.push({
                text: element,
                align: 'center',
                sortable: true,
                value: element,
            }));
            return fieldsHeader
        }
    },
}
</script>
