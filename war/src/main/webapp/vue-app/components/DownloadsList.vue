<template>
  <v-data-table
    :headers="headers"
    :items="downloads"
    :items-per-page="5"
    class="elevation-1"
  ></v-data-table>
</template>

<script>
  export default {
      props: ['downloads'],
    data () {
      return {
        headers: [
          { text: 'Resource Type', value: 'resourceType' },
          { text: 'Resource Name', value: 'resourceName' },
          { text: 'Date', value: 'resourceDownloadDate' },
        ],
      }
    },
  }
</script>